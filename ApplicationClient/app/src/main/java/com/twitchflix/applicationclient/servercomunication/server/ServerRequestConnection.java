package com.twitchflix.applicationclient.servercomunication.server;

import com.google.gson.reflect.TypeToken;
import com.twitchflix.applicationclient.ServerConnection;
import com.twitchflix.applicationclient.authentication.ActiveConnection;
import com.twitchflix.applicationclient.rest.models.EditVideo;
import com.twitchflix.applicationclient.rest.models.UserVideo;
import com.twitchflix.applicationclient.rest.models.Video;
import com.twitchflix.applicationclient.rest.models.VideoStream;
import com.twitchflix.applicationclient.servercomunication.ServerRequests;
import okhttp3.*;
import org.json.JSONException;
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

        try {
            jsonObject.put("search", text);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new Request.Builder()
                .url(ServerConnection.getServerIp() + "videos/search")
                .post(RequestBody.create(JSON, jsonObject.toString()))
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

            if (r.code() == 200) {

                return ServerConnection.getIns().getGson().fromJson(r.body().string(), Video.class);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void addView(UUID videoID, ActiveConnection connection) {

        JSONObject jsonObject = connection.toJSONObject();

        try {
            jsonObject.put("videoID", videoID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new Request.Builder()
                .url(ServerConnection.getServerIp() + "videos/view")
                .post(RequestBody.create(JSON, jsonObject.toString()))
                .build();

        try {
            ServerConnection.getIns().getClient().newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void deleteVideo(UUID videoID, ActiveConnection connection) {

        JSONObject jsonObject = connection.toJSONObject();

        try {

            jsonObject.put("videoID", videoID);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new Request.Builder()
                .url(ServerConnection.getServerIp() + "videos/deleteVideo")
                .post(RequestBody.create(JSON, jsonObject.toString()))
                .build();

        try {
            ServerConnection.getIns().getClient().newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void editVideo(EditVideo video) {

        String json = ServerConnection.getIns().getGson().toJson(video);

        Request request = new Request.Builder()
                .url(ServerConnection.getServerIp() + "videos/editVideo")
                .post(RequestBody.create(JSON, json))
                .build();

        try {
            ServerConnection.getIns().getClient().newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public VideoStream requestVideoStream(String title, String description, ActiveConnection connection) {

        JSONObject jsonObject = connection.toJSONObject();

        try {
            jsonObject.put("title", title);
            jsonObject.put("description", description);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new Request.Builder()
                .url(ServerConnection.getServerIp() + "videos/stream")
                .post(RequestBody.create(JSON, jsonObject.toString()))
                .build();

        try (Response r = ServerConnection.getIns().getClient().newCall(request).execute()) {

            if (r.code() == 200) {

                return ServerConnection.getIns().getGson().fromJson(r.body().string(), VideoStream.class);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<UserVideo> getVideosByUser(UUID userID) {

        Request r = new Request.Builder().url(ServerConnection.getServerIp() + "videos/byUploader")
                .header("user", userID.toString())
                .get()
                .build();

        return executeAndLoadVideos(r);

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
