package com.gabrielhd.claimcore.utils;

import com.gabrielhd.claimcore.ClaimCore;
import com.gabrielhd.claimcore.utils.placeholders.ClaimPlaceholders;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class ItemBuilder implements Cloneable {

    private static final ClaimCore plugin = ClaimCore.getInstance();

    private ItemStack itemStack;
    private ItemMeta itemMeta;
    private String textureValue = "";

    public ItemBuilder(ItemStack itemStack){
        this(itemStack.getType());

        this.itemMeta = itemStack.getItemMeta().clone();
    }

    public ItemBuilder(Material type){
        itemStack = new ItemStack(type, 1);
        itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder asSkullOf(OfflinePlayer player) {
        itemStack.setType(Material.PLAYER_HEAD);

        if(itemMeta instanceof SkullMeta skullMeta) {
            skullMeta.setOwningPlayer(player);
        }
        return this;
    }

    public ItemBuilder withName(String name){
        if(name != null)
            itemMeta.setDisplayName(Color.text(name));
        return this;
    }

    public ItemBuilder replaceName(String regex, String replace){
        if(itemMeta.hasDisplayName())
            withName(itemMeta.getDisplayName().replace(regex, replace));
        return this;
    }

    public ItemBuilder withLore(List<String> lore){
        if(lore != null)
            itemMeta.setLore(lore.stream().map(Color::text).collect(Collectors.toList()));
        return this;
    }

    public ItemBuilder appendLore(List<String> lore){
        List<String> currentLore = itemMeta.getLore() == null ? new ArrayList<>() : itemMeta.getLore();
        currentLore.addAll(lore);
        return withLore(currentLore);
    }

    public ItemBuilder withLore(String... lore){
        return withLore(Arrays.asList(lore));
    }

    public ItemBuilder withLore(String firstLine, List<String> listLine){
        List<String> loreList = new ArrayList<>();

        firstLine = Color.text(firstLine);
        loreList.add(firstLine);

        for(String line : listLine)
            loreList.add(ChatColor.getLastColors(firstLine) + Color.text(line));

        if(loreList.size() > 10){
            for(int i = 10; i < loreList.size(); i++){
                loreList.remove(loreList.get(i));
            }
            loreList.add(ChatColor.getLastColors(firstLine) + "...");
        }

        itemMeta.setLore(loreList);
        return this;
    }

    public ItemBuilder withLore(String firstLine, ConfigurationSection configurationSection){
        List<String> loreList = new ArrayList<>();

        firstLine = Color.text(firstLine);
        loreList.add(firstLine);

        for(String section : configurationSection.getKeys(false)){
            section = section + ": " + configurationSection.get(section).toString();
            loreList.add(ChatColor.getLastColors(firstLine) + Color.text(section));
        }

        if(loreList.size() > 16){
            loreList = loreList.subList(0, 16);
            loreList.add(ChatColor.getLastColors(firstLine) + "...");
        }

        itemMeta.setLore(loreList);
        return this;
    }

    public ItemBuilder replaceLore(String regex, String replace){
        if(!itemMeta.hasLore())
            return this;

        List<String> loreList = new ArrayList<>();

        for(String line : itemMeta.getLore()){
            loreList.add(line.replace(regex, replace));
        }

        withLore(loreList);
        return this;
    }

    public ItemBuilder replaceLoreWithLines(String regex, String... lines){
        if(!itemMeta.hasLore())
            return this;

        List<String> loreList = new ArrayList<>();
        List<String> linesToAdd = Arrays.asList(lines);
        boolean isEmpty = linesToAdd.isEmpty() || linesToAdd.stream().allMatch(String::isEmpty);

        for(String line : itemMeta.getLore()){
            if(line.contains(regex)){
                if(!isEmpty)
                    loreList.addAll(linesToAdd);
            }
            else{
                loreList.add(line);
            }
        }

        withLore(loreList);
        return this;
    }

    public ItemBuilder replaceAll(String regex, String replace){
        replaceName(regex, replace);
        replaceLore(regex, replace);
        return this;
    }

    public ItemBuilder withEnchant(Enchantment enchant, int level){
        itemMeta.addEnchant(enchant, level, true);
        return this;
    }

    public ItemBuilder withFlags(ItemFlag... itemFlags){
        itemMeta.addItemFlags(itemFlags);
        return this;
    }

    public ItemBuilder setUnbreakable(){
        itemMeta.setUnbreakable(true);
        return this;
    }

    public ItemMeta getItemMeta(){
        return itemMeta;
    }

    public ItemStack build(OfflinePlayer offlinePlayer){
        if(itemMeta.hasDisplayName()) {
            withName(PlaceholderAPI.setPlaceholders(offlinePlayer, itemMeta.getDisplayName().replace("%player%", offlinePlayer.getName())));
        }

        if(itemMeta.hasLore()) {
            withLore(itemMeta.getLore().stream().map(line -> PlaceholderAPI.setPlaceholders(offlinePlayer, line)).map(line -> new ClaimPlaceholders(ClaimCore.getInstance().getClaimManager().getClaimOfPlayer(offlinePlayer.getUniqueId())).set("%player%", offlinePlayer.getName()).parse(line)).collect(Collectors.toList()));
        }

        return build();
    }

    public ItemStack build(){
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public ItemBuilder clone(){
        try {
            ItemBuilder itemBuilder = (ItemBuilder) super.clone();
            itemBuilder.itemStack = itemStack.clone();
            itemBuilder.itemMeta = itemMeta.clone();
            itemBuilder.textureValue = textureValue;
            return itemBuilder;
        }catch(Exception ex){
            throw new NullPointerException(ex.getMessage());
        }
    }
}
