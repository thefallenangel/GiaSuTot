package com.indcgroup.google;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.indcgroup.giasutot.R;
import com.indcgroup.utility.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import dmax.dialog.SpotsDialog;

/**
 * Created by thefa on 05/08/2017.
 */

public class GoogleApiCommunication extends AsyncTask<String, Void, String> {

    public static final String GET_ADDRESS = "GET_ADDRESS";
    public static final String GET_LOCATION = "GET_LOCATION";
    public static final String GET_DISTANCE = "GET_DISTANCE";

    public GoogleApiResponse delegate = null;
    private String mType;
    private int mIndex;

    private Activity mActivity;
    private String mMessage;
    SpotsDialog mDialog;

    public GoogleApiCommunication(Activity activity, String message, String type) {
        this.mActivity = activity;
        this.mMessage = message;
        this.mType = type;
    }

    public GoogleApiCommunication(Activity activity, String message, String type, int index) {
        this(activity, message, type);
        this.mIndex = index;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (!mActivity.getLocalClassName().contains("FirstLoadingActivity")) {
            mDialog = new SpotsDialog(mActivity, mMessage, R.style.Custom);
            mDialog.show();
        }
    }

    @Override
    protected void onPostExecute(String s) {

        if (mDialog != null)
            mDialog.dismiss();

        try {
            JSONObject jsonObject = new JSONObject(s);

            if (mType.equals(GET_ADDRESS)) {
                JSONArray jsonArray = jsonObject.getJSONArray("results");
                if (jsonArray != null && jsonArray.length() > 0) {

                    JSONObject tmp = jsonArray.getJSONObject(0);
                    String addr = tmp.getString("formatted_address");
                    delegate.onFinishCommunication(addr);

                }
            }
            if (mType.equals(GET_LOCATION)) {
                JSONArray jsonArray = jsonObject.getJSONArray("results");
                if (jsonArray != null && jsonArray.length() > 0) {

                    JSONObject tmp = jsonArray.getJSONObject(0);
                    JSONObject tmp1 = tmp.getJSONObject("geometry");
                    JSONObject tmp2 = tmp1.getJSONObject("location");

                    String lat = tmp2.getString("lat");
                    String lng = tmp2.getString("lng");

                    delegate.onFinishCommunication(lat + "|" + lng);
                }
            }

            if (mType.equals(GET_DISTANCE)) {
                JSONArray jsonArray = jsonObject.getJSONArray("rows");
                JSONObject tmp1 = jsonArray.getJSONObject(0);
                JSONArray tmp2 = tmp1.getJSONArray("elements");
                JSONObject tmp3 = tmp2.getJSONObject(0);

                String status = tmp3.getString("status");
                if (status.equals("OK")) {
                    JSONObject tmp4 = tmp3.getJSONObject("distance");
                    String distance = tmp4.getString("text");
                    delegate.onFinishCommunication(mIndex + "|" + distance);
                } else {
                    delegate.onFinishCommunication(mIndex + "|" + Constants.Error_CanNotEstimateDistance);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e(Constants.TAG, ex.getMessage());
        }
    }


    @Override
    protected String doInBackground(String... strings) {
        String result = "";

        String link = strings[0];
        URL url;
        HttpURLConnection urlConnection = null;

        try {
            url = new URL(link);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Connection", "close");


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
        return result;
    }

}
