package com.twitchflix.authentication;

import com.twitchflix.App;
import com.twitchflix.authentication.accounts.OwnUser;
import com.twitchflix.authentication.oauth2.OAuthUser;
import org.bson.Document;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Base user storage class.
 * <p>
 * Does not store the password of the user
 * <p>
 * This class is meant to be used as OAuth2 google
 */
public abstract class User {

    //An unique universal identifier for each user
    private UUID userID;

    private String firstName, lastName, email, photoLink;

    private Set<UUID> uploadedVideos, likedVideos, watchedVideos;

    public User(String firstName, String lastName, String email) {
        this.userID = UUID.randomUUID();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.photoLink = "https://" + App.SERVER_IP + "/" + App.USER_PHOTOS + "/NO_PHOTO.png";

        this.uploadedVideos = Collections.synchronizedSet(new HashSet<>());
        this.likedVideos = Collections.synchronizedSet(new HashSet<>());
        this.watchedVideos = Collections.synchronizedSet(new HashSet<>());
    }

    public User(UUID userID, String firstname, String lastName, String email, String photoLink,
                Set<UUID> uploadedVideos, Set<UUID> likedVideos, Set<UUID> watchedVideos) {
        this.userID = userID;
        this.firstName = firstname;
        this.lastName = lastName;
        this.email = email;
        this.photoLink = photoLink;

        this.uploadedVideos = Collections.synchronizedSet(uploadedVideos);
        this.likedVideos = Collections.synchronizedSet(likedVideos);
        this.watchedVideos = Collections.synchronizedSet(watchedVideos);
    }

    public UUID getUserID() {
        return userID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhotoLink() {
        return this.photoLink;
    }

    public void addLike(UUID video) {
        this.likedVideos.add(video);

        App.getAsync().submit(() -> App.getUserDatabase().updateWatchedVideos(this));
    }

    public void addUploadedVideo(UUID video) {
        this.uploadedVideos.add(video);

        App.getAsync().submit(() -> App.getUserDatabase().updateWatchedVideos(this));
    }

    public boolean addWatchedVideo(UUID video) {
        if (this.watchedVideos.add(video)) {

            App.getAsync().submit(() -> App.getUserDatabase().updateWatchedVideos(this));

            return true;
        } else {
            return false;
        }
    }

    public boolean hasLikedVideo(UUID video) {
        return this.likedVideos.contains(video);
    }

    public boolean hasWatchedVideo(UUID video) {

        return this.watchedVideos.contains(video);

    }

    public boolean hasUploadedVideo(UUID video) {
        return this.uploadedVideos.contains(video);
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;

        App.getAsync().submit(() -> App.getUserDatabase().updateAccount(this));
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;

        App.getAsync().submit(() -> App.getUserDatabase().updateAccount(this));
    }

    public void setPhotoLink(String photoLink) {
        this.photoLink = photoLink;

        App.getAsync().submit(() -> App.getUserDatabase().updateAccount(this));
    }

    public Document toMongoDB() {

        return new Document("userID", this.getUserID())
                .append("FirstName", this.getFirstName())
                .append("LastName", this.getLastName())
                .append("email", this.getEmail())
                .append("photolink", this.getPhotoLink())
                .append("LikedVideos", this.likedVideos)
                .append("WatchedVideos", this.watchedVideos)
                .append("UploadedVideos", this.uploadedVideos);
    }

    public Document videosToMongo() {

        return new Document("WatchedVideos", this.watchedVideos)
                .append("LikedVideos", this.likedVideos)
                .append("UploadedVideos", this.uploadedVideos);

    }

    public static User fromMongoDB(Document d) {

        Class<? extends User> users = d.containsKey("Password") ? OwnUser.class : OAuthUser.class;

        User u;

        try {
            Method fromMongoDB = users.getDeclaredMethod("fromMongoDB", Document.class);

             u = (User) fromMongoDB.invoke(null, d);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();

            return null;
        }

        return u;
    }

}
