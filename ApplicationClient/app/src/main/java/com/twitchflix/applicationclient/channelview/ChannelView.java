package com.twitchflix.applicationclient.channelview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.twitchflix.applicationclient.R;
import com.twitchflix.applicationclient.landingpage.LandingPage;
import com.twitchflix.applicationclient.utils.Drawer;
import com.twitchflix.applicationclient.viewmodels.ChannelViewerModel;

import java.util.UUID;

public class ChannelView extends AppCompatActivity {

    private static final String CHANNEL_OWNER = "CHANNEL_OWNER";

    public static void start(Context previous, UUID channelOwner) {
        Intent intent = new Intent(previous, ChannelView.class);

        intent.putExtra(CHANNEL_OWNER, channelOwner);

        previous.startActivity(intent);
    }

    private UUID channelOwner;

    private ChannelViewerModel channelViewer;

    private Drawer channelDrawer;

    private TextView channelName;

    private ImageView channelThumbnail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_view);

        channelViewer = ViewModelProviders.of(this).get(ChannelViewerModel.class);

        channelOwner = (UUID) getIntent().getExtras().get(CHANNEL_OWNER);

        channelViewer.setChannelID(this.channelOwner);

        LinearLayout channelLayout = findViewById(R.id.channelNameLayout);

        channelName = channelLayout.findViewById(R.id.channelName);
        channelThumbnail = channelLayout.findViewById(R.id.channel_thumbnail);

        initAndDraw();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void initAndDraw() {

        SwipeRefreshLayout refreshLayout = findViewById(R.id.channel_activity);

        ScrollView scrollView = refreshLayout.findViewById(R.id.channel_scroll_view);

        LinearLayout clearable = scrollView.findViewById(R.id.channel_main_layout);

        channelDrawer = new ChannelDrawer(this, clearable);

        channelViewer.getChannelVideos().observe(this, (videos) -> {
            channelDrawer.draw(videos);

            refreshLayout.setRefreshing(false);
        });

        channelViewer.getUser().observe(this, (user) -> {

            channelName.setText(user.getFullName());

            channelThumbnail.setImageBitmap(user.getChannelThumbnail());

        });

        refreshLayout.setOnRefreshListener(() ->
                channelViewer.requestRefresh()
        );

        channelViewer.requestRefresh();

    }

    public void backButtonPressed(View view) {

        Intent intent = new Intent(this, LandingPage.class);

        startActivity(intent);

        finish();
    }
}
