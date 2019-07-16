package com.twitchflix.applicationclient.utils.loaders;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

public abstract class NetworkUser<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    private WeakReference<Context> context;

    public NetworkUser(Context context) {
        this.context = new WeakReference<>(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (!isInternetConnectionAvailable()) {
            showInternetNotAvailable();
        }
    }

    protected boolean isInternetConnectionAvailable() {

        Context context = this.context.get();

        if (context != null) {

            ConnectivityManager connMgr = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            // Create background thread to connect and get data
            return networkInfo != null && networkInfo.isConnected();
        }

        return false;
    }

    public void sendToActivity(Class<?> cls) {
        if (isContextPresent()) {
            Intent intent = new Intent(getContextIfPresent(), cls);

            getContextIfPresent().startActivity(intent);

        }

    }

    /**
     * This is no longer supported
     */
    @Deprecated
    public void finishActivity() {
        if (isContextPresent()) {
//            getContextIfPresent().finish();
        }
    }

    public Context getContextIfPresent() {
        return this.context.get();
    }

    public boolean isContextPresent() {
        return this.context.get() != null;
    }

    protected void showInternetNotAvailable() {

        if (isContextPresent())
            new AlertDialog.Builder(getContextIfPresent())
                    .setTitle("Internet connection not available")
                    .setMessage("Internet is necessary to run this app")
                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.ok, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

    }
}
