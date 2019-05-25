package com.twitchflix.rest.models;

public class UpdateNameModel extends LoginModel {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
