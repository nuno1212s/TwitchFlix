package com.twitchflix.rest.models;

import com.twitchflix.authentication.User;
import com.twitchflix.videohandler.Video;

import java.util.UUID;

public class UserVideo extends Video {

    private boolean watched, liked, owner;

    public UserVideo(Video videoObject, User user) {
        super(videoObject.getVideoID(), videoObject.getUploader(), videoObject.getTitle(),
                videoObject.getDescription(), videoObject.getUploadDate(), videoObject.getLikes(),
                videoObject.getViews(), videoObject.isLive());

        UUID videoID = videoObject.getVideoID();

        this.liked = user.hasLikedVideo(videoID);
        this.watched = user.hasWatchedVideo(videoID);
        this.owner = user.hasUploadedVideo(videoID);

    }

    public boolean isWatched() {
        return watched;
    }

    public boolean isLiked() {
        return liked;
    }

    public boolean isOwner() {
        return owner;
    }
}
