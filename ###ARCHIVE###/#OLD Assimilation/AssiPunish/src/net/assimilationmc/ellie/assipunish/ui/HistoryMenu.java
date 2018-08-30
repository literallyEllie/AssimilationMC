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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

/**
 * Created by Ellie on 22/04/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class HistoryMenu extends DynamicUI implements Listener {

    private String person;

    public HistoryMenu(AssiPunish assiPunish, Player player, String object, String display){
        super(54, "&c"+display+" &8➞ &cHistory", player);
        this.person = object;
        Bukkit.getPluginManager().registerEvents(this, assiPunish);
        player.openInventory(this.build());
    }

    @Override
    public Inventory build() {
        PunishManager punishManager = AssiPunish.getAssiPunish().getPunishManager();

        Material material = null;
        int i = 0;
        for (DataPunishment dataPunishment : punishManager.getPunishments(person)) {

            PunishmentOffence offence = PunishmentOffence.getPunishInfo(dataPunishment.getType(), dataPunishment.getOffence());
            if(offence != null) {
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
            }else material = Material.PAPER;

            addButton(i, (player, type) -> {
            }, new ItemBuilder(material).setDisplay("&f").
                    setLore("&9Punishment: &f"+(offence == null ?"Error":offence.getPunishmentResult()),
                            "&9Type: &f"+dataPunishment.getType().toString(),
                            "&9Offence: &f"+Math.addExact(dataPunishment.getOffence(), 1),
                            "&9Custom-Reason: &f"+dataPunishment.getCustomReason(),
                            "&9Issued by: &f"+dataPunishment.getPunishedBy(),
                            "&9Issued: &f"+ Util.formatDateDiff(dataPunishment.getIssued())+" ago",
                            "&9Expiry: &f"+(dataPunishment.getExpire() != -1 && Util.isPast(dataPunishment.getExpire())
                                    ? Util.formatDateDiff(dataPunishment.getExpire())+" ago"
                                    : (dataPunishment.getExpire() == -1 ? "n/a" : Util.getDuration(dataPunishment.getExpire()))),
                            "&f", "&9Unpunished by: &f"+dataPunishment.getUnpunishedBy(),
                            "&9Unpunished Time: &f"+(dataPunishment.getUnpunishedTime() == 0 ?"null":Util.formatDateDiff(dataPunishment.getUnpunishedTime())+" ago"))
                    .build());
            i++;
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
