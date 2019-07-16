package com.twitchflix.authentication;

import com.twitchflix.authentication.accounts.OwnUser;

import java.util.UUID;

public class UserData {

    private UUID uuid;

    private String firstName, lastName, email, salt, photoLink;

    UserData(UUID uuid, String firstName, String lastName, String email, String photoLink, String salt) {
        this.uuid = uuid;
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
        return photoLink;
    }

    public static UserData fromUserEmail(User user) {

        String salt = "";

        if (user instanceof OwnUser) {
            salt = ((OwnUser) user).getSalt();
        }

        return new UserData(user.getUserID(), "NO_PERMISSION", "NO_PERMISSION", user.getEmail(), user.getPhotoLink(), salt);
    }

    public static UserData fromActiveConnection(User user) {

        String salt = "";

        if (user instanceof OwnUser) {
            salt = ((OwnUser) user).getSalt();
        }

        return new UserData(user.getUserID(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhotoLink(), salt);
    }
}
