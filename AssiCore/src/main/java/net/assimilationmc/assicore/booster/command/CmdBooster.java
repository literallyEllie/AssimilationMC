package net.assimilationmc.assicore.booster.command;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.booster.ActiveBooster;
import net.assimilationmc.assicore.booster.Booster;
import net.assimilationmc.assicore.booster.BoosterManager;
import net.assimilationmc.assicore.booster.ui.UIBoosterShop;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.ui.Button;
import net.assimilationmc.assicore.ui.UI;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.ItemBuilder;
import net.assimilationmc.assicore.util.UtilPlayer;
import net.assimilationmc.assicore.util.UtilTime;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

public class CmdBooster extends AssiCommand {

    private UI uiBooster;
    private UIBoosterShop uiBoosterShop;

    public CmdBooster(BoosterManager boosterManager) {
        super(boosterManager.getPlugin(), "booster", "Booster menu", Lists.newArrayList("boosters"));
        requirePlayer();

        this.uiBoosterShop = new UIBoosterShop(boosterManager);

        ItemBuilder unknownBooster = new ItemBuilder(Material.BARRIER).setDisplay(ChatColor.RED + "Unrecognized booster")
                .setLore(C.C, C.C + ChatColor.ITALIC + "This booster is foreign to these lands",
                        C.C + ChatColor.ITALIC + "so cannot be used here...");

        this.uiBooster = new UI(plugin, ChatColor.AQUA + "Boosters", 54) {

            @Override
            public void open(Player player) {

                for (int i = 0; i < getInventorySize(); i++) {
                    Button button = getButton(i);
                    if (button == null || button.getItemStack(null).getType() != Material.INK_SACK) continue;
                    removeButton(i);
                }

                int slot = 10;

                AssiPlayer assiPlayer = getPlugin().getPlayerManager().getPlayer(player);

                if (assiPlayer.getBoosters().isEmpty()) {

                    addButton(21, new Button() {
                        @Override
                        public ItemStack getItemStack(AssiPlayer clicker) {
                            return new ItemBuilder(Material.GOLD_BLOCK)
                                    .setDisplay(ChatColor.LIGHT_PURPLE + "Oh no...")
                                    .setLore(C.C, ChatColor.YELLOW + "It looks like you have no boosters D:",
                                            C.II + "FEAR NOT! " + ChatColor.YELLOW + "Click me to get a link to find some. <3", C.C).build();
                        }

                        @Override
                        public void onAction(AssiPlayer clicker, ClickType clickType) {
                            clicker.getBase().chat("/store");
                            clicker.sendMessage(ChatColor.YELLOW + "For Boosters, find the " + ChatColor.BOLD + "Boosters" + ChatColor.YELLOW + " tab.");
                            closeInventory(clicker.getBase());
                        }
                    });

                    addButton(30, new Button() {
                        @Override
                        public ItemStack getItemStack(AssiPlayer clicker) {
                            return new ItemBuilder(Material.DIAMOND_BLOCK)
                                    .setDisplay(ChatColor.GREEN.toString() + "OR...")
                                    .setLore(C.C, ChatColor.YELLOW + "Click me to buy some here in game!", C.C).build();
                        }

                        @Override
                        public void onAction(AssiPlayer clicker, ClickType clickType) {
                            closeInventory(clicker.getBase());
                            uiBoosterShop.open(clicker.getBase());
                        }
                    });

                } else {

                    removeButton(21);
                    removeButton(30);

                    for (Map.Entry<String, Integer> stringIntegerEntry : assiPlayer.getBoosters().entrySet()) {
                        Booster booster = getPlugin().getBoosterManager().getBooster(stringIntegerEntry.getKey());
                        final Integer amount = stringIntegerEntry.getValue();

                        addButton(slot, new Button() {
                            @Override
                            public ItemStack getItemStack(AssiPlayer clicker) {
                                return (booster == null ? unknownBooster.setAmount(amount).build() : new ItemBuilder(booster.getItemStack().clone())
                                        .setAmount(amount)
                                        .setLore(false, (getPlugin().getBoosterManager().getActiveBooster() == null ?
                                                (C.V + "Click" + C.II + " to start this booster.") : null)).build());
                            }

                            @Override
                            public void onAction(AssiPlayer clicker, ClickType clickType) {
                                if (booster == null || getPlugin().getBoosterManager().getActiveBooster() != null)
                                    return;
                                getPlugin().getBoosterManager().startBooster(clicker, booster);
                                closeInventory(player);
                            }
                        });

                        slot++;

                        if (slot == 15) {
                            slot = 19;
                        } else if (slot == 24) {
                            slot = 28;
                        } else if (slot == 33) {
                            slot = 37;
                        }

                    }

                    addButton(43, new Button() {
                        @Override
                        public ItemStack getItemStack(AssiPlayer clicker) {
                            return new ItemBuilder(Material.DIAMOND_BLOCK)
                                    .setDisplay(ChatColor.GREEN.toString() + "Booster Shop")
                                    .setLore(C.C, ChatColor.YELLOW + "Click me to buy some boosters!", C.C).build();
                        }

                        @Override
                        public void onAction(AssiPlayer clicker, ClickType clickType) {
                            closeInventory(clicker.getBase());
                            uiBoosterShop.open(clicker.getBase());
                        }
                    });

                }

                super.open(player);
            }

        };

        ItemStack borderBlue = new ItemBuilder(Material.STAINED_GLASS_PANE).setDisplay(C.C).setColor(ItemBuilder.StackColor.BLUE).build();
        ItemStack borderWhite = new ItemBuilder(Material.STAINED_GLASS_PANE).setDisplay(C.C).setColor(ItemBuilder.StackColor.WHITE).build();

        uiBooster.addButton(3, new Button() {
            @Override
            public ItemStack getItemStack(AssiPlayer clicker) {
                if (clicker == null) return new ItemStack(Material.SKULL_ITEM);
                return new ItemBuilder(Material.SKULL_ITEM).asPlayerHead(clicker.getName()).
                        setDisplay(ChatColor.GREEN + "You").build();
            }

            @Override
            public void onAction(AssiPlayer clicker, ClickType clickType) {
            }
        });

        uiBooster.addButton(25, new Button() {
            @Override
            public ItemStack getItemStack(AssiPlayer clicker) {
                if (clicker == null) return new ItemStack(Material.SKULL_ITEM);

                final ActiveBooster activeBooster = plugin.getBoosterManager().getActiveBooster();

                return activeBooster == null ? new ItemBuilder(Material.SKULL_ITEM)
                        .setDurability((short) 3).setDisplay(C.C + ChatColor.ITALIC + "No booster active").build() :
                        new ItemBuilder(Material.SKULL_ITEM).asPlayerHead(activeBooster.getStarter()).setDisplay(ChatColor.GOLD + activeBooster.getStarter())
                                .setLore(C.C + "has the booster", activeBooster.getBooster().getPretty() + C.C + " activated.", C.C,
                                        C.C + "It expires in " + C.V + UtilTime.formatTimeStamp(activeBooster.getRemaining())).build();
            }

            @Override
            public void onAction(AssiPlayer clicker, ClickType clickType) {
            }
        });

        uiBooster.addButton(34, new Button() {
            @Override
            public ItemStack getItemStack(AssiPlayer clicker) {
                if (clicker == null) return new ItemStack(Material.PAPER);
                final ActiveBooster activeBooster = plugin.getBoosterManager().getActiveBooster();

                return new ItemBuilder(Material.PAPER).setDisplay(ChatColor.GREEN + "Tip" +
                        (activeBooster != null ? " " + activeBooster.getStarter() : "")).setLore(C.C, (activeBooster != null ?
                        (clicker.getName().equals(activeBooster.getStarter()) ? C.II + "You cannot tip yourself!" : C.V + "Tip them " + ChatColor.GREEN + "5 bucks") :
                        ChatColor.RED + "There is currently no one to tip."), C.C).build();
            }

            @Override
            public void onAction(AssiPlayer clicker, ClickType clickType) {
                final ActiveBooster activeBooster = plugin.getBoosterManager().getActiveBooster();
                if (activeBooster == null || clicker.getName().equals(activeBooster.getStarter())) return;

                if (!clicker.canAffordBucks(5)) {
                    uiBooster.closeInventory(clicker.getBase());
                    clicker.sendMessage(C.II + "You cannot afford that!");
                    return;
                }

                clicker.takeBucks(5);

                Player bStarter = UtilPlayer.get(activeBooster.getStarter());
                AssiPlayer starter;
                if (bStarter == null) {
                    UUID uuid = plugin.getPlayerManager().getUUID(activeBooster.getStarter());
                    if (uuid == null) {
                        // wot
                        clicker.sendMessage(C.II + "Failed to find the data of " + activeBooster.getStarter());
                        return;
                    }

                    starter = plugin.getPlayerManager().getOfflinePlayer(uuid);
                } else starter = plugin.getPlayerManager().getPlayer(bStarter);

                starter.addBucks(5);

                String message = C.C + ChatColor.ITALIC + "You have been tipped " + ChatColor.GREEN + "5 Bucks" + C.C + ChatColor.ITALIC +
                        " for your booster.";

                if (bStarter != null) {
                    bStarter.sendMessage(message);
                } else {
                    message += " (On " + plugin.getServerData().getId() + ")";
                    plugin.getPlayerManager().attemptGlobalPlayerMessage(starter.getUuid(), false, message);
                }

            }
        });

        for (Integer integer : Arrays.asList(0, 1, 2, 4, 5, 6, 7, 8,
                9, 15, 17,
                18, 24, 26,
                27, 33, 35,
                36, 42, 44,
                45, 46, 47, 48, 49, 50, 51, 52, 53)) {

            uiBooster.addButton(integer, new Button() {
                @Override
                public ItemStack getItemStack(AssiPlayer clicker) {
                    return borderBlue;
                }

                @Override
                public void onAction(AssiPlayer clicker, ClickType clickType) {
                }
            });
        }

        uiBooster.addButton(16, new Button() {
            @Override
            public ItemStack getItemStack(AssiPlayer clicker) {
                return borderWhite;
            }

            @Override
            public void onAction(AssiPlayer clicker, ClickType clickType) {
            }
        });


        if (!uiBooster.isButton(43)) {
            uiBooster.addButton(43, new Button() {
                @Override
                public ItemStack getItemStack(AssiPlayer clicker) {
                    return borderWhite;
                }

                @Override
                public void onAction(AssiPlayer clicker, ClickType clickType) {
                }
            });
        }

    }

    @Override
    public void onCommand(CommandSender sender, String usedLabel, String[] args) {
        uiBooster.open((Player) sender);
    }

}
