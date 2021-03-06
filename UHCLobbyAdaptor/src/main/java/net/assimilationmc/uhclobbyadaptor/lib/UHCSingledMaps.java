package net.assimilationmc.uhclobbyadaptor.lib;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public enum UHCSingledMaps {

//    STM("STM", Collections.singletonList(UHCGameSubType.TEST_SINGLES), new ItemBuilder(Material.CAULDRON_ITEM).
//            setDisplay(C.II + "Singles test").setLore(C.II + "PLEASE DO NOT USE THIS").build(), Lists.newArrayList("xEline")),

//    SAND("SandWorld", Lists.newArrayList(UHCGameSubType.TEST_SINGLES), new ItemBuilder(Material.SAND)
//            .setDisplay(ChatColor.YELLOW + "Sand").build(), Lists.newArrayList("L3eroy")),
    DAN_BIG_BOI("DanBigBoiWorld", Lists.newArrayList(UHCGameSubType.SINGLES_CLASSIC), new ItemBuilder(Material.GRASS)
        .setDisplay(ChatColor.GREEN + "Dan is a big boi").build(), Lists.newArrayList("danb76"), false),

    GRAVEL("GravelRoad", Lists.newArrayList(UHCGameSubType.SINGLES_DEATHMATCH), new ItemBuilder(Material.GRAVEL)
            .setDisplay(ChatColor.RED + "Gravel Road")
            .setLore(C.C + "1v1").build(), Lists.newArrayList("Perforalia")),
    TOWERS("Towers", Lists.newArrayList(UHCGameSubType.SINGLES_DEATHMATCH), new ItemBuilder(Material.SMOOTH_BRICK)
            .setDisplay(ChatColor.YELLOW + "Towers").setLore(C.C + "1v1").build(), Lists.newArrayList("Anyea")),

    FOREST("Forest", Lists.newArrayList(UHCGameSubType.SINGLES_CLASSIC), new ItemBuilder(Material.SAPLING)
    .setDisplay(ChatColor.GREEN + "Forest").build(), Lists.newArrayList("Mr_Edo_", "Anyea", "danb76")),


    ;


    private String id;
    private List<UHCGameSubType> applicableTypes;
    private ItemStack itemStack;
    private List<String> builders;
    private boolean enabled;

    UHCSingledMaps(String id, List<UHCGameSubType> applicableTypes, ItemStack itemStack, List<String> builders, boolean enabled) {
        this.id = id;
        this.applicableTypes = applicableTypes;
        this.itemStack = new ItemBuilder(itemStack).setLore("", C.II + "Builders: " + C.V + Joiner.on(C.C + ", " + C.V).join(builders)).build();
        this.builders = builders;
        this.enabled = enabled;
    }

    UHCSingledMaps(String id, List<UHCGameSubType> applicableTypes, ItemStack itemStack, List<String> builders) {
        this(id, applicableTypes, itemStack, builders, true);
    }

    public String getId() {
        return id;
    }

    public List<UHCGameSubType> getApplicableTypes() {
        return applicableTypes;
    }

    public boolean isApplicable(UHCGameSubType gameSubType) {
        return gameSubType.isSingle() && applicableTypes.contains(gameSubType);
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public List<String> getBuilders() {
        return builders;
    }

    public boolean isEnabled() {
        return enabled;
    }

}
