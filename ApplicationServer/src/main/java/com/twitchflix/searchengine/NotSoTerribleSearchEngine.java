package com.twitchflix.searchengine;

import com.twitchflix.App;
import com.twitchflix.authentication.User;
import com.twitchflix.videohandler.Video;

import java.util.ArrayList;
import java.util.List;

public class NotSoTerribleSearchEngine implements SearchEngine{

    public List<Video> searchVideoByTitle(String toLookFor, User userSearching) {

        List<Video> videos = App.getVideoDatabase().getAllVideos();
        List<Video> results = new ArrayList<>();

        for (Video v : videos) {
            if (v.getTitle().toLowerCase().contains(toLookFor.toLowerCase())) {
                results.add(v);
            }
        }

        return results;
    }

    @Override
    public List<Video> getFeed(User toPresent) {
        return App.getVideoDatabase().getVideosSortedByUploadDate();
    }
}
