package com.twitchflix.applicationclient.rest.models;

public class VideoStream extends Video {

    private String streamLink;

    protected VideoStream() {
        super();
    }

    public void setStreamLink(String streamLink) {
        this.streamLink = streamLink;
    }

    public String getStreamLink() {
        return streamLink;
    }
}
