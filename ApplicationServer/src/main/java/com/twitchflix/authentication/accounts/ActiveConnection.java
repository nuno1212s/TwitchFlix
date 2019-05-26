package com.twitchflix.authentication.accounts;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.UUID;

public class ActiveConnection {

    private UUID owner;

    private long createdTime, validFor;

    private String accessToken;

    public ActiveConnection(UUID owner, long validFor) {
        this.owner = owner;
        this.validFor = validFor;

        this.createdTime = System.currentTimeMillis();

        this.accessToken =  new String(new BigInteger(256, new SecureRandom()).toByteArray(),
                StandardCharsets.UTF_8);
    }

    public ActiveConnection refreshToken() {

        this.accessToken =  new String(new BigInteger(256, new SecureRandom()).toByteArray(),
                StandardCharsets.UTF_8);

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
        return getCreatedTime() + getValidFor() > System.currentTimeMillis();
    }

    public long getValidFor() {
        return validFor;
    }

    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public String toString() {
        return String.format("{Owner: %s, AccessToken: %s}", owner.toString(), getAccessToken());
    }
}
