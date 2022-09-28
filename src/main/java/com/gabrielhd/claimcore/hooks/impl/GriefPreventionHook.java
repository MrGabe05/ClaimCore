package com.gabrielhd.claimcore.hooks.impl;

import com.gabrielhd.claimcore.hooks.Hook;
import org.bukkit.Chunk;

public class GriefPreventionHook extends Hook {

    public GriefPreventionHook() {
        super("griefprevention");
    }

    @Override
    public boolean canClaim(Chunk chunk) {
        return false;
    }
}
