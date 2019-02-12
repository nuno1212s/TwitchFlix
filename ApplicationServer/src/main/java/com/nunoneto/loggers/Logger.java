package com.nunoneto.loggers;

import com.nunoneto.App;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class Logger {

    private static Logger ins;

    public static Logger getIns() {
        return ins;
    }

    private File outputFile;

    private PrintStream systemOut, logOut;

    public Logger(File dataFolder) {
        ins = this;

        outputFile = new File(dataFolder, "log.txt");

        if (outputFile.exists()) {
            //Output file already exists, zip the old log file and create a new one

            File storageFile = new File(dataFolder, "log- " + getDate() + ".zip");

            try (ZipOutputStream writeStream = new ZipOutputStream(new FileOutputStream(storageFile));
                 //Read the old log file
                    InputStream readStream = new FileInputStream(outputFile)) {

                storageFile.createNewFile();

                ZipEntry e = new ZipEntry("log.txt");

                writeStream.putNextEntry(e);

                int length;

                //1KB read buffer
                byte[] buffer = new byte[1024];

                while ((length = readStream.read(buffer)) != 0) {

                    writeStream.write(buffer, 0, length);

                }

                //Delete the contents of the output file
                outputFile.delete();
                outputFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        systemOut = System.out;

        try {
            FileOutputStream fileStream = new FileOutputStream(outputFile);

            logOut = new PrintStream(fileStream, false);

            System.setOut(logOut);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void log(Object object) {
        log(Level.INFO, object);
    }

    public void log(Level level, Object string) {


        String logMessage = "[" + getDate() + "] " + "[" + level.getName() + "] " + string.toString();

        logOut.println(logMessage);

        systemOut.println(logMessage);

        App.getAsync().submit(() -> logOut.flush());

    }

    private String getDate() {
        Calendar d = Calendar.getInstance();

        Date time = d.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

        return dateFormat.format(time);
    }

}
