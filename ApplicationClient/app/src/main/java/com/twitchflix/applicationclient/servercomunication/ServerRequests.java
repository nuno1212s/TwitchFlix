package com.twitchflix.applicationclient.servercomunication;

import com.twitchflix.applicationclient.authentication.ActiveConnection;

import java.util.List;
import java.util.UUID;

public interface ServerRequests {

    List<Video> getLandingPage(ActiveConnection connection);

    List<Video> searchVideo(String text, ActiveConnection connection);

    void addView(UUID videoID, ActiveConnection connection);

}
