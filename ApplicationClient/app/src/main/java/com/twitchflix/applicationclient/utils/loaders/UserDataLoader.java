package com.twitchflix.applicationclient.utils.loaders;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.twitchflix.applicationclient.ClientApp;
import com.twitchflix.applicationclient.rest.models.UserData;
import com.twitchflix.applicationclient.utils.daos.UserDAO;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public interface UserDataLoader {

    Map<UUID, UserDAO> userData = new ConcurrentHashMap<>();

    Map<UUID, Bitmap> userPhotos = new ConcurrentHashMap<>();

    default UserDAO getUserData(UUID userID) {
        if (userData.containsKey(userID)) {
            return userData.get(userID);
        }

        UserData userData = ClientApp.getIns().getUserDataRequests().requestUserData(userID);

        UserDAO userDAO = UserDAO.fromData(userID, userData.getFirstName(), userData.getLastName(), userData.getEmail(), getUserPhoto(userData));

        UserDataLoader.userData.put(userID, userDAO);

        return userDAO;
    }

    default UserDAO getUserData(UserData user) {
        if (userData.containsKey(user.getUuid())) {
            return userData.get(user.getUuid());
        }

        UserDAO userDAO = UserDAO.fromData(user.getUuid(), user.getFirstName(), user.getLastName(), user.getEmail(), getUserPhoto(user));

        UserDataLoader.userData.put(user.getUuid(), userDAO);

        return userDAO;
    }

    default void updateUserData(UUID userId, Bitmap newData) {
        userPhotos.put(userId, newData);

        UserDAO userDAO = userData.get(userId);

        if (userDAO != null)
            userDAO.setChannelThumbnail(newData);
    }

    default String getUsername(UUID userID) {
        return getUserData(userID).getFullName();
    }

    default Bitmap getUserPhoto(UserData user) {

        if (userPhotos.containsKey(user.getUuid())) {
            return userPhotos.get(user.getUuid());
        }

        try {

            InputStream inputStream = new URL(user.getPhotoLink()).openStream();

            return BitmapFactory.decodeStream(inputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
