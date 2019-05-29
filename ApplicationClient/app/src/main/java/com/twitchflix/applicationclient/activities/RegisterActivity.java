package com.twitchflix.applicationclient.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import com.twitchflix.applicationclient.R;
import com.twitchflix.applicationclient.ClientApp;
import com.twitchflix.applicationclient.landingpage.LandingPage;
import com.twitchflix.applicationclient.utils.NetworkUser;
import com.twitchflix.applicationclient.utils.Utils;

import java.lang.ref.WeakReference;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private static final Pattern emailPattern = Pattern.compile("^.+@.+\\..+");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        findViewById(R.id.attemptregister).setOnClickListener(this::onClickRegister);
    }

    public void onClickRegister(View view) {

        LinearLayout layout = findViewById(R.id.register_screen);

        Utils.removeViewsFrom(layout, R.id.email_not_valid, R.id.account_already_exists);

        EditText firstName = findViewById(R.id.inputfirstname),
                lastName = findViewById(R.id.inputlastname),
                email = findViewById(R.id.inputemail),
                password = findViewById(R.id.inputpassword);

        if (!emailPattern.matcher(email.getText()).find()) {
            Utils.addErrorText(this, layout, R.id.email_not_valid, R.string.email_not_valid);
            return;
        }

        new AttemptRegisterAccount(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                firstName.getText().toString(),
                lastName.getText().toString(),
                email.getText().toString(),
                password.getText().toString());

    }

    private static class AttemptRegisterAccount extends NetworkUser<String, Void, Boolean> {

        private WeakReference<LinearLayout> layout;

        private WeakReference<ProgressBar> bar;

        public AttemptRegisterAccount(RegisterActivity activity) {
            super(activity);

            this.layout = new WeakReference<>(activity.findViewById(R.id.register_screen));

            this.bar = new WeakReference<>(new ProgressBar(activity));

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            LinearLayout layout = this.layout.get();

            ProgressBar progressBar = this.bar.get();

            if (progressBar != null && layout != null) {
                progressBar.setPadding(0, 50, 0, 0);

                layout.addView(progressBar);
            }

        }

        @Override
        protected Boolean doInBackground(String... strings) {

            if (!isInternetConnectionAvailable()) {
                return false;
            }

            String firstName = strings[0],
                    lastName = strings[1],
                    email = strings[2],
                    password = strings[3];

            return ClientApp.getIns().getLoginHandler().registerAccount(email, firstName, lastName, password);
        }

        @Override
        protected void onPostExecute(Boolean successful) {

            LinearLayout layout = this.layout.get();
            ProgressBar bar = this.bar.get();

            if (isContextPresent() && layout != null && bar != null) {
                layout.removeView(bar);

                Activity activity = getContextIfPresent();

                if (successful) {

                    sendToActivity(LandingPage.class);

                    finishActivity();

                } else {
                    Utils.addErrorText(activity, layout, R.id.account_already_exists, R.string.account_already_exists);
                }
            }
        }
    }

}
