package com.twitchflix.applicationclient.authentication;

import com.twitchflix.applicationclient.ClientApp;

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

    public UUID getOwner() {
        return owner;
    }

    public boolean hasExpired() {

        return createdTime + validFor < System.currentTimeMillis();

    }

    public void refreshConnection() {

        ActiveConnection activeConnection = ClientApp.getIns().getAuthRequests().refreshActiveConnection(this);

        this.createdTime = activeConnection.createdTime;
        this.validFor = activeConnection.validFor;
        this.accessToken = activeConnection.accessToken;

    }

    public byte[] getAccessToken() {
        return accessToken;
    }


}
