package com.gabrielhd.claimcore.menu.impl;

import com.gabrielhd.claimcore.ClaimCore;
import com.gabrielhd.claimcore.claims.Claim;
import com.gabrielhd.claimcore.config.Config;
import com.gabrielhd.claimcore.config.YamlConfig;
import com.gabrielhd.claimcore.lang.Lang;
import com.gabrielhd.claimcore.menu.Menu;
import com.gabrielhd.claimcore.upgrades.Upgrades;
import com.gabrielhd.claimcore.utils.TextPlaceholders;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class UpgradesMenu extends Menu {

    private final Claim claim;

    public UpgradesMenu(Claim claim, Menu previous) {
        super(new YamlConfig(ClaimCore.getInstance(), "menus/Upgrades"));
        this.setPreviousMenu(previous);

        this.claim = claim;
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        if(this.getActions().containsKey(event.getSlot())) {
            if(this.getActions().get(event.getSlot()).equalsIgnoreCase("back") && this.getPreviousMenu() != null) {
                this.getPreviousMenu().openInventory(player);
                return;
            }

            for(Upgrades upgrades : Upgrades.values()) {
                String upgradeName = upgrades.name().replace("_", "");

                if(this.getActions().get(event.getSlot()).equalsIgnoreCase(upgradeName)) {
                    int currentLevel = this.claim.getUpgradeLevel(upgrades);

                    double currentMoney = this.claim.getMoney();

                    if(!Config.UPGRADES_LIMITS.get(upgrades).containsKey(currentLevel + 1)) {
                        Lang.UPGRADE_MAX_LEVEL.send(new TextPlaceholders().set("%upgrade%", upgradeName), player);
                        return;
                    }

                    if(Config.UPGRADES_COSTS.get(upgrades).get(currentLevel + 1) > currentMoney) {
                        Lang.INSUFFICIENT_MONEY.send(player);
                        return;
                    }

                    this.claim.setMoney(currentMoney - Config.UPGRADES_COSTS.get(upgrades).get(currentLevel + 1));
                    this.claim.addLevelToUpgrade(upgrades);

                    this.claim.sendMessage(Lang.UPGRADE_PURCHASED, new TextPlaceholders().set("%upgrade%", upgradeName));
                    return;
                }
            }
        }
    }
}
