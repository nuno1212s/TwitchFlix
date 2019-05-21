package com.twitchflix.filesystem;

import com.google.api.client.googleapis.util.Utils;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonFactory;
import com.twitchflix.App;

import java.io.*;
import java.net.URISyntaxException;

public class DefaultFileManager implements FileManager {

    @Override
    public File getFile(String pathFromJar) {
        //TODO: Make this work with sub-folders
        return new File(getBaseFilePath(), pathFromJar);
    }

    @Override
    public File getFileAndCreate(String pathFromJar) {

        File file = getFile(pathFromJar);

        if (!checkIfExistsAndCreate(file)) {
            return null;
        }

        return file;
    }

    @Override
    public boolean checkIfExistsAndCreate(File file) {

        if (!file.getParentFile().exists() && file.getParentFile().isDirectory()) {
            file.getParentFile().mkdirs();
        }

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
    public File getFileFromResource(String pathFromJar) {

        File file = getFile(pathFromJar);

        if (!file.exists()) {

            if (checkIfExistsAndCreate(file)) {

                try (InputStream resourceAsStream = App.class.getResourceAsStream(File.separator + pathFromJar);
                     OutputStream stream = new FileOutputStream(file)) {

                    byte[] buffer = new byte[1024];

                    int length;

                    while ((length = resourceAsStream.read(buffer)) > 0) {
                        stream.write(buffer, 0, length);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }

        return file;
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
