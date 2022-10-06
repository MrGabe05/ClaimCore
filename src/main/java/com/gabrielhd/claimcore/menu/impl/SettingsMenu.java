package com.gabrielhd.claimcore.menu.impl;

import com.gabrielhd.claimcore.ClaimCore;
import com.gabrielhd.claimcore.claims.Claim;
import com.gabrielhd.claimcore.config.YamlConfig;
import com.gabrielhd.claimcore.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Locale;

public class SettingsMenu extends Menu {

    private final Claim claim;

    public SettingsMenu(Claim claim) {
        super(new YamlConfig(ClaimCore.getInstance(), "menus/Settings"));

        this.claim = claim;
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        if(this.getActions().containsKey(event.getSlot())) {
            switch (this.getActions().get(event.getSlot()).toLowerCase(Locale.ROOT)) {
                case "missions" -> new MissionsMenu(this.claim, this).openInventory(player);
                case "upgrades" -> new UpgradesMenu(this.claim, this).openInventory(player);
                case "members" -> new MembersMenu(this.claim, this).openInventory(player);
                case "back" -> {
                    if(this.getPreviousMenu() != null) {
                        this.getPreviousMenu().openInventory(player);
                    }
                }
            }
        }
    }
}
