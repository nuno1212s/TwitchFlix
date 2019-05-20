package com.twitchflix.applicationclient;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.twitchflix.applicationclient.activities.LandingPage;
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

        {
            Intent intent = new Intent(this, LandingPage.class);

            startActivity(intent);
        }

        if (!ClientApp.getIns().getInformationStorage().isUserLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);

            startActivity(intent);
        } else {

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



            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

            MainActivity mainActivity = this.mainActivity.get();

            if (mainActivity != null) {

                Intent intent = new Intent(mainActivity, LandingPage.class);

                mainActivity.startActivity(intent);
            }
        }
    }
}
