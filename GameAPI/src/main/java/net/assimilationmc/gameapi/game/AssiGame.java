package net.assimilationmc.gameapi.game;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.event.update.UpdateEvent;
import net.assimilationmc.assicore.event.update.UpdateType;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.scoreboard.ScoreboardPolicy;
import net.assimilationmc.assicore.util.*;
import net.assimilationmc.assicore.world.WorldPreserver;
import net.assimilationmc.gameapi.GamePlugin;
import net.assimilationmc.gameapi.chat.GameChatPolicy;
import net.assimilationmc.gameapi.damage.GameDamageManager;
import net.assimilationmc.gameapi.death.DeathLogger;
import net.assimilationmc.gameapi.map.GameMapManager;
import net.assimilationmc.gameapi.module.GameModule;
import net.assimilationmc.gameapi.module.GameModuleHandle;
import net.assimilationmc.gameapi.phase.GamePhase;
import net.assimilationmc.gameapi.phase.GamePhaseChangeEvent;
import net.assimilationmc.gameapi.ping.GameNetworker;
import net.assimilationmc.gameapi.spectate.GameSpectateManager;
import net.assimilationmc.gameapi.stats.GameStatsProvider;
import net.assimilationmc.gameapi.team.GameTeam;
import net.assimilationmc.gameapi.team.GameTeamManager;
import net.assimilationmc.gameapi.util.GC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collection;
import java.util.List;

public abstract class AssiGame implements Listener {

    private final GamePlugin gamePlugin;
    private final AssiGameMeta assiGameMeta;
    private final AssiGameSettings assiGameSettings;

    private final Collection<GameModule> modules = Lists.newArrayList();
    private GameModuleHandle gameModuleHandle;
    private GameMapManager gameMapManager;
    private GameTeamManager teamManager;
    private GameDamageManager gameDamageManager;
    private GameSpectateManager spectateManager;
    private GameNetworker gameConnectManager;
    private DeathLogger deathLogger;
    private GameStatsProvider gameStatsProvider;

    private boolean foreverDayLight;
    private int dayLightTask;

    private WorldPreserver worldPreserver;

    private boolean autoTeam, forceStart;

    private GamePhase gamePhase;
    private long start;

    private int counter = -1;

    private int serverTimeoutTask = -1;

    public AssiGame(GamePlugin plugin, AssiGameMeta assiGameMeta) {
        this.gamePlugin = plugin;
        this.assiGameMeta = assiGameMeta;

        // default settings
        this.assiGameSettings = new AssiGameSettings();
        this.assiGameSettings.setMaxPlayers(100);
        this.assiGameSettings.setMinPlayers(2); // debug
        this.assiGameSettings.setLobbyTime(30);
        this.assiGameSettings.setWarmUpTime(60 * 5);
        this.assiGameSettings.setMaxGameTime(2700);
        this.assiGameSettings.setEndTime(30);
        this.assiGameSettings.setPve(true);
        this.assiGameSettings.setPvp(true);
        this.assiGameSettings.setFriendlyFire(false);

        this.gameModuleHandle = new GameModuleHandle(this);
        this.gameMapManager = new GameMapManager(this);
        this.teamManager = new GameTeamManager(this);
        this.gameDamageManager = new GameDamageManager(this);
        this.gameConnectManager = new GameNetworker(this);
        this.spectateManager = new GameSpectateManager(this);

        this.deathLogger = new DeathLogger();
        plugin.registerListener(deathLogger);

        plugin.getChatManager().setChatPolicy(new GameChatPolicy(this));

        setGamePhase(GamePhase.LOBBY);
        this.autoTeam = true;

        getPlugin().getScoreboardManager().setScoreboardPolicy(new ScoreboardPolicy(getPlugin()) {

            @Override
            public List<String> getSideBar(AssiPlayer player) {
                if (gamePhase != GamePhase.LOBBY) return null;
                final List<String> lines = Lists.newArrayList();

                lines.add(GC.C + "You're playing...");
                lines.add(GC.V + ChatColor.BOLD + assiGameMeta.getDisplay());

                if (gameMapManager.getSelectedWorld() != null) {
                    lines.add(GC.C + "On the map:");
                    lines.add(GC.V + gameMapManager.getSelectedWorld().getName());
                }

                if (gameStatsProvider != null) {
                    List<String> statLines = gameStatsProvider.getLobbyScoreboardLines(player.getBase());
                    if (statLines != null && !statLines.isEmpty()) {
                        lines.add(empty(1));
                        lines.addAll(statLines);
                    }
                }

                lines.add(empty(2));

                int waiting = (assiGameSettings.getMinPlayers() - UtilServer.getOnlinePlayers());
                lines.add(GC.C + "Players: " + GC.V + UtilServer.getOnlinePlayers());
                lines.add(ready() ? GC.II + "The game is now ready to start!" :
                        GC.C + "Need " + GC.V + waiting + GC.C + " more player" + (waiting == 1 ? "" : "s"));

                lines.add(empty(3));

                lines.add(GC.C + "Rank:");
                lines.add(player.getRank().getPrefix());

                GameTeam gameTeam = teamManager.getTeam(player.getBase());
                if (gameTeam != null && !gameTeam.isAutoAdd()) {
                    lines.add(empty(4));
                    lines.add(GC.C + "Team:");
                    lines.add(gameTeam.getColor() + gameTeam.getName());
                }

                lines.add(empty(5));

                lines.add(GC.V + Domain.WEB);

                return lines;
            }

            @Override
            public String getPlayerTagPrefix(AssiPlayer viewerPlayer, AssiPlayer player) {
                GameTeam team = teamManager.getTeam(player.getBase());
                if (team != null && !team.isAutoAdd()) {
                    return team.getColor().toString();
                }
                return player.getRank().getPrefix() + (player.getRank().isDefault() ? "" : " ");
            }

            @Override
            public String getPlayerTabName(AssiPlayer player) {
                GameTeam team = teamManager.getTeam(player.getBase());
                if (team != null) {
                    return team.getColor().toString() + player.getName();
                }
                return C.C + ChatColor.ITALIC + player.getName();
            }

        });

        setForeverDayLight(true);

        worldPreserver = new WorldPreserver(Bukkit.getWorlds().get(0));
        worldPreserver.setStopPlayerInteract(true);
        worldPreserver.setProtectPlayers(true);
        plugin.registerListener(worldPreserver);

        // Vamos!
        plugin.registerListener(this);
    }

