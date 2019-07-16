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
import java.util.List;
import java.util.UUID;

public class LandingPageViewModel extends AndroidViewModel {

    private MutableLiveData<List<VideoDAO>> videos;

    private MutableLiveData<List<VideoDAO>> searchVideos;

    private MutableLiveData<UserDAO> userPhoto;

    private UUID userID;

    private String queryString;

    public LandingPageViewModel(@NonNull Application application) {
        super(application);

        videos = new MutableLiveData<>();
        searchVideos = new MutableLiveData<>();
        userPhoto = new MutableLiveData<>();
    }

    public void setQueryString(String query) {
        this.queryString = query;

        runSearchQuery();
    }

    public void setUserID(UUID userID) {
        this.userID = userID;

        runPhotoLoad();
    }

    public void requestRefresh() {
        runVideoLoad();
    }

    public void requestLoadUserPhoto() {
        runPhotoLoad();
    }

    private void runVideoLoad() {
        new VideoLoader(this.getApplication().getApplicationContext(),
                this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void runSearchQuery() {
        new SearchQueryLoader(this.getApplication().getApplicationContext(),
                this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this.queryString);
    }

    private void runPhotoLoad() {

        new PhotoLoader(this.getApplication().getApplicationContext(),
                this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this.userID);
    }

    private void setLoadedVideos(List<VideoDAO> videos) {
        this.videos.setValue(videos);
    }

    private void setSearchVideos(List<VideoDAO> searchVideos) {
        this.searchVideos.setValue(searchVideos);
    }

    private void setUserPhoto(UserDAO bitmap) {
        this.userPhoto.setValue(bitmap);
    }

    public LiveData<List<VideoDAO>> getSearchQueryVideos() {
        return searchVideos;
    }

    public LiveData<List<VideoDAO>> getLandingPageVideos() {
        return videos;
    }

    public LiveData<UserDAO> getUserPhoto() {
        return this.userPhoto;
    }

    private static class VideoLoader extends NetworkUser<Void, Void, List<VideoDAO>>
            implements VideoDataLoader {

        private LandingPageViewModel storage;

        public VideoLoader(Context context, LandingPageViewModel storage) {
            super(context);

            this.storage = storage;
        }

        @Override
        protected List<VideoDAO> doInBackground(Void... voids) {

            List<UserVideo> landingPage =
                    ClientApp.getIns().getServerRequests()
                            .getLandingPage(ClientApp.getIns().getLoginHandler().getCurrentActiveConnection());

            List<VideoDAO> landingPageVideos = new ArrayList<>(landingPage.size());

            for (UserVideo userVideo : landingPage) {
                landingPageVideos.add(createVideoDAO(userVideo));
            }

            return landingPageVideos;
        }

        @Override
        protected void onPostExecute(List<VideoDAO> videoDAOS) {
            super.onPostExecute(videoDAOS);

            storage.setLoadedVideos(videoDAOS);
        }
    }

    private static class PhotoLoader extends NetworkUser<UUID, Void, UserDAO>
            implements UserDataLoader {

        private LandingPageViewModel storage;

        public PhotoLoader(Context context, LandingPageViewModel model) {
            super(context);

            this.storage = model;
        }

        @Override
        protected UserDAO doInBackground(UUID... uuids) {
            UUID uuid = uuids[0];

            return getUserData(uuid);
        }

        @Override
        protected void onPostExecute(UserDAO bitmap) {
            super.onPostExecute(bitmap);

            storage.setUserPhoto(bitmap);
        }
    }

    private static class SearchQueryLoader extends NetworkUser<String, Void, List<VideoDAO>>
            implements VideoDataLoader {

        private LandingPageViewModel storage;

        public SearchQueryLoader(Context context, LandingPageViewModel storage) {
            super(context);

            this.storage = storage;
        }


        @Override
        protected List<VideoDAO> doInBackground(String... query) {

            List<UserVideo> videoResults = ClientApp.getIns().getServerRequests()
                    .searchVideo(query[0], ClientApp.getIns().getLoginHandler().getCurrentActiveConnection());

            List<VideoDAO> videos = new ArrayList<>(videoResults.size());

            for (UserVideo videoResult : videoResults) {
                videos.add(createVideoDAO(videoResult));
            }

            return videos;
        }

        @Override
        protected void onPostExecute(List<VideoDAO> videoDAOS) {
            super.onPostExecute(videoDAOS);

            storage.setSearchVideos(videoDAOS);
        }
    }


}
