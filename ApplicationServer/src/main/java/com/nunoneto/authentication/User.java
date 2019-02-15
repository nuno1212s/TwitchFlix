package com.nunoneto.authentication;

import java.util.UUID;

/**
 * Base user storage class.
 *
 * Does not store the password of the user
 *
 * This class is meant to be used as OAuth2 google
 */
public abstract class User {

    //An unique universal identifier for each user
    private UUID userID;

    private String firstName, lastName, email;

    //TODO: Implement likes, subscriptions and other stuff like that. (Maybe notifications?)

    public User(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public User(UUID userID, String firstname, String lastName, String email) {
        this.userID = userID;
        this.firstName = firstname;
        this.lastName = lastName;
        this.email = email;
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
}
