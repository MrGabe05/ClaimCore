package com.gabrielhd.claimcore.menu.impl;

import com.gabrielhd.claimcore.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class UpgradesMenu extends Menu {

    public UpgradesMenu(String name, int rows) {
        super(name, rows);
    }

    @Override
    public void onClose(Player player, InventoryCloseEvent event) {

    }

    @Override
    public void onOpen(Player player, InventoryOpenEvent event) {

    }

    @Override
    public void onClick(Player player, InventoryClickEvent event) {

    }
}
