package com.twitchflix.applicationclient.servercomunication;

import java.util.UUID;

public class Video {

    private UUID videoID, uploader;

    private String title, description;

    //Upload date does not handle timezones
    private long uploadDate;

    private int likes, views;

    private boolean live;

    private String link, thumbnailLink;

    private boolean watched, like, owner;

    protected Video(UUID videoID, UUID uploader, String title, String description, long uploadDate,
                    int likes, int views, boolean live, String link, String thumbnailLink,
                    boolean watched, boolean like, boolean owner) {

        this.videoID = videoID;
        this.uploader = uploader;
        this.title = title;
        this.description = description;
        this.uploadDate = uploadDate;
        this.likes = likes;
        this.views = views;
        this.live = live;
        this.link = link;
        this.thumbnailLink = thumbnailLink;
        this.owner = owner;
        this.like = like;
        this.watched = watched;

    }

    public boolean isWatched() {
        return watched;
    }

    public boolean isLike() {
        return like;
    }

    public boolean isOwner() {
        return owner;
    }

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
        return link;
    }

    public String getThumbnailLink() {
        return thumbnailLink;
    }
}
