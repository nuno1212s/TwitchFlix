package com.twitchflix.applicationclient.authentication;

import com.twitchflix.applicationclient.ClientApp;
import com.twitchflix.applicationclient.datastorage.UserLogin;
import com.twitchflix.applicationclient.rest.models.UserData;

public class LoginHandler {

    private ActiveConnection currentActiveConnection;

    private UserData currentUserData;

    public LoginHandler() { }

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

                System.out.println(activeConnection.getOwner().toString());

                UserData currentUserData = ClientApp.getIns().getUserDataRequests().requestUserData(this.currentActiveConnection);

                System.out.println(currentUserData);

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

    public boolean attempLogin(String googleIdToken) {

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

            System.out.println(activeConnection.getOwner());

            UserData currentUserData = ClientApp.getIns().getUserDataRequests().requestUserData(activeConnection);

            System.out.println(currentUserData);

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

        setCurrentActiveConnection(null);
        setCurrentUserData(null);

        ClientApp.getIns().getInformationStorage().deleteUserLogin();
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
