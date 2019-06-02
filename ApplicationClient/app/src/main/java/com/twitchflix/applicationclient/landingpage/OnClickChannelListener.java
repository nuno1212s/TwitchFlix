package com.twitchflix.applicationclient.landingpage;

import android.app.Activity;
import android.view.View;
import com.twitchflix.applicationclient.channelview.ChannelView;

import java.lang.ref.WeakReference;
import java.util.UUID;

public class OnClickChannelListener implements View.OnClickListener {

    private WeakReference<Activity> activity;

    private UUID channelID;

    public OnClickChannelListener(Activity activity, UUID channelID) {
        this.activity = new WeakReference<>(activity);
        this.channelID = channelID;
    }

    @Override
    public void onClick(View view) {

        Activity activity = this.activity.get();

        if (activity != null) {

            ChannelView.start(activity, channelID);

        }

    }
}
