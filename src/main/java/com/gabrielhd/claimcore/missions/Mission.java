package com.gabrielhd.claimcore.missions;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
public class Mission {

    private final String id;
    private final MissionType type;

    private final Set<String> requiredMission;
    private final Map<String, Integer> required;

    @Setter private String displayName;

    public Mission(String id, MissionType type) {
        this.id = id;
        this.type = type;
        this.displayName = id;

        this.required = new HashMap<>();
        this.requiredMission = new HashSet<>();
    }
}
