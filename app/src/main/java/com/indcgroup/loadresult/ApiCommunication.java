package com.indcgroup.loadresult;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.indcgroup.giasutot.R;
import com.indcgroup.model.MyResponse;
import com.indcgroup.utility.Constants;
import com.indcgroup.utility.Utilities;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import dmax.dialog.SpotsDialog;

/**
 * Created by thefa on 05/08/2017.
 */

public class ApiCommunication extends AsyncTask<String, String, String> {

    public ApiResponse delegate = null;
    private Utilities utl = new Utilities();

    SpotsDialog mDialog;
    Activity mActivity;
    Bitmap mAvatar;
    String mMethod, mMessage;

    public ApiCommunication(Activity activity, String message, String method) {
        this.mActivity = activity;
        this.mMethod = method;
        this.mMessage = message;
    }

    public ApiCommunication(Activity activity, String message, String method, Bitmap avatar) {
        this(activity, message, method);
        this.mAvatar = avatar;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (!mActivity.getLocalClassName().contains("FirstLoadingActivity")) {
            mDialog = new SpotsDialog(mActivity, mMessage, R.style.Custom);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setCancelable(false);
            mDialog.show();
        }
    }

    @Override
    protected void onPostExecute(String s) {
        if (mDialog != null)
            mDialog.dismiss();

        try {
            JSONObject jsonObject = new JSONObject(s);
            MyResponse item = new MyResponse();
            item.ResponseCode = jsonObject.getString("Code");
            item.ResponseMessage = jsonObject.getString("Message");
            delegate.onFinishCommunication(item);
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e(Constants.TAG, ex.getMessage());
        }
    }


    @Override
    protected String doInBackground(String... strings) {
        String result = "";
        String link = strings[0];

        Log.e(Constants.TAG, link);

        URL url;
        HttpURLConnection urlConnection = null;

        try {
            url = new URL(link);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(mMethod);
            urlConnection.setUseCaches(false);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Connection", "close");
            urlConnection.setRequestProperty("Cache-Control", "no-cache");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "*/*");

            if (mAvatar != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                mAvatar.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

                byte[] imgInByteArray = byteArrayOutputStream.toByteArray();
                String enImage = utl.base64Encode2(imgInByteArray);

                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "ASCII"));
                writer.write(enImage);

                writer.flush();
                writer.close();
                os.close();
            }

            InputStream in = urlConnection.getInputStream();
            InputStreamReader isw = new InputStreamReader(in);
            int data = isw.read();
            while (data != -1) {
                char current = (char) data;
                data = isw.read();
                result += current;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e(Constants.TAG, ex.getMessage());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        try {
            result = utl.base64Decode(result);

            if (!result.equals("")) {
                //result = result.substring(1, result.length() - 1);
                result = result.replace("\\", "");
                result = result.replace("\"[", "[");
                result = result.replace("]\"", "]");
                result = result.replace("\"{", "{");
                result = result.replace("}\"", "}");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e(Constants.TAG, ex.getMessage());
        }

        return result;
    }
}
