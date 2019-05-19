package com.twitchflix.authentication.accounts;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

public class ActiveConnection {

    private UUID owner;

    private long createdTime, validFor;

    private byte[] accessToken;

    public ActiveConnection(UUID owner, long validFor) {
        this.owner = owner;
        this.validFor = validFor;

        this.createdTime = System.currentTimeMillis();

        this.accessToken = new BigInteger(256, new SecureRandom()).toByteArray();
    }

    public ActiveConnection refreshToken() {

        this.accessToken = new BigInteger(256, new SecureRandom()).toByteArray();

        this.createdTime = System.currentTimeMillis();

        return this;
    }

    public UUID getOwner() {
        return owner;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public boolean isValid() {
        return getCreatedTime() + getValidFor() <= System.currentTimeMillis();
    }

    public long getValidFor() {
        return validFor;
    }

    public byte[] getAccessTokenBytes() {
        return accessToken;
    }

    public String getAccessToken() {
        return Base64.getEncoder().encodeToString(accessToken);
    }
}
