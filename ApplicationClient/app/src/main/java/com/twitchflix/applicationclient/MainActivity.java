package com.twitchflix.applicationclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import com.twitchflix.applicationclient.activities.LoginActivity;
import com.twitchflix.applicationclient.datastorage.FileStorage;
import com.twitchflix.applicationclient.datastorage.InformationStorage;
import com.twitchflix.applicationclient.datastorage.UserLogin;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("1");

        Intent intent = new Intent(this, LoginActivity.class);

        startActivity(intent);

    }
}
