package com.gabrielhd.claimcore.claims;

import com.gabrielhd.claimcore.ClaimCore;
import com.gabrielhd.claimcore.config.Config;
import com.gabrielhd.claimcore.lang.Lang;
import com.gabrielhd.claimcore.missions.Mission;
import com.gabrielhd.claimcore.missions.MissionProgress;
import com.gabrielhd.claimcore.player.PlayerRole;
import com.gabrielhd.claimcore.upgrades.Upgrades;
import com.gabrielhd.claimcore.utils.Color;
import com.gabrielhd.claimcore.utils.TextPlaceholders;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.math.IntRange;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.*;
import java.util.stream.Collectors;

import static com.gabrielhd.claimcore.player.PlayerRole.*;

@Getter @Setter
public class Claim {

    private final UUID owner;
    private final UUID claim;

    private int tier;
    private int level;
    private double exp;
    private double money;
    private boolean close;

    private Inventory inventory;

    private String resultRoles;
    private String resultUpgrades;
    private final String ownerName;

    private MissionProgress currentMission;

    private Set<UUID> members;
    private Set<UUID> invites;
    private Set<Chunk> chunks;
    private Set<Location> hoppers;
    private Set<Location> spawners;

    private Set<String> completedMissions;
    private Map<String, Integer> upgrades;
    private Map<UUID, PlayerRole> playerRoles;

    public Claim(UUID uuid) {
        this(uuid, UUID.randomUUID());
    }

    public Claim(UUID uuid, UUID claimUuid) {
        this.owner = uuid;
        this.claim = claimUuid;
        this.ownerName = Bukkit.getOfflinePlayer(uuid).getName();

        this.exp = 0.0;
        this.money = 0.0;

        this.level = 1;
        this.tier = 0;

        this.chunks = new HashSet<>();
        this.hoppers = new HashSet<>();
        this.members = new HashSet<>();
        this.invites = new HashSet<>();
        this.spawners = new HashSet<>();

        this.completedMissions = new HashSet<>();

        this.upgrades = new HashMap<>();
        this.playerRoles = new HashMap<>();

        for(Upgrades upgrades : Upgrades.values()) {
            this.upgrades.put(upgrades.name().toLowerCase(Locale.ROOT), 1);
        }

        this.members.add(uuid);
        this.playerRoles.put(uuid, OWNER);

        this.inventory = Bukkit.createInventory(null, 9 * 3, Color.text("&aBackpack"));
    }

    public boolean hasNotCompleted(Set<String> missions) {
        for(String missionName : missions) {
            Mission mission = ClaimCore.getInstance().getMissionsManager().getMission(missionName);

            if(!hasCompleted(mission)) {
                return true;
            }
        }

        return false;
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
        for(Mission mission : ClaimCore.getInstance().getMissionsManager().getMissions(this.tier)) {
            if(!hasCompleted(mission)) {
                return;
            }
        }

        this.tier++;
    }

    public void calcHoppers() {
        Set<Location> locs = new HashSet<>();

        for(Chunk chunk : chunks) {
            int bx = chunk.getX()<<4;
            int bz = chunk.getZ()<<4;

            World world = chunk.getWorld();

            for(int xx = bx; xx < bx+16; xx++) {
                for(int zz = bz; zz < bz+16; zz++) {
                    for(int yy = 0; yy < 128; yy++) {
                        Block block = world.getBlockAt(xx, yy, zz);
                        if(block.getType() == Material.HOPPER) {
                            locs.add(block.getLocation());
                        }
                    }
                }
            }
        }

        this.hoppers = locs;
    }

    public int getHoppersAmount() {
        return this.hoppers.size();
    }

    public void calcSpawners() {
        Set<Location> locs = new HashSet<>();

        for(Chunk chunk : chunks) {
            int bx = chunk.getX()<<4;
            int bz = chunk.getZ()<<4;

            World world = chunk.getWorld();

            for(int xx = bx; xx < bx+16; xx++) {
                for(int zz = bz; zz < bz+16; zz++) {
                    for(int yy = 0; yy < 128; yy++) {
                        Block block = world.getBlockAt(xx, yy, zz);
                        if(block.getType() == Material.SPAWNER) {
                            locs.add(block.getLocation());
                        }
                    }
                }
            }
        }

        this.spawners = locs;
    }

