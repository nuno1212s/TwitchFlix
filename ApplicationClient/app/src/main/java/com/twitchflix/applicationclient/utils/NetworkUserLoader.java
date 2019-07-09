package com.twitchflix.applicationclient.utils;

import android.app.AlertDialog;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.loader.content.AsyncTaskLoader;
import com.twitchflix.applicationclient.ClientApp;

public abstract class NetworkUserLoader<D> extends AsyncTaskLoader<D> {

    public NetworkUserLoader(@NonNull Context context) {
        super(context);
    }

    public boolean isNetworkAvailable() {
        return ClientApp.getIns().getNetworkHandler().isNetworkAvailable();
    }

    public boolean isWifi() {
        return ClientApp.getIns().getNetworkHandler().isWifiAvailable();
    }

    protected void showInternetNotAvailable() {

            new AlertDialog.Builder(getContext())
                    .setTitle("Internet connection not available")
                    .setMessage("Internet is necessary to run this app")
                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.ok, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

    }

}