    public abstract void onGameEnd();

    @EventHandler
    public final void tick(final UpdateEvent e) {
        if (e.getType() != UpdateType.SEC) return;

        if (serverTimeoutTask != -1 && UtilServer.getOnlinePlayers() != 0) {
            gamePlugin.getServer().getScheduler().cancelTask(serverTimeoutTask);
            serverTimeoutTask = -1;
        }

        if (UtilServer.getOnlinePlayers() == 0 || !ready()) {
            if (gamePhase != GamePhase.LOBBY) {
                getPlugin().getLogger().info("Server shutting down as no players. ");
                Bukkit.shutdown();
                return;
            }

            if (UtilServer.getOnlinePlayers() == 0 && serverTimeoutTask == -1) {
                serverTimeoutTask = gamePlugin.getServer().getScheduler().runTaskLater(getPlugin(), Bukkit::shutdown, 15 * 20L).getTaskId();
            }

            Bukkit.getOnlinePlayers().forEach(o -> o.setLevel(assiGameSettings.getLobbyTime()));
            counter = 0;
            return;
        }


        switch (gamePhase) {
            case LOBBY:
                Bukkit.getOnlinePlayers().forEach(o -> o.setLevel(assiGameSettings.getLobbyTime() - counter));

                if (ready()) {

                    if (counter == 0) {
                        UtilServer.broadcast(GC.II + "The game is now ready to start!");
                        UtilServer.broadcast(GC.C + "The game will be starting in " + GC.V + (assiGameSettings.getLobbyTime() - counter) + GC.C + " seconds!");
                        break;
                    }

                    if (assiGameSettings.getLobbyTime() - counter <= 3) {
                        Bukkit.getOnlinePlayers().forEach(o -> o.playSound(o.getLocation(), Sound.CLICK, 30, 20));
                    }

                    if ((assiGameSettings.getLobbyTime() - counter) % 10 == 0) {
                        if (assiGameSettings.getLobbyTime() - counter == 0) {
                            UtilServer.broadcast(GC.II + "The game warm-up is now starting!");

                            getLivePlayers().forEach(player -> {
                                player.setFlying(false);
                                player.setAllowFlight(false);
                                player.setFireTicks(0);
                            });

                        } else
                            UtilServer.broadcast(GC.C + "The Warm-up will be starting in " + GC.V + (assiGameSettings.getLobbyTime() - counter) + GC.C + " seconds!");
                    }

                    if (assiGameSettings.getLobbyTime() == counter) {
                        setGamePhase(GamePhase.WARMUP);
                        break;
                    }

                }
                break;
            case WARMUP:

                if (allDead()) {
                    setGamePhase(GamePhase.END);
                    break;
                }

                if (counter == 1 || assiGameSettings.getWarmUpTime() == 0) {
                    getPlugin().getLocalAnnouncementSetting().setAcceptingAnnouncements(false);
                    start = System.currentTimeMillis();
                    getPlugin().registerListener(deathLogger);

                    if (assiGameSettings.getWarmUpTime() != 0)
                        UtilServer.broadcast(GC.II + "The Warm-up is now starting!");
                    else setGamePhase(GamePhase.IN_GAME);
                    break;
                }

                if (assiGameSettings.getWarmUpTime() - counter <= 3) {
                    Bukkit.getOnlinePlayers().forEach(o -> o.playSound(o.getLocation(), Sound.CLICK, 30, 20));
                }

                if (assiGameSettings.getWarmUpTime() - counter == 0) {
                    Bukkit.getOnlinePlayers().forEach(o -> UtilMessage.sendHotbar(o, GC.II + "The game is now starting!"));
                } else {
                    Bukkit.getOnlinePlayers().forEach(o -> UtilMessage.sendHotbar(o, GC.C + "Warmup ending in " + GC.V +
                            String.valueOf(UtilTime.formatMinutes(assiGameSettings.getWarmUpTime() - counter, true))));
                }


                if (assiGameSettings.getWarmUpTime() == counter) {
                    setGamePhase(GamePhase.IN_GAME);
                    Bukkit.getOnlinePlayers().forEach(o -> o.setLevel(0));
                    break;
                }

                break;
            case IN_GAME:

                if (allDead()) {
                    onGameEnd();
                    setGamePhase(GamePhase.END);
                    break;
                }

                if (assiGameSettings.getMaxGameTime() == counter) {
                    UtilServer.broadcast(GC.II);
                    UtilServer.broadcast(GC.II + "The game max time has been reached! The game will now end...");
                    UtilServer.broadcast(GC.II);
                    onGameEnd();
                    setGamePhase(GamePhase.END);
                }

                break;
            case END:
                if (counter == 0) {
                    UtilServer.broadcast(GC.II + "The game has now ended! You will be sent to a Hub in " + GC.V + assiGameSettings.getEndTime() + GC.II + " seconds!");
                    break;
                }

                if (assiGameSettings.getEndTime() - 5 == counter) {
                    getPlugin().getPlayerManager().getOnlinePlayers().values().forEach(player ->
                            getPlugin().getPlayerManager().sendLobby(player, GC.II + "You will now be sent to a Hub..."));
                }

                if (assiGameSettings.getEndTime() == counter) {
                    Bukkit.shutdown();
                }
                break;
        }

        counter++;
    }

