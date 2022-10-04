package com.gabrielhd.claimcore.missions;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
public class Mission {

    private final int tier;
    private final String id;
    private final MissionType type;

    private double exp;

    private final List<String> rewards;
    private final Set<String> requiredMission;
    private final Map<String, Integer> required;

    @Setter private String displayName;

    public Mission(int tier, String id, MissionType type) {
        this.tier = tier;
        this.id = id;
        this.type = type;
        this.displayName = id;

        this.rewards = new ArrayList<>();
        this.required = new HashMap<>();
        this.requiredMission = new HashSet<>();
    }
}
