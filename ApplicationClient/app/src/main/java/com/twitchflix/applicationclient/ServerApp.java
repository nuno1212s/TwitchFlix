package com.twitchflix.applicationclient;

import com.twitchflix.applicationclient.authentication.AuthRequests;
import com.twitchflix.applicationclient.datastorage.InformationStorage;

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

    private ServerApp() {

    }

    public InformationStorage getInformationStorage() {
        return informationStorage;
    }

    public AuthRequests getAuthRequests() {
        return authRequests;
    }
}
