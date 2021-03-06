package com.twitchflix.applicationclient.utils.loaders;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.twitchflix.applicationclient.rest.models.UserVideo;
import com.twitchflix.applicationclient.rest.models.Video;
import com.twitchflix.applicationclient.utils.daos.UserDAO;
import com.twitchflix.applicationclient.utils.daos.VideoDAO;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public interface VideoDataLoader extends UserDataLoader {

    Map<UUID, Bitmap> videoThumnails = new HashMap<>();

    default Bitmap getVideoThumbnail(Video video) {

        if (videoThumnails.containsKey(video.getVideoID())) {
            return videoThumnails.get(video.getVideoID());
        }

        try {

            InputStream inputStream = new URL(video.getThumbnailLink()).openStream();

            return BitmapFactory.decodeStream(inputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    default VideoDAO createVideoDAO(Video video) {

        UserDAO userData = getUserData(video.getUploader());

        Bitmap bitmap = getVideoThumbnail(video);

        return VideoDAO.fromData(video.getVideoID(), userData,
                video.getTitle(), video.getDescription(), bitmap);
    }

}
