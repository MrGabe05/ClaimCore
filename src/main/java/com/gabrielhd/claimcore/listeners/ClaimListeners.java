package com.gabrielhd.claimcore.listeners;

import com.gabrielhd.claimcore.ClaimCore;
import com.gabrielhd.claimcore.claims.Claim;
import com.gabrielhd.claimcore.config.Config;
import com.gabrielhd.claimcore.lang.Lang;
import com.gabrielhd.claimcore.missions.MissionProgress;
import com.gabrielhd.claimcore.missions.MissionType;
import com.gabrielhd.claimcore.player.PlayerData;
import com.gabrielhd.claimcore.upgrades.Upgrades;
import com.gabrielhd.claimcore.utils.TextPlaceholders;
import com.gabrielhd.claimcore.visualization.Visualization;
import com.gabrielhd.claimcore.visualization.VisualizationType;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Locale;

public class ClaimListeners implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        PlayerData.load(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlockPlaced();

        Claim claim = ClaimCore.getInstance().getClaimManager().getClaimChunk(block.getChunk());
        if(claim != null) {
            if(!claim.isMember(player.getUniqueId()) && !player.hasPermission("claimcore.admin")) {
                event.setCancelled(true);
                return;
            }

            if(block.getType() == Material.HOPPER || block.getType() == Material.HOPPER_MINECART) {
                if(Config.UPGRADES_LIMITS.get(Upgrades.MAX_HOPPERS).getOrDefault(claim.getUpgradeLevel(Upgrades.MAX_HOPPERS), claim.getHoppersAmount()) >= claim.getHoppersAmount()) {
                    event.setCancelled(true);
                    return;
                }
                claim.getHoppers().add(block.getLocation());
            }

            if(block.getType() == Material.SPAWNER) {
                if(Config.UPGRADES_LIMITS.get(Upgrades.MAX_SPAWNERS).getOrDefault(claim.getUpgradeLevel(Upgrades.MAX_SPAWNERS), claim.getSpawnersAmount()) >= claim.getSpawnersAmount()) {
                    event.setCancelled(true);
                    return;
                }
                claim.getSpawners().add(block.getLocation());
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        Claim claim = ClaimCore.getInstance().getClaimManager().getClaimChunk(block.getChunk());
        if(claim != null) {
            if(!claim.isMember(player.getUniqueId()) && !player.hasPermission("claimcore.admin")) {
                event.setCancelled(true);
                return;
            }

            if(block.getType() == Material.HOPPER || block.getType() == Material.HOPPER_MINECART) {
                claim.getHoppers().remove(block.getLocation());
            }

            if(block.getType() == Material.SPAWNER) {
                claim.getSpawners().remove(block.getLocation());
            }
        }
    }

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

        MissionProgress mission = claim.getCurrentMission();
        if(mission != null && mission.getMission().getType() == MissionType.MINING && claim.isCompleted()) {
            Lang.NOTIFY_MISSION_COMPLETED.send(new TextPlaceholders().set("%mission%", mission.getMission().getDisplayName()), player);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player player = entity.getKiller();

        if((entity instanceof Player) || player == null) {
            return;
        }

        Claim claim = ClaimCore.getInstance().getClaimManager().getClaimOfPlayer(player.getUniqueId());
        if(claim == null) return;

        if(Config.XP_ENTITY.containsKey(entity.getType())) {
            if(claim.isInRegion(entity.getLocation())) {
                claim.setExp(claim.getExp() + Config.XP_ENTITY.get(entity.getType()));
            }
        }

        event.setDroppedExp(event.getDroppedExp() * Config.UPGRADES_LIMITS.get(Upgrades.MOB_XP).getOrDefault(claim.getUpgradeLevel(Upgrades.MOB_XP), 1));

        MissionProgress mission = claim.getCurrentMission();
        if(mission == null || mission.getMission().getType() != MissionType.KILLING) return;

        String type = entity.getType().name().toUpperCase(Locale.ROOT);
        if(mission.getMission().getRequired().containsKey(type)) {
            if(claim.isCompleted()) {
                Lang.NOTIFY_MISSION_COMPLETED.send(new TextPlaceholders().set("%mission%", mission.getMission().getDisplayName()), player);
                return;
            }

            if(mission.getProgress().getOrDefault(type, 0) >= mission.getMission().getRequired().get(type)) return;
            mission.getProgress().put(type, mission.getProgress().getOrDefault(type, 0) + 1);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        PlayerData playerData = PlayerData.of(player.getUniqueId());
        if(playerData != null) {
            Location loc = event.getTo();

            if(playerData.lastChunk != loc.getChunk()) {
                playerData.lastChunk = loc.getChunk();
            }

            Claim claim = ClaimCore.getInstance().getClaimManager().getClaimChunk(loc.getChunk());
            if(claim != null) {
                if(claim.isMember(player.getUniqueId())) {
                    if(!playerData.inClaim) {
                        playerData.inClaim = true;

                        Visualization visualization = new Visualization();
                        for(Chunk chunks : claim.getChunks()) {
                            visualization.addChunkElements(chunks, player.getLocation().getBlockY(), VisualizationType.Chunk, player.getLocation());
                        }

                        Visualization.Apply(player, visualization);
                    }
                } else {
                    playerData.inClaim = false;
                }
            }
        }
    }
}
