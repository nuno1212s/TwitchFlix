package com.twitchflix.applicationclient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;

public class ServerConnection {

    private static ServerConnection ins;

    public static ServerConnection getIns() {

        if (ins == null) {
            ins = new ServerConnection();
        }

        return ins;
    }

    private OkHttpClient client;

    private Gson gson;

    private static final String SERVER_IP = "https://35.246.91.18:8443/";

    private ServerConnection() {

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
