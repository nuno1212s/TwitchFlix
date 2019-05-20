package com.twitchflix.applicationclient.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.twitchflix.applicationclient.R;
import com.twitchflix.applicationclient.ServerApp;
import com.twitchflix.applicationclient.authentication.ActiveConnection;
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
    public void onBackPressed() {
        //Do nothing on back pressed
    }

    @Override
    protected void onStart() {
        super.onStart();

        GoogleSignInAccount lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(this);

        if (lastSignedInAccount != null) {
            handleSignIn(lastSignedInAccount);
        }

    }

    public void onClickLogin(View view) {
        EditText email = findViewById(R.id.login_email),
                password = findViewById(R.id.login_password);



    }

    public void onLoginGoogle(View view) {

        Intent signInIntent = signInClient.getSignInIntent();

        startActivityForResult(signInIntent, RC_SIGN_IN);

    }


    public void onClickRegister(View view) {
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

    private class AttempToLogin extends AsyncTask<String, Void, Boolean> {

        private WeakReference<LoginActivity> loginActivity;

        public AttempToLogin(LoginActivity activity) {
            this.loginActivity = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {

            LoginActivity loginActivity = this.loginActivity.get();

            if (loginActivity != null) {

                Utils.addProgressBar(loginActivity, loginActivity.findViewById(R.id.login_activity), R.id.login_progress_bar);

            }

        }

        @Override
        protected Boolean doInBackground(String... strings) {
            String email = strings[0];

            return null;
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
                LinearLayout layout = loginActivity.findViewById(R.id.login_activity);

                Utils.addProgressBar(loginActivity, layout, R.id.login_progress_bar);
            }

        }

        @Override
        protected Boolean doInBackground(String... strings) {

            String idToken = strings[0];

            ActiveConnection activeConnection = ServerApp.getIns().getAuthRequests().requestConnection(idToken);

            if (activeConnection != null) {
                ServerApp.getIns().setCurrentActiveAccount(activeConnection);

                ServerApp.getIns().setUserData(ServerApp.getIns().getUserDataRequests().requestUserData(activeConnection));
            }

            return activeConnection != null;
        }

        @Override
        protected void onPostExecute(Boolean successfull) {

            LoginActivity loginActivity = this.loginActivity.get();

            if (loginActivity != null) {

                LinearLayout layout = loginActivity.findViewById(R.id.login_activity);

                Utils.removeViewsFrom(layout, R.id.login_progress_bar);

                if (successfull) {

                    Intent intent = new Intent(loginActivity, LandingPageN.class);

                    loginActivity.startActivity(intent);

                } else {

                    Utils.addErrorText(loginActivity, layout, R.id.login_not_valid, R.string.login_not_valid);

                }

            }

        }
    }
}
