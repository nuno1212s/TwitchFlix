package com.twitchflix.databases.mongodb;

import com.google.api.client.json.GenericJson;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;
import com.twitchflix.App;
import com.twitchflix.filesystem.FileManager;

public abstract class MongoDB {

    private static MongoClient client;

    protected static String userName, database, password, IP;

    public MongoDB() {

        if (client != null) {
            return;
        }

        FileManager fileManager = App.getFileManager();

        GenericJson genericJson = fileManager.readFile(fileManager.getFileFromResource("mongoconf.json"));

        MongoDB.userName = (String) genericJson.get("UserName");
        MongoDB.database = (String) genericJson.get("Database");
        MongoDB.password = (String) genericJson.get("Password");
        MongoDB.IP = (String) genericJson.get("IP");

        MongoCredential credential = MongoCredential.createCredential(userName, database, password.toCharArray());

        ConnectionString connectionString = new ConnectionString("mongodb://" + MongoDB.IP);

        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .credential(credential)
                .build();

        MongoDB.client = MongoClients.create(clientSettings);
    }

    protected MongoClient getClient() {
        return MongoDB.client;
    }

    protected MongoDatabase getDatabase() {
        return getClient().getDatabase(MongoDB.database);
    }

}
