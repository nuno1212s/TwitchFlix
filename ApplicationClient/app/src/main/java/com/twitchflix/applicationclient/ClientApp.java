package com.twitchflix.applicationclient;

import com.twitchflix.applicationclient.authentication.ActiveConnection;
import com.twitchflix.applicationclient.authentication.AuthRequests;
import com.twitchflix.applicationclient.authentication.server.AuthServerConnection;
import com.twitchflix.applicationclient.datastorage.InformationStorage;
import com.twitchflix.applicationclient.servercomunication.ServerRequests;
import com.twitchflix.applicationclient.rest.models.UserData;
import com.twitchflix.applicationclient.userdata.UserDataRequests;
import com.twitchflix.applicationclient.userdata.server.UserDataServerConnection;
import okhttp3.OkHttpClient;

public class ClientApp {

    private static ClientApp ins;

    public synchronized static ClientApp getIns() {

        if (ins == null) {
            ins = new ClientApp();
        }

        return ins;
    }

    private InformationStorage informationStorage;

    private AuthRequests authRequests;

    private UserDataRequests userDataRequests;

    private ServerRequests serverRequests;

    private ActiveConnection currentActiveAccount;

    private UserData userData;

    private OkHttpClient client;

    private ClientApp() {
//        this.informationStorage = new FileStorage();
        this.client = new OkHttpClient();

        this.authRequests = new AuthServerConnection();
        this.userDataRequests = new UserDataServerConnection();
    }

    public UserDataRequests getUserDataRequests() {
        return userDataRequests;
    }

    public InformationStorage getInformationStorage() {
        return informationStorage;
    }

    public ServerRequests getServerRequests() {
        return serverRequests;
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

    public OkHttpClient getClient() {
        return this.client;
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
