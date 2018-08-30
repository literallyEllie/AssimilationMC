package net.assimilationmc.pvplobbyadaptor;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.achievement.AchievementCategory;
import net.assimilationmc.assicore.combat.CombatLogEvent;
import net.assimilationmc.assicore.combat.CombatLogger;
import net.assimilationmc.assicore.lobby.LobbyScorePolicy;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.*;
import net.assimilationmc.assicore.world.WorldData;
import net.assimilationmc.pvplobbyadaptor.stats.PVPPlayer;
import net.assimilationmc.pvplobbyadaptor.stats.PVPStatsProvider;
import net.minecraft.server.v1_8_R3.TileEntity;
import net.minecraft.server.v1_8_R3.TileEntityChest;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftItem;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PVPLobbyAdaptor extends JavaPlugin implements Listener {

    private final String WORLD = "PvPArena";
    private AssiPlugin assiPlugin;
    private PVPStatsProvider pvpStatsProvider;
    private CombatLogger combatLogger;
    private Location spawn;
    private Map<Block, Hologram> deathHolograms;
    private Map<Block, /*Pair<*/Integer/*, Material>*/> blockCleanup;
    private ItemStack goldenHead;
    private Map<Block, Material> blockRegen;

    private Map<UUID, Integer> killCount;

    private AchieveTenStreakWarmup achieveTenStreakWarmup;
    private List<String> disableCommand = Lists.newArrayList("speed", "wspeed", "fly", "cosmetic", "cosmetics", "perks");

    @Override
    public void onEnable() {
        assiPlugin = (AssiPlugin) getServer().getPluginManager().getPlugin("AssiCore");
        if (!assiPlugin.getServerData().isLobby()) {
            return;
        }

        File copyOver = new File("PvPArena-Copy");
        if (!copyOver.exists()) {
            getLogger().severe("There must be a copy world for this plugin to operate.");
            return;
        }

        try {
            FileUtils.copyDirectory(copyOver, new File(WORLD));
        } catch (IOException e) {
            getLogger().severe("FAILED TO COPY OVER MAP");
            e.printStackTrace();
        }

        pvpStatsProvider = new PVPStatsProvider(assiPlugin);
        combatLogger = new CombatLogger(assiPlugin, 15);

        // Load
        Bukkit.createWorld(new WorldCreator(WORLD));
        final World world = Bukkit.getWorld(WORLD);
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setGameRuleValue("doMobLoot", "false");
        world.setGameRuleValue("doMobSpawning", "false");
        world.setGameRuleValue("doEntityDrops", "false");
        world.setGameRuleValue("naturalRegeneration", "true");
        world.setAutoSave(false);

        // Scoreboard
        assiPlugin.getScoreboardManager().setScoreboardPolicy(new LobbyScorePolicy(assiPlugin) {

            @Override
            public List<String> getSideBar(AssiPlayer player) {
                if (!player.getBase().getWorld().getName().equals(WORLD)) return super.getSideBar(player);
                final ArrayList<String> lines = Lists.newArrayList();
                PVPPlayer pvpPlayer = pvpStatsProvider.getPlayer(player.getBase());

                lines.add(empty(0));
                lines.add(C.C + "You are playing:");
                lines.add(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Warmup");
                lines.add(empty(1));

                lines.add(C.C + "Level: " + C.V + pvpPlayer.getLevel());
                lines.add(C.C + "XP: " + C.V + pvpPlayer.getXp());
                lines.add(C.C + "Kills: " + C.V + pvpPlayer.getKills());
                lines.add(C.C + "Deaths: " + C.V + pvpPlayer.getDeaths());
                lines.add(C.C + "K/D: " + C.V + UtilMath.trim(pvpPlayer.kd()));

                lines.add(empty(2));
                lines.add(C.C + "Players: " + C.V + Bukkit.getWorld(WORLD).getPlayers().size());
                lines.add(empty(3));

                lines.add(C.V + "assimilationmc.net");

                return lines;
            }
        });

        assiPlugin.getJoinItemManager().addItem(new PVPTeleportItem(this));

        this.spawn = new Location(Bukkit.getWorld(WORLD), 16, 40, 15);
        this.deathHolograms = Maps.newHashMap();
        this.blockCleanup = Maps.newHashMap();
        this.blockRegen = Maps.newHashMap();

        Bukkit.getPluginManager().registerEvents(this, this);

        // Water damage and boarder
        getServer().getScheduler().runTaskTimer(this, () -> {

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.getWorld().getName().equals(WORLD) || player.getGameMode() == GameMode.CREATIVE) continue;
                final Block block = player.getPlayer().getLocation().getBlock();
                if (block.getBiome() == Biome.SWAMPLAND) {
                    player.sendMessage(C.II + ChatColor.BOLD.toString() + "Turn back before you are annihilated!");
                    if (handleDamage(player, 3, true)) player.damage(3);

                }
            }

        }, 20 * 30, 20);


        goldenHead = new ItemBuilder(Material.SKULL_ITEM).setDurability((byte) 3)
                .setDisplay(ChatColor.GOLD + ChatColor.BOLD.toString() + "Golden Head")
                .setLore("", C.C + "Right-click this to active special powers")
                .build();

        // make not stackable
        final net.minecraft.server.v1_8_R3.ItemStack nmsCopy = CraftItemStack.asNMSCopy(goldenHead);
        nmsCopy.getItem().c(1);
        goldenHead = CraftItemStack.asBukkitCopy(nmsCopy);

        ShapedRecipe shapedRecipe = new ShapedRecipe(goldenHead);
        shapedRecipe.shape("GGG",
                "GHG",
                "GGG");

        shapedRecipe.setIngredient('G', new MaterialData(Material.GOLD_INGOT));
        shapedRecipe.setIngredient('H', new MaterialData(Material.SKULL_ITEM, (byte) 3));
        getServer().addRecipe(shapedRecipe);

        this.killCount = Maps.newHashMap();

        this.achieveTenStreakWarmup = new AchieveTenStreakWarmup(this);
        assiPlugin.getAchievementManager().unregisterAchievement(AchievementCategory.GAME_PLAY, achieveTenStreakWarmup.getId());
        assiPlugin.getAchievementManager().addAchievement(achieveTenStreakWarmup);

        getServer().dispatchCommand(getServer().getConsoleSender(), "hd reload");

    }

    @Override
    public void onDisable() {

        deathHolograms.values().forEach(Hologram::delete);
        deathHolograms.clear();

//        blockCleanup.forEach((block, mat) -> block.setType(mat.getValue()));
        //blockCleanup.keySet().forEach(block -> {
          //  D.d("block " + block.getType() + " to air");
            //block.setType(Material.AIR);
        //});
        blockCleanup.clear();

        //blockRegen.forEach(Block::setType);
        blockCleanup.clear();

        Bukkit.unloadWorld(WORLD, false);

        try {
            FileUtils.deleteDirectory(new File(WORLD));
        } catch (IOException e) {
            getLogger().severe("FAILED TO DELETE WORLD.");
            e.printStackTrace();
        }

        killCount.clear();
    }

    public AssiPlugin getAssiPlugin() {
        return assiPlugin;
    }

    @EventHandler
    public void on(final PlayerTeleportEvent e) {
        if (e.getFrom().getWorld().getName().equals(e.getTo().getWorld().getName())) return;
        if (!e.getTo().getWorld().getName().equals(WORLD)) {
            resetKillStreak(e.getPlayer());
            return;
        }

        final Player player = e.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) return;
        kit(player);

        if (getAssiPlugin().getPlayerManager().getPlayer(player).isVanished()) {
            player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "!!!YOU ARE IN VANISH!!!");
        }

        PVPPlayer pvpPlayer = getPvpStatsProvider().getPlayer(player);
        if (pvpPlayer.getKills() == 0) {
            player.sendMessage(C.C);
            player.sendMessage(C.C);
            player.sendMessage(C.II + "            You are now playing " + ChatColor.YELLOW + ChatColor.BOLD.toString() + "Warmup" + C.II + "!");
            player.sendMessage(C.C);
            player.sendMessage(C.C + "You leave any time with " + C.V + "/spawn");
            player.sendMessage(C.C + "Info:");
            player.sendMessage(C.C + ChatColor.ITALIC + "1) " + C.V + "You can break " + ChatColor.YELLOW + "Wheat" + C.V + ", " + ChatColor.AQUA + "Ores" + C.V + ", " +
                    ChatColor.GOLD + "Trees" + C.V + " and " + ChatColor.GREEN + "Leaves");
            player.sendMessage(C.C + ChatColor.ITALIC + "2) " + C.V + "When you die your items and your head are placed in a chest which lasts for 1 minute.");
            player.sendMessage(C.C + ChatColor.ITALIC + "3) " + C.V + "If you go into the swamp, you'll probably die.");
            player.sendMessage(C.C + ChatColor.ITALIC + "4) " + C.V + "With a head, you can make a golden head.");
            player.sendMessage(C.C + ChatColor.ITALIC + "5) " + C.V + "Terrain is re-generating and all blocks you place are deleted after " + C.V + "45 minutes.");
            player.sendMessage(C.C);

        }

    }

    @EventHandler
    public void on(final BlockBreakEvent e) {
        final Player player = e.getPlayer();
        if (!player.getWorld().getName().equals(WORLD)) return;
        if (player.getGameMode().equals(GameMode.CREATIVE)) return;

        e.setCancelled(true);

        final Block block = e.getBlock();
        final PlayerInventory inventory = player.getInventory();

        if (blockCleanup.containsKey(block)) {
            getServer().getScheduler().cancelTask(blockCleanup.get(block));
            blockCleanup.remove(block);
            inventory.addItem(new ItemStack(block.getType()));
            block.setType(Material.AIR);
            return;
        }

        switch (block.getType()) {
            case CROPS:
                inventory.addItem(new ItemStack(Material.BREAD));
                break;
            case LOG:
                inventory.addItem(new ItemStack(Material.WOOD, 4));
                break;
            case LEAVES:

                if (new Random().nextInt(2) == 0) {
                    inventory.addItem(new ItemStack(Material.APPLE));
                }
                break;

            case STONE:
            case COBBLESTONE:
                inventory.addItem(new ItemStack(Material.COBBLESTONE, 16));
                break;
            case COAL_BLOCK:
                inventory.addItem(new ItemStack(Material.COAL_BLOCK));
                break;
            case COAL_ORE:
                inventory.addItem(new ItemStack(Material.COAL));
                break;
            case IRON_ORE:
                inventory.addItem(new ItemStack(Material.IRON_INGOT));
                break;
            case GOLD_ORE:
                inventory.addItem(new ItemStack(Material.GOLD_INGOT));
                break;
            case DIAMOND_ORE:
                inventory.addItem(new ItemStack(Material.DIAMOND));
                break;

        }

        if (block.getType().name().contains("ORE") || (block.getType() == Material.STONE && block.getData() == 0) || block.getType() == Material.COBBLESTONE
                || block.getType() == Material.COAL_BLOCK
                || block.getType() == Material.LOG) {

            blockRegen.put(block, block.getType());
            getServer().getScheduler().runTaskLater(this, () -> {
                block.setType(blockRegen.get(block));
                blockRegen.remove(block);
            }, 20 * 30);

            player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 3f, 0.5f);
            block.setType(Material.AIR);
        }

    }

    @EventHandler
    public void on(final BlockPlaceEvent e) {
        final Player player = e.getPlayer();
        final Block blockPlaced = e.getBlockPlaced();

        if (!player.getWorld().getName().equals(WORLD)) return;
        if (player.getGameMode().equals(GameMode.CREATIVE)) return;

        if (blockPlaced.getLocation().getBlockY() >= 25) {
            e.setCancelled(true);
            player.sendMessage(C.II + "You cannot build any higher.");
            return;
        }

        if (blockPlaced.getType() == Material.SKULL) {
            e.setCancelled(true);
            return;
        }

        if (blockPlaced.getRelative(BlockFace.DOWN) != null &&
                blockPlaced.getRelative(BlockFace.DOWN).getType() == Material.SOIL) {
            e.setCancelled(true);
            return;
        }

        if (blockCleanup.containsKey(blockPlaced)) {
            e.setCancelled(true);
            return;
        }

        WorldData worldData = assiPlugin.getWorldManager().getWorldData(WORLD);
        if (worldData.getRegions().containsKey("spawn")) {
            if (worldData.getRegions().get("spawn").intersects(blockPlaced.getLocation(), true)) {
                e.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You cannot build here.");
                return;
            }
        }

        blockCleanup.put(blockPlaced, getServer().getScheduler().runTaskLater(this, () -> blockPlaced.setType(Material.AIR), (45 * 60) * 20).getTaskId());
    }

    @EventHandler
    public void on(final PlayerBucketEmptyEvent e) {
        final Block blockClicked = e.getBlockClicked().getRelative(e.getBlockFace());
        blockCleanup.put(blockClicked, getServer().getScheduler().runTaskLater(this, () -> blockClicked.setType(Material.AIR), (45 * 60) * 20).getTaskId());
    }

    @EventHandler
    public void on(final PlayerBucketFillEvent e) {
        final Player player = e.getPlayer();

        final Block blockClicked = e.getBlockClicked();
        if (blockClicked == null) return;

        if (blockClicked.getType().name().contains("LAVA")) {
            e.setCancelled(true);
            player.setItemInHand(new ItemStack(Material.LAVA_BUCKET));
        } else if (blockClicked.getType().name().contains("WATER")) {
            e.setCancelled(true);
            player.setItemInHand(new ItemStack(Material.WATER_BUCKET));
        }
    }

    @EventHandler
    public void on(final PlayerInteractEvent e) {
        final Player player = e.getPlayer();

        if (!player.getWorld().getName().equals(WORLD)) return;
        if (player.getGameMode().equals(GameMode.CREATIVE)) return;

        if (e.getItem() != null && e.getItem().equals(goldenHead)) {

            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 4, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 20, 1));

            e.setCancelled(true);
            player.getInventory().setItemInHand(null);
            return;
        }

        final Block clickedBlock = e.getClickedBlock();
        if (clickedBlock == null) return;

        if ((clickedBlock.getType() == Material.SOIL || clickedBlock.getRelative(BlockFace.UP).getType() == Material.CROPS && e.getAction() == Action.PHYSICAL)
                || clickedBlock.getType().name().contains("DOOR")) {
            e.setUseInteractedBlock(Event.Result.DENY);
            e.setCancelled(true);
        }

    }

    @EventHandler
    public void on(final PlayerDropItemEvent e) {
        final Player player = e.getPlayer();
        if (!e.getPlayer().getWorld().getName().equals(WORLD)) return;
        if (player.getGameMode().equals(GameMode.CREATIVE)) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void on(final PlayerDeathEvent e) {
        e.setDeathMessage(null);
    }

    @EventHandler
    public void on(final ProjectileLaunchEvent e) {
        if (!e.getEntity().getWorld().getName().equals(WORLD)) return;
        if (!(e.getEntity() instanceof Arrow)) return;

        if (e.getEntity().getShooter() instanceof Player) {
            if (((Player) e.getEntity().getShooter()).getLocation().getY() > 35) {
                e.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void on(final CombatLogEvent e) {
        final Player player = e.getPlayer();

        AssiPlayer assiPlayer = getAssiPlugin().getPlayerManager().getPlayer(player);
        assiPlayer.takeBucks(30);

    }

    @EventHandler
    public void on(final EntityDamageEvent e) {
        if (!e.getEntity().getWorld().getName().equals(WORLD)) return;
        final Entity attacked = e.getEntity();
        if (!(attacked instanceof Player)) return;
        Player player = (Player) e.getEntity();

        if (!player.getWorld().getName().equals(WORLD)) return;

        if (player.getLocation().getY() > 35) {
            e.setCancelled(true);
            return;
        }

        if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
            WorldData worldData = assiPlugin.getWorldManager().getWorldData(WORLD);
            if (worldData.getRegions().containsKey("spawn")) {
                if (worldData.getRegions().get("spawn").intersects(player.getLocation(), true)) {
                    e.setCancelled(true);
                    return;
                }
            }
        }

        if (player.getHealth() - e.getDamage() > 0) return;
        // Player is dead
        e.setCancelled(true);

        if (e instanceof EntityDamageByEntityEvent) {
            final EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) e;

            final Entity damager = entityDamageByEntityEvent.getDamager();
            if (damager instanceof Player || damager instanceof Arrow) {

                Player actualDamager;
                PVPPlayer pvpDamager;

                boolean melee = true;

                if (damager instanceof Player) {
                    pvpDamager = getPvpStatsProvider().getPlayer(((Player) damager));
                    actualDamager = (Player) damager;
                } else if (((Arrow) damager).getShooter() instanceof Player) {
                    pvpDamager = getPvpStatsProvider().getPlayer(((Player) ((Arrow) damager).getShooter()).getPlayer());
                    melee = false;
                    actualDamager = ((Player) ((Arrow) damager).getShooter());
                } else return;

                pvpDamager.addKill();
                assiPlugin.getRewardManager().giveBucks(assiPlugin.getPlayerManager().getPlayer(pvpDamager.getUuid()), BuckRewards.LOBBY_PVP_KILL);

                pvpDamager.addXp(50);
                addKillStreak(actualDamager);

                actualDamager.playSound(damager.getLocation(), Sound.ARROW_HIT, 3f, .5f);
                messageWorld(C.V + player.getName() + C.C + " was " + (melee ? "slain" : "sniped") + " by " + C.V + pvpDamager.getName() +
                        ChatColor.DARK_RED + " " + ((Math.round(actualDamager.getHealth()) / 2d) + "❤"));
            }

        }

        handleDamage(player, e.getDamage(), false);

    }

    @EventHandler
    public void on(final WeatherChangeEvent e) {
        if (!e.getWorld().getName().equals(WORLD)) return;
        if (e.toWeatherState()) e.setCancelled(true);
    }

    @EventHandler
    public void on(final LeavesDecayEvent e) {
        if (!e.getBlock().getWorld().getName().equals(WORLD)) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void on(final BlockFromToEvent e) {
        final Block toBlock = e.getToBlock();

//        System.out.println(e.getBlock());

//        System.out.println(toBlock.getType());

        if (toBlock.getType() == Material.DIRT
                || toBlock.getType().name().contains("STONE")
                || toBlock.getType() == Material.OBSIDIAN) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void on(final PlayerCommandPreprocessEvent e) {
        final Player player = e.getPlayer();
        if (!player.getWorld().getName().equals(WORLD)) return;
        AssiPlayer assiPlayer = getAssiPlugin().getPlayerManager().getPlayer(player);
        if (assiPlayer.getRank().isHigherThanOrEqualTo(Rank.ADMIN)) return;

        final String cmd = e.getMessage().replaceFirst("/", "").split(" ")[0].toLowerCase();

        if (cmd.equals("fly") && assiPlayer.isVanished()) {
            return;
        }

        if (disableCommand.contains(cmd)) {
            e.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot run this command right now.");
        }

    }

    public PVPStatsProvider getPvpStatsProvider() {
        return pvpStatsProvider;
    }

    public Location getSpawn() {
        return spawn;
    }

    private void messageWorld(String message) {
        Bukkit.getWorld(WORLD).getPlayers().forEach(player -> player.sendMessage(message));
    }

    private void kit(Player player) {

        player.setWalkSpeed(0.2f);
        player.setFoodLevel(20);
        player.setLevel(0);
        player.setExp(0);
        final PlayerInventory inventory = player.getInventory();
        inventory.clear();
        player.setGameMode(GameMode.SURVIVAL);

        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60 * 20, 2, false, false));

        inventory.addItem(new ItemStack(Material.STONE_SWORD),
                new ItemStack(Material.BOW),
                new ItemStack(Material.IRON_PICKAXE),
                new ItemStack(Material.IRON_AXE),
                new ItemStack(Material.GOLDEN_APPLE, 2));

        inventory.setItem(35, new ItemStack(Material.ARROW, 64));

        inventory.setHelmet(new ItemStack(Material.LEATHER_HELMET));
        inventory.setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
        inventory.setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
        inventory.setBoots(new ItemStack(Material.LEATHER_BOOTS));

        assiPlugin.getCosmeticManager().removeAllPlayerCosmetic(player);
    }

    private boolean handleDamage(Player player, double inflictedDamage, boolean border) {

        if (player.getHealth() - inflictedDamage > 0) return true;

        combatLogger.cancelCount(player, true);
        final Material type = player.getLocation().getBlock().getType();
        if (type == Material.WATER || type == Material.STATIONARY_WATER) {
            player.sendMessage(C.II + "You were washed up by the sea...");
        } else if (border) {

            player.sendMessage(C.II + "In fairness, you were warned...");

        } else {

            final EntityDamageEvent lastDamageCause = player.getLastDamageCause();

            if (lastDamageCause != null) {
                // Death message
                switch (lastDamageCause.getCause()) {
                    case FALL:
                        player.sendMessage(C.II + "You fell to your death.");
                        break;
                    case ENTITY_ATTACK:
                    case PROJECTILE:
                        break;
                    default:
                        player.sendMessage(C.II + "You died.");
                }

            }

        }

        PVPPlayer victim = pvpStatsProvider.getPlayer(player);
        victim.addDeath();
        resetKillStreak(player);
        getAssiPlugin().getScoreboardManager().update(getAssiPlugin().getPlayerManager().getPlayer(player));


        // Death effects
        if (player.getInventory().firstEmpty() != 0) {
            player.getLocation().getBlock().setType(Material.CHEST);

            final Block block = player.getLocation().getBlock();
            final BlockState blockState = block.getState();
            final Inventory inventory = ((Chest) blockState).getBlockInventory();

            final CraftWorld world = (CraftWorld) block.getWorld();
            final TileEntity entity = world.getTileEntityAt(block.getX(), block.getY(), block.getZ());
            ((TileEntityChest) entity).a(C.II + "The remains of " + C.V + player.getName());
            entity.update();

            for (ItemStack itemStack : player.getInventory().getContents()) {
                if (itemStack == null) continue;
                inventory.addItem(itemStack);
            }

            for (ItemStack itemStack : player.getInventory().getArmorContents()) {
                if (itemStack == null) continue;
                inventory.addItem(itemStack);
            }

            if (new Random().nextInt(2) == 1) {

                try {
                    final ItemStack playerHead = new ItemBuilder(Material.SKULL_ITEM)
                            .asPlayerHead(player.getName())
                            .setDisplay(C.II + "The head of " + C.V + player.getName())
                            .setLore("", C.C + "Craft this with 8 golden ingots around it",
                                    C.C + "to get a special " + ChatColor.GOLD + ChatColor.BOLD.toString() + "Golden Head").build();
                    inventory.addItem(playerHead);
                } catch (Exception e) {
                } // authentications exception if player has died too many times.
                // Random suppresses the chance of this occurring.
            } else {
                inventory.addItem(new ItemBuilder(Material.WEB).setDisplay(C.C + "Unlucky").build());
            }

            deathHolograms.put(block, HologramsAPI.createHologram(this, block.getLocation().clone().add(0.5, 2, 0.5)).
                    appendTextLine(C.II + "The remains of " + C.V + player.getName()).getParent());

            getServer().getScheduler().runTaskLater(this, () -> {
                if (block.getType() == Material.CHEST) {
                    inventory.clear();
                    block.setType(Material.AIR);
                    for (Player players : Bukkit.getWorld(WORLD).getPlayers()) {
                        if (players.getLocation().distance(block.getLocation()) < 3) {
                            players.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 3, 2));
                        }
                    }
                    block.getLocation().getWorld().createExplosion(block.getLocation().getX(),
                            block.getLocation().getY(), block.getLocation().getZ(), 2, false, false);

                }
                deathHolograms.get(block).delete();
                deathHolograms.remove(block);
            }, 20 * 60);

        }

        player.setHealth(20);
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 3));
        player.teleport(spawn);
        kit(player);

        return false;
    }

    private void resetKillStreak(Player player) {
        if (killCount.containsKey(player.getUniqueId())) {
            final Integer streak = killCount.get(player.getUniqueId());
            if (streak > 1) {
                player.sendMessage(C.C + ChatColor.ITALIC + "You lost your kill streak of " + C.V + ChatColor.ITALIC + streak + C.C + ChatColor.ITALIC + "!");
            }
            killCount.remove(player.getUniqueId());

            getAssiPlugin().getPlayerManager().getPlayer(player).clearProgress(achieveTenStreakWarmup.getId());
        }
    }

    private void addKillStreak(Player player) {
        int streak = killCount.getOrDefault(player.getUniqueId(), 0) + 1; // Set to one
        killCount.put(player.getUniqueId(), streak);

        final AssiPlayer assiPlayer = getAssiPlugin().getPlayerManager().getPlayer(player);

        if (assiPlayer.hasAchievement(achieveTenStreakWarmup.getId())) {
            assiPlayer.editProgress(achieveTenStreakWarmup.getId(), String.valueOf(streak));
            achieveTenStreakWarmup.showProgressHotbar(assiPlayer);
        }

        switch (streak) {
            case 10:
                achieveTenStreakWarmup.trigger(assiPlayer);
                UtilServer.broadcast(C.SS + C.C + "Wow, " + player.getDisplayName() + C.C + " is on a kill streak of " + C.V + streak + C.C + "!");
                break;
            case 20:
                UtilServer.broadcast(C.SS + C.C + player.getDisplayName() + C.C + " is really getting thirsty for those kills (" + C.V + streak + C.C + " streak)");
                break;
            case 50:
                UtilServer.broadcast(C.SS + C.C + player.getDisplayName() + C.C + " is literally sitting in a bath of their own sweat. (" + C.V + streak + C.C + " streak");
                break;
            case 100:
                UtilServer.broadcast(C.SS + C.C + "Can someone check up on " + player.getDisplayName() + C.C + "? They're on a kill streak of  " + C.V + streak + C.C + " gg bois.");
                break;
        }

    }

}
