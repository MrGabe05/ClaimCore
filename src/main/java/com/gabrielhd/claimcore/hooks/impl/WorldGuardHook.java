package com.gabrielhd.claimcore.hooks.impl;

import com.gabrielhd.claimcore.hooks.Hook;
import org.bukkit.Chunk;

public class WorldGuardHook extends Hook {

    public WorldGuardHook(String hookName) {
        super(hookName);
    }

    @Override
    public boolean canClaim(Chunk chunk) {
        return false;
    }
}
