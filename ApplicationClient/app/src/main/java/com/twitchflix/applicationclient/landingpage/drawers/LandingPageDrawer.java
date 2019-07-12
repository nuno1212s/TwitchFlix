package com.twitchflix.applicationclient.landingpage.drawers;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.twitchflix.applicationclient.landingpage.OnClickVideoListener;
import com.twitchflix.applicationclient.viewmodels.LandingPageViewModel;

import java.util.*;

public class LandingPageDrawer extends Drawer {

    public LandingPageDrawer(Activity parent, ViewGroup parentView) {
        super(parent, parentView);
    }

    @Override
    public void draw(List<LandingPageViewModel.VideoDAO> videos) {

        getParentView().removeAllViews();

        Map<UUID, List<LandingPageViewModel.VideoDAO>> groupedVideos = new HashMap<>();

        for (LandingPageViewModel.VideoDAO video : videos) {
            List<LandingPageViewModel.VideoDAO> videoDAOS = groupedVideos.get(video.getUploader());

            if (videoDAOS == null) {
                videoDAOS = new ArrayList<>();
            }

            videoDAOS.add(video);

            groupedVideos.put(video.getUploader(), videoDAOS);
        }

        for (Map.Entry<UUID, List<LandingPageViewModel.VideoDAO>> channels : groupedVideos.entrySet()) {

            String channel_name_text = channels.getValue().get(0).getUploaderName();

            TextView channel_name = new TextView(getParentActivity());

            channel_name.setText(channel_name_text);

            channel_name.setClickable(true);

            channel_name.setTextSize(25);

            ViewGroup.MarginLayoutParams viewParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            viewParams.setMargins(10, 40, 0, 15);

            channel_name.setLayoutParams(viewParams);

            getParentView().addView(channel_name);

            drawIntoView(getParentView(), channels.getValue());

        }

    }

    private void drawIntoView(ViewGroup parent, List<LandingPageViewModel.VideoDAO> toDraw) {

        HorizontalScrollView scrollingVideo = new HorizontalScrollView(getParentActivity());

        scrollingVideo.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout videos = new LinearLayout(getParentActivity());

        videos.setOrientation(LinearLayout.HORIZONTAL);

        videos.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        scrollingVideo.addView(videos);

        parent.addView(scrollingVideo);

        for (LandingPageViewModel.VideoDAO videoDAO : toDraw) {
            drawIntoView(videos, videoDAO);
        }
    }

    private void drawIntoView(ViewGroup parent, LandingPageViewModel.VideoDAO toDraw) {

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
