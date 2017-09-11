package com.indcgroup.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.SyncStateContract;
import android.util.Log;

import com.indcgroup.utility.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by thefa on 09/08/2017.
 */

public class ModelArticle implements Parcelable {

    //Param
    public int Page;
    public int Sort;
    public double Latitude;
    public double Longitude;

    public long ArticleID;
    public String Content;
    public String CreatedDate;
    public String Grade;
    public String Subject;
    public long UserID;
    public String Fullname;
    public String Avatar;
    public String Gender;
    public String Position;
    public String Distance;
    public String Rate;

    //Constructor
    public ModelArticle() {
    }

    protected ModelArticle(Parcel in) {
        Page = in.readInt();
        Sort = in.readInt();
        Latitude = in.readDouble();
        Longitude = in.readDouble();
        ArticleID = in.readLong();
        Content = in.readString();
        CreatedDate = in.readString();
        Grade = in.readString();
        Subject = in.readString();
        UserID = in.readLong();
        Avatar = in.readString();
        Fullname = in.readString();
        Gender = in.readString();
        Position = in.readString();
        Distance = in.readString();
        Rate = in.readString();
    }


    //Convert from json to object
    public static ArrayList<ModelArticle> toModelList(String obj) {
        ArrayList<ModelArticle> result = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(obj);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                ModelArticle item = new ModelArticle();
                item.Page = jsonObject.getInt("Page");
                item.Latitude = jsonObject.getDouble("Latitude");
                item.Longitude = jsonObject.getDouble("Longitude");
                item.Sort = jsonObject.getInt("Sort");

                item.ArticleID = jsonObject.getLong("ArticleID");
                item.Content = jsonObject.getString("Content");
                item.CreatedDate = jsonObject.getString("CreatedDate");
                item.Grade = jsonObject.getString("Grade");
                item.Subject = jsonObject.getString("Subject");
                item.UserID = jsonObject.getLong("UserID");
                item.Avatar = jsonObject.getString("Avatar");
                item.Fullname = jsonObject.getString("Fullname");
                item.Gender = jsonObject.getString("Gender");
                item.Position = jsonObject.getString("Position");
                item.Distance = jsonObject.getString("Distance");
                item.Rate = jsonObject.getString("Rate");

                result.add(item);
            }


        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e("TAG", ex.getMessage());
        }

        return result;
    }

    public static ModelArticle toModelObject(String obj) {
        try {
            JSONObject jsonObject = new JSONObject(obj);

            ModelArticle item = new ModelArticle();
            item.ArticleID = jsonObject.getLong("ArticleID");
            item.UserID = jsonObject.getLong("UserID");
            item.CreatedDate = jsonObject.getString("CreatedDate");
            item.Content = jsonObject.getString("Content");
            item.Grade = jsonObject.getString("Grade");
            item.Subject = jsonObject.getString("Subject");

            return item;

        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e("TAG", ex.getMessage());
        }
        return null;
    }

    //Convert object to json (just convert needed param to lower text)
    public static String toJson(ModelArticle item) {
        try {
            JSONObject object = new JSONObject();

            object.put("Page", item.Page);
            object.put("Sort", item.Sort);
            object.put("Latitude", item.Latitude);
            object.put("Longitude", item.Longitude);

            object.put("Gender", item.Gender);
            object.put("Position", item.Position);
            object.put("Grade", item.Grade);
            object.put("Subject", item.Subject);

            object.put("UserID", item.UserID);
            object.put("Content", item.Content);

            return object.toString();

        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(Page);
        parcel.writeInt(Sort);
        parcel.writeDouble(Latitude);
        parcel.writeDouble(Longitude);
        parcel.writeLong(ArticleID);
        parcel.writeString(Content);
        parcel.writeString(CreatedDate);
        parcel.writeString(Grade);
        parcel.writeString(Subject);
        parcel.writeLong(UserID);
        parcel.writeString(Avatar);
        parcel.writeString(Fullname);
        parcel.writeString(Gender);
        parcel.writeString(Position);
        parcel.writeString(Distance);
        parcel.writeString(Rate);
    }

    public static final Creator<ModelArticle> CREATOR = new Creator<ModelArticle>() {
        @Override
        public ModelArticle createFromParcel(Parcel parcel) {
            return new ModelArticle(parcel);
        }

        @Override
        public ModelArticle[] newArray(int i) {
            return new ModelArticle[i];
        }
    };
}
