package net.assimilationmc.assiuhc.drops;

import com.google.common.collect.Maps;
import net.assimilationmc.assicore.event.update.UpdateEvent;
import net.assimilationmc.assicore.event.update.UpdateType;
import net.assimilationmc.assicore.util.UtilRandom;
import net.assimilationmc.assiuhc.drops.loot.LootTier;
import net.assimilationmc.assiuhc.drops.packages.TierOneDropPackage;
import net.assimilationmc.assiuhc.drops.packages.TierThreeDropPackage;
import net.assimilationmc.assiuhc.drops.packages.TierTwoDropPackage;
import net.assimilationmc.assiuhc.game.UHCGame;
import net.assimilationmc.assiuhc.game.custom.CustomizationProperties;
import net.assimilationmc.gameapi.module.GameModule;
import net.assimilationmc.gameapi.module.ModuleActivePolicy;
import net.minecraft.server.v1_8_R3.TileEntity;
import net.minecraft.server.v1_8_R3.TileEntityChest;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.inventivetalent.particle.ParticleEffect;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class  DropManager extends GameModule {

    private Map<DropPackage, Double> dropPackages;
    private Map<Location, DropPackage> droppedPackages;
    private int particleTask;

    private FireworkEffect tierThreeEffect;

    public DropManager(UHCGame uhcGame) {
        super(uhcGame, "Drop Manager", ModuleActivePolicy.GAME);
    }

    @Override
    public void start() {
        this.dropPackages = Maps.newHashMap();
        this.droppedPackages = Maps.newHashMap();

        if (!(boolean) ((UHCGame) getAssiGame()).getProperty(CustomizationProperties.CRATES)) {
            return;
        }

        registerDropPackage(new TierOneDropPackage(((UHCGame) getAssiGame())));
        registerDropPackage(new TierTwoDropPackage(((UHCGame) getAssiGame())));
        registerDropPackage(new TierThreeDropPackage(((UHCGame) getAssiGame())));

        particleTask = Bukkit.getScheduler().runTaskTimer(getAssiGame().getPlugin(), () -> {
            if (droppedPackages.isEmpty()) return;

            for (Map.Entry<Location, DropPackage> locationDropPackageEntry : droppedPackages.entrySet()) {
                final Location loc = locationDropPackageEntry.getKey();
                final DropPackage droppedPackage = locationDropPackageEntry.getValue();

                if (droppedPackage.getTier() == LootTier.ONE) continue;

                if (droppedPackage.getTier() == LootTier.TWO) {
                    // SPARKLE
                    ParticleEffect.REDSTONE.send(Bukkit.getOnlinePlayers(), loc, 0, 1, 0, 3, 1);
                    ParticleEffect.REDSTONE.send(Bukkit.getOnlinePlayers(), loc, 0.5, 1, 0.5, 3, 1);
                }

                if (droppedPackage.getTier() == LootTier.THREE) {
                    // "BIG EFFECT"
                    ParticleEffect.SPELL.send(Bukkit.getOnlinePlayers(), loc, 0, 1, 0, 2, 2);
                    ParticleEffect.SPELL.send(Bukkit.getOnlinePlayers(), loc, 0.5, 1, 0.5, 2, 2);
                }

            }

        }, 20 * 60, 5).getTaskId();

        this.tierThreeEffect = FireworkEffect.builder().trail(true).flicker(true).withColor(Color.PURPLE)
                .withFade(Color.BLACK).with(FireworkEffect.Type.BALL_LARGE).build();

    }

    @Override
    public void end() {
        if (dropPackages != null) dropPackages.clear();
        if (droppedPackages != null) droppedPackages.clear();
        if (particleTask != 0)
            Bukkit.getScheduler().cancelTask(particleTask);
    }

    @EventHandler
    public void on(final UpdateEvent e) {
        if (e.getType() == UpdateType.THREE_MIN) {
            if (!drop()) return; // 5/10 chance
            final DropPackage dropPackage = UtilRandom.selectWeightedRandom(dropPackages);

            final List<Location> locs = getAssiGame().getGameMapManager().getSelectedWorld().getSpawns().entrySet().stream().
                    filter(stringSerializedLocationEntry -> stringSerializedLocationEntry.getKey().toUpperCase().startsWith("DP_")).map(stringSerializedLocationEntry ->
                    stringSerializedLocationEntry.getValue().toLocation()).collect(Collectors.toList());
            if (locs.isEmpty()) return;
            final Location location = locs.get(UtilRandom.randomNumber(locs.size()));
            dropPackage.drop(location);
            locs.clear();
            droppedPackages.remove(location);
            droppedPackages.put(location, dropPackage);
        }

    }

    @EventHandler
    public void on(final BlockBreakEvent e) {
        final Location location = e.getBlock().getLocation();
        droppedPackages.keySet().removeIf(droploc -> droploc.getBlockX() == location.getBlockX() &&
                droploc.getBlockY() == location.getBlockY() && droploc.getBlockZ() == location.getBlockZ());
    }

    @EventHandler
    public void on(final BlockPlaceEvent e) {
        final Block block = e.getBlock();
        final List<Location> locs = getAssiGame().getGameMapManager().getSelectedWorld().getSpawns().entrySet().stream().
                filter(stringSerializedLocationEntry -> stringSerializedLocationEntry.getKey().toUpperCase().startsWith("DP_")).map(stringSerializedLocationEntry ->
                stringSerializedLocationEntry.getValue().toLocation()).collect(Collectors.toList());

        for (Location loc : locs) {
            if (loc.getBlockX() == block.getX() && loc.getBlockZ() == block.getX()) {
                e.setCancelled(true);
                return;
            }
        }

    }

    public void registerDropPackage(DropPackage dropPackage) {
        dropPackages.put(dropPackage, dropPackage.getChance());
    }

    public void unregisterAllPackages() {
        dropPackages.clear();
    }

    private boolean drop() {
        return UtilRandom.randomNumber(10) >= 5;
    }

    public FireworkEffect getTierThreeEffect() {
        return tierThreeEffect;
    }

    public void setChestName(String name, Block block) {
        if (block.getType() != Material.CHEST) return;

        final CraftWorld world = (CraftWorld) block.getWorld();
        final TileEntity entity = world.getTileEntityAt(block.getX(), block.getY(), block.getZ());
        ((TileEntityChest) entity).a(name);
        entity.update();
    }

}
