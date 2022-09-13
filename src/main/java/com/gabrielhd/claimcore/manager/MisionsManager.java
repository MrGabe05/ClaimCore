package com.gabrielhd.claimcore.manager;

import com.gabrielhd.claimcore.Main;
import com.gabrielhd.claimcore.config.YamlConfig;
import com.gabrielhd.claimcore.missions.Mission;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class MisionsManager {

    @Getter private final List<Mission> missions = new ArrayList<>();

    private void loadMissions() {
        YamlConfig missionsConfig = new YamlConfig(Main.getInstance(), "Missions");


    }
}
