package com.gabrielhd.claimcore.manager;

import com.gabrielhd.claimcore.ClaimCore;
import com.gabrielhd.claimcore.config.YamlConfig;
import com.gabrielhd.claimcore.missions.Mission;
import com.gabrielhd.claimcore.missions.MissionType;
import com.gabrielhd.claimcore.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class MissionsManager {

    private static final List<Mission> missions = new ArrayList<>();

    public MissionsManager() {
        ClaimCore.getInstance().getLogger().log(Level.INFO, "Loading missions config...");

        this.loadMissions();
    }

    private void loadMissions() {
        YamlConfig missionsConfig = new YamlConfig(ClaimCore.getInstance(), "Missions");

        ConfigurationSection missionsSections = missionsConfig.getConfigurationSection("Missions");
        for (String missionType : missionsSections.getKeys(false)) {
            ConfigurationSection missionConfig = missionsSections.getConfigurationSection(missionType);

            for (String tier : missionConfig.getConfigurationSection("Tiers").getKeys(false)) {
                if (Utils.isNotInt(tier)) continue;

                for (String missionId : missionConfig.getConfigurationSection("Tiers." + tier).getKeys(false)) {
                    if (getMission(missionId) != null) continue;

                    String path = "Tiers." + tier + "." + missionId + ".";

                    Mission mission = new Mission(Integer.parseInt(tier), missionId, MissionType.valueOf(missionType.toUpperCase()));
                    mission.setDisplayName(missionConfig.getString(path + "DisplayName", missionId));

                    for (String required : missionConfig.getConfigurationSection(path + "Required").getKeys(false)) {
                        mission.getRequired().put(required.toUpperCase(), missionConfig.getInt(path + "Required." + required, 1));
                    }

                    if(missionConfig.isSet(path + "Required-mission")) {
                        mission.getRequiredMission().addAll(missionConfig.getStringList(path + "Required-mission"));
                    }
                    mission.getRewards().addAll(missionConfig.getStringList(path + "Rewards"));

                    missions.add(mission);
                }
            }
        }

        ClaimCore.getInstance().getLogger().log(Level.INFO, "Missions loaded correctly!");
    }

    public Mission getMission(String id) {
        return missions.stream().filter(mission -> mission.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
    }

    public List<Mission> getMissions(int tier, MissionType type) {
        return missions.stream().filter(mission -> mission.getTier() == tier && mission.getType() == type).collect(Collectors.toList());
    }

    public List<Mission> getMissions(int tier) {
        return missions.stream().filter(mission -> mission.getTier() == tier).collect(Collectors.toList());
    }
}
