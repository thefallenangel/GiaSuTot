package com.indcgroup.giasutot.user;

import android.net.ConnectivityManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.indcgroup.adapter.SuperAdapter;
import com.indcgroup.giasutot.R;
import com.indcgroup.model.ModelTransaction;
import com.indcgroup.model.MyResponse;
import com.indcgroup.loadresult.ApiCommunication;
import com.indcgroup.loadresult.ApiResponse;
import com.indcgroup.utility.Constants;
import com.indcgroup.utility.GLOBAL;
import com.indcgroup.utility.Utilities;

import java.util.ArrayList;


public class UserTransactionActivity extends AppCompatActivity implements ApiResponse {

    Utilities utl = new Utilities();
    ConnectivityManager conmng;

    AdView adView;
    TextView txtPoint;
    ListView lstTransaction;
    FloatingActionButton fabCharge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_transaction);

        adView = (AdView) findViewById(R.id.adView);
        txtPoint = (TextView) findViewById(R.id.txtPoint);
        lstTransaction = (ListView) findViewById(R.id.lstTransaction);
        fabCharge = (FloatingActionButton) findViewById(R.id.fabCharge);

        adView.loadAd(utl.createAdRequest());

        conmng = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        //Check internet connection
        if (!utl.checkInternetConnection(conmng)) {
            Toast.makeText(getApplication(), Constants.Error_NoInternetConnection, Toast.LENGTH_SHORT).show();
            return;
        }

        //Execute
        ApiCommunication comm = new ApiCommunication(UserTransactionActivity.this, Constants.Alert_DownloadTransaction, "GET");
        comm.delegate = UserTransactionActivity.this;

        String value = utl.base64Encode(String.valueOf(GLOBAL.USER.UserID));
        String url = String.format(getString(R.string.GetUserTransaction), value);
        comm.execute(url);

        fabCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(UserTransactionActivity.this, "Not available at this moment!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    void showTransaction(ArrayList<ModelTransaction> list) {
        txtPoint.setText(String.valueOf(GLOBAL.POINT));
        lstTransaction.setAdapter(new SuperAdapter(SuperAdapter.TYPE_TRANSACTION, this, list));
    }

    @Override
    public void onFinishCommunication(MyResponse result) {
        if (result.ResponseCode.equals("01")) {
            ArrayList<ModelTransaction> list = ModelTransaction.toModelList(result.ResponseMessage);

            int point = 0;
            for (ModelTransaction item : list) {
                point += item.Point;
            }
            GLOBAL.POINT = point;

            showTransaction(list);
        } else {
            Toast.makeText(UserTransactionActivity.this, result.ResponseMessage, Toast.LENGTH_SHORT).show();
        }
    }
}
