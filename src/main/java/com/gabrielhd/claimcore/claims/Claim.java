package com.gabrielhd.claimcore.claims;

import com.gabrielhd.claimcore.missions.Mission;
import com.gabrielhd.claimcore.missions.MissionProgress;
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
    
    @Getter @Setter private MissionProgress currentMission;

    private final List<String> completedMissions;

    public Claim(UUID uuid) {
        this.owner = uuid;

        this.members = new HashSet<>();
        this.chunks = new HashSet<>();

        this.upgrades = new HashMap<>();
        this.completedMissions = new ArrayList<>();
    }

    public static Claim of(UUID uuid) {
        return claims.getOrDefault(uuid, null);
    }

    public boolean hasCompleted(Mission mission) {
        return this.completedMissions.contains(mission.getName().toLowerCase(Locale.ROOT));
    }
}
