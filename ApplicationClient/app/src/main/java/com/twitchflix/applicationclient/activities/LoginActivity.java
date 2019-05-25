package com.twitchflix.applicationclient.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.twitchflix.applicationclient.R;
import com.twitchflix.applicationclient.ClientApp;
import com.twitchflix.applicationclient.authentication.ActiveConnection;
import com.twitchflix.applicationclient.authentication.PasswordHandler;
import com.twitchflix.applicationclient.landingpage.LandingPage;
import com.twitchflix.applicationclient.rest.models.UserData;
import com.twitchflix.applicationclient.utils.Utils;

import java.lang.ref.WeakReference;

public class LoginActivity extends AppCompatActivity {

    private GoogleSignInClient signInClient;

    private static final short RC_SIGN_IN = 6002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.server_client_id))
                .requestProfile()
                .build();

        signInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.sign_in_button).setOnClickListener(this::onLoginGoogle);
    }

    @Override
    protected void onStart() {
        super.onStart();

        GoogleSignInAccount lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(this);

        if (lastSignedInAccount != null) {
            handleSignIn(lastSignedInAccount);
        }

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

        Intent signInIntent = signInClient.getSignInIntent();

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

    private static class AttemptToLogin extends AsyncTask<String, Void, Boolean> {

        private WeakReference<LoginActivity> loginActivity;

        public AttemptToLogin(LoginActivity activity) {
            this.loginActivity = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {

            LoginActivity loginActivity = this.loginActivity.get();

            if (loginActivity != null) {

                loginActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                Utils.addProgressBar(loginActivity, loginActivity.findViewById(R.id.login_activity), R.id.login_progress_bar);

            }

        }

        @Override
        protected Boolean doInBackground(String... strings) {
            String email = strings[0], password = strings[1];

            UserData userData = ClientApp.getIns().getUserDataRequests().requestUserData(email);

            String hashedPassword = PasswordHandler.hashPassword(password, userData.getSalt());

            ActiveConnection activeConnection = ClientApp.getIns().getAuthRequests().requestConnection(email, hashedPassword);

            if (activeConnection != null) {
                ClientApp.getIns().setCurrentActiveAccount(activeConnection);
                ClientApp.getIns().setUserData(userData);
            }

            return activeConnection != null;
        }

        @Override
        protected void onPostExecute(Boolean successful) {

            LoginActivity activity = this.loginActivity.get();

            if (activity != null) {

                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                ViewGroup layout = activity.findViewById(R.id.login_activity);

                Utils.removeViewsFrom(layout, R.id.login_progress_bar);

                activity.handleUserSignIn(successful, layout);

            }

        }
    }

    private static class AttemptToLoginGoogleAuth extends AsyncTask<String, Void, Boolean> {

        private WeakReference<LoginActivity> loginActivity;

        public AttemptToLoginGoogleAuth(LoginActivity activity) {
            this.loginActivity = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {

            LoginActivity loginActivity = this.loginActivity.get();

            if (loginActivity != null) {

                loginActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                LinearLayout layout = loginActivity.findViewById(R.id.login_activity);

                Utils.addProgressBar(loginActivity, layout, R.id.login_progress_bar);
            }

        }

        @Override
        protected Boolean doInBackground(String... strings) {

            String idToken = strings[0];

            ActiveConnection activeConnection = ClientApp.getIns().getAuthRequests().requestConnection(idToken);

            if (activeConnection != null) {
                ClientApp.getIns().setCurrentActiveAccount(activeConnection);

                ClientApp.getIns().setUserData(ClientApp.getIns().getUserDataRequests().requestUserData(activeConnection));
            }

            return activeConnection != null;
        }

        @Override
        protected void onPostExecute(Boolean successful) {

            LoginActivity loginActivity = this.loginActivity.get();

            if (loginActivity != null) {

                loginActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                LinearLayout layout = loginActivity.findViewById(R.id.login_activity);

                Utils.removeViewsFrom(layout, R.id.login_progress_bar);

                loginActivity.handleUserSignIn(successful, layout);

            }

        }
    }
}
