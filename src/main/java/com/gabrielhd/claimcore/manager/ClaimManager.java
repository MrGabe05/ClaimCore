package com.gabrielhd.claimcore.manager;

import com.gabrielhd.claimcore.claims.Claim;
import lombok.Getter;
import org.bukkit.Chunk;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class ClaimManager {

    @Getter private final Map<UUID, Claim> claims = new HashMap<>();

    public Claim of(UUID uuid) {
        return claims.getOrDefault(uuid, null);
    }

    public Claim getClaimChunk(Chunk chunk) {
        for(Claim claim : claims.values()) {
            Set<Chunk> chunks = claim.getChunks().stream().filter(claimChunk -> claimChunk.getX() == chunk.getX() && claimChunk.getZ() == chunk.getZ()).collect(Collectors.toSet());

            if(!chunks.isEmpty()) return claim;
        }
        return null;
    }
}
