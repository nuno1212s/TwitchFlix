package com.twitchflix.applicationclient.utils.daos;

import android.graphics.Bitmap;

import java.util.UUID;

public class VideoDAO {

    private UUID videoID;

    private String title, description;

    private Bitmap thumbnail;

    private UserDAO uploader;

    private VideoDAO(UUID videoID, UserDAO uploader, String title,
                     String description, Bitmap thumbnail) {
        this.videoID = videoID;
        this.uploader = uploader;
        this.title = title;
        this.description = description;
        this.thumbnail = thumbnail;
    }

    public static VideoDAO fromData(UUID videoID, UserDAO uploader, String title,
                                    String description, Bitmap thumbnail) {
        return new VideoDAO(videoID, uploader, title, description, thumbnail);
    }

    public UUID getVideoID() {
        return videoID;
    }

    public UserDAO getUploader() {
        return uploader;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUploaderName() {
        return getUploader().getFullName();
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public Bitmap getChannelThumbnail() {
        return this.getUploader().getChannelThumbnail();
    }
}