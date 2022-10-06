package com.gabrielhd.claimcore.visualization;

import org.bukkit.Material;
import org.bukkit.Location;

public class VisualizationElement {
    
    public Location location;
    public Material visualizedMaterial;

    public VisualizationElement(Location location, Material visualizedMaterial) {
        this.location = location;
        this.visualizedMaterial = visualizedMaterial;
    }
}