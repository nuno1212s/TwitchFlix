package com.twitchflix.databases.mysql;

import com.twitchflix.databases.VideoDatabase;
import com.twitchflix.videohandler.Video;

import java.util.List;
import java.util.UUID;

public class MySqlVideoDB extends MySQLDB implements VideoDatabase {

    public MySqlVideoDB() {
        super();
    }

    @Override
    public Video getVideoByID(UUID videoID) {
        return null;
    }

    @Override
    public List<Video> getVideosByID(List<UUID> videoID) {
        return null;
    }

    @Override
    public List<Video> getAllVideos() {
        return null;
    }

    @Override
    public List<Video> getVideosSortedByUploadDate() {
        return null;
    }

    @Override
    public void incrementVideoViews(UUID videoID) {

    }
}
