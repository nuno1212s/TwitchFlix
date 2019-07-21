package com.twitchflix.applicationclient;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.twitchflix.applicationclient.activities.LoginActivity;
import com.twitchflix.applicationclient.landingpage.LandingPage;
import com.twitchflix.applicationclient.utils.loaders.NetworkUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleSignInAccount signedInAccount = ClientApp.getIns().getLoginHandler().checkIfSignedInGoogle(getApplicationContext());
        if (signedInAccount != null) {

            new LoadGoogleLogin(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                    signedInAccount.getIdToken());

            return;
        }

        if (!ClientApp.getIns().getInformationStorage().isUserLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);

            startActivity(intent);
            finish();
        } else {
            new LoadUserLogin(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private static class LoadGoogleLogin extends NetworkUser<String, Void, Boolean> {

        public LoadGoogleLogin(MainActivity mainActivity) {
            super(mainActivity);
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            if (!isInternetConnectionAvailable()) {
                return false;
            }
            return ClientApp.getIns().getLoginHandler().attemptLogin(strings[0]);
        }

        @Override
        protected void onPostExecute(Boolean successful) {
            Activity mainActivity = (Activity) getContextIfPresent();

            if (mainActivity != null) {
                if (successful) {

                    sendToActivity(LandingPage.class);

                    finishActivity();
                } else {

                    sendToActivity(LoginActivity.class);

                    finishActivity();
                }
            }
        }
    }

    private static class LoadUserLogin extends NetworkUser<String, Void, Boolean> {

        public LoadUserLogin(MainActivity activity) {
            super(activity);
        }

        @Override
        protected Boolean doInBackground(String... strings) {

            if (!isInternetConnectionAvailable()) {
                return false;
            }

            return ClientApp.getIns().getLoginHandler().attemptRelogin();

        }

        @Override
        protected void onPostExecute(Boolean success) {

            Activity mainActivity = (Activity) getContextIfPresent();

            if (mainActivity != null) {

                if (success) {
                    Intent intent = new Intent(mainActivity, LandingPage.class);

                    mainActivity.startActivity(intent);

                    mainActivity.finish();
                } else {

                    Intent intent = new Intent(mainActivity, LoginActivity.class);

                    mainActivity.startActivity(intent);

                    mainActivity.finish();
                }
            }
        }
    }
}
