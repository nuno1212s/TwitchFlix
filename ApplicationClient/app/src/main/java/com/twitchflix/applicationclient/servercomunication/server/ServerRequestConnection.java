package com.twitchflix.applicationclient.servercomunication.server;

import com.google.gson.reflect.TypeToken;
import com.twitchflix.applicationclient.ServerConnection;
import com.twitchflix.applicationclient.authentication.ActiveConnection;
import com.twitchflix.applicationclient.rest.models.UserVideo;
import com.twitchflix.applicationclient.rest.models.Video;
import com.twitchflix.applicationclient.rest.models.VideoStream;
import com.twitchflix.applicationclient.servercomunication.ServerRequests;
import okhttp3.*;
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

        Request request = new Request.Builder()
                .url(ServerConnection.getServerIp() + "videos/search")
                .header("search", text)
                .post(RequestBody.create(JSON, connection.toJSONObject().toString()))
                .build();

        return executeAndLoadVideos(request);
    }

    @Override
    public Video getVideoByID(UUID videoID) {

        Request request = new Request.Builder()
                .url(ServerConnection.getServerIp() + "videos/getVideoByID")
                .header("videoID", videoID.toString())
                .get()
                .build();

        try (Response r = ServerConnection.getIns().getClient().newCall(request).execute()) {

            if (r.code() == 200)  {

                return ServerConnection.getIns().getGson().fromJson(r.body().string(), Video.class);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

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

                String string = response.body().string();
                System.out.println(string);

                return ServerConnection.getIns().getGson().fromJson(string, type);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
