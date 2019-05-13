package com.twitchflix.filesystem;

import com.google.api.client.googleapis.util.Utils;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonFactory;
import com.twitchflix.App;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;

public class DefaultFileManager implements FileManager {

    @Override
    public File getFile(String pathFromJar) {
        return new File(getBaseFilePath(), pathFromJar);
    }

    @Override
    public File getFileAndCreate(String pathFromJar) {

        File file = new File(getBaseFilePath(), pathFromJar);

        if (checkIfExistsAndCreate(file)) {
            return null;
        }

        return file;
    }

    @Override
    public boolean checkIfExistsAndCreate(File file) {

        if (!file.exists()) {
            try {
                return file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    @Override
    public GenericJson readFile(File file) {

        JsonFactory defaultJsonFactory = Utils.getDefaultJsonFactory();

        try (FileInputStream inputStream = new FileInputStream(file)) {

            return defaultJsonFactory.fromInputStream(inputStream, GenericJson.class);

        } catch (IOException e) {

            e.printStackTrace();

        }

        return null;
    }

    private static File getBaseFilePath() {

        try {
            String path = new File(App.class.getProtectionDomain().getCodeSource().getLocation()
                    .toURI()).getAbsolutePath();
            return new File(path.substring(0, path.lastIndexOf(File.separator)));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return null;

    }
}
