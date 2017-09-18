package com.indcgroup.giasutot.customer;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.indcgroup.dialog.SuperDialog;
import com.indcgroup.dialog.SuperDialogConfirmListener;
import com.indcgroup.giasutot.R;
import com.indcgroup.giasutot.addition.AboutUsActivity;
import com.indcgroup.giasutot.addition.InstructionActivity;
import com.indcgroup.giasutot.addition.LicenseActivity;
import com.indcgroup.model.ModelArticle;
import com.indcgroup.model.MyResponse;
import com.indcgroup.loadresult.ApiCommunication;
import com.indcgroup.loadresult.ApiResponse;
import com.indcgroup.utility.Constants;
import com.indcgroup.adapter.SuperAdapter;
import com.indcgroup.utility.GLOBAL;
import com.indcgroup.utility.Utilities;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ApiResponse, SuperDialogConfirmListener, NavigationView.OnNavigationItemSelectedListener {

    static final int DOWNLOAD_FIRST_ARTICLE = 0;
    static final int DOWNLOAD_MORE_ARTICLE = 1;

    static boolean isDownloading = false;
    static boolean isUserScroll = false;
    boolean isAllDownloaded = false;
    static ArrayList<ModelArticle> data;
    ConnectivityManager conmng;

    static int downloadFlag = 0;

    Utilities utl = new Utilities();
    SuperAdapter adapter = null;

    AdView adView;
    ListView lstArticle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        lstArticle = (ListView) findViewById(R.id.lstArticle);
        adView = (AdView) findViewById(R.id.adView);

        adView.loadAd(utl.createAdRequest());

        conmng = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        //Check internet connection
        if (!utl.checkInternetConnection(conmng)) {
            Toast.makeText(getApplication(), Constants.Error_NoInternetConnection, Toast.LENGTH_SHORT).show();
            return;
        }

        //Start downloading the latest article.
        downloadFlag = DOWNLOAD_FIRST_ARTICLE;
        ApiCommunication comm = new ApiCommunication(MainActivity.this, Constants.Alert_DownloadArticle, "GET");
        comm.delegate = MainActivity.this;

        ModelArticle model = new ModelArticle();
        model.Page = 0;
        model.Sort = 0;
        model.Latitude = GLOBAL.LATITUDE;
        model.Longitude = GLOBAL.LONGITUDE;

        String value = utl.base64Encode(ModelArticle.toJson(model));
        String url = String.format(getString(R.string.GetLatestArticleURL), value);
        comm.execute(url);

    }


    void showLatestArticle() {
        //Show on view
        for (ModelArticle item : data) {
            item.Content = utl.convertBreakline(item.Content, 0);
        }

        adapter = new SuperAdapter(SuperAdapter.TYPE_ARTICLE, MainActivity.this, data);
        lstArticle.setAdapter(adapter);

        lstArticle.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                //Check scroll is the behavior of user or listview itself when the adapter is updated
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL || scrollState == SCROLL_STATE_FLING) {
                    isUserScroll = true;
                } else {
                    isUserScroll = false;
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                int itemCount = lstArticle.getAdapter().getCount();

                //First load is 5 items
                if (itemCount >= 5) {
                    //Check when scroll reach bottom
                    if (lstArticle.getLastVisiblePosition() == lstArticle.getAdapter().getCount() - 1 &&
                            lstArticle.getChildAt(lstArticle.getChildCount() - 1).getBottom() <= lstArticle.getHeight()) {

                        //Load more data when: scrolling is user's behaviour, there is no request at this moment, when still have data to download
                        if (isUserScroll && !isDownloading && !isAllDownloaded) {
                            isDownloading = true;

                            downloadFlag = DOWNLOAD_MORE_ARTICLE;
                            ApiCommunication comm = new ApiCommunication(MainActivity.this, Constants.Alert_DownloadArticle, "GET");
                            comm.delegate = MainActivity.this;

                            ModelArticle model = new ModelArticle();
                            model.Page = itemCount;
                            model.Sort = 0;
                            model.Latitude = GLOBAL.LATITUDE;
                            model.Longitude = GLOBAL.LONGITUDE;

                            String value = utl.base64Encode(ModelArticle.toJson(model));
                            String url = String.format(getString(R.string.GetLatestArticleURL), value);
                            comm.execute(url);
                        }
                    }
                }
            }
        });

        lstArticle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                ModelArticle item = (ModelArticle) adapterView.getAdapter().getItem(i);

                Intent intent = new Intent(MainActivity.this, UserInformationActivity.class);
                intent.putExtra("Flag", 0);
                intent.putExtra("UserID", item.UserID);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onFinishCommunication(MyResponse result) {
        if (downloadFlag == DOWNLOAD_FIRST_ARTICLE) {
            if (result.ResponseCode.equals("01")) {
                data = ModelArticle.toModelList(result.ResponseMessage);
                showLatestArticle();
            } else {
                Toast.makeText(MainActivity.this, result.ResponseMessage, Toast.LENGTH_SHORT).show();
            }
        } else if (downloadFlag == DOWNLOAD_MORE_ARTICLE) {
            if (result.ResponseCode.equals("01")) {
                isDownloading = false;

                ArrayList<ModelArticle> extraList = ModelArticle.toModelList(result.ResponseMessage);
                for (ModelArticle item : extraList) {
                    item.Content = utl.convertBreakline(item.Content, 0);
                }
                isAllDownloaded = extraList.size() <= 0;

                data.addAll(extraList);
                adapter.notifyDataSetChanged();
                lstArticle.invalidateViews();
            } else {
                Toast.makeText(MainActivity.this, result.ResponseMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_search) {
            Intent intent = new Intent(MainActivity.this, SearchingUserSetupActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_recruit) {
            Intent intent = new Intent(MainActivity.this, RecruitmentActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_signup) {
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_signin) {
            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_license) {
            Intent intent = new Intent(MainActivity.this, LicenseActivity.class);
            intent.putExtra("Flag", 0);
            startActivity(intent);
        } else if (id == R.id.nav_instruction) {
            Intent intent = new Intent(MainActivity.this, InstructionActivity.class);
            intent.putExtra("Flag", 0);
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(MainActivity.this, AboutUsActivity.class);
            intent.putExtra("Flag", 0);
            startActivity(intent);
        } else if (id == R.id.nav_rate) {
            try {
                Intent intent = utl.createRateUsIntent(MainActivity.this, "market://details");
                startActivity(intent);
            } catch (ActivityNotFoundException ex) {
                Intent rateIntent = utl.createRateUsIntent(MainActivity.this, "https://play.google.com/store/apps/details");
                startActivity(rateIntent);
            }
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            utl.showSuperDialog(new SuperDialog(),
                    getFragmentManager(),
                    false,
                    SuperDialog.DIALOG_TYPE_CONFIRM,
                    Constants.Confirm_Exit);
        }
    }

    @Override
    public void confirmButtonClicked() {
        this.finishAffinity();
    }
}
