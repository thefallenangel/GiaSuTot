package com.indcgroup.giasutot.customer;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.indcgroup.adapter.SuperAdapter;
import com.indcgroup.dialog.SuperDialog;
import com.indcgroup.giasutot.R;
import com.indcgroup.model.ModelArticle;
import com.indcgroup.model.MyResponse;
import com.indcgroup.loadresult.ApiCommunication;
import com.indcgroup.loadresult.ApiResponse;
import com.indcgroup.utility.Constants;
import com.indcgroup.utility.GLOBAL;
import com.indcgroup.utility.Utilities;

import java.util.ArrayList;

public class SearchingUserSetupActivity extends AppCompatActivity implements ApiResponse {

    Utilities utl = new Utilities();
    ConnectivityManager conmng;

    AdView adView;
    Spinner spinGender, spinPosition;
    MultiAutoCompleteTextView atxtGrade, atxtSubject;
    RadioButton radTime, radDistance;
    Button btnSearch;

    static ModelArticle model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching_user_setup);

        conmng = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        adView = (AdView) findViewById(R.id.adView);
        spinGender = (Spinner) findViewById(R.id.spinGender);
        spinPosition = (Spinner) findViewById(R.id.spinPosition);
        atxtGrade = (MultiAutoCompleteTextView) findViewById(R.id.atxtGrade);
        atxtSubject = (MultiAutoCompleteTextView) findViewById(R.id.atxtSubject);
        radTime = (RadioButton) findViewById(R.id.radTime);
        radDistance = (RadioButton) findViewById(R.id.radDistance);
        btnSearch = (Button) findViewById(R.id.btnSearch);

        adView.loadAd(utl.createAdRequest());

        atxtGrade.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        atxtSubject.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        spinGender.setAdapter(new SuperAdapter(SuperAdapter.TYPE_SIMPLE, this, Constants.generateGender()));
        spinPosition.setAdapter(new SuperAdapter(SuperAdapter.TYPE_SIMPLE, this, Constants.generatePosition()));
        atxtGrade.setAdapter(new ArrayAdapter<String>(this, R.layout.adapter_simple_text, Constants.My_Grade));
        atxtSubject.setAdapter(new ArrayAdapter<String>(this, R.layout.adapter_simple_text, Constants.My_Subject));


        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Validation
                if (!utl.validationMultiAutoCompleteTextView(atxtGrade, atxtSubject)) {
                    utl.showSuperDialog(new SuperDialog(),
                            getFragmentManager(),
                            false,
                            SuperDialog.DIALOG_TYPE_ERROR,
                            Constants.Error_MissingRequiredField);
                    return;
                }

                //Check internet connection
                if (!utl.checkInternetConnection(conmng)) {
                    Toast.makeText(getApplication(), Constants.Error_NoInternetConnection, Toast.LENGTH_SHORT).show();
                    return;
                }

                //Execute
                ApiCommunication comm = new ApiCommunication(SearchingUserSetupActivity.this, Constants.Alert_DownloadArticle, "GET");
                comm.delegate = SearchingUserSetupActivity.this;

                model = new ModelArticle();
                model.Page = 0;
                model.Sort = radTime.isChecked() ? 0 : 1;
                model.Latitude = GLOBAL.LATITUDE;
                model.Longitude = GLOBAL.LONGITUDE;
                model.Gender = spinGender.getSelectedItem().toString().contains("-") ? "" : spinGender.getSelectedItem().toString();
                model.Position = spinPosition.getSelectedItem().toString().contains("-") ? "" : spinPosition.getSelectedItem().toString();
                model.Grade = atxtGrade.getText().toString();
                model.Subject = atxtSubject.getText().toString();

                String value = ModelArticle.toJson(model);
                value = utl.base64Encode(value);
                String url = String.format(getString(R.string.SearchArticleURL), value);
                comm.execute(url);
            }
        });
    }

    @Override
    public void onFinishCommunication(MyResponse result) {
        if (result.ResponseCode.equals("01")) {
            ArrayList<ModelArticle> list = ModelArticle.toModelList(result.ResponseMessage);
            for (ModelArticle item : list) {
                item.Content = utl.convertBreakline(item.Content, 0);
            }

            Intent intent = new Intent(SearchingUserSetupActivity.this, SearchingUserResultActivity.class);
            intent.putParcelableArrayListExtra("Data", list);
            intent.putExtra("Model", model);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        } else {
            Toast.makeText(SearchingUserSetupActivity.this, result.ResponseMessage, Toast.LENGTH_SHORT).show();
        }
    }
}
