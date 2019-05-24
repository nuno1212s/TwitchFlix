package com.twitchflix.rest.models;

public class RefreshConnectionModel extends LoginModel {

    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
