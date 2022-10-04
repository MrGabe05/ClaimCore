package com.gabrielhd.claimcore.menu.impl;

import com.gabrielhd.claimcore.ClaimCore;
import com.gabrielhd.claimcore.claims.Claim;
import com.gabrielhd.claimcore.config.Config;
import com.gabrielhd.claimcore.config.YamlConfig;
import com.gabrielhd.claimcore.lang.Lang;
import com.gabrielhd.claimcore.menu.Menu;
import com.gabrielhd.claimcore.missions.Mission;
import com.gabrielhd.claimcore.missions.MissionProgress;
import com.gabrielhd.claimcore.missions.MissionType;
import com.gabrielhd.claimcore.utils.ItemBuilder;
import com.gabrielhd.claimcore.utils.TextPlaceholders;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Getter @Setter
public class MissionMenu extends Menu {

    private Claim claim;
    private MissionType type;

    private List<Mission> missions;
    private final List<Integer> missionsSlots;

    public MissionMenu(YamlConfig cfg) {
        super(cfg);

        this.missionsSlots = getSlots(this.getCfg(), "Missions", this.getCharSlots());
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        if(this.getActions().containsKey(event.getSlot()) && this.getActions().get(event.getSlot()).equalsIgnoreCase("back") && this.getPreviousMenu() != null) {
            this.getPreviousMenu().openInventory(player);
            return;
        }

        for(int i = 0; i < this.missionsSlots.size();) {
            if(i >= this.missions.size()) return;

            int slot = this.missionsSlots.get(i);

            if(event.getSlot() == slot) {
                Mission mission = this.missions.get(i);
                if (mission == null) continue;

                TextPlaceholders placeholders = new TextPlaceholders().set("%mission-id%", mission.getId()).set("%mission-name%", mission.getDisplayName());

                if(!this.claim.hasCompleted(mission)) {
                    if(!this.claim.hasCompleted(mission.getRequiredMission())) {
                        Lang.MISSION_LOCKED.send(placeholders, player);
                        return;
                    }

                    if(this.claim.getCurrentMission() != null) {
                        if(this.claim.getCurrentMission().getMission().getId().equalsIgnoreCase(mission.getId())) {
                            if(this.claim.isCompleted()) {
                                Lang.MISSION_COMPLETED.send(placeholders, player);

                                this.claim.getCompletedMissions().add(mission.getId());
                                this.claim.setCurrentMission(null);

                                this.claim.setExp(this.claim.getExp() + Config.XP_MISSION);
                                return;
                            }

                            Lang.MISSION_ALREADY_SELECT.send(placeholders, player);
                            return;
                        }
                        Lang.MISSION_IN_PROGRESS.send(player);
                        return;
                    }

                    this.claim.setCurrentMission(new MissionProgress(mission));
                    Lang.MISSION_SELECT.send(placeholders, player);
                } else {
                    Lang.MISSION_ALREADY_COMPLETED.send(placeholders, player);
                }
            }
            i++;
        }
    }

    protected void load() {
        if(this.missionsSlots == null || this.missionsSlots.isEmpty() || this.type == null) {
            return;
        }

        String sType = this.type.name().toLowerCase(Locale.ROOT);
        String missionType = sType.substring(0, 1).toUpperCase() + sType.substring(1);

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
                    builder = getItemStack("Missions.yml", missionsConfig.getConfigurationSection("Missions." + sType + ".Item.Locked"), textPlaceholders);
                } else if(this.claim.getCurrentMission() != null && this.claim.getCurrentMission().getMission().getId().equalsIgnoreCase(mission.getId())) {
                    MissionProgress currentMission = this.claim.getCurrentMission();

                    int progress = 0;
                    int totalProgress = 0;
                    for(Map.Entry<String, Integer> value : mission.getRequired().entrySet()) {
                        progress += (currentMission.getProgress().get(value.getKey().toLowerCase()) / value.getValue()) * 100;
                        totalProgress += value.getValue();
                    }

                    int finalProgress = (progress / totalProgress) * 100;

                    builder = getItemStack("Missions.yml", missionsConfig.getConfigurationSection("Missions." + sType + ".Item.Select"), textPlaceholders.set("%progress%", finalProgress));
                } else {
                    builder = getItemStack("Missions.yml", missionsConfig.getConfigurationSection("Missions." + sType + ".Item.Available"), textPlaceholders);
                }
            } else {
                builder = getItemStack("Missions.yml", missionsConfig.getConfigurationSection("Missions." + sType + ".Item.Complete"));
            }

            this.setItem(slot, builder.build());

            i++;
        }
    }
}
