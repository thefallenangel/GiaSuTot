package com.indcgroup.giasutot.customer;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.indcgroup.dialog.SuperDialog;
import com.indcgroup.dialog.SuperDialogCloseListener;
import com.indcgroup.giasutot.R;
import com.indcgroup.loadimage.ImageCommunication;
import com.indcgroup.model.ModelRating;
import com.indcgroup.model.ModelUserInformation;
import com.indcgroup.model.MyResponse;
import com.indcgroup.loadresult.ApiCommunication;
import com.indcgroup.loadresult.ApiResponse;
import com.indcgroup.utility.Constants;
import com.indcgroup.utility.Utilities;

public class UserInformationActivity extends AppCompatActivity implements ApiResponse, SuperDialogCloseListener {

    static final int PERMISSION_READ_PHONE_STATE = 1;

    static int flag = 0;
    static long userID;
    static int requestType = 0;
    ModelUserInformation model = new ModelUserInformation();

    Utilities utl = new Utilities();
    ConnectivityManager conmng;

    AdView adView;
    TextView txtCreatedDate, txtGrade, txtSubject, txtContent, txtFullname, txtGender,
            txtEmail, txtPhone, txtAddress, txtIdentityCard, txtPosition, txtDocumentation;
    ImageView imgAvatar;
    RatingBar rabRate;
    FloatingActionButton fabCall, fabMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);

        conmng = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        flag = getIntent().getIntExtra("Flag", 0);
        userID = getIntent().getLongExtra("UserID", 0);

        adView = (AdView) findViewById(R.id.adView);
        txtCreatedDate = (TextView) findViewById(R.id.txtCreatedDate);
        txtGrade = (TextView) findViewById(R.id.txtGrade);
        txtSubject = (TextView) findViewById(R.id.txtSubject);
        txtContent = (TextView) findViewById(R.id.txtContent);
        txtFullname = (TextView) findViewById(R.id.txtFullname);
        txtGender = (TextView) findViewById(R.id.txtGender);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        txtPhone = (TextView) findViewById(R.id.txtPhone);
        txtAddress = (TextView) findViewById(R.id.txtAddress);
        txtIdentityCard = (TextView) findViewById(R.id.txtIdentityCard);
        txtPosition = (TextView) findViewById(R.id.txtPosition);
        txtDocumentation = (TextView) findViewById(R.id.txtDocumentation);
        imgAvatar = (ImageView) findViewById(R.id.imgAvatar);
        rabRate = (RatingBar) findViewById(R.id.rabRate);
        fabCall = (FloatingActionButton) findViewById(R.id.fabCall);
        fabMessage = (FloatingActionButton) findViewById(R.id.fabMessage);

        adView.loadAd(utl.createAdRequest());

        //Check internet connection
        if (!utl.checkInternetConnection(conmng)) {
            Toast.makeText(getApplication(), Constants.Error_NoInternetConnection, Toast.LENGTH_SHORT).show();
        } else {
            ApiCommunication comm = new ApiCommunication(UserInformationActivity.this, Constants.Alert_DownloadUser, "GET");
            comm.delegate = UserInformationActivity.this;
            requestType = 0;

            String value = utl.base64Encode(String.valueOf(userID));
            String url = String.format(getString(R.string.GetUserInformationURL), value);
            comm.execute(url);
        }

        rabRate.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

                if (utl.checkPermission(UserInformationActivity.this, Manifest.permission.READ_PHONE_STATE)) {
                    //Check internet connection
                    if (!utl.checkInternetConnection(conmng)) {
                        Toast.makeText(getApplication(), Constants.Error_NoInternetConnection, Toast.LENGTH_SHORT).show();
                    } else {
                        TelephonyManager tmng = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                        String imei = tmng.getDeviceId();
                        int val = (int) v;

                        //Do nothing if not rate
                        if (val != 0) {
                            ApiCommunication comm = new ApiCommunication(UserInformationActivity.this, Constants.Alert_SendRating, "GET");
                            comm.delegate = UserInformationActivity.this;
                            requestType = 1;

                            ModelRating model = new ModelRating();
                            model.IMEI = imei;
                            model.UserID = userID;
                            model.Value = val;

                            String value = utl.base64Encode(ModelRating.toJson(model));
                            String url = String.format(getString(R.string.SaveRating), value);
                            comm.execute(url);
                        }
                    }
                } else {
                    ActivityCompat.requestPermissions(UserInformationActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSION_READ_PHONE_STATE);
                }
            }
        });

        fabCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + model.Phone));
                startActivity(intent);
            }
        });

        fabMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("sms: " + model.Phone));
                startActivity(intent);
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_READ_PHONE_STATE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Check internet connection
                    if (!utl.checkInternetConnection(conmng)) {
                        Toast.makeText(getApplication(), Constants.Error_NoInternetConnection, Toast.LENGTH_SHORT).show();
                    } else {
                        TelephonyManager tmng = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                        String imei = tmng.getDeviceId();
                        int val = (int) rabRate.getRating();

                        //Do nothing if not rate
                        if (val != 0) {
                            ApiCommunication comm = new ApiCommunication(UserInformationActivity.this, Constants.Alert_SendRating, "GET");
                            comm.delegate = UserInformationActivity.this;
                            requestType = 1;

                            ModelRating model = new ModelRating();
                            model.IMEI = imei;
                            model.UserID = userID;
                            model.Value = val;

                            String value = utl.base64Encode(ModelRating.toJson(model));
                            String url = String.format(getString(R.string.SaveRating), value);
                            comm.execute(url);
                        }
                    }
                } else {
                    utl.showSuperDialog(new SuperDialog(),
                            getFragmentManager(),
                            true,
                            SuperDialog.DIALOG_TYPE_ERROR,
                            Constants.Error_NoGrantPermission);
                }
            }
            break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home: {
                if (flag == 0) {
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(this, SearchingUserResultActivity.class);
                    startActivity(intent);
                }
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (flag == 0) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, SearchingUserResultActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @Override
    public void onFinishCommunication(MyResponse result) {
        if (requestType == 0) {
            if (result.ResponseCode.equals("01")) {
                model = ModelUserInformation.toModelObject(result.ResponseMessage);

                //Show user information
                txtCreatedDate.setText(txtCreatedDate.getText().toString() + model.CreatedDate);
                txtGrade.setText(txtGrade.getText().toString() + model.Grade);
                txtSubject.setText(txtSubject.getText().toString() + model.Subject);
                txtContent.setText(utl.convertBreakline(model.Content, 0));
                txtFullname.setText(txtFullname.getText().toString() + model.Fullname);
                txtGender.setText(txtGender.getText().toString() + model.Gender);
                txtEmail.setText(txtEmail.getText().toString() + model.Email);
                txtPhone.setText(txtPhone.getText().toString() + model.Phone);
                txtAddress.setText(txtAddress.getText().toString() + model.Address);
                txtIdentityCard.setText(txtIdentityCard.getText().toString() + model.IdentityCard);
                txtPosition.setText(txtPosition.getText().toString() + model.Position);
                txtDocumentation.setText(txtDocumentation.getText().toString() + model.Documentation);

                //Load user avatar
                String url = String.format(getString(R.string.LoadAvatar), String.valueOf(model.UserID));
                new ImageCommunication(imgAvatar).execute(url);

            } else {
                Toast.makeText(UserInformationActivity.this, result.ResponseMessage, Toast.LENGTH_SHORT).show();
            }
        } else {
            if (result.ResponseCode.equals("01")) {
                switch ((int) rabRate.getRating()) {
                    case 1:
                        Toast.makeText(UserInformationActivity.this, "Rất kém", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(UserInformationActivity.this, "Kém", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Toast.makeText(UserInformationActivity.this, "Trung bình", Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
                        Toast.makeText(UserInformationActivity.this, "Tốt", Toast.LENGTH_SHORT).show();
                        break;
                    case 5:
                        Toast.makeText(UserInformationActivity.this, "Rất tốt", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            } else {
                Toast.makeText(UserInformationActivity.this, result.ResponseMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {
        this.finishAffinity();
    }
}


