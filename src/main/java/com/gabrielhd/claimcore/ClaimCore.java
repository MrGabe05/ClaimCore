package com.gabrielhd.claimcore;

import com.gabrielhd.claimcore.commands.ClaimCmds;
import com.gabrielhd.claimcore.commands.PartyCmds;
import com.gabrielhd.claimcore.config.Config;
import com.gabrielhd.claimcore.database.Database;
import com.gabrielhd.claimcore.lang.Lang;
import com.gabrielhd.claimcore.listeners.ClaimListeners;
import com.gabrielhd.claimcore.listeners.GeneralListeners;
import com.gabrielhd.claimcore.listeners.MissionListeners;
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

        new Config();

        Lang.loadLangs();

        this.claimManager = new ClaimManager();
        this.missionsManager = new MissionsManager();

        new Database(this);

        this.getCommand("party").setExecutor(new PartyCmds());
        this.getCommand("claim").setExecutor(new ClaimCmds());

        this.getServer().getPluginManager().registerEvents(new ClaimListeners(), this);
        this.getServer().getPluginManager().registerEvents(new GeneralListeners(), this);
        this.getServer().getPluginManager().registerEvents(new MissionListeners(), this);
    }
}
