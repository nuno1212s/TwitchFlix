package com.twitchflix.applicationclient.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import com.twitchflix.applicationclient.R;
import com.twitchflix.applicationclient.utils.daos.VideoDAO;
import com.twitchflix.applicationclient.viewmodels.EditVideoViewModel;

import java.util.UUID;

public class EditVideoActivity extends AppCompatActivity {

    private static final String VIDEO = "videoDAO";

    public static void startActivity(Context origin, UUID video) {

        Intent intent = new Intent(origin, EditVideoActivity.class);

        intent.putExtra(VIDEO, video.toString());

        origin.startActivity(intent);
    }

    private ImageView videoThumbnailView;
    private EditText videoTitleEdit;
    private EditText videoDescriptionEdit;

    private EditVideoViewModel editVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_video);

        this.editVideo = ViewModelProviders.of(this).get(EditVideoViewModel.class);

        videoThumbnailView = findViewById(R.id.videoThumbnail);
        videoTitleEdit = findViewById(R.id.videoTitle);
        videoDescriptionEdit = findViewById(R.id.videoDescription);

        Intent intent = getIntent();

        this.editVideo.getVideoThumbnail().observe(this,
                (videoThumbnail) -> this.videoThumbnailView.setImageBitmap(videoThumbnail));

        this.editVideo.getVideoTitle().observe(this,
                (videoTitle) -> this.videoTitleEdit.setHint(videoTitle));

        this.editVideo.getVideoDescription().observe(this,
                (videoDescription) -> this.videoDescriptionEdit.setHint(videoDescription));

        if (intent != null) {
            UUID videoDAO = UUID.fromString(intent.getStringExtra(VIDEO));

            this.editVideo.setCurrentVideo(videoDAO);
        }

    }

    public void finishEdit(View view) {

        Editable newVideoTitle = this.videoTitleEdit.getText(),
                newVideoDescription = this.videoDescriptionEdit.getText();

        String videoTitle = this.editVideo.getVideoTitle().getValue(),
                videoDescription = this.editVideo.getVideoDescription().getValue();

        if (newVideoTitle.length() > 0) {

            videoTitle = newVideoTitle.toString();

        }

        if (newVideoDescription.length() > 0) {

            videoDescription = newVideoDescription.toString();

        }

        this.editVideo.postEdit(videoTitle, videoDescription);
    }

}
