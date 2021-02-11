package com.example.addabazi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.addabazi.common.Util;

public class MessagesActivity extends AppCompatActivity {
    private Button btnRetry,btnClose;
    private TextView tv_checkInternet;
    private View progressbar;
    private ConnectivityManager.NetworkCallback networkCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        btnClose = findViewById(R.id.btn_close_MessagesActivity_ID);
        btnRetry = findViewById(R.id.btn_RetrySendingResetEmail_ID);
        tv_checkInternet=findViewById(R.id.tv_checkInternet_ID);
        progressbar = findViewById(R.id.pb_MessagingActivity_ID);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            networkCallback = new ConnectivityManager.NetworkCallback(){
                @Override
                public void onAvailable(@NonNull Network network) {
                    super.onAvailable(network);
                    finish();
                }


                @Override
                public void onLost(@NonNull Network network) {
                    super.onLost(network);

                    tv_checkInternet.setText(getString(R.string.checkInternetConnection));
                }
            };

            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            connectivityManager.registerNetworkCallback(new NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build(),networkCallback);

        }

    }







    public void btnRetryClicked(View view){
        tv_checkInternet.setVisibility(View.GONE);
        progressbar.setVisibility(View.VISIBLE);
        if(Util.connectionAvailable(this)){
            //tv_checkInternet.setVisibility(View.VISIBLE);
            //progressbar.setVisibility(View.GONE);
            finish();
        }

        else {
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                        tv_checkInternet.setVisibility(View.VISIBLE);
                        progressbar.setVisibility(View.GONE);
                }
            },1000);
        }
    }






    public void btnCloseClicked(View view){
        finishAffinity();
    }
}