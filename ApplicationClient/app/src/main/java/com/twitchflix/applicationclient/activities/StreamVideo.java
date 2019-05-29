package com.twitchflix.applicationclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.pedro.encoder.input.video.CameraHelper;
import com.pedro.rtplibrary.rtmp.RtmpCamera1;
import com.twitchflix.applicationclient.R;
import com.twitchflix.applicationclient.landingpage.LandingPage;
import net.ossrs.rtmp.ConnectCheckerRtmp;

public class StreamVideo extends AppCompatActivity implements ConnectCheckerRtmp, SurfaceHolder.Callback {

    private String rtmpURL;

    private RtmpCamera1 camera;

    private SurfaceView cameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream_video);

        rtmpURL = getIntent().getExtras().getString("streamLink");

        cameraView = findViewById(R.id.camera_view);

        camera = new RtmpCamera1(cameraView, this);

        cameraView.getHolder().addCallback(this);
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    public void stopStream(View view) {

        if (!camera.isStreaming()) {

            if (camera.prepareVideo() && camera.prepareAudio()) {
                camera.startStream(rtmpURL);
            }
        }

        if (camera != null) {
            camera.stopStream();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }

    @Override
    public void onConnectionSuccessRtmp() {

    }

    @Override
    public void onConnectionFailedRtmp(String reason) {

        runOnUiThread(() -> {

            camera.stopPreview();

            camera.stopStream();

            Intent backToMainScreen = new Intent(this, LandingPage.class);

            startActivity(backToMainScreen);
        });

    }

    @Override
    public void onDisconnectRtmp() {
        runOnUiThread(() -> {
            Intent backToMainScreen = new Intent(this, LandingPage.class);

            startActivity(backToMainScreen);
        });
    }

    @Override
    public void onAuthErrorRtmp() {
    }

    @Override
    public void onAuthSuccessRtmp() {
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        camera.startPreview(CameraHelper.Facing.FRONT);

        if (camera.prepareVideo() && camera.prepareAudio()) {
            camera.startStream(rtmpURL);
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        camera.startPreview(CameraHelper.Facing.FRONT);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        if (camera.isStreaming()) {
            camera.stopStream();
        }

        camera.stopPreview();
    }

    public void switchCamera(View view) {
        camera.switchCamera();
    }
}