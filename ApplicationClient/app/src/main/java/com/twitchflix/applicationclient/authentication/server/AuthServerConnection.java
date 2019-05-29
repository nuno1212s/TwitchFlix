package com.twitchflix.applicationclient.authentication.server;

import com.twitchflix.applicationclient.ClientApp;
import com.twitchflix.applicationclient.ServerConnection;
import com.twitchflix.applicationclient.authentication.ActiveConnection;
import com.twitchflix.applicationclient.authentication.AuthRequests;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class AuthServerConnection implements AuthRequests {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private String getIp() {
        return ServerConnection.getServerIp();
    }

    @Override
    public ActiveConnection requestConnection(String email, String hashed_password) {

        JSONObject postBody = new JSONObject();

        try {
            postBody.put("email", email);
            postBody.put("password", hashed_password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new Request.Builder()
                .url(getIp() + "auth/login")
                .post(RequestBody.create(JSON, postBody.toString()))
                .build();


        return executeAndGetConnection(request);
    }

    @Override
    public ActiveConnection requestConnection(String google_token_id) {

        JSONObject idToken = new JSONObject();

        try {
            idToken.put("idToken", google_token_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println(google_token_id);

        Request request = new Request.Builder()
                .url(getIp() + "oauth/authenticate")
                .post(RequestBody.create(JSON, idToken.toString()))
                .build();

        return executeAndGetConnection(request);
    }

    @Override
    public ActiveConnection refreshActiveConnection(ActiveConnection activeConnection) {

        JSONObject jsonObject = activeConnection.toJSONObject(true);

        try {
            jsonObject.put("password", ClientApp.getIns().getInformationStorage().getCurrentLogin().getToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new Request.Builder()
                .url(getIp() + "auth/refresh")
                .post(RequestBody.create(JSON, jsonObject.toString()))
                .build();

        return executeAndGetConnection(request);
    }

    @Override
    public void destroyConnection(ActiveConnection activeConnection) {

        Request request = new Request.Builder()
                .url(getIp() + "auth/logout")
                .post(RequestBody.create(JSON, activeConnection.toJSONObject().toString()))
                .build();

        try {

            ClientApp.getIns().getClient().newCall(request).execute();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public ActiveConnection registerAccount(String email, String first_name, String last_name, String hashed_password, String salt) {

        JSONObject register = new JSONObject();

        try {
            register.put("email", email);
            register.put("firstName", first_name);
            register.put("lastName", last_name);
            register.put("password", hashed_password);
            register.put("salt", salt);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new Request.Builder()
                .url(getIp() + "auth/register")
                .post(RequestBody.create(JSON, register.toString()))
                .build();

        return executeAndGetConnection(request);
    }

    @Override
    public boolean accountExistsWithEmail(String email) {

        Request request = new Request.Builder()
                .url(getIp() + "userdata/rquserbyemail")
                .header("email", email)
                .get()
                .build();

        try (Response r = ClientApp.getIns().getClient().newCall(request).execute()) {

            if (r.code() == 200) {

                return true;

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private ActiveConnection executeAndGetConnection(Request r) {
        try (Response response = ClientApp.getIns().getClient().newCall(r).execute()) {

            if (response.code() == 200) {

                return ServerConnection.getIns().getGson().fromJson(response.body().string(), ActiveConnection.class);

            }

            String string = response.body().string();
            System.out.println(string);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

}
