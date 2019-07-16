package com.twitchflix.applicationclient.landingpage.drawers;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.twitchflix.applicationclient.customview.RoundedView;
import com.twitchflix.applicationclient.landingpage.OnClickVideoListener;
import com.twitchflix.applicationclient.utils.Drawer;
import com.twitchflix.applicationclient.utils.VideoDAO;

import java.util.*;

public class LandingPageDrawer extends Drawer {

    public LandingPageDrawer(Activity parent, ViewGroup parentView) {
        super(parent, parentView);

    }

    @Override
    public void draw(List<VideoDAO> videos) {

        getParentView().removeAllViews();

        Map<UUID, List<VideoDAO>> groupedVideos = new HashMap<>();

        for (VideoDAO video : videos) {
            List<VideoDAO> videoDAOS = groupedVideos.get(video.getUploader().getUuid());

            if (videoDAOS == null) {
                videoDAOS = new ArrayList<>();
            }

            videoDAOS.add(video);

            groupedVideos.put(video.getUploader().getUuid(), videoDAOS);
        }

        for (Map.Entry<UUID, List<VideoDAO>> channels : groupedVideos.entrySet()) {

            LinearLayout channelNameLayout = new LinearLayout(getParentActivity());

            channelNameLayout.setOrientation(LinearLayout.HORIZONTAL);

            ViewGroup.MarginLayoutParams channelNameParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 50);

            channelNameLayout.setLayoutParams(channelNameParams);

            VideoDAO videoDAO = channels.getValue().get(0);

            //Draw the channel thumbnail
            RoundedView channelThumbnail = new RoundedView(getParentActivity());

            ViewGroup.LayoutParams channelParams = new ViewGroup.LayoutParams(32, 32);

            channelThumbnail.setPadding(9, 9, 9, 9);

            channelThumbnail.setLayoutParams(channelParams);

            channelThumbnail.setImageBitmap(videoDAO.getChannelThumbnail());

            //Channel name
            String channel_name_text = videoDAO.getUploaderName();

            ViewGroup.MarginLayoutParams channelTextNameParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 50);

            TextView channelName = new TextView(getParentActivity());

            channelName.setLayoutParams(channelTextNameParams);

            channelName.setGravity(View.TEXT_ALIGNMENT_CENTER);

            channelName.setText(channel_name_text);

            //Add all to the layout
            channelNameLayout.addView(channelThumbnail);

            channelNameLayout.addView(channelName);

            getParentView().addView(channelNameLayout);

            drawIntoView(getParentView(), channels.getValue());

        }

    }

    private void drawIntoView(ViewGroup parent, List<VideoDAO> toDraw) {

        HorizontalScrollView scrollingVideo = new HorizontalScrollView(getParentActivity());

        scrollingVideo.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout videos = new LinearLayout(getParentActivity());

        videos.setOrientation(LinearLayout.HORIZONTAL);

        videos.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        scrollingVideo.addView(videos);

        parent.addView(scrollingVideo);

        for (VideoDAO videoDAO : toDraw) {
            drawIntoView(videos, videoDAO);
        }
    }

    private void drawIntoView(ViewGroup parent, VideoDAO toDraw) {

        LinearLayout linearVideoLayout = new LinearLayout(getParentActivity());

        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(320, 300);

        params.setMargins(15, 0, 0, 0);

        linearVideoLayout.setLayoutParams(params);

        linearVideoLayout.setOrientation(LinearLayout.VERTICAL);

        linearVideoLayout.setClickable(true);

        ImageView thumbnail = new ImageView(getParentActivity());

        thumbnail.setLayoutParams(new ViewGroup.LayoutParams(320, 240));

        thumbnail.setAdjustViewBounds(true);

        thumbnail.setScaleType(ImageView.ScaleType.FIT_CENTER);

        thumbnail.setImageBitmap(toDraw.getThumbnail());

        TextView videoTitle = new TextView(getParentActivity());

        videoTitle.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        videoTitle.setText(toDraw.getTitle());

        linearVideoLayout.addView(thumbnail);
        linearVideoLayout.addView(videoTitle);

        linearVideoLayout.setOnClickListener(new OnClickVideoListener(getParentActivity(), toDraw.getVideoID()));

        parent.addView(linearVideoLayout);
    }
}
