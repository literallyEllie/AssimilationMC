package net.assimilationmc.assicore.cosmetic.command;

import com.google.common.collect.Maps;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.cosmetic.Cosmetic;
import net.assimilationmc.assicore.cosmetic.CosmeticType;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.ui.Button;
import net.assimilationmc.assicore.ui.UI;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.ItemBuilder;
import net.assimilationmc.assicore.util.UtilPaging;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public class MenuCosmetics extends UI {

    private static final int MAX_PAGE_ELEMENTS = 9 * 5;
    private Map<UUID, Integer> pages;

    public MenuCosmetics(AssiPlugin plugin) {
        super(plugin, ChatColor.GREEN + "Cosmetics", 54);

        this.pages = Maps.newHashMap();

        addButton(48, new Button() {
            @Override

            public ItemStack getItemStack(AssiPlayer clicker) {
                return new ItemBuilder(
                        ItemBuilder.getSkull("eed78822576317b048eea92227cd85f7afcc44148dcb832733baccb8eb56fa1"))
                        .setDisplay(C.C + "Previous page").build();
            }

            @Override
            public void onAction(AssiPlayer clicker, ClickType clickType) {
                int page = pages.get(clicker.getUuid());
                if (page - 1 == 0) {
                    return;
                }

                pages.replace(clicker.getUuid(), page - 1);
                showPage(clicker, page - 1);
            }
        });

        addButton(50, new Button() {
            @Override
            public ItemStack getItemStack(AssiPlayer clicker) {
                return new ItemBuilder(
                        ItemBuilder.getSkull("715445da16fab67fcd827f71bae9c1d2f90c73eb2c1bd1ef8d8396cd8e8"))
                        .setDisplay(C.C + "Next page").build();
            }

            @Override
            public void onAction(AssiPlayer clicker, ClickType clickType) {
                int page = pages.get(clicker.getUuid());
                if (UtilPaging.getPageCount(CosmeticType.values().length, MAX_PAGE_ELEMENTS) > page + 1) {
                    return;
                }

                pages.replace(clicker.getUuid(), page + 1);
                showPage(clicker, page + 1);

            }
        });

        addButton(53, new Button() {
            @Override
            public ItemStack getItemStack(AssiPlayer clicker) {
                return new ItemBuilder(Material.IRON_DOOR)
                        .setDisplay(ChatColor.RED + "Exit").build();
            }

            @Override
            public void onAction(AssiPlayer clicker, ClickType clickType) {
                closeInventory(clicker.getBase());
            }
        });

    }

    @Override
    public void open(Player player) {
        for (int i = 0; i < MAX_PAGE_ELEMENTS; i++) {
            removeButton(i);
        }

        pages.put(player.getUniqueId(), 1);

        showPage(getPlugin().getPlayerManager().getPlayer(player), 1);

        super.open(player);
    }

    @Override
    public void onClose(Player player) {
        pages.remove(player.getUniqueId());

        super.onClose(player);
    }

    public void showPage(AssiPlayer player, int pageIndex) {
        final CosmeticType[] types = CosmeticType.values();

        int pageCount = UtilPaging.getPageCount(types.length, MAX_PAGE_ELEMENTS);
        if (pageCount > pageIndex) return;

        for (int i = UtilPaging.getPageElementIndex(pageIndex, pageCount, MAX_PAGE_ELEMENTS); i < MAX_PAGE_ELEMENTS && i < types.length; i++) {
            Cosmetic cosmetic = getPlugin().getCosmeticManager().getCosmetic(types[i].getClazz());
            if (cosmetic == null) continue;

            boolean perm = player.hasCosmetic(cosmetic.getType());

            addButton(new Button() {
                @Override
                public ItemStack getItemStack(AssiPlayer clicker) {
                    return new ItemBuilder(cosmetic.getType().getMaterial())
                            .setDisplay((perm ? ChatColor.GREEN.toString() : ChatColor.RED.toString()) + cosmetic.getName())
                            .setLore(C.C, (perm ? ChatColor.DARK_PURPLE + "You can use this!" : ChatColor.RED + ChatColor.BOLD.toString() + "No permission!"),
                                    (perm ? (getPlugin().getCosmeticManager().hasActiveCosmetic(clicker.getBase(),
                                            cosmetic.getType()) ? ChatColor.GREEN + "Currently activated." : null) :
                                            cosmetic.formatPrice()), (perm ? C.C : null),
                                    C.C + cosmetic.getDescription()).build();
                }

                @Override
                public void onAction(AssiPlayer clicker, ClickType clickType) {

                    if (perm) {
                        boolean isActive = getPlugin().getCosmeticManager().hasActiveCosmetic(player.getBase(), cosmetic.getType());

                        if (isActive) {
                            getPlugin().getCosmeticManager().removePlayerCosmetic(player.getBase(), cosmetic.getType());
                        } else {
                            getPlugin().getCosmeticManager().playPlayerCosmetic(player.getBase(), cosmetic);
                        }

                        clicker.sendMessage(getPlugin().getCommandManager().getCommand(CmdCosmetic.class).prefix("Cosmetics") +
                                "You have " + (isActive ? "de" : "") + "activated the cosmetic " + C.V + cosmetic.getName() + C.C + ".");

                        closeInventory(clicker.getBase());
                        open(clicker.getBase());

                    } else if (cosmetic.getPriceBucks() > 0) {

                        // buy
                    } else if (cosmetic.getPriceUC() > 0) {

                    }

                }
            });

        }


    }

}
