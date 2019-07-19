package com.twitchflix.videohandler;

import com.twitchflix.App;
import com.twitchflix.authentication.User;
import com.twitchflix.rest.models.*;
import com.twitchflix.searchengine.SearchEngine;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Path("videos")
public class VideoRestHandler {

    @POST
    @Path("search")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response search(SearchModel model) {

        SearchEngine videoSearchEngine = App.getVideoSearchEngine();

        if (App.getAuthenticationHandler().isValid(model.getUserID(), model.getAccessToken())) {

            User user = App.getUserDatabase().getAccountInformation(model.getUserID());

            List<Video> videos = videoSearchEngine.searchVideoByTitle(model.getSearch(), user);

            List<UserVideo> userVideos = instantiateVideos(videos, user);

            return Response.ok().entity(userVideos).build();
        } else {

            return Response.status(400).entity("Access Token is not valid").build();

        }
    }

    @GET
    @Path("getVideoByID")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVideoByID(@HeaderParam("videoID") String videoID) {

        Video videoById = App.getVideoDatabase().getVideoByID(UUID.fromString(videoID));

        if (videoById == null) {
            return Response.status(404).build();
        }

        return Response.ok().entity(videoById).build();
    }

    @POST
    @Path("mainPage")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response mainPage(LoginModel model) {

        SearchEngine engine = App.getVideoSearchEngine();

        if (App.getAuthenticationHandler().isValid(model.getUserID(), model.getAccessToken())) {

            User user = App.getUserDatabase().getAccountInformation(model.getUserID());

            List<Video> videos = engine.getFeed(user);

            List<UserVideo> instantiatedVideos = instantiateVideos(videos, user);

            return Response.ok().entity(instantiatedVideos).build();

        } else {

            return Response.status(400).entity("Access token is not valid").build();

        }

    }

    @POST
    @Path("view")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response acceptView(AcceptView view) {

        if (App.getAuthenticationHandler().isValid(view.getUserID(), view.getAccessToken())) {

            User user = App.getUserDatabase().getAccountInformation(view.getUserID());

            if (user.addWatchedVideo(view.getVideoID())) {

                Video vid = App.getVideoDatabase().getVideoByID(view.getVideoID());

                vid.addView();

                return Response.ok().build();
            }
        }

        return Response.status(400).build();
    }

    @POST
    @Path("stream")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response requestStreamLink(RequestStream streamRequest) {

        if (App.getAuthenticationHandler().isValid(streamRequest.getUserID(), streamRequest.getAccessToken())) {

            UUID videoID = UUID.randomUUID();

            VideoStream video = new VideoBuilder()
                    .setVideoID(videoID)
                    .setUploader(streamRequest.getUserID())
                    .setTitle(streamRequest.getTitle())
                    .setDescription(streamRequest.getDescription())
                    .setUploadDate(System.currentTimeMillis())
                    .setLive(false)
                    .setLink("https://" + App.SERVER_IP + "/watch/hls/" + videoID.toString() + ".m3u8")
                    .setThumbnailLink("https://" + App.SERVER_IP + "/images/" + videoID.toString() + ".jpg")
                    .setStreamLink("rtmp://" + App.SERVER_IP + "/show/" + videoID.toString())
                    .createVideoStream();

            App.getAsync().submit(() -> App.getVideoDatabase().registerVideoStream(video));

            return Response.ok().entity(video).build();
        }

        return Response.status(400).entity("Wrong access token").build();
    }

    @GET
    @Path("start")
    @Produces(MediaType.TEXT_PLAIN)
    public Response handleStreamStart(@QueryParam("streamid") String streamId) {

        Video videoByID = App.getVideoDatabase().getVideoByID(UUID.fromString(streamId));

        if (videoByID == null) {
            return Response.status(404).build();
        }

        videoByID.setLive(true);

        User uploader = App.getUserDatabase().getAccountInformation(videoByID.getUploader());

        uploader.addUploadedVideo(videoByID.getVideoID());

        App.getAsync().submit(() -> App.getVideoDatabase().updateVideo(videoByID));

        return Response.ok().entity("SUCCESS").build();
    }

    @GET
    @Path("endstream")
    @Produces(MediaType.TEXT_PLAIN)
    public Response handleStreamEnd(@QueryParam("streamid") String streamId) {

        Video videoByID = App.getVideoDatabase().getVideoByID(UUID.fromString(streamId));

        if (videoByID == null) {

            return Response.status(404).build();

        }

        videoByID.setTranscoding(false);

        videoByID.setLink("https://" + App.SERVER_IP + "/recordings/" + streamId + ".mp4");

        App.getAsync().submit(() -> App.getVideoDatabase().updateVideo(videoByID));

        return Response.ok()
                .entity("SUCCESS")
                .build();
    }

    @GET
    @Path("processing")
    @Produces(MediaType.TEXT_PLAIN)
    public Response handleProcessingStart(@QueryParam("streamid") String streamId) {

        Video videoByID = App.getVideoDatabase().getVideoByID(UUID.fromString(streamId));

        if (videoByID == null) {
            return Response.status(404).build();
        }

        videoByID.setLive(false);

        videoByID.setTranscoding(true);

        App.getAsync().submit(() -> App.getVideoDatabase().updateVideo(videoByID));

        return Response.ok().entity("SUCCESS")
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("byUploader")
    public Response getVideosByUploader(@HeaderParam("user") String userID) {

        List<Video> byUploader = App.getVideoDatabase().getVideosWithUploader(UUID.fromString(userID));

        if (byUploader == null) {
            return Response.status(404).build();
        }

        return Response.ok().entity(byUploader).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("editVideo")
    public Response editVideo(EditVideoModel editVideo) {

        if (App.getAuthenticationHandler().isValid(editVideo.getUserID(), editVideo.getAccessToken())) {

            Video videoByID = App.getVideoDatabase().getVideoByID(editVideo.getVideoID());

            if (videoByID == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            videoByID.setTitle(editVideo.getTitle());

            videoByID.setDescription(editVideo.getDescription());

            App.getAsync().submit(() ->
                    App.getVideoDatabase().editVideo(videoByID));

            return Response.ok().build();
        }

        return Response.status(Response.Status.FORBIDDEN).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("deleteVideo")
    public Response deleteVideo(DeleteVideoModel deleteVideo) {

        if (App.getAuthenticationHandler().isValid(deleteVideo.getUserID(), deleteVideo.getAccessToken())) {

            App.getAsync().submit(() -> {
                App.getVideoDatabase().deleteVideo(deleteVideo.getVideoID());

                String dataFolder = App.DATA_FOLDER + App.VIDEO_FOLDER + File.separator,
                        videoThumbnailsPath = App.DATA_FOLDER + App.VIDEO_THUMBNAIL + File.separator;

                File videoFolder = new File(dataFolder),
                        videoThumbnails = new File(videoThumbnailsPath);

                File[] files = videoFolder.listFiles((file, s) -> s.startsWith(deleteVideo.getVideoID().toString()));

                for (File file : files) {
                    file.delete();
                }

                files = videoThumbnails.listFiles((file, name) -> name.startsWith(deleteVideo.getVideoID().toString()));

                for (File file : files) {
                    file.delete();
                }

            });

            return Response.ok().build();
        }

        return Response.status(Response.Status.FORBIDDEN).build();
    }

    private List<UserVideo> instantiateVideos(List<Video> videos, User user) {

        List<UserVideo> instantiatedVideos = new ArrayList<>(videos.size());

        videos.forEach((video) -> instantiatedVideos.add(new UserVideo(video, user)));

        return instantiatedVideos;
    }

}
