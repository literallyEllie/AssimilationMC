package net.assimilationmc.assicore.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ItemBuilder {

    private static Map<String, String> specialHeads;

    static {
        specialHeads = Maps.newHashMap();

        specialHeads.put("f3cf43ce95b5d4c7274bf3416f2cc866a9972b8f719f74eb82842dcb43951f", "Twitter");
        specialHeads.put("7873c12bffb5251a0b88d5ae75c7247cb39a75ff1a81cbe4c8a39b311ddeda", "Discord");
    }

    private ItemStack itemStack;

    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
    }

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public static ItemStack getSkull(String textures) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), (specialHeads.getOrDefault(textures, null)));
        byte[] encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"http://textures.minecraft.net/texture/%s\"}}}", textures).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        UtilReflect.setValue(skullMeta, "profile", profile);
        skull.setItemMeta(skullMeta);
        return skull;
    }

    public ItemBuilder setAmount(int x) {
        this.itemStack.setAmount(x);
        return this;
    }

    public ItemBuilder setDisplay(String display) {
        ItemMeta _im = itemStack.getItemMeta();
        _im.setDisplayName(display);
        updateMeta(_im);
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        ItemMeta _im = itemStack.getItemMeta();
        final List<String> a = _im.hasLore() ? _im.getLore() : Lists.newLinkedList();
        for (String s : lore) {
            if (s != null)
                a.add(s);
        }
        _im.setLore(a);
        updateMeta(_im);
        return this;
    }

    public ItemBuilder setLore(boolean reset, String... lore) {
        final ItemMeta _im = itemStack.getItemMeta();
        final List<String> a = Lists.newLinkedList();
        if (!reset) return setLore(lore);

        for (String s : lore) {
            if (s != null)
                a.add(s);
        }
        _im.setLore(a);
        updateMeta(_im);
        return this;
    }

    public ItemBuilder appendLore(String string) {
        final ItemMeta _im = itemStack.getItemMeta();
        final List<String> a = _im.hasLore() ? _im.getLore() : Lists.newArrayList();
        a.add(string);
        _im.setLore(a);
        updateMeta(_im);
        return this;
    }

    public ItemBuilder setType(Material material) {
        itemStack.setType(material);
        return this;
    }

    public ItemBuilder setDurability(short a) {
        itemStack.setDurability(a);
        return this;
    }

    public ItemBuilder asPlayerHead(String owner) {
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

    public ItemBuilder setColor(StackColor color) {
        if (itemStack.getType() == Material.WOOL || itemStack.getType() == Material.GLASS || itemStack.getType() == Material.STAINED_GLASS_PANE
                || itemStack.getType() == Material.INK_SACK) {

            if (itemStack.getType() == Material.INK_SACK) {
                itemStack.setDurability(color.getDyeData());
            } else itemStack.setDurability(color.getData());
        }
        return this;
    }

    public ItemBuilder enchant(Enchantment enchantment, int level) {
        itemStack.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder unenchant(Enchantment enchantment) {
        itemStack.removeEnchantment(enchantment);
        return this;
    }

    public ItemBuilder addGlow() {
        enchant(Enchantment.DURABILITY, 0);
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        updateMeta(itemMeta);
        return this;
    }

    public ItemBuilder addStoredEnchant(Enchantment enchantment, int level) {
        if (itemStack.getType() != Material.ENCHANTED_BOOK) return this;
        EnchantmentStorageMeta storageMeta = (EnchantmentStorageMeta) itemStack.getItemMeta();
        storageMeta.addStoredEnchant(enchantment, level, true);
        updateMeta(storageMeta);
        return this;
    }

    public ItemStack build() {
        return this.itemStack;
    }

    private void updateMeta(ItemMeta _im) {
        itemStack.setItemMeta(_im);
    }

    public enum StackColor {

        WHITE((byte) 0, (byte) 15, ChatColor.WHITE),
        ORANGE((byte) 1, (byte) 14, ChatColor.GOLD),
        MAGENTA((byte) 2, (byte) 13, null),
        LIGHT_BLUE((byte) 3, (byte) 12, ChatColor.AQUA),
        YELLOW((byte) 4, (byte) 11, ChatColor.YELLOW),
        LIME((byte) 5, (byte) 10, ChatColor.GREEN),
        PINK((byte) 6, (byte) 9, ChatColor.LIGHT_PURPLE),
        GRAY((byte) 7, (byte) 8, ChatColor.DARK_GRAY),
        LIGHT_GRAY((byte) 8, (byte) 7, ChatColor.GRAY),
        CYAN((byte) 9, (byte) 6, ChatColor.DARK_AQUA),
        PURPLE((byte) 10, (byte) 5, ChatColor.DARK_PURPLE),
        BLUE((byte) 11, (byte) 4, ChatColor.BLUE),
        BROWN((byte) 12, (byte) 3, null),
        GREEN((byte) 13, (byte) 2, ChatColor.DARK_GREEN),
        RED((byte) 14, (byte) 1, ChatColor.RED),
        BLACK((byte) 15, (byte) 0, ChatColor.BLACK);

        private byte woolData, dyeData;
        private ChatColor chatColor;

        StackColor(byte data, byte dyeData, ChatColor chatColor) {
            this.woolData = data;
            this.dyeData = dyeData;
            this.chatColor = chatColor;
        }

        public static StackColor fromChatColor(ChatColor chatColor) {
            for (StackColor stackColor : values()) {
                if (stackColor.getChatColor() == chatColor)
                    return stackColor;
            }
            return null;
        }

        public byte getData() {
            return woolData;
        }

        public byte getDyeData() {
            return dyeData;
        }

        public ChatColor getChatColor() {
            return chatColor;
        }

    }


}