    public int getSpawnersAmount() {
        return this.spawners.size();
    }

    public void updateSpawners(int speed, int amount) {
        this.spawners.removeIf(loc -> loc.getBlock().getType() != Material.SPAWNER);

        for(Location loc : this.spawners) {
            CreatureSpawner spawner = (CreatureSpawner) loc.getBlock().getState();

            spawner.setDelay(speed);
            spawner.setSpawnCount(amount);
        }
    }

    public void calcExp() {
        double base = Config.XP_BASE;
        double increase = Config.XP_INCREASE;

        while(base + increase * this.level <= this.exp) {
            this.level++;
        }

        this.sendMessage(Lang.PARTY_CALC_LEVEL, new TextPlaceholders().set("%exp%", this.exp).set("%level%", this.level));
    }

    public boolean isInRegion(Location loc) {
        for(Chunk chunk : this.chunks) {
            int posX = chunk.getX() << 4;
            int posZ = chunk.getZ() << 4;

            if(new IntRange(posX, posX + 16).containsDouble(loc.getX()) && new IntRange(posZ, posZ + 16).containsDouble(loc.getZ())) {
                return true;
            }
        }
        return false;
    }

    public boolean addChunk(Chunk chunk) {
        return this.chunks.add(chunk);
    }

    public boolean containsChunk(Chunk chunk) {
        return this.chunks.contains(chunk);
    }

    public int getSize() {
        return this.members.size();
    }

    public int getUpgradeLevel(Upgrades upgrades) {
        return this.upgrades.getOrDefault(upgrades.name().toLowerCase(Locale.ROOT), 1);
    }

    public void addLevelToUpgrade(Upgrades upgrades) {
        this.upgrades.put(upgrades.name().toLowerCase(Locale.ROOT), this.getUpgradeLevel(upgrades) + 1);
    }

    public void promoteMember(UUID uuid) {
        PlayerRole oldPlayerRole = this.playerRoles.getOrDefault(uuid, MEMBER);

        if (MEMBER.equals(oldPlayerRole)) {
            this.playerRoles.put(uuid, MOD);
        } else if (MOD.equals(oldPlayerRole)) {
            this.playerRoles.put(uuid, ADMIN);
        } else if (ADMIN.equals(oldPlayerRole)) {
            this.playerRoles.put(uuid, OWNER);
        }
    }

    public void demoteMember(UUID uuid) {
        PlayerRole oldPlayerRole = this.playerRoles.getOrDefault(uuid, MEMBER);

        if(ADMIN.equals(oldPlayerRole)) {
            this.playerRoles.put(uuid, MOD);
        } else if(MOD.equals(oldPlayerRole)) {
            this.playerRoles.put(uuid, MEMBER);
        }
    }

    public boolean addMember(UUID uuid) {
        if(this.members.add(uuid)) {
            this.playerRoles.put(uuid, MEMBER);
            return true;
        }
        return false;
    }

    public boolean removeMember(UUID uuid) {
        if(this.members.remove(uuid)) {
            this.playerRoles.remove(uuid);
            return true;
        }
        return false;
    }

    public boolean isMember(UUID uuid) {
        return this.members.contains(uuid);
    }

    public boolean invitePlayer(UUID uuid) {
        return this.invites.add(uuid);
    }

    public boolean removeInvite(UUID uuid) {
        return this.invites.remove(uuid);
    }

    public boolean hasInvite(UUID uuid) {
        return this.invites.contains(uuid);
    }

    public PlayerRole getPlayerRole(UUID uuid) {
        return this.playerRoles.getOrDefault(uuid, MEMBER);
    }

    public List<OfflinePlayer> getPlayers() {
        return this.members.stream().map(Bukkit::getOfflinePlayer).collect(Collectors.toList());
    }

    public void sendMessage(Lang message, TextPlaceholders textPlaceholders) {
        message.send(textPlaceholders, this.members.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).distinct().toArray(Player[]::new));
    }
}
