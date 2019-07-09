package com.twitchflix.applicationclient.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import com.google.android.exoplayer2.*;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.twitchflix.applicationclient.ClientApp;
import com.twitchflix.applicationclient.R;
import com.twitchflix.applicationclient.rest.models.Video;
import com.twitchflix.applicationclient.utils.NetworkUser;

import java.lang.ref.WeakReference;
import java.util.UUID;

public class WatchVideo extends AppCompatActivity {

    private static final String VIDEO_ID = "videoID";

    private static final String KEY_URL = "video_url";
    private static final String KEY_WINDOW = "window";
    private static final String KEY_POSITION = "position";
    private static final String KEY_AUTO_PLAY = "auto_play";
    private static final String KEY_LIVE = "live";

    public static void start(Context context, UUID videoId) {

        Intent intent = new Intent(context, WatchVideo.class);

        intent.putExtra(VIDEO_ID, videoId.toString());

        context.startActivity(intent);
    }

    private PlayerView playerView;

    private TextView videoTitle, videoDescription;

    private UUID videoID;

    private String videoURL;

    private boolean startAutoPlay, live;

    private long startPosition = 0;

    private int startWindow = C.INDEX_UNSET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_video);

        Bundle extras = getIntent().getExtras();

        playerView = findViewById(R.id.video_player);
        videoTitle = findViewById(R.id.video_title);
        videoDescription = findViewById(R.id.video_description);


        if (extras != null) {

            videoID = UUID.fromString(extras.getString(VIDEO_ID));

            if (extras.containsKey(KEY_URL)) {
                videoURL = extras.getString(KEY_URL);

                startAutoPlay = extras.getBoolean(KEY_AUTO_PLAY);
                startPosition = extras.getLong(KEY_POSITION);
                startWindow = extras.getInt(KEY_WINDOW);
                live = extras.getBoolean(KEY_LIVE);
            } else {
                startPosition = 0;
                startAutoPlay = true;

                new LoadVideo(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, videoID);
            }

        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (playerView != null && playerView.getPlayer() != null) {

            startPosition = Math.max(0, playerView.getPlayer().getContentPosition());
            startWindow = playerView.getPlayer().getCurrentWindowIndex();

            playerView.getPlayer().release();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (playerView.getPlayer() != null)
            playerView.getPlayer().release();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (playerView == null) {
            initPlayer();
        } else {
            resumePlayer();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        outState.putString(VIDEO_ID, this.videoID.toString());
        outState.putInt(KEY_WINDOW, startWindow);
        outState.putLong(KEY_POSITION, startPosition);
        outState.putBoolean(KEY_AUTO_PLAY, startAutoPlay);
        outState.putString(KEY_URL, videoURL);
        outState.putBoolean(KEY_LIVE, live);

    }

    private void initPlayer() {

        boolean hasPlayPosition = this.startWindow != C.INDEX_UNSET;

        if (live) {
            DataSource.Factory dataSource = new DefaultHttpDataSourceFactory(Util.getUserAgent(this, "TwitchFlix"));

            HlsMediaSource source = new HlsMediaSource.Factory(dataSource).createMediaSource(Uri.parse(videoURL));

            SimpleExoPlayer simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this);

            simpleExoPlayer.prepare(source);

            simpleExoPlayer.setPlayWhenReady(startAutoPlay);

            if (hasPlayPosition) {
                this.playerView.getPlayer().seekTo(startWindow, startPosition);
            }

            this.playerView.setPlayer(simpleExoPlayer);
        } else {

            DataSource.Factory dataSource = new DefaultHttpDataSourceFactory(Util.getUserAgent(this, "TwitchFlix"));

            MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(videoURL),
                    dataSource, new DefaultExtractorsFactory(), null, null);

            SimpleExoPlayer simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this);

            simpleExoPlayer.prepare(mediaSource);

            simpleExoPlayer.setPlayWhenReady(startAutoPlay);

            if (hasPlayPosition) {
                this.playerView.getPlayer().seekTo(startWindow, startPosition);
            }

            this.playerView.setPlayer(simpleExoPlayer);
        }

        System.out.println(this.playerView.getPlayer().getContentDuration());

        playerView.getPlayer().addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) { }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {}

            @Override
            public void onLoadingChanged(boolean isLoading) { }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == ExoPlayer.STATE_ENDED) {
                    playerView.getPlayer().seekTo(0);
                    playerView.getPlayer().setPlayWhenReady(false);
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) { }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) { }

            @Override
            public void onPlayerError(ExoPlaybackException error) { }

            @Override
            public void onPositionDiscontinuity(int reason) { }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) { }

            @Override
            public void onSeekProcessed() { }
        });

    }

    private void resumePlayer() {

        Player player = playerView.getPlayer();

        if (player != null && startWindow != C.INDEX_UNSET) {
            player.setPlayWhenReady(this.startAutoPlay);
            player.seekTo(startWindow, startPosition);
        }

    }

    private void setLive(boolean live) {
        this.live = live;
    }

    private void setVideoTitle(String videoTitle) {
        this.videoTitle.setText(videoTitle);
    }

    private void setVideoDescription(String videoDescription) {
        this.videoDescription.setText(videoDescription);
    }

    private void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    private void setStartAutoPlay(boolean b) {
        this.startAutoPlay = b;
    }

    private static class LoadVideo extends NetworkUser<UUID, Void, Video> {

        public LoadVideo(Activity activity) {
            super(activity);
        }

        @Override
        protected Video doInBackground(UUID... uuids) {
            if (!isInternetConnectionAvailable()) {
                return null;
            }

            Video videoByID = ClientApp.getIns().getServerRequests().getVideoByID(uuids[0]);

            ClientApp.getIns().getServerRequests().addView(videoByID.getVideoID(),
                    ClientApp.getIns().getLoginHandler().getCurrentActiveConnection());

            return videoByID;
        }

        @Override
        protected void onPostExecute(Video video) {

            if (isContextPresent()) {

                if (video == null) {
                    finishActivity();

                    return;
                }

                WatchVideo activity = (WatchVideo) getContextIfPresent();

                activity.setLive(video.isLive());
                activity.setVideoTitle(video.getTitle());
                activity.setVideoDescription(video.getDescription());
                activity.setVideoURL(video.getLink());
                activity.setStartAutoPlay(true);

                activity.initPlayer();

            }
        }
    }

}
