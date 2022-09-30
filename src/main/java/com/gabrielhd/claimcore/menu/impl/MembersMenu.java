package com.gabrielhd.claimcore.menu.impl;

import com.gabrielhd.claimcore.ClaimCore;
import com.gabrielhd.claimcore.claims.Claim;
import com.gabrielhd.claimcore.config.YamlConfig;
import com.gabrielhd.claimcore.menu.Menu;
import com.gabrielhd.claimcore.utils.ItemBuilder;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class MembersMenu extends Menu {

    private final int page;
    private final Claim claim;

    private final List<Integer> memberSlots;

    public MembersMenu(Claim claim, int page, Menu previous) {
        super(new YamlConfig(ClaimCore.getInstance(), "menus/Members"));
        this.setPreviousMenu(previous);

        this.page = page;
        this.claim = claim;

        this.memberSlots = getSlots(this.getCfg(), "Members", this.getCharSlots());

        this.load();
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        if(this.getActions().containsKey(event.getSlot()) && this.getActions().get(event.getSlot()).equalsIgnoreCase("back") && this.getPreviousMenu() != null) {
            this.getPreviousMenu().openInventory(player);
        }
    }

    private void load() {
        List<OfflinePlayer> players = this.claim.getPlayers();

        for(int member = (this.page * this.memberSlots.size()); member < ((this.page + 1) * this.memberSlots.size()); member++) {
            if(players.size() < member) {
                return;
            }

            int slot = this.memberSlots.get(member);

            OfflinePlayer player = players.get(member);

            ConfigurationSection section = this.getCfg().getConfigurationSection("MembersItem");

            ItemBuilder itemBuilder = getItemStack("Members.yml", section);
            if(itemBuilder != null) {
                this.setItem(slot, itemBuilder.asSkullOf(player).build(player));
            }
        }
    }
}
