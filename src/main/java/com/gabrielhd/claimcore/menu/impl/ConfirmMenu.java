package com.gabrielhd.claimcore.menu.impl;

import com.gabrielhd.claimcore.ClaimCore;
import com.gabrielhd.claimcore.config.YamlConfig;
import com.gabrielhd.claimcore.menu.Menu;
import com.gabrielhd.claimcore.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.function.Consumer;

public class ConfirmMenu extends Menu {

    private final Consumer<Boolean> action;

    public ConfirmMenu(Consumer<Boolean> action) {
        super(new YamlConfig(ClaimCore.getInstance(), "menus/Confirm"));

        this.action = action;
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        if(this.getActions().containsKey(event.getSlot())) {
            this.action.accept(Utils.parseBoolean(this.getActions().get(event.getSlot())));

            player.closeInventory();
        }
    }
}
