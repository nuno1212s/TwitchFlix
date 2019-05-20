package com.twitchflix.applicationclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import com.twitchflix.applicationclient.activities.LandingPage;
import com.twitchflix.applicationclient.activities.LoginActivity;
import com.twitchflix.applicationclient.datastorage.FileStorage;
import com.twitchflix.applicationclient.datastorage.InformationStorage;
import com.twitchflix.applicationclient.datastorage.UserLogin;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ServerApp.getIns().setInformationStorage(new FileStorage(this));

        if (!ServerApp.getIns().getInformationStorage().isUserLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);

            startActivity(intent);
        } else {

            Intent intent = new Intent(this, LandingPage.class);


        }


    }
}
