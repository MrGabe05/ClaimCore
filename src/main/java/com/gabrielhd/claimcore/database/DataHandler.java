package com.gabrielhd.claimcore.database;

import com.gabrielhd.claimcore.ClaimCore;
import com.gabrielhd.claimcore.claims.Claim;
import com.gabrielhd.claimcore.config.Config;
import com.gabrielhd.claimcore.missions.Mission;
import com.gabrielhd.claimcore.missions.MissionProgress;
import com.gabrielhd.claimcore.player.PlayerRole;
import com.gabrielhd.claimcore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;
import java.util.logging.Level;

public abstract class DataHandler {

    protected abstract Connection getConnection();

    private static final String TABLE_CLAIM = "claimscore_claimdata_";

    private final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_CLAIM + " (uuid VARCHAR(40), owner VARCHAR(40), progress TEXT, close boolean, tier int, exp double, money double, members TEXT, chunks TEXT, missions TEXT, upgrades TEXT, roles TEXT, inventory TEXT, PRIMARY KEY ('uuid'));";

    private final String INSERT_CLAIM = "INSERT INTO " + TABLE_CLAIM + " (uuid, owner, progress, close, tier, exp, money, members, chunks, missions, upgrades, roles, inventory) VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s');";
    private final String UPDATE_CLAIM = "UPDATE " + TABLE_CLAIM + " SET progress='%s', close='%s', tier='%s', exp='%s', money='%s', members='%s', chunks='%s', missions='%s', upgrades='%s', roles='%s', inventory='%s' WHERE uuid='%s';";

    private final String SELECT_ALL_CLAIMS = "SELECT * FROM " + TABLE_CLAIM + ";";
    private final String SELECT_CLAIM = "SELECT * FROM " + TABLE_CLAIM + " WHERE uuid='%s';";
    private final String DELETE_CLAIM = "DELETE FROM " + TABLE_CLAIM + " WHERE uuid='%s';";

    protected synchronized void setupTable() {
        try {
            this.execute(CREATE_TABLE);
        } catch (SQLException e) {
            ClaimCore.getInstance().getLogger().log(Level.SEVERE, "Error inserting columns! Please check your configuration!");
            ClaimCore.getInstance().getLogger().log(Level.SEVERE, "If this error persists, please report it to the developer!");

            e.printStackTrace();
        }
    }

    protected void execute(String sql, Object... replacements) throws SQLException {
        Connection connection = this.getConnection();
        try(PreparedStatement statement = connection.prepareStatement(String.format(sql, replacements))) {
            statement.execute();
        }
    }

