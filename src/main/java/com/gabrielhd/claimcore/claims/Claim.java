package com.gabrielhd.claimcore.claims;

import com.gabrielhd.claimcore.ClaimCore;
import com.gabrielhd.claimcore.lang.Lang;
import com.gabrielhd.claimcore.missions.Mission;
import com.gabrielhd.claimcore.missions.MissionProgress;
import com.gabrielhd.claimcore.player.PlayerRole;
import com.gabrielhd.claimcore.upgrades.Upgrades;
import com.gabrielhd.claimcore.utils.TextPlaceholders;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.math.IntRange;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

import static com.gabrielhd.claimcore.player.PlayerRole.*;

@Getter @Setter
public class Claim {

    private final UUID owner;
    private final UUID claim;

    private final String ownerName;

    private final Set<UUID> members;
    private final Set<Chunk> chunks;

    private final List<String> completedMissions;
    private final Map<Upgrades, Integer> upgrades;
    private final Map<UUID, PlayerRole> playerRoles;

    private double exp;
    private double money;

    private int level;
    private int missionsTier;

    private MissionProgress currentMission;

    public Claim(UUID uuid) {
        this(uuid, UUID.randomUUID());
    }

    public Claim(UUID uuid, UUID claimUuid) {
        this.owner = uuid;
        this.claim = claimUuid;
        this.ownerName = Bukkit.getOfflinePlayer(uuid).getName();

        this.exp = 0.0;
        this.money = 0.0;

        this.level = 0;
        this.missionsTier = 0;

        this.members = new HashSet<>();
        this.chunks = new HashSet<>();

        this.upgrades = new HashMap<>();
        this.playerRoles = new HashMap<>();
        this.completedMissions = new ArrayList<>();

        this.members.add(uuid);
    }

    public boolean hasCompleted(Set<String> missions) {
        for(String missionName : missions) {
            Mission mission = ClaimCore.getInstance().getMissionsManager().getMission(missionName);

            if(!hasCompleted(mission)) {
                return false;
            }
        }

        return true;
    }

    public boolean hasCompleted(Mission mission) {
        return this.completedMissions.contains(mission.getId().toLowerCase(Locale.ROOT));
    }

    public boolean isCompleted() {
        for(String key : this.currentMission.getMission().getRequired().keySet()) {
            if(this.currentMission.getProgress().getOrDefault(key, 0) < this.currentMission.getMission().getRequired().get(key)) {
                return false;
            }
        }

        return true;
    }

    public boolean isOwner(UUID uuid) {
        return this.owner.equals(uuid);
    }

    public void checkCompletedMissions() {
        if(!hasCompleted(ClaimCore.getInstance().getMissionsManager().getMissions(this.missionsTier).stream().map(Mission::getId).collect(Collectors.toSet()))) {
            return;
        }

        this.missionsTier++;
    }

    public void checkExp() {

    }

    public boolean isInRegion(Location loc) {
        for(Chunk chunk : this.chunks) {
            int posX = chunk.getX() << 4;
            int posZ = chunk.getZ() << 4;

            if(new IntRange(posX, posX + 16).containsDouble(loc.getX()) && new IntRange(0, 255).containsDouble(loc.getY()) && new IntRange(posZ, posZ + 16).containsDouble(loc.getZ())) {
                return true;
            }
        }
        return false;
    }

    public boolean addChunk(Chunk chunk) {
        return this.chunks.add(chunk);
    }

    public int getUpgradeLevel(Upgrades upgrades) {
        return this.upgrades.getOrDefault(upgrades, 1);
    }

    public void addLevelToUpgrade(Upgrades upgrades) {
        this.upgrades.put(upgrades, this.getUpgradeLevel(upgrades) + 1);
    }

    public void promoteMember(OfflinePlayer player) {
        PlayerRole oldPlayerRole = this.playerRoles.getOrDefault(player.getUniqueId(), MEMBER);

        if (MEMBER.equals(oldPlayerRole)) {
            this.playerRoles.put(player.getUniqueId(), MOD);
        } else if (MOD.equals(oldPlayerRole)) {
            this.playerRoles.put(player.getUniqueId(), ADMIN);
        } else if (ADMIN.equals(oldPlayerRole)) {
            this.playerRoles.put(player.getUniqueId(), OWNER);
        }
    }

    public void demoteMember(OfflinePlayer player) {
        PlayerRole oldPlayerRole = this.playerRoles.getOrDefault(player.getUniqueId(), MEMBER);

        if(ADMIN.equals(oldPlayerRole)) {
            this.playerRoles.put(player.getUniqueId(), MOD);
        } else if(MOD.equals(oldPlayerRole)) {
            this.playerRoles.put(player.getUniqueId(), MEMBER);
        }
    }

    public boolean addMember(OfflinePlayer player) {
        if(this.members.add(player.getUniqueId())) {
            this.playerRoles.put(player.getUniqueId(), MEMBER);
            return true;
        }
        return false;
    }

    public boolean removeMember(OfflinePlayer player) {
        if(this.members.remove(player.getUniqueId())) {
            this.playerRoles.remove(player.getUniqueId());
            return true;
        }
        return false;
    }

    public List<OfflinePlayer> getPlayers() {
        return this.members.stream().map(Bukkit::getOfflinePlayer).collect(Collectors.toList());
    }

    public void sendMessage(Lang message, TextPlaceholders textPlaceholders) {
        message.send(textPlaceholders, this.members.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).distinct().toArray(Player[]::new));
    }
}
