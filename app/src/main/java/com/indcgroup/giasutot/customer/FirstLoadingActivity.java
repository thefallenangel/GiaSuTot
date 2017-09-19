package com.indcgroup.giasutot.customer;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.indcgroup.dialog.SuperDialog;
import com.indcgroup.dialog.SuperDialogCloseListener;
import com.indcgroup.giasutot.R;
import com.indcgroup.giasutot.addition.InstructionActivity;
import com.indcgroup.utility.Constants;
import com.indcgroup.utility.GLOBAL;
import com.indcgroup.utility.Utilities;

import java.util.List;
import java.util.Locale;

import static java.security.AccessController.getContext;

public class FirstLoadingActivity extends AppCompatActivity implements LocationListener, SuperDialogCloseListener {

    static final int CLOSE_TO_SETTINGS = 0;
    static final int CLOSE_EXIT_APP = 1;

    static final int PERMISSION_ACCESS_LOCATION = 1;
    static final int PERMISSION_OPEN_LOCATION_SETTING = 2;

    static int closeFlag = 0;
    private FirebaseAnalytics mFirebaseAnalytics;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Utilities utl = new Utilities();

    ConnectivityManager conmng;
    LocationManager locationManager;
    String locationProvider;
    Location currentLocation;
    Geocoder currentGeocoder;

    TextView txtNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_loading);

        //Init ads
        MobileAds.initialize(getApplicationContext(), getString(R.string.ad_application_id));
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        conmng = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        txtNotification = (TextView) findViewById(R.id.txtNotification);

        //Check location mode
        if (!utl.isLocationEnable(this)) {
            closeFlag = CLOSE_TO_SETTINGS;
            utl.showSuperDialog(new SuperDialog(),
                    getFragmentManager(),
                    true,
                    SuperDialog.DIALOG_TYPE_ERROR,
                    Constants.Error_LocationServiceOff);
        }


        //Get location permission
        if (utl.checkPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) && utl.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            //Check internet connection
            if (!utl.checkInternetConnection(conmng)) {
                Toast.makeText(getApplication(), Constants.Error_NoInternetConnection, Toast.LENGTH_SHORT).show();
            } else {
                //Start getting the address of user
                txtNotification.setText(Constants.Alert_GetAddress);

                initializeLocationManager();
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 1, this);

            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ACCESS_LOCATION);
        }
    }

    void initializeLocationManager() {
        currentGeocoder = new Geocoder(this, Locale.getDefault());
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        List<String> acceptableLocationProvider = locationManager.getProviders(criteria, true);
        if (acceptableLocationProvider.isEmpty()) {
            locationProvider = "";
        } else {
            locationProvider = acceptableLocationProvider.get(0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Check internet connection
                    if (!utl.checkInternetConnection(conmng)) {
                        Toast.makeText(getApplication(), Constants.Error_NoInternetConnection, Toast.LENGTH_SHORT).show();
                    } else {

                        //Start getting the address of user
                        txtNotification.setText(Constants.Alert_GetAddress);

                        initializeLocationManager();
                        if (utl.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) && utl.checkPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 1, this);
                        }
                    }
                } else {
                    closeFlag = CLOSE_EXIT_APP;
                    utl.showSuperDialog(new SuperDialog(),
                            getFragmentManager(),
                            true,
                            SuperDialog.DIALOG_TYPE_ERROR,
                            Constants.Error_NoGrantPermission);
                }
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        //After location manager is init, the phone can get current location after period of time
        currentLocation = location;

        GLOBAL.LATITUDE = currentLocation.getLatitude();
        GLOBAL.LONGITUDE = currentLocation.getLongitude();

        //Load shared preferences
        preferences = getSharedPreferences("Security", MODE_PRIVATE);
        boolean isFirstRun = preferences.getBoolean("FirstRun", true);

        if (isFirstRun) {

            Toast.makeText(FirstLoadingActivity.this, Constants.Success_FirstRun, Toast.LENGTH_LONG).show();

            Intent intent = new Intent(FirstLoadingActivity.this, InstructionActivity.class);
            intent.putExtra("Flag", 0);
            startActivity(intent);

            //Save first run flag
            editor = preferences.edit();
            editor.putBoolean("FirstRun", false);
            editor.apply();

        } else {
            if (GLOBAL.LATITUDE != 0 && GLOBAL.LONGITUDE != 0) {
                Intent intent = new Intent(FirstLoadingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }


        //Google analytics
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.VALUE, GLOBAL.LATITUDE + "," + GLOBAL.LONGITUDE);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        Log.e(Constants.TAG, "Pass google analytics");
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onStop() {
        super.onStop();

        if (locationManager != null)
            locationManager.removeUpdates(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (utl.checkPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) && utl.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            //Check internet connection
            if (!utl.checkInternetConnection(conmng)) {
                Toast.makeText(getApplication(), Constants.Error_NoInternetConnection, Toast.LENGTH_SHORT).show();
            } else {
                //Start getting the address of user
                txtNotification.setText(Constants.Alert_GetAddress);

                initializeLocationManager();
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 1, this);

            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ACCESS_LOCATION);
        }


    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {
        if (closeFlag == CLOSE_TO_SETTINGS) {
            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), PERMISSION_OPEN_LOCATION_SETTING);
        } else if (closeFlag == CLOSE_EXIT_APP) {
            this.finishAffinity();
        }
    }
}
