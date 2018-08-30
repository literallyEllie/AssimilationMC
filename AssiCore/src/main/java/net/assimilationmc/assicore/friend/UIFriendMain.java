package net.assimilationmc.assicore.friend;

import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.ui.Button;
import net.assimilationmc.assicore.ui.UI;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.ItemBuilder;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public class UIFriendMain extends UI {

    private final FriendData friendData;

    public UIFriendMain(AssiPlugin plugin, FriendData data) {
        super(plugin, ChatColor.GREEN + "Friend Menu", 54);
        this.friendData = data;

        Button button = new Button() {
            @Override
            public ItemStack getItemStack(AssiPlayer clicker) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE).setColor(ItemBuilder.StackColor.LIME).setDisplay(C.II).build();
            }

            @Override
            public void onAction(AssiPlayer clicker, ClickType clickType) {
            }
        };

        // Footer outline
        for (int i = 45; i < getInventorySize(); i++) {
            addButton(i, button);
        }

        // Add

        addButton(50, new Button() {
            @Override
            public ItemStack getItemStack(AssiPlayer clicker) {
                return new ItemBuilder(Material.NAME_TAG).setDisplay(ChatColor.YELLOW + "Make a new friend!")
                        .setLore(C.C, C.C + "Add a friend").build();
            }

            @Override
            public void onAction(AssiPlayer clicker, ClickType clickType) {
                closeInventory(clicker.getBase());
                clicker.getBase().spigot().sendMessage(new ComponentBuilder("Click here to add a player")
                        .color(net.md_5.bungee.api.ChatColor.RED).bold(true).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/fr add ")).create());
            }
        });

        // Exit
        addButton(53, new Button() {
            @Override
            public ItemStack getItemStack(AssiPlayer clicker) {
                return new ItemBuilder(Material.IRON_DOOR).setDisplay(C.II + "Exit").build();
            }

            @Override
            public void onAction(AssiPlayer clicker, ClickType clickType) {
                closeInventory(clicker.getBase());
            }
        });

        refreshFriendList();
        refreshSettings(0);
    }

    public void refreshFriendList() {

        for (int i = 0; i < 43; i++) {
            removeButton(i);
        }

        if (friendData.getFriends().isEmpty()) {
            addButton(13, new Button() {
                @Override
                public ItemStack getItemStack(AssiPlayer clicker) {
                    return new ItemBuilder(Material.SPONGE).setDisplay(ChatColor.LIGHT_PURPLE + "Oh no,")
                            .setLore(C.C, ChatColor.YELLOW + "It appears you have no friends.").build();
                }

                @Override
                public void onAction(AssiPlayer clicker, ClickType clickType) {
                }
            });
            return;
        }

        for (Map.Entry<UUID, String> uuidStringEntry : friendData.getFriends().entrySet()) {
            final UUID uuid = uuidStringEntry.getKey();
            final String name = uuidStringEntry.getValue();
            addButton(new Button() {
                @Override
                public ItemStack getItemStack(AssiPlayer clicker) {
                    return new ItemBuilder(Material.SKULL_ITEM).asPlayerHead(name).setDisplay(ChatColor.GRAY + name)
                            .setLore(C.C, C.V + "Right click " + C.II + "to remove").build();
                }

                @Override
                public void onAction(AssiPlayer clicker, ClickType clickType) {
                    if (!clickType.isRightClick()) return;

                    final String s = getPlugin().getFriendManager().removeFriend(clicker, uuid);

                    if (s.equals("REFRESH_NFS")) {
                        clicker.sendMessage(C.II + "You were not friends with that player.");
                    }

                    closeInventory(clicker.getBase());

                }
            });

        }

    }

    public void refreshSettings(int slot) {

        if (slot == 0 || slot == 45) {
            removeButton(45);

            addButton(45, new Button() {
                @Override
                public ItemStack getItemStack(AssiPlayer clicker) {
                    return new ItemBuilder(Material.WOOL).setColor(friendData.isAllowRequests() ? ItemBuilder.StackColor.GREEN : ItemBuilder.StackColor.RED)
                            .setDisplay((friendData.isAllowRequests() ? ChatColor.GREEN : ChatColor.RED) + "Receive Friend Requests")
                            .setLore(C.C, C.C + "Toggling the ability for players to send",
                                    C.C + "you friend requests.",
                                    C.C, C.V + "Right click " + C.II + "to toggle").build();
                }

                @Override
                public void onAction(AssiPlayer clicker, ClickType clickType) {

                    friendData.setAllowRequests(!friendData.isAllowRequests());
                    clicker.sendMessage(C.C + "Players can " + (friendData.isAllowRequests() ? ChatColor.GREEN + "now" : ChatColor.RED + "no longer") + C.C +
                            " send you friend requests.");

                    refreshSettings(45);
                    open(clicker.getBase());
                }

            });

        }

        if (slot == 0 || slot == 46) {
            removeButton(46);

            addButton(46, new Button() {
                @Override
                public ItemStack getItemStack(AssiPlayer clicker) {
                    return new ItemBuilder(Material.WOOL).setColor(friendData.isSeeFriendBroadcast() ? ItemBuilder.StackColor.GREEN : ItemBuilder.StackColor.RED)
                            .setDisplay((friendData.isSeeFriendBroadcast() ? ChatColor.GREEN : ChatColor.RED) + "Receive Friend Broadcasts")
                            .setLore(C.C, C.C + "Toggling the ability to receive broadcasts.",
                                    C.C + " from friends", C.C, C.V + "Right click " + C.II + "to toggle").build();

                }

                @Override
                public void onAction(AssiPlayer clicker, ClickType clickType) {

                    friendData.setSeeFriendBroadcast(!friendData.isSeeFriendBroadcast());
                    clicker.sendMessage(C.C + "You will " + (friendData.isSeeFriendBroadcast() ? ChatColor.GREEN + "now" : ChatColor.RED + "no longer") + C.C +
                            " see broadcasts from friends.");

                    refreshSettings(46);
                    open(clicker.getBase());
                }
            });

        }

        if (slot == 0 || slot == 47) {
            removeButton(47);

            addButton(47, new Button() {
                @Override
                public ItemStack getItemStack(AssiPlayer clicker) {
                    return new ItemBuilder(Material.WOOL).setColor(friendData.isSendJoinLeave() ? ItemBuilder.StackColor.GREEN : ItemBuilder.StackColor.RED)
                            .setDisplay((friendData.isSendJoinLeave() ? ChatColor.GREEN : ChatColor.RED) + "Send join/leave notifications")
                            .setLore(C.C, C.C + "Toggle the ability for notifications to be",
                                    C.C + "sent to your friends when you join/leave the network.",
                                    C.C, C.V + "Right click " + C.II + "to toggle").build();
                }

                @Override
                public void onAction(AssiPlayer clicker, ClickType clickType) {

                    friendData.setSendJoinLeave(!friendData.isSendJoinLeave());
                    clicker.sendMessage(C.C + "You friends will " + (friendData.isSendJoinLeave() ? ChatColor.GREEN + "now" : ChatColor.RED + "no longer") + C.C +
                            " receive notifications when you join/leave the network.");
                    refreshSettings(47);
                    open(clicker.getBase());

                }
            });

        }

        if (slot == 0 || slot == 48) {
            removeButton(48);

            addButton(48, new Button() {
                @Override
                public ItemStack getItemStack(AssiPlayer clicker) {
                    return new ItemBuilder(Material.WOOL).setColor(friendData.isSeeJoinLeave() ? ItemBuilder.StackColor.GREEN : ItemBuilder.StackColor.RED)
                            .setDisplay((friendData.isSeeJoinLeave() ? ChatColor.GREEN : ChatColor.RED) + "Receive join/leave notifications")
                            .setLore(C.C, C.C + "Toggle the status of friend notifications",
                                    C.C + "sent to you.",
                                    C.C, C.V + "Right click " + C.II + "to toggle").build();

                }

                @Override
                public void onAction(AssiPlayer clicker, ClickType clickType) {

                    friendData.setSeeJoinLeave(!friendData.isSeeJoinLeave());
                    clicker.sendMessage(C.C + "You friends will " + (friendData.isSeeJoinLeave() ? ChatColor.GREEN + "now" : ChatColor.RED + "no longer") + C.C +
                            " receive notifications when your friends join/leave the network.");
                    refreshSettings(48);
                    open(clicker.getBase());

                }
            });

        }

    }

}
