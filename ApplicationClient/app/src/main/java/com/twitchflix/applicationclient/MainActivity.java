package com.twitchflix.applicationclient;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.twitchflix.applicationclient.authentication.ActiveConnection;
import com.twitchflix.applicationclient.landingpage.LandingPage;
import com.twitchflix.applicationclient.activities.LoginActivity;
import com.twitchflix.applicationclient.datastorage.FileStorage;
import com.twitchflix.applicationclient.datastorage.UserLogin;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ClientApp.getIns().setInformationStorage(new FileStorage(this));

        if (!ClientApp.getIns().getInformationStorage().isUserLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);

            startActivity(intent);
        } else {
            new LoadUserLogin(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private static class LoadUserLogin extends AsyncTask<String, Void, Boolean> {

        private WeakReference<MainActivity> mainActivity;

        public LoadUserLogin(MainActivity activity) {
            this.mainActivity = new WeakReference<>(activity);
        }

        @Override
        protected Boolean doInBackground(String... strings) {

            UserLogin currentLogin = ClientApp.getIns().getInformationStorage().getCurrentLogin();

            ActiveConnection activeConnection = currentLogin.toActiveConnection();

            if (activeConnection.refreshConnection()) {
                ClientApp.getIns().setCurrentActiveAccount(activeConnection);
                ClientApp.getIns().setUserData(ClientApp.getIns().getUserDataRequests().requestUserData(activeConnection));

                return true;
            }

            ClientApp.getIns().getInformationStorage().deleteUserLogin();

            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {

            MainActivity mainActivity = this.mainActivity.get();

            if (mainActivity != null) {

                if (success) {
                    Intent intent = new Intent(mainActivity, LandingPage.class);

                    mainActivity.startActivity(intent);
                } else {

                    Intent intent = new Intent(mainActivity, LoginActivity.class);

                    mainActivity.startActivity(intent);

                }
            }
        }
    }
}
