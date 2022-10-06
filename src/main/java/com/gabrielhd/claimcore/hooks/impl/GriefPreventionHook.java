package com.gabrielhd.claimcore.hooks.impl;

import com.gabrielhd.claimcore.hooks.Hook;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class GriefPreventionHook extends Hook {

    public GriefPreventionHook() {
        super("GriefPrevention");
    }

    public boolean canClaim(Player player) {
        me.ryanhamshire.GriefPrevention.PlayerData gData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), false, gData.lastClaim);

        return claim == null || claim.getOwnerName().equalsIgnoreCase(player.getName());
    }
}
