package com.twitchflix.filesystem;

import com.google.api.client.json.GenericJson;

import java.io.File;

public interface FileManager {

    File getFile(String pathFromJar);

    File getFileAndCreate(String pathFromJar);

    boolean checkIfExistsAndCreate(File file);

    GenericJson readFile(File file);

}
