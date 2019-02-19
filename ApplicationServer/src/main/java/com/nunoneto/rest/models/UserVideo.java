package com.nunoneto.rest.models;

import com.nunoneto.authentication.User;
import com.nunoneto.videohandler.Video;

import java.util.UUID;

public class UserVideo extends Video {

    private boolean watched, liked, owner;

    public UserVideo(Video videoObject, User user) {
        super(videoObject.getVideoID(), videoObject.getUploader(), videoObject.getTitle(),
                videoObject.getDescription(), videoObject.getUploadDate(), videoObject.getLikes(),
                videoObject.getViews(), videoObject.isLive(),
                videoObject.getLink(), videoObject.getThumbnailLink());

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
