package com.twitchflix.databases;

import com.twitchflix.videohandler.Video;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public interface VideoDatabase {

    Video getVideoByID(UUID videoID);

    List<Video> getVideosByID(HashSet<UUID> videoID);

    List<Video> getAllVideos();

    List<Video> getVideosSortedByUploadDate();

    void incrementVideoViews(UUID videoID);


}
