package com.twitchflix.applicationclient.activities;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import com.twitchflix.applicationclient.ClientApp;
import com.twitchflix.applicationclient.R;
import com.twitchflix.applicationclient.authentication.ActiveConnection;
import com.twitchflix.applicationclient.utils.loaders.NetworkUser;

public class AccountSettings extends AppCompatActivity {

    private EditText firstName, lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        firstName = findViewById(R.id.edit_text_first_name);

        lastName = findViewById(R.id.edit_text_last_name);
    }

    public void onClickUpdateAccount(View view) {
        String firstName = this.firstName.getText().toString(),
                lastName = this.lastName.getText().toString();

        new UpdateAccountSettings(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                firstName, lastName);

    }

    private static class UpdateAccountSettings extends NetworkUser<String, Void, Boolean> {

        public UpdateAccountSettings(Activity context) {
            super(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ProgressBar viewById = ((Activity) getContextIfPresent()).findViewById(R.id.progressbar_id);

            viewById.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(String... strings) {

            if (!isInternetConnectionAvailable()) {
                return false;
            }

            String firstName = strings[0], lastName = strings[1];

            ActiveConnection currentConnection = ClientApp.getIns().getLoginHandler().getCurrentActiveConnection();

            if (!firstName.isEmpty()) {
                ClientApp.getIns().getUserDataRequests().updateFirstName(currentConnection,
                        firstName);

                System.out.println(firstName);
            }

            if (!lastName.isEmpty()) {
                ClientApp.getIns().getUserDataRequests().updateLastName(currentConnection, lastName);

                System.out.println(lastName);
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean successful) {

            if (isContextPresent()) {
                ProgressBar viewById = ((Activity) getContextIfPresent()).findViewById(R.id.progressbar_id);

                viewById.setVisibility(View.GONE);

            }

        }
    }
}
