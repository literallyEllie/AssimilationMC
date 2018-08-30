package net.assimilationmc.assiuhc.game;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.assimilationmc.assicore.achievement.AchievementCategory;
import net.assimilationmc.assicore.booster.Booster;
import net.assimilationmc.assicore.booster.BoosterType;
import net.assimilationmc.assicore.combat.CombatLogger;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.*;
import net.assimilationmc.assiuhc.achievement.AchieveFirstBlood;
import net.assimilationmc.assiuhc.achievement.AchieveSinglesWin;
import net.assimilationmc.assiuhc.achievement.AchieveStrongBonds;
import net.assimilationmc.assiuhc.achievement.AchieveTeamedWin;
import net.assimilationmc.assiuhc.border.GameBorder;
import net.assimilationmc.assiuhc.command.CmdForceRespawn;
import net.assimilationmc.assiuhc.drops.DropManager;
import net.assimilationmc.assiuhc.game.custom.CustomizationProperties;
import net.assimilationmc.assiuhc.game.custom.CustomizationProperty;
import net.assimilationmc.assiuhc.network.UHCNetworkManager;
import net.assimilationmc.assiuhc.player.UHCPlayer;
import net.assimilationmc.assiuhc.player.UHCPlayerStatProvider;
import net.assimilationmc.assiuhc.reward.CmdXP;
import net.assimilationmc.assiuhc.reward.CmdXpManager;
import net.assimilationmc.assiuhc.reward.XPManager;
import net.assimilationmc.assiuhc.reward.XPRewards;
import net.assimilationmc.gameapi.GamePlugin;
import net.assimilationmc.gameapi.game.AssiGame;
import net.assimilationmc.gameapi.game.AssiGameMeta;
import net.assimilationmc.gameapi.phase.GamePhase;
import net.assimilationmc.gameapi.phase.GamePhaseChangeEvent;
import net.assimilationmc.gameapi.spectate.GameSpectateManager;
import net.assimilationmc.gameapi.spectate.PlayerGameDeathEvent;
import net.assimilationmc.gameapi.team.GameTeam;
import net.assimilationmc.gameapi.team.GameTeamManager;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Furnace;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public abstract class UHCGame extends AssiGame {

    private final UHCPlayerStatProvider playerManager;
    private final XPManager xpManager;
    private final UHCNetworkManager uhcNetworkManager;
    private final DropManager dropManager;
    private UHCGameSubType gameSubType;

    private int startingPlayers;
    private CombatLogger combatLogger;
    private GameBorder gameBorder;

    private List<String> blockedCommands;
    private List<UUID> rewarded;

    private List<PotionEffectType> headEffects;

    private Map<String, Object> serverProperties;

    public UHCGame(GamePlugin plugin, AssiGameMeta assiGameMeta) {
        super(plugin, assiGameMeta);
        this.gameSubType = UHCGameSubType.valueOf(assiGameMeta.getSubType());

        this.playerManager = new UHCPlayerStatProvider(this);
        setGameStatsProvider(playerManager);

        this.xpManager = new XPManager(this);
        plugin.getCommandManager().registerCommand(new CmdXpManager(this), new CmdXP(this));

        this.serverProperties = Maps.newHashMap();

        if (System.getProperty("custom") !=  null) {
            serverProperties = UtilJson.deserialize(new Gson(), new TypeToken<Map<String, Object>>() {
            }, System.getProperty("custom"));
        }

        this.uhcNetworkManager = new UHCNetworkManager(this);

        this.dropManager = new DropManager(this);

        this.blockedCommands = Lists.newArrayList("hub", "fly", "cosmetic", "cosmetics", "spawn", "feed",
                "eat", "heal", "perks", "party", "tp", "v", "vanish", "teleport");

        getAssiGameSettings().setPve(true);
        getAssiGameSettings().setPvp(false);

        getSpectateManager().setSendRespawnToLocation(false);

        Bukkit.getWorlds().forEach(world -> world.setGameRuleValue("naturalRegeneration", "false"));

        World world = Bukkit.getWorld(getGameMapManager().getSelectedWorld().getName());
        if (world != null) {
                world.setTime((boolean) getProperty(CustomizationProperties.MOBS) ? 12500 : 24000);
                world.setWeatherDuration(1);
            // world.setGameRuleValue("doMobSpawning", String.valueOf(mobs));
        }

        this.combatLogger = new CombatLogger(getPlugin(), 15);
        combatLogger.setNotifyInCombat(false);

        this.rewarded = Lists.newArrayList();

        getPlugin().getBoosterManager().registerBooster(new Booster("45_DOUBLE_XP", "x2 XP",
                "All XP rewards will be doubled for the whole game.", BoosterType.UHC_XP, TimeUnit.MINUTES.toMillis(30), 20) {
            @Override
            public int processUHCXp(int in) {
                return in * 2;
            }
        });


        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            int time = getIntProperty(CustomizationProperties.WARMUP_TIME);
            if (time == -2) return;

            getAssiGameSettings().setWarmUpTime(Math.max(0, time));
        }, 20 * 4);

        headEffects = Arrays.stream(PotionEffectType.values()).filter(potionEffectType ->
                potionEffectType == PotionEffectType.BLINDNESS && potionEffectType == PotionEffectType.CONFUSION
                && potionEffectType == PotionEffectType.HARM && potionEffectType == PotionEffectType.POISON
                && potionEffectType == PotionEffectType.WEAKNESS && potionEffectType == PotionEffectType.SLOW_DIGGING
                && potionEffectType == PotionEffectType.FAST_DIGGING && potionEffectType == PotionEffectType.WITHER
                && potionEffectType == PotionEffectType.NIGHT_VISION && potionEffectType == PotionEffectType.HUNGER
                && potionEffectType == PotionEffectType.INVISIBILITY && potionEffectType == PotionEffectType.WATER_BREATHING
                && potionEffectType == PotionEffectType.JUMP).collect(Collectors.toList());

        if ((boolean) getProperty(CustomizationProperties.NON_VANILLA_RECIPES)) {
            // God apple with nuggets

            ShapedRecipe godApple = new ShapedRecipe(new ItemStack(Material.GOLDEN_APPLE, 1, (byte) 1));
            godApple.shape("GGG",
                    "GAG",
                    "GGG");
            godApple.setIngredient('G', new MaterialData(Material.GOLD_NUGGET));
            godApple.setIngredient('A', new MaterialData(Material.APPLE));
            plugin.getServer().addRecipe(godApple);

            //

        }

        // Commands
        getPlugin().getCommandManager().registerCommand(new CmdForceRespawn(this));

        // Achievement init
        getPlugin().getAchievementManager().addAchievement(new AchieveFirstBlood(getPlugin()));
        getPlugin().getAchievementManager().addAchievement(new AchieveSinglesWin(getPlugin()));
        getPlugin().getAchievementManager().addAchievement(new AchieveTeamedWin(getPlugin()));
        getPlugin().getAchievementManager().addAchievement(new AchieveStrongBonds(this));



    }

    public UHCGameSubType getGameSubType() {
        return gameSubType;
    }

    public UHCPlayerStatProvider getPlayerManager() {
        return playerManager;
    }

    public XPManager getXpManager() {
        return xpManager;
    }

    public UHCNetworkManager getUhcNetworkManager() {
        return uhcNetworkManager;
    }

    public DropManager getDropManager() {
        return dropManager;
    }

    public GameBorder getGameBorder() {
        return gameBorder;
    }

    public void setGameBorder(GameBorder gameBorder) {
        this.gameBorder = gameBorder;
    }

    public final boolean callDeathMatch() {
        return false;
    }

    public abstract int winnerXp();

    public abstract int winnerUC();

    public abstract int winnerBucks();

    @EventHandler
    public void commandBlock(final PlayerCommandPreprocessEvent e) {
        if (!(getGamePhase() == GamePhase.WARMUP || getGamePhase() == GamePhase.IN_GAME)) return;
        String command = e.getMessage().split(" ")[0].replace("/", "").toLowerCase();

        if (!blockedCommands.contains(command)) return;


        GameTeam team = getTeamManager().getTeam(e.getPlayer());
        AssiPlayer player = getPlugin().getPlayerManager().getPlayer(e.getPlayer());

        if (!player.getRank().isHigherThanOrEqualTo(Rank.ADMIN) || (team != null && team.getName().equals(GameSpectateManager.SPECTATOR_TEAM_NAME))) {
            player.sendMessage(ChatColor.RED + "You cannot perform that command at this time.");
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void customExplode(final EntityExplodeEvent  e) {
        if (!(boolean) getProperty(CustomizationProperties.EXPLOSIONS)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void noSlimes(final EntitySpawnEvent e) {
        final Entity entity = e.getEntity();

        if (UtilMob.isAggressive(entity.getType())) {
            if ((!(boolean) getProperty(CustomizationProperties.MOBS))) {
                e.setCancelled(true);
                return;
            }

            if (entity.getType() == EntityType.SLIME) {
                e.setCancelled(true);
                return;
            }

            if (new Random().nextInt(3) <= 1) {
                e.setCancelled(true);
            }

        }

    }

    @EventHandler
    public void pvpEnable(final GamePhaseChangeEvent e) {
        if (e.getTo() == GamePhase.WARMUP) {

            final Object objHp = getProperty(CustomizationProperties.MAX_HEALTH);
            double hp = (objHp instanceof Integer ? ((Integer) objHp).doubleValue() : (double) objHp);

            getLivePlayers().forEach(player -> {
                final CraftPlayer craftPlayer = (CraftPlayer) player;
                craftPlayer.setMaxHealth(hp);
                craftPlayer.setHealthScale(hp);
                craftPlayer.setHealth((float) hp);
            });

        }

        if (e.getTo() == GamePhase.IN_GAME) {
            startingPlayers = getLivePlayers().size();
            getAssiGameSettings().setPvp(true);
            Bukkit.getOnlinePlayers().forEach(o -> o.playSound(o.getLocation(), Sound.ENDERDRAGON_GROWL, 3f, 5f));
            UtilServer.broadcast(C.II + ChatColor.BOLD + "PvP has now been enabled. GLHF");
        }
    }

    @EventHandler
    public void customBurn(final FurnaceBurnEvent e) {
        if (!(boolean) getProperty(CustomizationProperties.QUICK_SMELT)) return;
        Furnace furnace = (Furnace) e.getBlock().getState();
        furnace.setCookTime((short) 100);
        furnace.update();
    }

    @EventHandler
    public void deathMsgReward(final PlayerGameDeathEvent e) {
        final Entity killer = e.getKiller();
        final GameTeam team = e.getTeam();
        if (team == null) return;

        final Player player = e.getPlayer();

        String deathMsg = (team.getName().equals(GameTeamManager.DEFAULT_PLAYER_TEAM) ? ChatColor.GREEN : team.getColor() +
                team.getName() + " " + C.V) + player.getName() + C.C + ChatColor.ITALIC + " ";

        if (killer != null) {

            if (killer instanceof Player) {
                // Issue rewardseating
                final AssiPlayer assiKiller = getPlugin().getPlayerManager().getPlayer(killer.getUniqueId());
                getXpManager().giveXP(getPlayerManager().getPlayer(killer.getUniqueId()), XPRewards.KILL + (assiKiller.getRank().isDonator() ? 3 : 0));

                getPlugin().getRewardManager().giveBucks(assiKiller, BuckRewards.GENERIC_UHC_KILL + (assiKiller.getRank().isDonator() ? 5 : 0));

                // Death message
                GameTeam gameTeam = getTeamManager().getTeam((Player) killer);
                deathMsg += "died to the hands of " + (gameTeam.getName().equals(GameTeamManager.DEFAULT_PLAYER_TEAM) ? ChatColor.GREEN :
                        gameTeam.getColor() + gameTeam.getName() + " " + C.V) + killer.getName() + C.C + ChatColor.ITALIC + ".";
            } else {
                deathMsg += "was killed by a " + C.V + killer.getName() + C.C + ChatColor.ITALIC + ".";
            }

        } else if (player.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.SUFFOCATION){
            deathMsg += " died to the border.";
        } else deathMsg += "died.";
        player.getWorld().strikeLightningEffect(player.getLocation());

        Bukkit.getOnlinePlayers().forEach(o -> o.playSound(o.getLocation(), Sound.AMBIENCE_THUNDER, 3f, 5f));
        UtilServer.broadcast(deathMsg);

        player.getWorld().dropItemNaturally(player.getLocation(), new ItemBuilder(Material.SKULL_ITEM)
                .asPlayerHead(player.getName()).setDisplay(C.II + "The head of " + player.getName())
                .setLore(C.C, C.V + "Interact" + C.V + " with to get a random effect.").build());
    }

    @EventHandler
    public void on(final PlayerInteractEvent e) {
        final Player player = e.getPlayer();
        final ItemStack item = e.getItem();
        if (item != null && item.getType() == Material.SKULL_ITEM && !getSpectateManager().getSpectatorTeam().contains(player)) {
            e.setCancelled(true);

            List<PotionEffectType> toGive = Lists.newArrayList();
            for (int i = 0; i < new Random().nextInt(1) + 1; i++) {
                toGive.add(headEffects.get(new Random().nextInt(headEffects.size() -1)));
            }

            player.getInventory().remove(item);
            toGive.forEach(potionEffectType -> player.addPotionEffect(new PotionEffect(potionEffectType, 5 * 20, 2, false, false)));

            player.sendMessage(ChatColor.GREEN + "You have received the effect" + (toGive.size() > 1 ? "s" : "") + " of " +
                    Joiner.on(" and ").join(toGive.stream().map(potionEffectType -> StringUtils.capitalize(potionEffectType.getName().toLowerCase()
                            .replace("_", " "))).collect(Collectors.toList())) + ".");
        }

    }

    @EventHandler
    public void noDamageEnd(final EntityDamageEvent e) {
        if (getGamePhase() == GamePhase.END) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void xpQuit(final PlayerQuitEvent e) {
        final Player player = e.getPlayer();
        if (getGamePhase() == GamePhase.LOBBY || getGamePhase() == GamePhase.END || !getDeathLogger().hasDied(player))
            return;
        if (rewarded.contains(player.getUniqueId())) return;
        UHCPlayer uhcPlayer = getPlayerManager().getPlayer(player);
        getXpManager().giveXP(uhcPlayer, XPRewards.PARTICIPATE);
        uhcPlayer.addGamePlayed(getGameSubType());
        rewarded.add(player.getUniqueId());
    }

    @EventHandler
    public void appleDrop(final BlockBreakEvent e) {
        if ((e.getBlock().getType() == Material.LEAVES || e.getBlock().getType() == Material.LEAVES_2) && new Random().nextInt(3) == 1) {
            e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemStack(Material.APPLE, 1));

        }
    }

    public boolean isDeathmatch() {
        return false;
    }

    public List<String> getBlockedCommands() {
        return blockedCommands;
    }

    public void addBlockedCommand(String command) {
        this.blockedCommands.add(command.toLowerCase());
    }

    public int getStartingPlayers() {
        return startingPlayers;
    }

    public CombatLogger getCombatLogger() {
        return combatLogger;
    }

    public List<UUID> getRewarded() {
        return rewarded;
    }

    public Map<String, Object> getServerProperties() {
        return serverProperties;
    }

    public Object getProperty(CustomizationProperty key) {
        if (serverProperties.containsKey(key.id()))
            return serverProperties.get(key.id());
        return key.getDefaultValue(getGameSubType());
    }

    public int getIntProperty(CustomizationProperty key) {
        final Object property = getProperty(key);
        if (property instanceof Double)
            return ((Double) property).intValue();
        return -2;
    }


}


