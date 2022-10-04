package com.gabrielhd.claimcore.menu.impl;

import com.gabrielhd.claimcore.ClaimCore;
import com.gabrielhd.claimcore.claims.Claim;
import com.gabrielhd.claimcore.config.YamlConfig;
import com.gabrielhd.claimcore.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class SettingsMenu extends Menu {

    private final Claim claim;

    public SettingsMenu(Claim claim) {
        super(new YamlConfig(ClaimCore.getInstance(), "menus/Settings"));

        this.claim = claim;
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event) {

    }
}
