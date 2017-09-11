package com.indcgroup.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by thefa on 18/08/2017.
 */

public class ModelRating {
    public long ID;
    public String IMEI;
    public long UserID;
    public int Value;

    public static String toJson(ModelRating item) {
        try {
            JSONObject object = new JSONObject();

            object.put("IMEI", item.IMEI);
            object.put("UserID", item.UserID);
            object.put("Value", item.Value);

            return object.toString();

        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

}
