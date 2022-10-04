package com.gabrielhd.claimcore.listeners;

import com.gabrielhd.claimcore.ClaimCore;
import com.gabrielhd.claimcore.claims.Claim;
import com.gabrielhd.claimcore.config.Config;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class ClaimListeners implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlockPlaced();

        Claim claim = ClaimCore.getInstance().getClaimManager().getClaimOfPlayer(player.getUniqueId());
        if(claim == null || block == null) {
            return;
        }

        if(Config.XP_BLOCKS.containsKey(block.getType())) {
            if(claim.isInRegion(block.getLocation())) {
                claim.setExp(claim.getExp() + Config.XP_BLOCKS.get(block.getType()));
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        Claim claim = ClaimCore.getInstance().getClaimManager().getClaimOfPlayer(player.getUniqueId());
        if(claim == null || block == null) {
            return;
        }

        if(Config.XP_BLOCKS.containsKey(block.getType())) {
            if(claim.isInRegion(block.getLocation())) {
                claim.setExp(Math.max(0.0, (claim.getExp() - Config.XP_BLOCKS.get(block.getType()))));
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();

        if((entity instanceof Player) || entity.getKiller() == null) {
            return;
        }

        Player killer = entity.getKiller();

        Claim claim = ClaimCore.getInstance().getClaimManager().getClaimOfPlayer(killer.getUniqueId());
        if(claim == null) {
            return;
        }

        if(Config.XP_ENTITY.containsKey(entity.getType())) {
            if(claim.isInRegion(entity.getLocation())) {
                claim.setExp(claim.getExp() + Config.XP_ENTITY.get(entity.getType()));
            }
        }
    }
}
