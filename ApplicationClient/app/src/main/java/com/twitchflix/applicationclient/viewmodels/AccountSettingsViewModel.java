package com.twitchflix.applicationclient.viewmodels;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.twitchflix.applicationclient.ClientApp;
import com.twitchflix.applicationclient.authentication.ActiveConnection;
import com.twitchflix.applicationclient.utils.daos.UserDAO;
import com.twitchflix.applicationclient.utils.loaders.NetworkUser;
import com.twitchflix.applicationclient.utils.loaders.UserDataLoader;

import java.util.UUID;

public class AccountSettingsViewModel extends AndroidViewModel {

    private MutableLiveData<Bitmap> channelThumbnail;

    private MutableLiveData<String> firstName, lastName;

    private MutableLiveData<Integer> progressBarVisibility;

    public AccountSettingsViewModel(@NonNull Application application) {
        super(application);

        this.channelThumbnail = new MutableLiveData<>();
        this.firstName = new MutableLiveData<>();
        this.lastName = new MutableLiveData<>();
        this.progressBarVisibility = new MutableLiveData<>();
    }

    public void postUserID(UUID userID) {

        new LoadUserData(getApplication().getApplicationContext(),
                this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, userID);

    }

    public LiveData<Bitmap> getChannelThumbnail() {
        return channelThumbnail;
    }

    public LiveData<String> getFirstName() {
        return firstName;
    }

    public LiveData<String> getLastName() {
        return lastName;
    }

    public LiveData<Integer> getVisibility() {
        return progressBarVisibility;
    }

    private void handleUserLoad(UserDAO user) {
        this.channelThumbnail.setValue(user.getChannelThumbnail());

        this.firstName.setValue(user.getFirstName());
        this.lastName.setValue(user.getLastName());
    }

    public void updateAccount(String firstName, String lastName)  {

        new UpdateAccount(getApplication().getApplicationContext(), this.progressBarVisibility)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, firstName, lastName);

    }

    private static class LoadUserData extends NetworkUser<UUID, Void, UserDAO>
            implements UserDataLoader {

        private AccountSettingsViewModel storage;

        public LoadUserData(Context context, AccountSettingsViewModel model) {
            super(context);

            this.storage = model;
        }

        @Override
        protected UserDAO doInBackground(UUID... uuids) {

            UUID userID = uuids[0];

            return getUserData(userID);
        }

        @Override
        protected void onPostExecute(UserDAO userDAO) {
            super.onPostExecute(userDAO);

            storage.handleUserLoad(userDAO);
        }
    }

    private static class UpdateAccount extends NetworkUser<String, Void, Void> {

        private MutableLiveData<Integer> progressBarVisibility;

        public UpdateAccount(Context context, MutableLiveData<Integer> progressBarVisibility) {
            super(context);

            this.progressBarVisibility = progressBarVisibility;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressBarVisibility.setValue(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... strings) {

            String firstName = strings[0],
                    lastName = strings[1];

            ActiveConnection currentConnection = ClientApp.getIns().getLoginHandler().getCurrentActiveConnection();

            if (!firstName.isEmpty()) {
                ClientApp.getIns().getUserDataRequests().updateFirstName(currentConnection,
                        firstName);

                System.out.println(firstName);
            }

            if (!lastName.isEmpty()) {
                ClientApp.getIns().getUserDataRequests().updateLastName(currentConnection, lastName);

                System.out.println(lastName);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            progressBarVisibility.setValue(View.GONE);
        }
    }

}
