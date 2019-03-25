package com.twitchflix.databases.mongodb;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.model.Filters;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import com.twitchflix.authentication.User;
import com.twitchflix.authentication.accounts.OwnUser;
import com.twitchflix.databases.UserDatabase;
import org.bson.Document;
import org.reactivestreams.Publisher;

import java.util.UUID;

public class MongoUserDB implements UserDatabase {

    private MongoClient client;

    private String userName, database, password;

    public MongoUserDB() {

        //TODO: Load database configs

        MongoCredential credential = MongoCredential.createCredential(userName, database, password.toCharArray());

        ConnectionString connectionString = new ConnectionString("mongodb://localhost");

        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .credential(credential)
                .build();

        this.client = MongoClients.create(clientSettings);

    }

    @Override
    public boolean existsAccountWithEmail(String email) {

        MongoDatabase database = client.getDatabase(this.database);

        MongoCollection<Document> users = database.getCollection("users");

        Publisher<Document> email1 = users.find(Filters.eq("email", email))
                .limit(1).first();

        ObservableSubscriber<Document> subscriber = new ObservableSubscriber<>();

        email1.subscribe(subscriber);

        try {
            subscriber.await();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        return subscriber.getReceived().isEmpty();
    }

    @Override
    public void createAccount(User user) {

    }

    @Override
    public void deleteAccount(UUID userID) {

    }

    @Override
    public User getAccountInformation(String email) {
        return null;
    }

    @Override
    public User getAccountInformation(UUID userID) {
        return null;
    }

    @Override
    public OwnUser getAccountInformationOwnAccount(String email) {
        return null;
    }

    @Override
    public OwnUser getAccountInformationOwnAccount(UUID ID) {
        return null;
    }
}
