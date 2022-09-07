package com.gabrielhd.claimcore.claims;

import com.gabrielhd.claimcore.missions.MissionClaim;
import com.gabrielhd.claimcore.upgrades.Upgrades;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Chunk;

import java.util.*;

public class Claim {

    @Getter private static final Map<UUID, Claim> claims = new HashMap<>();

    @Getter private final UUID owner;

    @Getter private final Set<UUID> members;
    @Getter private final Set<Chunk> chunks;

    @Getter private final Map<Upgrades, Integer> upgrades;
    
    @Getter @Setter private MissionClaim currentMission;

    public Claim(UUID uuid) {
        this.owner = uuid;

        this.members = new HashSet<>();
        this.chunks = new HashSet<>();

        this.upgrades = new HashMap<>();
    }

    public static Claim of(UUID uuid) {
        return claims.getOrDefault(uuid, null);
    }
}
