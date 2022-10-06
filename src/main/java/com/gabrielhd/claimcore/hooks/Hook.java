package com.gabrielhd.claimcore.hooks;

import com.gabrielhd.claimcore.ClaimCore;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

@Getter
public abstract class Hook {

    private final String hookName;

    public Hook(String hookName) {
        this.hookName = hookName;

        Plugin plugin = Bukkit.getPluginManager().getPlugin(hookName);
        ClaimCore.getInstance().getLogger().log(Level.INFO, hookName + " Hooked in the version " + plugin.getDescription().getVersion());
    }
}
