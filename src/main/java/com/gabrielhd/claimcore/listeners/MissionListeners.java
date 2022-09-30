package com.gabrielhd.claimcore.listeners;

import com.gabrielhd.claimcore.ClaimCore;
import com.gabrielhd.claimcore.claims.Claim;
import com.gabrielhd.claimcore.lang.Lang;
import com.gabrielhd.claimcore.missions.MissionProgress;
import com.gabrielhd.claimcore.missions.MissionType;
import com.gabrielhd.claimcore.utils.TextPlaceholders;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Locale;

public class MissionListeners implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if(event.isCancelled()) return;

        Claim claim = ClaimCore.getInstance().getClaimManager().of(player.getUniqueId());
        if(claim == null) return;

        MissionProgress mission = claim.getCurrentMission();
        if(mission == null || mission.getMission().getType() != MissionType.MINING) return;

        String type = block.getType().name().toLowerCase(Locale.ROOT);

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
    public void onEntityDead(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player player = entity.getKiller();

        if(player == null) return;

        Claim claim = ClaimCore.getInstance().getClaimManager().of(player.getUniqueId());
        if(claim == null) return;

        MissionProgress mission = claim.getCurrentMission();
        if(mission == null || mission.getMission().getType() != MissionType.KILLING) return;

        String type = entity.getType().name().toLowerCase(Locale.ROOT);

        if(mission.getMission().getRequired().containsKey(type)) {
            if(claim.isCompleted()) {
                Lang.NOTIFY_MISSION_COMPLETED.send(new TextPlaceholders().set("%mission%", mission.getMission().getDisplayName()), player);
                return;
            }

            if(mission.getProgress().getOrDefault(type, 0) >= mission.getMission().getRequired().get(type)) return;
            mission.getProgress().put(type, mission.getProgress().getOrDefault(type, 0) + 1);
        }
    }
}
