package com.twitchflix.rest.models;

import java.util.UUID;

public class AcceptView extends LoginModel {

    private UUID videoID;

    public UUID getVideoID() {
        return videoID;
    }

    public void setVideoID(UUID videoID) {
        this.videoID = videoID;
    }

}
