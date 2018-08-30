package net.assimilationmc.uhclobbyadaptor.lib;

import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum UHCGameSubType {

//    TEST_SINGLES(UHCGameType.SINGLES, "single test", new ItemBuilder(Material.YELLOW_FLOWER).setDisplay(ChatColor.GRAY + "Singles Dev").setLore("don't click me").build()),
//    TEST_TEAMED(UHCGameType.TEAMED, "team test", new ItemBuilder(Material.CACTUS).setDisplay(ChatColor.GRAY + "Team Dev").setLore("don't click me").build()),

    TEAMED_CLASSIC(UHCGameType.TEAMED, "Classic UHC", new ItemBuilder(Material.DIAMOND_SWORD)
            .setDisplay(ChatColor.BLUE + "Classic UHC")
            .setLore(C.C, C.C + "The original UHC we all know and love.", C.C).build()),
    //    TEAMED_BLIND(UHCGameType.TEAMED, "Blind Teams", new ItemBuilder(Material.WEB)
//            .setDisplay(ChatColor.WHITE + "Blind Teams")
//            .setLore(C.C, C.C + "UHC with a small twist...", C.C + "You don't know who is in your team until you", C.C + "hit them.", C.C).build()),
    TEAMED_SCATTER(UHCGameType.TEAMED, "Scatter", new ItemBuilder(Material.COMPASS)
            .setDisplay(ChatColor.GREEN + "Scatter").setLore(C.C, C.C + "Communications are out and equipped with only a tracker and your voice",
                    C.C + "you must reassemble your team to become victorious.", C.C, C.II + "Hard", C.C).build()),
    TEAMED_DEATHMATCH(UHCGameType.TEAMED, "Teamed Death-Match", new ItemBuilder(Material.SKULL_ITEM)
            .setDisplay(ChatColor.DARK_RED + "Death-Match")
            .setLore(C.C, C.C + "Play a short game until the death in a small map with boosted resources.", C.C).build()),

    SINGLES_CLASSIC(UHCGameType.SINGLES, "Classic UHC", new ItemBuilder(Material.DIAMOND_SWORD)
            .setDisplay(ChatColor.BLUE + "Classic UHC")
            .setLore(C.C, C.C + "The original UHC we all know and love.").build()),
    //    SINGLES_OP(UHCGameType.SINGLES, "OP-UHC", new ItemBuilder(Material.DIAMOND_CHESTPLATE)
//            .setDisplay(ChatColor.GOLD + "OP-UHC")
//            .setLore(C.C, C.C + "UHC but its OP!", C.C).build()),
    SINGLES_DEATHMATCH(UHCGameType.SINGLES, "Death-Match", new ItemBuilder(Material.DIAMOND_CHESTPLATE)
            .setDisplay(ChatColor.GOLD + "Death-Match")
            .setLore(C.C, C.C + "An intense battle to the death with resources that you collect", C.C).build()),
//    SINGLES_RANKED(UHCGameType.SINGLES, "Ranked (Competitive)", new ItemBuilder(Material.BREWING_STAND_ITEM)
//            .setDisplay(ChatColor.RED + "Ranked (Competitive)")
//            .setLore(C.C + "Play against friends or foes in a tense battle.", C.C, C.II + ChatColor.BOLD + "Can last up to 1 hour so make some time!",
//            C.II + ChatColor.BOLD + "Abandoning the game will have automatic consequences.").build())
    ;

    private UHCGameType type;
    private String display;
    private ItemStack item;

    UHCGameSubType(UHCGameType parent, String display, ItemStack itemStack) {
        this.type = parent;
        this.display = display;
        this.item = itemStack;
    }

    public UHCGameType getType() {
        return type;
    }

    public String getDisplay() {
        return display;
    }

    public ItemStack getItem() {
        return item;
    }

    public boolean isTeamed() {
        return type == UHCGameType.TEAMED;
    }

    public boolean isSingle() {
        return type == UHCGameType.SINGLES;
    }


}
