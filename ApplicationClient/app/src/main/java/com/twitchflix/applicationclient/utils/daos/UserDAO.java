package com.twitchflix.applicationclient.utils.daos;

import android.graphics.Bitmap;

import java.util.UUID;

public class UserDAO {

    private UUID userID;

    private String firstName, lastName, email;

    private Bitmap channelThumbnail;

    private UserDAO(UUID userID, String firstName, String lastName, String email, Bitmap channelThumbnail) {
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.channelThumbnail = channelThumbnail;
    }

    public String getEmail() {return this.email;}

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

    public void setChannelThumbnail(Bitmap bitmap) {
        this.channelThumbnail = bitmap;
    }

    public static UserDAO fromData(UUID userID, String firstName, String lastName, String email, Bitmap channelThumbnail) {
        return new UserDAO(userID, firstName, lastName, email, channelThumbnail);
    }

}
