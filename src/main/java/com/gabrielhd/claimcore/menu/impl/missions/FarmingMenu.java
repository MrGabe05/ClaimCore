package com.gabrielhd.claimcore.menu.impl.missions;

import com.gabrielhd.claimcore.ClaimCore;
import com.gabrielhd.claimcore.claims.Claim;
import com.gabrielhd.claimcore.config.YamlConfig;
import com.gabrielhd.claimcore.menu.Menu;
import com.gabrielhd.claimcore.missions.Mission;
import com.gabrielhd.claimcore.missions.MissionProgress;
import com.gabrielhd.claimcore.missions.MissionType;
import com.gabrielhd.claimcore.utils.ItemBuilder;
import com.gabrielhd.claimcore.utils.TextPlaceholders;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;
import java.util.Map;

public class FarmingMenu extends Menu {

    private final Claim claim;

    private final List<Mission> missions;
    private final List<Integer> missionsSlots;

    public FarmingMenu(Claim claim, Menu previous) {
        super(new YamlConfig(ClaimCore.getInstance(), "menus/missions/Farming"));
        this.setPreviousMenu(previous);

        this.claim = claim;

        this.missionsSlots = getSlots(this.getCfg(), "Missions", this.getCharSlots());
        this.missions = ClaimCore.getInstance().getMissionsManager().getMissions(this.claim.getMissionsTier(), MissionType.FARMING);

        this.load();
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event) {

    }

    private void load() {
        YamlConfig missionsConfig = new YamlConfig(ClaimCore.getInstance(), "Missions");

        for(int i = 0; i < this.missionsSlots.size();) {
            if(i >= this.missions.size()) return;

            int slot = this.missionsSlots.get(i);

            Mission mission = this.missions.get(i);
            if(mission == null) continue;

            TextPlaceholders textPlaceholders = new TextPlaceholders();

            StringBuilder requiredString = new StringBuilder();
            for(Map.Entry<String, Integer> value : mission.getRequired().entrySet()) {
                requiredString.append(value.getKey().replace("_", " ")).append(" x").append(value.getValue()).append("\n");
            }
            StringBuilder requiredMissions = new StringBuilder();
            for(String requiredMission : mission.getRequiredMission()) {
                requiredMissions.append(requiredMission).append("\n");
            }

            textPlaceholders.set("%required%", requiredString.toString());
            textPlaceholders.set("%required-missions%", requiredMissions.toString());

            ItemBuilder builder;
            if(!this.claim.hasCompleted(mission)) {
                if (!this.claim.hasCompleted(mission.getRequiredMission())) {
                    builder = getItemStack("Missions.yml", missionsConfig.getConfigurationSection("Missions.Farming.Item.Locked"), textPlaceholders);
                } else if(this.claim.getCurrentMission() != null && this.claim.getCurrentMission().getMission().getId().equalsIgnoreCase(mission.getId())) {
                    MissionProgress currentMission = this.claim.getCurrentMission();

                    int progress = 0;
                    int totalProgress = 0;
                    for(Map.Entry<String, Integer> value : mission.getRequired().entrySet()) {
                        progress += (currentMission.getProgress().get(value.getKey().toLowerCase()) / value.getValue()) * 100;
                        totalProgress += value.getValue();
                    }

                    int finalProgress = (progress / totalProgress) * 100;

                    builder = getItemStack("Missions.yml", missionsConfig.getConfigurationSection("Missions.Farming.Item.Select"), textPlaceholders.set("%progress%", finalProgress));
                } else {
                    builder = getItemStack("Missions.yml", missionsConfig.getConfigurationSection("Missions.Farming.Item.Available"), textPlaceholders);
                }
            } else {
                builder = getItemStack("Missions.yml", missionsConfig.getConfigurationSection("Missions.Farming.Item.Complete"));
            }

            this.setItem(slot, builder.build());

            i++;
        }
    }
}
