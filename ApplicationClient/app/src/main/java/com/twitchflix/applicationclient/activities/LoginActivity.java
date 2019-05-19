package com.twitchflix.applicationclient.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.twitchflix.applicationclient.R;

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

    public void onClickLogin(View view) {
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

    private void handleSignIn(GoogleSignInAccount account) {



    }

    private class AttempToLogin extends AsyncTask<String, Void, Boolean> {



        @Override
        protected Boolean doInBackground(String... strings) {
            return null;
        }
    }
}
