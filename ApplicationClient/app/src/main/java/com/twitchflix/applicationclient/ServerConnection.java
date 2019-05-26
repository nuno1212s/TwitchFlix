package com.twitchflix.applicationclient;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.twitchflix.applicationclient.authentication.ActiveConnection;
import okhttp3.OkHttpClient;

public class ServerConnection {

    private static ServerConnection ins;

    public static ServerConnection getIns() {
        return ins;
    }

    public static void init(Context c) {
        ins = new ServerConnection(c);
    }

    private OkHttpClient client;

    private Gson gson;

    private static final String SERVER_IP = "https://nuno1212s.ovh:8443/";

    private ServerConnection(Context c) {

        this.client = new OkHttpClient();

        GsonBuilder builder = new GsonBuilder();

        this.gson = builder.create();

    }

    public static String getServerIp() {
        return SERVER_IP;
    }

    public OkHttpClient getClient() {
        return client;
    }

    public Gson getGson() {
        return gson;
    }

}
