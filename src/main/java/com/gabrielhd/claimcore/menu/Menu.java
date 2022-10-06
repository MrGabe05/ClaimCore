package com.gabrielhd.claimcore.menu;

import com.gabrielhd.claimcore.ClaimCore;
import com.gabrielhd.claimcore.config.YamlConfig;
import com.gabrielhd.claimcore.utils.Color;
import com.gabrielhd.claimcore.utils.ItemBuilder;
import com.gabrielhd.claimcore.utils.TextPlaceholders;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Getter
public abstract class Menu implements Listener {

    private final YamlConfig cfg;

    private final Inventory inventory;

    @Setter private Menu previousMenu;

    private Map<Character, List<Integer>> charSlots;

    private final Map<Integer, String> actions = new HashMap<>();
    private final Map<Integer, ItemBuilder> fillItems = new HashMap<>();

    public Menu(YamlConfig cfg) {
        this.cfg = cfg;

        InventoryType type = InventoryType.valueOf(cfg.getString("InventoryType", "CHEST"));
        if(type == InventoryType.CHEST || type == InventoryType.PLAYER) {
            this.inventory = Bukkit.createInventory(null, 9 * cfg.getStringList("Pattern").size(), Color.text(cfg.getString("Title")));
        } else {
            this.inventory = Bukkit.createInventory(null, type, Color.text(cfg.getString("Title")));
        }

        ClaimCore.getInstance().getServer().getPluginManager().registerEvents(this, ClaimCore.getInstance());

        this.loadMenu();
    }

    public void addItem(ItemStack stack) {
        this.inventory.addItem(stack);
    }

    public void setItem(int i, ItemStack stack) {
        this.inventory.setItem(i, stack);
    }

    public void openInventory(Player p) {
        p.openInventory(this.inventory);
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getInventory().equals(this.inventory) && event.getPlayer() instanceof Player) {
            for(Map.Entry<Integer, ItemBuilder> itemStackEntry : this.fillItems.entrySet()) {
                ItemBuilder itemBuilder = itemStackEntry.getValue().clone();
                if(itemStackEntry.getKey() >= 0) {
                    inventory.setItem(itemStackEntry.getKey(), itemBuilder.build((OfflinePlayer) event.getPlayer()));
                }
            }
        }
    }

    @EventHandler
    public void itemMove(InventoryMoveItemEvent event) {
        if (event.getDestination().equals(this.inventory) || event.getSource().equals(this.inventory)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().equals(this.inventory) && event.getCurrentItem() != null && event.getWhoClicked() instanceof Player) {
            this.onClick((Player) event.getWhoClicked(), event);

            event.setCancelled(true);
        }
    }

    private void loadMenu() {
        Map<Character, List<Integer>> charSlots = new HashMap<>();

        List<String> pattern = cfg.getStringList("Pattern");
        for(int row = 0; row < pattern.size(); row++) {
            String patternLine = pattern.get(row);
            int slot = row * 9;

            for (int i = 0; i < patternLine.length(); i++) {
                char ch = patternLine.charAt(i);
                if (ch != ' ') {
                    ConfigurationSection section = cfg.getConfigurationSection("Items." + ch);

                    ItemBuilder itemBuilder = getItemStack(cfg.getFile().getName(), section);

                    if(itemBuilder != null) {
                        this.fillItems.put(slot, itemBuilder);

                        if(section.isSet("Action")) this.actions.put(slot, section.getString("Action"));
                    }

                    if(!charSlots.containsKey(ch))
                        charSlots.put(ch, new ArrayList<>());

                    charSlots.get(ch).add(slot);

                    slot++;
                }
            }
        }

        this.charSlots = charSlots;
    }

    protected static ItemBuilder getItemStack(String fileName, ConfigurationSection section) {
        return getItemStack(fileName, section, new TextPlaceholders());
    }

    protected static ItemBuilder getItemStack(String fileName, ConfigurationSection section, TextPlaceholders textPlaceholders) {
        if(section == null || !section.contains("Type")) return null;

        Material type;
        try {
            type = Material.valueOf(section.getString("Type"));
        } catch (Exception ex) {
            ClaimCore.getInstance().getLogger().log(Level.SEVERE, "[" + fileName + "] Couldn't convert " + section.getCurrentPath() + " into an itemstack. Check type sections!");

            type = Material.BEDROCK;
        }

        ItemBuilder itemBuilder = new ItemBuilder(type);
        if(section.contains("Name")) itemBuilder.withName(textPlaceholders.parse(section.getString("Name")));
        if(section.contains("Lore")) itemBuilder.withLore(section.getStringList("Lore").stream().map(textPlaceholders::parse).collect(Collectors.toList()));
        if(section.contains("Enchants")) {
            for(String _enchantment : section.getConfigurationSection("Enchants").getKeys(false)) {
                Enchantment enchantment;

                try {
                    enchantment = Enchantment.getByName(_enchantment);
                } catch (Exception ex) {
                    ClaimCore.getInstance().getLogger().info("&c[" + fileName + "] Couldn't convert " + section.getCurrentPath() + ".enchants." + _enchantment + " into an enchantment, skipping...");
                    continue;
                }

                itemBuilder.withEnchant(enchantment, section.getInt("Enchants." + _enchantment));
            }
        }

        if(section.contains("Flags")) {
            for(String flag : section.getStringList("Flags")) itemBuilder.withFlags(ItemFlag.valueOf(flag));
        }

        if(section.getBoolean("Unbreakable", false)) itemBuilder.setUnbreakable();

        return itemBuilder;
    }

    protected static List<Integer> getSlots(YamlConfig section, String key, Map<Character, List<Integer>> charSlots) {
        if(!section.contains(key))
            return new ArrayList<>();

        List<Character> chars = new ArrayList<>();

        for(char ch : section.getString(key).toCharArray())
            chars.add(ch);

        List<Integer> slots = new ArrayList<>();

        chars.stream().filter(charSlots::containsKey).forEach(ch -> slots.addAll(charSlots.get(ch)));

        return slots.isEmpty() ? Collections.singletonList(-1) : slots;
    }

    public abstract void onClick(Player player, InventoryClickEvent event);
}
