package com.indcgroup.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by thefa on 18/08/2017.
 */

public class ModelTransaction {
    public long ID;
    public int Point;
    public boolean Type;
    public String CreatedDate;
    public String Description;
    public long UserID;

    public static ArrayList<ModelTransaction> toModelList(String obj) {
        ArrayList<ModelTransaction> result = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(obj);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                ModelTransaction item = new ModelTransaction();
                item.ID = jsonObject.getLong("ID");
                item.Point = jsonObject.getInt("Point");
                item.Type = jsonObject.getBoolean("Type");
                item.CreatedDate = jsonObject.getString("CreatedDate");
                item.Description = jsonObject.getString("Description");
                item.UserID = jsonObject.getLong("UserID");

                result.add(item);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e("TAG", ex.getMessage());
        }

        return result;
    }

}
