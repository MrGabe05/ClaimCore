package com.gabrielhd.claimcore.config;

import com.gabrielhd.claimcore.ClaimCore;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

@Getter
public class YamlConfig extends YamlConfiguration {

    private final File file;
    private final String path;
    private final ClaimCore plugin;

    public YamlConfig(ClaimCore plugin, String path) {
        this.plugin = plugin;

        this.path = path + ".yml";
        this.file = new File(plugin.getDataFolder(), this.path);
        this.saveDefault();
        this.reload();

        this.options().parseComments(true);
    }

    public YamlConfig(ClaimCore plugin, File file) {
        this.plugin = plugin;

        this.path = file.getName() + ".yml";
        this.file = file;

        this.reload();

        this.options().parseComments(true);
    }

    public void reload() {
        try {
            super.load(this.file);
        }
        catch (Exception ignored) {}
    }

    public void save() {
        try {
            super.save(this.file);
        }
        catch (Exception ignored) {}
    }

    public void saveDefault() {
        try {
            if (!this.file.exists()) {
                if (plugin.getResource(this.path) != null) {
                    plugin.saveResource(this.path, false);
                }
                else {
                    this.file.createNewFile();
                }
            }
        }
        catch (Exception ignored) {}
    }
}