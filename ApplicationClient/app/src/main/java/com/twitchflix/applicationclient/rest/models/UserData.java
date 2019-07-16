package com.twitchflix.applicationclient.rest.models;

import java.util.UUID;

public class UserData {

    private UUID uuid;

    private String firstName, lastName, email, salt;

    private String photoLink;

    public UserData(UUID playerID, String firstName, String lastName, String email, String salt, String photoLink) {
        this.uuid = playerID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.salt = salt;
        this.photoLink = photoLink;
    }

    public UUID getUuid() {
        return uuid;
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

    public String getSalt() {
        return salt;
    }

    public String getPhotoLink() {
        return this.photoLink;
    }

    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public void setPhotoLink(String photoLink) {
        this.photoLink = photoLink;
    }
}
