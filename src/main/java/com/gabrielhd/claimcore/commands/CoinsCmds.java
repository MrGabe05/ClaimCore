package com.gabrielhd.claimcore.commands;

import com.gabrielhd.claimcore.ClaimCore;
import com.gabrielhd.claimcore.claims.Claim;
import com.gabrielhd.claimcore.lang.Lang;
import com.gabrielhd.claimcore.manager.ClaimManager;
import com.gabrielhd.claimcore.player.PlayerRole;
import com.gabrielhd.claimcore.utils.TextPlaceholders;
import com.gabrielhd.claimcore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class CoinsCmds implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player player) {
            ClaimManager claimManager = ClaimCore.getInstance().getClaimManager();
            Claim claim = claimManager.getClaimOfPlayer(player.getUniqueId());

            if(args.length == 0) {
                if(claim == null) {
                    Lang.PARTY_NOT_IN.send(player);
                    return true;
                }
                Lang.COINS_BALANCE.send(new TextPlaceholders().set("%money%", claim.getMoney()), player);
                return true;
            }

            if(args.length == 3) {
                switch (args[0].toLowerCase(Locale.ROOT)) {
                    case "pay", "pagar" -> {
                        if(claim == null) {
                            Lang.PARTY_NOT_IN.send(player);
                            return true;
                        }

                        PlayerRole playerRole = claim.getPlayerRole(player.getUniqueId());
                        if(playerRole.hasPermission(PlayerRole.RolePermissions.CONTROLE_BALANCE)) {
                            Lang.PLAYER_NOT_PERMISSIONS.send(player);
                            return true;
                        }

                        if(Utils.isNotInt(args[2])) {
                            Lang.COINS_AMOUNT_NOT_NUMBER.send(player);
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

                        double amount = Double.parseDouble(args[2]);
                        double money = claim.getMoney();

                        if(money < amount) {
                            Lang.INSUFFICIENT_MONEY.send(player);
                            return true;
                        }

                        claim.setMoney(claim.getMoney() - amount);
                        targetClaim.setMoney(targetClaim.getMoney() + amount);
                        targetClaim.sendMessage(Lang.COINS_RECEIVED, new TextPlaceholders().set("%money%", amount));

                        Lang.COINS_PAID.send(new TextPlaceholders().set("%amount%", amount).set("%player%", offlinePlayer.getName()), player);
                        return true;
                    }
                    case "give" -> {
                        if(!player.hasPermission("claimcore.coins.give")) {
                            Lang.PLAYER_NOT_PERMISSIONS.send(player);
                            return true;
                        }

                        if(Utils.isNotInt(args[2])) {
                            Lang.COINS_AMOUNT_NOT_NUMBER.send(player);
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
                        double amount = Double.parseDouble(args[2]);

                        targetClaim.setMoney(targetClaim.getMoney() + amount);
                        targetClaim.sendMessage(Lang.COINS_RECEIVED, new TextPlaceholders().set("%money%", amount));

                        Lang.COINS_GIVED.send(new TextPlaceholders().set("%money%", amount).set("%player%", offlinePlayer.getName()), player);
                        return true;
                    }
                    case "take", "remove" -> {
                        if(!player.hasPermission("claimcore.coins.take")) {
                            Lang.PLAYER_NOT_PERMISSIONS.send(player);
                            return true;
                        }

                        if(Utils.isNotInt(args[2])) {
                            Lang.COINS_AMOUNT_NOT_NUMBER.send(player);
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
                        double amount = Double.parseDouble(args[2]);

                        targetClaim.setMoney(Math.max(0.0, targetClaim.getMoney() - amount));
                        Lang.COINS_TAKE.send(new TextPlaceholders().set("%money%", amount).set("%player%", offlinePlayer.getName()), player);
                        return true;
                    }
                    case "set" -> {
                        if(!player.hasPermission("claimcore.coins.set")) {
                            Lang.PLAYER_NOT_PERMISSIONS.send(player);
                            return true;
                        }

                        if(Utils.isNotInt(args[2])) {
                            Lang.COINS_AMOUNT_NOT_NUMBER.send(player);
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
                        double amount = Double.parseDouble(args[2]);

                        targetClaim.setMoney(Math.max(0.0, amount));
                        Lang.COINS_SET.send(new TextPlaceholders().set("%money%", amount).set("%player%", offlinePlayer.getName()), player);
                        return true;
                    }
                }
            }

            Lang.COINS_COMMANDS_HELP.send(player);
            return true;
        }
        return false;
    }
}
