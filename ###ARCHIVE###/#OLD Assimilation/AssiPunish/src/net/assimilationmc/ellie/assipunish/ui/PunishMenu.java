package net.assimilationmc.ellie.assipunish.ui;

import com.google.common.base.Joiner;
import net.assimilationmc.ellie.assicore.api.AssiPlayer;
import net.assimilationmc.ellie.assicore.api.ItemBuilder;
import net.assimilationmc.ellie.assicore.api.ui.DynamicUI;
import net.assimilationmc.ellie.assicore.manager.ModuleManager;
import net.assimilationmc.ellie.assicore.util.Util;
import net.assimilationmc.ellie.assipunish.AssiPunish;
import net.assimilationmc.ellie.assipunish.punish.Punishment;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by Ellie on 18/04/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class PunishMenu extends DynamicUI implements Listener {

    /*
        0123456789
    00  ---------  08
    09 |++++H++++| 18
    19 |+A B C D+| 27
    28 |+E F G H+| 36
    37 |+I J K L+| 45
    46 |+ STUFF +| 54
    55 |+++++++++| 63
        ---------
    */

    private String playerName;
    private String reason = null;
    private AssiPunish assiPunish;

    public PunishMenu(AssiPunish assiPunish, Player player, String name, String reason){
        super(54, "&c"+name, player,
                Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44,
                        45, 53), new ItemBuilder(Material.STAINED_GLASS_PANE).setDisplay("&f").setColor(ItemBuilder.StackColor.BLUE).build());

        this.playerName = name;
        this.reason = reason;
        this.assiPunish = assiPunish;
        Bukkit.getPluginManager().registerEvents(this, assiPunish);
        player.openInventory(this.build());
    }

    @Override
    public Inventory build() {

        UUID display = ModuleManager.getModuleManager().getPlayerManager().getPlayer(playerName);

        boolean ip = playerName.matches(Util.ipPattern.pattern());

        ItemBuilder headB = new ItemBuilder(Material.SKULL_ITEM).asPlayerHead(ip ? "Hack" : playerName).setDisplay("&c"+playerName);

        if(ip){
            headB.setLore("&9IP usages", "&f");
            ModuleManager.getModuleManager().getPlayerManager().getIPUsers(playerName).values().forEach(name -> headB.appendLore("&c"+name));
        }else{
            AssiPlayer assiPlayer = ModuleManager.getModuleManager().getPlayerManager().getPlayerData(display);
            headB.setLore("&9Previous names: &f"+ Joiner.on("&9, &f").join(assiPlayer.getPreviousNames()),
                    "&9Last IP: &f"+ (getPlayer().hasPermission(assiPunish.ipFilterPermission) ? assiPlayer.getIP() : "&cFILTERED"),
                    "&9Rank: &f"+assiPlayer.getPermissionsRank(),
                    "&9Last seen: &f"+(assiPlayer.isOnline() ? "online" : Util.formatDateDiff(assiPlayer.getLastSeen())+" ago"),
            "&f", "&cClick the papers below to see more punishment details");

            addButton(14, (player, type) ->{
                closeInventory();
                new PunishMenu(assiPunish, player, assiPlayer.getIP(), reason);
            }, new ItemBuilder(Material.ARROW).setDisplay("&cClick this to punish IP").build());

        }

        addButton(13, (player, type) -> {}, headB.build());

        List<Integer> bounds = Arrays.asList(20, 21, 22, 23, 24,
                                            29, 30, 31, 32, 33);

        Punishment[] punishments = Punishment.values();
        int index = 0;
        for (Integer bound : bounds) {
            Punishment punishment = punishments[index];
            addButton(bound, (player, type) -> {
                String a = display == null?playerName:display.toString();
                // on click
                if(punishment.equals(Punishment.OTHER) && reason == null) {
                    closeInventory();
                    Util.mINFO(player, "&cYou must provide a reason for punishment OTHER!");
                    return;
                }

                if(!AssiPunish.getAssiPunish().getPunishManager().hasActivePunishment(a, punishment)){
                    AssiPunish.getAssiPunish().getPunishManager().givePunishment(player.getName(), a, punishment, reason);
                }else{
                    Util.mINFO(player, "&cPlayer already has an active punishment of this! You need to unban/unmute them first!");
                }
                closeInventory();
            }, punishment.getItemStack());

            index++;
        }

        ItemStack currentPunishments = new ItemBuilder(Material.PAPER).setDisplay("&cCurrent Punishments").build();
        ItemStack history = new ItemBuilder(Material.PAPER).setDisplay("&cHistory").build();

        addButton(48, (player, type) -> {
            closeInventory();
            new CurrentPunishmentsMenu(assiPunish, player, display == null?playerName:display.toString(), playerName, reason);
        }, currentPunishments);

        addButton(50, (player, type) -> {
            closeInventory();
            new HistoryMenu(assiPunish, player, display == null?playerName:display.toString(), playerName);
        }, history);

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
