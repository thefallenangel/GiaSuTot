package com.indcgroup.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by thefa on 05/08/2017.
 */

public class ModelAuthentication {
    //Account
    public String Username;
    public String Password;

    //User
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

    //Convert from Object to Json
    public static String toJson(ModelAuthentication item) {
        try {
            JSONObject object = new JSONObject();

            object.put("Username", item.Username);
            object.put("Password", item.Password);

            object.put("Fullname", item.Fullname);
            object.put("Email", item.Email);
            object.put("Phone", item.Phone);
            object.put("Address", item.Address);
            object.put("Latitude", item.Latitude);
            object.put("Longitude", item.Longitude);
            object.put("Gender", item.Gender);
            object.put("IdentityCard", item.IdentityCard);
            object.put("Avatar", item.Avatar);
            object.put("Position", item.Position);
            object.put("Documentation", item.Documentation);

            return object.toString();

        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static ModelAuthentication toModelObject(String obj) {
        ModelAuthentication result = new ModelAuthentication();

        try {
            JSONObject jsonObject = new JSONObject(obj);

            result.UserID = jsonObject.getLong("UserID");
            result.Fullname = jsonObject.getString("Fullname");
            result.Email = jsonObject.getString("Email");
            result.Phone = jsonObject.getString("Phone");
            result.Address = jsonObject.getString("Address");
            result.Latitude = jsonObject.getDouble("Latitude");
            result.Longitude = jsonObject.getDouble("Longitude");
            result.Gender = jsonObject.getString("Gender");
            result.IdentityCard = jsonObject.getString("IdentityCard");
            result.Avatar = jsonObject.getString("Avatar");
            result.Position = jsonObject.getString("Position");
            result.Documentation = jsonObject.getString("Documentation");

            return result;

        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e("TAG", ex.getMessage());
        }
        return null;
    }
}
