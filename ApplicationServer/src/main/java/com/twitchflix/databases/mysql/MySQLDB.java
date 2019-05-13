package com.twitchflix.databases.mysql;

import com.google.api.client.json.GenericJson;
import com.twitchflix.App;
import com.twitchflix.filesystem.FileManager;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public abstract class MySQLDB {

    protected static HikariDataSource dataSource;

    protected static String userName, password, database, IP;

    public MySQLDB() {
        if (dataSource == null) {
            return;
        }

        FileManager fileManager = App.getFileManager();

        GenericJson parsedFile = fileManager.readFile(fileManager.getFileAndCreate("mysqlcfg.json"));

        MySQLDB.userName = (String) parsedFile.get("UserName");
        MySQLDB.password = (String) parsedFile.get("Password");
        MySQLDB.database = (String) parsedFile.get("Database");
        MySQLDB.IP = (String) parsedFile.get("IP");

        HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:mysql://" + IP + "/" + database);

        config.setUsername(userName);
        config.setPassword(password);
        config.addDataSourceProperty("prepStmtCacheSize", "350");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("cachePrepStmts", "true");

        dataSource = new HikariDataSource(config);
    }

}
