package com.gabrielhd.claimcore.claims;

import lombok.Getter;
import org.bukkit.Chunk;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Claim {

    @Getter private final UUID owner;

    @Getter private final Set<UUID> members;

    @Getter private final Set<Chunk> chunks;

    public Claim(UUID uuid) {
        this.owner = uuid;

        this.members = new HashSet<>();
        this.chunks = new HashSet<>();
    }
}
