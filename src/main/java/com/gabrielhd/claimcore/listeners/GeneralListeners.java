package com.gabrielhd.claimcore.listeners;

import com.gabrielhd.claimcore.player.PlayerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GeneralListeners implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        PlayerData.load(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        PlayerData.save(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        PlayerData.save(event.getPlayer().getUniqueId());
    }
}
