package com.twitchflix.videohandler;

import com.twitchflix.App;
import com.twitchflix.authentication.User;
import com.twitchflix.authentication.accounts.ActiveConnection;
import com.twitchflix.rest.models.UserVideo;
import com.twitchflix.util.Pair;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Path("videos")
public class VideoRestHandler {

    @GET
    @Path("search")
    @Produces(MediaType.APPLICATION_JSON)
    public Pair<List<UserVideo>, ActiveConnection> search(@HeaderParam("videoName") String videoName,
                                                          @HeaderParam("UUID") String userID,
                                                          @HeaderParam("accessToken") String accessToken) {

        SearchEngine videoSearchEngine = App.getVideoSearchEngine();

        UUID userUUID = UUID.fromString(userID);

        if (App.getAuthenticationHandler().isValid(userUUID, Base64.getDecoder().decode(accessToken))) {

            User user = App.getUserDatabase().getAccountInformation(userUUID);

            List<Video> videos = videoSearchEngine.searchVideoByTitle(videoName);

            List<UserVideo> userVideos = instantiateVideos(videos, user);

            return new Pair<>(userVideos, App.getAuthenticationHandler().getActiveConnection(userUUID)
                    .refreshToken());

        } else {

            throw new WebApplicationException("Access Token is not valid");

        }
    }

    @GET
    @Path("mainPage")
    @Produces(MediaType.APPLICATION_JSON)
    public Pair<List<UserVideo>, ActiveConnection> mainPage(@HeaderParam("UUID") String userID,
                                                            @HeaderParam("accessToken") String accessToken) {

        SearchEngine engine = App.getVideoSearchEngine();

        UUID userID2 = UUID.fromString(userID);

        if (App.getAuthenticationHandler().isValid(userID2, Base64.getDecoder().decode(accessToken))) {

            User user = App.getUserDatabase().getAccountInformation(userID2);

            List<Video> videos = engine.getFeed();

            List<UserVideo> instantiatedVideos = instantiateVideos(videos, user);

            return new Pair<>(instantiatedVideos, App.getAuthenticationHandler().getActiveConnection(userID2).refreshToken());

        } else {

            throw new WebApplicationException("Access token is not valid");

        }

    }

    @POST
    @Path("view")
    @Consumes(MediaType.APPLICATION_JSON)
    public void acceptView(String userID, String AccessToken, String videoID) {

        UUID userID2 = UUID.fromString(userID);

        if (App.getAuthenticationHandler().isValid(userID2, Base64.getDecoder().decode(AccessToken))) {

            User user = App.getUserDatabase().getAccountInformation(userID2);

            UUID videoID2 = UUID.fromString(videoID);

            user.addWatchedVideo(videoID2);

            Video vid = App.getVideoDatabase().getVideoByID(videoID2);

            vid.addView();

        }

    }

    private List<UserVideo> instantiateVideos(List<Video> videos, User user) {

        List<UserVideo> instantiatedVideos = new ArrayList<>(videos.size());

        videos.forEach((video) -> instantiatedVideos.add(new UserVideo(video, user)));

        return instantiatedVideos;
    }

}
