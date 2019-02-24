package com.twitchflix.videohandler;

import java.util.List;

public interface SearchEngine {

    List<Video> searchVideoByTitle(String title);

    List<Video> getLatestVideos();

}
