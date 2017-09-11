package com.indcgroup.giasutot.customer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.indcgroup.dialog.SuperDialogCloseListener;
import com.indcgroup.giasutot.R;
import com.indcgroup.giasutot.user.UserActivity;
import com.indcgroup.model.ModelAuthentication;
import com.indcgroup.model.MyResponse;
import com.indcgroup.loadresult.ApiCommunication;
import com.indcgroup.loadresult.ApiResponse;
import com.indcgroup.utility.Constants;
import com.indcgroup.dialog.SuperDialog;
import com.indcgroup.utility.GLOBAL;
import com.indcgroup.utility.Utilities;

public class SignInActivity extends AppCompatActivity implements ApiResponse, SuperDialogCloseListener {

    Utilities utl = new Utilities();
    ConnectivityManager conmng;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    AdView adView;
    EditText edtUsername, edtPassword;
    Button btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        conmng = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        adView = (AdView) findViewById(R.id.adView);
        edtUsername = (EditText) findViewById(R.id.edtUsername);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);

        adView.loadAd(utl.createAdRequest());

        //Load shared preferences
        preferences = getSharedPreferences("Security", MODE_PRIVATE);
        String savedUsername = preferences.getString("Username", "");
        String savedPassword = preferences.getString("Password", "");
        edtUsername.setText(savedUsername);
        edtPassword.setText(savedPassword);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Check internet connection
                if (!utl.checkInternetConnection(conmng)) {
                    Toast.makeText(getApplication(), Constants.Error_NoInternetConnection, Toast.LENGTH_SHORT).show();
                    return;
                }

                //Validation
                if (!utl.validationEditText(edtUsername, edtPassword)) {
                    utl.showSuperDialog(new SuperDialog(),
                            getFragmentManager(),
                            false,
                            SuperDialog.DIALOG_TYPE_ERROR,
                            Constants.Error_MissingRequiredField);
                    return;
                }

                //Execute
                ApiCommunication comm = new ApiCommunication(SignInActivity.this, Constants.Alert_PleaseWait, "GET");
                comm.delegate = SignInActivity.this;

                ModelAuthentication model = new ModelAuthentication();
                model.Username = edtUsername.getText().toString();
                model.Password = edtPassword.getText().toString();

                String value = ModelAuthentication.toJson(model);
                value = utl.base64Encode(value);
                String url = String.format(getString(R.string.LoginURL), value);
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
                    Constants.Success_SignIn);


            //Save the logined information for the next time
            editor = preferences.edit();
            editor.putString("Username", edtUsername.getText().toString());
            editor.putString("Password", edtPassword.getText().toString());
            editor.apply();

            //Save to global
            ModelAuthentication model = ModelAuthentication.toModelObject(result.ResponseMessage);
            GLOBAL.USER = model;

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
        Intent intent = new Intent(SignInActivity.this, UserActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
