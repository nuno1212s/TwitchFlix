package com.twitchflix.applicationclient.viewmodels;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.twitchflix.applicationclient.ClientApp;
import com.twitchflix.applicationclient.authentication.ActiveConnection;
import com.twitchflix.applicationclient.rest.models.UserData;
import com.twitchflix.applicationclient.utils.daos.UserDAO;
import com.twitchflix.applicationclient.utils.loaders.NetworkUser;
import com.twitchflix.applicationclient.utils.loaders.UserDataLoader;

import java.io.IOException;

public class ChoosePictureViewModel extends AndroidViewModel {

    private MutableLiveData<Bitmap> userPhoto;

    public ChoosePictureViewModel(@NonNull Application application) {
        super(application);

        userPhoto = new MutableLiveData<>();
        loadUserPhoto();
    }

    private void loadUserPhoto() {
        UserData user = ClientApp.getIns().getLoginHandler().getCurrentUserData();

        new LoadUserData(getApplication().getApplicationContext(),
                this.userPhoto).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, user);
    }

    public void setUserPhoto(Bitmap photo) {
        this.userPhoto.setValue(photo);
    }

    public void handleChoosePhoto(Uri photoURI) {

        new LoadPhotoFromFile(getApplication().getApplicationContext(), this)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, photoURI);

    }

    public LiveData<Bitmap> getUserPhoto() {
        return this.userPhoto;
    }

    public void postResults() {

        new SaveUserData(getApplication().getApplicationContext())
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this.userPhoto.getValue());

    }

    private static class LoadUserData extends NetworkUser<UserData, Void, UserDAO>
            implements UserDataLoader {

        private MutableLiveData<Bitmap> userPhoto;

        public LoadUserData(Context context, MutableLiveData<Bitmap> userPhoto) {
            super(context);

            this.userPhoto = userPhoto;
        }

        @Override
        protected UserDAO doInBackground(UserData... userData) {
            return getUserData(userData[0]);
        }

        @Override
        protected void onPostExecute(UserDAO userDAO) {
            super.onPostExecute(userDAO);

            userPhoto.setValue(userDAO.getChannelThumbnail());
        }
    }

    private static class SaveUserData extends NetworkUser<Bitmap, Void, Void>
            implements UserDataLoader {

        public SaveUserData(Context context) {
            super(context);
        }

        @Override
        protected Void doInBackground(Bitmap... userData) {

            ActiveConnection currentActiveConnection = ClientApp.getIns().getLoginHandler().getCurrentActiveConnection();

            updateUserData(currentActiveConnection.getOwner(), userData[0]);

            ClientApp.getIns().getUserDataRequests().updateUserPhoto(currentActiveConnection, userData[0]);

            return null;
        }
    }

    private static class LoadPhotoFromFile extends NetworkUser<Uri, Void, Bitmap> {

        private ChoosePictureViewModel viewModel;

        public LoadPhotoFromFile(Context context, ChoosePictureViewModel model) {
            super(context);

            this.viewModel = model;
        }

        @Override
        protected Bitmap doInBackground(Uri... uris) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(viewModel.getApplication().getContentResolver(), uris[0]);

                return Bitmap.createScaledBitmap(bitmap, 512,
                        512, true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            this.viewModel.setUserPhoto(bitmap);
        }
    }

}
