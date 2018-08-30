package com.assimilation.ellie.assicore.api;

import com.assimilation.ellie.assicore.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Ellie on 11/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class ItemBuilder {

    private ItemStack itemStack;

    public ItemBuilder(Material material){
        this.itemStack = new ItemStack(material);
    }

    public ItemBuilder(ItemStack itemStack){
        this.itemStack = itemStack;
    }

    public ItemBuilder setAmount(int x){
        this.itemStack.setAmount(x);
        return this;
    }

    public ItemBuilder setDisplay(String display){
        ItemMeta _im = itemStack.getItemMeta();
        _im.setDisplayName(Util.color(display));
        updateMeta(_im);
        return this;
    }

    public ItemBuilder setLore(String... lore){
        ItemMeta _im = itemStack.getItemMeta();
        LinkedList<String> a = new LinkedList<>();
        for (String s : lore) {
            a.add(Util.color(s));
        }
        _im.setLore(a);
        updateMeta(_im);
        return this;
    }

    public ItemBuilder appendLore(String string){
        ItemMeta _im = itemStack.getItemMeta();
        List<String> a = _im.hasLore() ? _im.getLore() : new ArrayList<>();
        a.add(Util.color(string));
        _im.setLore(a);
        updateMeta(_im);
        return this;
    }

    public ItemBuilder setType(Material material){
        itemStack.setType(material);
        return this;
    }

    public ItemBuilder setDurability(short a){
        itemStack.setDurability(a);
        return this;
    }

    public ItemBuilder asPlayerHead(String owner){
        ItemStack is = build();
        is.setDurability((byte) 3);
        SkullMeta skullMeta = (SkullMeta) is.getItemMeta();
        skullMeta.setOwner(owner);
        is.setItemMeta(skullMeta);
        setItemStack(is);
        return this;
    }

    public ItemBuilder setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
        return this;
    }

    public ItemBuilder setColor(StackColor color){
        if(itemStack.getType() == Material.WOOL || itemStack.getType() == Material.GLASS || itemStack.getType() == Material.STAINED_GLASS_PANE){
            itemStack.setDurability(color.getData());
        }
        return this;
    }

    public ItemStack build(){
        return this.itemStack;
    }

    private void updateMeta(ItemMeta _im){
        itemStack.setItemMeta(_im);
    }

    public enum StackColor {

        WHITE((byte)0, ChatColor.WHITE),
        ORANGE((byte)1, ChatColor.GOLD),
        MAGENTA((byte)1, null),
        LIGHT_BLUE((byte)3, ChatColor.AQUA),
        YELLOW((byte)4, ChatColor.YELLOW),
        LIME((byte)5, ChatColor.GREEN),
        PINK((byte)6, ChatColor.LIGHT_PURPLE),
        GRAY((byte)7, ChatColor.DARK_GRAY),
        LIGHT_GRAY((byte)8, ChatColor.GRAY),
        CYAN((byte)9, ChatColor.DARK_AQUA),
        PURPLE((byte)10, ChatColor.DARK_PURPLE),
        BLUE((byte)11, ChatColor.BLUE),
        BROWN((byte)12, null),
        GREEN((byte)13, ChatColor.DARK_GREEN),
        RED((byte)14, ChatColor.RED),
        BLACK((byte)15, ChatColor.BLACK);

        private byte data;
        private ChatColor chatColor;

        StackColor(byte data, ChatColor chatColor){
            this.data = data;
            this.chatColor = chatColor;
        }

        public byte getData() {
            return data;
        }

        public ChatColor getChatColor(){
            return chatColor;
        }
    }

}
