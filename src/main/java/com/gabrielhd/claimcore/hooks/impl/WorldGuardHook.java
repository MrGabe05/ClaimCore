package com.gabrielhd.claimcore.hooks.impl;

import com.gabrielhd.claimcore.hooks.Hook;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;

public class WorldGuardHook extends Hook {

    public WorldGuardHook() {
        super("WorldGuard");
    }

    public boolean canClaim(Location loc) {
        WorldGuard guard = WorldGuard.getInstance();
        RegionManager manager = guard.getPlatform().getRegionContainer().get(new BukkitWorld(loc.getWorld()));

        for(ProtectedRegion region : manager.getApplicableRegions(BlockVector3.at(loc.getX(), loc.getY(), loc.getZ())).getRegions()) {
            if(region != null && !region.getId().equalsIgnoreCase("__global__")) {
                return false;
            }
        }

        return true;
    }
}
