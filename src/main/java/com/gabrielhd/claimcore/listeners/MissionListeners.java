package com.gabrielhd.claimcore.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;

public class MissionListeners implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

    }

    @EventHandler
    public void onCrafting(CraftItemEvent event) {
        Player player = (Player) event.getWhoClicked();

    }

    @EventHandler
    public void onEntityDead(EntityDeathEvent event) {
        Player player = event.getEntity().getKiller();

    }
}
