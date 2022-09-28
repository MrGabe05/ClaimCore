package com.gabrielhd.claimcore.claims;

import com.gabrielhd.claimcore.missions.Mission;
import com.gabrielhd.claimcore.missions.MissionProgress;
import com.gabrielhd.claimcore.upgrades.Upgrades;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Chunk;

import java.util.*;

public class Claim {

    @Getter private final UUID owner;
    @Getter private final UUID claim;

    @Getter private final Set<UUID> members;
    @Getter private final Set<Chunk> chunks;

    @Getter private final Map<Upgrades, Integer> upgrades;
    
    @Getter @Setter private MissionProgress currentMission;

    private final List<String> completedMissions;

    public Claim(UUID uuid) {
        this(uuid, UUID.randomUUID());
    }

    public Claim(UUID uuid, UUID claimUuid) {
        this.owner = uuid;
        this.claim = claimUuid;

        this.members = new HashSet<>();
        this.chunks = new HashSet<>();

        this.upgrades = new HashMap<>();
        this.completedMissions = new ArrayList<>();
    }

    public boolean hasCompleted(Mission mission) {
        return this.completedMissions.contains(mission.getId().toLowerCase(Locale.ROOT));
    }
}
