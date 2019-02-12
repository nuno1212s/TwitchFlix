package com.nunoneto.loggers;

import com.nunoneto.App;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;

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

        systemOut = System.out;

        try {
            FileOutputStream fileStream = new FileOutputStream(outputFile);

            logOut = new PrintStream(fileStream, false);

            System.setOut(logOut);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void log(Object object)  {
        log(Level.INFO, object);
    }

    public void log(Level level, Object string) {

        Calendar d = Calendar.getInstance();

        Date time = d.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

        String format = dateFormat.format(time);

        String logMessage = "[" + format + "] " + "[" + level.getName() + "] " + string.toString();

        logOut.println(logMessage);

        systemOut.println(logMessage);

        App.getAsync().submit(() -> logOut.flush());

    }

}
