package com.indcgroup.giasutot.user;

import android.net.ConnectivityManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
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


public class UserArticleActivity extends AppCompatActivity implements ApiResponse, SuperDialogConfirmListener {

    static int closeDialogFlag = 0;
    Utilities utl = new Utilities();
    ConnectivityManager conmng;
    int requestType = 0;

    AdView adView;
    TextView txtCreatedDate;
    EditText edtGrade, edtSubject, edtContent;
    Button btnSelectGrade, btnSelectSubject;
    FloatingActionButton fabSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_article);

        conmng = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        adView = (AdView) findViewById(R.id.adView);
        txtCreatedDate = (TextView) findViewById(R.id.txtCreatedDate);
        edtGrade = (EditText) findViewById(R.id.edtGrade);
        edtSubject = (EditText) findViewById(R.id.edtSubject);
        edtContent = (EditText) findViewById(R.id.edtContent);
        btnSelectGrade = (Button) findViewById(R.id.btnSelectGrade);
        btnSelectSubject = (Button) findViewById(R.id.btnSelectSubject);
        fabSave = (FloatingActionButton) findViewById(R.id.fabSave);

        adView.loadAd(utl.createAdRequest());

        //Check internet connection
        if (!utl.checkInternetConnection(conmng)) {
            Toast.makeText(getApplication(), Constants.Error_NoInternetConnection, Toast.LENGTH_SHORT).show();
            return;
        }

        //Execute
        ApiCommunication comm = new ApiCommunication(UserArticleActivity.this, Constants.Alert_DownloadArticle, "GET");
        comm.delegate = UserArticleActivity.this;
        requestType = 0;

        long userID = GLOBAL.USER.UserID;
        String value = utl.base64Encode(String.valueOf(userID));
        String url = String.format(getString(R.string.GetUserArticle), value);
        comm.execute(url);

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


        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check internet connection
                if (!utl.checkInternetConnection(conmng)) {
                    Toast.makeText(getApplication(), Constants.Error_NoInternetConnection, Toast.LENGTH_SHORT).show();
                    return;
                }

                //Validation
                if (!utl.validationEditText(edtGrade, edtSubject, edtContent)) {
                    utl.showSuperDialog(new SuperDialog(),
                            getFragmentManager(),
                            false,
                            SuperDialog.DIALOG_TYPE_ERROR,
                            Constants.Error_MissingRequiredField);
                    return;
                }

                closeDialogFlag = 2;
                utl.showSuperDialog(new SuperDialog(),
                        getFragmentManager(),
                        false,
                        SuperDialog.DIALOG_TYPE_CONFIRM,
                        Constants.Confirm_SendArticle);
            }
        });


    }

    void showUserArticle(ModelArticle model) {
        txtCreatedDate.setText("Ngày cập nhật: " + model.CreatedDate);
        edtGrade.setText(model.Grade);
        edtSubject.setText(model.Subject);
        edtContent.setText(utl.convertBreakline(model.Content, 0));
    }

    @Override
    public void onFinishCommunication(MyResponse result) {
        if (requestType == 0) {
            if (result.ResponseCode.equals("01")) {
                ModelArticle model = ModelArticle.toModelObject(result.ResponseMessage);
                showUserArticle(model);
            } else {
                utl.showSuperDialog(new SuperDialog(),
                        getFragmentManager(),
                        false,
                        SuperDialog.DIALOG_TYPE_ERROR,
                        result.ResponseMessage);
            }
        } else {
            if (result.ResponseCode.equals("01")) {
                utl.showSuperDialog(new SuperDialog(),
                        getFragmentManager(),
                        true,
                        SuperDialog.DIALOG_TYPE_SUCCESS,
                        Constants.Success_UpdateInfo);
                txtCreatedDate.setText("Ngày cập nhật: " + utl.createCurrentDateTime());

            } else {
                utl.showSuperDialog(new SuperDialog(),
                        getFragmentManager(),
                        false,
                        SuperDialog.DIALOG_TYPE_ERROR,
                        result.ResponseMessage);
            }
        }
    }

    @Override
    public void confirmButtonClicked() {
        if (closeDialogFlag == 2) {
            ApiCommunication comm = new ApiCommunication(UserArticleActivity.this, Constants.Alert_PleaseWait, "GET");
            comm.delegate = UserArticleActivity.this;
            requestType = 1;

            ModelArticle model = new ModelArticle();
            model.UserID = GLOBAL.USER.UserID;
            model.Grade = edtGrade.getText().toString();
            model.Subject = edtSubject.getText().toString();
            model.Content = utl.convertBreakline(edtContent.getText().toString(), 1);

            String value = ModelArticle.toJson(model);
            value = utl.base64Encode(value);
            String url = String.format(getString(R.string.AddUserArticle), value);
            comm.execute(url);
        } else {
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
}
