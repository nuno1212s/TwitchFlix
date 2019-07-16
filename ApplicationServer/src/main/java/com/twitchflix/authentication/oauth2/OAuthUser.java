package com.twitchflix.authentication.oauth2;

import com.twitchflix.App;
import com.twitchflix.authentication.User;
import org.bson.Document;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class OAuthUser extends User {

    public OAuthUser(String firstName, String lastName, String email) {
        super(firstName, lastName, email);
    }

    public OAuthUser(UUID playerID, String firstName, String lastName, String email, String photoLink,
                     Set<UUID> likedVideos, Set<UUID> watchedVideos, Set<UUID> uploadedVideos) {
        super(playerID, firstName, lastName, email, photoLink, uploadedVideos, likedVideos, watchedVideos);
    }

    public static User fromMongoDB(Document d) {

        UUID userID = (UUID) d.get("userID");
        String firstName = d.getString("FirstName"),
                lastName = d.getString("LastName"),
                email = d.getString("email"),
                photoLink = d.getString("photolink");


        if (photoLink == null) {
            photoLink = "https://" + App.SERVER_IP + "/" + App.USER_PHOTOS + "/NO_PHOTO.png";
        }

        Set<UUID> likedVideos = new HashSet<>(d.getList("LikedVideos", UUID.class)),
                watchedVideos = new HashSet<>(d.getList("WatchedVideos", UUID.class)),
                uploadedVideos = new HashSet<>(d.getList("UploadedVideos", UUID.class));

        return new OAuthUser(userID, firstName, lastName, email, photoLink, likedVideos, watchedVideos, uploadedVideos);
    }

}