    public void saveClaim(Claim claim, boolean newClaim) {
        Bukkit.getScheduler().runTaskAsynchronously(ClaimCore.getInstance(), () -> {
            if(Config.DEBUG) ClaimCore.getInstance().getLogger().log(Level.INFO, "Saving claim " + claim.getClaim().toString());
            try {
                UUID uuid = claim.getClaim();
                UUID owner = claim.getOwner();
                String progress = this.getProgressString(claim.getCurrentMission());
                boolean isClose = claim.isClose();
                int tier = claim.getTier();
                double exp = claim.getExp();
                double money = claim.getMoney();
                String members = this.getMembersString(claim.getMembers());
                String chunks = this.getChunksString(claim.getChunks());
                String missions = this.getMissionsString(claim.getCompletedMissions());
                String upgrades = this.getUpgrades(claim.getUpgrades());
                String roles = this.getPlayerRoles(claim.getPlayerRoles());
                String inventory = Utils.InventoryToString(claim.getInventory());

                if(newClaim) {
                    this.execute(INSERT_CLAIM, uuid.toString(), owner.toString(), progress, isClose, tier, exp, money, members, chunks, missions, upgrades, roles, inventory);
                } else {
                    this.execute(UPDATE_CLAIM, progress, isClose, tier, exp, money, members, chunks, missions, upgrades, roles, inventory, uuid.toString());
                }

                if(Config.DEBUG) ClaimCore.getInstance().getLogger().log(Level.INFO, "Saved claim " + uuid);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    public void loadClaims(Consumer<List<Claim>> action) {
        List<Claim> claims = new ArrayList<>();

        Connection connection = this.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_ALL_CLAIMS)) {
            ResultSet rs = statement.executeQuery();
            if(rs == null) {
                action.accept(claims);
                return;
            }

            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                UUID owner = UUID.fromString(rs.getString("owner"));

                Claim claim = new Claim(owner, uuid);
                claim.setClose(rs.getBoolean("close"));
                claim.setExp(rs.getDouble("exp"));
                claim.setMoney(rs.getDouble("money"));
                claim.setTier(rs.getInt("tier"));
                claim.setChunks(StringToChunkList(rs.getString("chunks")));
                claim.setMembers(StringToMemberList(rs.getString("members")));
                claim.setCompletedMissions(StringToMissionsList(rs.getString("missions")));
                claim.setCurrentMission(StringToProgress(rs.getString("progress")));

                registerRoles(rs.getString("roles"), claim);
                registerUpgrades(rs.getString("upgrades"), claim);

                claim.setInventory(Utils.StringToInventory(rs.getString("inventory")));

                claim.calcExp();
                claims.add(claim);

                if(Config.DEBUG) ClaimCore.getInstance().getLogger().log(Level.INFO, "Claim " + claim.getClaim().toString() + "Loaded");
            }
        } catch (SQLException throwables) {
            ClaimCore.getInstance().getLogger().log(Level.SEVERE, throwables.getMessage());
        }
        action.accept(claims);
    }

    public void deleteClaim(UUID uuid, Consumer<Boolean> action) {
        Bukkit.getScheduler().runTaskAsynchronously(ClaimCore.getInstance(), () -> {
            try {
                this.execute(DELETE_CLAIM, uuid.toString());
            } catch (SQLException throwables) {
                throwables.printStackTrace();

                action.accept(false);
                return;
            }

            if(Config.DEBUG) ClaimCore.getInstance().getLogger().log(Level.INFO, "Claim delete " + uuid);

            action.accept(true);
        });
    }

    private Set<Chunk> StringToChunkList(String chunksText) {
        Set<Chunk> chunks = new HashSet<>();

        String[] chunksSplit = chunksText.split(";");
        for(String chunkS : chunksSplit) {
            String[] split = chunkS.split(":");

            World world = Bukkit.getWorld(split[0]);
            if(world == null) continue;

            Chunk chunk = world.getChunkAt(Integer.parseInt(split[1]), Integer.parseInt(split[2]));
            if(chunk != null) {
                chunks.add(chunk);
            }
        }

        return chunks;
    }

    private Set<UUID> StringToMemberList(String membersText) {
        Set<UUID> members = new HashSet<>();

        String[] membersSplit = membersText.split(";");
        for(String uuidS : membersSplit) {
            members.add(UUID.fromString(uuidS));
        }
        return members;
    }

    private Set<String> StringToMissionsList(String missionsText) {
        String[] missionsSplit = missionsText.split(";");

        return new HashSet<>(Arrays.asList(missionsSplit));
    }

    private MissionProgress StringToProgress(String currentMission) {
        if(currentMission.isEmpty()) return null;

        String[] missionSplit = currentMission.split(";");

        Mission mission = ClaimCore.getInstance().getMissionsManager().getMission(missionSplit[0]);
        if(mission == null) return null;

        MissionProgress missionProgress = new MissionProgress(mission);
        for(String progress : missionSplit) {
            if(!progress.contains(":")) continue;

            String[] values = progress.split(":");
            missionProgress.getProgress().put(values[0], Integer.parseInt(values[1]));
        }

        return missionProgress;
    }

    private String getChunksString(Set<Chunk> chunks) {
        if(chunks.isEmpty()) return "empty";

        StringBuilder builder = new StringBuilder();
        for(Chunk chunk : chunks) {
            if(!builder.isEmpty()) builder.append(";");

            builder.append(chunk.getWorld().getName()).append(":").append(chunk.getX()).append(":").append(chunk.getZ());
        }
        return builder.toString();
    }

    private String getMembersString(Set<UUID> members) {
        StringBuilder builder = new StringBuilder();
        for(UUID uuid : members) {
            if(!builder.isEmpty()) builder.append(";");

            builder.append(uuid.toString());
        }
        return builder.toString();
    }

    private String getMissionsString(Set<String> completedMissions) {
        if(completedMissions.isEmpty()) return "empty";

        StringBuilder builder = new StringBuilder();
        for(String mission : completedMissions) {
            if(!builder.isEmpty()) builder.append(";");

            builder.append(mission);
        }

        return builder.toString();
    }

    private String getProgressString(MissionProgress progress) {
        if(progress == null) return "";

        StringBuilder builder = new StringBuilder(progress.getMission().getId());

        for(Map.Entry<String, Integer> value : progress.getProgress().entrySet()) {
            builder.append(";").append(value.getKey()).append(":").append(value.getValue());
        }

        return builder.toString();
    }

    private String getUpgrades(Map<String, Integer> upgrades) {
        if(upgrades.isEmpty()) return "empty";

        StringBuilder builder = new StringBuilder();

        for(Map.Entry<String, Integer> value : upgrades.entrySet()) {
            if(!builder.isEmpty()) builder.append(";");

            builder.append(value.getKey().toLowerCase()).append(":").append(value.getValue());
        }

        return builder.toString();
    }

    private void registerUpgrades(String resultUpgrades, Claim claim) {
        if(resultUpgrades != null && !resultUpgrades.equalsIgnoreCase("empty")) {
            String[] mejorasSplit = resultUpgrades.split(";");
            for(String mejorasS : mejorasSplit) {
                String[] split = mejorasS.split(":");

                claim.getUpgrades().put(split[0].toLowerCase(Locale.ROOT), Integer.parseInt(split[1]));
            }
        }
    }

    private String getPlayerRoles(Map<UUID, PlayerRole> playerRoles) {
        if(playerRoles.isEmpty()) return "empty";

        StringBuilder builder = new StringBuilder();

        for(Map.Entry<UUID, PlayerRole> value : playerRoles.entrySet()) {
            if(!builder.isEmpty()) builder.append(";");

            builder.append(value.getKey().toString()).append(":").append(value.getValue().name());
        }

        return builder.toString();
    }

    private void registerRoles(String resultRoles, Claim claim) {
        if(resultRoles != null && !resultRoles.equalsIgnoreCase("empty")) {
            String[] playerRolesSplit = resultRoles.split(";");
            for (String playerRolesS : playerRolesSplit) {
                String[] split = playerRolesS.split(":");

                claim.getPlayerRoles().put(UUID.fromString(split[0]), PlayerRole.valueOf(split[1]));
            }
        }
    }
}
