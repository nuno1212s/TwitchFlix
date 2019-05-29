package com.twitchflix.searchengine;

import com.twitchflix.App;
import com.twitchflix.authentication.User;
import com.twitchflix.videohandler.Video;

import java.util.List;

public class TerribleSearchEngine implements SearchEngine{

    @Override
    public List<Video> searchVideoByTitle(String toLookFor, User userSearching) {
        return App.getVideoDatabase().getVideosSortedByUploadDate();
    }

    @Override
    public List<Video> getFeed(User toPresent) {

        return App.getVideoDatabase().getVideosSortedByUploadDate();
    }
}
