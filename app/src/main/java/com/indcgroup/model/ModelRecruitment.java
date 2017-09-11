package com.indcgroup.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by thefa on 18/08/2017.
 */

public class ModelRecruitment {
    public long ID;
    public String Grade;
    public String Subject;
    public String Address;
    public String Phone;
    public String Content;
    public String CreatedDate;

    //Convert from Object to Json
    public static String toJson(ModelRecruitment item) {
        try {
            JSONObject object = new JSONObject();

            object.put("Grade", item.Grade);
            object.put("Subject", item.Subject);
            object.put("Address", item.Address);
            object.put("Phone", item.Phone);
            object.put("Content", item.Content);

            return object.toString();

        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static ArrayList<ModelRecruitment> toModelList(String obj) {
        ArrayList<ModelRecruitment> result = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(obj);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                ModelRecruitment item = new ModelRecruitment();

                item.ID = jsonObject.getLong("ID");
                item.Grade = jsonObject.getString("Grade");
                item.Subject = jsonObject.getString("Subject");
                item.Address = jsonObject.getString("Address");
                item.Phone = jsonObject.getString("Phone");
                item.Content = jsonObject.getString("Content");
                item.CreatedDate = jsonObject.getString("CreatedDate");

                result.add(item);
            }


        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e("TAG", ex.getMessage());
        }

        return result;
    }

}
