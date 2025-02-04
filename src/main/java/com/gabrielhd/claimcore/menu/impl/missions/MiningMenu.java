package com.gabrielhd.claimcore.menu.impl.missions;

import com.gabrielhd.claimcore.ClaimCore;
import com.gabrielhd.claimcore.claims.Claim;
import com.gabrielhd.claimcore.config.YamlConfig;
import com.gabrielhd.claimcore.menu.Menu;
import com.gabrielhd.claimcore.menu.impl.MissionMenu;
import com.gabrielhd.claimcore.missions.MissionType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MiningMenu extends MissionMenu {

    public MiningMenu(Claim claim, Menu previous) {
        super(new YamlConfig(ClaimCore.getInstance(), "menus/missions/Mining"));

        this.setPreviousMenu(previous);
        this.setClaim(claim);
        this.setType(MissionType.MINING);
        this.setMissions(ClaimCore.getInstance().getMissionsManager().getMissions(claim.getTier(), MissionType.MINING));

        this.load();
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        super.onClick(player, event);
    }
}
