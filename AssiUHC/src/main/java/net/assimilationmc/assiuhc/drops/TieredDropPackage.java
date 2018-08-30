package net.assimilationmc.assiuhc.drops;

import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilRandom;
import net.assimilationmc.assicore.util.UtilServer;
import net.assimilationmc.assiuhc.drops.loot.LootTier;
import net.assimilationmc.assiuhc.game.UHCGame;
import net.assimilationmc.gameapi.util.GC;
import org.bukkit.ChatColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

public abstract class TieredDropPackage extends DropPackage implements Listener {

    public TieredDropPackage(UHCGame game, String name, LootTier tier, double chance) {
        super(game, name, tier, chance);
    }

    @Override
    public void drop(Location location) {
        Block block = location.getBlock();
        if (block != null && block.getType() != Material.AIR) {
            block.setType(Material.AIR);
        }

        final Location up50 = location.clone();
        up50.setY(location.getY() + 5/*0*/);
        location.getWorld().spawnFallingBlock(up50, Material.CHEST, (byte) 0);

        getGame().getPlugin().registerListener(new Listener() {
            @EventHandler
            public void on(final EntityChangeBlockEvent e) {
                final Block nyBlock = e.getBlock();
                if (nyBlock.getLocation().getBlockX() != location.getBlockX() && nyBlock.getLocation().getBlockZ() != location.getBlockZ()
                        && nyBlock.getLocation().getBlockY() != location.getBlockY())
                    return;

                nyBlock.setType(Material.CHEST);

                ItemStack[] population = populate();

                final BlockState blockState = nyBlock.getState();
                final Inventory inventory = ((Chest) blockState).getBlockInventory();
                inventory.clear();

                getGame().getDropManager().setChestName(GC.C + ChatColor.BOLD + "A " + getName(), nyBlock);

                for (ItemStack itemStack : population) {
                    inventory.setItem(UtilRandom.randomNumber(inventory.getSize()), itemStack);
                }

                playDropEffect(location);

                UtilServer.broadcast(C.SS + GC.II + "A " + GC.V + ChatColor.BOLD + getName() + GC.II + " has spawned at " + GC.V +
                        location.getX() + GC.II + ", " + GC.V + location.getY() + GC.II + ", " + GC.V + location.getZ() + GC.II + "!");
                HandlerList.unregisterAll(this);
            }
        });
    }

    @Override
    public void playDropEffect(Location location) {
        final FireworkEffect fireworkEffect = getGame().getDropManager().getTierThreeEffect();
        final Firework firework = location.getWorld().spawn(location, Firework.class);
        final FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.clearEffects();
        fireworkMeta.addEffect(fireworkEffect);
        firework.setFireworkMeta(fireworkMeta);
    }

}
