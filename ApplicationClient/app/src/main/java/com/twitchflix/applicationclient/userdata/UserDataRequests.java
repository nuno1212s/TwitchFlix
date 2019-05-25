package com.twitchflix.applicationclient.userdata;

import com.twitchflix.applicationclient.authentication.ActiveConnection;
import com.twitchflix.applicationclient.rest.models.UserData;

import java.util.UUID;

public interface UserDataRequests {

    UserData requestUserData(UUID userID);

    UserData requestUserData(String email);

    UserData requestUserData(ActiveConnection connection);

    void updateFirstName(ActiveConnection connection, String firstName);

    void updateLastName(ActiveConnection connection, String lastName);

}
