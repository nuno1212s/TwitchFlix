package com.twitchflix.applicationclient.rest.models;

import com.twitchflix.applicationclient.ClientApp;
import com.twitchflix.applicationclient.authentication.ActiveConnection;

import java.util.UUID;

public class EditVideo {

    private UUID userID;

    private String accessToken;

    private UUID videoID;

    private String title, description;

    private EditVideo(UUID userID, String accessToken, UUID videoID, String title, String description) {

        this.userID = userID;
        this.accessToken = accessToken;
        this.videoID = videoID;
        this.title = title;
        this.description = description;

    }

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

    public UUID getVideoID() {
        return videoID;
    }

    public void setVideoID(UUID videoID) {
        this.videoID = videoID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static EditVideo fromData(UUID videoID, String title, String description) {
        ActiveConnection currentActiveConnection = ClientApp.getIns().getLoginHandler().getCurrentActiveConnection();

        return new EditVideo(currentActiveConnection.getOwner(), currentActiveConnection.getAccessToken(), videoID, title, description);
    }

}
