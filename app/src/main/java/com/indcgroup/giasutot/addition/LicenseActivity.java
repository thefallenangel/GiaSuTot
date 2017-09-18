package com.indcgroup.giasutot.addition;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.ads.InterstitialAd;
import com.indcgroup.giasutot.R;
import com.indcgroup.giasutot.customer.MainActivity;
import com.indcgroup.giasutot.user.UserActivity;
import com.indcgroup.utility.Utilities;


public class LicenseActivity extends AppCompatActivity {

    Utilities utl = new Utilities();
    static int backFlag;

    InterstitialAd adPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);

        backFlag = getIntent().getIntExtra("Flag", 0);

        adPage = utl.createAdPage(this, getString(R.string.ad_page_id));
    }

    @Override
    public void onBackPressed() {
        if (backFlag == 0) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, UserActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        if (adPage.isLoaded())
            adPage.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home: {
                if (backFlag == 0) {
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(this, UserActivity.class);
                    startActivity(intent);
                }
                
                if (adPage.isLoaded())
                    adPage.show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
