package com.gabrielhd.claimcore;

import com.gabrielhd.claimcore.database.Database;
import com.gabrielhd.claimcore.manager.ClaimManager;
import com.gabrielhd.claimcore.manager.MissionsManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class ClaimCore extends JavaPlugin {

    @Getter private static ClaimCore instance;

    private ClaimManager claimManager;
    private MissionsManager missionsManager;

    @Override
    public void onEnable() {
        instance = this;

        this.claimManager = new ClaimManager();
        this.missionsManager = new MissionsManager();

        new Database(this);
    }
}
