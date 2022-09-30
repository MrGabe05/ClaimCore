package com.gabrielhd.claimcore.menu.impl;

import com.gabrielhd.claimcore.ClaimCore;
import com.gabrielhd.claimcore.claims.Claim;
import com.gabrielhd.claimcore.config.YamlConfig;
import com.gabrielhd.claimcore.menu.Menu;
import com.gabrielhd.claimcore.menu.impl.missions.CraftingMenu;
import com.gabrielhd.claimcore.menu.impl.missions.FarmingMenu;
import com.gabrielhd.claimcore.menu.impl.missions.KillingMenu;
import com.gabrielhd.claimcore.menu.impl.missions.MiningMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MissionsMenu extends Menu {

    private final Claim claim;

    public MissionsMenu(Claim claim, Menu previous) {
        super(new YamlConfig(ClaimCore.getInstance(), "menus/Missions"));
        this.setPreviousMenu(previous);

        this.claim = claim;
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        if(this.getActions().containsKey(event.getSlot())) {
            switch (this.getActions().get(event.getSlot()).toLowerCase()) {
                case "mining" -> new MiningMenu(this.claim, this).openInventory(player);
                case "killing" -> new KillingMenu(this.claim, this).openInventory(player);
                case "farming" -> new FarmingMenu(this.claim, this).openInventory(player);
                case "crafting" -> new CraftingMenu(this.claim, this).openInventory(player);
                case "back" -> {
                    if (this.getPreviousMenu() != null) this.getPreviousMenu().openInventory(player);
                }
            }
        }
    }
}
