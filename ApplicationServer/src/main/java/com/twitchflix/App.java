package com.nunoneto;

import com.nunoneto.authentication.accounts.AuthenticationHandler;
import com.nunoneto.databases.UserDatabase;
import com.nunoneto.loggers.Logger;
import com.nunoneto.videohandler.SearchEngine;
import com.nunoneto.videohandler.VideoRestHandler;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.glassfish.jersey.servlet.ServletContainer;

import java.io.File;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class App {

    private static ExecutorService executors = Executors.newFixedThreadPool(15);

    public static ExecutorService getAsync() {
        return executors;
    }

    private static UserDatabase userDatabase;

    public static UserDatabase getUserDatabase() {
        return userDatabase;
    }

    private static SearchEngine videoSearchEngine;

    public static SearchEngine getVideoSearchEngine() {
        return videoSearchEngine;
    }

    private static VideoDatabase videoDatabase;

    public static VideoDatabase getVideoDatabase() {
        return videoDatabase;
    }

    private static AuthenticationHandler authenticationHandler;

    public static AuthenticationHandler getAuthenticationHandler() {
        return authenticationHandler;
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

        authenticationHandler = new AuthenticationHandler();

        //Setup http multithreading to stop blocking I/O requests
        QueuedThreadPool httpThreadPool = new QueuedThreadPool();

        httpThreadPool.setMaxThreads(20);

        Server server = new Server(httpThreadPool);

        File f = new File(dataFolder + File.separator + "SSL" + File.separator, "keystore2.jks");

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

}
