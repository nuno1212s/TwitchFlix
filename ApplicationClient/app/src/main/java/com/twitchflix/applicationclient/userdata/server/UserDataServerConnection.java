package com.twitchflix.applicationclient.userdata.server;

import com.twitchflix.applicationclient.ServerConnection;
import com.twitchflix.applicationclient.authentication.ActiveConnection;
import com.twitchflix.applicationclient.rest.models.UserData;
import com.twitchflix.applicationclient.userdata.UserDataRequests;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

public class UserDataServerConnection implements UserDataRequests {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    public UserData requestUserData(UUID userID) {
        Request request = new Request.Builder()
                .url(ServerConnection.getServerIp() + "userdata/rquserbyuuid")
                .header("uuid", userID.toString())
                .get()
                .build();

        return executeAndGetUserData(request);
    }

    @Override
    public UserData requestUserData(String email) {

        Request request = new Request.Builder()
                .url(ServerConnection.getServerIp() + "userdata/rquserbyemail")
                .header("email", email)
                .get()
                .build();

        return executeAndGetUserData(request);
    }

    @Override
    public UserData requestUserData(ActiveConnection connection) {

        Request request = new Request.Builder()
                .url(ServerConnection.getServerIp() + "userdata/rquser")
                .post(RequestBody.create(JSON, connection.toJSONObject().toString()))
                .build();

        System.out.println(connection.toJSONObject().toString());

        return executeAndGetUserData(request);
    }

    @Override
    public void updateFirstName(ActiveConnection connection, String firstName) {

        JSONObject jsonObject = connection.toJSONObject();

        try {
            jsonObject.put("name", firstName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new Request.Builder()
                .url(ServerConnection.getServerIp() + "userdata/updateFirstName")
                .post(RequestBody.create(JSON, jsonObject.toString()))
                .build();

        try (Response r = ServerConnection.getIns().getClient().newCall(request).execute()) {

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void updateLastName(ActiveConnection connection, String lastName) {


        JSONObject jsonObject = connection.toJSONObject();

        try {

            jsonObject.put("name", lastName);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new Request.Builder()
                .url(ServerConnection.getServerIp() + "userdata/updateLastName")
                .post(RequestBody.create(JSON, jsonObject.toString()))
                .build();

        try (Response r = ServerConnection.getIns().getClient().newCall(request).execute()) {

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private UserData executeAndGetUserData(Request request) {

        try (Response r = ServerConnection.getIns().getClient().newCall(request).execute()) {

            if (r.code() == 200) {

                String string = r.body().string();
                System.out.println(string);

                return ServerConnection.getIns().getGson().fromJson(string, UserData.class);

            } else {
                System.out.println("ERROR:");
                System.out.println(r.code());
                System.out.println(r.body().string());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
