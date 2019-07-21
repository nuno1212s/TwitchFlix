package com.twitchflix.applicationclient.channelview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.twitchflix.applicationclient.R;
import com.twitchflix.applicationclient.activities.EditVideoActivity;
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
        setContentView(R.layout.channelview_page_layout);

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
        SwipeRefreshLayout refreshLayout = findViewById(R.id.channelMainView);

        LinearLayout channelLayout = refreshLayout.findViewById(R.id.channelNameLayout);

        ScrollView scrollView = channelLayout.findViewById(R.id.channel_scroll_view);

        LinearLayout videoPlace = scrollView.findViewById(R.id.channel_main_layout);

        refreshLayout.setRefreshing(true);

        channelDrawer = new ChannelDrawer(this, videoPlace);

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

    }

    public void backButtonPressed(View view) {
        super.onBackPressed();
    }

    public void showPopup(View view) {

        PopupMenu popup = new PopupMenu(this, view);

        popup.inflate(R.menu.own_video_actions);

        UUID videoID = (UUID) view.getTag(R.id.options_menu_target_video);

        popup.setOnMenuItemClickListener((item) -> {
            switch (item.getItemId()) {

                case R.id.delete_video:

                    new AlertDialog.Builder(this)
                            .setTitle(R.string.delete_video_certain)
                            .setMessage(R.string.delete_video_certain_body)
                            .setIcon(R.drawable.ic_dialog_alert)
                            .setPositiveButton(R.string.delete_video_yes, (dialog, which) -> {
                                channelViewer.deleteVideo(videoID);
                            })
                            .setNegativeButton(R.string.delete_video_no, null)
                            .show();

                    return true;

                case R.id.edit_video:

                    EditVideoActivity.startActivity(this, videoID);

                    return true;

            }

            return false;

        });

        popup.show();
    }

}
