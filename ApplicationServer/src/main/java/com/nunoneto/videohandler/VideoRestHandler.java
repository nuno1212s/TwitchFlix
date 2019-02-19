package com.nunoneto.videohandler;

import com.nunoneto.App;
import com.nunoneto.authentication.User;
import com.nunoneto.authentication.accounts.ActiveConnection;
import com.nunoneto.rest.models.UserVideo;
import com.nunoneto.util.Pair;

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
                                                          @HeaderParam("AccessToken") String accessToken) {

        SearchEngine videoSearchEngine = App.getVideoSearchEngine();

        UUID userUUID = UUID.fromString(userID);

        if (App.getAuthenticationHandler().isValid(userUUID, Base64.getDecoder().decode(accessToken))) {

            User user = App.getUserDatabase().getAccountInformation(userUUID);

            List<Video> videos = videoSearchEngine.searchVideoByTitle(videoName);

            List<UserVideo> instantiatedVideos = new ArrayList<>(videos.size());

            videos.forEach((video) -> instantiatedVideos.add(new UserVideo(video, user)));

            return new Pair<>(instantiatedVideos, App.getAuthenticationHandler().getActiveConnection(userUUID)
                    .refreshToken());

        } else {

            throw new WebApplicationException("Access Token is not valid");

        }
    }

}
