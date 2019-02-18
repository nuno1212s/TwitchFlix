package com.nunoneto.authentication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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

    private String firstName, lastName, email;

    private List<UUID> uploadedVideos, likedVideos, watchedVideos;

    public User(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;

        this.uploadedVideos = Collections.synchronizedList(new ArrayList<>());
        this.likedVideos = Collections.synchronizedList(new ArrayList<>());
        this.watchedVideos = Collections.synchronizedList(new ArrayList<>());
    }

    public User(UUID userID, String firstname, String lastName, String email,
                List<UUID> uploadedVideos, List<UUID> likedVideos, List<UUID> watchedVideos) {
        this.userID = userID;
        this.firstName = firstname;
        this.lastName = lastName;
        this.email = email;

        this.uploadedVideos = Collections.synchronizedList(uploadedVideos);
        this.likedVideos = Collections.synchronizedList(likedVideos);
        this.watchedVideos = Collections.synchronizedList(watchedVideos);
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

    public void addLike(UUID video) {
        this.likedVideos.add(video);
    }

    public void addUploadedVideo(UUID video) {
        this.uploadedVideos.add(video);
    }

    public void addWatchedVideo(UUID video) {
        this.watchedVideos.add(video);
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

}
