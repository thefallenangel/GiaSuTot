package com.indcgroup.giasutot.user;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.indcgroup.adapter.SuperAdapter;
import com.indcgroup.dialog.SuperDialog;
import com.indcgroup.dialog.SuperDialogConfirmListener;
import com.indcgroup.giasutot.R;
import com.indcgroup.giasutot.addition.AboutUsActivity;
import com.indcgroup.giasutot.addition.InstructionActivity;
import com.indcgroup.giasutot.addition.LicenseActivity;
import com.indcgroup.giasutot.customer.MainActivity;
import com.indcgroup.loadimage.ImageCommunication;
import com.indcgroup.model.ModelRecruitment;
import com.indcgroup.model.MyResponse;
import com.indcgroup.loadresult.ApiCommunication;
import com.indcgroup.loadresult.ApiResponse;
import com.indcgroup.utility.Constants;
import com.indcgroup.utility.GLOBAL;
import com.indcgroup.utility.Utilities;

import java.util.ArrayList;

public class UserActivity extends AppCompatActivity
        implements ApiResponse, SuperDialogConfirmListener, NavigationView.OnNavigationItemSelectedListener {

    Utilities utl = new Utilities();
    ConnectivityManager conmng;
    static boolean isFirstLoad = false;
    static boolean isUserScroll = false;
    static boolean isDownloading = false;
    boolean isAllDownloaded = false;
    static ArrayList<ModelRecruitment> data;
    SuperAdapter adapter = null;

    AdView adView;
    ImageView imgAvatar;
    TextView txtFullname, txtEmail, txtPhone;
    ListView lstRecruitment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_user_view);
        navigationView.setNavigationItemSelectedListener(this);

        showMenuValues(navigationView);

        conmng = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        adView = (AdView) findViewById(R.id.adView);
        lstRecruitment = (ListView) findViewById(R.id.lstRecruitment);

        adView.loadAd(utl.createAdRequest());

        //Check internet connection
        if (!utl.checkInternetConnection(conmng)) {
            Toast.makeText(getApplication(), Constants.Error_NoInternetConnection, Toast.LENGTH_SHORT).show();
            return;
        }

        //Execute
        isFirstLoad = true;
        ApiCommunication comm = new ApiCommunication(UserActivity.this, Constants.Alert_DownloadRecruitment, "GET");
        comm.delegate = UserActivity.this;

        String value = utl.base64Encode("0");
        String url = String.format(getString(R.string.GetLatestRecruitmentURL), value);
        comm.execute(url);

    }

    void showMenuValues(NavigationView navigationView) {

        View view = navigationView.getHeaderView(0);

        imgAvatar = view.findViewById(R.id.imgAvatar);
        txtFullname = view.findViewById(R.id.txtFullname);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtPhone = view.findViewById(R.id.txtPhone);

        txtFullname.setText(GLOBAL.USER.Fullname);
        txtEmail.setText("Email: " + GLOBAL.USER.Email);
        txtPhone.setText("SĐT: " + GLOBAL.USER.Phone);

        //Load avatar
        String url = String.format(getString(R.string.LoadAvatar), String.valueOf(GLOBAL.USER.UserID));
        new ImageCommunication(imgAvatar).execute(url);
    }

    void showLatestRecruitment(ArrayList<ModelRecruitment> data) {
        adapter = new SuperAdapter(SuperAdapter.TYPE_RECRUITMENT, this, data);
        lstRecruitment.setAdapter(adapter);
        lstRecruitment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                ModelRecruitment model = (ModelRecruitment) adapterView.getAdapter().getItem(i);

                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + model.Phone));
                startActivity(intent);
            }
        });
        lstRecruitment.setOnScrollListener(new AbsListView.OnScrollListener() {
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
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                int itemCount = lstRecruitment.getAdapter().getCount();

                //First load is 5 items
                if (itemCount >= 5) {
                    //Check when scroll reach bottom
                    if (lstRecruitment.getLastVisiblePosition() == lstRecruitment.getAdapter().getCount() - 1 &&
                            lstRecruitment.getChildAt(lstRecruitment.getChildCount() - 1).getBottom() <= lstRecruitment.getHeight()) {

                        //Load more data when: scrolling is user's behaviour, there is no request at this moment, when still have data to download
                        if (isUserScroll && !isDownloading && !isAllDownloaded) {
                            isDownloading = true;
                            ApiCommunication comm = new ApiCommunication(UserActivity.this, Constants.Alert_DownloadRecruitment, "GET");
                            comm.delegate = UserActivity.this;

                            int page = itemCount - 1;
                            isFirstLoad = false;

                            String value = utl.base64Encode(String.valueOf(page));
                            String url = String.format(getString(R.string.GetLatestRecruitmentURL), value);
                            comm.execute(url);
                        }
                    }
                }
            }
        });
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


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_userinfo) {
            Intent intent = new Intent(UserActivity.this, UserProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_userarticle) {
            Intent intent = new Intent(UserActivity.this, UserArticleActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_userpoint) {
            Intent intent = new Intent(UserActivity.this, UserTransactionActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_signout) {
            Intent intent = new Intent(UserActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            GLOBAL.USER = null;
            startActivity(intent);

            //StartAppAd.showAd(UserActivity.this);

        } else if (id == R.id.nav_license) {
            Intent intent = new Intent(UserActivity.this, LicenseActivity.class);
            intent.putExtra("Flag", 1);
            startActivity(intent);
        } else if (id == R.id.nav_instruction) {
            Intent intent = new Intent(UserActivity.this, InstructionActivity.class);
            intent.putExtra("Flag", 1);
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(UserActivity.this, AboutUsActivity.class);
            intent.putExtra("Flag", 1);
            startActivity(intent);
        } else if (id == R.id.nav_rate) {
            try {
                Intent intent = utl.createRateUsIntent(UserActivity.this, "market://details");
                startActivity(intent);
            } catch (ActivityNotFoundException ex) {
                Intent rateIntent = utl.createRateUsIntent(UserActivity.this, "https://play.google.com/store/apps/details");
                startActivity(rateIntent);
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void confirmButtonClicked() {
        this.finishAffinity();
    }

    @Override
    public void onFinishCommunication(MyResponse result) {
        if (result.ResponseCode.equals("01")) {
            if (isFirstLoad) {
                data = ModelRecruitment.toModelList(result.ResponseMessage);

                for (ModelRecruitment item : data) {
                    item.Content = utl.convertBreakline(item.Content, 0);
                }

                showLatestRecruitment(data);
            } else {
                isDownloading = false;
                ArrayList<ModelRecruitment> extraList = ModelRecruitment.toModelList(result.ResponseMessage);
                for (ModelRecruitment item : extraList) {
                    item.Content = utl.convertBreakline(item.Content, 0);
                }
                isAllDownloaded = extraList.size() <= 0;

                data.addAll(extraList);
                adapter.notifyDataSetChanged();
                lstRecruitment.invalidateViews();
            }
        } else {
            Toast.makeText(UserActivity.this, result.ResponseMessage, Toast.LENGTH_SHORT).show();
        }
    }

}
