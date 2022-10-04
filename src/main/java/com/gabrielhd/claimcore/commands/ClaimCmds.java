package com.gabrielhd.claimcore.commands;

import com.gabrielhd.claimcore.ClaimCore;
import com.gabrielhd.claimcore.claims.Claim;
import com.gabrielhd.claimcore.config.Config;
import com.gabrielhd.claimcore.lang.Lang;
import com.gabrielhd.claimcore.manager.ClaimManager;
import com.gabrielhd.claimcore.upgrades.Upgrades;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ClaimCmds implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(sender instanceof Player player) {
            ClaimManager claimManager = ClaimCore.getInstance().getClaimManager();
            Claim claim = claimManager.getClaimOfPlayer(player.getUniqueId());

            if (claim == null) {
                Lang.PARTY_NOT_IN.send(player);
                return true;
            }

            if(!claim.isOwner(player.getUniqueId())) {
                Lang.PARTY_NOT_OWNER.send(player);
                return true;
            }

            if(Config.UPGRADES_LIMITS.get(Upgrades.CLAIMS).getOrDefault(claim.getUpgradeLevel(Upgrades.CLAIMS), claim.getChunks().size()) >= claim.getChunks().size()) {
                Lang.CHUNK_MAX_LIMIT.send(player);
                return true;
            }

            Chunk chunk = player.getLocation().getChunk();

            if(Config.CLAIM_GEN) {

            } else {
                if(!chunk.isLoaded()) chunk.load();

                if (claimManager.getClaimChunk(chunk) != null) {
                    Lang.CHUNK_ALREADY_CLAIM.send(player);
                    return true;
                }

                if(claim.getChunks().add(chunk)) {
                    Lang.CHUNK_CLAIMED.send(player);
                } else {
                    Lang.CHUNK_ALREADY_CLAIMED.send(player);
                }
                return true;
            }
            return true;
        }
        return false;
    }
}
