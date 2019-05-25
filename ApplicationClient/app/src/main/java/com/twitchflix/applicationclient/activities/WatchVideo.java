package com.twitchflix.applicationclient.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.twitchflix.applicationclient.ClientApp;
import com.twitchflix.applicationclient.R;
import com.twitchflix.applicationclient.rest.models.Video;

import java.lang.ref.WeakReference;
import java.util.UUID;

public class WatchVideo extends AppCompatActivity {

    private static final String VIDEO_ID = "videoID";

    public static void start(Context context, UUID videoId) {

        Intent intent = new Intent(context, WatchVideo.class);

        intent.putExtra(VIDEO_ID, videoId);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_video);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            UUID videoId = (UUID) extras.get(VIDEO_ID);

            new LoadVideo(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                    videoId);
        }

    }

    private static class LoadVideo extends AsyncTask<UUID, Void, Video> {

        private WeakReference<Activity> activity;

        public LoadVideo(Activity activity) {

            this.activity = new WeakReference<>(activity);

        }

        @Override
        protected Video doInBackground(UUID... uuids) {
            Video videoByID = ClientApp.getIns().getServerRequests().getVideoByID(uuids[0]);

            ClientApp.getIns().getServerRequests().addView(videoByID.getVideoID(), ClientApp.getIns().getCurrentActiveAccount());

            return videoByID;
        }

        @Override
        protected void onPostExecute(Video video) {

            Activity activity = this.activity.get();

            if (activity != null) {

                if (video == null) {
                    activity.finish();

                    return;
                }

                PlayerView videoPlayer = activity.findViewById(R.id.video_player);

                DataSource.Factory dataSource = new DefaultHttpDataSourceFactory(Util.getUserAgent(activity, "TwitchFlix"));

                HlsMediaSource source = new HlsMediaSource.Factory(dataSource).createMediaSource(Uri.parse(video.getLink()));

                SimpleExoPlayer simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(activity);

                simpleExoPlayer.prepare(source);

                videoPlayer.setPlayer(simpleExoPlayer);

                TextView title = activity.findViewById(R.id.video_title),
                        desc = activity.findViewById(R.id.video_description);

                title.setText(video.getTitle());
                desc.setText(video.getDescription());
            }

        }
    }

}
