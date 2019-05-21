package com.twitchflix.applicationclient.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import com.twitchflix.applicationclient.R;
import com.twitchflix.applicationclient.ClientApp;
import com.twitchflix.applicationclient.authentication.PasswordHandler;
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

        String salt = PasswordHandler.getSalt(10);

        String hashedPassword = PasswordHandler.hashPassword(password.getText().toString(), salt);

        new AttemptRegisterAccount(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                firstName.getText().toString(),
                lastName.getText().toString(),
                 email.getText().toString(),
                hashedPassword,
                salt);

    }

    private static class AttemptRegisterAccount extends AsyncTask<String, Void, Boolean> {

        private WeakReference<RegisterActivity> context;

        private WeakReference<LinearLayout> layout;

        private WeakReference<ProgressBar> bar;

        public AttemptRegisterAccount(RegisterActivity activity) {

            this.context = new WeakReference<>(activity);

            this.layout = new WeakReference<>(activity.findViewById(R.id.register_screen));

            this.bar = new WeakReference<>(new ProgressBar(activity));

        }

        @Override
        protected void onPreExecute() {
            LinearLayout layout = this.layout.get();

            ProgressBar progressBar = this.bar.get();

            if (progressBar != null && layout != null) {
                progressBar.setPadding(0, 50, 0, 0);

                layout.addView(progressBar);
            }

        }

        @Override
        protected Boolean doInBackground(String... strings) {

            String firstName = strings[0],
                    lastName = strings[1],
                    email = strings[2],
                    hashed_password = strings[3],
                    salt = strings[4];

            if (ClientApp.getIns().getAuthRequests().accountExistsWithEmail(email)) {
                return false;
            }

            ClientApp.getIns().getAuthRequests().registerAccount(email, firstName, lastName, hashed_password, salt);

            return true;
        }

        @Override
        protected void onPostExecute(Boolean successful) {

            LinearLayout layout = this.layout.get();

            RegisterActivity activity = this.context.get();
            ProgressBar bar = this.bar.get();

            if (layout != null && activity != null && bar != null) {
                layout.removeView(bar);

                if (successful) {

                    Intent intent = new Intent(activity, LandingPage.class);

                    activity.startActivity(intent);

                    activity.finish();

                } else {
                    Utils.addErrorText(activity, layout, R.id.account_already_exists, R.string.account_already_exists);
                }
            }
        }
    }

}
