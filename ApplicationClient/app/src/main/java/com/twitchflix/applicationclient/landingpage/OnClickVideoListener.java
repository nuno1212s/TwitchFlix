package com.twitchflix.applicationclient.landingpage;

import android.app.Activity;
import android.view.View;
import com.twitchflix.applicationclient.activities.WatchVideo;

import java.lang.ref.WeakReference;
import java.util.UUID;

public class OnClickVideoListener implements View.OnClickListener {

    private WeakReference<Activity> landingPageActivity;

    private UUID videoID;

    public OnClickVideoListener(Activity landingPage, UUID videoID) {
        landingPageActivity = new WeakReference<>(landingPage);
        this.videoID = videoID;
    }

    @Override
    public void onClick(View v) {

        Activity activity = landingPageActivity.get();

        if (activity != null) {

            WatchVideo.start(activity, this.videoID);

        }

    }
}
