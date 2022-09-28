package com.gabrielhd.claimcore.player;

import com.gabrielhd.claimcore.claims.Claim;
import com.gabrielhd.claimcore.database.Database;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {

    @Getter private static final Map<UUID, PlayerData> players = new HashMap<>();

    @Getter private final UUID uuid;

    @Getter @Setter private Claim claim;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;

        Database.getStorage().loadPlayer(this).thenAccept(loaded -> players.put(this.uuid, this));
    }

    public static PlayerData of(UUID uuid) {
        return players.getOrDefault(uuid, null);
    }

    public static void load(UUID uuid) {
        new PlayerData(uuid);
    }

    public static void save(UUID uuid) {
        Database.getStorage().uploadPlayer(of(uuid));
    }
}
