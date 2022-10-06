package com.gabrielhd.claimcore.visualization;

import com.gabrielhd.claimcore.player.PlayerData;
import org.bukkit.entity.Player;

public class VisualizationApplicationTask implements Runnable
{
    private final Visualization visualization;
    private final Player player;
    private final PlayerData playerData;

    public VisualizationApplicationTask(Player player, PlayerData playerData, Visualization visualization) {
        this.visualization = visualization;
        this.playerData = playerData;
        this.player = player;
    }

    @Override
    public void run() {
        if (this.playerData.currentVisualization == this.visualization) {
            for (int i = 0; i < this.visualization.elements.size(); ++i) {
                VisualizationElement element = this.visualization.elements.get(i);
                if (element.location != null) {
                    this.player.sendBlockChange(element.location, element.visualizedMaterial.createBlockData());
                }
            }
        }
    }
}