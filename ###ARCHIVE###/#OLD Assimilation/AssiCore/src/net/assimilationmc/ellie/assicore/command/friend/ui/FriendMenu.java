package net.assimilationmc.ellie.assicore.command.friend.ui;

import net.assimilationmc.ellie.assicore.AssiPlugin;
import net.assimilationmc.ellie.assicore.api.ui.IButton;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 * Created by Ellie on 05/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class FriendMenu implements Listener {

    private AssiPlugin assiPlugin;
    private Player player;

    private HashMap<Integer, IButton> buttons = new HashMap<>();
    private Inventory inventory;
    private FriendPage currentPage;

    public FriendMenu(AssiPlugin assiPlugin, Player player){
        this.assiPlugin = assiPlugin;
        this.player = player;


        Bukkit.getPluginManager().registerEvents(this, assiPlugin);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e){
        if(player.equals(e.getPlayer())){
            unregisterListener();

            player = null;
            assiPlugin = null;
        }
    }

    public Inventory build(){


        buttons.clear();

        for(int i = 0; i < FriendPage.values().length; i++){

            FriendPage page = FriendPage.values()[i];

            ItemStack itemStack = page == currentPage ? null : page.getItemStack(); //// TODO: 16/12/2016  

            this.addButton(i, itemStack, (player, type) -> {
                currentPage = page;
                build();
            });

        }

        return inventory;
    }


    private void unregisterListener(){
        HandlerList.unregisterAll(this);
    }

    private void addButton(int slot, ItemStack item, IButton button) {
        inventory.setItem(slot, item);
        buttons.put(slot, button);
    }



}
