package com.nunoneto.databases;

import com.nunoneto.videohandler.Video;

import java.util.List;
import java.util.UUID;

public interface VideoDatabase {

    Video getVideoByID(UUID videoID);

    List<Video> getVideosByID(List<UUID> videoID);

    List<Video> getAllVideos();

    List<Video> getVideosSortedByUploadDate();


}
