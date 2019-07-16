package com.twitchflix.applicationclient.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pedro.encoder.input.video.CameraHelper;
import com.pedro.rtplibrary.rtmp.RtmpCamera1;
import com.twitchflix.applicationclient.ClientApp;
import com.twitchflix.applicationclient.R;
import com.twitchflix.applicationclient.landingpage.LandingPage;
import com.twitchflix.applicationclient.rest.models.VideoStream;
import com.twitchflix.applicationclient.utils.loaders.NetworkUser;
import net.ossrs.rtmp.ConnectCheckerRtmp;

public class Stream extends AppCompatActivity implements ConnectCheckerRtmp, SurfaceHolder.Callback {

    private static final String STREAMING = "streaming";
    private static final String STREAM_URL = "streamurl";
    private static final String STREAM_TITLE = "title";
    private static final String STREAM_DESC = "description";
    private static final String FACING = "facing";

    private EditText streamTitle;

    private FloatingActionButton startStreamButton;

    private ProgressBar startStreamProgressBar;

    private String streamURL, streamTitleText, streamDescriptionText;

    private boolean streaming = false;

    private RtmpCamera1 camera;

    private CameraHelper.Facing cameraFacing = CameraHelper.Facing.FRONT;

    private SurfaceView cameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream);

        startStreamButton = findViewById(R.id.startStreamButton);

        streamTitle = findViewById(R.id.editStreamTitle);

        this.cameraView = findViewById(R.id.camera_view);

        this.startStreamProgressBar = findViewById(R.id.startStreamProgress);

        this.camera = new RtmpCamera1(this.cameraView, this);

        cameraView.getHolder().addCallback(this);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {

            this.streaming = extras.getBoolean(STREAMING);
            this.streamURL = extras.getString(STREAM_URL);
            this.streamTitleText = extras.getString(STREAM_TITLE);
            this.streamDescriptionText = extras.getString(STREAM_DESC);
            this.cameraFacing = CameraHelper.Facing.valueOf(extras.getString(FACING));

        }
    }

    private boolean checkStreamStartRequirements() {

        if (streamTitle.getText().length() == 0) {
            return false;
        }

        this.streamTitleText = this.streamTitle.getText().toString();
        this.streamDescriptionText = streamTitleText;

        return true;
    }

    public void onClickStream(View view) {

        if (!streaming) {

            if (checkStreamStartRequirements()) {

                //Here we will initiate the stream
                this.startStreamProgressBar.setVisibility(View.VISIBLE);
                this.startStreamButton.setVisibility(View.GONE);
                this.startStreamButton.setImageResource(R.drawable.ic_lens_gray_24dp);

                if (this.streamTitle.isFocused()) {
                    this.streamTitle.clearFocus();
                }

                if (streamURL == null) {
                    new RequestStreamLink(this)
                            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, streamTitleText, streamDescriptionText);
                } else {
                    startStream();
                }
            } else {
                Toast.makeText(this, "Stream must have a title", Toast.LENGTH_SHORT).show();
            }

        } else {
            stopStream();
        }

    }

    public void startStream() {

        if (streamURL != null) {

            streaming = true;

            this.startStreamProgressBar.setVisibility(View.GONE);
            this.startStreamButton.setVisibility(View.VISIBLE);

            if (camera.prepareVideo() && camera.prepareAudio()) {
                camera.startStream(streamURL);
            }
        }
    }

    public void stopStream() {
        this.streamURL = null;
        this.streamDescriptionText = null;
        this.streamTitleText = null;
        streaming = false;

        if (camera.isStreaming()) {
            camera.stopStream();
        }

        this.startStreamButton.setImageResource(R.drawable.ic_lens_red_24dp);

    }

    public void switchCamera(View view) {
        this.camera.switchCamera();

        this.cameraFacing = cameraFacing == CameraHelper.Facing.BACK ? CameraHelper.Facing.FRONT : CameraHelper.Facing.BACK;
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {

        outState.putString(FACING, this.cameraFacing.name());
        outState.putString(STREAM_URL, streamURL);
        outState.putString(STREAM_DESC, streamDescriptionText);
        outState.putString(STREAM_TITLE, streamTitleText);
        outState.putBoolean(STREAMING, streaming);

        super.onSaveInstanceState(outState, outPersistentState);
    }

    public void goBack(View view) {

        if (streaming) {
            stopStream();
        }

        camera.stopPreview();

        Intent intent = new Intent(this, LandingPage.class);

        startActivity(intent);

        finish();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (this.camera.isStreaming()) {
            this.camera.stopStream();
        }

        this.camera.stopPreview();

        finish();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera.startPreview(cameraFacing);

        if (streamURL != null) {

            if (camera.prepareVideo() && camera.prepareAudio()) {
                camera.startStream(streamURL);
            }

        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        if (camera.isStreaming()) {
            camera.stopStream();
        }

        camera.stopPreview();
    }

    @Override
    public void onConnectionSuccessRtmp() {

    }

    @Override
    public void onConnectionFailedRtmp(String reason) {

        runOnUiThread(() -> {
            camera.stopStream();

            Toast.makeText(Stream.this, "Failed to start stream", Toast.LENGTH_SHORT).show();
        });

    }

    @Override
    public void onDisconnectRtmp() {

    }

    @Override
    public void onAuthErrorRtmp() {

    }

    @Override
    public void onAuthSuccessRtmp() {

    }

    private void setStreamURL(String streamURL) {
        this.streamURL = streamURL;
    }


    private static class RequestStreamLink extends NetworkUser<String, Void, VideoStream> {

        RequestStreamLink(Stream options) {
            super(options);
        }

        @Override
        protected VideoStream doInBackground(String... args) {
            if (!isInternetConnectionAvailable()) {
                return null;
            }

            return ClientApp.getIns().getServerRequests().requestVideoStream(args[0],
                    args[1],
                    ClientApp.getIns().getLoginHandler().getCurrentActiveConnection());
        }

        @Override
        protected void onPostExecute(VideoStream videoStream) {

            if (isContextPresent() && videoStream != null) {

                Stream stream = (Stream) getContextIfPresent();

                stream.setStreamURL(videoStream.getStreamLink());

                stream.startStream();
            }
        }
    }

}
