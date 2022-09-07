package com.gabrielhd.claimcore.database;

import com.gabrielhd.claimcore.Main;
import com.gabrielhd.claimcore.player.PlayerData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.logging.Level;

public abstract class DataHandler {

    public abstract Connection getConnection();

    private static final String TABLE = "claimscore_";

    private final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE + " (uuid VARCHAR(100), claims TEXT, PRIMARY KEY ('uuid'));";

    private final String SELECT_PLAYER = "SELECT * FROM " + TABLE + " WHERE uuid='%s'";

    public synchronized void setupTable() {
        try {
            this.execute(CREATE_TABLE);
        } catch (SQLException e) {
            Main.getInstance().getLogger().log(Level.SEVERE, "Error inserting columns! Please check your configuration!");
            Main.getInstance().getLogger().log(Level.SEVERE, "If this error persists, please report it to the developer!");

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

    private Map<String, Integer> get(String s) {
        Map<String, Integer> value = new HashMap<>();

        String[] split = s.split(";");

        for(String s2 : split) {
            String[] split2 = s2.split(":");

            value.put(split2[0], Integer.valueOf(split2[1]));
        }

        return value;
    }

    private String get(Map<String, Integer> value) {
        StringBuilder builder = new StringBuilder();

        for(Map.Entry<String, Integer> entry : value.entrySet()) {
            if(builder.length() > 0) {
                builder.append(";");
            }

            builder.append(entry.getKey()).append(entry.getValue());
        }

        return builder.toString();
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
