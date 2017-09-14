package com.indcgroup.giasutot.customer;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.indcgroup.dialog.SuperDialog;
import com.indcgroup.dialog.SuperDialogCloseListener;
import com.indcgroup.dialog.SuperDialogConfirmListener;
import com.indcgroup.giasutot.R;
import com.indcgroup.model.ModelRecruitment;
import com.indcgroup.model.MyResponse;
import com.indcgroup.loadresult.ApiCommunication;
import com.indcgroup.loadresult.ApiResponse;
import com.indcgroup.utility.Constants;
import com.indcgroup.utility.GLOBAL;
import com.indcgroup.utility.Utilities;

import java.util.ArrayList;

public class RecruitmentActivity extends AppCompatActivity implements ApiResponse, SuperDialogCloseListener, SuperDialogConfirmListener {

    static int closeDialogFlag = 0;
    Utilities utl = new Utilities();
    ConnectivityManager conmng;

    AdView adView;
    EditText edtGrade, edtSubject, edtAddress, edtPhone, edtContent;
    Button btnSelectGrade, btnSelectSubject, btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruitment);

        conmng = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        adView = (AdView) findViewById(R.id.adView);
        edtAddress = (EditText) findViewById(R.id.edtAddress);
        edtPhone = (EditText) findViewById(R.id.edtPhone);
        edtContent = (EditText) findViewById(R.id.edtContent);
        edtGrade = (EditText) findViewById(R.id.edtGrade);
        edtSubject = (EditText) findViewById(R.id.edtSubject);
        btnSelectGrade = (Button) findViewById(R.id.btnSelectGrade);
        btnSelectSubject = (Button) findViewById(R.id.btnSelectSubject);
        btnSend = (Button) findViewById(R.id.btnSend);

        adView.loadAd(utl.createAdRequest());

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

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Validation
                if (!utl.validationEditText(edtGrade, edtSubject, edtAddress, edtPhone, edtContent)) {
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

                closeDialogFlag = 2;
                utl.showSuperDialog(new SuperDialog(),
                        getFragmentManager(),
                        false,
                        SuperDialog.DIALOG_TYPE_CONFIRM,
                        Constants.Confirm_SendRecruitment);

            }
        });

    }

    @Override
    public void onFinishCommunication(MyResponse result) {
        if (result.ResponseCode.equals("01")) {
            utl.showSuperDialog(new SuperDialog(),
                    getFragmentManager(),
                    true,
                    SuperDialog.DIALOG_TYPE_SUCCESS,
                    Constants.Success_SendRecruitment);
        } else {
            utl.showSuperDialog(new SuperDialog(),
                    getFragmentManager(),
                    false,
                    SuperDialog.DIALOG_TYPE_ERROR,
                    result.ResponseMessage);
        }
    }

    @Override
    public void confirmButtonClicked() {

        if (closeDialogFlag == 2) {
            //Execute
            ApiCommunication comm = new ApiCommunication(RecruitmentActivity.this, Constants.Alert_PleaseWait, "GET");
            comm.delegate = RecruitmentActivity.this;

            ModelRecruitment model = new ModelRecruitment();
            model.Grade = edtGrade.getText().toString();
            model.Subject = edtSubject.getText().toString();
            model.Address = edtAddress.getText().toString();
            model.Phone = edtPhone.getText().toString();
            model.Content = utl.convertBreakline(edtContent.getText().toString(), 1);

            String value = ModelRecruitment.toJson(model);
            value = utl.base64Encode(value);
            String url = String.format(getString(R.string.AddRecruitment), value);
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

    @Override
    public void handleDialogClose(DialogInterface dialog) {
        Intent intent = new Intent(RecruitmentActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
