package com.gabrielhd.claimcore.commands;

import com.gabrielhd.claimcore.ClaimCore;
import com.gabrielhd.claimcore.claims.Claim;
import com.gabrielhd.claimcore.config.Config;
import com.gabrielhd.claimcore.lang.Lang;
import com.gabrielhd.claimcore.manager.ClaimManager;
import com.gabrielhd.claimcore.menu.impl.*;
import com.gabrielhd.claimcore.player.PlayerRole;
import com.gabrielhd.claimcore.upgrades.Upgrades;
import com.gabrielhd.claimcore.utils.Clickable;
import com.gabrielhd.claimcore.utils.TextPlaceholders;
import com.gabrielhd.claimcore.visualization.Visualization;
import com.gabrielhd.claimcore.visualization.VisualizationType;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class PartyCmds implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(sender instanceof Player player) {
            ClaimManager claimManager = ClaimCore.getInstance().getClaimManager();
            Claim claim = claimManager.getClaimOfPlayer(player.getUniqueId());

            if(args.length == 1) {
                switch (args[0].toLowerCase(Locale.ROOT)) {
                    case "crear", "create" -> {
                        if(claim != null) {
                            Lang.PARTY_ALREADY_IN.send(player);
                            return true;
                        }

                        claimManager.create(player.getUniqueId());

                        Lang.PARTY_CREATE.send(player);
                        return true;
                    }
                    case "vault", "backpack" -> {
                        if(claim == null) {
                            Lang.PARTY_NOT_IN.send(player);
                            return true;
                        }

                        player.openInventory(claim.getInventory());
                        return true;
                    }
                    case "calc", "level", "nivel" -> {
                        if(claim == null) {
                            Lang.PARTY_NOT_IN.send(player);
                            return true;
                        }

                        claim.calcExp();
                        return true;
                    }
                    case "settings", "ajustes" -> {
                        if(claim == null) {
                            Lang.PARTY_NOT_IN.send(player);
                            return true;
                        }

                        new SettingsMenu(claim).openInventory(player);
                        return true;
                    }
                    case "disband", "eliminar" -> {
                        if(claim == null) {
                            Lang.PARTY_NOT_IN.send(player);
                            return true;
                        }

                        if(!claim.isOwner(player.getUniqueId())) {
                            Lang.PARTY_NOT_OWNER.send(player);
                            return true;
                        }

                        new ConfirmMenu(delete -> {
                            if(delete) {
                                claimManager.delete(claim);

                                Lang.PARTY_DISBAND.send(player);
                            }
                        }).openInventory(player);
                        return true;
                    }
                    case "upgrades", "mejoras" -> {
                        if(claim == null) {
                            Lang.PARTY_NOT_IN.send(player);
                            return true;
                        }

                        new UpgradesMenu(claim, null).openInventory(player);
                        return true;
                    }
                    case "missions", "misiones" -> {
                        if(claim == null) {
                            Lang.PARTY_NOT_IN.send(player);
                            return true;
                        }

                        new MissionsMenu(claim, null).openInventory(player);
                        return true;
                    }
                    case "members", "miembros" -> {
                        if(claim == null) {
                            Lang.PARTY_NOT_IN.send(player);
                            return true;
                        }

                        new MembersMenu(claim,null).openInventory(player);
                        return true;
                    }
                    case "leave", "salir" -> {
                        if(claim == null) {
                            Lang.PARTY_NOT_IN.send(player);
                            return true;
                        }

                        if(claim.removeMember(player.getUniqueId())) {
                            claim.sendMessage(Lang.PARTY_PLAYER_QUIT, new TextPlaceholders().set("%player%", player.getName()));
                        }
                        return true;
                    }
                    case "claim", "reclamar" -> {
                        if (claim == null) {
                            Lang.PARTY_NOT_IN.send(player);
                            return true;
                        }

                        if(!claim.isOwner(player.getUniqueId())) {
                            Lang.PARTY_NOT_OWNER.send(player);
                            return true;
                        }

                        if(Config.UPGRADES_LIMITS.get(Upgrades.CLAIMS).getOrDefault(claim.getUpgradeLevel(Upgrades.CLAIMS), claim.getChunks().size()) <= claim.getChunks().size()) {
                            Lang.CHUNK_MAX_LIMIT.send(player);
                            return true;
                        }

                        Chunk chunk = player.getLocation().getChunk();

                        if(Config.CLAIM_GEN) {

                        } else {
                            if(!chunk.isLoaded()) chunk.load();

                            if(ClaimCore.getInstance().getWorldGuardHook() != null && !ClaimCore.getInstance().getWorldGuardHook().canClaim(player.getLocation())) {
                                Lang.CHUNK_CANT_CLAIM.send(player);
                                return true;
                            }

                            if(ClaimCore.getInstance().getGriefPreventionHook() != null && !ClaimCore.getInstance().getGriefPreventionHook().canClaim(player)) {
                                Lang.CHUNK_CANT_CLAIM.send(player);
                                return true;
                            }

                            if (claimManager.getClaimChunk(chunk) != null) {
                                Lang.CHUNK_ALREADY_CLAIM.send(player);
                                return true;
                            }

                            if(claim.getChunks().add(chunk)) {
                                Lang.CHUNK_CLAIMED.send(player);

                                Visualization visualization = new Visualization();
                                for(Chunk chunks : claim.getChunks()) {
                                    visualization.addChunkElements(chunks, player.getLocation().getBlockY(), VisualizationType.Chunk, player.getLocation());
                                }

                                Visualization.Apply(player, visualization);
                            } else {
                                Lang.CHUNK_ALREADY_CLAIMED.send(player);
                            }
                            return true;
                        }
                        return true;
                    }
                }
            }

            if(args.length == 2) {
                switch (args[0].toLowerCase(Locale.ROOT)) {
                    case "join", "entrar" -> {
                        if(claim != null) {
                            Lang.PARTY_ALREADY_IN.send(player);
                            return true;
                        }

                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
                        if(!offlinePlayer.hasPlayedBefore()) {
                            Lang.PLAYER_NOT_EXISTS.send(player);
                            return true;
                        }

                        Claim targetClaim = claimManager.getClaimOfPlayer(offlinePlayer.getUniqueId());
                        if(targetClaim == null) {
                            Lang.PARTY_NOT_EXISTS.send(player);
                            return true;
                        }

                        if(targetClaim.isClose() && !targetClaim.hasInvite(player.getUniqueId())) {
                            Lang.PARTY_NOT_INVITE.send(player);
                            return true;
                        }

                        if(targetClaim.addMember(player.getUniqueId())) {
                            targetClaim.sendMessage(Lang.PARTY_PLAYER_JOIN, new TextPlaceholders().set("%player%", player.getName()));
                        }
                        return true;
                    }
                    case "promote", "promover" -> {
                        if(claim == null) {
                            Lang.PARTY_NOT_IN.send(player);
                            return true;
                        }

                        if(!claim.isOwner(player.getUniqueId())) {
                            Lang.PARTY_NOT_OWNER.send(player);
                            return true;
                        }

                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
                        if(!offlinePlayer.hasPlayedBefore()) {
                            Lang.PLAYER_NOT_EXISTS.send(player);
                            return true;
                        }

                        new ConfirmMenu(promoted -> {
                            if(promoted) {
                                claim.promoteMember(offlinePlayer.getUniqueId());
                            }
                        }).openInventory(player);
                        return true;
                    }
                    case "demote", "degradar" -> {
                        if(claim == null) {
                            Lang.PARTY_NOT_IN.send(player);
                            return true;
                        }

                        if(!claim.isOwner(player.getUniqueId())) {
                            Lang.PARTY_NOT_OWNER.send(player);
                            return true;
                        }

                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
                        if(!offlinePlayer.hasPlayedBefore()) {
                            Lang.PLAYER_NOT_EXISTS.send(player);
                            return true;
                        }

                        new ConfirmMenu(demoted -> {
                            if(demoted) {
                                claim.demoteMember(offlinePlayer.getUniqueId());
                            }
                        }).openInventory(player);
                        return true;
                    }
                    case "kick", "expulsar" -> {
                        if(claim == null) {
                            Lang.PARTY_NOT_IN.send(player);
                            return true;
                        }

                        PlayerRole playerRole = claim.getPlayerRole(player.getUniqueId());
                        if(playerRole.hasPermission(PlayerRole.RolePermissions.KICK_PLAYER)) {
                            Lang.PLAYER_NOT_PERMISSIONS.send(player);
                            return true;
                        }

                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
                        if(!offlinePlayer.hasPlayedBefore()) {
                            Lang.PLAYER_NOT_EXISTS.send(player);
                            return true;
                        }

                        if(offlinePlayer.getUniqueId().equals(player.getUniqueId())) {
                            Lang.PARTY_SAME_PLAYER.send(player);
                            return true;
                        }

                        TextPlaceholders placeholders = new TextPlaceholders().set("%player%", offlinePlayer.getName());

                        if(playerRole.getPrority() <= claim.getPlayerRole(offlinePlayer.getUniqueId()).getPrority()) {
                            Lang.PARTY_CANT_KICK.send(placeholders, player);
                            return true;
                        }

                        new ConfirmMenu(kicked -> {
                            if(kicked) {
                                if(claim.removeMember(offlinePlayer.getUniqueId())) {
                                    placeholders.set("%by%", player.getName());

                                    claim.sendMessage(Lang.PARTY_PLAYER_KICK, placeholders);
                                }
                            }
                        }).openInventory(player);
                        return true;
                    }
                    case "invite", "invitar" -> {
                        if(claim == null) {
                            Lang.PARTY_NOT_IN.send(player);
                            return true;
                        }

                        PlayerRole playerRole = claim.getPlayerRole(player.getUniqueId());
                        if(playerRole.hasPermission(PlayerRole.RolePermissions.INVITE_PLAYER)) {
                            Lang.PLAYER_NOT_PERMISSIONS.send(player);
                            return true;
                        }

                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
                        if(!offlinePlayer.hasPlayedBefore()) {
                            Lang.PLAYER_NOT_EXISTS.send(player);
                            return true;
                        }

                        if(!offlinePlayer.isOnline()) {
                            Lang.PLAYER_NOT_ONLINE.send(player);
                            return true;
                        }

                        if(offlinePlayer.getUniqueId().equals(player.getUniqueId())) {
                            Lang.PARTY_SAME_PLAYER.send(player);
                            return true;
                        }

                        if(claim.isMember(offlinePlayer.getUniqueId())) {
                            Lang.PARTY_PLAYER_ALREADY_MEMBER.send(player);
                            return true;
                        }

                        Claim targetClaim = claimManager.getClaimOfPlayer(offlinePlayer.getUniqueId());
                        if(targetClaim != null) {
                            Lang.PARTY_PLAYER_ALREADY_IN.send(player);
                            return true;
                        }

                        if(!claim.invitePlayer(player.getUniqueId())) {
                            Lang.PARTY_ALREADY_INVITE.send(player);
                            return true;
                        }

                        TextPlaceholders ph = new TextPlaceholders().set("%player%", player.getName());

                        Lang.PARTY_INVITE_PLAYER.send(new TextPlaceholders().set("%player%", offlinePlayer.getName()), player);

                        Clickable clickable = new Clickable(Lang.PARTY_INVITE_PLAYER_RECEIVED.get(offlinePlayer.getPlayer(), ph), Lang.PARTY_INVITE_PLAYER_RECEIVED_HOVER.get(offlinePlayer.getPlayer(), ph), "/party join " + player.getName());
                        clickable.sendToPlayer(offlinePlayer.getPlayer());

                        Bukkit.getScheduler().runTaskLaterAsynchronously(ClaimCore.getInstance(), () -> claim.removeInvite(player.getUniqueId()), 20L * 60L * 5L);
                        return true;
                    }
                }
            }

            Lang.PARTY_COMMANDS_HELP.send(player);
            return true;
        }
        return false;
    }
}
