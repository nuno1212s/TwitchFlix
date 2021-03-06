package com.twitchflix.loggers;

import com.twitchflix.App;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Logger {

    private static File outputFile;

    private static PrintStream systemOut, logOut, errorOut;

    public Logger() {

        outputFile = App.getFileManager().getFile("log.txt");

        //Output file already exists, zip the old log file and create a new one

        if (outputFile.exists()) {
            File storageFile = App.getFileManager().getFileAndCreate("log-" + getDate() + ".zip");

            try (ZipOutputStream writeStream = new ZipOutputStream(new FileOutputStream(storageFile));
                 //Read the old log file
                 InputStream readStream = new FileInputStream(outputFile)) {

                ZipEntry e = new ZipEntry("log.txt");

                writeStream.putNextEntry(e);

                int length;

                //1KB read buffer
                byte[] buffer = new byte[1024];

                while ((length = readStream.read(buffer)) > 0) {

                    writeStream.write(buffer, 0, length);

                }

                //Delete the contents of the output file
                outputFile.delete();
                outputFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            App.getFileManager().checkIfExistsAndCreate(outputFile);
        }

        systemOut = System.out;
        errorOut = System.err;

        try {
            FileOutputStream fileStream = new FileOutputStream(outputFile);

            logOut = new PrintStream(fileStream, false);

            System.setOut(logOut);

            System.setErr(logOut);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static void log(Object object) {
        log(Level.INFO, object);
    }

    public static void log(Level level, Object string) {


        String logMessage = "[" + getDate() + "] " + "[" + level.getName() + "] " + string.toString();

        logOut.println(logMessage);

        systemOut.println(logMessage);

        App.getAsync().submit(() -> logOut.flush());

    }

    public static void logException(Throwable throwable) {

        throwable.printStackTrace(logOut);

        throwable.printStackTrace(systemOut);

        App.getAsync().submit(() -> logOut.flush());
    }

    private static String getDate() {
        Calendar d = Calendar.getInstance();

        Date time = d.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

        return dateFormat.format(time);
    }

}