package com.nunoneto.videohandler;

import java.util.List;

public interface SearchEngine {

    List<Video> searchVideoByTitle(String title);

    List<Video> getLatestVideos();

}
