package com.twitchflix.applicationclient.datastorage;

import android.app.Activity;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;

public class FileStorage implements InformationStorage {

    private static final String fileName = "datastorage.json";

    private final File cacheFile;

    public FileStorage(Activity a) {
        this.cacheFile = new File(a.getCacheDir(), fileName);
    }

    @Override
    public boolean isUserLoggedIn() {

        return false;
//        return this.cacheFile.exists();

    }

    @Override
    public void setUserLogin(UserLogin loginData) {

        try (BufferedWriter outputStream = new BufferedWriter(new FileWriter(getCacheFileOrCreate()))) {

            JSONObject userData = new JSONObject();

            userData.put("UserName", loginData.getUserName());

            userData.put("Password", loginData.getHashedPassword());

            userData.put("AccessToken", loginData.getAccessToken());

            outputStream.write(userData.toString());

            outputStream.flush();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public UserLogin getCurrentLogin() {

        if (!this.isUserLoggedIn()) {
            return null;
        }

        JSONObject jsonObject = readFileAndReturn();

        try {
            return new UserLogin.UserLoginBuilder()
                    .setUserName(jsonObject.getString("UserName"))
                    .setAccessToken(jsonObject.getString("AccessToken"))
                    .setHashedPassword(jsonObject.getString("Password"))
                    .createUserLogin();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void deleteUserLogin() {
        this.cacheFile.delete();
    }

    private File getCacheFileOrCreate() throws IOException {

        if (!this.cacheFile.exists()) {
            this.cacheFile.createNewFile();
        }

        return cacheFile;
    }

    private JSONObject readFileAndReturn() {

        if (!this.cacheFile.exists()) {
            return null;
        }

        try (BufferedReader inputStream = new BufferedReader(new FileReader(getCacheFileOrCreate()))) {

            StringBuilder builder = new StringBuilder();

            String buffer;

            while ((buffer = inputStream.readLine()) != null) {
                builder.append(buffer);
            }

            return (JSONObject) new JSONTokener(builder.toString()).nextValue();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
