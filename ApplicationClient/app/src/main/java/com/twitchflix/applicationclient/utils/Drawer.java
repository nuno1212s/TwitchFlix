package com.twitchflix.applicationclient.utils;

import android.app.Activity;
import android.view.ViewGroup;
import com.twitchflix.applicationclient.utils.daos.VideoDAO;

import java.util.List;

public abstract class Drawer {

    private ViewGroup parentViewGroup;

    private Activity parentActivity;

    public Drawer(Activity parent, ViewGroup parentView) {

        this.parentActivity = parent;
        this.parentViewGroup = parentView;

    }

    protected Activity getParentActivity() {
        return parentActivity;
    }

    protected ViewGroup getParentView() {
        return parentViewGroup;
    }

    public abstract void draw(List<VideoDAO> videos);

}
