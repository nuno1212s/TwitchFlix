package com.twitchflix.applicationclient.userdata;

import com.twitchflix.applicationclient.authentication.ActiveConnection;

public interface UserDataRequests {

    UserData requestUserData(String email);

    UserData requestUserData(ActiveConnection connection);

    void updateFirstName(ActiveConnection connection, String firstName);

    void updateLastName(ActiveConnection connection, String lastName);

}
