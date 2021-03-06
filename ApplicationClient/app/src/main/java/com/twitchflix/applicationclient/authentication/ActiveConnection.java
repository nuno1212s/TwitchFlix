package com.twitchflix.applicationclient.authentication;

import com.twitchflix.applicationclient.ClientApp;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class ActiveConnection {

    private UUID owner;

    private long createdTime, validFor;

    private String accessToken;

    private ActiveConnection() {}

    public ActiveConnection(UUID owner, long createdTime, long validFor, String accessToken) {

        this.owner = owner;
        this.createdTime = createdTime;
        this.validFor = validFor;
        this.accessToken = accessToken;

    }

    public UUID getOwner() {
        return owner;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public long getValidFor() {
        return validFor;
    }

    public boolean hasExpired() {

        return createdTime + validFor < System.currentTimeMillis();

    }

    public boolean refreshConnection() {

        ActiveConnection activeConnection = ClientApp.getIns().getAuthRequests().refreshActiveConnection(this);

        if (activeConnection != null) {

            this.createdTime = activeConnection.createdTime;
            this.validFor = activeConnection.validFor;
            this.accessToken = activeConnection.accessToken;

            return true;
        }

        return false;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public JSONObject toJSONObject() {
        return toJSONObject(false);
    }

    public JSONObject toJSONObject(boolean ignoreExpiration) {

        if (hasExpired() && !ignoreExpiration) {
            refreshConnection();
        }

        JSONObject json = new JSONObject();

        try {
            json.put("userID", owner.toString());
            json.put("accessToken", getAccessToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }


}
