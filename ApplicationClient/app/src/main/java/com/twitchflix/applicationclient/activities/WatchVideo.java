package com.twitchflix.applicationclient.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import com.google.android.exoplayer2.ui.PlayerView;
import com.twitchflix.applicationclient.R;
import com.twitchflix.applicationclient.viewmodels.WatchVideoViewModel;

import java.util.UUID;

public class WatchVideo extends AppCompatActivity {

    private static final String VIDEO_ID = "videoID";

    public static void start(Context context, UUID videoId) {

        Intent intent = new Intent(context, WatchVideo.class);

        intent.putExtra(VIDEO_ID, videoId.toString());

        context.startActivity(intent);
    }

    private PlayerView playerView;

    private TextView videoTitle, videoDescription;

    private WatchVideoViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_video);

        Bundle extras = getIntent().getExtras();

        playerView = findViewById(R.id.video_player);
        videoTitle = findViewById(R.id.video_title);
        videoDescription = findViewById(R.id.video_description);

        viewModel = ViewModelProviders.of(this).get(WatchVideoViewModel.class);

        viewModel.getPlayer().observe(this, (player) -> {
            this.playerView.setPlayer(player);
        });

        viewModel.getVideo().observe(this, (video) -> {
            videoTitle.setText(video.getTitle());
            videoDescription.setText(video.getDescription());
        });

        if (extras != null) {
            viewModel.playVideo(UUID.fromString(extras.getString(VIDEO_ID)));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        viewModel.pauseVideo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        viewModel.pauseVideo();
    }

    @Override
    protected void onResume() {
        super.onResume();

        viewModel.playVideo();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }

}
