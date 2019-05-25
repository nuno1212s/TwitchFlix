package com.twitchflix.rest.models;

import com.twitchflix.App;
import com.twitchflix.videohandler.Video;

import java.util.UUID;

public class VideoStream extends Video {

    private String streamLink;

    public VideoStream(UUID videoID, UUID uploader,
                       String title, String description,
                       long uploadDate, int likes, int views,
                       boolean live, String link, String thumbnailLink) {
        super(videoID, uploader, title, description, uploadDate, likes, views, live, link, thumbnailLink);
    }

    public void setStreamLink(String streamLink) {
        this.streamLink = streamLink;
    }

    public String getStreamLink() {
        return streamLink.replace("%SERVER_IP%", App.SERVER_IP);
    }

}
