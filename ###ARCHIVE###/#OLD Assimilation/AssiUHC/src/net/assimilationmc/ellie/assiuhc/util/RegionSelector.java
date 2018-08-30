package net.assimilationmc.ellie.assiuhc.util;

import net.assimilationmc.ellie.assicore.api.AssiRegion;
import net.assimilationmc.ellie.assicore.api.ItemBuilder;
import net.assimilationmc.ellie.assicore.util.Util;
import net.assimilationmc.ellie.assiuhc.UHC;
import net.assimilationmc.ellie.assiuhc.command.admin.CmdCreate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Ellie on 20/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class RegionSelector implements Listener {

    private CmdCreate cmdCreate;
    private String holder;
    private String map;
    private ItemStack itemStack;

    private Location loc1;
    private Location loc2;
    private AssiRegion region;

    public RegionSelector(CmdCreate cmdCreate, String map, String holder){
        this.cmdCreate = cmdCreate;
        this.holder = holder;
        this.map = map;
        itemStack = new ItemBuilder(Material.IRON_INGOT).setDisplay("&cRegion Selector").appendLore(holder).appendLore("\n&fLeft click for pos1. Right click for pos2\n").build();
        Bukkit.getPluginManager().registerEvents(this, UHC.getPlugin(UHC.class));

        Bukkit.getPlayer(holder).getInventory().addItem(itemStack);
        Bukkit.getPlayer(holder).sendMessage(Util.color(UColorChart.R+"The region selector in the form of an Iron Ingot has been added to your inventory"));
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        if(!e.getPlayer().getName().equals(holder))
            return;

        if(!e.getItem().equals(itemStack))
            return;

        if(e.getAction() == Action.LEFT_CLICK_BLOCK){
            loc1 = e.getClickedBlock().getLocation();
            e.setCancelled(true);
        }

        if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
            loc2 = e.getClickedBlock().getLocation();
            e.setCancelled(true);
        }

        if(waiting()){
            Bukkit.getPlayer(holder).sendMessage(Util.color(UColorChart.R+"Type anything in chat to finish"));
        }
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent e){
        if(e.getPlayer().getName().equals(holder)){
            cmdCreate.editors.remove(map);
            e.getPlayer().getInventory().remove(itemStack);
            unregister();
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e){
        if(e.getPlayer().getName().equals(holder)) {
            e.setCancelled(true);

            region = new AssiRegion(loc1, loc2);
            unregister();
            cmdCreate.setFinished(map, this);
            e.getPlayer().getInventory().remove(itemStack);
        }
    }

    private void unregister(){
        HandlerList.unregisterAll(this);
    }

    public String getHolder() {
        return holder;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public AssiRegion getRegion() {
        return region;
    }

    public boolean waiting(){
        return loc1 != null && loc2 != null;
    }

}
