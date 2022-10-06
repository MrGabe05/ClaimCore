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
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

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

        this.missionsSlots = getSlots(cfg, "Missions", this.getCharSlots());
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
                    if(this.claim.hasNotCompleted(mission.getRequiredMission())) {
                        Lang.MISSION_LOCKED.send(placeholders, player);

                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1f, 1f);
                        return;
                    }

                    if(this.claim.getCurrentMission() != null) {
                        if(this.claim.getCurrentMission().getMission().getId().equalsIgnoreCase(mission.getId())) {
                            if(this.claim.isCompleted()) {
                                Lang.MISSION_COMPLETED.send(placeholders, player);

                                this.claim.getCompletedMissions().add(mission.getId());
                                this.claim.setCurrentMission(null);

                                this.claim.setExp(this.claim.getExp() + Config.XP_MISSION);

                                mission.getRewards().forEach(reward -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), reward.replace("/", "").replace("%player%", player.getName())));

                                this.load();
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

                    this.load();
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

        String sType = this.type.getColoquial();

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
                if (this.claim.hasNotCompleted(mission.getRequiredMission())) {
                    builder = getItemStack("Missions.yml", missionsConfig.getConfigurationSection("Missions." + sType + ".Item.Locked"), textPlaceholders);
                } else if(this.claim.getCurrentMission() != null && this.claim.getCurrentMission().getMission().getId().equalsIgnoreCase(mission.getId())) {
                    MissionProgress currentMission = this.claim.getCurrentMission();

                    int progress = 0;
                    int totalProgress = 0;

                    for(Map.Entry<String, Integer> value : mission.getRequired().entrySet()) {
                        if(!currentMission.getProgress().containsKey(value.getKey().toLowerCase(Locale.ROOT))) continue;

                        progress += (currentMission.getProgress().get(value.getKey().toLowerCase(Locale.ROOT)) / value.getValue()) * 100;
                        totalProgress += value.getValue();
                    }

                    builder = getItemStack("Missions.yml", missionsConfig.getConfigurationSection("Missions." + sType + ".Item.Select"), textPlaceholders.set("%progress%", (progress == 0 && totalProgress == 0 ? "0%" : (progress / totalProgress) * 100) + "%"));
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

    private void checkPlayerItems(Player player, MissionProgress missionProgress) {
        for(int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack itemStack = player.getInventory().getItem(i);
            if(itemStack == null || itemStack.getType() == Material.AIR) continue;

            Mission mission = missionProgress.getMission();

            String type = itemStack.getType().name();

            if(mission.getRequired().containsKey(type)) {
                missionProgress.getProgress().put(type, missionProgress.getProgress().getOrDefault(type, 0) + itemStack.getAmount());
            }
        }
    }
}
