package com.twitchflix.applicationclient.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.twitchflix.applicationclient.R;
import com.twitchflix.applicationclient.ServerApp;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private static final char[] charSet = "123456789abcdefghijklmnopqrstuvyxwzABCDEFGHIJKLMNOPQRSTUVYXWZ".toCharArray();

    private static final Random random = new Random();

    private static final Pattern emailPattern = Pattern.compile("^.+@.+\\..+");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        findViewById(R.id.attempregister).setOnClickListener(this::onClickRegister);


    }

    public void onClickRegister(View view) {

        LinearLayout layout = findViewById(R.id.register_screen);

        View account_already_exists = findViewById(R.id.account_already_exists),
            wrong_email = findViewById(R.id.email_not_valid);

        if (account_already_exists != null) {
            layout.removeView(account_already_exists);
        }

        if (wrong_email != null) {
            layout.removeView(wrong_email);
        }

        EditText firstName = findViewById(R.id.inputfirstname),
                lastName = findViewById(R.id.inputlastname),
                email = findViewById(R.id.inputemail),
                password = findViewById(R.id.inputpassword);

        if (!emailPattern.matcher(email.getText()).find()) {
            addTextElementToEnd(layout, R.string.email_not_valid);
            return;
        }

        String salt = getRandomString(15);

        String hashedPassword = hashPassword(password.getText().toString(), salt);

        new AttemptRegisterAccount().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                firstName.getText().toString(),
                lastName.getText().toString(),
                email.getText().toString(),
                hashedPassword,
                salt);

    }

    private void addTextElementToEnd(ViewGroup l, int resId) {

        TextView emailNotValid = new TextView(this);

        emailNotValid.setId(R.id.email_not_valid);

        emailNotValid.setPadding(0, 10, 0 ,0);

        emailNotValid.setGravity(Gravity.CENTER);

        emailNotValid.setText(resId);

        emailNotValid.setTextColor(Color.RED);

        l.addView(emailNotValid);
    }

    private String getRandomString(int size) {

        StringBuilder builder = new StringBuilder("");

        for (int i = 0; i < size; i++) {

            builder.append(charSet[random.nextInt(charSet.length)]);

        }

        return builder.toString();
    }

    private String hashPassword(String password, String salt) {

        try {
            MessageDigest instance = MessageDigest.getInstance("SHA-256");

            return new String(instance.digest((password + salt).getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    private class AttemptRegisterAccount extends AsyncTask<String, Void, Boolean> {

        private LinearLayout layout;

        private ProgressBar bar;

        @Override
        protected void onPreExecute() {

            this.layout = findViewById(R.id.register_screen);

            this.bar = new ProgressBar(RegisterActivity.this);

            this.bar.setPadding(0, 50, 0, 0);

            layout.addView(this.bar);

        }

        @Override
        protected Boolean doInBackground(String... strings) {

            try {
                Thread.sleep(100000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String firstName = strings[0],
                    lastName = strings[1],
                    email = strings[2],
                    hashed_password = strings[3],
                    salt = strings[4];

            if (ServerApp.getIns().getAuthRequests().accountExistsWithEmail(email)) {
                return false;
            }

            ServerApp.getIns().getAuthRequests().registerAccount(email, firstName, lastName, hashed_password, salt);

            return true;
        }

        @Override
        protected void onPostExecute(Boolean successfull) {

            layout.removeView(bar);

            if (successfull) {

                Intent intent = new Intent(RegisterActivity.this, LandingPage.class);

                startActivity(intent);

            } else {
                addTextElementToEnd(layout, R.string.account_already_exists);
            }
        }
    }

}
