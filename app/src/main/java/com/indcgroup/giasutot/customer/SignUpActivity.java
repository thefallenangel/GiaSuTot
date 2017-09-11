package com.indcgroup.giasutot.customer;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.indcgroup.dialog.SuperDialogCloseListener;
import com.indcgroup.giasutot.R;
import com.indcgroup.google.GoogleApiCommunication;
import com.indcgroup.google.GoogleApiResponse;
import com.indcgroup.model.ModelAuthentication;
import com.indcgroup.model.MyResponse;
import com.indcgroup.loadresult.ApiCommunication;
import com.indcgroup.loadresult.ApiResponse;
import com.indcgroup.utility.Constants;
import com.indcgroup.dialog.SuperDialog;
import com.indcgroup.utility.Utilities;

import java.net.URLEncoder;


public class SignUpActivity extends AppCompatActivity implements ApiResponse, GoogleApiResponse, SuperDialogCloseListener {

    Utilities utl = new Utilities();
    ConnectivityManager conmng;


    private static int RESULT_LOAD_IMAGE = 1;

    AdView adView;
    ImageButton imgAvatar;
    EditText edtUsername, edtPassword;
    EditText edtFullname, edtEmail, edtPhone, edtAddress, edtIdentityCard, edtDocumentation;
    RadioButton radMale, radFemale, radStudent, radGraduated, radTeacher;
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        conmng = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        adView = (AdView) findViewById(R.id.adView);
        imgAvatar = (ImageButton) findViewById(R.id.imgAvatar);
        edtUsername = (EditText) findViewById(R.id.edtUsername);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtFullname = (EditText) findViewById(R.id.edtFullname);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPhone = (EditText) findViewById(R.id.edtPhone);
        edtAddress = (EditText) findViewById(R.id.edtAddress);
        edtIdentityCard = (EditText) findViewById(R.id.edtIdentityCard);
        edtDocumentation = (EditText) findViewById(R.id.edtDocumentation);
        radMale = (RadioButton) findViewById(R.id.radMale);
        radFemale = (RadioButton) findViewById(R.id.radFemale);
        radStudent = (RadioButton) findViewById(R.id.radStudent);
        radGraduated = (RadioButton) findViewById(R.id.radGraduated);
        radTeacher = (RadioButton) findViewById(R.id.radTeacher);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);

        adView.loadAd(utl.createAdRequest());

        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_LOAD_IMAGE);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Check internet connection
                if (!utl.checkInternetConnection(conmng)) {
                    Toast.makeText(getApplication(), Constants.Error_NoInternetConnection, Toast.LENGTH_SHORT).show();
                    return;
                }

                //Validation
                if (!utl.validationEditText(edtUsername, edtPassword, edtFullname, edtEmail, edtPhone, edtAddress, edtIdentityCard, edtDocumentation)) {
                    utl.showSuperDialog(new SuperDialog(),
                            getFragmentManager(),
                            false,
                            SuperDialog.DIALOG_TYPE_ERROR,
                            Constants.Error_MissingRequiredField);
                    return;
                }

                //Get geocode based on address
                try {
                    GoogleApiCommunication comm = new GoogleApiCommunication(SignUpActivity.this, Constants.Alert_GetAddress, GoogleApiCommunication.GET_LOCATION);
                    comm.delegate = SignUpActivity.this;

                    String value = edtAddress.getText().toString();
                    String url = String.format(getString(R.string.Google_GetLocationURL), URLEncoder.encode(value, "UTF-8"));
                    comm.execute(url);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
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
                    Constants.Success_SignUp);
        } else {
            utl.showSuperDialog(new SuperDialog(),
                    getFragmentManager(),
                    false,
                    SuperDialog.DIALOG_TYPE_ERROR,
                    result.ResponseMessage);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {

            Uri selectedImage = data.getData();

            if (!utl.checkImageSize(selectedImage, this)) {
                utl.showSuperDialog(new SuperDialog(),
                        getFragmentManager(),
                        false,
                        SuperDialog.DIALOG_TYPE_ERROR,
                        Constants.Error_BigImage);
                return;
            }

            try {
                String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
                Cursor cur = getApplicationContext().getContentResolver().query(selectedImage, orientationColumn, null, null, null);
                int orientation = 0;
                if (cur != null && cur.moveToFirst()) {
                    orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
                }
                cur.close();

                Bitmap bm = utl.handleImageCaptured(orientation, this, selectedImage);
                imgAvatar.setImageBitmap(bm);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onFinishCommunication(String result) {

        double lat = Double.valueOf(result.split("\\|")[0]);
        double lng = Double.valueOf(result.split("\\|")[1]);


        ModelAuthentication model = new ModelAuthentication();
        model.Username = edtUsername.getText().toString();
        model.Password = edtPassword.getText().toString();

        model.Fullname = edtFullname.getText().toString();
        model.Email = edtEmail.getText().toString();
        model.Phone = edtPhone.getText().toString();
        model.Address = edtAddress.getText().toString();
        model.Latitude = lat;
        model.Longitude = lng;
        model.Gender = radMale.isChecked() ? Constants.My_Gender[0] : Constants.My_Gender[1];
        model.IdentityCard = edtIdentityCard.getText().toString();
        model.Documentation = edtDocumentation.getText().toString();
        if (radStudent.isChecked())
            model.Position = Constants.My_Position[0];
        else if (radGraduated.isChecked())
            model.Position = Constants.My_Position[1];
        else
            model.Position = Constants.My_Position[2];
        Bitmap bm = ((BitmapDrawable) imgAvatar.getDrawable()).getBitmap();


        ApiCommunication comm = new ApiCommunication(SignUpActivity.this, Constants.Alert_PleaseWait, "GET", bm);
        comm.delegate = SignUpActivity.this;

        String value = ModelAuthentication.toJson(model);
        value = utl.base64Encode(value);
        String url = String.format(getString(R.string.RegisterURL), value);
        comm.execute(url);
    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {
        Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
