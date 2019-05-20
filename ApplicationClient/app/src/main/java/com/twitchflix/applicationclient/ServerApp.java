package com.twitchflix.applicationclient;

import com.twitchflix.applicationclient.authentication.ActiveConnection;
import com.twitchflix.applicationclient.authentication.AuthRequests;
import com.twitchflix.applicationclient.datastorage.InformationStorage;
import com.twitchflix.applicationclient.userdata.UserData;
import com.twitchflix.applicationclient.userdata.UserDataRequests;

public class ServerApp {

    private static ServerApp ins;

    public synchronized static ServerApp getIns() {

        if (ins == null) {
            ins = new ServerApp();
        }

        return ins;
    }

    private InformationStorage informationStorage;

    private AuthRequests authRequests;

    private UserDataRequests userDataRequests;

    private ActiveConnection currentActiveAccount;

    private UserData userData;

    private ServerApp() {
//        this.informationStorage = new FileStorage();

    }

    public UserDataRequests getUserDataRequests() {
        return userDataRequests;
    }

    public InformationStorage getInformationStorage() {
        return informationStorage;
    }
    public AuthRequests getAuthRequests() {
        return authRequests;
    }

    public ActiveConnection getCurrentActiveAccount() {
        return currentActiveAccount;
    }

    public UserData getUserData() {
        return userData;
    }

    public void setCurrentActiveAccount(ActiveConnection connection) {
        this.currentActiveAccount = connection;
    }


    public void setInformationStorage(InformationStorage storage) {
        this.informationStorage = storage;
    }

    public void setUserData(UserData loggedInUserData) {
        this.userData = loggedInUserData;
    }
}