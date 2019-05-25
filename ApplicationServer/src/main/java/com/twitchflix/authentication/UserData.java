package com.twitchflix.authentication;

import com.twitchflix.authentication.accounts.OwnUser;

import java.util.UUID;

public class UserData {

    private UUID uuid;

    private String firstName, lastName, email, salt;

    UserData(UUID uuid, String firstName, String lastName, String email, String salt) {
        this.uuid = uuid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.salt = salt;
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

    public static UserData fromUser(User user) {

        String salt = "";

        if (user instanceof OwnUser) {
            salt = ((OwnUser) user).getSalt();
        }

        return new UserData(user.getUserID(), user.getFirstName(), user.getLastName(), user.getEmail(), salt);
    }
}
