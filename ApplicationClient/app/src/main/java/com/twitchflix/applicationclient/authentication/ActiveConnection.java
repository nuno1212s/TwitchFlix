package com.twitchflix.applicationclient.authentication;

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



}
