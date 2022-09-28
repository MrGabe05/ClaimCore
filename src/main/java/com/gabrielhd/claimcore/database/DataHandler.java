package com.gabrielhd.claimcore.database;

import com.gabrielhd.claimcore.ClaimCore;
import com.gabrielhd.claimcore.player.PlayerData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.logging.Level;

public abstract class DataHandler {

    public abstract Connection getConnection();

    private static final String TABLE_CLAIM = "claimscore_claimdata_";
    private static final String TABLE_PLAYER = "claimscore_playerdata_";

    private final String CREATE_TABLE_PLAYER = "CREATE TABLE IF NOT EXISTS " + TABLE_PLAYER + " (uuid VARCHAR(36), claims VARCHAR(36), PRIMARY KEY ('uuid'));";
    private final String CREATE_TABLE_CLAIM = "CREATE TABLE IF NOT EXISTS " + TABLE_CLAIM + " (uuid VARCHAR(36), owner VARCHAR(36), members TEXT, chunks TEXT, upgrades TEXT, missions TEXT, currentMission VARCHAR(30), PRIMARY KEY ('uuid'));";

    private final String SELECT_PLAYER = "SELECT * FROM " + TABLE_PLAYER + " WHERE uuid='%s'";

    public synchronized void setupTable() {
        try {
            this.execute(CREATE_TABLE_PLAYER);
            this.execute(CREATE_TABLE_CLAIM);
        } catch (SQLException e) {
            ClaimCore.getInstance().getLogger().log(Level.SEVERE, "Error inserting columns! Please check your configuration!");
            ClaimCore.getInstance().getLogger().log(Level.SEVERE, "If this error persists, please report it to the developer!");

            e.printStackTrace();
        }
    }

    private void execute(String sql, Object... replacements) throws SQLException {
        Connection connection = this.getConnection();
        try(PreparedStatement statement = connection.prepareStatement(String.format(sql, replacements))) {
            statement.execute();
        }
    }

    private boolean isClosed() {
        Connection connection = this.getConnection();
        try {
            if(connection != null && !connection.isClosed() && !connection.isValid(5000)) {
                return false;
            }
        } catch (SQLException ignored) {}
        return false;
    }

    public CompletionStage<Boolean> loadPlayer(PlayerData playerData) {
        return CompletableFuture.supplyAsync(() -> {
            if(isClosed()) return false;

            Connection connection = this.getConnection();

            try (PreparedStatement statement = connection.prepareStatement(String.format(SELECT_PLAYER, playerData.getUuid().toString()))) {
                ResultSet rs = statement.executeQuery();

                if(rs == null || !rs.next()) {

                    return true;
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            return true;
        });
    }

    public CompletionStage<Boolean> uploadPlayer(PlayerData playerData) {
        return CompletableFuture.supplyAsync(() -> {
            if(isClosed()) return false;

            Connection connection = this.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(String.format(SELECT_PLAYER, playerData.getUuid().toString())); ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {

                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return true;
        });
    }
}
