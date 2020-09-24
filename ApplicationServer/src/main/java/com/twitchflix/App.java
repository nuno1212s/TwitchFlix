package com.twitchflix;

import com.twitchflix.authentication.UserDataHandler;
import com.twitchflix.authentication.accounts.AuthenticationHandler;
import com.twitchflix.authentication.accounts.AuthenticationRestHandler;
import com.twitchflix.authentication.oauth2.OAuth2Handler;
import com.twitchflix.authentication.oauth2.OAuth2RestHandler;
import com.twitchflix.databases.UserDatabase;
import com.twitchflix.databases.VideoDatabase;
import com.twitchflix.databases.mongodb.MongoUserDB;
import com.twitchflix.databases.mysql.MySqlVideoDB;
import com.twitchflix.filesystem.DefaultFileManager;
import com.twitchflix.filesystem.FileManager;
import com.twitchflix.loggers.Logger;
import com.twitchflix.searchengine.NotSoTerribleSearchEngine;
import com.twitchflix.searchengine.SearchEngine;
import com.twitchflix.videohandler.VideoRestHandler;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJsonProvider;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.servlet.ServletContainer;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class App {

    public static String SERVER_IP, DATA_FOLDER, USER_PHOTOS, VIDEO_FOLDER, VIDEO_THUMBNAIL;

    private static ExecutorService executors;

    private static UserDatabase userDatabase;

    private static SearchEngine videoSearchEngine;

    private static VideoDatabase videoDatabase;

    private static AuthenticationHandler authenticationHandler;

    private static OAuth2Handler auth2Handler;

    private static FileManager fileManager;

    public static OAuth2Handler getAuth2Handler() {
        return auth2Handler;
    }

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
        SERVER_IP = "nunogneto.pt";
        DATA_FOLDER = "/mnt/databases/data/";

        USER_PHOTOS = "userphotos";
        VIDEO_FOLDER = "recordings";
        VIDEO_THUMBNAIL = "images";

        initFileManager();
        initLoggers();
        initExecutors();
        initDatabases();
        initSearchEngine();
        initHandlers();

        //Setup http multithreading to stop blocking I/O requests
        QueuedThreadPool httpThreadPool = new QueuedThreadPool();

        httpThreadPool.setMaxThreads(20);

        Server server = new Server(httpThreadPool);

        Logger.log(Level.INFO, "Exporting keystore file.");

        File f = getFileManager().getFileFromResource("keystore.jks");

        HttpConfiguration configuration = new HttpConfiguration();

        configuration.setSecureScheme("https");
        configuration.setSecurePort(8443);
        configuration.setOutputBufferSize(32768);

        SslContextFactory contextFactory = new SslContextFactory();

        contextFactory.setKeyStorePath(f.getAbsolutePath());
        contextFactory.setKeyStorePassword("trabalhopdm");
        contextFactory.setKeyManagerPassword("trabalhopdm");

        HttpConfiguration config = new HttpConfiguration(configuration);
        SecureRequestCustomizer customizer = new SecureRequestCustomizer();

        customizer.setStsMaxAge(2000);
        customizer.setStsIncludeSubDomains(true);
        config.addCustomizer(customizer);

        ServerConnector https = new ServerConnector(server,
                new SslConnectionFactory(contextFactory, HttpVersion.HTTP_1_1.asString()),
                new HttpConnectionFactory(config));

        https.setPort(8443);
        https.setHost("0.0.0.0");
        https.setIdleTimeout(500000);

        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        servletContextHandler.setContextPath("/");

        server.setHandler(registerServlets(servletContextHandler));

        server.setConnectors(new Connector[]{https});

        server.start();
        server.join();
    }

    private static HandlerList registerServlets(ServletContextHandler ctx) {

        HandlerList handlers = new HandlerList();

        ServletHolder servlet = ctx.addServlet(ServletContainer.class, "/*");
        servlet.setInitOrder(1);

        servlet.setInitParameter(ServerProperties.PROVIDER_CLASSNAMES,
                String.join(",", Arrays.asList(
                        AuthenticationRestHandler.class.getCanonicalName(),
                        VideoRestHandler.class.getCanonicalName(),
                        OAuth2RestHandler.class.getCanonicalName(),
                        UserDataHandler.class.getCanonicalName(),
                        DebugExceptionMapper.class.getCanonicalName(),
                        JacksonJsonProvider.class.getCanonicalName(),
                        MultiPartFeature.class.getCanonicalName()
                )));

        handlers.setHandlers(new Handler[]{ctx, new DefaultHandler()});

        return handlers;

    }

    private static void initHandlers() {

        authenticationHandler = new AuthenticationHandler();
        try {
            auth2Handler = new OAuth2Handler();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void initDatabases() {

        userDatabase = new MongoUserDB();
        videoDatabase = new MySqlVideoDB();

    }

    private static void initSearchEngine() {
        videoSearchEngine = new NotSoTerribleSearchEngine();
    }

    private static void initFileManager() {

        fileManager = new DefaultFileManager();

    }

    private static void initLoggers() {
        new Logger();
    }

    private static void initExecutors() {
        executors = Executors.newFixedThreadPool(20);
    }
}
