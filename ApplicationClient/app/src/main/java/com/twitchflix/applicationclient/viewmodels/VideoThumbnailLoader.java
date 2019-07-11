package com.twitchflix.applicationclient.viewmodels;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.twitchflix.applicationclient.rest.models.UserVideo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public interface VideoThumbnailLoader extends UserDataLoader {

    default Bitmap getVideoThumbnail(UserVideo video) {
        try {

            InputStream inputStream = new URL(video.getThumbnailLink()).openStream();

            return BitmapFactory.decodeStream(inputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    default LandingPageViewModel.VideoDAO createVideoDAO(UserVideo video) {

        String userName = getUsername(video.getUploader());

        Bitmap bitmap = getVideoThumbnail(video);

        return LandingPageViewModel.VideoDAO.fromData(video.getVideoID(), video.getUploader(),
                video.getTitle(), video.getDescription(), userName, bitmap);
    }

}
