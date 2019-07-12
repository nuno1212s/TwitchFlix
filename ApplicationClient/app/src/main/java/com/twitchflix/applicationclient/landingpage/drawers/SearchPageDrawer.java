package com.twitchflix.applicationclient.landingpage.drawers;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.twitchflix.applicationclient.viewmodels.LandingPageViewModel;

import java.util.List;

public class SearchPageDrawer extends Drawer {

    public SearchPageDrawer(Activity parent, ViewGroup parentView) {
        super(parent, parentView);
    }

    @Override
    public void draw(List<LandingPageViewModel.VideoDAO> videos) {

        getParentView().removeAllViews();

        for (LandingPageViewModel.VideoDAO video : videos) {

            getParentView().addView(draw(video));

        }

    }

    public View draw(LandingPageViewModel.VideoDAO video) {

        LinearLayout videoLayout = new LinearLayout(getParentActivity());

        videoLayout.setOrientation(LinearLayout.HORIZONTAL);

        videoLayout.setClickable(true);

        ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 240);

        margin.setMargins(0, 35, 0, 0);

        videoLayout.setLayoutParams(margin);

        ImageView view = new ImageView(getParentActivity());

        view.setLayoutParams(new ViewGroup.LayoutParams(320, 240));

        view.setImageBitmap(video.getThumbnail());

        LinearLayout verticalLayout = new LinearLayout(getParentActivity());

        verticalLayout.setOrientation(LinearLayout.VERTICAL);

        TextView title = new TextView(getParentActivity());

        ViewGroup.MarginLayoutParams titleLayout = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 200);

        titleLayout.setMargins(10, 0, 0, 5);

        title.setText(video.getTitle());

        title.setTextSize(32);

        title.setLayoutParams(titleLayout);

        TextView channelTitle = new TextView(getParentActivity());

        ViewGroup.MarginLayoutParams channelNameLayout = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 20);

        channelNameLayout.setMargins(10, 5, 0, 0);

        channelTitle.setLayoutParams(channelNameLayout);

        channelTitle.setText(video.getUploaderName());

        channelTitle.setTextSize(12);

        verticalLayout.addView(title);
        verticalLayout.addView(channelTitle);

        videoLayout.addView(view);
        videoLayout.addView(verticalLayout);

        return videoLayout;
    }

}
