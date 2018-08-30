package net.assimilationmc.assicore.booster.ui;

import net.assimilationmc.assicore.booster.BoosterManager;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.ui.Button;
import net.assimilationmc.assicore.ui.UI;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class UIBoosterShop extends UI {

    public UIBoosterShop(BoosterManager boosterManager) {
        super(boosterManager.getPlugin(), ChatColor.GOLD + "Booster shop", 27);

        boosterManager.getBoosters().forEach((boosterType, booster) -> addButton(new Button() {
            @Override
            public ItemStack getItemStack(AssiPlayer clicker) {
                return new ItemBuilder(booster.getItemStack().clone()).setLore(ChatColor.RED + "Price: " + C.UC + booster.getPrice() + " Ultra Coins").build();
            }

            @Override
            public void onAction(AssiPlayer clicker, ClickType clickType) {
                if (!clicker.canAffordUltraCoins(booster.getPrice())) {
                    clicker.sendMessage(C.II + "You cannot afford that!");
                    return;
                }

                clicker.takeUltraCoins(booster.getPrice());
                clicker.addBooster(booster.getId());

                clicker.sendMessage(ChatColor.GOLD + "You have bought a " + C.V + booster.getPretty() + ChatColor.GOLD + " booster.");

            }
        }));

        addButton(26, new Button() {
            @Override
            public ItemStack getItemStack(AssiPlayer clicker) {
                return new ItemBuilder(Material.IRON_DOOR).setDisplay(ChatColor.RED + "Back").build();
            }

            @Override
            public void onAction(AssiPlayer clicker, ClickType clickType) {
                closeInventory(clicker.getBase());
                clicker.getBase().performCommand("boosters");
            }
        });

    }

}
