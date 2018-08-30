package net.assimilationmc.gameapi.spectate;

import com.google.common.collect.Maps;
import net.assimilationmc.assicore.chat.ChatMessage;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.util.*;
import net.assimilationmc.gameapi.cmd.CmdSpecChatToggle;
import net.assimilationmc.gameapi.game.AssiGame;
import net.assimilationmc.gameapi.module.GameModule;
import net.assimilationmc.gameapi.module.ModuleActivePolicy;
import net.assimilationmc.gameapi.team.GameTeam;
import net.assimilationmc.gameapi.util.GC;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.UUID;

public class GameSpectateManager extends GameModule {

    public static final String SPECTATOR_TEAM_NAME = "Spectators", SPECTATOR_SPAWN = "SPECTATOR_SPAWN";

    private GameTeam spectatorTeam;

    private boolean instantSpawn;
    private int respawnTime;
    private boolean sendRespawnToLocation;
    private boolean permanentSpectator;

    private Map<UUID, String> limboTeams;

    private ItemStack leaveItem;

    public GameSpectateManager(AssiGame assiGame) {
        super(assiGame, "Spectate Manager", ModuleActivePolicy.PERMANENT);
    }

    @Override
    public void start() {
        this.limboTeams = Maps.newHashMap();
        setRespawnTime(-1);
        this.permanentSpectator = true;
        this.sendRespawnToLocation = true;

        spectatorTeam = new GameTeam(SPECTATOR_TEAM_NAME, ChatColor.GRAY);
        spectatorTeam.setHidden(true);
        getAssiGame().getTeamManager().addTeam(spectatorTeam);

        this.leaveItem = new ItemBuilder(Material.BED).setDisplay(C.II + "Back to Hub")
                .setLore(C.C, C.C + "Interact with this item to go back to safety").build();
    }

    @Override
    public void end() {
        this.limboTeams.clear();
    }

    @EventHandler
    public void on(final PlayerDeathEvent e) {
        Player player = e.getEntity();
        if (spectatorTeam.contains(player)) return;
        e.setDeathMessage(null);

        Entity killer = null;
        if (player.getLastDamageCause().getCause() != EntityDamageEvent.DamageCause.VOID) {
            killer = getAssiGame().getDeathLogger().getLastDamagedBy(player);
        }

        GameTeam gameTeam = getAssiGame().getTeamManager().getTeam(player);

        PlayerGameDeathEvent event = new PlayerGameDeathEvent(player, permanentSpectator, killer, gameTeam);
        UtilServer.callEvent(event);

        if (!event.isPerm() && gameTeam != null) {
            limboTeams.put(player.getUniqueId(), gameTeam.getName());
        }

        getAssiGame().getTeamManager().removeFromAnyTeam(player);

        setSpectator(player);

        if (!event.isPerm()) {
            if (!instantSpawn) {
                getAssiGame().getPlugin().getServer().getScheduler().runTaskLater(getAssiGame().getPlugin(), () -> {
                    if (UtilPlayer.get(player.getUniqueId()) == null) {
                        limboTeams.remove(player.getUniqueId());
                        return;
                    }
                    if (!limboTeams.containsKey(player.getUniqueId())) return;

                    PlayerGamePreRespawnEvent gamePreRespawnEvent = new PlayerGamePreRespawnEvent(player);
                    UtilServer.callEvent(gamePreRespawnEvent);

                    if (gamePreRespawnEvent.isCancelled()) {
                        limboTeams.remove(player.getUniqueId());
                        return;
                    }

                    GameTeam team = getAssiGame().getTeamManager().getTeam(limboTeams.get(player.getUniqueId()));

                    limboTeams.remove(player.getUniqueId());
                    if (team == null) {
                        player.sendMessage(GC.II + "Couldn't find your old team.");
                    } else {
                        team.add(player);
                        spectatorTeam.remove(player);
                        unset(player);
                    }

                    if (sendRespawnToLocation) {
                        final Map<String, SerializedLocation> spawns = getAssiGame().getPlugin().getWorldManager().getWorldData(player.getWorld()).getSpawns();
                        if (spawns.containsKey(SPECTATOR_SPAWN)) {
                            player.teleport(spawns.get(SPECTATOR_SPAWN).toLocation());
                        } else player.sendMessage(GC.II + "Couldn't find the spectator spawn.");
                    }

                }, respawnTime * 20);
            } else if (sendRespawnToLocation) {

                PlayerGamePreRespawnEvent gamePreRespawnEvent = new PlayerGamePreRespawnEvent(player);
                UtilServer.callEvent(gamePreRespawnEvent);

                if (!gamePreRespawnEvent.isCancelled()) {
                    GameTeam team = getAssiGame().getTeamManager().getTeam(limboTeams.get(player.getUniqueId()));

                    limboTeams.remove(player.getUniqueId());
                    if (team == null) {
                        player.sendMessage(GC.II + "Couldn't find your old team.");
                    } else {
                        team.add(player);
                        spectatorTeam.remove(player);
                        unset(player);
                    }

                    final Map<String, SerializedLocation> spawns = getAssiGame().getGameMapManager().getSelectedWorld().getSpawns();
                    if (spawns.containsKey(SPECTATOR_SPAWN)) {
                        player.teleport(spawns.get(SPECTATOR_SPAWN).toLocation());
                    } else player.sendMessage(GC.II + "Couldn't find the spectator spawn.");

                } else limboTeams.remove(player.getUniqueId());

            }
        }

    }

