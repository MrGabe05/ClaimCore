package com.gabrielhd.claimcore.missions;

import lombok.Getter;

public enum MissionType {

    MINING("Mining"),
    KILLING("Killing"),
    CRAFTING("Crafting"),
    FARMING("Farming");

    @Getter private final String coloquial;

    MissionType(String coloquialName) {
        this.coloquial = coloquialName;
    }
}
