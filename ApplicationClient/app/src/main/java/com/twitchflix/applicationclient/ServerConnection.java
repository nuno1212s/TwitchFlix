package com.twitchflix.applicationclient;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.twitchflix.applicationclient.authentication.ActiveConnection;
import com.twitchflix.applicationclient.authentication.server.ActiveConnectionGson;
import okhttp3.OkHttpClient;

import javax.net.ssl.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;

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

        builder.registerTypeAdapter(ActiveConnection.class, new ActiveConnectionGson());

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
