package com.indcgroup.model;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by thefa on 17/08/2017.
 */

public class ModelUserInformation {
    public long ArticleID;
    public String CreatedDate;
    public String Content;
    public String Grade;
    public String Subject;
    public long UserID;
    public String Fullname;
    public String Email;
    public String Phone;
    public String Address;
    public String Gender;
    public String Avatar;
    public String IdentityCard;
    public String Position;
    public String Documentation;

    public static ModelUserInformation toModelObject(String obj) {
        try {
            JSONObject jsonObject = new JSONObject(obj);

            ModelUserInformation item = new ModelUserInformation();
            item.ArticleID = jsonObject.getLong("ArticleID");
            item.CreatedDate = jsonObject.getString("CreatedDate");
            item.Content = jsonObject.getString("Content");
            item.Grade = jsonObject.getString("Grade");
            item.Subject = jsonObject.getString("Subject");
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

}
