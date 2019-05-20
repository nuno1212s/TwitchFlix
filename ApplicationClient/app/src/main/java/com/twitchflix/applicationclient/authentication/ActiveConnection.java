package com.twitchflix.applicationclient.authentication;

import com.twitchflix.applicationclient.ServerApp;

import java.util.UUID;

public class ActiveConnection {

    private UUID owner;

    private long createdTime, validFor;

    private byte[] accessToken;

    public ActiveConnection(UUID owner, long createdTime, long validFor, byte[] accessToken) {

        this.owner = owner;
        this.createdTime = createdTime;
        this.validFor = validFor;
        this.accessToken = accessToken;

    }

    public boolean hasExpired() {

        return createdTime + validFor < System.currentTimeMillis();

    }

    public void refreshConnection() {

        ServerApp.getIns().getAuthRequests().refreshActiveConnection(this);

    }

    public byte[] getAccessToken() {
        return accessToken;
    }


}
