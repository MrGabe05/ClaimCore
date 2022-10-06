package com.gabrielhd.claimcore.manager;

import com.gabrielhd.claimcore.ClaimCore;
import com.gabrielhd.claimcore.claims.Claim;
import com.gabrielhd.claimcore.database.Database;
import lombok.Getter;
import org.bukkit.Chunk;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class ClaimManager {

    @Getter private static final Map<UUID, Claim> claims = new HashMap<>();

    public ClaimManager() {
        ClaimCore.getInstance().getLogger().log(Level.INFO, "Loading claims...");

        Database.getStorage().loadClaims(list -> {
            for(Claim claim : list) {
                claim.calcHoppers();
                claim.calcSpawners();

                claims.put(claim.getOwner(), claim);
            }

            ClaimCore.getInstance().getLogger().log(Level.INFO, "Claims loaded correctly!");
        });
    }

    public Claim of(UUID uuid) {
        return claims.getOrDefault(uuid, null);
    }

    public Claim getClaimOfPlayer(UUID uuid) {
        return claims.values().stream().filter(claim -> claim.isMember(uuid)).findFirst().orElse(null);
    }

    public Claim getClaimChunk(Chunk chunk) {
        return claims.values().stream().filter(claim -> claim.containsChunk(chunk)).findFirst().orElse(null);
    }

    public void create(UUID uuid) {
        Claim claim = new Claim(uuid);

        claims.put(claim.getClaim(), claim);

        Database.getStorage().saveClaim(claim, true);
    }

    public void delete(Claim claim) {
        Database.getStorage().deleteClaim(claim.getClaim(), removed -> claims.remove(claim.getClaim()));
    }
}
