package com.twitchflix.applicationclient.datastorage;

import android.content.Context;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.util.UUID;

public class FileStorage implements InformationStorage {

    private static final String fileName = "datastorage.json";

    private Context context;

    public FileStorage(Context context) {
        this.context = context;
    }

    @Override
    public boolean isUserLoggedIn() {

        return new File(context.getFilesDir(), fileName).exists();

    }

    @Override
    public void setUserLogin(UserLogin loginData) {

        try (FileOutputStream out = context.openFileOutput(fileName, Context.MODE_PRIVATE);
             OutputStreamWriter writer = new OutputStreamWriter(out);
             BufferedWriter outputStream = new BufferedWriter(writer)) {

            JSONObject userData = new JSONObject();

            userData.put("UUID", loginData.getUserID().toString());

            userData.put("UserName", loginData.getEmail());

            userData.put("Token", loginData.getToken());

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
                    .setUserID(UUID.fromString(jsonObject.getString("UUID")))
                    .setEmail(jsonObject.getString("UserName"))
                    .setAccessToken(jsonObject.getString("AccessToken"))
                    .setToken(jsonObject.getString("Token"))
                    .createUserLogin();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void deleteUserLogin() {

        context.deleteFile(fileName);

    }


    private JSONObject readFileAndReturn() {

        try (FileInputStream stream = context.openFileInput(fileName);
             InputStreamReader reader = new InputStreamReader(stream);
             BufferedReader inputStream = new BufferedReader(reader)) {

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
