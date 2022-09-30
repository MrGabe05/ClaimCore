package com.gabrielhd.claimcore.menu.impl;

import com.gabrielhd.claimcore.ClaimCore;
import com.gabrielhd.claimcore.config.YamlConfig;
import com.gabrielhd.claimcore.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class SettingsMenu extends Menu {

    public SettingsMenu() {
        super(new YamlConfig(ClaimCore.getInstance(), "menus/Settings"));
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event) {

    }
}
