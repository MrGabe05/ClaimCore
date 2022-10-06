package com.gabrielhd.claimcore;

import com.gabrielhd.claimcore.commands.CoinsCmds;
import com.gabrielhd.claimcore.commands.PartyCmds;
import com.gabrielhd.claimcore.config.Config;
import com.gabrielhd.claimcore.config.YamlConfig;
import com.gabrielhd.claimcore.database.Database;
import com.gabrielhd.claimcore.hooks.impl.GriefPreventionHook;
import com.gabrielhd.claimcore.hooks.impl.PlaceholderAPIHook;
import com.gabrielhd.claimcore.hooks.impl.WorldGuardHook;
import com.gabrielhd.claimcore.lang.Lang;
import com.gabrielhd.claimcore.listeners.ClaimListeners;
import com.gabrielhd.claimcore.manager.ClaimManager;
import com.gabrielhd.claimcore.manager.MissionsManager;
import com.gabrielhd.claimcore.tasks.SaveTask;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

@Getter
public class ClaimCore extends JavaPlugin {

    @Getter private static ClaimCore instance;

    private ClaimManager claimManager;
    private MissionsManager missionsManager;

    private WorldGuardHook worldGuardHook;
    private GriefPreventionHook griefPreventionHook;

    @Override
    public void onEnable() {
        instance = this;

        new Config();
        new Database(this);

        this.loadMenus();
        Lang.loadLangs();

        this.claimManager = new ClaimManager();
        this.missionsManager = new MissionsManager();

        if(this.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderAPIHook().register();

            this.getLogger().log(Level.INFO, "PlaceholderAPI Hooked correctly!");
        }

        if(this.getServer().getPluginManager().isPluginEnabled("WorldGuard")) {
            this.worldGuardHook = new WorldGuardHook();
        }

        if(this.getServer().getPluginManager().isPluginEnabled("GriefPrevention")) {
            this.griefPreventionHook = new GriefPreventionHook();
        }

        this.getCommand("party").setExecutor(new PartyCmds());
        this.getCommand("coins").setExecutor(new CoinsCmds());

        this.getServer().getPluginManager().registerEvents(new ClaimListeners(), this);

        this.getServer().getScheduler().runTaskTimerAsynchronously(this, new SaveTask(), 1200L, 600L);
    }

    private void loadMenus() {
        new YamlConfig(this, "menus/Confirm");
        new YamlConfig(this, "menus/Members");
        new YamlConfig(this, "menus/Missions");
        new YamlConfig(this, "menus/Settings");
        new YamlConfig(this, "menus/Upgrades");

        new YamlConfig(this, "menus/missions/Crafting");
        new YamlConfig(this, "menus/missions/Farming");
        new YamlConfig(this, "menus/missions/Killing");
        new YamlConfig(this, "menus/missions/Mining");

        new YamlConfig(this, "lang/Lang_es");
    }
}
