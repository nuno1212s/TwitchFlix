package com.twitchflix.applicationclient.utils;

import android.graphics.Bitmap;

import java.util.UUID;

public class UserDAO {

    private UUID userID;

    private String firstName, lastName;

    private Bitmap channelThumbnail;

    private UserDAO(UUID userID, String firstName, String lastName, Bitmap channelThumbnail) {
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.channelThumbnail = channelThumbnail;
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

    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    public Bitmap getChannelThumbnail() {
        return channelThumbnail;
    }

    public static UserDAO fromData(UUID userID, String firstName, String lastName, Bitmap channelThumbnail) {
        return new UserDAO(userID, firstName, lastName, channelThumbnail);
    }

}
