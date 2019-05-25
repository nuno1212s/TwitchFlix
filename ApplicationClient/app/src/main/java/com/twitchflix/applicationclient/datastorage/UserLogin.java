package com.twitchflix.applicationclient.datastorage;

import com.twitchflix.applicationclient.ClientApp;
import com.twitchflix.applicationclient.authentication.ActiveConnection;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class UserLogin {

    private UUID userID;

    /**
     * The token is varied, can be either the google id token or the hashed user password
     */
    private String email, token, accessToken;

    private UserLogin(UUID userID, String email, String token, String accessToken) {
        this.userID = userID;
        this.email = email;
        this.token = token;
        this.accessToken = accessToken;
    }

    public UUID getUserID() {
        return userID;
    }

    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public ActiveConnection toActiveConnection() {

        ActiveConnection a = new ActiveConnection(getUserID(), 0, 0, this.accessToken.getBytes(StandardCharsets.UTF_8));

        a.refreshConnection();

        return a;
    }

    public static class UserLoginBuilder {

        private UUID userID;
        private String email;
        private String token;
        private String accessToken;

        public UserLoginBuilder setUserID(UUID userID) {
            this.userID = userID;

            return this;
        }

        public UserLoginBuilder setEmail(String email) {
            this.email = email;
            return this;
        }

        public UserLoginBuilder setToken(String token) {
            this.token = token;
            return this;
        }

        public UserLoginBuilder setAccessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public UserLogin createUserLogin() {
            return new UserLogin(this.userID, email, token, accessToken);
        }
    }
}
