package com.twitchflix.applicationclient.authentication;

import android.content.Context;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.twitchflix.applicationclient.ClientApp;
import com.twitchflix.applicationclient.R;
import com.twitchflix.applicationclient.datastorage.UserLogin;
import com.twitchflix.applicationclient.rest.models.UserData;

public class LoginHandler {

    private ActiveConnection currentActiveConnection;

    private UserData currentUserData;

    private GoogleSignInClient googleAccount;

    /**
     * Creates the google sign in client with the application context
     *
     * @param context The context
     */
    public void mainActivityCreate(Context context) {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestIdToken(context.getString(R.string.server_client_id))
                .build();

        googleAccount = GoogleSignIn.getClient(context, gso);

    }

    /**
     * Check if the user is signed in
     *
     * @param context The application context
     * @return
     */
    public GoogleSignInAccount checkIfSignedInGoogle(Context context) {
        return GoogleSignIn.getLastSignedInAccount(context);
    }

    public boolean attemptRelogin() {

        if (!ClientApp.getIns().getInformationStorage().isUserLoggedIn()) {
            return false;
        }

        UserLogin currentLogin = ClientApp.getIns().getInformationStorage().getCurrentLogin();

        ActiveConnection activeConnection = currentLogin.toActiveConnection();

        if (activeConnection.refreshConnection()) {
            setCurrentActiveConnection(activeConnection);
            setCurrentUserData(ClientApp.getIns().getUserDataRequests().requestUserData(getCurrentActiveConnection()));

            return true;
        }

        ClientApp.getIns().getInformationStorage().deleteUserLogin();

        return false;
    }

    public boolean attemptLogin(String email, String unhashedPassword) {

        UserData userData = ClientApp.getIns().getUserDataRequests().requestUserData(email);

        if (userData != null) {

            String salt = userData.getSalt();

            String hashedPassword = PasswordHandler.hashPassword(unhashedPassword, salt);

            ActiveConnection activeConnection = ClientApp.getIns().getAuthRequests().requestConnection(email, hashedPassword);

            if (activeConnection != null) {
                setCurrentActiveConnection(activeConnection);

                UserData currentUserData = ClientApp.getIns().getUserDataRequests().requestUserData(this.currentActiveConnection);

                setCurrentUserData(currentUserData);

                UserLogin userLogin = new UserLogin.UserLoginBuilder()
                        .fromUserDataAndToken(activeConnection, getCurrentUserData())
                        .setToken(hashedPassword)
                        .createUserLogin();

                ClientApp.getIns().getInformationStorage().setUserLogin(userLogin);

                return true;
            }

            return false;
        } else {
            return false;
        }

    }

    public boolean attemptLogin(String googleIdToken) {

        ActiveConnection activeConnection = ClientApp.getIns().getAuthRequests().requestConnection(googleIdToken);

        if (activeConnection != null) {

            setCurrentActiveConnection(activeConnection);

            setCurrentUserData(ClientApp.getIns().getUserDataRequests().requestUserData(activeConnection));

            UserLogin userLogin = new UserLogin.UserLoginBuilder()
                    .fromUserDataAndToken(activeConnection, getCurrentUserData())
                    .setToken(googleIdToken)
                    .createUserLogin();

            ClientApp.getIns().getInformationStorage().setUserLogin(userLogin);

            return true;

        }

        return false;
    }

    public boolean registerAccount(String email, String firstName, String lastName, String unhashedPassword) {

        String salt = PasswordHandler.getSalt(10);

        String hashedPassword = PasswordHandler.hashPassword(unhashedPassword, salt);

        ActiveConnection activeConnection = ClientApp.getIns().getAuthRequests().registerAccount(email, firstName, lastName, hashedPassword, salt);

        if (activeConnection != null) {

            setCurrentActiveConnection(activeConnection);

            UserData currentUserData = ClientApp.getIns().getUserDataRequests().requestUserData(activeConnection);

            setCurrentUserData(currentUserData);

            UserLogin userLogin = new UserLogin.UserLoginBuilder()
                    .fromUserDataAndToken(activeConnection, getCurrentUserData())
                    .setToken(hashedPassword)
                    .createUserLogin();

            ClientApp.getIns().getInformationStorage().setUserLogin(userLogin);

            return true;

        }

        return false;
    }

    public void logOut() {

        ClientApp.getIns().getAuthRequests().destroyConnection(getCurrentActiveConnection());

        if (googleAccount != null) {
            logOutOfGoogle();
        }

        setCurrentActiveConnection(null);
        setCurrentUserData(null);

        ClientApp.getIns().getInformationStorage().deleteUserLogin();
    }

    public void logOutOfGoogle() {
        googleAccount.signOut();
    }

    public GoogleSignInClient getGoogleClient() {
        return googleAccount;
    }

    public void setCurrentGoogleLogin(GoogleSignInClient client) {
        this.googleAccount = client;
    }

    public ActiveConnection getCurrentActiveConnection() {
        return currentActiveConnection;
    }

    private void setCurrentActiveConnection(ActiveConnection currentActiveConnection) {
        this.currentActiveConnection = currentActiveConnection;
    }

    public UserData getCurrentUserData() {
        return currentUserData;
    }

    private void setCurrentUserData(UserData currentUserData) {
        this.currentUserData = currentUserData;
    }
}
