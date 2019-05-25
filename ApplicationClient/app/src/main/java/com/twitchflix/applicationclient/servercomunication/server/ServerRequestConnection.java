package com.twitchflix.applicationclient.servercomunication.server;

import com.google.gson.reflect.TypeToken;
import com.twitchflix.applicationclient.ServerConnection;
import com.twitchflix.applicationclient.authentication.ActiveConnection;
import com.twitchflix.applicationclient.rest.models.UserVideo;
import com.twitchflix.applicationclient.rest.models.Video;
import com.twitchflix.applicationclient.rest.models.VideoStream;
import com.twitchflix.applicationclient.servercomunication.ServerRequests;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;

public class ServerRequestConnection implements ServerRequests {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    public List<UserVideo> getLandingPage(ActiveConnection connection) {

        JSONObject jsonObject = connection.toJSONObject();

        Request request = new Request.Builder()
                .url(ServerConnection.getServerIp() + "videos/mainPage")
                .post(RequestBody.create(JSON, jsonObject.toString()))
                .build();

        return executeAndLoadVideos(request);
    }

    @Override
    public List<UserVideo> searchVideo(String text, ActiveConnection connection) {

        JSONObject jsonObject = connection.toJSONObject();

        Request request = new Request.Builder()
                .url(ServerConnection.getServerIp() + "videos/search")

        return null;
    }

    @Override
    public Video getVideoByID(UUID videoID) {
        return null;
    }

    @Override
    public void addView(UUID videoID, ActiveConnection connection) {

    }

    @Override
    public VideoStream requestVideoStream(String title, String description, ActiveConnection connection) {
        return null;
    }

    private List<UserVideo> executeAndLoadVideos(Request request) {
        try (Response response = ServerConnection.getIns().getClient().newCall(request).execute()) {

            if (response.code() == 200) {

                Type type = new TypeToken<List<UserVideo>>() {
                }.getType();

                return ServerConnection.getIns().getGson().fromJson(response.body().string(), type);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
