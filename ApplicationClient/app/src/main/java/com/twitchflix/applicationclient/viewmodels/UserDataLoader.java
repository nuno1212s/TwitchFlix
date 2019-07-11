package com.twitchflix.applicationclient.viewmodels;

import com.twitchflix.applicationclient.ClientApp;
import com.twitchflix.applicationclient.rest.models.UserData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public interface UserDataLoader {

    Map<UUID, String> userNames = new HashMap<>();

    default String getUsername(UUID userID) {

        if (userNames.containsKey(userID)) {
            return userNames.get(userID);
        }

        UserData userData = ClientApp.getIns().getUserDataRequests().requestUserData(userID);

        String userName = userData.getFirstName() + " " + userData.getLastName();

        userNames.put(userID, userName);

        return userName;
    }

}
