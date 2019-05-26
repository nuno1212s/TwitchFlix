package com.twitchflix.authentication.accounts;

import com.twitchflix.authentication.User;
import org.bson.Document;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class OwnUser extends User {

    private byte[] password;

    private String salt;

    private boolean confirmed;

    public OwnUser(UUID userID, String firstName, String lastName, String email,
                   Set<UUID> watchedVideos, Set<UUID> likedVideos, Set<UUID> uploadedVideos,
                   String hashed_password, String salt) {
        super(userID, firstName, lastName, email, uploadedVideos, likedVideos, watchedVideos);

        this.password = hashed_password.getBytes(StandardCharsets.UTF_8);
        this.salt = salt;

    }

    public OwnUser(String name, String lastName, String email, String hashed_password, String salt) {
        super(name, lastName, email);

        this.password = hashed_password.getBytes(StandardCharsets.UTF_8);
        this.salt = salt;

        this.confirmed = false;
    }

    public byte[] getPassword() {
        return password;
    }

    public String getSalt() {
        return salt;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    @Override
    public Document toMongoDB() {
        Document document = super.toMongoDB();

        document.append("Password", new String(password, StandardCharsets.UTF_8));
        document.append("Salt", salt);
        document.append("Confirmed", confirmed);

        return document;
    }

    public static User fromMongoDB(Document d) {

        UUID userID = (UUID) d.get("userID");
        String firstName = d.getString("FirstName"),
                lastName = d.getString("LastName"),
                email = d.getString("email"),
                salt = d.getString("Salt"),
                password = d.getString("Password");

        Set<UUID> likedVideos = new HashSet<>(d.getList("LikedVideos", UUID.class)),
                watchedVideos = new HashSet<>(d.getList("WatchedVideos", UUID.class)),
                uploadedVideos = new HashSet<>(d.getList("UploadedVideos", UUID.class));

        return new OwnUser(userID, firstName, lastName, email, watchedVideos, likedVideos, uploadedVideos, password, salt);
    }
}
