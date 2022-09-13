package com.gabrielhd.claimcore.player;

import com.gabrielhd.claimcore.claims.Claim;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {

    @Getter private static final Map<UUID, PlayerData> players = new HashMap<>();

    @Getter private final UUID uuid;

    @Getter @Setter private Claim claim;
    @Getter @Setter private MissionPlayer currentMission;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
    }

    public static PlayerData of(UUID uuid) {
        return players.getOrDefault(uuid, null);
    }
}
