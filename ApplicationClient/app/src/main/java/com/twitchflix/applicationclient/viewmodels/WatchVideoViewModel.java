package com.twitchflix.applicationclient.viewmodels;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Pair;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.android.exoplayer2.*;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.twitchflix.applicationclient.ClientApp;
import com.twitchflix.applicationclient.rest.models.Video;
import com.twitchflix.applicationclient.utils.daos.UserDAO;
import com.twitchflix.applicationclient.utils.loaders.NetworkUser;
import com.twitchflix.applicationclient.utils.loaders.VideoDataLoader;

import java.util.UUID;

public class WatchVideoViewModel extends AndroidViewModel {

    private UUID videoID;

    private DataSource.Factory dataSource;

    private MediaSource mediaSource;

    private MutableLiveData<SimpleExoPlayer> exoPlayer;

    private MutableLiveData<Video> videoToPlay;

    private MutableLiveData<Bitmap> channelThumbnail;

    private MutableLiveData<String> channelName;

    public WatchVideoViewModel(@NonNull Application application) {
        super(application);

        exoPlayer = new MutableLiveData<>();
        videoToPlay = new MutableLiveData<>();
        channelThumbnail = new MutableLiveData<>();
        channelName = new MutableLiveData<>();
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        release();
    }

    public void playVideo(UUID videoID) {
        if (this.videoID != null && this.videoID.equals(videoID)) {
            //Video is already being played (State change)
            return;
        }

        this.videoID = videoID;

        new LoadVideo(getApplication().getApplicationContext(),
                this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, videoID);
    }

    public void release() {
        if (this.getPlayer().getValue() != null)
            this.getPlayer().getValue().release();
    }


    private void setVideo(Video toPlay) {
        this.videoToPlay.setValue(toPlay);

        initializePlayer();
    }

    private void setVideoUploader(UserDAO uploader) {
        this.channelThumbnail.setValue(uploader.getChannelThumbnail());
        this.channelName.setValue(uploader.getFullName());
    }

    private void initializePlayer() {
        Video toPlay = this.videoToPlay.getValue();

        if (toPlay.isLive()) {
            initializeLivestreamPlayer();
        } else {
            initializeVideoPlayer();
        }

        addPlayerListener(getPlayer().getValue());

    }

    public void pauseVideo() {
        if (getPlayer().getValue() != null)
            getPlayer().getValue().setPlayWhenReady(false);
    }

    public void playVideo() {
        if (getPlayer().getValue() != null)
            getPlayer().getValue().setPlayWhenReady(true);
    }

    private void initializeVideoPlayer() {

        Context context = getApplication().getApplicationContext();

        SimpleExoPlayer simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(context);

        dataSource = new DefaultHttpDataSourceFactory(Util.getUserAgent(context, "TwitchFlix"));

//        mediaSource = new ProgressiveMediaSource(Uri.parse(videoToPlay.getValue().getLink()),
//                dataSource, new DefaultExtractorsFactory(), null, null));

        mediaSource = new ExtractorMediaSource(Uri.parse(videoToPlay.getValue().getLink()),
                dataSource, new DefaultExtractorsFactory(), null, null);

        simpleExoPlayer.prepare(mediaSource);

        simpleExoPlayer.setPlayWhenReady(true);

        this.exoPlayer.setValue(simpleExoPlayer);
    }

    private void initializeLivestreamPlayer() {

        Context context = getApplication().getApplicationContext();

        Video toPlay = this.videoToPlay.getValue();

        SimpleExoPlayer simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(context);

        dataSource = new DefaultHttpDataSourceFactory(
                Util.getUserAgent(context, "TwitchFlix"));

        mediaSource = new HlsMediaSource.Factory(dataSource).createMediaSource(Uri.parse(toPlay.getLink()));

        simpleExoPlayer.prepare(mediaSource);

        simpleExoPlayer.setPlayWhenReady(true);

        this.exoPlayer.setValue(simpleExoPlayer);
    }

    private void addPlayerListener(SimpleExoPlayer player) {
        player.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {
            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == ExoPlayer.STATE_ENDED) {
                    player.seekTo(0);
                    player.setPlayWhenReady(false);
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {
            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
            }

            @Override
            public void onPositionDiscontinuity(int reason) {
            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            }

            @Override
            public void onSeekProcessed() {
            }
        });

    }

    public LiveData<Video> getVideo() {
        return this.videoToPlay;
    }

    public LiveData<SimpleExoPlayer> getPlayer() {
        return this.exoPlayer;
    }

    public LiveData<String> getChannelName() {
        return this.channelName;
    }

    public LiveData<Bitmap> getChannelThumbnail() {
        return this.channelThumbnail;
    }

    private static class LoadVideo extends NetworkUser<UUID, Void, Pair<Video, UserDAO>>
            implements VideoDataLoader {

        private WatchVideoViewModel videoModel;

        public LoadVideo(Context context, WatchVideoViewModel video) {
            super(context);

            this.videoModel = video;
        }

        @Override
        protected Pair<Video, UserDAO> doInBackground(UUID... uuids) {

            ClientApp.getIns().getServerRequests().addView(uuids[0],
                    ClientApp.getIns().getLoginHandler().getCurrentActiveConnection());

            Video video = ClientApp.getIns().getServerRequests().getVideoByID(uuids[0]);

            UserDAO userData = getUserData(video.getUploader());

            return new Pair<>(video, userData);
        }

        @Override
        protected void onPostExecute(Pair<Video, UserDAO> video) {
            this.videoModel.setVideo(video.first);
            this.videoModel.setVideoUploader(video.second);
        }
    }

}
