package com.twitchflix.filesystem;

import com.google.api.client.json.GenericJson;

import java.io.File;

public interface FileManager {

    /**
     * Get the file
     * @param pathFromJar
     * @return
     */
    File getFile(String pathFromJar);

    /**
     * Get the file and create it if it does not already exist
     * @param pathFromJar
     * @return
     */
    File getFileAndCreate(String pathFromJar);

    /**
     * Get the file and create it from the resource if it does not already exist
     * @param pathFromJar
     * @return
     */
    File getFileFromResource(String pathFromJar);

    /**
     * Check if the file exists, if not create it
     * @param file
     * @return
     */
    boolean checkIfExistsAndCreate(File file);

    /**
     * Read a file into JSON
     * @param file
     * @return
     */
    GenericJson readFile(File file);

}
