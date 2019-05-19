package com.twitchflix.applicationclient.datastorage;

public interface InformationStorage {

    boolean isUserLoggedIn();

    void setUserLogin(UserLogin login);

    void deleteUserLogin();

    UserLogin getCurrentLogin();

}
