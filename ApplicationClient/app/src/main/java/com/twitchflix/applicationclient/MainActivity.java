package com.twitchflix.applicationclient;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.twitchflix.applicationclient.activities.LoginActivity;
import com.twitchflix.applicationclient.datastorage.FileStorage;
import com.twitchflix.applicationclient.landingpage.LandingPage;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ClientApp.getIns().setInformationStorage(new FileStorage(this));

        ServerConnection.init(getApplication().getApplicationContext());

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

            return ClientApp.getIns().getLoginHandler().attemptRelogin();

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
