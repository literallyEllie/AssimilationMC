package net.assimilationmc.ellie.assipunish.punish;

import net.assimilationmc.ellie.assicore.api.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Ellie on 18/04/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public enum Punishment {

    CLIENT("Hacking/Client", new ItemBuilder(Material.DIAMOND_SWORD)
            .setDisplay("&f").setLore("&7Using &chacked clients &7or any &cunallowed mods&7.", "&f").build()
            , "Use of a 3rd party program which may have given you an advantage over players"),

    ADVERTISING("Advertising", new ItemBuilder(Material.BONE)
            .setDisplay("&f").
                    setLore("&cAdvertising links or addresses&7 that are to do with the server",
                            "&7If a player asks, the player can message the other privately", "&f").build(),
            "Advertising an websites/IP Addresses not related to Assimilation"),

    SPAMMING("Spamming", new ItemBuilder(Material.PAPER)
            .setDisplay("&f").setLore("&cSpamming &7a message (or similar) more than &73 &ctimes in a row", "&f").build(), "Spamming chat excessively"),

    TOXICITY("Toxicity", new ItemBuilder(Material.PACKED_ICE)
            .setDisplay("&f").setLore("&7Being &crude &7or &cimmature &7in the chat", "&f").build(), "Toxicity within chat"),

    TEAM_NAME("Inappropriate team name", new ItemBuilder(Material.ITEM_FRAME)
            .setDisplay("&f").setLore("&7Having a &cUHC team name &7like 'Nazis' etc", "&f").build(), "Inappropriate team name"),

    BAN_EVADE("Punishment evading", new ItemBuilder(Material.BARRIER)
            .setDisplay("&f").setLore("&cEvading a punishment &7through any means", "&f").build(), "Evading a punishment"),

    CHARGEBACK("Charge-back", new ItemBuilder(Material.RED_MUSHROOM)
            .setDisplay("&f").setLore("&7An unauthorised &ccharge back &7from a payment", "&f").build(), "Charging back from a payment"),

    THREATENING("Threatening server/staff", new ItemBuilder(Material.PACKED_ICE)
            .setDisplay("&f").setLore("&7Threatening to &cDDoS&7, &cDOX &7or not being nice :(", "&f").build(), "Threatening server or staff"),

    MALICIOUS_INTENT("Malicious Intent", new ItemBuilder(Material.FIREBALL)
            .setDisplay("&f").setLore("&cBreaking &7into the server etc", "&f").build(), "Carrying out malicious action on/against the server"),

    OTHER("Other", new ItemBuilder(Material.DIAMOND_AXE)
            .setDisplay("&f").setLore("&c&lOther &l&7Reason must be specified", "&f").build(), "");


    private String display;
    private ItemStack itemStack;
    private String rawReason;

    Punishment(String display, ItemStack itemStack, String rawReason){
        this.display = display;
        this.itemStack = itemStack;
        this.rawReason = rawReason;
    }

    public String getDisplay() {
        return display;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public String getRawReason() {
        return rawReason;
    }

}
