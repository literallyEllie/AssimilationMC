package net.assimilationmc.assicore.lobby;

import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.Module;
import net.assimilationmc.assicore.lobby.donor.CmdChatColor;
import net.assimilationmc.assicore.parkour.ParkourManager;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.redis.pubsub.PubSubRecipient;
import net.assimilationmc.assicore.redis.pubsub.RedisPubSubMessage;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.Domain;
import net.assimilationmc.assicore.util.UtilMessage;
import net.assimilationmc.assicore.world.AssiRegion;
import net.assimilationmc.assicore.world.WorldData;
import net.assimilationmc.assicore.world.WorldPreserver;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import redis.clients.jedis.Jedis;

import java.util.Map;
import java.util.Random;

;

public class LobbyManager extends Module {

    private String DONATOR_PLAYER_JOIN, WORLD;

    private boolean disabled;
    private WorldPreserver worldPreserver;

    private ParkourManager parkourManager;

    public LobbyManager(AssiPlugin plugin) {
        super(plugin, "Lobby Manager");
    }

    @Override
    protected void start() {
        disabled = !getPlugin().getServerData().isLobby();
        if (disabled) return;

        worldPreserver = new WorldPreserver(Bukkit.getWorlds().get(0));
        worldPreserver.setProtectPlayers(true);
        worldPreserver.setStopPlayerInteract(true);
        getPlugin().registerListener(worldPreserver);

        this.DONATOR_PLAYER_JOIN = ChatColor.GREEN + ChatColor.BOLD.toString() + "+ {display_name} " + ChatColor.RESET + ChatColor.GRAY + " has joined the lobby.";
        this.WORLD = getPlugin().getWorldManager().getPrimaryWorld().getName();

        getPlugin().getServer().getScheduler().scheduleSyncRepeatingTask(getPlugin(), () -> Bukkit.getWorlds().get(0).setTime(0), 0, 200L);

        getPlugin().getServer().getScheduler().scheduleSyncRepeatingTask(getPlugin(), () -> getPlugin().getPlayerManager().getOnlinePlayers().values().stream().filter(player -> player.getBase().isFlying())
                .forEach(player -> {

                    if (player.getRank().isHigherThanOrEqualTo(Rank.DEVELOPER) && !player.isVanished()) {
                        for (AssiPlayer oPlayer : getPlugin().getPlayerManager().getOnlinePlayers().values()) {
                            oPlayer.getBase().playEffect(player.getBase().getLocation(), Effect.COLOURED_DUST, 20);
                        }
                    } else if (!player.getRank().isDefault() && !player.isVanished()) {
                        for (AssiPlayer oPlayer : getPlugin().getPlayerManager().getOnlinePlayers().values()) {
                            oPlayer.getBase().playEffect(player.getBase().getLocation(), Effect.CLOUD, 20);
                        }
                    }


                }), 0L, 5L);

        getPlugin().getScoreboardManager().setScoreboardPolicy(new LobbyScorePolicy(getPlugin()));
        getPlugin().getJoinItemManager().addItem(new ItemLobbySelector(getPlugin()));

        this.parkourManager = new ParkourManager(getPlugin());

        getPlugin().getCommandManager().registerCommand(new CmdChatColor(getPlugin()));

        if (getPlugin().getServerData().isLobby() && !getPlugin().getServerData().isLocal()) {
            getPlugin().getRedisManager().sendPubSubMessage("INTERNAL", new RedisPubSubMessage(PubSubRecipient.PROXY,
                    getPlugin().getServerData().getId(), "ONLINE", new String[]{"memes"}));
        }

    }

    @Override
    protected void end() {
        disabled = true;

        if (getPlugin().getServerData().isLobby() && !getPlugin().getServerData().isLocal()) {
            getPlugin().getRedisManager().sendPubSubMessage("INTERNAL", new RedisPubSubMessage(PubSubRecipient.PROXY,
                    getPlugin().getServerData().getId(), "OFFLINE", new String[]{"memes"}));
        }

    }

