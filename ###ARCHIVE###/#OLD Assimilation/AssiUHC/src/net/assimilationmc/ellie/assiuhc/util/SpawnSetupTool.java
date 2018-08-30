package net.assimilationmc.ellie.assiuhc.util;

import net.assimilationmc.ellie.assicore.api.ItemBuilder;
import net.assimilationmc.ellie.assicore.api.SerializableLocation;
import net.assimilationmc.ellie.assicore.util.Util;
import net.assimilationmc.ellie.assiuhc.UHC;
import net.assimilationmc.ellie.assiuhc.command.admin.CmdSpawns;
import net.assimilationmc.ellie.assiuhc.game.UHCSpawn;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Ellie on 10/05/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class SpawnSetupTool implements Listener {

    private HashMap<Integer, List<UHCSpawn>> spawns;

    private CmdSpawns cmdSpawns;
    private Player player;
    private int group;
    private int currentId;
    private ItemStack itemStack;
    private String map;

    public SpawnSetupTool(CmdSpawns cmdSpawns, String map, Player player) {
        this.player = player;
        this.cmdSpawns = cmdSpawns;
        this.spawns = new HashMap<>();
        this.group = 0;
        this.currentId = 0;
        this.map = map;
        Bukkit.getPluginManager().registerEvents(this, UHC.getPlugin(UHC.class));
        itemStack = new ItemBuilder(Material.ENDER_PORTAL_FRAME).setDisplay("&cSpawn placer").build();
        player.getInventory().addItem(itemStack);
        player.sendMessage(Util.color(UColorChart.R+"Place the spawn where you want."));
        player.sendMessage(Util.color(UColorChart.R+"Group " + UColorChart.VARIABLE + group + UColorChart.R + " Current Id: " + UColorChart.VARIABLE + currentId + UColorChart.R+"."));
        player.sendMessage(Util.color(UColorChart.R+"Type '"+UColorChart.VARIABLE+"next"+UColorChart.R+"' to move onto the next team."));
        player.sendMessage(Util.color(UColorChart.R+"Type '"+UColorChart.VARIABLE+"done"+UColorChart.R+"' to finish."));
    }

    @EventHandler
    public void onInteract(BlockPlaceEvent e) {
        if (e.getPlayer() != player || e.getItemInHand() == null && !e.getItemInHand().equals(itemStack)
                || e.getBlock() == null) return;
        e.setCancelled(true);
        player.sendMessage(Util.color(UColorChart.R+"Added ("+UColorChart.VARIABLE+ currentId + UColorChart.R+")."));
        addNextSpawn(e.getBlock().getLocation());
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (e.getPlayer() != player) return;

        String message = e.getMessage();
        if (message.equalsIgnoreCase("next")) {
            nextGroup();
            e.setCancelled(true);
            return;
        }

        if (message.equalsIgnoreCase("done")) {
            HandlerList.unregisterAll(this);
            e.setCancelled(true);
            e.getPlayer().getInventory().remove(itemStack);
            itemStack = null;
            cmdSpawns.getSpawnSetups().get(map).run(this);
        }
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent e) {
        if (e.getPlayer() == player) {
            HandlerList.unregisterAll(this);
            e.getPlayer().getInventory().remove(itemStack);
            cmdSpawns.getSpawnSetups().remove(map);
            itemStack = null;
        }
    }

    private void nextGroup() {
        group = group + 1;
        this.currentId = 0;
    }

    private void nextId() {
        this.currentId = currentId + 1;
    }

    public int getGroup() {
        return group;
    }

    public int getCurrentId() {
        return currentId;
    }

    private void addNextSpawn(Location location) {
        List<UHCSpawn> spawns = this.spawns.containsKey(group) ? this.spawns.get(group) : new ArrayList<>();
        UHCSpawn spawn = new UHCSpawn(new SerializableLocation(location));
        spawn.setGroupId(group);
        spawn.setId(currentId);
        spawns.add(spawn);
        this.spawns.put(group, spawns);
        nextId();
    }

    public HashMap<Integer, List<UHCSpawn>> getSpawns() {
        return spawns;
    }

}
