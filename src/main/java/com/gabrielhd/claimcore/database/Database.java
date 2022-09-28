package com.gabrielhd.claimcore.database;

import com.gabrielhd.claimcore.ClaimCore;
import com.gabrielhd.claimcore.config.YamlConfig;
import com.gabrielhd.claimcore.database.types.MySQL;
import com.gabrielhd.claimcore.database.types.SQLite;
import org.bukkit.configuration.file.FileConfiguration;

public class Database {

    private static DataHandler storage;
    
    public Database(ClaimCore plugin) {
        FileConfiguration data = new YamlConfig(plugin, "Settings");

        String host = data.getString("Database.Host");
        String port = data.getString("Database.Port");
        String db = data.getString("Database.Database");
        String user = data.getString("Database.Username");
        String pass = data.getString("Database.Password");

        if (data.getString("StorageType", "sqlite").equalsIgnoreCase("mysql")) {
            storage = new MySQL(plugin, host, port, db, user, pass);
        } else {
            storage = new SQLite(plugin);
        }
    }
    
    public static DataHandler getStorage() {
        return Database.storage;
    }
}
