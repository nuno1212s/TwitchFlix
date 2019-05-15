package com.twitchflix;

import com.twitchflix.authentication.accounts.AuthenticationHandler;
import com.twitchflix.authentication.oauth2.OAuth2Handler;
import com.twitchflix.databases.UserDatabase;
import com.twitchflix.databases.VideoDatabase;
import com.twitchflix.databases.mongodb.MongoUserDB;
import com.twitchflix.databases.mysql.MySqlVideoDB;
import com.twitchflix.filesystem.DefaultFileManager;
import com.twitchflix.filesystem.FileManager;
import com.twitchflix.loggers.Logger;
import com.twitchflix.searchengine.SearchEngine;
import com.twitchflix.videohandler.VideoRestHandler;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.glassfish.jersey.servlet.ServletContainer;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class App {

    private static ExecutorService executors = Executors.newFixedThreadPool(15);

    private static UserDatabase userDatabase;

    private static SearchEngine videoSearchEngine;

    private static VideoDatabase videoDatabase;

    private static AuthenticationHandler authenticationHandler;

    private static FileManager fileManager;

    public static AuthenticationHandler getAuthenticationHandler() {
        return authenticationHandler;
    }

    public static VideoDatabase getVideoDatabase() {
        return videoDatabase;
    }

    public static SearchEngine getVideoSearchEngine() {
        return videoSearchEngine;
    }

    public static UserDatabase getUserDatabase() {
        return userDatabase;
    }

    public static ExecutorService getAsync() {
        return executors;
    }

    public static FileManager getFileManager() {
        return fileManager;
    }


    public static void main(String[] args) throws Exception {

        //Initialize the logger

        initFileManager();
        initLoggers();
        initDatabases();

        authenticationHandler = new AuthenticationHandler();

        //Setup http multithreading to stop blocking I/O requests
        QueuedThreadPool httpThreadPool = new QueuedThreadPool();

        httpThreadPool.setMaxThreads(20);

        Server server = new Server(httpThreadPool);

        File f = getFileManager().getFile("SSL" + File.separator + "keystore2.jks");

        if (!f.exists()) {

            exportKeyStore(f);

            Logger.log(Level.SEVERE, "The keystore file does not exist. Cannot establish secure server.");

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

        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        servletContextHandler.setContextPath("/");
        server.setHandler(servletContextHandler);

        registerServlets(servletContextHandler);

        server.addConnector(https);

        server.start();
        server.join();

    }

    /**
     * Saves the key store (SHA 256 SSH certificate) to the application folder
     */
    private static void exportKeyStore(File destination) {

        Logger.log(Level.INFO, "Exporting keystore file.");

        try (InputStream resourceAsStream = App.class.getResourceAsStream("keystore2.jks");
             OutputStream outputStream = new FileOutputStream(destination)) {

            destination.createNewFile();

            int length;

            byte[] buffer = new byte[1024];

            while ((length = resourceAsStream.read(buffer)) != 0) {

                outputStream.write(buffer, 0, length);

            }

        } catch (IOException e) {
            Logger.logException(e);
        }

    }

    private static void registerServlets(ServletContextHandler ctx) {

        ServletHolder authentication = ctx.addServlet(ServletContainer.class, "/*");
        authentication.setInitOrder(1);
        authentication.setInitParameter("jersey.config.server.provider.classnames",
                AuthenticationHandler.class.getCanonicalName());

        ServletHolder mainRest = ctx.addServlet(ServletContainer.class, "/*");
        mainRest.setInitOrder(2);
        mainRest.setInitParameter("jersey.config.server.provider.classnames",
                VideoRestHandler.class.getCanonicalName());

        ServletHolder oAuthRest = ctx.addServlet(ServletContainer.class, "/*");
        oAuthRest.setInitOrder(3);
        oAuthRest.setInitParameter("jersey.config.server.provider.classnames",
                OAuth2Handler.class.getCanonicalName());

    }

    private static void initDatabases() {

        userDatabase = new MongoUserDB();
        videoDatabase = new MySqlVideoDB();

    }

    private static void initSearchEngine() {



    }

    private static void initFileManager() {

        fileManager = new DefaultFileManager();

    }

    private static void initLoggers() {
        new Logger();
    }

}
