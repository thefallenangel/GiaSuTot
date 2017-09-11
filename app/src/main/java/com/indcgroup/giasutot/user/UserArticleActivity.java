package com.indcgroup.giasutot.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.indcgroup.dialog.SuperDialog;
import com.indcgroup.dialog.SuperDialogCloseListener;
import com.indcgroup.dialog.SuperDialogConfirmListener;
import com.indcgroup.giasutot.R;
import com.indcgroup.giasutot.customer.SignInActivity;
import com.indcgroup.giasutot.customer.SignUpActivity;
import com.indcgroup.model.ModelArticle;
import com.indcgroup.model.MyResponse;
import com.indcgroup.loadresult.ApiCommunication;
import com.indcgroup.loadresult.ApiResponse;
import com.indcgroup.utility.Constants;
import com.indcgroup.utility.GLOBAL;
import com.indcgroup.utility.Utilities;


public class UserArticleActivity extends AppCompatActivity implements ApiResponse, SuperDialogConfirmListener {

    Utilities utl = new Utilities();
    ConnectivityManager conmng;
    int requestType = 0;

    AdView adView;
    TextView txtCreatedDate;
    MultiAutoCompleteTextView atxtGrade, atxtSubject;
    EditText edtContent;
    FloatingActionButton fabSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_article);

        conmng = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        adView = (AdView) findViewById(R.id.adView);
        txtCreatedDate = (TextView) findViewById(R.id.txtCreatedDate);
        atxtGrade = (MultiAutoCompleteTextView) findViewById(R.id.atxtGrade);
        atxtSubject = (MultiAutoCompleteTextView) findViewById(R.id.atxtSubject);
        edtContent = (EditText) findViewById(R.id.edtContent);
        fabSave = (FloatingActionButton) findViewById(R.id.fabSave);

        adView.loadAd(utl.createAdRequest());

        atxtGrade.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        atxtSubject.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        atxtGrade.setAdapter(new ArrayAdapter<String>(this, R.layout.adapter_simple_text, Constants.My_Grade));
        atxtSubject.setAdapter(new ArrayAdapter<String>(this, R.layout.adapter_simple_text, Constants.My_Subject));

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

        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check internet connection
                if (!utl.checkInternetConnection(conmng)) {
                    Toast.makeText(getApplication(), Constants.Error_NoInternetConnection, Toast.LENGTH_SHORT).show();
                    return;
                }

                //Validation
                if (!utl.validationEditText(edtContent) && !utl.validationMultiAutoCompleteTextView(atxtGrade, atxtSubject)) {
                    utl.showSuperDialog(new SuperDialog(),
                            getFragmentManager(),
                            false,
                            SuperDialog.DIALOG_TYPE_ERROR,
                            Constants.Error_MissingRequiredField);
                    return;
                }

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
        atxtGrade.setText(model.Grade);
        atxtSubject.setText(model.Subject);
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
        ApiCommunication comm = new ApiCommunication(UserArticleActivity.this, Constants.Alert_PleaseWait, "GET");
        comm.delegate = UserArticleActivity.this;
        requestType = 1;

        ModelArticle model = new ModelArticle();
        model.UserID = GLOBAL.USER.UserID;
        model.Grade = atxtGrade.getText().toString();
        model.Subject = atxtSubject.getText().toString();
        model.Content = utl.convertBreakline(edtContent.getText().toString(), 1);

        String value = ModelArticle.toJson(model);
        value = utl.base64Encode(value);
        String url = String.format(getString(R.string.AddUserArticle), value);
        comm.execute(url);
    }
}
