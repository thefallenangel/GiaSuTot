package com.indcgroup.giasutot.customer;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.indcgroup.dialog.SuperDialog;
import com.indcgroup.dialog.SuperDialogCloseListener;
import com.indcgroup.giasutot.R;
import com.indcgroup.giasutot.user.UserActivity;
import com.indcgroup.loadresult.ApiCommunication;
import com.indcgroup.loadresult.ApiResponse;
import com.indcgroup.model.ModelAuthentication;
import com.indcgroup.model.MyResponse;
import com.indcgroup.utility.Constants;
import com.indcgroup.utility.Utilities;

public class ForgetPasswordActivity extends AppCompatActivity implements ApiResponse, SuperDialogCloseListener {

    Utilities utl = new Utilities();
    ConnectivityManager conmng;

    AdView adView;
    EditText edtUsername, edtEmail;
    Button btnGetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        conmng = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        adView = (AdView) findViewById(R.id.adView);
        edtUsername = (EditText) findViewById(R.id.edtUsername);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        btnGetPassword = (Button) findViewById(R.id.btnGetPassword);

        adView.loadAd(utl.createAdRequest());

        btnGetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check internet connection
                if (!utl.checkInternetConnection(conmng)) {
                    Toast.makeText(getApplication(), Constants.Error_NoInternetConnection, Toast.LENGTH_SHORT).show();
                    return;
                }

                //Validation
                if (!utl.validationEditText(edtUsername, edtEmail)) {
                    utl.showSuperDialog(new SuperDialog(),
                            getFragmentManager(),
                            false,
                            SuperDialog.DIALOG_TYPE_ERROR,
                            Constants.Error_MissingRequiredField);
                    return;
                }

                //Execute
                ApiCommunication comm = new ApiCommunication(ForgetPasswordActivity.this, Constants.Alert_PleaseWait, "GET");
                comm.delegate = ForgetPasswordActivity.this;

                ModelAuthentication model = new ModelAuthentication();
                model.Username = edtUsername.getText().toString();
                model.Email = edtEmail.getText().toString();

                String value = ModelAuthentication.toJson(model);
                value = utl.base64Encode(value);
                String url = String.format(getString(R.string.RecoveryPassword), value);
                comm.execute(url);
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
                    result.ResponseMessage);
        } else {
            utl.showSuperDialog(new SuperDialog(),
                    getFragmentManager(),
                    false,
                    SuperDialog.DIALOG_TYPE_ERROR,
                    result.ResponseMessage);
        }
    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {
        Intent intent = new Intent(ForgetPasswordActivity.this, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
