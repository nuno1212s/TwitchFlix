package com.twitchflix.applicationclient.viewmodels;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.twitchflix.applicationclient.ClientApp;
import com.twitchflix.applicationclient.rest.models.EditVideo;
import com.twitchflix.applicationclient.rest.models.Video;
import com.twitchflix.applicationclient.utils.daos.VideoDAO;
import com.twitchflix.applicationclient.utils.loaders.NetworkUser;
import com.twitchflix.applicationclient.utils.loaders.VideoDataLoader;

import java.util.UUID;

public class EditVideoViewModel extends AndroidViewModel {

    private UUID currentVideoID;

    private MutableLiveData<String> videoTitle, videoDescription;

    private MutableLiveData<Bitmap> videoThumbnail;

    public EditVideoViewModel(@NonNull Application application) {
        super(application);

        videoTitle = new MutableLiveData<>();
        videoDescription = new MutableLiveData<>();
        videoThumbnail = new MutableLiveData<>();
    }

    public void setCurrentVideo(UUID video) {

        this.currentVideoID = video;

        new LoadVideo(getApplication().getApplicationContext(), this)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, video);
    }

    private void setVideoData(VideoDAO video) {

        this.videoTitle.setValue(video.getTitle());
        this.videoDescription.setValue(video.getDescription());
        this.videoThumbnail.setValue(video.getThumbnail());

    }

    public LiveData<String> getVideoTitle() {
        return videoTitle;
    }

    public LiveData<String> getVideoDescription() {
        return videoDescription;
    }

    public LiveData<Bitmap> getVideoThumbnail() {
        return this.videoThumbnail;
    }

    public void postEdit(String title, String description) {

        this.videoTitle.setValue(title);
        this.videoDescription.setValue(description);

        EditVideo editVideo = EditVideo.fromData(currentVideoID, title, description);

        new PostEdit(getApplication().getApplicationContext())
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, editVideo);
    }

    private static class LoadVideo extends NetworkUser<UUID, Void, VideoDAO> implements VideoDataLoader {

        EditVideoViewModel model;

        public LoadVideo(Context context, EditVideoViewModel model) {
            super(context);

            this.model = model;
        }

        @Override
        protected VideoDAO doInBackground(UUID... uuids) {

            UUID videoID = uuids[0];

            Video videoByID = ClientApp.getIns().getServerRequests().getVideoByID(videoID);

            return createVideoDAO(videoByID);
        }

        @Override
        protected void onPostExecute(VideoDAO videoDAO) {
            model.setVideoData(videoDAO);
        }
    }

    private static class PostEdit extends NetworkUser<EditVideo, Void, Void> {

        public PostEdit(Context context) {
            super(context);
        }

        @Override
        protected Void doInBackground(EditVideo... editVideos) {

            ClientApp.getIns().getServerRequests().editVideo(editVideos[0]);

            return null;
        }
    }

}
