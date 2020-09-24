package com.twitchflix.applicationclient.rest.models;

import com.twitchflix.applicationclient.utils.VideoLinkCreator;

import java.util.UUID;

public class Video {

    private UUID videoID, uploader;

    private String title, description;

    //Upload date does not handle timezones
    private long uploadDate;

    private int likes, views;

    private boolean live;

    protected Video() {}

    public UUID getVideoID() {
        return videoID;
    }

    public UUID getUploader() {
        return uploader;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public long getUploadDate() {
        return uploadDate;
    }

    public int getLikes() {
        return likes;
    }

    public int getViews() {
        return views;
    }

    public boolean isLive() {
        return live;
    }

    public String getLink() {
        return VideoLinkCreator.createVideoURL(getVideoID().toString(), isLive());
    }

    public String getThumbnailLink() {
        return VideoLinkCreator.createThumbnailLink(getVideoID().toString());
    }

    public void setVideoID(UUID videoID) {
        this.videoID = videoID;
    }

    public void setUploader(UUID uploader) {
        this.uploader = uploader;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUploadDate(long uploadDate) {
        this.uploadDate = uploadDate;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public void setLive(boolean live) {
        this.live = live;
    }
}