    @EventHandler
    public void onLobby(final PlayerJoinEvent e) {
        final Player player = e.getPlayer();
        e.setJoinMessage(gamePhase == GamePhase.LOBBY ? player.getDisplayName() + GC.C + " joined the lobby (" + spillerIgjen(false) + GC.C + ")" : null);

        if (gamePhase == GamePhase.LOBBY) {
            if (autoTeam && teamManager.getTeams().size() == 1 && teamManager.isTeam(GameSpectateManager.SPECTATOR_TEAM_NAME)) {
                teamManager.addTeam(new GameTeam(GameTeamManager.DEFAULT_PLAYER_TEAM, ChatColor.GRAY, true));
                teamManager.getTeam(GameTeamManager.DEFAULT_PLAYER_TEAM).add(player);
            } else {
                GameTeam team = teamManager.getDefaultTeam();
                if (team != null) {
                    team.add(player);
                }
            }

            SerializedLocation spawn = gamePlugin.getWorldManager().getPrimaryWorld().getSpawns().get("LOBBY_SPAWN");
            if (spawn != null) {
                player.teleport(spawn.toLocation());
            } else getPlugin().getLogger().warning("No lobby spawn defined!");

            AssiPlayer assiPlayer = getPlugin().getPlayerManager().getPlayer(player);

            player.setAllowFlight(assiPlayer.getRank().isHigherThanOrEqualTo(Rank.DEMONIC));
            player.setFlying(player.getAllowFlight());
            if (assiPlayer.isVanished()) {
                assiPlayer.setVanished(false);
            }
        }

        player.setWalkSpeed(0.2f);
        player.setFlySpeed(0.2f);
        player.setFoodLevel(20);
        player.setHealthScale(20);
        player.setSaturation(20);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setGameMode(GameMode.SURVIVAL);
        player.setExp(0);

    }

