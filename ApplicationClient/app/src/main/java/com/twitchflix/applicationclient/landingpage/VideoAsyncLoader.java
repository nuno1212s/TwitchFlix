package com.twitchflix.applicationclient.landingpage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;
import com.twitchflix.applicationclient.ClientApp;
import com.twitchflix.applicationclient.rest.models.UserData;
import com.twitchflix.applicationclient.rest.models.UserVideo;
import com.twitchflix.applicationclient.rest.models.Video;
import com.twitchflix.applicationclient.utils.NetworkUserLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

public class VideoAsyncLoader extends NetworkUserLoader<Map<UUID, List<LandingPage.VideoDAO>>> {

    private Map<UUID, List<LandingPage.VideoDAO>> videos;

    private Map<UUID, String> userNames;

    public VideoAsyncLoader(@NonNull Context context) {
        super(context);

        userNames = new HashMap<>();
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        if (videos != null) {
            deliverResult(this.videos);
        } else {
            loadInBackground();
        }
    }

    @Nullable
    @Override
    public Map<UUID, List<LandingPage.VideoDAO>> loadInBackground() {

        this.videos = new HashMap<>();

        List<UserVideo> videos = ClientApp.getIns().getServerRequests()
                .getLandingPage(ClientApp.getIns().getLoginHandler().getCurrentActiveConnection());

        for (UserVideo video : videos) {

            String userName = getUsername(video.getUploader());

            Bitmap thumbnail = getThumbnail(video);

            LandingPage.VideoDAO videoDAO =
                    LandingPage.VideoDAO.fromData(thumbnail, video.getTitle(), video.getDescription(), userName, video.getVideoID());

            List<LandingPage.VideoDAO> videosByUser = this.videos.get(video.getUploader());

            if (videosByUser == null) {
                videosByUser = new ArrayList<>();
            }

            videosByUser.add(videoDAO);

            this.videos.put(video.getUploader(), videosByUser);
        }

        return this.videos;
    }

    private String getUsername(UUID userID) {

        if (userNames.containsKey(userID)) {
            return userNames.get(userID);
        }

        UserData userData = ClientApp.getIns().getUserDataRequests().requestUserData(userID);

        String userName = userData.getFirstName() + " " + userData.getLastName();

        this.userNames.put(userID, userName);

        return userName;
    }

    private Bitmap getThumbnail(Video video) {
        try {
            InputStream inputStream = new URL(video.getThumbnailLink()).openStream();

            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
