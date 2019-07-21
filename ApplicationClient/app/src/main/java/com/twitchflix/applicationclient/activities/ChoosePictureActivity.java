package com.twitchflix.applicationclient.activities;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.lifecycle.ViewModelProviders;
import com.twitchflix.applicationclient.ClientApp;
import com.twitchflix.applicationclient.R;
import com.twitchflix.applicationclient.rest.models.UserData;
import com.twitchflix.applicationclient.viewmodels.ChoosePictureViewModel;

import java.io.IOException;

public class ChoosePictureActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 3;

    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    private static final String READ_EXTERNAL_STORAGE_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final String WRITE_EXTERNAL_STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    private ImageView currentPicture;

    private ChoosePictureViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_picture);

        currentPicture = findViewById(R.id.currentPhoto);

        this.viewModel = ViewModelProviders.of(this).get(ChoosePictureViewModel.class);

        this.viewModel.getUserPhoto().observe(this, (picture) -> {
            currentPicture.setImageBitmap(picture);
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {

            if (data == null) {
                return;
            }

            Uri uri = data.getData();

            this.viewModel.handleChoosePhoto(uri);

        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            if (data == null) {
                return;
            }

            Bitmap newThumbnail = data.getExtras().getParcelable("data");

            this.viewModel.setUserPhoto(newThumbnail);
        }

    }

    public void choosePhoto(View view) {
        Intent choosePhoto = new Intent(Intent.ACTION_GET_CONTENT);

        choosePhoto.setType("image/*");

        startActivityForResult(Intent.createChooser(choosePhoto, "Select Picture"), PICK_IMAGE);
    }

    public void takePhoto(View view) {

        Intent takePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePhoto.resolveActivity(getPackageManager()) != null) {

            startActivityForResult(takePhoto, REQUEST_IMAGE_CAPTURE);

        }

    }

    public void saveChanges(View view) {

        this.viewModel.postResults();

        Toast.makeText(this, R.string.saving_user_photo, Toast.LENGTH_LONG).show();

    }

    public void handleBackButton(View view) {

        super.onBackPressed();

    }
}
