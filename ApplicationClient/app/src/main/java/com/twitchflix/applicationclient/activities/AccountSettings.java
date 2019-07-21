package com.twitchflix.applicationclient.activities;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import androidx.lifecycle.ViewModelProviders;
import com.twitchflix.applicationclient.ClientApp;
import com.twitchflix.applicationclient.R;
import com.twitchflix.applicationclient.authentication.ActiveConnection;
import com.twitchflix.applicationclient.utils.loaders.NetworkUser;
import com.twitchflix.applicationclient.viewmodels.AccountSettingsViewModel;

public class AccountSettings extends AppCompatActivity {

    private ImageView channelThumbnail;

    private EditText firstName, lastName;

    private AccountSettingsViewModel settingsModel;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        channelThumbnail = findViewById(R.id.channelThumbnail);

        firstName = findViewById(R.id.edit_text_first_name);

        lastName = findViewById(R.id.edit_text_last_name);

        progressBar = findViewById(R.id.progressBar);

        settingsModel = ViewModelProviders.of(this).get(AccountSettingsViewModel.class);

        settingsModel.getChannelThumbnail().observe(this, (image) ->
                channelThumbnail.setImageBitmap(image));

        settingsModel.getFirstName().observe(this, (firstName) ->
                this.firstName.setHint(firstName));

        settingsModel.getLastName().observe(this, (lastName) ->
                this.lastName.setHint(lastName));

        settingsModel.getVisibility().observe(this, (visibility) ->
                progressBar.setVisibility(visibility));

        settingsModel.postUserID(ClientApp.getIns().getLoginHandler().getCurrentUserData().getUuid());

    }

    public void onClickUpdateAccount(View view) {
        String firstName = this.firstName.getText().toString(),
                lastName = this.lastName.getText().toString();


        settingsModel.updateAccount(firstName, lastName);
    }

    public void backButtonPressed(View view) {
        super.onBackPressed();
    }
}
