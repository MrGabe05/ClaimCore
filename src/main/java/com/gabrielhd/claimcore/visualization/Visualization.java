package com.gabrielhd.claimcore.visualization;

import com.gabrielhd.claimcore.ClaimCore;
import com.gabrielhd.claimcore.player.PlayerData;

import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import java.util.ArrayList;

public class Visualization
{
    public ArrayList<VisualizationElement> elements;

    public Visualization() {
        this.elements = new ArrayList<>();
    }

    public static void Apply(Player player, Visualization visualization) {
        PlayerData playerData = PlayerData.of(player.getUniqueId());
        if (playerData.currentVisualization != null) {
            Revert(player);
        }
        if (player.isOnline()) {
            playerData.currentVisualization = visualization;

            Bukkit.getScheduler().scheduleSyncDelayedTask(ClaimCore.getInstance(), new VisualizationApplicationTask(player, playerData, visualization), 10L);
            Bukkit.getScheduler().scheduleSyncDelayedTask(ClaimCore.getInstance(), new VisualizationClearTask(player, playerData, visualization), 400L);
        }
    }

    public static void Revert(Player player) {
        PlayerData playerData = PlayerData.of(player.getUniqueId());
        Visualization visualization = playerData.currentVisualization;
        if (playerData.currentVisualization != null) {
            if (player.isOnline()) {
                for (int i = 0; i < visualization.elements.size(); ++i) {
                    VisualizationElement element = visualization.elements.get(i);
                    if (element.location != null) {
                        Block block = element.location.getBlock();
                        player.sendBlockChange(element.location, block.getType(), block.getData());
                    }
                }
            }
            playerData.currentVisualization = null;
        }
    }

    public void addChunkElements(Chunk chunk, int height, VisualizationType visualizationType, Location not) {
        World world = chunk.getWorld();
        int smallx = chunk.getX() * 16;
        int smallz = chunk.getZ() * 16;
        int bigx = (chunk.getX() + 1) * 16 - 1;
        int bigz = (chunk.getZ() + 1) * 16 - 1;
        Material cornerMaterial = Material.SNOW_BLOCK;
        Material accentMaterial = Material.SNOW_BLOCK;

        if (visualizationType == VisualizationType.Chunk) {
            cornerMaterial = Material.ORANGE_WOOL;
            accentMaterial = Material.ORANGE_WOOL;
        } else if (visualizationType == VisualizationType.ErrorChunk) {
            cornerMaterial = Material.NETHERRACK;
            accentMaterial = Material.NETHERRACK;
        } else if (visualizationType == VisualizationType.Public) {
            cornerMaterial = Material.BLUE_WOOL;
            accentMaterial = Material.BLUE_WOOL;
        }

        this.elements.add(new VisualizationElement(getVisibleLocation(world, smallx, height, smallz, not), cornerMaterial));
        this.elements.add(new VisualizationElement(getVisibleLocation(world, smallx + 1, height, smallz, not), accentMaterial));
        this.elements.add(new VisualizationElement(getVisibleLocation(world, smallx + 2, height, smallz, not), accentMaterial));
        this.elements.add(new VisualizationElement(getVisibleLocation(world, smallx, height, smallz + 1, not), accentMaterial));
        this.elements.add(new VisualizationElement(getVisibleLocation(world, smallx, height, smallz + 2, not), accentMaterial));
        this.elements.add(new VisualizationElement(getVisibleLocation(world, bigx, height, smallz, not), cornerMaterial));
        this.elements.add(new VisualizationElement(getVisibleLocation(world, bigx - 1, height, smallz, not), accentMaterial));
        this.elements.add(new VisualizationElement(getVisibleLocation(world, bigx - 2, height, smallz, not), accentMaterial));
        this.elements.add(new VisualizationElement(getVisibleLocation(world, bigx, height, smallz + 1, not), accentMaterial));
        this.elements.add(new VisualizationElement(getVisibleLocation(world, bigx, height, smallz + 2, not), accentMaterial));
        this.elements.add(new VisualizationElement(getVisibleLocation(world, bigx, height, bigz, not), cornerMaterial));
        this.elements.add(new VisualizationElement(getVisibleLocation(world, bigx - 1, height, bigz, not), accentMaterial));
        this.elements.add(new VisualizationElement(getVisibleLocation(world, bigx - 2, height, bigz, not), accentMaterial));
        this.elements.add(new VisualizationElement(getVisibleLocation(world, bigx, height, bigz - 1, not), accentMaterial));
        this.elements.add(new VisualizationElement(getVisibleLocation(world, bigx, height, bigz - 2, not), accentMaterial));
        this.elements.add(new VisualizationElement(getVisibleLocation(world, smallx, height, bigz, not), cornerMaterial));
        this.elements.add(new VisualizationElement(getVisibleLocation(world, smallx + 1, height, bigz, not), accentMaterial));
        this.elements.add(new VisualizationElement(getVisibleLocation(world, smallx + 2, height, bigz, not), accentMaterial));
        this.elements.add(new VisualizationElement(getVisibleLocation(world, smallx, height, bigz - 1, not), accentMaterial));
        this.elements.add(new VisualizationElement(getVisibleLocation(world, smallx, height, bigz - 2, not), accentMaterial));
    }

    private static Location getVisibleLocation(World world, int x, int y, int z, Location not) {
        Block block = world.getBlockAt(x, y, z);
        for (BlockFace direction = block.getType().isTransparent() ? BlockFace.DOWN : BlockFace.UP; block.getY() >= 1 && block.getY() < world.getMaxHeight() - 1 && (!block.getRelative(BlockFace.UP).getType().isTransparent() || block.getType().isTransparent()); block = block.getRelative(direction));

        Location location = block.getLocation();
        if (not != null && location.getX() == not.getX() && location.getY() == not.getY() && location.getZ() == not.getZ()) {
            return null;
        }

        return location;
    }
}