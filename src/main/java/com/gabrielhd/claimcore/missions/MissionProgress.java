package com.gabrielhd.claimcore.missions;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class MissionProgress {

    @Getter private final Mission mission;

    @Getter private final Map<String, Integer> progress;

    public MissionProgress(Mission mission) {
        this.mission = mission;

        this.progress = new HashMap<>();
    }
}
