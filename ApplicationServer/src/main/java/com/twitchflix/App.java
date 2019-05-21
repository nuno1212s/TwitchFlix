package com.twitchflix;

import com.twitchflix.authentication.UserDataHandler;
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
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.servlet.ServletContainer;

import java.io.*;
import java.util.Arrays;
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
        initSearchEngine();

        authenticationHandler = new AuthenticationHandler();

        //Setup http multithreading to stop blocking I/O requests
        QueuedThreadPool httpThreadPool = new QueuedThreadPool();

        httpThreadPool.setMaxThreads(20);

        Server server = new Server(httpThreadPool);

        Logger.log(Level.INFO, "Exporting keystore file.");

        File f = getFileManager().getFileFromResource("keystore2.jks");

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

        ServerConnector connector = new ServerConnector(server);

        connector.setPort(80);

        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        servletContextHandler.setContextPath("/");

        server.setHandler(registerServlets(servletContextHandler));

        server.setConnectors(new Connector[]{connector, https});

        server.start();
        server.join();

    }

    private static HandlerList registerServlets(ServletContextHandler ctx) {

        HandlerList handlers = new HandlerList();

        ServletHolder authentication = ctx.addServlet(ServletContainer.class, "/*");
        authentication.setInitOrder(1);

        authentication.setInitParameter(ServerProperties.PROVIDER_CLASSNAMES,
                String.join(",", Arrays.asList(
                        AuthenticationHandler.class.getCanonicalName(),
                        VideoRestHandler.class.getCanonicalName(),
                        OAuth2Handler.class.getCanonicalName(),
                        UserDataHandler.class.getCanonicalName()
                )));

        handlers.setHandlers(new Handler[]{ctx, new DefaultHandler()});

        return handlers;

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
