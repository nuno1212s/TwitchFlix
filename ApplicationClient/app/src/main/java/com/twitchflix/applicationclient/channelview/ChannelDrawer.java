package com.twitchflix.applicationclient.channelview;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.twitchflix.applicationclient.landingpage.OnClickVideoListener;
import com.twitchflix.applicationclient.utils.Drawer;
import com.twitchflix.applicationclient.utils.daos.VideoDAO;

import java.util.List;

public class ChannelDrawer extends Drawer {

    public ChannelDrawer(Activity parent, ViewGroup parentView) {
        super(parent, parentView);
    }

    @Override
    public void draw(List<VideoDAO> videos) {

        System.out.println(videos);

        getParentView().removeAllViews();

        for (VideoDAO video : videos) {

            LinearLayout eachVideo = new LinearLayout(getParentActivity());

            eachVideo.setClickable(true);

            eachVideo.setOnClickListener(new OnClickVideoListener(getParentActivity(), video.getVideoID()));

            eachVideo.setPadding(0, 35, 0, 0);

            eachVideo.setOrientation(LinearLayout.HORIZONTAL);

            ImageView thumbnail = new ImageView(getParentActivity());

            thumbnail.setLayoutParams(new ViewGroup.MarginLayoutParams(320, 240));

            thumbnail.setImageBitmap(video.getThumbnail());

            LinearLayout textAndDesc = new LinearLayout(getParentActivity());

            textAndDesc.setOrientation(LinearLayout.VERTICAL);

            TextView videoName = new TextView(getParentActivity());

            videoName.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 40));

            videoName.setText(video.getTitle());

            videoName.setGravity(View.TEXT_ALIGNMENT_CENTER);

            TextView videoDesc = new TextView(getParentActivity());

            videoDesc.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 200));

            videoDesc.setText(video.getDescription());

            textAndDesc.addView(videoName);
            textAndDesc.addView(videoDesc);

            eachVideo.addView(thumbnail);
            eachVideo.addView(textAndDesc);

            getParentView().addView(eachVideo);
        }

    }

    private void drawInto(ViewGroup group, VideoDAO video) {

        LinearLayout eachVideo = new LinearLayout(getParentActivity());



    }
}
