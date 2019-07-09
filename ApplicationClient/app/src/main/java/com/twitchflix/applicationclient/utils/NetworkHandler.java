package com.twitchflix.applicationclient.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;

public class NetworkHandler {

    private Context context;

    public NetworkHandler(Context context) {

        this.context = context;

    }

    private Context getContext() {
        return context;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // Create background thread to connect and get data
        return networkInfo != null && networkInfo.isConnected();
    }

    public boolean isWifiAvailable() {

        ConnectivityManager connMgr = (ConnectivityManager)
                getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        Network[] allNetworks = connMgr.getAllNetworks();

        for (Network network : allNetworks) {

            NetworkCapabilities capabilities = connMgr.getNetworkCapabilities(network);

            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true;
            }

        }

        return false;
    }

}
