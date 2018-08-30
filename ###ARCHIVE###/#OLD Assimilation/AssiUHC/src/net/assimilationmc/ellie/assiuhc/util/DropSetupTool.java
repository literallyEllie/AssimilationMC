package net.assimilationmc.ellie.assiuhc.util;

import com.google.common.base.Joiner;
import net.assimilationmc.ellie.assicore.api.ItemBuilder;
import net.assimilationmc.ellie.assicore.event.AssiChatEvent;
import net.assimilationmc.ellie.assicore.util.Util;
import net.assimilationmc.ellie.assicore.util.UtilPlayer;
import net.assimilationmc.ellie.assiuhc.UHC;
import net.assimilationmc.ellie.assiuhc.backend.DropItemManager;
import net.assimilationmc.ellie.assiuhc.backend.IUHCDropPackage;
import net.assimilationmc.ellie.assiuhc.command.admin.CmdDrops;
import net.assimilationmc.ellie.assiuhc.game.DropPackageRegular;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 * Created by Ellie on 2.8.17 for AssimilationMC.
 * <p>
 * Copyright 2017 Ellie
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class DropSetupTool implements Listener {

    private HashMap<Integer, IUHCDropPackage> drops;
    private DropItemManager dropItemManager;
    private CmdDrops cmdDrops;
    private String map;

    private int currentId;
    private ItemStack setter;
    private Player player;

    private IUHCDropPackage current;
    private Location unset_currentLoc;

    public DropSetupTool(DropItemManager dropItemManager, CmdDrops drops, String map, Player player) {
        this.dropItemManager = dropItemManager;
        this.cmdDrops = drops;
        this.map = map;
        this.currentId = 0;
        this.player = player;
        this.setter = new ItemBuilder(Material.CHEST).setDisplay("&cDrop Setup tool").build();
        Bukkit.getPluginManager().registerEvents(this, UHC.getPlugin(UHC.class));
        player.getInventory().addItem(setter);
        UtilPlayer.mINFO_noP(player, UColorChart.R + "Place the chest where you want to create a drop.");
        UtilPlayer.mINFO_noP(player, UColorChart.R + "Current id: " + UColorChart.VARIABLE + currentId);
        UtilPlayer.mINFO_noP(player, UColorChart.R + "Type '" + UColorChart.VARIABLE + "next" + UColorChart.R + "' to go to the next drop.");
        UtilPlayer.mINFO_noP(player, UColorChart.R + "Type '" + UColorChart.VARIABLE + "done" + UColorChart.R + "' to finish.");
        this.drops = new HashMap<>();
    }

    @EventHandler
    public void onInteract(BlockPlaceEvent e) {
        if (e.getPlayer() != player || e.getItemInHand() == null && !e.getItemInHand().equals(setter)
                || e.getBlock() == null) return;
        e.setCancelled(true);
        player.sendMessage(Util.color(UColorChart.R + "Added (" + UColorChart.VARIABLE + currentId + UColorChart.R + ")."));
        newDropLocation(e.getBlock().getLocation());
    }

    private void newDropLocation(Location location) {
        unset_currentLoc = location;
        UtilPlayer.mINFO_noP(player, UColorChart.R + "Now type the type of drop you want: " + UColorChart.VARIABLE
                + Joiner.on(UColorChart.R + ", " + UColorChart.VARIABLE).join(dropItemManager.getDropOptions().keySet()));
    }

    public int getCurrentId() {
        return currentId;
    }

    private void nextId() {
        this.currentId = currentId + 1;
    }

    @EventHandler
    public void onChat(AssiChatEvent e) {
        if (e.getPlayer() != player) return;

        String message = e.getMessage().trim();
        if (message.equalsIgnoreCase("next")) {
            nextId();
            e.setCancelled(true);
            return;
        }

        if (message.equalsIgnoreCase("done")) {
            HandlerList.unregisterAll(this);
            e.setCancelled(true);
            e.getPlayer().getBase().getInventory().remove(setter);
            setter = null;
            cmdDrops.getDropSetups().get(map).run(this);
            return;
        }

        if (unset_currentLoc != null) {

            if (current == null) {
                e.setCancelled(true);
                String type = message.toUpperCase();

                if (!dropItemManager.getDropOptions().containsKey(type)) {
                    UtilPlayer.mINFO_noP(player, UColorChart.R + "Invalid type.");
                    return;
                }

                switch (type) {
                    case "REGULAR":
                        current = new DropPackageRegular(unset_currentLoc, dropItemManager.getDropOptions().get(type));
                        break;
                }
                UtilPlayer.mINFO_noP(player, UColorChart.R + "Type '" + UColorChart.VARIABLE + "next" + UColorChart.R + "' to go to the next drop.");
                this.drops.put(currentId, current);
                unset_currentLoc = null;
                return;
            }
            UtilPlayer.mINFO_noP(player, UColorChart.R + "Type '" + UColorChart.VARIABLE + "next" + UColorChart.R + "' to go to the next drop.");
        }

    }

    public HashMap<Integer, IUHCDropPackage> getSavedDrops() {
        return drops;
    }

}
