package com.gabrielhd.claimcore.missions;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class MissionPlayer {

    @Getter private final String name;

    @Getter private final MissionType type;

    @Getter private final List<String> required;

    public MissionPlayer(String name, MissionType type) {
        this.name = name;
        this.type = type;

        this.required = new ArrayList<>();
    }
}
