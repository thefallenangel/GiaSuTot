package com.indcgroup.dialog;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.indcgroup.giasutot.R;

/**
 * Created by thefa on 05/08/2017.
 */

public class SuperDialog extends DialogFragment {

    public static final int DIALOG_TYPE_ERROR = 0;
    public static final int DIALOG_TYPE_SUCCESS = 1;
    public static final int DIALOG_TYPE_CONFIRM = 2;

    public boolean mRedirect = false;
    public int mType;
    public String mMessage;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRedirect = getArguments().getBoolean("Redirect");
        mType = getArguments().getInt("Type");
        mMessage = getArguments().getString("Message");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);

        if (mType == DIALOG_TYPE_ERROR || mType == DIALOG_TYPE_SUCCESS) {

            View rootView = inflater.inflate(R.layout.dialog_alert, container, false);

            TextView txtBody = rootView.findViewById(R.id.txtDialogBody);
            ImageView imgIcon = rootView.findViewById(R.id.imgDialogImage);
            Button btnConfirm = rootView.findViewById(R.id.btnDialogConfirm);

            if (mType == DIALOG_TYPE_ERROR) {
                getDialog().setTitle("Lỗi");
                imgIcon.setImageResource(R.mipmap.ic_error);
                txtBody.setText(mMessage);
                txtBody.setTextColor(getResources().getColor(R.color.colorErrorText));
                btnConfirm.setBackgroundColor(getResources().getColor(R.color.colorErrorButton));
            } else if (mType == DIALOG_TYPE_SUCCESS) {
                getDialog().setTitle("Thành công");
                imgIcon.setImageResource(R.mipmap.ic_success);
                txtBody.setText(mMessage);
                txtBody.setTextColor(getResources().getColor(R.color.colorSuccessText));
                btnConfirm.setBackgroundColor(getResources().getColor(R.color.colorSuccessButton));
            }

            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getDialog().dismiss();
                }
            });

            return rootView;
        } else if (mType == DIALOG_TYPE_CONFIRM) {
            View rootView = inflater.inflate(R.layout.dialog_confirm, container, false);

            TextView txtBody = rootView.findViewById(R.id.txtDialogBody);
            Button btnYes = rootView.findViewById(R.id.btnDialogYes);
            Button btnNo = rootView.findViewById(R.id.btnDialogNo);

            getDialog().setTitle("Chú ý");
            txtBody.setText(mMessage);

            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getDialog().dismiss();
                }
            });
            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Activity activity = getActivity();
                    if (activity instanceof SuperDialogConfirmListener) {
                        ((SuperDialogConfirmListener) activity).confirmButtonClicked();
                        getDialog().dismiss();
                    }
                }
            });

            return rootView;
        }
        return null;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        if (mRedirect) {
            Activity activity = getActivity();
            if (activity instanceof SuperDialogCloseListener) {
                ((SuperDialogCloseListener) activity).handleDialogClose(dialog);
            }
        }
    }
}
