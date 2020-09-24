package com.twitchflix.applicationclient.rest.models;

import com.twitchflix.applicationclient.utils.VideoLinkCreator;

public class VideoStream extends Video {

    protected VideoStream() {
        super();
    }

    public String getStreamLink() {
        return VideoLinkCreator.createStreamURL(getVideoID().toString());
    }
}
