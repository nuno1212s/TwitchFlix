package com.twitchflix.authentication.oauth2;

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

    public OAuthUser(UUID playerID, String firstName, String lastName, String email,
                     Set<UUID> likedVideos, Set<UUID> watchedVideos, Set<UUID> uploadedVideos) {
        super(playerID, firstName, lastName, email, uploadedVideos, likedVideos, watchedVideos);
    }

    public static User fromMongoDB(Document d) {

        UUID userID = UUID.fromString(d.getString("userID"));
        String firstName = d.getString("FirstName"),
                lastName = d.getString("LastName"),
                email = d.getString("email");

        Set<UUID> likedVideos = new HashSet<>(d.getList("LikedVideos", UUID.class)),
                watchedVideos = new HashSet<>(d.getList("WatchedVideos", UUID.class)),
                uploadedVideos = new HashSet<>(d.getList("UploadedVideos", UUID.class));

        return new OAuthUser(userID, firstName, lastName, email, likedVideos, watchedVideos, uploadedVideos);
    }

}

