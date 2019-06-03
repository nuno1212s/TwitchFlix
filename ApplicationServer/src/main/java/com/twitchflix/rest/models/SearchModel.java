package com.twitchflix.rest.models;

public class SearchModel extends LoginModel {

    private String search;

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
