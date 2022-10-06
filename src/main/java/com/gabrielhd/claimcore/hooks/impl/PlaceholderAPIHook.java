package com.gabrielhd.claimcore.hooks.impl;

import com.gabrielhd.claimcore.ClaimCore;
import com.gabrielhd.claimcore.claims.Claim;
import com.gabrielhd.claimcore.config.Config;
import com.gabrielhd.claimcore.upgrades.Upgrades;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    @Override
    public String onPlaceholderRequest(Player player, String s) {
        Claim claim = ClaimCore.getInstance().getClaimManager().getClaimOfPlayer(player.getUniqueId());

        if(claim != null) {
            switch (s.toLowerCase()) {
                case "uuid" -> {
                    return claim.getClaim().toString();
                }
                case "owner" -> {
                    return claim.getOwnerName();
                }
                case "owner_uuid" -> {
                    return claim.getOwner().toString();
                }
                case "chunks" -> {
                    return String.valueOf(claim.getChunks().size());
                }
                case "members" -> {
                    return String.valueOf(claim.getMembers().size());
                }
                case "money" -> {
                    return String.valueOf(claim.getMoney());
                }
                case "exp" -> {
                    return String.valueOf(claim.getExp());
                }
                case "level" -> {
                    return String.valueOf(claim.getLevel());
                }
            }

            for (Upgrades upgrades : Upgrades.values()) {
                String upgradeName = (upgrades.name().replace("_", "").toLowerCase());

                int upgradeLevel = claim.getUpgradeLevel(upgrades);

                if (s.equalsIgnoreCase("upgrades_" + upgradeName + "_level")) return String.valueOf(upgradeLevel);
                if (s.equalsIgnoreCase(upgradeName + "_limits"))
                    return String.valueOf(Config.UPGRADES_LIMITS.get(upgrades).get(upgradeLevel));
            }
        }

        return "";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "claimcore";
    }

    @Override
    public @NotNull String getAuthor() {
        return "GabrielHD55";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }
}