    /**
     * Reset a player inventory and stuff.
     *
     * @param player The player to reset.
     */
    public void resetPlayer(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setGameMode(GameMode.ADVENTURE);
        player.setFoodLevel(20);
        player.setHealth(20);
        player.setExhaustion(0);
        player.setExp(0);
        player.setLevel(0);
        player.setWalkSpeed(0.2f);
        player.setFlySpeed(0.2f);
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 30L, 20L);
    }

    public WorldPreserver getWorldPreserver() {
        return worldPreserver;
    }

    public ParkourManager getParkourManager() {
        return parkourManager;
    }

    @EventHandler
    public void on(final PlayerJoinEvent e) {
        if (disabled) return;
        final AssiPlayer player = getPlugin().getPlayerManager().getPlayer(e.getPlayer());

        if (player.getJoins() == 1) {
            e.setJoinMessage(ChatColor.GOLD + player.getName() + ChatColor.YELLOW + ChatColor.BOLD.toString()
                    + " has joined " + ChatColor.DARK_GREEN + "Assi" + ChatColor.GREEN + "milationMC " + ChatColor.YELLOW + ChatColor.BOLD.toString() +
                    "for the first time! Welcome");
        } else if (player.getRank().isDonator()) {
            e.setJoinMessage(DONATOR_PLAYER_JOIN.replace("{display_name}", player.getDisplayName()));
            UtilMessage.sendSubTitle(player.getBase(), C.II + "Welcome back to " + ChatColor.DARK_GREEN + "Assi" + ChatColor.GREEN + "milationMC " + C.V +
                    player.getName() + C.II + "!", 7, 30, 7);
        } else e.setJoinMessage(null);

        final WorldData worldData = getPlugin().getWorldManager().getPrimaryWorld();

        if (worldData != null && worldData.getSpawns().containsKey("spawn")) {
            player.getBase().teleport(worldData.getSpawns().get("spawn").toLocation());

            if (player.getRank().isDonator()) {
                player.getBase().getLocation().getWorld().strikeLightning(player.getBase().getLocation());
            }

        }
        getPlugin().getJoinItemManager().give(player);

        resetPlayer(player.getBase());

        final boolean higherThanOrEqualTo = player.getRank().isHigherThanOrEqualTo(Rank.DEMONIC);
        player.getBase().setAllowFlight(higherThanOrEqualTo);
        player.getBase().setFlying(higherThanOrEqualTo);

        switch (new Random().nextInt(3)) {
            case 0:
                player.sendMessage(C.SS + ChatColor.GREEN + "Have you voted today?");
                break;
            case 1:
                player.sendMessage(C.SS + ChatColor.GREEN + "Check out our donator store for epic perks around the network " + C.V + Domain.PROT_STORE);
                break;
            case 2:
                player.sendMessage(C.SS + ChatColor.GREEN + "If you click the Emerald block, you can create your own game!");
        }

    }

    @EventHandler
    public void on(final PlayerDeathEvent e) {
        if (disabled) return;
        final Player player = e.getEntity();

        final WorldData worldData = getPlugin().getWorldManager().getPrimaryWorld();

        if (worldData != null && worldData.getSpawns().containsKey("spawn")) {
            player.teleport(worldData.getSpawns().get("spawn").toLocation());
        }

        AssiPlayer assiPlayer = getPlugin().getPlayerManager().getPlayer(player);
        resetPlayer(player);
        getPlugin().getJoinItemManager().give(assiPlayer);

        final boolean higherThanOrEqualTo = assiPlayer.getRank().isHigherThanOrEqualTo(Rank.DEMONIC);
        player.setAllowFlight(higherThanOrEqualTo);
        player.setFlying(higherThanOrEqualTo);

    }

    @EventHandler
    public void on(final PlayerTeleportEvent e) {
        if (disabled) return;
        if (e.getFrom().getWorld().getName().equals(e.getTo().getWorld().getName())) return;

        final Player player = e.getPlayer();

        if (e.getTo().getWorld().getName().equals(WORLD)) {
            AssiPlayer assiPlayer = getPlugin().getPlayerManager().getPlayer(player);
            resetPlayer(player);
            getPlugin().getJoinItemManager().give(assiPlayer);

            final boolean higherThanOrEqualTo = assiPlayer.getRank().isHigherThanOrEqualTo(Rank.DEMONIC);
            player.setAllowFlight(higherThanOrEqualTo);
            player.setFlying(higherThanOrEqualTo);
        }

    }

    @EventHandler
    public void on(final PlayerQuitEvent e) {
        if (disabled) return;
        e.setQuitMessage(null);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void on(final EntityDamageEvent e) {
        if (disabled || !e.getEntity().getWorld().getName().equals(WORLD)) return;
        Entity entity = e.getEntity();

        final Map<String, AssiRegion> regions = getPlugin().getWorldManager().getWorldData(entity.getWorld()).getRegions();
        if (regions.containsKey("spawn")) {
            if (regions.get("spawn").containsEntity(entity)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void on(final PlayerMoveEvent e) {
        if (disabled || !e.getPlayer().getWorld().getName().equals(WORLD)) return;
        final Player player = e.getPlayer();
        if (player.getLocation().getBlockY() <= 0) {
            final WorldData worldData = getPlugin().getWorldManager().getPrimaryWorld();
            if (worldData != null && worldData.getSpawns().containsKey("spawn")) {
                player.teleport(worldData.getSpawns().get("spawn").toLocation());
                player.sendMessage(C.C + ChatColor.ITALIC + "Caught you!");
            }
            return;
        }

        final Block below = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
        if (below != null && below.getType() == Material.SEA_LANTERN) {
            Location launch = player.getLocation().clone();
            launch.add(0, 1, 0);
            launch.setPitch(-10);
            player.setVelocity(launch.getDirection().multiply(2));
            return;
        }

    }

}
