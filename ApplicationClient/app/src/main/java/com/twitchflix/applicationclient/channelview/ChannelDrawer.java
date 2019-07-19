package com.twitchflix.applicationclient.channelview;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.twitchflix.applicationclient.ClientApp;
import com.twitchflix.applicationclient.R;
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

            View inflate = LayoutInflater.from(getParentActivity()).inflate(R.layout.channel_video_layout, null);

            inflate.setOnClickListener(new OnClickVideoListener(getParentActivity(), video.getVideoID()));

            ImageView thumbnail = inflate.findViewById(R.id.video_thumbnail);

            thumbnail.setImageBitmap(video.getThumbnail());

            View title_desc = inflate.findViewById(R.id.title_desc);

            TextView title = title_desc.findViewById(R.id.title),
                    description = title_desc.findViewById(R.id.description);

            View options_button = title_desc.findViewById(R.id.options_button);

            if (video.getUploader().getUserID().equals(ClientApp.getIns().getLoginHandler().getCurrentActiveConnection().getOwner())) {
                options_button.setVisibility(View.VISIBLE);
                options_button.setTag(R.id.options_menu_target_video, video.getVideoID());
            }

            title.setText(video.getTitle());

            description.setText(video.getDescription());

            getParentView().addView(inflate);
        }

    }

}
