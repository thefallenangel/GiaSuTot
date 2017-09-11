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
import com.indcgroup.utility.Utilities;

public class RecruitmentActivity extends AppCompatActivity implements ApiResponse, SuperDialogCloseListener, SuperDialogConfirmListener {

    Utilities utl = new Utilities();
    ConnectivityManager conmng;

    AdView adView;
    MultiAutoCompleteTextView atxtGrade, atxtSubject;
    EditText edtAddress, edtPhone, edtContent;
    Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruitment);

        conmng = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        adView = (AdView) findViewById(R.id.adView);
        atxtGrade = (MultiAutoCompleteTextView) findViewById(R.id.atxtGrade);
        atxtSubject = (MultiAutoCompleteTextView) findViewById(R.id.atxtSubject);
        edtAddress = (EditText) findViewById(R.id.edtAddress);
        edtPhone = (EditText) findViewById(R.id.edtPhone);
        edtContent = (EditText) findViewById(R.id.edtContent);
        btnSend = (Button) findViewById(R.id.btnSend);

        adView.loadAd(utl.createAdRequest());

        atxtGrade.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        atxtSubject.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        atxtGrade.setAdapter(new ArrayAdapter<String>(this, R.layout.adapter_simple_text, Constants.My_Grade));
        atxtSubject.setAdapter(new ArrayAdapter<String>(this, R.layout.adapter_simple_text, Constants.My_Subject));

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Validation
                if (!utl.validationMultiAutoCompleteTextView(atxtGrade, atxtSubject) ||
                        !utl.validationEditText(edtAddress, edtPhone, edtContent)) {
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
        //Execute
        ApiCommunication comm = new ApiCommunication(RecruitmentActivity.this, Constants.Alert_PleaseWait, "GET");
        comm.delegate = RecruitmentActivity.this;

        ModelRecruitment model = new ModelRecruitment();
        model.Grade = atxtGrade.getText().toString();
        model.Subject = atxtSubject.getText().toString();
        model.Address = edtAddress.getText().toString();
        model.Phone = edtPhone.getText().toString();
        model.Content = utl.convertBreakline(edtContent.getText().toString(), 1);

        String value = ModelRecruitment.toJson(model);
        value = utl.base64Encode(value);
        String url = String.format(getString(R.string.AddRecruitment), value);
        comm.execute(url);
    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {
        Intent intent = new Intent(RecruitmentActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
