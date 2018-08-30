package net.assimilationmc.ellie.assipunish.ui;

import net.assimilationmc.ellie.assicore.api.ItemBuilder;
import net.assimilationmc.ellie.assicore.api.ui.DynamicUI;
import net.assimilationmc.ellie.assicore.util.Util;
import net.assimilationmc.ellie.assipunish.AssiPunish;
import net.assimilationmc.ellie.assipunish.punish.PunishmentOffence;
import net.assimilationmc.ellie.assipunish.punish.data.DataPunishment;
import net.assimilationmc.ellie.assipunish.punish.data.PunishManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.List;

/**
 * Created by Ellie on 22/04/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CurrentPunishmentsMenu extends DynamicUI implements Listener {
    private String name;
    private PunishManager punishManager;
    private String reason;

    public CurrentPunishmentsMenu(AssiPunish assiPunish, Player player, String name, String display, String reason){
        super(54, "&c"+display+" &8➞ &cCurrent Punishments", player);
        this.name = name;
        this.punishManager = assiPunish.getPunishManager();
        this.reason= reason;
        Bukkit.getPluginManager().registerEvents(this, assiPunish);
        player.openInventory(this.build());
    }

    @Override
    public Inventory build() {
        List<DataPunishment> punishmentList = punishManager.getActivePunishments(name);

        Material material = Material.PAPER;
        int i = 0;
        for (DataPunishment dataPunishment : punishmentList) {
            PunishmentOffence offence = PunishmentOffence.getPunishInfo(dataPunishment.getType(), dataPunishment.getOffence());
            if (offence != null) {
                switch (offence.getPunishmentResult()) {
                    case WARN:
                        material = Material.FIREBALL;
                        break;
                    case MUTE:
                        material = Material.DIAMOND_AXE;
                        break;
                    case KICK:
                        material = Material.BONE;
                        break;
                    case BAN:
                        material = Material.PACKED_ICE;
                        break;
                    case IP_BAN:
                        material = Material.ARROW;
                        break;
                    case BAN_AND_IP:
                        material = Material.CACTUS;
                        break;
                    case BLACKLIST:
                        material = Material.ANVIL;
                        break;
                    case BAN_ALL:
                        material = Material.BARRIER;
                        break;
                }
            }
            addButton(i, (player, type) -> {
                if(type.equals(ClickType.RIGHT)){

                    if(reason == null){
                        closeInventory();
                        Util.mINFO(player, "&cYou must provide a reason for unpunishing!");
                        return;
                    }

                    closeInventory();
                    dataPunishment.setUnpunished_by(player.getName());
                    dataPunishment.setUnpunished_time(System.currentTimeMillis());
                    dataPunishment.setUnpunished_reason(reason);
                    punishManager.unPunish(dataPunishment, name, false);
                }

            }, new ItemBuilder(material).setDisplay("&f").
                    setLore("&9Punishment: &f" + (offence == null ? "Error" : offence.getPunishmentResult()),
                            "&9Type: &f" + dataPunishment.getType().toString(),
                            "&9Offence: &f" + Math.addExact(dataPunishment.getOffence(), 1),
                            "&9Custom-Reason: &f" + dataPunishment.getCustomReason(),
                            "&9Issued by: &f" + dataPunishment.getPunishedBy(),
                            "&9Issued: &f" + Util.formatDateDiff(dataPunishment.getIssued()) + " ago",
                            "&9Expiry: &f" + (dataPunishment.getExpire() != -1 && Util.isPast(dataPunishment.getExpire())
                                    ? Util.formatDateDiff(dataPunishment.getExpire()) + " ago"
                                    : (dataPunishment.getExpire() == -1 ? "n/a" : Util.getDuration(dataPunishment.getExpire()))),
                            "&f", "&cRight click to unpunish &lReason must be specified")
                    .build());
        }

        addButton(53, (player1, type) -> closeInventory(), new ItemBuilder(Material.WOOD_DOOR).setDisplay("&cExit").build());

        return super.build();
    }

    @EventHandler
    public void onClick(InventoryClickEvent e){
        handleClick(e);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e){
        handleClose(this, e);
    }
}
