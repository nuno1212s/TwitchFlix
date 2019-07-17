package com.twitchflix.applicationclient.landingpage.drawers;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.twitchflix.applicationclient.R;
import com.twitchflix.applicationclient.customview.RoundedView;
import com.twitchflix.applicationclient.landingpage.OnClickChannelListener;
import com.twitchflix.applicationclient.landingpage.OnClickVideoListener;
import com.twitchflix.applicationclient.utils.Drawer;
import com.twitchflix.applicationclient.utils.daos.VideoDAO;

import java.util.List;

public class SearchPageDrawer extends Drawer {

    public SearchPageDrawer(Activity parent, ViewGroup parentView) {
        super(parent, parentView);
    }

    @Override
    public void draw(List<VideoDAO> videos) {

        getParentView().removeAllViews();

        for (VideoDAO video : videos) {

            getParentView().addView(draw(video));

        }

    }

    public View draw(VideoDAO video) {

        View inflate = LayoutInflater.from(getParentActivity()).inflate(R.layout.search_video_layout, null);

        ImageView thumbnail = inflate.findViewById(R.id.videoThumbnail);

        thumbnail.setImageBitmap(video.getThumbnail());

        OnClickVideoListener onClickVideo = new OnClickVideoListener(getParentActivity(), video.getVideoID());

        thumbnail.setOnClickListener(onClickVideo);

        View information = inflate.findViewById(R.id.videoInformation);

        TextView title = information.findViewById(R.id.title);

        title.setText(video.getTitle());

        title.setOnClickListener(onClickVideo);

        View channelInformation = information.findViewById(R.id.channelInformation);

        channelInformation.setOnClickListener(new OnClickChannelListener(getParentActivity(), video.getUploader().getUserID()));

        RoundedView channelThumbnail = channelInformation.findViewById(R.id.channelThumbnail);

        channelThumbnail.setImageBitmap(video.getChannelThumbnail());

        TextView channelName = channelInformation.findViewById(R.id.channelName);

        channelName.setText(video.getUploaderName());

        return inflate;
    }

}
