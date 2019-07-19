package com.twitchflix.applicationclient.viewmodels;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.twitchflix.applicationclient.ClientApp;
import com.twitchflix.applicationclient.rest.models.UserVideo;
import com.twitchflix.applicationclient.utils.daos.UserDAO;
import com.twitchflix.applicationclient.utils.daos.VideoDAO;
import com.twitchflix.applicationclient.utils.loaders.NetworkUser;
import com.twitchflix.applicationclient.utils.loaders.UserDataLoader;
import com.twitchflix.applicationclient.utils.loaders.VideoDataLoader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class ChannelViewerModel extends AndroidViewModel {

    private MutableLiveData<List<VideoDAO>> channelVideos;

    private MutableLiveData<UserDAO> userInfo;

    private UUID channelID;

    public ChannelViewerModel(@NonNull Application application) {
        super(application);

        channelVideos = new MutableLiveData<>();
        userInfo = new MutableLiveData<>();
    }

    public LiveData<List<VideoDAO>> getChannelVideos() {
        return this.channelVideos;
    }

    public LiveData<UserDAO> getUser() {
        return this.userInfo;
    }

    public void setChannelID(UUID channelID) {
        this.channelID = channelID;

        loadUser();
        requestRefresh();
    }

    private void loadUser() {
        new LoadUser(getApplication().getApplicationContext(), this.userInfo)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this.channelID);
    }

    public void requestRefresh() {
        new LoadChannelVideos(getApplication().getApplicationContext(), channelVideos)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, channelID);
    }

    public void deleteVideo(UUID videoID) {
        List<VideoDAO> videos = this.channelVideos.getValue();

        Iterator<VideoDAO> iterator = videos.iterator();

        while (iterator.hasNext()) {

            VideoDAO video = iterator.next();

            if (video.getVideoID().equals(videoID)) {
                iterator.remove();
            }

        }

        this.channelVideos.setValue(videos);

        new DeleteVideo(getApplication().getApplicationContext())
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, videoID);

    }

    private static class DeleteVideo extends NetworkUser<UUID, Void, Void> {

        public DeleteVideo(Context context) {
            super(context);
        }

        @Override
        protected Void doInBackground(UUID... uuids) {

            ClientApp.getIns().getServerRequests().deleteVideo(uuids[0], ClientApp.getIns().getLoginHandler().getCurrentActiveConnection());

            return null;
        }
    }

    private static class LoadUser extends NetworkUser<UUID, Void, UserDAO> implements UserDataLoader {

        private MutableLiveData<UserDAO> userData;

        public LoadUser(Context context, MutableLiveData<UserDAO> userData) {
            super(context);

            this.userData = userData;
        }

        @Override
        protected UserDAO doInBackground(UUID... uuids) {

            UUID uuid = uuids[0];

            return getUserData(uuid);
        }

        @Override
        protected void onPostExecute(UserDAO userDAO) {
            super.onPostExecute(userDAO);

            this.userData.setValue(userDAO);
        }
    }

    private static class LoadChannelVideos extends NetworkUser<UUID, Void, List<VideoDAO>> implements VideoDataLoader {

        private MutableLiveData<List<VideoDAO>> data;

        public LoadChannelVideos(Context context, MutableLiveData<List<VideoDAO>> videos) {
            super(context);

            this.data = videos;
        }

        @Override
        protected List<VideoDAO> doInBackground(UUID... uuids) {

            if (!isInternetConnectionAvailable()) {
                return null;
            }

            UUID channelID = uuids[0];

            List<UserVideo> videosByUser = ClientApp.getIns().getServerRequests().getVideosByUser(channelID);

            List<VideoDAO> videos = new ArrayList<>(videosByUser.size());

            for (UserVideo userVideo : videosByUser) {
                videos.add(createVideoDAO(userVideo));
            }

            return videos;
        }

        @Override
        protected void onPostExecute(List<VideoDAO> videoDAOS) {
            super.onPostExecute(videoDAOS);

            if (videoDAOS == null) {
                return;
            }

            data.setValue(videoDAOS);
        }
    }

}

