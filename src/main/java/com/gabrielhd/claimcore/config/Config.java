package com.gabrielhd.claimcore.config;

import com.gabrielhd.claimcore.ClaimCore;
import com.gabrielhd.claimcore.upgrades.Upgrades;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

public class Config {

    public static int PARTY_LIMITS;

    public static double XP_UPGRADE, XP_MISSION;

    public static boolean UPGRADES_ONLY_OWNER, CLAIM_NEW_CHUNK, UPGRADES, MISSIONS, XPSYSTEM, ECONOMY, CLAIM_GEN;

    public static String TYPE, HOST, PORT, DATABASE, USERNAME, PASSWORD;

    public static Map<Upgrades, Map<Integer, Double>> UPGRADES_COSTS;
    public static Map<Upgrades, Map<Integer, Integer>> UPGRADES_LIMITS;

    public static Map<Material, Double> XP_BLOCKS;
    public static Map<EntityType, Double> XP_ENTITY;

    public Config() {
        this.loadConfig();
    }

    private void loadConfig() {
        YamlConfig settings = new YamlConfig(ClaimCore.getInstance(), "Settings");

        PARTY_LIMITS = settings.getInt("Settings.PartyLimits", 10);
        XP_UPGRADE = settings.getDouble("XPSystem.Upgrade", 10.0);
        XP_MISSION = settings.getDouble("XPSystem.Missions", 20.0);

        ECONOMY = settings.getBoolean("Settings.Economy", true);
        UPGRADES = settings.getBoolean("Settings.Upgrades", true);
        MISSIONS = settings.getBoolean("Settings.Missions", true);
        XPSYSTEM = settings.getBoolean("Settings.XPSystem", true);
        CLAIM_GEN = settings.getBoolean("Settings.ClaimGen", false);

        TYPE = settings.getString("StorageType", "sqlite");
        HOST = settings.getString("Database.Host", "localhost");
        PORT = settings.getString("Database.Port", "3306");
        DATABASE = settings.getString("Database.Database", "claimcoredatabase");
        USERNAME = settings.getString("Database.Username", "claimcoreuser");
        PASSWORD = settings.getString("Database.Password", "claimcorepass");

        XP_ENTITY = new HashMap<>();
        XP_BLOCKS = new HashMap<>();

        ConfigurationSection entitySection = settings.getConfigurationSection("XPSystem.Entity");
        for(String keys : entitySection.getKeys(false)) {
            EntityType type = EntityType.valueOf(keys.toUpperCase());
            if(type == null) {
                continue;
            }

            XP_ENTITY.putIfAbsent(type, entitySection.getDouble(keys, 1));
        }

        ConfigurationSection blocksSection = settings.getConfigurationSection("XPSystem.Blocks");
        for(String keys : blocksSection.getKeys(false)) {
            Material type = Material.getMaterial(keys.toUpperCase());
            if(type == null) {
                continue;
            }

            XP_BLOCKS.putIfAbsent(type, blocksSection.getDouble(keys, 1));
        }

        this.loadUpgradesConfig();
    }

    private void loadUpgradesConfig() {
        YamlConfig upgradesConfig = new YamlConfig(ClaimCore.getInstance(), "Upgrades");

        Map<Upgrades, Map<Integer, Double>> upgrade_costs = new HashMap<>();
        Map<Upgrades, Map<Integer, Integer>> upgrade_limits = new HashMap<>();

        for(Upgrades upgrades : Upgrades.values()) {
            Map<Integer, Double> costs = new HashMap<>();
            Map<Integer, Integer> limits = new HashMap<>();

            for(String upgradesName : upgradesConfig.getKeys(false)) {
                if(upgradesName.equalsIgnoreCase(upgrades.name())) {
                    for(String level : upgradesConfig.getConfigurationSection(upgradesName).getKeys(false)) {
                        if(upgradesConfig.isSet(upgradesName + "." + level + ".Amount")) {
                            limits.put(Integer.parseInt(level), upgradesConfig.getInt(upgradesName + "." + level + ".Amount"));
                        }

                        if(upgradesConfig.isSet(upgradesName + "." + level + ".Price")) {
                            costs.put(Integer.parseInt(level), upgradesConfig.getDouble(upgradesName + "." + level + ".Price"));
                        }
                    }
                    break;
                }
            }

            upgrade_costs.put(upgrades, costs);
            upgrade_limits.put(upgrades, limits);
        }

        UPGRADES_COSTS = upgrade_costs;
        UPGRADES_LIMITS = upgrade_limits;
    }
}
