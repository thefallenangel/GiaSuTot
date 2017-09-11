package com.indcgroup.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.indcgroup.giasutot.R;
import com.indcgroup.loadimage.ImageCommunication;
import com.indcgroup.model.ModelArticle;
import com.indcgroup.model.ModelRecruitment;
import com.indcgroup.model.ModelTransaction;
import com.indcgroup.utility.Utilities;

import java.util.ArrayList;

/**
 * Created by thefa on 10/08/2017.
 */

public class SuperAdapter extends BaseAdapter {

    public static final String TYPE_SIMPLE = "simple";
    public static final String TYPE_ARTICLE = "article";
    public static final String TYPE_RECRUITMENT = "recruitment";
    public static final String TYPE_TRANSACTION = "transaction";

    public Utilities utl = new Utilities();
    public ArrayList<? extends Object> mList;
    public String mType;
    public Context mContext;

    public SuperAdapter(String type, Context context, ArrayList<? extends Object> list) {
        this.mList = list;
        this.mType = type;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (mType.equals(TYPE_ARTICLE)) {
            if (view == null)
                view = LayoutInflater.from(mContext).inflate(R.layout.adapter_article, viewGroup, false);

            ImageView imgAvatar = view.findViewById(R.id.imgAvatar);
            TextView txtFullname = view.findViewById(R.id.txtFullname);
            TextView txtRate = view.findViewById(R.id.txtRate);
            TextView txtPosition = view.findViewById(R.id.txtPosition);
            TextView txtFilter = view.findViewById(R.id.txtFiltered);
            TextView txtContent = view.findViewById(R.id.txtReviewContent);

            ModelArticle item = (ModelArticle) mList.get(i);

            loadAvatar(imgAvatar, item.UserID);
            txtFullname.setText(item.Fullname);
            txtRate.setText(createRatingText(item.Rate));
            txtPosition.setText(item.Position + " - " + item.Gender);
            txtContent.setText(item.Content);
            txtFilter.setText(item.CreatedDate + " - " + item.Distance);

        } else if (mType.equals(TYPE_RECRUITMENT)) {
            if (view == null)
                view = LayoutInflater.from(mContext).inflate(R.layout.adapter_recruitment, viewGroup, false);

            TextView txtCreatedDate = view.findViewById(R.id.txtCreatedDate);
            TextView txtGrade = view.findViewById(R.id.txtGrade);
            TextView txtSubject = view.findViewById(R.id.txtSubject);
            TextView txtAddress = view.findViewById(R.id.txtAddress);
            TextView txtPhone = view.findViewById(R.id.txtPhone);
            TextView txtContent = view.findViewById(R.id.txtContent);

            ModelRecruitment item = (ModelRecruitment) mList.get(i);

            txtCreatedDate.setText("Ngày đăng: " + item.CreatedDate);
            txtGrade.setText("Lớp: " + item.Grade);
            txtSubject.setText("Môn: " + item.Subject);
            txtAddress.setText("Địa chỉ: " + item.Address);
            txtPhone.setText("Số điện thoại: " + item.Phone);
            txtContent.setText(item.Content);

        } else if (mType.equals(TYPE_TRANSACTION)) {
            if (view == null)
                view = LayoutInflater.from(mContext).inflate(R.layout.adapter_transaction, viewGroup, false);

            TextView txtCreatedDate = view.findViewById(R.id.txtCreatedDate);
            TextView txtPoint = view.findViewById(R.id.txtPoint);
            TextView txtDescription = view.findViewById(R.id.txtDescription);

            ModelTransaction item = (ModelTransaction) mList.get(i);

            txtCreatedDate.setText("Thời gian: " + item.CreatedDate);
            txtDescription.setText(item.Description);
            if (item.Type) {
                txtPoint.setTextColor(mContext.getResources().getColor(R.color.colorSuccessText));
                txtPoint.setText("+" + item.Point);
            } else {
                txtPoint.setTextColor(mContext.getResources().getColor(R.color.colorErrorButton));
                txtPoint.setText(String.valueOf(item.Point));
            }

        } else if (mType.equals(TYPE_SIMPLE)) {
            if (view == null)
                view = LayoutInflater.from(mContext).inflate(R.layout.adapter_simple_text, viewGroup, false);

            TextView txtItem = view.findViewById(R.id.txtItem);

            String item = (String) mList.get(i);

            txtItem.setText(item);

        }
        return view;
    }

    private void loadAvatar(ImageView img, long userID) {
        String url = String.format(mContext.getString(R.string.LoadAvatar), String.valueOf(userID));
        new ImageCommunication(img).execute(url);
    }

    private SpannableStringBuilder createRatingText(String text) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(text).append("   ");
        builder.setSpan(new ImageSpan(mContext, R.drawable.ic_star_border), builder.length() - 1, builder.length(), 0);

        return builder;
    }
}
