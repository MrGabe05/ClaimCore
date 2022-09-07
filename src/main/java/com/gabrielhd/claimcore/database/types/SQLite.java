package com.gabrielhd.claimcore.database.types;

import com.gabrielhd.claimcore.Main;
import com.gabrielhd.claimcore.database.DataHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

public class SQLite extends DataHandler {

    private final String table;
    private Connection connection;
    
    public SQLite(Main plugin) {
        this.table = "claimcore_";

        this.connect(plugin);
        this.setupTable();
    }
    
    private synchronized void connect(Main plugin) {
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder() + "/Database.db");
        } catch (SQLException | ClassNotFoundException ex) {
            plugin.getLogger().log(Level.SEVERE, "Can't initialize database connection! Please check your configuration!");
            plugin.getLogger().log(Level.SEVERE, "If this error persists, please report it to the developer!");

            ex.printStackTrace();
        }
    }

    @Override
    public Connection getConnection() {
        return null;
    }
}
