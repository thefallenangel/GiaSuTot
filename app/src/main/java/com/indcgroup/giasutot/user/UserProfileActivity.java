package com.indcgroup.giasutot.user;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.indcgroup.dialog.SuperDialog;
import com.indcgroup.giasutot.R;
import com.indcgroup.google.GoogleApiCommunication;
import com.indcgroup.google.GoogleApiResponse;
import com.indcgroup.loadimage.ImageCommunication;
import com.indcgroup.model.ModelUserProfile;
import com.indcgroup.model.MyResponse;
import com.indcgroup.loadresult.ApiCommunication;
import com.indcgroup.loadresult.ApiResponse;
import com.indcgroup.utility.Constants;
import com.indcgroup.utility.GLOBAL;
import com.indcgroup.utility.Utilities;

import java.net.URLEncoder;

public class UserProfileActivity extends AppCompatActivity implements ApiResponse, GoogleApiResponse {

    Utilities utl = new Utilities();
    ConnectivityManager conmng;
    int requestType = 0;

    private static int RESULT_LOAD_IMAGE = 1;

    AdView adView;
    EditText edtUsername, edtPassword;
    ImageButton imgAvatar;
    EditText edtFullname, edtEmail, edtPhone, edtAddress, edtIdentityCard, edtDocumentation;
    RadioButton radMale, radFemale, radStudent, radGraduated, radTeacher;
    FloatingActionButton fabSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        conmng = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        adView = (AdView) findViewById(R.id.adView);
        edtUsername = (EditText) findViewById(R.id.edtUsername);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtFullname = (EditText) findViewById(R.id.edtFullname);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPhone = (EditText) findViewById(R.id.edtPhone);
        edtAddress = (EditText) findViewById(R.id.edtAddress);
        edtIdentityCard = (EditText) findViewById(R.id.edtIdentityCard);
        edtDocumentation = (EditText) findViewById(R.id.edtDocumentation);
        imgAvatar = (ImageButton) findViewById(R.id.imgAvatar);
        radMale = (RadioButton) findViewById(R.id.radMale);
        radFemale = (RadioButton) findViewById(R.id.radFemale);
        radStudent = (RadioButton) findViewById(R.id.radStudent);
        radGraduated = (RadioButton) findViewById(R.id.radGraduated);
        radTeacher = (RadioButton) findViewById(R.id.radTeacher);
        fabSave = (FloatingActionButton) findViewById(R.id.fabSave);

        adView.loadAd(utl.createAdRequest());

        //Check internet connection
        if (!utl.checkInternetConnection(conmng)) {
            Toast.makeText(getApplication(), Constants.Error_NoInternetConnection, Toast.LENGTH_SHORT).show();
            return;
        }

        //Execute
        ApiCommunication comm = new ApiCommunication(UserProfileActivity.this, Constants.Alert_DownloadUser, "GET");
        comm.delegate = UserProfileActivity.this;
        requestType = 0;

        long userID = GLOBAL.USER.UserID;
        String value = utl.base64Encode(String.valueOf(userID));
        String url = String.format(getString(R.string.GetUserProfile), value);
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
                if (!utl.validationEditText(edtPassword, edtFullname, edtEmail, edtPhone, edtAddress, edtDocumentation)) {
                    utl.showSuperDialog(new SuperDialog(),
                            getFragmentManager(),
                            false,
                            SuperDialog.DIALOG_TYPE_ERROR,
                            Constants.Error_MissingRequiredField);
                    return;
                }

                //Get geocode based on address
                try {
                    GoogleApiCommunication comm = new GoogleApiCommunication(UserProfileActivity.this, Constants.Alert_GetAddress, GoogleApiCommunication.GET_LOCATION);
                    comm.delegate = UserProfileActivity.this;

                    String value = edtAddress.getText().toString();
                    String url = String.format(getString(R.string.Google_GetLocationURL), URLEncoder.encode(value, "UTF-8"));
                    comm.execute(url);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_LOAD_IMAGE);
            }
        });

    }

    void showUserProfile(ModelUserProfile model) {
        edtUsername.setText(model.Username);
        edtPassword.setText(model.Password);
        edtFullname.setText(model.Fullname);
        edtEmail.setText(model.Email);
        edtPhone.setText(model.Phone);
        edtAddress.setText(model.Address);
        edtIdentityCard.setText(model.IdentityCard);
        edtDocumentation.setText(model.Documentation);
        if (model.Gender.equals(Constants.My_Gender[0]))
            radMale.setChecked(true);
        else
            radFemale.setChecked(true);
        if (model.Position.equals(Constants.My_Position[0])) {
            radStudent.setChecked(true);

        } else if (model.Position.equals(Constants.My_Position[1])) {
            radGraduated.setChecked(true);

        } else if (model.Position.equals(Constants.My_Position[2])) {
            radTeacher.setChecked(true);
        }

        //Load avatar image
        String url = String.format(getString(R.string.LoadAvatar), String.valueOf(GLOBAL.USER.UserID));
        new ImageCommunication(imgAvatar).execute(url);

    }

    @Override
    public void onFinishCommunication(MyResponse result) {
        if (result.ResponseCode.equals("01")) {
            if (requestType == 0) {
                ModelUserProfile model = ModelUserProfile.toModelObject(result.ResponseMessage);
                showUserProfile(model);
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
        } else {
            utl.showSuperDialog(new SuperDialog(),
                    getFragmentManager(),
                    false,
                    SuperDialog.DIALOG_TYPE_ERROR,
                    result.ResponseMessage);
        }
    }

    @Override
    public void onFinishCommunication(String result) {
        double lat = Double.valueOf(result.split("\\|")[0]);
        double lng = Double.valueOf(result.split("\\|")[1]);

        ModelUserProfile model = new ModelUserProfile();
        model.Password = edtPassword.getText().toString();
        model.UserID = GLOBAL.USER.UserID;
        model.Fullname = edtFullname.getText().toString();
        model.Email = edtEmail.getText().toString();
        model.Phone = edtPhone.getText().toString();
        model.Address = edtAddress.getText().toString();
        model.Latitude = lat;
        model.Longitude = lng;
        model.Gender = radMale.isChecked() ? Constants.My_Gender[0] : Constants.My_Gender[1];
        model.Position = radStudent.isChecked() ? Constants.My_Position[0] : radGraduated.isChecked() ? Constants.My_Position[1] : Constants.My_Position[2];
        model.Documentation = edtDocumentation.getText().toString();

        Bitmap bm = ((BitmapDrawable) imgAvatar.getDrawable()).getBitmap();

        ApiCommunication comm = new ApiCommunication(UserProfileActivity.this, Constants.Alert_PleaseWait, "GET", bm);
        comm.delegate = UserProfileActivity.this;
        requestType = 1;

        String value = ModelUserProfile.toJson(model);
        value = utl.base64Encode(value);
        String url = String.format(getString(R.string.SaveUserProfile), value);
        comm.execute(url);
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
}
