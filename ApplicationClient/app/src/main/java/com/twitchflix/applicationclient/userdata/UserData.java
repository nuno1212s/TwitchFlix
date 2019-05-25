package com.twitchflix.applicationclient.userdata;

import java.util.UUID;

public class UserData {

    private UUID uuid;

    private String firstName, lastName, email, salt;

    public UserData(UUID playerID, String firstName, String lastName, String email, String salt) {
        this.uuid = playerID;
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
}
