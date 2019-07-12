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
import com.twitchflix.applicationclient.rest.models.UserVideo;
import com.twitchflix.applicationclient.utils.NetworkUser;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LandingPageViewModel extends AndroidViewModel {

    private MutableLiveData<List<VideoDAO>> videos;

    private MutableLiveData<List<VideoDAO>> searchVideos;

    private String queryString;

    public LandingPageViewModel(@NonNull Application application) {
        super(application);

        videos = new MutableLiveData<>();
        searchVideos = new MutableLiveData<>();
    }

    public void setQueryString(String query) {
        this.queryString = query;

        runSearchQuery();
    }

    public void requestRefresh() {
        runVideoLoad();
    }

    private void runVideoLoad() {
        new VideoLoader(this.getApplication().getApplicationContext(),
                this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void runSearchQuery() {
        new SearchQueryLoader(this.getApplication().getApplicationContext(),
                this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this.queryString);
    }

    private void setLoadedVideos(List<VideoDAO> videos) {
        this.videos.setValue(videos);
    }

    private void setSearchVideos(List<VideoDAO> searchVideos) {
        this.searchVideos.setValue(searchVideos);
    }

    public LiveData<List<VideoDAO>> getSearchQueryVideos() {
        return searchVideos;
    }

    public LiveData<List<VideoDAO>> getLandingPageVideos() {
        return videos;
    }

    private static class VideoLoader extends NetworkUser<Void, Void, List<VideoDAO>>
            implements VideoThumbnailLoader {

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

    private static class SearchQueryLoader extends NetworkUser<String, Void, List<VideoDAO>>
            implements VideoThumbnailLoader {

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

    public static class VideoDAO {

        private UUID videoID, uploader;

        private String title, description, uploaderName;

        private Bitmap thumbnail;

        private VideoDAO(UUID videoID, UUID uploader, String title, String description, String uploaderName, Bitmap thumbnail) {
            this.videoID = videoID;
            this.uploader = uploader;
            this.title = title;
            this.description = description;
            this.uploaderName = uploaderName;
            this.thumbnail = thumbnail;
        }

        public static VideoDAO fromData(UUID videoID, UUID uploader, String title, String description, String uploaderName, Bitmap thumbnail) {
            return new VideoDAO(videoID, uploader, title, description, uploaderName, thumbnail);
        }

        public UUID getVideoID() {
            return videoID;
        }

        public UUID getUploader() {
            return uploader;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getUploaderName() {
            return uploaderName;
        }

        public Bitmap getThumbnail() {
            return thumbnail;
        }
    }


}
