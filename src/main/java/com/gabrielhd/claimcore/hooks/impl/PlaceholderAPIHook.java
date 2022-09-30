package com.gabrielhd.claimcore.hooks.impl;

import com.gabrielhd.claimcore.hooks.Hook;
import org.bukkit.Chunk;

public class PlaceholderAPIHook extends Hook {

    public PlaceholderAPIHook(String hookName) {
        super(hookName);
    }

    @Override
    public boolean canClaim(Chunk chunk) {
        return false;
    }
}
