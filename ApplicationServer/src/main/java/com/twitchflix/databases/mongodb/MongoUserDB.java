package com.twitchflix.databases.mongodb;

import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import com.mongodb.reactivestreams.client.Success;
import com.twitchflix.authentication.User;
import com.twitchflix.authentication.accounts.OwnUser;
import com.twitchflix.databases.UserDatabase;
import org.bson.Document;
import org.reactivestreams.Publisher;

import java.util.UUID;

public class MongoUserDB extends MongoDB implements UserDatabase {

    private static final String USERS = "users";

    public MongoUserDB() {
        super();
    }

    @Override
    public boolean existsAccountWithEmail(String email) {

        MongoDatabase database = getDatabase();

        MongoCollection<Document> users = database.getCollection(USERS);

        Publisher<Document> email1 = users.find(Filters.eq("email", email))
                .limit(1).first();

        ObservableSubscriber<Document> subscriber = subcribeAndWait(email1);

        return !subscriber.getReceived().isEmpty();
    }

    @Override
    public void createAccount(User user) {

        MongoDatabase database = getDatabase();

        MongoCollection<Document> users = database.getCollection(USERS);

        Publisher<Success> successPublisher = users.insertOne(user.toMongoDB());

        OperationSubscriber<Success> subscriber = new OperationSubscriber<>();

        successPublisher.subscribe(subscriber);

    }

    @Override
    public void deleteAccount(UUID userID) {

        MongoDatabase database = getDatabase();

        MongoCollection<Document> users = database.getCollection(USERS);

        Publisher<DeleteResult> deleter = users.deleteOne(new Document("userID", userID.toString()));

        OperationSubscriber<DeleteResult> subscriber = new OperationSubscriber<>();

        deleter.subscribe(subscriber);

    }

    @Override
    public User getAccountInformation(String email) {

        MongoDatabase database = getDatabase();

        MongoCollection<Document> users = database.getCollection(USERS);

        Publisher<Document> email1 = users.find(new Document("email", email)).limit(1).first();

        return getUser(email1);
    }

    @Override
    public User getAccountInformation(UUID userID) {

        MongoDatabase database = getDatabase();

        MongoCollection<Document> users = database.getCollection(USERS);

        Publisher<Document> userID1 = users.find(new Document("userID", userID)).limit(1).first();

        return getUser(userID1);
    }


    @Override
    public OwnUser getAccountInformationOwnAccount(String email) {

        MongoDatabase database = getDatabase();

        MongoCollection<Document> users = database.getCollection(USERS);

        Publisher<Document> userData = users.find(new Document("email", email)).limit(1).first();

        return (OwnUser) getUser(userData);
    }

    @Override
    public OwnUser getAccountInformationOwnAccount(UUID ID) {

        MongoDatabase database = getDatabase();

        MongoCollection<Document> users = database.getCollection(USERS);

        Publisher<Document> userData = users.find(new Document("userID", ID)).limit(1).first();

        return (OwnUser) getUser(userData);
    }

    @Override
    public void updateAccount(User user) {
        MongoDatabase database = getDatabase();

        MongoCollection<Document> users = database.getCollection(USERS);

        Publisher<UpdateResult> updateResultPublisher = users.updateOne(Filters.eq("userID", user.getUserID()),
                new Document("$set", user.toMongoDB()));

        OperationSubscriber<UpdateResult> subscriber = new OperationSubscriber<>();
        updateResultPublisher.subscribe(subscriber);
    }

    @Override
    public void updateWatchedVideos(User user) {
        MongoDatabase database = getDatabase();

        MongoCollection<Document> users = database.getCollection(USERS);

        Publisher<UpdateResult> updateResultPublisher = users.updateOne(Filters.eq("userID", user.getUserID()),
                new Document("$set", user.videosToMongo()));

        OperationSubscriber<UpdateResult> subscriber = new OperationSubscriber<>();

        updateResultPublisher.subscribe(subscriber);
    }

    private User getUser(Publisher<Document> userID1) {
        ObservableSubscriber<Document> subscriber = subcribeAndWait(userID1);

        if (subscriber.getReceived().isEmpty()) {
            return null;
        }

        return User.fromMongoDB(subscriber.getReceived().get(0));
    }

    private ObservableSubscriber<Document> subcribeAndWait(Publisher<Document> email1) {
        ObservableSubscriber<Document> subscriber = new ObservableSubscriber<>();

        email1.subscribe(subscriber);

        try {
            subscriber.await();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        return subscriber;
    }

}
