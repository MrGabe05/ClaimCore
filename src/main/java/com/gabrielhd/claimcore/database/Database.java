package com.gabrielhd.claimcore.database;

import com.gabrielhd.claimcore.ClaimCore;
import com.gabrielhd.claimcore.config.Config;
import com.gabrielhd.claimcore.database.types.MySQL;
import com.gabrielhd.claimcore.database.types.SQLite;

public class Database {

    private static DataHandler storage;
    
    public Database(ClaimCore plugin) {
        String host = Config.HOST;
        String port = Config.PORT;
        String db = Config.DATABASE;
        String user = Config.USERNAME;
        String pass = Config.PASSWORD;

        if (Config.TYPE.equalsIgnoreCase("mysql")) {
            storage = new MySQL(plugin, host, port, db, user, pass);
        } else {
            storage = new SQLite(plugin);
        }
    }
    
    public static DataHandler getStorage() {
        return Database.storage;
    }
}
