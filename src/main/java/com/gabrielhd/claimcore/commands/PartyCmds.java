package com.gabrielhd.claimcore.commands;

import com.gabrielhd.claimcore.ClaimCore;
import com.gabrielhd.claimcore.claims.Claim;
import com.gabrielhd.claimcore.config.Config;
import com.gabrielhd.claimcore.lang.Lang;
import com.gabrielhd.claimcore.manager.ClaimManager;
import com.gabrielhd.claimcore.menu.impl.ConfirmMenu;
import com.gabrielhd.claimcore.menu.impl.MembersMenu;
import com.gabrielhd.claimcore.menu.impl.MissionsMenu;
import com.gabrielhd.claimcore.menu.impl.UpgradesMenu;
import com.gabrielhd.claimcore.utils.Color;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PartyCmds implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender instanceof Player player) {
            if(args.length == 0) {
                Lang.PARTY_COMMANDS_HELP.send(player);
                return true;
            }

            ClaimManager claimManager = ClaimCore.getInstance().getClaimManager();
            Claim claim = claimManager.of(player.getUniqueId());

            if(args.length == 1) {
                if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("crear")) {
                    if(claim != null) {
                        Lang.PARTY_ALREADY_IN.send(player);
                        return true;
                    }

                    claimManager.create(player.getUniqueId());

                    Lang.PARTY_CREATE.send(player);
                    return true;
                }

                if(args[0].equalsIgnoreCase("disband") || args[0].equalsIgnoreCase("eliminar")) {
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

                if(args[0].equalsIgnoreCase("upgrades") || args[0].equalsIgnoreCase("mejoras")) {
                    if(claim == null) {
                        Lang.PARTY_NOT_IN.send(player);
                        return true;
                    }

                    if(Config.UPGRADES_ONLY_OWNER) {
                        if(!claim.isOwner(player.getUniqueId())) {
                            Lang.PARTY_NOT_OWNER.send(player);
                            return true;
                        }
                    }

                    new UpgradesMenu(claim, null).openInventory(player);
                    return true;
                }

                if(args[0].equalsIgnoreCase("missions") || args[0].equalsIgnoreCase("misiones")) {
                    if(claim == null) {
                        Lang.PARTY_NOT_IN.send(player);
                        return true;
                    }

                    new MissionsMenu(claim, null).openInventory(player);
                    return true;
                }

                if(args[0].equalsIgnoreCase("members") || args[0].equalsIgnoreCase("miembros")) {
                    if(claim == null) {
                        Lang.PARTY_NOT_IN.send(player);
                        return true;
                    }

                    new MembersMenu(claim, 0, null).openInventory(player);
                    return true;
                }

                Lang.PARTY_COMMANDS_HELP.send(player);
                return true;
            }

            if(args.length == 2) {
                if(args[0].equalsIgnoreCase("promote") || args[0].equalsIgnoreCase("promover")) {
                    if(claim == null) {
                        Lang.PARTY_NOT_IN.send(player);
                        return true;
                    }

                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
                    if(!offlinePlayer.hasPlayedBefore()) {
                        Lang.PLAYER_NOT_EXISTS.send(player);
                        return true;
                    }

                    new ConfirmMenu(promoted -> {
                        if(promoted) {
                            claim.promoteMember(offlinePlayer);
                        }
                    }).openInventory(player);
                    return true;
                }
            }
        }
        return false;
    }

    public void sendHelpEn(CommandSender sender) {
        sender.sendMessage(Color.text("  &a/party create &8- &7Create a new party."));
        sender.sendMessage(Color.text("  &a/party invite <Player> &8- &7Invite a player to your party."));
        sender.sendMessage(Color.text("  &a/party kick <Player> &8- &7Kick a player from your party."));
        sender.sendMessage(Color.text("  &a/party promote <Player> &8- &7You promote the player."));
        sender.sendMessage(Color.text("  &a/party demote <Player> &8- &7You demote the player."));
        sender.sendMessage(Color.text("  &a/party upgrades &8- &7Claim Upgrades."));
        sender.sendMessage(Color.text("  &a/party missions &8- &7Claim Missions."));
        sender.sendMessage(Color.text("  &a/party members &8- &7Party Members."));
        sender.sendMessage(Color.text("  &a/party disband &8- &7Disband your current party."));
    }
}