    @EventHandler(priority = EventPriority.LOW)
    public void on(final PlayerQuitEvent e) {
        final Player player = e.getPlayer();
        spectatorTeam.remove(player);
    }

    @EventHandler
    public void on(final BlockBreakEvent e) {
        if (e.getPlayer().getGameMode() != GameMode.CREATIVE && spectatorTeam.contains(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    public void on(final BlockPlaceEvent e) {
        if (e.getPlayer().getGameMode() != GameMode.CREATIVE && spectatorTeam.contains(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    public void on(final EntityDamageEvent e) {
        if (e.getEntity() instanceof Player && ((Player) e.getEntity()).getGameMode() != GameMode.CREATIVE &&
                spectatorTeam.contains(e.getEntity().getUniqueId())) e.setCancelled(true);
    }

    @EventHandler
    public void on(final EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && ((Player) e.getDamager()).getGameMode() != GameMode.CREATIVE &&
                spectatorTeam.contains(e.getDamager().getUniqueId())) e.setCancelled(true);
    }

    @EventHandler
    public void on(final PlayerInteractEvent e) {
        final Player player = e.getPlayer();

        if (spectatorTeam.contains(e.getPlayer())) {
            if (player.getItemInHand().equals(leaveItem)) {
                getAssiGame().getPlugin().getPlayerManager().sendLobby(getAssiGame().getPlugin().getPlayerManager().getPlayer(player), "");
                e.setCancelled(true);
                return;
            }

            if (e.getPlayer().getGameMode() != GameMode.CREATIVE) e.setCancelled(true);

        }

    }

    @EventHandler
    public void on(final PlayerDropItemEvent e) {
        if (e.getPlayer().getGameMode() != GameMode.CREATIVE && spectatorTeam.contains(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    public void on(final PlayerPickupItemEvent e) {
        if (e.getPlayer().getGameMode() != GameMode.CREATIVE && spectatorTeam.contains(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    public void on(final EntityTargetLivingEntityEvent e) {
        if (e.getTarget() != null && spectatorTeam.contains(e.getTarget().getUniqueId())) e.setCancelled(true);
    }

    @EventHandler
    public void on(final FoodLevelChangeEvent e) {
        if (spectatorTeam.contains(e.getEntity().getUniqueId())) e.setCancelled(true);
    }

    public void handleChat(ChatMessage chatMessage) {
        AssiPlayer sender = chatMessage.getSender();
        if (!spectatorTeam.contains(sender.getUuid())) return;

        String message = chatMessage.getFormat().replace("{name}", sender.getName()).replace("{message}", chatMessage.getMessage());

        for (UUID uuid : spectatorTeam.getPlayers()) {
            Player spectator = UtilPlayer.get(uuid);
            if (spectator == null) continue;
            spectator.sendMessage(message);
        }

        CmdSpecChatToggle cmdSpecChatToggle = (CmdSpecChatToggle) getAssiGame().getPlugin().getCommandManager().getCommand(CmdSpecChatToggle.class);
        if (cmdSpecChatToggle != null) {
            for (UUID uuid : cmdSpecChatToggle.getToggled()) {
                Player watch = UtilPlayer.get(uuid);
                if (watch == null || sender.getUuid().equals(watch.getUniqueId())) continue;
                watch.sendMessage(message);
            }
        }

    }

    public GameTeam getSpectatorTeam() {
        return spectatorTeam;
    }

    public boolean isInstantSpawn() {
        return instantSpawn;
    }

    public void setInstantSpawn(boolean instantSpawn) {
        this.instantSpawn = instantSpawn;
    }

    public int getRespawnTime() {
        return respawnTime;
    }

    public void setRespawnTime(int respawnTime) {
        this.respawnTime = respawnTime;
        this.instantSpawn = respawnTime < 1;
    }

    public boolean isSendRespawnToLocation() {
        return sendRespawnToLocation;
    }

    public void setSendRespawnToLocation(boolean sendRespawnToLocation) {
        this.sendRespawnToLocation = sendRespawnToLocation;
    }

    public boolean isPermanentSpectator() {
        return permanentSpectator;
    }

    public void setPermanentSpectator(boolean permanentSpectator) {
        this.permanentSpectator = permanentSpectator;
    }

    public void setSpectator(Player player) {
        spectatorTeam.add(player);
        setup(player, true);
    }

    private void setup(Player player, boolean effect) {
        if (effect) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 3, 1, false, false));
        }
        player.setAllowFlight(true);
        player.setFlying(true);
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        player.setWalkSpeed(0.2f);
        player.setFlySpeed(0.2f);
        player.setMaxHealth(20);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setFireTicks(0);
        player.playSound(player.getLocation(), Sound.CAT_MEOW, 1f, 1f);
        UtilMessage.sendSubTitle(player, GC.II + "You are now a Spectator.", 10, 3 * 20, 10);
        CmdSpecChatToggle cmdSpecChatToggle = (CmdSpecChatToggle) getAssiGame().getPlugin().getCommandManager().getCommand(CmdSpecChatToggle.class);
        if (cmdSpecChatToggle != null) {
            cmdSpecChatToggle.getToggled().remove(player.getUniqueId());
        }

        player.getInventory().setItem(8, leaveItem);
    }

    public void unset(Player player) {
        player.setFlying(false);
        player.setAllowFlight(false);
        player.setFoodLevel(20);
        player.setHealth(player.getMaxHealth());
        player.setFireTicks(0);
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));

        player.getInventory().remove(leaveItem);
    }

}
