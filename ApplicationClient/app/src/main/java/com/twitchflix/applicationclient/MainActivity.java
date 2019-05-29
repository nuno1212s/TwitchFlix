package com.twitchflix.applicationclient;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.twitchflix.applicationclient.activities.LoginActivity;
import com.twitchflix.applicationclient.landingpage.LandingPage;

import java.lang.ref.WeakReference;

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

    private static class LoadGoogleLogin extends AsyncTask<String, Void, Boolean> {

        private WeakReference<MainActivity> mainActivity;

        public LoadGoogleLogin(MainActivity mainActivity) {
            this.mainActivity = new WeakReference<>(mainActivity);
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            return ClientApp.getIns().getLoginHandler().attemptLogin(strings[0]);
        }

        @Override
        protected void onPostExecute(Boolean successful) {

            MainActivity mainActivity = this.mainActivity.get();

            if (mainActivity != null) {
                if (successful) {

                    Intent mainPage = new Intent(mainActivity, LandingPage.class);

                    mainActivity.startActivity(mainPage);

                    mainActivity.finish();
                } else {

                    Intent loginPage = new Intent(mainActivity, LoginActivity.class);

                    mainActivity.startActivity(loginPage);

                    mainActivity.finish();

                }
            }
        }
    }

    private static class LoadUserLogin extends AsyncTask<String, Void, Boolean> {

        private WeakReference<MainActivity> mainActivity;

        public LoadUserLogin(MainActivity activity) {
            this.mainActivity = new WeakReference<>(activity);
        }

        @Override
        protected Boolean doInBackground(String... strings) {

            return ClientApp.getIns().getLoginHandler().attemptRelogin();

        }

        @Override
        protected void onPostExecute(Boolean success) {

            MainActivity mainActivity = this.mainActivity.get();

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
