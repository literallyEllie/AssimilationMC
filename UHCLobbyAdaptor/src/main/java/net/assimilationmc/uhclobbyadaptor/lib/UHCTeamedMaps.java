package net.assimilationmc.uhclobbyadaptor.lib;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public enum UHCTeamedMaps {

    TTM("TTM", Collections.singletonList(UHCGameSubType.TEAMED_SCATTER), new ItemBuilder(Material.CAULDRON_ITEM).
            setDisplay(C.II + "Team test").setLore(C.II + "PLEASE DO NOT USE THIS").build(), Lists.newArrayList("xEline"), false),
    DAN_BIG_BOI("DanBigBoiWorld", Lists.newArrayList(UHCGameSubType.TEAMED_SCATTER), new ItemBuilder(Material.GRASS)
            .setDisplay(ChatColor.GREEN + "Dan is a big boi").build(), Lists.newArrayList("danb76"), false),
    SAND("SandWorld", Lists.newArrayList(UHCGameSubType.TEAMED_DEATHMATCH), new ItemBuilder(Material.SAND)
            .setDisplay(ChatColor.YELLOW + "Sand").build(), Lists.newArrayList("L3eroy"), false),

    GRAVEL("GravelRoad", Lists.newArrayList(UHCGameSubType.TEAMED_DEATHMATCH), new ItemBuilder(Material.GRAVEL)
            .setDisplay(ChatColor.RED + "Gravel Road")
            .setLore(C.C + "3v3 " + C.II + "Hard").build(), Lists.newArrayList("Perforalia")),
    TOWERS("Towers", Lists.newArrayList(UHCGameSubType.TEAMED_DEATHMATCH), new ItemBuilder(Material.SMOOTH_BRICK)
            .setDisplay(ChatColor.YELLOW + "Towers").setLore(C.C + "2v2").build(), Lists.newArrayList("Anyea")),

    FOREST("Forest", Lists.newArrayList(UHCGameSubType.TEAMED_CLASSIC, UHCGameSubType.TEAMED_SCATTER), new ItemBuilder(Material.SAPLING)
            .setDisplay(ChatColor.GREEN + "Forest").build(), Lists.newArrayList("Mr_Edo_", "Anyea", "danb76"))
    ;

    private String id;
    private List<UHCGameSubType> applicableTypes;
    private ItemStack itemStack;
    private List<String> builders;
    private boolean enabled;

    UHCTeamedMaps(String id, List<UHCGameSubType> applicableTypes, ItemStack itemStack, List<String> builders, boolean enabled) {
        this.id = id;
        this.applicableTypes = applicableTypes;
        this.itemStack = new ItemBuilder(itemStack).setLore("", C.II + "Builders: " + C.V + Joiner.on(C.C + ", " + C.V).join(builders)).build();
        this.builders = builders;
        this.enabled = enabled;
    }

    UHCTeamedMaps(String id, List<UHCGameSubType> applicableTypes, ItemStack itemStack, List<String> builders) {
        this(id, applicableTypes, itemStack, builders, true);
    }

    public String getId() {
        return id;
    }

    public List<UHCGameSubType> getApplicableTypes() {
        return applicableTypes;
    }

    public boolean isApplicable(UHCGameSubType gameSubType) {
        return gameSubType.isTeamed() && applicableTypes.contains(gameSubType);
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
