package com.indcgroup.giasutot.customer;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.indcgroup.adapter.SuperAdapter;
import com.indcgroup.dialog.SuperDialog;
import com.indcgroup.dialog.SuperDialogConfirmListener;
import com.indcgroup.giasutot.R;
import com.indcgroup.model.ModelArticle;
import com.indcgroup.model.MyResponse;
import com.indcgroup.loadresult.ApiCommunication;
import com.indcgroup.loadresult.ApiResponse;
import com.indcgroup.utility.Constants;
import com.indcgroup.utility.GLOBAL;
import com.indcgroup.utility.Utilities;

import java.util.ArrayList;

public class SearchingUserSetupActivity extends AppCompatActivity implements ApiResponse, SuperDialogConfirmListener {

    static int closeDialogFlag = 0;
    Utilities utl = new Utilities();
    ConnectivityManager conmng;

    AdView adView;
    Spinner spinGender, spinPosition;
    RadioButton radTime, radDistance;
    Button btnSelectGrade, btnSelectSubject, btnSearch;
    EditText edtGrade, edtSubject;

    static ModelArticle model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching_user_setup);

        conmng = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        adView = (AdView) findViewById(R.id.adView);
        spinGender = (Spinner) findViewById(R.id.spinGender);
        spinPosition = (Spinner) findViewById(R.id.spinPosition);
        radTime = (RadioButton) findViewById(R.id.radTime);
        radDistance = (RadioButton) findViewById(R.id.radDistance);
        edtGrade = (EditText) findViewById(R.id.edtGrade);
        edtSubject = (EditText) findViewById(R.id.edtSubject);
        btnSelectGrade = (Button) findViewById(R.id.btnSelectGrade);
        btnSelectSubject = (Button) findViewById(R.id.btnSelectSubject);
        btnSearch = (Button) findViewById(R.id.btnSearch);

        adView.loadAd(utl.createAdRequest());

        spinGender.setAdapter(new SuperAdapter(SuperAdapter.TYPE_SIMPLE, this, Constants.generateGender()));
        spinPosition.setAdapter(new SuperAdapter(SuperAdapter.TYPE_SIMPLE, this, Constants.generatePosition()));

        btnSelectGrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GLOBAL.CHECKED_CHECKBOXES = new ArrayList<>();
                closeDialogFlag = 0;
                utl.showSuperDialog(new SuperDialog(),
                        getFragmentManager(),
                        false,
                        SuperDialog.DIALOG_TYPE_GRADE_SELECTION,
                        "");
            }
        });

        btnSelectSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GLOBAL.CHECKED_CHECKBOXES = new ArrayList<>();
                closeDialogFlag = 1;
                utl.showSuperDialog(new SuperDialog(),
                        getFragmentManager(),
                        false,
                        SuperDialog.DIALOG_TYPE_SUBJECT_SELECTION,
                        "");
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Validation
                if (!utl.validationEditText(edtSubject, edtGrade)) {
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
                model.Grade = edtGrade.getText().toString();
                model.Subject = edtSubject.getText().toString();

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

    @Override
    public void confirmButtonClicked() {
        String value = "";
        for (String item : GLOBAL.CHECKED_CHECKBOXES) {
            value += item + ", ";
        }
        if (closeDialogFlag == 0)
            edtGrade.setText(value);
        else
            edtSubject.setText(value);
    }
}
