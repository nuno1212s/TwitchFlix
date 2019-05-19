package com.twitchflix.applicationclient.datastorage;

public class UserLogin {

    private String userName, hashedPassword, accessToken;

    private UserLogin(String userName, String hashedPassword, String accessToken) {
        this.userName = userName;
        this.hashedPassword = hashedPassword;
        this.accessToken = accessToken;
    }

    public String getUserName() {
        return userName;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public static class UserLoginBuilder {
        private String userName;
        private String hashedPassword;
        private String accessToken;

        public UserLoginBuilder setUserName(String userName) {
            this.userName = userName;
            return this;
        }

        public UserLoginBuilder setHashedPassword(String hashedPassword) {
            this.hashedPassword = hashedPassword;
            return this;
        }

        public UserLoginBuilder setAccessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public UserLogin createUserLogin() {
            return new UserLogin(userName, hashedPassword, accessToken);
        }
    }
}
