package com.gabrielhd.claimcore.lang;

import com.gabrielhd.claimcore.ClaimCore;
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
            CHUNK_CLAIMED,
            CHUNK_ALREADY_CLAIMED,
            CHUNK_ALREADY_CLAIM,
            CHUNK_MAX_LIMIT,
            INSUFFICIENT_MONEY,
            UPGRADE_PURCHASED,
            UPGRADE_MAX_LEVEL,
            UPGRADE_MAX_LEVEL_FORMAT,
            MISSION_LOCKED,
            MISSION_SELECT,
            MISSION_COMPLETED,
            MISSION_IN_PROGRESS,
            MISSION_ALREADY_SELECT,
            MISSION_ALREADY_COMPLETED,
            MISSION_TIER_LEVEL_UP,
            NOTIFY_MISSION_COMPLETED,
            PLAYER_NOT_EXISTS,
            PLAYER_NOT_ONLINE,
            PARTY_COMMANDS_HELP,
            PARTY_PLAYER_JOIN,
            PARTY_PLAYER_QUIT,
            PARTY_PLAYER_KICK,
            PARTY_ALREADY_IN,
            PARTY_NOT_IN,
            PARTY_NOT_OWNER,
            PARTY_CREATE,
            PARTY_DISBAND;

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

    public String get() {
        return (String) langs.values().stream().findFirst().orElse(this.getClass().getName() + " value not set");
    }

    public String get(Player player) {
        return get(player, new TextPlaceholders());
    }

    public String get(Player player, TextPlaceholders textPlaceholders) {
        String lang = player.getLocale().split("_")[0];

        return (String) langs.getOrDefault(lang.toLowerCase(Locale.ROOT), langs.values().stream().findFirst().orElse(this.getClass().getName() + " value not set"));
    }

    private void addLangList(String lang, List<String> text) {
        List<String> finaltext = new ArrayList<>();
        for(String s : text) {
            finaltext.add(Color.text(s));
        }

        langs.put(lang.toLowerCase(Locale.ROOT), finaltext);
    }

    private void addLang(String lang, String... text) {
        for(String s : text) {
            langs.put(lang.toLowerCase(Locale.ROOT), Color.text(s));
        }
    }

    public static void loadLangs() {
        File langFolder = new File(ClaimCore.getInstance().getDataFolder(), "/lang/");
        if(!langFolder.exists()) langFolder.mkdir();

        Arrays.stream(langFolder.listFiles()).filter(File::isFile).filter(file -> file.getPath().endsWith(".yml")).forEach(file -> {
            YamlConfig langConfig = new YamlConfig(ClaimCore.getInstance(), file);

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
                            obj.addLangList(lang, langConfig.getStringList(field.getName().toLowerCase(Locale.ROOT)));
                        } else {
                            obj.addLang(lang, langConfig.getString(field.getName().toLowerCase(Locale.ROOT)));
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            langConfig.save();

            ClaimCore.getInstance().getLogger().info("Lang " + lang + " loaded.");
        });
    }
}
