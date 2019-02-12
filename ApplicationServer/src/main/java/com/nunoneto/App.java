package com.nunoneto;

import com.nunoneto.loggers.Logger;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import java.io.File;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class App {

    private static ExecutorService executors = Executors.newFixedThreadPool(50);

    public static ExecutorService getAsync() {
        return executors;
    }

    private static File dataFolder;

    static {

        try {
            dataFolder = new File(App.class.getProtectionDomain().getCodeSource().getLocation()
                    .toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        String parentPath = dataFolder.toString();
        dataFolder = new File(parentPath.substring(0, parentPath.lastIndexOf(File.separator)) + File.separator + "App");

        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }
    }

    public static void main(String[] args) throws Exception {

        //Initialize the logger

        new Logger(dataFolder);

        //Setup http multithreading to stop blocking I/O requests
        QueuedThreadPool httpThreadPool = new QueuedThreadPool();

        httpThreadPool.setMaxThreads(20);

        Server server = new Server(httpThreadPool);

        File f = new File(App.class.getResource("keystore2.jks").toURI());

        if (!f.exists()) {
            Logger.getIns().log(Level.SEVERE, "The keystore file does not exist. Cannot establish secure server.");

            return;
        }

        HttpConfiguration configuration = new HttpConfiguration();

        configuration.setSecureScheme("https");
        configuration.setSecurePort(8443);
        configuration.setOutputBufferSize(32768);

        SslContextFactory contextFactory = new SslContextFactory();

        contextFactory.setKeyStorePath(f.getAbsolutePath());
        contextFactory.setKeyStorePassword("123456");
        contextFactory.setKeyManagerPassword("123456");

        HttpConfiguration config = new HttpConfiguration(configuration);
        SecureRequestCustomizer customizer = new SecureRequestCustomizer();

        customizer.setStsMaxAge(2000);
        customizer.setStsIncludeSubDomains(true);
        config.addCustomizer(customizer);

        ServerConnector https = new ServerConnector(server,
                new SslConnectionFactory(contextFactory, HttpVersion.HTTP_1_1.asString()),
                new HttpConnectionFactory(config));

        https.setPort(8443);
        https.setHost("localhost");
        https.setIdleTimeout(500000);

        server.addConnector(https);

        server.start();
        server.join();

    }



}
