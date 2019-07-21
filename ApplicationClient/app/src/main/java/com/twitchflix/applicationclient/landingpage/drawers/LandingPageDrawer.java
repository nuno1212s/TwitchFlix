package com.twitchflix.applicationclient.landingpage.drawers;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.twitchflix.applicationclient.R;
import com.twitchflix.applicationclient.customviews.RoundedView;
import com.twitchflix.applicationclient.landingpage.OnClickChannelListener;
import com.twitchflix.applicationclient.landingpage.OnClickVideoListener;
import com.twitchflix.applicationclient.utils.Drawer;
import com.twitchflix.applicationclient.utils.daos.UserDAO;
import com.twitchflix.applicationclient.utils.daos.VideoDAO;

import java.util.*;

public class LandingPageDrawer extends Drawer {

    private LayoutInflater inflater;

    public LandingPageDrawer(Activity parent, ViewGroup parentView) {
        super(parent, parentView);

        inflater = LayoutInflater.from(getParentActivity());
    }

    @Override
    public void draw(List<VideoDAO> videos) {

        getParentView().removeAllViews();

        Map<UUID, List<VideoDAO>> groupedVideos = new HashMap<>();

        for (VideoDAO video : videos) {
            List<VideoDAO> videoDAOS = groupedVideos.get(video.getUploader().getUserID());

            if (videoDAOS == null) {
                videoDAOS = new ArrayList<>();
            }

            videoDAOS.add(video);

            groupedVideos.put(video.getUploader().getUserID(), videoDAOS);
        }


        for (Map.Entry<UUID, List<VideoDAO>> channels : groupedVideos.entrySet()) {

            View channelLayout = inflater.inflate(R.layout.landingpage_channel_layout, null);

            View channelInfo = channelLayout.findViewById(R.id.channelInformation);

            channelInfo.setOnClickListener(new OnClickChannelListener(getParentActivity(), channels.getKey()));

            List<VideoDAO> channelVideos = channels.getValue();

            ImageView channelThumbnail = channelInfo.findViewById(R.id.channelThumbnail);

            TextView textView = channelInfo.findViewById(R.id.channelTitle);

            UserDAO uploader = channelVideos.get(0).getUploader();

            channelThumbnail.setImageBitmap(uploader.getChannelThumbnail());

            textView.setText(uploader.getFullName());

            LinearLayout videoLayout = channelLayout.findViewById(R.id.channelVideosScroll).findViewById(R.id.channelVideos);

            for (VideoDAO channelVideo : channelVideos) {
                drawIntoView(videoLayout, channelVideo);
            }

            getParentView().addView(channelLayout);
        }

    }

    private void drawIntoView(ViewGroup parent, VideoDAO toDraw) {

        View videoLayout = inflater.inflate(R.layout.landingpage_individual_video, null);

        ImageView videoThumbnail = videoLayout.findViewById(R.id.videoThumbnail);

        TextView videoTitle = videoLayout.findViewById(R.id.videoTitle);

        videoThumbnail.setImageBitmap(toDraw.getThumbnail());

        videoTitle.setText(toDraw.getTitle());

        videoLayout.setOnClickListener(new OnClickVideoListener(getParentActivity(), toDraw.getVideoID()));

        parent.addView(videoLayout);
    }
}
