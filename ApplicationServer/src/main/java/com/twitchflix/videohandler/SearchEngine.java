package com.twitchflix.videohandler;

import com.twitchflix.authentication.User;

import java.util.List;

public interface SearchEngine {

    /**
     * Returns a list of videos that are adequate to the string that is given
     * @param toLookFor
     * @return
     */
    List<Video> searchVideoByTitle(String toLookFor, User userSearching);

    /**
     * Returns a list of videos to show in the app's first screen
     *
     * @param toPresent If given, search the videos for the user's preference
     * @return
     */
    List<Video> getFeed(User toPresent);

}
