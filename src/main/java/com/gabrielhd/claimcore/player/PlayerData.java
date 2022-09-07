package com.gabrielhd.claimcore.player;

import lombok.Getter;

import java.util.UUID;

public class PlayerData {

    @Getter private final UUID uuid;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
    }
}
