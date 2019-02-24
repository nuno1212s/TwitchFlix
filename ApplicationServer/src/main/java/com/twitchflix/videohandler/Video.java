package com.twitchflix.videohandler;

import com.twitchflix.App;

import java.util.UUID;

public class Video {

    private UUID videoID;

    private UUID uploader;

    private String title, description;

    //Upload date does not handle timezones
    private long uploadDate;

    private int likes, views;

    private boolean live;

    private String link, thumbnailLink;

    public Video(UUID uploader, String title, String description, boolean live,
                 String link, String thumbnailLink) {

        this.videoID = UUID.randomUUID();
        this.uploader = uploader;
        this.title = title;
        this.description = description;
        this.live = live;
        this.link = link;
        this.thumbnailLink = thumbnailLink;
        this.likes = 0;
        this.views = 0;
        this.uploadDate = System.currentTimeMillis();

    }

    public Video(UUID videoID, UUID uploader, String title, String description, long uploadDate,
                 int likes, int views, boolean live, String link, String thumbnailLink) {

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

    public boolean isLive() {
        return live;
    }

    public String getLink() {
        return link;
    }

    public String getThumbnailLink() {
        return thumbnailLink;
    }

    public int getLikes() {
        return likes;
    }

    public int getViews() {
        return views;
    }

    public void addView() {
        this.views++;

        App.getVideoDatabase().incrementVideoViews(this.getVideoID());
    }
}
