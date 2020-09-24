package com.twitchflix.rest.models;

import com.twitchflix.videohandler.Video;

import java.util.UUID;

public class VideoStream extends Video {

    public VideoStream(UUID videoID, UUID uploader,
                       String title, String description,
                       long uploadDate, int likes, int views,
                       boolean live) {
        super(videoID, uploader, title, description, uploadDate, likes, views, live);
    }

}
