package com.gabrielhd.claimcore.visualization;

import com.gabrielhd.claimcore.player.PlayerData;
import org.bukkit.entity.Player;

public class VisualizationClearTask implements Runnable
{
    private final Visualization visualization;
    private final Player player;
    private final PlayerData playerData;

    public VisualizationClearTask(Player player, PlayerData playerData, Visualization visualization) {
        this.visualization = visualization;
        this.playerData = playerData;
        this.player = player;
    }

    @Override
    public void run() {
        if (this.playerData.currentVisualization == this.visualization) {
            Visualization.Revert(this.player);
        }
    }
}