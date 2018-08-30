package net.assimilationmc.assicore.punish.cmd;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.punish.PunishProfile;
import net.assimilationmc.assicore.punish.PunishSorter;
import net.assimilationmc.assicore.punish.model.PunishmentCategory;
import net.assimilationmc.assicore.punish.model.PunishmentData;
import net.assimilationmc.assicore.punish.model.UnpunishData;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.ui.Button;
import net.assimilationmc.assicore.ui.UI;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.ItemBuilder;
import net.assimilationmc.assicore.util.UtilString;
import net.assimilationmc.assicore.util.UtilTime;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CmdPunish extends AssiCommand {

    private final List<Integer> punishButtonBounds = Arrays.asList(20, 21, 22, 23, 24, 29, 30, 31, 32, 33);
    private UI punishUI;
    private String uiTitle = C.II + "Punish user " + C.V;

    public CmdPunish(AssiPlugin plugin) {
        super(plugin, "punish", "Punish a player", Rank.HELPER, Lists.newArrayList("unpunish", "ban", "kick"), "<player>", "[reason]");
        requirePlayer();
        this.punishUI = new UI(plugin, uiTitle, 54);

    }

    @Override
    public void onCommand(CommandSender commandSender, String usedLabel, String[] args) {
        AssiPlayer sender = asPlayer(commandSender);

        if (args[0].equalsIgnoreCase(sender.getName())) {
            sender.sendMessage(C.II + "You cannot punish yourself.");
            return;
        }

        UUID uuid = plugin.getPlayerManager().getUUID(args[0]);
        if (uuid == null) {
            sender.sendMessage(C.C + "It doesn't look like anyone with the name " + C.V + args[0] + C.C + " has ever joined the network.");
            return;
        }

        AssiPlayer target = plugin.getPlayerManager().getOfflinePlayer(uuid);

        if (target == null) {
            sender.sendMessage(C.C + "Couldn't acquire their data (null)");
            return;
        }

        if (target.getRank().isHigherThanOrEqualTo(sender.getRank()) && !sender.getRank().isHigherThanOrEqualTo(Rank.MANAGER)) {
            sender.sendMessage(C.II + "You cannot punish a user with the same rank or higher than you.");
            return;
        }

        target.getPunishmentProfile(true);

        if (usedLabel.equalsIgnoreCase("unpunish")) {
            buildCurrentPunishments(sender, target, args.length > 1 ? UtilString.getFinalArg(args, 1) : null);
        } else buildPunishMenu(sender, target, args.length > 1 ? UtilString.getFinalArg(args, 1) : null);

    }

    /**
     * Menu to build the main punishment menu.
     *
     * @param player the player punishing.
     * @param target the player who could get un/punished
     * @param reason the reason for the un/punish.
     */
    private void buildPunishMenu(AssiPlayer player, AssiPlayer target, String reason) {
        punishUI.removeAllButtons();

        this.punishUI.setInventoryTitle(uiTitle + target.getName());

        punishUI.addButton(4, new Button() {
            @Override
            public ItemStack getItemStack(AssiPlayer clicker) {
                return new ItemBuilder(Material.SKULL_ITEM).asPlayerHead(target.getName())
                        .setDisplay(target.getDisplayName())
                        .setLore(C.C, C.C + "Previous names: " + C.V + Joiner.on(C.C + ", " + C.V).join(target.getPreviousNames()),
                                C.C + "Last IP: " + C.V + (clicker.getRank().isHigherThanOrEqualTo(Rank.MOD) ? target.lastIP() : "[censored]"),
                                C.C + "Last seen: " + C.V + UtilTime.formatTimeStamp(target.getLastSeen()), C.C,
                                C.II + "Click the Paper icons to view more punishment details", C.C).build();
            }

            @Override
            public void onAction(AssiPlayer clicker, ClickType clickType) {
            }
        });

        final PunishmentCategory[] punishmentCategory = PunishmentCategory.values();

        int index = 0;
        for (Integer slots : punishButtonBounds) {
            PunishmentCategory category = punishmentCategory[index];

            punishUI.addButton(slots, new Button() {
                @Override
                public ItemStack getItemStack(AssiPlayer clicker) {

                    ItemBuilder builder = new ItemBuilder(category.getDisplayMaterial());

                    // add for some punishments
                    if (clicker.getRank() == Rank.HELPER && category.getMaxOffenses() == 1) {
                        return builder.setDisplay(C.II + ChatColor.STRIKETHROUGH + category.getDisplay())
                                .setLore(C.C, C.II + ChatColor.BOLD + "You do not have permission to do this.",
                                        C.II + "Contact a moderator if there is a problem.", C.C).build();
                    }

                    return builder.setDisplay(C.V + category.getDisplay()).setLore(C.C, C.C + "Example:", C.V + category.getExample(), C.C).build();
                }

                @Override
                public void onAction(AssiPlayer clicker, ClickType clickType) {

                    // basic no-perm punishment filter, rest is checked elsewhere
                    if (clicker.getRank() == Rank.HELPER && category.getMaxOffenses() == 1) {
                        return;
                    }
                    plugin.getPunishmentManager().punish(clicker, target, category, reason);

                    punishUI.closeInventory(clicker.getBase());
                }
            });

            index++;
        }

        punishUI.addButton(48, new Button() {
            @Override
            public ItemStack getItemStack(AssiPlayer clicker) {
                return new ItemBuilder(Material.PAPER).setDisplay(C.II + "Current Punishments")
                        .setLore(C.C, C.C + "From this menu you can view current punishments",
                                C.C + "and choose to remove them.",
                                C.II + "To remove a punishment you MUST specify a reason.").build();
            }

            @Override
            public void onAction(AssiPlayer clicker, ClickType clickType) {
                buildCurrentPunishments(player, target, reason);
            }
        });

        punishUI.addButton(50, new Button() {
            @Override
            public ItemStack getItemStack(AssiPlayer clicker) {
                return new ItemBuilder(Material.PAPER).setDisplay(C.II + "Punishment History").setLore(C.C, C.C + "From this menu you can view old punishments.").build();
            }

            @Override
            public void onAction(AssiPlayer clicker, ClickType clickType) {
                buildPastPunishments(player, target);
            }
        });

        punishUI.addButton(53, new Button() {
            @Override
            public ItemStack getItemStack(AssiPlayer clicker) {
                return new ItemBuilder(Material.IRON_DOOR).setDisplay(C.C + "Quit menu.").build();
            }

            @Override
            public void onAction(AssiPlayer clicker, ClickType clickType) {
                punishUI.closeInventory(clicker.getBase());
            }
        });

        punishUI.open(player.getBase());

    }

    /**
     * Menu to build the current-punishment menu.
     *
     * @param player the player who's viewing the punishments.
     * @param target the player who's punishments we're viewing.
     * @param reason the reason for the unpunishing.
     */
    private void buildCurrentPunishments(AssiPlayer player, AssiPlayer target, String reason) {
        punishUI.removeAllButtons();

        punishUI.setInventoryTitle(C.V + target.getName() + " " + C.SS + C.II + "Current punishments");

        punishUI.addButton(53, new Button() {
            @Override
            public ItemStack getItemStack(AssiPlayer clicker) {
                return new ItemBuilder(Material.IRON_DOOR).setDisplay(C.C + "Quit menu.").build();
            }

            @Override
            public void onAction(AssiPlayer clicker, ClickType clickType) {
                punishUI.closeInventory(clicker.getBase());
            }
        });

        PunishProfile punishProfile = target.getPunishProfile();

        if (punishProfile == null) {
            punishUI.addButton(0, new Button() {
                @Override
                public ItemStack getItemStack(AssiPlayer clicker) {
                    return new ItemBuilder(Material.BARRIER).setDisplay(C.II + "Error getting punish profile.").build();
                }

                @Override
                public void onAction(AssiPlayer clicker, ClickType clickType) {
                }
            });
            return;
        }

        final List<PunishmentData> allNew = Lists.newArrayList();
        for (List<PunishmentData> punishmentDataList : punishProfile.getActivePunishments().values()) {
            allNew.addAll(punishmentDataList);
        }
        allNew.sort(new PunishSorter());

        for (PunishmentData punishmentData : allNew) {

            String length = (punishmentData.getPunishLength() != PunishmentCategory.TIME_INAPPLICABLE ?
                    (punishmentData.getPunishLength() == PunishmentCategory.TIME_PERM ? "forever!" :
                            TimeUnit.MILLISECONDS.toDays(punishmentData.getPunishLength()) + " days") : "n/a");

            String expires = punishmentData.getPunishExpiry() != PunishmentCategory.TIME_INAPPLICABLE ?
                    (punishmentData.getPunishExpiry() == PunishmentCategory.TIME_PERM ? "never!" :
                            UtilTime.formatTimeStamp(punishmentData.getPunishExpiry())) : "n/a";

            punishUI.addButton(new Button() {
                @Override
                public ItemStack getItemStack(AssiPlayer clicker) {
                    return new ItemBuilder(punishmentData.getPunishmentCategory().getDisplayMaterial())
                            .setDisplay(C.V + punishmentData.getPunishmentCategory().getDisplay())
                            .setLore(C.C, C.C + "Offence: " + C.V + punishmentData.getSeverity(),
                                    C.C + "Reason: " + C.V + punishmentData.getReason(),
                                    C.C + "Issued by: " + C.V + punishmentData.getPunisherDisplay(),
                                    C.C + "Outcome: " + C.V + punishmentData.getPunishmentType().toString(),
                                    (punishmentData.getIp() != null ? clicker.getRank().isHigherThanOrEqualTo(Rank.MOD) ? C.C +
                                            "Covered IP: " + C.V + punishmentData.getIp() : C.C + "Covered IP: " + C.V + "[censored]" : ""),
                                    C.C + "Issued: " + C.V + UtilTime.formatTimeStamp(punishmentData.getPunishIssued()),
                                    C.C + "Length: " + C.V + length,
                                    C.C + "Expires in: " + C.V + expires,
                                    C.C, C.II + "Right click" + C.C + " to remove.").build();
                }

                @Override
                public void onAction(AssiPlayer clicker, ClickType clickType) {
                    if (clickType != ClickType.RIGHT) return;

                    if (reason == null) {
                        player.getBase().playSound(player.getBase().getLocation(), Sound.ANVIL_LAND, 30L, 20L);
                        player.sendMessage(C.II + "You must provide a reason for unpunishing! " + C.C + "/punish <player> <reason>");
                        return;
                    }

                    punishUI.closeInventory(clicker.getBase());
                    plugin.getPunishmentManager().unpunish(player, target, punishmentData.getId(), reason);
                }
            });
        }

        punishUI.open(player.getBase());

    }

    /**
     * Menu to build the past-punishment menu.
     *
     * @param player the player who's viewing the old punishments.
     * @param target the player who's punishments we're viewing
     */
    private void buildPastPunishments(AssiPlayer player, AssiPlayer target) {
        punishUI.removeAllButtons();

        punishUI.setInventoryTitle(C.V + target.getName() + " " + C.SS + C.II + "Past punishments");

        punishUI.addButton(53, new Button() {
            @Override
            public ItemStack getItemStack(AssiPlayer clicker) {
                return new ItemBuilder(Material.IRON_DOOR).setDisplay(C.C + "Quit menu.").build();
            }

            @Override
            public void onAction(AssiPlayer clicker, ClickType clickType) {
                punishUI.closeInventory(clicker.getBase());
            }
        });

        PunishProfile punishProfile = target.getPunishProfile();

        if (punishProfile == null) {
            punishUI.addButton(0, new Button() {
                @Override
                public ItemStack getItemStack(AssiPlayer clicker) {
                    return new ItemBuilder(Material.BARRIER).setDisplay(C.II + "Error getting punish profile.").build();
                }

                @Override
                public void onAction(AssiPlayer clicker, ClickType clickType) {
                }
            });
            return;
        }

        final List<PunishmentData> allOld = Lists.newArrayList();
        for (List<PunishmentData> punishmentDataList : punishProfile.getOldPunishments().values()) {
            allOld.addAll(punishmentDataList);
        }
        allOld.sort(new PunishSorter());

        for (PunishmentData punishmentData : allOld) {

            punishUI.addButton(new Button() {
                @Override
                public ItemStack getItemStack(AssiPlayer clicker) {
                    long unpunishedTime = 0L;
                    String unpunishedBy = "n/a";
                    String unpunishedReason = "n/a";

                    UnpunishData unpunishData = punishmentData.getUnpunishData();
                    if (unpunishData != null) {
                        unpunishedTime = unpunishData.getUnpunishTime();
                        unpunishedBy = unpunishData.getUnpunisherDisplay();
                        unpunishedReason = unpunishData.getUnpunishReason();
                    }

                    String length = (punishmentData.getPunishLength() != PunishmentCategory.TIME_INAPPLICABLE ?
                            (punishmentData.getPunishLength() == PunishmentCategory.TIME_PERM ? "forever!" :
                                    TimeUnit.MILLISECONDS.toDays(punishmentData.getPunishLength()) + " days") : "n/a");

                    return new ItemBuilder(punishmentData.getPunishmentCategory().getDisplayMaterial())
                            .setDisplay(C.V + punishmentData.getPunishmentCategory().getDisplay())
                            .setLore(C.C, C.C + "Offence: " + C.V + punishmentData.getSeverity(),
                                    C.C + "Reason: " + C.V + punishmentData.getReason(),
                                    C.C + "Issued by: " + C.V + punishmentData.getPunisherDisplay(),
                                    C.C + "Outcome: " + C.V + punishmentData.getPunishmentType().toString(),
                                    (punishmentData.getIp() != null ? C.C + "Covered IP: " + C.V + (clicker.getRank().isHigherThanOrEqualTo(Rank.MOD) ?
                                            punishmentData.getIp() : "[censored]") : ""),
                                    C.C + "Issued: " + C.V + UtilTime.formatTimeStamp(punishmentData.getPunishIssued()),
                                    C.C + "Length: " + C.V + length,
                                    C.C, C.C + "Unpunished: " + C.V + (unpunishedTime == 0L ? "n/a" : UtilTime.formatTimeStamp(unpunishedTime)),
                                    C.C + "Unpunished by: " + C.V + unpunishedBy,
                                    C.C + "Unpunished reason: " + C.V + unpunishedReason).build();
                }

                @Override
                public void onAction(AssiPlayer clicker, ClickType clickType) {
                }
            });
        }

        punishUI.open(player.getBase());
    }


}
