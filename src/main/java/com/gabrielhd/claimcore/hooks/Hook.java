package com.gabrielhd.claimcore.hooks;

import org.bukkit.Chunk;

public abstract class Hook {

    private final String hookName;

    public Hook(String hookName) {
        this.hookName = hookName;
    }

    public abstract boolean canClaim(Chunk chunk);
}
