package com.indcgroup.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by thefa on 19/08/2017.
 */

public class ModelUserProfile {
    public long AccountID;
    public String Username;
    public String Password;
    public long UserID;
    public String Fullname;
    public String Email;
    public String Phone;
    public String Address;
    public double Latitude;
    public double Longitude;
    public String Gender;
    public String IdentityCard;
    public String Avatar;
    public String Position;
    public String Documentation;

    public static ModelUserProfile toModelObject(String obj) {
        try {
            JSONObject jsonObject = new JSONObject(obj);

            ModelUserProfile item = new ModelUserProfile();
            item.AccountID = jsonObject.getLong("AccountID");
            item.Username = jsonObject.getString("Username");
            item.Password = jsonObject.getString("Password");
            item.UserID = jsonObject.getLong("UserID");
            item.Fullname = jsonObject.getString("Fullname");
            item.Email = jsonObject.getString("Email");
            item.Phone = jsonObject.getString("Phone");
            item.Address = jsonObject.getString("Address");
            item.Gender = jsonObject.getString("Gender");
            item.Avatar = jsonObject.getString("Avatar");
            item.IdentityCard = jsonObject.getString("IdentityCard");
            item.Position = jsonObject.getString("Position");
            item.Documentation = jsonObject.getString("Documentation");

            return item;

        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e("TAG", ex.getMessage());
        }
        return null;
    }

    public static String toJson(ModelUserProfile item) {
        try {
            JSONObject object = new JSONObject();

            object.put("Password", item.Password);

            object.put("UserID",item.UserID);
            object.put("Fullname", item.Fullname);
            object.put("Email", item.Email);
            object.put("Phone", item.Phone);
            object.put("Address", item.Address);
            object.put("Latitude", item.Latitude);
            object.put("Longitude", item.Longitude);
            object.put("Gender", item.Gender);
            object.put("Avatar", item.Avatar);
            object.put("Position", item.Position);
            object.put("Documentation", item.Documentation);

            return object.toString();

        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

}
