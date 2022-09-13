package com.gabrielhd.claimcore.missions;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class Mission {

    @Getter private final String name;
    @Getter private final String displayName;

    @Getter private final MissionType type;

    @Getter private final Map<String, Integer> required;

    public Mission(String name, MissionType type) {
        this.name = name;
        this.type = type;
        this.displayName = name;

        this.required = new HashMap<>();
    }
}
