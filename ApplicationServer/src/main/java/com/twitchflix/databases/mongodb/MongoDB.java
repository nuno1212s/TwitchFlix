package com.twitchflix.databases.mongodb;

import com.google.api.client.json.GenericJson;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.twitchflix.App;
import com.twitchflix.filesystem.FileManager;

public abstract class MongoDB {

    protected static MongoClient client;

    protected static String userName, database, password;

    public MongoDB() {

        if (client == null) {
            return;
        }

        FileManager fileManager = App.getFileManager();

        GenericJson genericJson = fileManager.readFile(fileManager.getFileAndCreate("mongocfg.json"));

        MongoDB.userName = (String) genericJson.get("UserName");
        MongoDB.database = (String) genericJson.get("Database");
        MongoDB.password = (String) genericJson.get("Password");

        MongoCredential credential = MongoCredential.createCredential(userName, database, password.toCharArray());

        ConnectionString connectionString = new ConnectionString("mongodb://localhost");

        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .credential(credential)
                .build();

        MongoDB.client = MongoClients.create(clientSettings);
    }

}
