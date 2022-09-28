package com.gabrielhd.claimcore.manager;

import com.gabrielhd.claimcore.ClaimCore;
import com.gabrielhd.claimcore.config.YamlConfig;
import com.gabrielhd.claimcore.missions.Mission;
import com.gabrielhd.claimcore.missions.MissionType;
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

            for(String missionId : missionConfig.getKeys(false)) {
                Mission mission = new Mission(missionId, MissionType.valueOf(missionType.toUpperCase()));

                mission.setDisplayName(missionConfig.getString("DisplayName", missionId));

                for(String required : missionConfig.getConfigurationSection("Required").getKeys(false)) {
                    mission.getRequired().put(required, missionConfig.getInt(required, 1));
                }
            }
        }
    }
}
