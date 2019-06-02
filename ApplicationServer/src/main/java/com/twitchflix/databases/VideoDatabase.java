package com.twitchflix.databases;

import com.twitchflix.videohandler.Video;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public interface VideoDatabase {

    Video getVideoByID(UUID videoID);

    List<Video> getVideosByID(HashSet<UUID> videoID);

    List<Video> getVideosWithUploader(UUID uploader);

    List<Video> getAllVideos();

    List<Video> getVideosSortedByUploadDate();

    void incrementVideoViews(UUID videoID);

    void registerVideoStream(Video video);

    void updateVideo(Video video);


}
