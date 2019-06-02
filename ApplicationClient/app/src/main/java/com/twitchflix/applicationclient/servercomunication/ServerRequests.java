package com.twitchflix.applicationclient.servercomunication;

import com.twitchflix.applicationclient.authentication.ActiveConnection;
import com.twitchflix.applicationclient.rest.models.UserVideo;
import com.twitchflix.applicationclient.rest.models.Video;
import com.twitchflix.applicationclient.rest.models.VideoStream;

import java.util.List;
import java.util.UUID;

public interface ServerRequests {

    List<UserVideo> getLandingPage(ActiveConnection connection);

    List<UserVideo> searchVideo(String text, ActiveConnection connection);

    List<UserVideo> getVideosByUser(UUID userID);

    Video getVideoByID(UUID videoID);

    void addView(UUID videoID, ActiveConnection connection);

    VideoStream requestVideoStream(String title, String description, ActiveConnection connection);

}
