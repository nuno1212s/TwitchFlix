package com.twitchflix.applicationclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import com.twitchflix.applicationclient.activities.LoginActivity;
import com.twitchflix.applicationclient.datastorage.FileStorage;
import com.twitchflix.applicationclient.datastorage.InformationStorage;
import com.twitchflix.applicationclient.datastorage.UserLogin;

public class MainActivity extends AppCompatActivity {

    private InformationStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storage = new FileStorage(this);

        setTitle("1");

        if (storage.isUserLoggedIn()) {

            UserLogin currentLogin = storage.getCurrentLogin();



        } else {

            System.out.println("Hello?");

            TextView text = findViewById(R.id.main_page_text);

            Intent intent = new Intent(this, LoginActivity.class);

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, text, ViewCompat.getTransitionName(text));

            startActivity(intent, options.toBundle());

        }

    }
}
