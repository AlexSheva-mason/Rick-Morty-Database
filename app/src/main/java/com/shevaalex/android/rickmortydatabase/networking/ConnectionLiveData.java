package com.shevaalex.android.rickmortydatabase.networking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.lifecycle.LiveData;

public class ConnectionLiveData extends LiveData<ConnectionModel> {
    private Context context;
    private ConnectivityManager connectivityManager;
    private static final int WIFI_DATA = 101;
    private static final int MOBILE_DATA = 1;

    public ConnectionLiveData(Context context) {
        this.context = context;
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    protected void onActive() {
        super.onActive();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(networkReceiver, filter);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        context.unregisterReceiver(networkReceiver);
    }

    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
            if(isConnected) {
                switch (activeNetwork.getType()){
                    case ConnectivityManager.TYPE_WIFI:
                        postValue(new ConnectionModel(WIFI_DATA,true));
                        break;
                    case ConnectivityManager.TYPE_MOBILE:
                        postValue(new ConnectionModel(MOBILE_DATA,true));
                        break;
                }
            } else {
                postValue(new ConnectionModel(0,false));
            }
        }
    };
}
