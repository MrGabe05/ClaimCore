package com.gabrielhd.claimcore.menu.impl.missions;

import com.gabrielhd.claimcore.ClaimCore;
import com.gabrielhd.claimcore.claims.Claim;
import com.gabrielhd.claimcore.config.YamlConfig;
import com.gabrielhd.claimcore.menu.Menu;
import com.gabrielhd.claimcore.menu.impl.MissionMenu;
import com.gabrielhd.claimcore.missions.MissionType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class KillingMenu extends MissionMenu {

    public KillingMenu(Claim claim, Menu previous) {
        super(new YamlConfig(ClaimCore.getInstance(), "menus/missions/Killing"));

        this.setPreviousMenu(previous);
        this.setClaim(claim);
        this.setMissions(ClaimCore.getInstance().getMissionsManager().getMissions(claim.getMissionsTier(), MissionType.KILLING));

        this.load();
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        super.onClick(player, event);
    }
}
