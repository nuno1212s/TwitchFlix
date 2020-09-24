package com.twitchflix.databases.mysql;

import com.google.api.client.json.GenericJson;
import com.twitchflix.App;
import com.twitchflix.filesystem.FileManager;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class MySQLDB {

    protected static HikariDataSource dataSource;

    protected static String userName, password, database, IP;

    public MySQLDB() {
        if (dataSource != null) {
            return;
        }

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        FileManager fileManager = App.getFileManager();

        GenericJson parsedFile = fileManager.readFile(fileManager.getFileFromResource("mysqlcfg.json"));

        MySQLDB.userName = (String) parsedFile.get("UserName");
        MySQLDB.password = (String) parsedFile.get("Password");
        MySQLDB.database = (String) parsedFile.get("Database");
        MySQLDB.IP = (String) parsedFile.get("IP");

        HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:mysql://" + IP + "/" + database + "?serverTimezone=UTC");

        config.setUsername(userName);
        config.setPassword(password);
        config.addDataSourceProperty("prepStmtCacheSize", "350");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("cachePrepStmts", "true");


        MySQLDB.dataSource = new HikariDataSource(config);
    }

    protected Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }


}
