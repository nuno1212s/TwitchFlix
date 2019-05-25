package com.twitchflix.applicationclient.rest.models;

public class UserVideo extends Video {

    private boolean watched, like, owner;

    public UserVideo() {
        super();
    }

    public boolean isWatched() {
        return watched;
    }

    public boolean isLike() {
        return like;
    }

    public boolean isOwner() {
        return owner;
    }

    public void setWatched(boolean watched) {
        this.watched = watched;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }
}
