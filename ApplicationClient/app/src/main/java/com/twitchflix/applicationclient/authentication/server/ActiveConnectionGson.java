package com.twitchflix.applicationclient.authentication.server;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.twitchflix.applicationclient.authentication.ActiveConnection;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ActiveConnectionGson extends TypeAdapter<ActiveConnection> {

    @Override
    public void write(JsonWriter out, ActiveConnection value) throws IOException {
        out.beginObject();

        out.name("owner");
        out.value(value.getOwner().toString());

        out.name("createdTime");
        out.value(value.getCreatedTime());

        out.name("validFor");
        out.value(value.getValidFor());

        out.name("accessToken");
        out.value(value.getAccessToken());

        out.endObject();

    }

    @Override
    public ActiveConnection read(JsonReader in) throws IOException {

        in.beginObject();

        UUID owner = null;

        long createdTime = 0, validFor = 0;

        String accessToken = null;

        while (in.hasNext()) {
            switch (in.nextName()) {
                case "owner":
                    owner = UUID.fromString(in.nextString());
                    break;
                case "createdTime":
                    createdTime = in.nextLong();
                    break;
                case "validFor":
                    validFor = in.nextLong();
                    break;
                case "accessTokenBytes":
                    accessToken = in.nextString();
                    break;
                case "accessToken":
                    in.nextString();
                    break;
                case "valid":
                    in.nextBoolean();
                    break;
                default:
                    break;
            }

        }

        in.endObject();

        return new ActiveConnection(owner, createdTime, validFor, accessToken.getBytes(StandardCharsets.UTF_8));
    }
}
