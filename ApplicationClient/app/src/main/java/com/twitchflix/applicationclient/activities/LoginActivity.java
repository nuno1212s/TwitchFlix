package com.twitchflix.applicationclient.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.twitchflix.applicationclient.ClientApp;
import com.twitchflix.applicationclient.R;
import com.twitchflix.applicationclient.landingpage.LandingPage;
import com.twitchflix.applicationclient.utils.NetworkUser;
import com.twitchflix.applicationclient.utils.Utils;

import java.lang.ref.WeakReference;

public class LoginActivity extends AppCompatActivity {

    private static final short RC_SIGN_IN = 6002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.sign_in_button).setOnClickListener(this::onLoginGoogle);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void removeErrorText() {

        Utils.removeViewsFrom(findViewById(R.id.login_activity), R.id.login_not_valid);

    }

    public void onClickLogin(View view) {
        removeErrorText();

        EditText email = findViewById(R.id.login_email),
                password = findViewById(R.id.login_password);

        new AttemptToLogin(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                email.getText().toString(), password.getText().toString());

    }

    public void onLoginGoogle(View view) {
        removeErrorText();

        Intent signInIntent = ClientApp.getIns().getLoginHandler().getGoogleClient().getSignInIntent();

        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    /**
     * Handle clicking the register button
     * Redirects to the register screen
     *
     * @param view
     */
    public void onClickRegister(View view) {
        removeErrorText();

        Intent register = new Intent(this, RegisterActivity.class);

        startActivity(register);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {

        try {
            GoogleSignInAccount result = task.getResult(ApiException.class);

            handleSignIn(result);

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            handleSignIn(null);
            e.printStackTrace();

        }

    }

    private void handleSignIn(@Nullable GoogleSignInAccount account) {

        if (account == null) {

            Utils.addErrorText(this, findViewById(R.id.login_activity), R.id.login_not_valid, R.string.login_not_valid);

        } else {
            new AttemptToLoginGoogleAuth(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                    account.getIdToken());
        }
    }

    private void handleUserSignIn(boolean successful, ViewGroup layout) {
        if (successful) {

            Intent intent = new Intent(this, LandingPage.class);

            startActivity(intent);

            finish();

        } else {

            Utils.addErrorText(this, layout, R.id.login_not_valid, R.string.login_not_valid);

        }

    }

    private static class AttemptToLogin extends NetworkUser<String, Void, Boolean> {

        public AttemptToLogin(LoginActivity activity) {
            super(activity);
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            if (isContextPresent()) {
                Activity loginActivity = getContextIfPresent();

                loginActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                Utils.addProgressBar(loginActivity, loginActivity.findViewById(R.id.login_activity), R.id.login_progress_bar);

            }

        }

        @Override
        protected Boolean doInBackground(String... strings) {
            if (!isInternetConnectionAvailable()) {
                return false;
            }
            String email = strings[0], password = strings[1];

            return ClientApp.getIns().getLoginHandler().attemptLogin(email, password);
        }

        @Override
        protected void onPostExecute(Boolean successful) {

            if (isContextPresent()) {
                Activity activity = getContextIfPresent();

                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                ViewGroup layout = activity.findViewById(R.id.login_activity);

                Utils.removeViewsFrom(layout, R.id.login_progress_bar);

                ((LoginActivity) activity).handleUserSignIn(successful, layout);

            }

        }
    }

    private static class AttemptToLoginGoogleAuth extends NetworkUser<String, Void, Boolean> {

        public AttemptToLoginGoogleAuth(LoginActivity activity) {
            super(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (isContextPresent()) {

                Activity loginActivity = getContextIfPresent();

                loginActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                LinearLayout layout = loginActivity.findViewById(R.id.login_activity);

                Utils.addProgressBar(loginActivity, layout, R.id.login_progress_bar);
            }

        }

        @Override
        protected Boolean doInBackground(String... strings) {

            if (!isInternetConnectionAvailable()) {
                return false;
            }

            String idToken = strings[0];

            return ClientApp.getIns().getLoginHandler().attemptLogin(idToken);
        }

        @Override
        protected void onPostExecute(Boolean successful) {

            if (isContextPresent()) {
                Activity loginActivity = getContextIfPresent();

                loginActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                LinearLayout layout = loginActivity.findViewById(R.id.login_activity);

                Utils.removeViewsFrom(layout, R.id.login_progress_bar);

                ((LoginActivity) loginActivity).handleUserSignIn(successful, layout);

            }

        }
    }
}
