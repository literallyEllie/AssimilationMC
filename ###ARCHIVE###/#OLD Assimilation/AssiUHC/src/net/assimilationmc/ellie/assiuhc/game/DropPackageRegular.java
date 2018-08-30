package net.assimilationmc.ellie.assiuhc.game;

import net.assimilationmc.ellie.assicore.manager.ModuleManager;
import net.assimilationmc.ellie.assicore.util.WeightedRandom;
import net.assimilationmc.ellie.assiuhc.UHC;
import net.assimilationmc.ellie.assiuhc.backend.IUHCDropPackage;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

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
public class DropPackageRegular implements IUHCDropPackage, Listener {

    private Location location;
    private HashMap<ItemStack, Double> items; // itemstack chance

    public DropPackageRegular(Location location, HashMap<ItemStack, Double> items) {
        this.location = location;
        this.items = items;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void drop() {

        Location up50 = location;
        Block block = blockAt(location);
        if(block != null && block.getType() != Material.AIR){
            block.setType(Material.AIR);
        }
        ModuleManager.getModuleManager().getUtilManager().getReserver().setReserved(up50);

        up50.setY((location.getY() + 50));

        if (blockAt(up50) != null && blockAt(up50).getType() != Material.AIR) {
            throw new IllegalArgumentException("50 Blocks up from DropPackage is not an empty space! " + up50);
        }

        final FallingBlock b = location.getWorld().spawnFallingBlock(up50, Material.CHEST, (byte) 0); // ignore "magic value"

        Bukkit.getPluginManager().registerEvents(this, UHC.getPlugin(UHC.class));
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onBlockChange(EntityChangeBlockEvent e) {
                if(e.getTo().equals(Material.CHEST)) {
                    supplyDropParticle(b.getLocation());
                    Block block = e.getBlock();
                    block.setType(e.getTo());
                    fill(block);
                    ModuleManager.getModuleManager().getUtilManager().getReserver().unReserve(b.getLocation());
                }
            }
        }, UHC.getPlugin(UHC.class));

    }


    @Override
    public void setItems(HashMap<ItemStack, Double> items) {
        this.items = items;
    }

    private Block blockAt(Location location){
        if(location == null) return null;
        return location.getWorld().getBlockAt(location);
    }

    @Override
    public void fill(Block block) {
        BlockState state = block.getState();
        if(!(state instanceof Chest)) return;

        Inventory inv = ((Chest) state).getBlockInventory();

        final Random random = new Random();
        final int chestItems = random.nextInt((10-7)+7);

        Iterator<Map.Entry<ItemStack, Double>> it = items.entrySet().iterator();

        int done = -1;

        WeightedRandom<ItemStack> weightedRandom = new WeightedRandom<>();

        while(it.hasNext() && done != chestItems) {
            final int chestIndex = random.nextInt(inv.getSize());
            inv.setItem(chestIndex, weightedRandom.getWeightedItem(items).getKey());
            done++;
        }
        System.out.println("FILLED");
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public HashMap<ItemStack, Double> getItems() {
        return items;
    }

    @Override
    public String getType() {
        return "REGULAR";
    }

    public void supplyDropParticle(Location l){

        Location loc = l.clone().add(0, 50, 0);
        new BukkitRunnable() {
            int y = 0;
            public void run() {

                y++;

                for (Player player : l.getWorld().getPlayers()){

                    for (Location l : getCircle(loc.add(0, -1, 0), 1.5, 20)){
                        particle(player, EnumParticle.VILLAGER_HAPPY, l.getX(), l.getY(), l.getZ(), 0, 0, 0, 1, 30);
                    }

                    if (loc.getBlockY() == l.getBlockY() || loc.getBlockY() < l.getBlockY()){
                        particle(player, EnumParticle.FIREWORKS_SPARK, l.getX(), l.getY(), l.getZ(), 0, 0, 0, (float) 0.2, 30);
                        particle(player, EnumParticle.SMOKE_NORMAL, l.getX(), l.getY(), l.getZ(), 0, 0, 0, (float) 0.2, 30);
                    }

                }

                if (loc.getBlockY() == l.getBlockY() || loc.getBlockY() < l.getBlockY()){
                    l.getWorld().playEffect(l, Effect.STEP_SOUND, Material.ENDER_CHEST);
                    l.getWorld().strikeLightningEffect(l);
                    this.cancel();
                }


            }
        }.runTaskTimer(UHC.getPlugin(UHC.class), 0, 0);

    }

    /// TODO MOVE THIS

    private void particle(Player player, EnumParticle particle, double x, double y, double z, float xoffset,
                         float yoffset, float zoffset, float speed, int amount) {

        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(particle, // particle
                // type.
                true, // true
                (float) x, // x coordinate
                (float) y, // y coordinate
                (float) z, // z coordinate
                xoffset, // x offset
                yoffset, // y offset
                zoffset, // z offset
                speed, // speed
                amount, // number of particles
                null);

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private ArrayList<Location> getCircle(Location center, double radius, int amount) {
        World world = center.getWorld();
        double increment = 6.283185307179586D / amount;
        ArrayList<Location> locations = new ArrayList();
        for (int i = 0; i < amount; i++) {
            double angle = i * increment;
            double x = center.getX() + radius * Math.cos(angle);
            double z = center.getZ() + radius * Math.sin(angle);
            locations.add(new Location(world, x, center.getY(), z));
        }
        return locations;
    }

}
