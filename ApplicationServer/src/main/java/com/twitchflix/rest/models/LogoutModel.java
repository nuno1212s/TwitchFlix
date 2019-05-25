package com.twitchflix.rest.models;

import java.util.UUID;

public class LogoutModel {

    private UUID userID;

    private String accessToken;

    public UUID getUserID() {
        return userID;
    }

    public void setUserID(UUID userID) {
        this.userID = userID;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
