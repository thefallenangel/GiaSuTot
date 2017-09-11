package com.indcgroup.giasutot.customer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.indcgroup.adapter.SuperAdapter;
import com.indcgroup.giasutot.R;
import com.indcgroup.model.ModelArticle;
import com.indcgroup.model.MyResponse;
import com.indcgroup.loadresult.ApiCommunication;
import com.indcgroup.loadresult.ApiResponse;
import com.indcgroup.utility.Constants;
import com.indcgroup.utility.Utilities;

import java.util.ArrayList;

public class SearchingUserResultActivity extends AppCompatActivity implements ApiResponse {

    Utilities utl = new Utilities();
    static boolean isDownloading = false;
    static boolean isUserScroll = false;
    static ArrayList<ModelArticle> data;
    static ModelArticle model;
    SuperAdapter adapter = null;

    AdView adView;
    TextView txtGender, txtPosition, txtGrade, txtSubject;
    ListView lstArticle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching_user_result);

        adView = (AdView) findViewById(R.id.adView);
        txtGender = (TextView) findViewById(R.id.txtGender);
        txtPosition = (TextView) findViewById(R.id.txtPosition);
        txtGrade = (TextView) findViewById(R.id.txtGrade);
        txtSubject = (TextView) findViewById(R.id.txtSubject);
        lstArticle = (ListView) findViewById(R.id.lstArticle);

        adView.loadAd(utl.createAdRequest());

        //Get data from intent
        if (getIntent().hasExtra("Data"))
            data = getIntent().getParcelableArrayListExtra("Data");
        if (getIntent().hasExtra("Model"))
            model = getIntent().getParcelableExtra("Model");

        //Append data
        String gender = model.Gender.equals("") ? "-- Tất cả --" : model.Gender;
        txtGender.setText("Giới tính: " + gender);
        String position = model.Position.equals("") ? "-- Tất cả --" : model.Position;
        txtPosition.setText("Ngành nghề: " + position);
        txtGrade.setText("Lớp: " + model.Grade);
        txtSubject.setText("Môn: " + model.Subject);

        adapter = new SuperAdapter(SuperAdapter.TYPE_ARTICLE, SearchingUserResultActivity.this, data);
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
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                int itemCount = lstArticle.getAdapter().getCount();

                //First load is 5 items
                if (itemCount >= 5) {
                    //Check when scroll reach bottom
                    if (lstArticle.getLastVisiblePosition() == lstArticle.getAdapter().getCount() - 1 &&
                            lstArticle.getChildAt(lstArticle.getChildCount() - 1).getBottom() <= lstArticle.getHeight()) {

                        //Only load more data when scrolling is the behavior of user and there are no current request
                        if (isUserScroll && !isDownloading) {
                            ApiCommunication comm = new ApiCommunication(SearchingUserResultActivity.this, Constants.Alert_DownloadArticle, "GET");
                            comm.delegate = SearchingUserResultActivity.this;

                            model.Page = itemCount;

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

                Intent intent = new Intent(SearchingUserResultActivity.this, UserInformationActivity.class);
                intent.putExtra("Flag", 1);
                intent.putExtra("UserID", item.UserID);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onFinishCommunication(MyResponse result) {
        if (result.ResponseCode.equals("01")) {

            isDownloading = false;

            ArrayList<ModelArticle> extraList = ModelArticle.toModelList(result.ResponseMessage);
            for (ModelArticle item : extraList) {
                item.Content = utl.convertBreakline(item.Content, 0);
            }

            data.addAll(extraList);
            adapter.notifyDataSetChanged();
            lstArticle.invalidateViews();
        } else {
            Toast.makeText(SearchingUserResultActivity.this, result.ResponseMessage, Toast.LENGTH_SHORT).show();
        }
    }
}