    @EventHandler
    public void onLobby(final PlayerQuitEvent e) {
        final Player player = e.getPlayer();
        e.setQuitMessage(gamePhase == GamePhase.LOBBY ? player.getDisplayName() + GC.C + " left the lobby (" + spillerIgjen(true) + GC.C + ")" : null);
    }

    @EventHandler
    public void on(final FoodLevelChangeEvent e) {
        if (gamePhase == GamePhase.LOBBY || gamePhase == GamePhase.WARMUP) {
            e.setFoodLevel(20);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onLobby(final PlayerPickupItemEvent e) {
        if (gamePhase != GamePhase.LOBBY) return;
        e.setCancelled(true);
    }

    private String spillerIgjen(boolean leave) {
        return GC.V + (leave ? UtilServer.getOnlinePlayers() - 1 : UtilServer.getOnlinePlayers()) + GC.C + "/" + GC.V + ((leave ? UtilServer.getOnlinePlayers() - 1 :
                UtilServer.getOnlinePlayers()) > assiGameSettings.getMinPlayers() ? assiGameSettings.getMaxPlayers() : assiGameSettings.getMinPlayers());
    }

    private boolean ready() {
        return forceStart || (getGamePhase() == GamePhase.LOBBY && UtilServer.getOnlinePlayers() >= assiGameSettings.getMinPlayers())
                || getGamePhase() != GamePhase.LOBBY;
    }

    public List<Player> getLivePlayers() {
        List<Player> players = Lists.newArrayList();
        for (Player player : Bukkit.getOnlinePlayers()) {
            GameTeam team = getTeamManager().getTeam(player);
            if (team != null && team.getName().equals(GameSpectateManager.SPECTATOR_TEAM_NAME)) continue;
            players.add(player);
        }
        return players;
    }

    public boolean allDead() {
        GameTeam spec = teamManager.getTeam(GameSpectateManager.SPECTATOR_TEAM_NAME);
        return spec != null && spec.getPlayers().size() >= UtilServer.getOnlinePlayers();
    }

    public final GamePlugin getPlugin() {
        return gamePlugin;
    }

    public final AssiGameMeta getAssiGameMeta() {
        return assiGameMeta;
    }

    public final Collection<GameModule> getGameModules() {
        return modules;
    }

    public final GameModuleHandle getGameModuleHandle() {
        return gameModuleHandle;
    }

    public GameMapManager getGameMapManager() {
        return gameMapManager;
    }

    public final GameTeamManager getTeamManager() {
        return teamManager;
    }

    public GameDamageManager getGameDamageManager() {
        return gameDamageManager;
    }

    public final GameSpectateManager getSpectateManager() {
        return spectateManager;
    }

    public GameNetworker getGameConnectManager() {
        return gameConnectManager;
    }

    public DeathLogger getDeathLogger() {
        return deathLogger;
    }

    public GameStatsProvider getGameStatsProvider() {
        return gameStatsProvider;
    }

    public void setGameStatsProvider(GameStatsProvider gameStatsProvider) {
        this.gameStatsProvider = gameStatsProvider;
    }

    public final GamePhase getGamePhase() {
        return gamePhase;
    }

    public void setGamePhase(GamePhase gamePhase) {
        if (gamePhase == this.gamePhase) return;
        if (gamePhase != GamePhase.IN_GAME)
            counter = 0;
        UtilServer.callEvent(new GamePhaseChangeEvent(this.gamePhase, gamePhase));
        this.gamePhase = gamePhase;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public AssiGameSettings getAssiGameSettings() {
        return assiGameSettings;
    }

    public boolean isAutoTeam() {
        return autoTeam;
    }

    public void setAutoTeam(boolean autoTeam) {
        this.autoTeam = autoTeam;
    }

    public boolean isForceStart() {
        return forceStart;
    }

    public void setForceStart(boolean forceStart) {
        this.forceStart = forceStart;
    }

    public boolean isForeverDayLight() {
        return foreverDayLight;
    }

    public final void setForeverDayLight(boolean foreverDayLight) {
        if (foreverDayLight && !this.foreverDayLight) {
            dayLightTask = getPlugin().getServer().getScheduler().runTaskTimer(getPlugin(), () ->
                    Bukkit.getWorlds().get(0).setTime(0), 20, 200).getTaskId();
        } else if (!foreverDayLight && this.foreverDayLight && dayLightTask != -1) {
            getPlugin().getServer().getScheduler().cancelTask(dayLightTask);
            dayLightTask = -1;
        }
        this.foreverDayLight = foreverDayLight;
    }

    public WorldPreserver getWorldPreserver() {
        return worldPreserver;
    }

    public long getStart() {
        return start;
    }

}
