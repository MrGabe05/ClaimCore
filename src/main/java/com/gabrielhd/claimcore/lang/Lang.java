package com.gabrielhd.claimcore.lang;

import com.gabrielhd.claimcore.Main;
import com.gabrielhd.claimcore.config.YamlConfig;
import com.gabrielhd.claimcore.utils.Color;
import com.gabrielhd.claimcore.utils.TextPlaceholders;
import org.bukkit.entity.Player;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

public class Lang {

    private final Map<String, Object> langs = new HashMap<>();

    public static Lang
            MISSION_COMPLETED,
            NOTIFY_MISSION_COMPLETED;

    public void addLang(String lang, String... text) {
        for(String s : text) {
            langs.put(lang.toLowerCase(Locale.ROOT), Color.text(s));
        }
    }

    public void send(Player player) {
        send(new TextPlaceholders(), player);
    }

    public void send(TextPlaceholders textPlaceholders, Player... players) {
        Arrays.stream(players).forEach(player -> {
            String lang = player.getLocale().split("_")[0];

            Object obj = langs.getOrDefault(lang.toLowerCase(Locale.ROOT), langs.values().stream().findFirst().orElse(this.getClass().getName() + " value not set"));

            if(obj instanceof List) {
                for(String s : (List<String>) obj) {
                    s = textPlaceholders.parse(s);

                    player.sendMessage(Color.text(s));
                }
                return;
            }

            player.sendMessage(Color.text(textPlaceholders.parse((String) obj)));
        });
    }

    public String get(Player player) {
        return get(player, new TextPlaceholders());
    }

    public String get(Player player, TextPlaceholders textPlaceholders) {
        String lang = player.getLocale().split("_")[0];

        return (String) langs.getOrDefault(lang.toLowerCase(Locale.ROOT), langs.values().stream().findFirst().orElse(this.getClass().getName() + " value not set"));
    }

    public static void loadLangs() {
        File langFolder = new File(Main.getInstance().getDataFolder(), "/lang/");
        if(!langFolder.exists()) langFolder.mkdir();

        Arrays.stream(langFolder.listFiles()).filter(File::isFile).filter(file -> file.getPath().endsWith(".yml")).forEach(file -> {
            YamlConfig langConfig = new YamlConfig(Main.getInstance(), file);

            String lang = file.getName().split("_")[1].replace(".yml", "");

            for(Field field : Lang.class.getFields()) {
                field.setAccessible(true);

                if(field.getType() == Lang.class) {
                    if(!langConfig.isSet(field.getName().toLowerCase(Locale.ROOT))) langConfig.set(field.getName().toLowerCase(Locale.ROOT), field.getName().toLowerCase(Locale.ROOT) + " value not set");

                    try {
                        if(field.get(null) == null) {
                            field.set(field, new Lang());
                        }

                        Lang obj = (Lang) field.get(null);

                        if(langConfig.isList(field.getName().toLowerCase(Locale.ROOT))) {
                            obj.addLang(lang, langConfig.getStringList(field.getName().toLowerCase(Locale.ROOT)).toArray(new String[0]));
                        } else {
                            obj.addLang(lang, langConfig.getString(field.getName().toLowerCase(Locale.ROOT)));
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            langConfig.save();

            Main.getInstance().getLogger().info("Lang " + lang + " loaded.");
        });
    }
}
