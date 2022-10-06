package com.gabrielhd.claimcore.player;

import com.gabrielhd.claimcore.ClaimCore;
import com.gabrielhd.claimcore.claims.Claim;
import com.gabrielhd.claimcore.visualization.Visualization;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Chunk;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {

    @Getter private static final Map<UUID, PlayerData> players = new HashMap<>();

    @Getter private final UUID uuid;

    @Getter @Setter private Claim claim;

    public boolean inClaim;
    public Claim lastClaim;
    public Chunk lastChunk;
    public Visualization currentVisualization;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        this.claim = ClaimCore.getInstance().getClaimManager().getClaimOfPlayer(uuid);

        players.put(this.uuid, this);
    }

    public static PlayerData of(UUID uuid) {
        return players.getOrDefault(uuid, null);
    }

    public static void load(UUID uuid) {
        new PlayerData(uuid);
    }
}
