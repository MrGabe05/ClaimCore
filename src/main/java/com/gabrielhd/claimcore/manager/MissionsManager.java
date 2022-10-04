package com.gabrielhd.claimcore.manager;

import com.gabrielhd.claimcore.ClaimCore;
import com.gabrielhd.claimcore.config.YamlConfig;
import com.gabrielhd.claimcore.missions.Mission;
import com.gabrielhd.claimcore.missions.MissionType;
import com.gabrielhd.claimcore.utils.Utils;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class MissionsManager {

    @Getter private final List<Mission> missions = new ArrayList<>();

    public MissionsManager() {
        this.loadMissions();
    }

    private void loadMissions() {
        YamlConfig missionsConfig = new YamlConfig(ClaimCore.getInstance(), "Missions");

        ConfigurationSection missionsSections = missionsConfig.getConfigurationSection("Missions");
        for(String missionType : missionsSections.getKeys(false)) {
            ConfigurationSection missionConfig = missionsSections.getConfigurationSection(missionType);

            for(String tier : missionConfig.getConfigurationSection("Tiers").getKeys(false)) {
                if(Utils.isNotInt(tier)) continue;

                for (String missionId : missionConfig.getConfigurationSection("Tiers." + tier).getKeys(false)) {
                    if(getMission(missionId) != null) continue;

                    Mission mission = new Mission(Integer.parseInt(tier), missionId, MissionType.valueOf(missionType.toUpperCase()));
                    mission.setDisplayName(missionConfig.getString("DisplayName", missionId));

                    for (String required : missionConfig.getConfigurationSection("Required").getKeys(false)) {
                        mission.getRequired().put(required.toUpperCase(), missionConfig.getInt(required, 1));
                    }

                    mission.getRewards().addAll(missionConfig.getStringList("Rewards"));
                }
            }
        }
    }

    public Mission getMission(String id) {
        for(Mission mission : this.missions) {
            if(mission.getId().equalsIgnoreCase(id)) {
                return mission;
            }
        }
        return null;
    }

    public List<Mission> getMissions(int tier, MissionType type) {
        List<Mission> missions = new ArrayList<>();

        for(Mission mission : this.missions) {
            if(mission.getTier() == tier && mission.getType() == type) {
                missions.add(mission);
            }
        }

        return missions;
    }

    public List<Mission> getMissions(int tier) {
        List<Mission> missions = new ArrayList<>();

        for(Mission mission : this.missions) {
            if(mission.getTier() == tier) {
                missions.add(mission);
            }
        }

        return missions;
    }
}
