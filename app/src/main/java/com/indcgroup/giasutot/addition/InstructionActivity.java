package com.indcgroup.giasutot.addition;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.indcgroup.giasutot.R;
import com.indcgroup.giasutot.customer.MainActivity;
import com.indcgroup.giasutot.user.UserActivity;
import com.indcgroup.utility.Constants;
import com.indcgroup.utility.Utilities;

public class InstructionActivity extends AppCompatActivity {

    Utilities utl = new Utilities();
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    InterstitialAd adPage;
    static int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);

        flag = getIntent().getIntExtra("Flag", 0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        adPage = utl.createAdPage(this, getString(R.string.ad_page_id));
    }

    @Override
    public void onBackPressed() {
        if (flag == 0) {
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
                if (flag == 0) {
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


    public static class PlaceholderFragment extends Fragment {

        TextView txtInstruction;

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt("Number", sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_instruction, container, false);
            int number = getArguments().getInt("Number");

            txtInstruction = rootView.findViewById(R.id.txtInstruction);

            switch (number) {
                case 0:
                    txtInstruction.setText(getString(R.string.InsPermission));
                    break;
                case 1:
                    txtInstruction.setText(getString(R.string.InsLatestArticle));
                    break;
                case 2:
                    txtInstruction.setText(getString(R.string.InsUserInformation));
                    break;
                case 3:
                    txtInstruction.setText(getString(R.string.InsSearchUser));
                    break;
                case 4:
                    txtInstruction.setText(getString(R.string.InsRecruitment));
                    break;
                case 5:
                    txtInstruction.setText(getString(R.string.InsSignUp));
                    break;
                case 6:
                    txtInstruction.setText(getString(R.string.InsSignIn));
                    break;
                case 7:
                    txtInstruction.setText(getString(R.string.InsLatestRecruitment));
                    break;
                case 8:
                    txtInstruction.setText(getString(R.string.InsUserProfile));
                    break;
                case 9:
                    txtInstruction.setText(getString(R.string.InsUserArticle));
                    break;
                case 10:
                    txtInstruction.setText(getString(R.string.InsUserTransaction));
                    break;
                case 11:
                    txtInstruction.setText(getString(R.string.InsSignOut));
                    break;
                default:
                    break;
            }

            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            // Show 12 total pages.
            return 12;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Quyền và thiết lập quyền";
                case 1:
                    return "Bài đăng mới nhất";
                case 2:
                    return "Thông tin gia sư";
                case 3:
                    return "Tìm kiếm gia sư";
                case 4:
                    return "Đăng tìm gia sư";
                case 5:
                    return "Đăng ký thành viên";
                case 6:
                    return "Đăng nhập";
                case 7:
                    return "Tuyển dụng mới nhất";
                case 8:
                    return "Thông tin cá nhân";
                case 9:
                    return "Thông tin bài viết";
                case 10:
                    return "Điểm của tôi";
                case 11:
                    return "Đăng xuất";
            }
            return null;
        }
    }
}
