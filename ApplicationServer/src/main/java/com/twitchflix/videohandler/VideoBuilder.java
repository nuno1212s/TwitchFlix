package com.twitchflix.videohandler;

import com.twitchflix.rest.models.VideoStream;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class VideoBuilder {
    private UUID uploader;
    private String title;
    private String description;
    private boolean live;
    private String link;
    private String thumbnailLink;
    private String streamLink;
    private UUID videoID;
    private long uploadDate;
    private int likes;
    private int views;

    public VideoBuilder setUploader(UUID uploader) {
        this.uploader = uploader;
        return this;
    }

    public VideoBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public VideoBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public VideoBuilder setLive(boolean live) {
        this.live = live;
        return this;
    }

    public VideoBuilder setLink(String link) {
        this.link = link;
        return this;
    }

    public VideoBuilder setThumbnailLink(String thumbnailLink) {
        this.thumbnailLink = thumbnailLink;
        return this;
    }

    public VideoBuilder setVideoID(UUID videoID) {
        this.videoID = videoID;
        return this;
    }

    public VideoBuilder setUploadDate(long uploadDate) {
        this.uploadDate = uploadDate;
        return this;
    }

    public VideoBuilder setLikes(int likes) {
        this.likes = likes;
        return this;
    }

    public VideoBuilder setViews(int views) {
        this.views = views;
        return this;
    }

    public VideoBuilder setStreamLink(String streamLink) {
        this.streamLink = streamLink;
        return this;
    }

    public VideoBuilder fromResultSet(ResultSet set) throws SQLException {

        setUploader(UUID.fromString(set.getString("UPLOADER")));

        setTitle(set.getString("TITLE"));
        setDescription(set.getString("DESCRIPTION"));
        setLive(set.getBoolean("LIVE"));
        setLink(set.getString("LINK"));
        setThumbnailLink(set.getString("THUMBLINK"));
        setUploadDate(set.getDate("UPLOADDATE").toInstant().toEpochMilli());
        setLikes(set.getInt("LIKES"));
        setViews(set.getInt("VIEWS"));

        return this;
    }

    public Video createVideo() {
        return new Video(videoID, uploader, title, description, uploadDate,
                likes, views, live, link, thumbnailLink);
    }

    public VideoStream createVideoStream() {
        VideoStream videoStream = new VideoStream(videoID, uploader, title, description, uploadDate, likes, views, live, link, thumbnailLink);

        videoStream.setStreamLink(this.streamLink);

        return videoStream;
    }
}