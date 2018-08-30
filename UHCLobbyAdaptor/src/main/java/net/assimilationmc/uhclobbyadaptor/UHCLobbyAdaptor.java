package net.assimilationmc.uhclobbyadaptor;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.internal.ServerPing;
import net.assimilationmc.assicore.redis.pubsub.RedisChannelSubscriber;
import net.assimilationmc.assicore.redis.pubsub.RedisPubSubMessage;
import net.assimilationmc.assicore.util.*;
import net.assimilationmc.uhclobbyadaptor.achievement.AchieveFirstBlood;
import net.assimilationmc.uhclobbyadaptor.achievement.AchieveSinglesWin;
import net.assimilationmc.uhclobbyadaptor.achievement.AchieveStrongBonds;
import net.assimilationmc.uhclobbyadaptor.achievement.AchieveTeamedWin;
import net.assimilationmc.uhclobbyadaptor.cmd.*;
import net.assimilationmc.uhclobbyadaptor.items.GameCreateItem;
import net.assimilationmc.uhclobbyadaptor.items.GamePlayItem;
import net.assimilationmc.uhclobbyadaptor.leaderboard.UHCLeaderBoard;
import net.assimilationmc.uhclobbyadaptor.lib.GC;
import net.assimilationmc.uhclobbyadaptor.stats.ROUHCStatsProvider;
import net.assimilationmc.uhclobbyadaptor.stats.StatsDisplayer;
import net.assimilationmc.uhclobbyadaptor.lib.UHCGameSubType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import javax.script.Bindings;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class UHCLobbyAdaptor extends JavaPlugin implements RedisChannelSubscriber, Listener {

    private AssiPlugin assiPlugin;
    private GameCreationFactory creationFactory;
    private Map<String, UHCServer> serverMap;
    private GamePlayItem gamePlayItem;
    private GameCreateItem gameCreateItem;
    private ROUHCStatsProvider rouhcStatsProvider;
    private StatsDisplayer displayer;

    private UHCLeaderBoard uhcLeaderBoard;

    private Map<UUID, String> midgameLeavers;

    @Override
    public void onEnable() {
        assiPlugin = (AssiPlugin) getServer().getPluginManager().getPlugin("AssiCore");
        if (!assiPlugin.getServerData().isLobby()) {
            return;
        }

        this.creationFactory = new GameCreationFactory(assiPlugin);

        this.gameCreateItem = new GameCreateItem(this);
        this.assiPlugin.getJoinItemManager().addItem(gameCreateItem);
        this.gamePlayItem = new GamePlayItem(this);
        this.assiPlugin.getJoinItemManager().addItem(gamePlayItem);
        this.serverMap = Maps.newLinkedHashMap();

        assiPlugin.getRedisManager().registerChannelSubscriber("UHC", this);
        // just to get init
        getServer().getScheduler().runTaskLater(this, () -> assiPlugin.getInternalPingHandle().ping("UHC", this::convert), 80L);

        this.rouhcStatsProvider = new ROUHCStatsProvider(this);
        this.displayer = new StatsDisplayer(this);
        assiPlugin.getCommandManager().registerCommand(new CmdWebErrorLog(this), new CmdUHC(this), new CmdReconnect(this),
                new CmdToggleStats(this), new CmdStats(this));

        getServer().getPluginManager().registerEvents(this, this);

        this.uhcLeaderBoard = new UHCLeaderBoard(this);
        getServer().getPluginManager().registerEvents(uhcLeaderBoard, this);

        this.midgameLeavers = Maps.newHashMap();

        assiPlugin.getAchievementManager().addAchievement(new AchieveFirstBlood(assiPlugin));
        assiPlugin.getAchievementManager().addAchievement(new AchieveSinglesWin(assiPlugin));
        assiPlugin.getAchievementManager().addAchievement(new AchieveTeamedWin(assiPlugin));
        assiPlugin.getAchievementManager().addAchievement(new AchieveStrongBonds(this));

    }

    @Override
    public void onDisable() {
        serverMap.clear();
        creationFactory.getErrorCache().clear();
        uhcLeaderBoard.finish();
        midgameLeavers.clear();
        Bukkit.getOnlinePlayers().forEach(o -> displayer.stopPlayerTasks(o));
    }

    @Override
    public void onChannelMessage(RedisPubSubMessage redisPubSubMessage) {
        String fra = redisPubSubMessage.getFrom();

        // new server
        if (redisPubSubMessage.getSubject().equals("HELLO")) {
            serverMap.remove(fra);

            UHCServer uhcServer = new UHCServer(fra);
            uhcServer.setMaxPlayers(Integer.parseInt(redisPubSubMessage.getArgs()[0]));
            uhcServer.setMapName(redisPubSubMessage.getArgs()[1]);
            uhcServer.setGameSubType(UHCGameSubType.valueOf(redisPubSubMessage.getArgs()[2]));
            serverMap.put(fra, uhcServer);

            creationFactory.onServerStart(fra);

            // get custom data
            if (Boolean.valueOf(redisPubSubMessage.getArgs()[3])) {
                // have to delay for an unknown reason
                Bukkit.getScheduler().runTaskLater(this, () -> assiPlugin.getInternalPingHandle().ping(fra, this::convert), 10L);
            }

            Bukkit.getScheduler().runTaskLater(this, () -> {

                BaseComponent[] gameDetails = new ComponentBuilder("Game type: ").color(ChatColor.GREEN).append(uhcServer.getGameSubType().getDisplay()).color(ChatColor.GOLD)
                        .append("\n").append("Map: ").color(ChatColor.GREEN).append(uhcServer.getMapName()).color(ChatColor.GOLD).append("\n")
                        .append("Max players: ").color(ChatColor.GREEN).append(String.valueOf(uhcServer.getMaxPlayers())).color(ChatColor.GOLD).append("\n")
                        .append("Custom attributes: ").color(ChatColor.GREEN).append((Boolean.valueOf(redisPubSubMessage.getArgs()[3]) ? "See /uhc" : "None")).color(ChatColor.GOLD).create();

                BaseComponent[] newGame = new ComponentBuilder(C.SS).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, gameDetails)).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/uhc " + fra))
                        .append("New game starting at ").color(ChatColor.AQUA)
                        .append(fra).color(ChatColor.GOLD).append("!").color(ChatColor.AQUA).create();

                BaseComponent[] click = new ComponentBuilder("Click this message ").color(ChatColor.GOLD)
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, gameDetails)).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/uhc " + fra))
                        .append("to go there or").color(ChatColor.GREEN).append(" hover").color(ChatColor.GOLD).append(" to see the game details!").color(ChatColor.GREEN).create();

                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage("");
                    player.spigot().sendMessage(newGame);
                    player.spigot().sendMessage(click);
                    player.sendMessage("");
                }

            }, 15L);

            return;
        }

        // shutting down
        if (redisPubSubMessage.getSubject().equals("BYE")) {
            serverMap.remove(fra);

            Set<UUID> toRemove = Sets.newHashSet();
            for (Map.Entry<UUID, String> uuidStringEntry : midgameLeavers.entrySet()) {
                if (uuidStringEntry.getValue().equals(fra))
                    toRemove.add(uuidStringEntry.getKey());
            }

            toRemove.forEach(uuid -> midgameLeavers.remove(uuid));
            toRemove.clear();
            return;
        }

        // update
        if (redisPubSubMessage.getSubject().equals("UPDATE")) {
//            D.d("got UHC update");
            String updated = redisPubSubMessage.getArgs()[0];

            if (!serverMap.containsKey(fra)) {
//                D.d("who da fuck is" + fra + " lets find out!");
                Bukkit.getScheduler().runTaskLater(this, () -> assiPlugin.getInternalPingHandle().ping(fra, this::convert), 10L);
                return;
            }

//            D.d("that update is for us!");

            UHCServer server = serverMap.get(fra);

            switch (updated.toUpperCase()) {
                case "GAME_PHASE":
                    server.setGamePhase(redisPubSubMessage.getArgs()[1]);
                    if (server.getGamePhase().equals("WARMUP")) {
                        server.setWarmupStart(UtilTime.now());
                    }
                    break;
                case "ONLINE":
                    server.setOnline(Integer.parseInt(redisPubSubMessage.getArgs()[1]));
                    break;
                case "MAX_PLAYERS":
                    server.setMaxPlayers(Integer.parseInt(redisPubSubMessage.getArgs()[1]));
                    break;
            }

            return;
        }

        if (redisPubSubMessage.getSubject().equals("LOOK_RECONNECT")) {
            UUID uuid = UUID.fromString(redisPubSubMessage.getArgs()[0]);

            midgameLeavers.put(uuid, fra);

            final Player player = UtilPlayer.get(uuid);
            if (player != null) {
                promptReconnect(player);
            }

            return;
        }

        if (redisPubSubMessage.getSubject().equals("RECONNECTED")) {
            UUID uuid = UUID.fromString(redisPubSubMessage.getArgs()[0]);
            midgameLeavers.remove(uuid);
        }

    }

    private void convert(ServerPing serverPing) {

        serverMap.remove(serverPing.getServerId());

        UHCServer uhcServer = new UHCServer(serverPing.getServerId());
        if (serverPing.getAttribute("game_phase") == null) {
            throw new IllegalArgumentException("Invalid UHC/Game response! game_phase is null!");
        }
        uhcServer.setGamePhase(serverPing.getAttribute("game_phase"));
        uhcServer.setOnline(Integer.parseInt(serverPing.getAttribute("online")));
        uhcServer.setMaxPlayers(Integer.parseInt(serverPing.getAttribute("game_max_players")));
        uhcServer.setMapName(serverPing.getAttribute("map"));
        uhcServer.setGameSubType(UHCGameSubType.valueOf(serverPing.getAttribute("game_type")));
        if (serverPing.getAttributes().containsKey("start")) {
            uhcServer.setWarmupStart(Long.parseLong(serverPing.getAttribute("start")));
        }

        if (serverPing.getAttributes().containsKey("custom")) {
            uhcServer.getCustomAttributes().putAll(UtilJson.deserialize(new Gson(), serverPing.getAttribute("custom")));
        }

        serverMap.put(serverPing.getServerId(), uhcServer);
    }

    private void promptReconnect(Player player) {
        player.sendMessage(C.C);
        player.spigot().sendMessage(new ComponentBuilder("It looks like you left a game before the party was over!").color(ChatColor.RED).bold(true)
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/reconnect")).create());
        player.spigot().sendMessage(new ComponentBuilder("Click this message to reconnect.").color(ChatColor.GREEN).bold(true)
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/reconnect")).create());
        player.sendMessage(C.C + ChatColor.ITALIC + "Or do /reconnect");
        player.sendMessage(C.C);
    }

    @EventHandler
    public void on(final PlayerJoinEvent e) {
        final Player player = e.getPlayer();
        if (midgameLeavers.containsKey(player.getUniqueId())) {
            promptReconnect(player);
            return;
        }

        if (!serverMap.isEmpty())
            player.sendMessage(C.SS + ChatColor.GREEN + "There are currently " + C.V + serverMap.size() + ChatColor.GREEN + " games running." +
                    " If you're looking for people that is where they are! Right click your " + ChatColor.AQUA + ChatColor.BOLD + "Diamond Sword"
                    + ChatColor.GREEN + " to find them.");
    }

    public Map<UUID, String> getMidgameLeavers() {
        return midgameLeavers;
    }

    public AssiPlugin getAssiPlugin() {
        return assiPlugin;
    }

    public GameCreationFactory getCreationFactory() {
        return creationFactory;
    }

    public Map<String, UHCServer> getServerMap() {
        return serverMap;
    }

    public GameCreateItem getGameCreateItem() {
        return gameCreateItem;
    }

    public GamePlayItem getGamePlayItem() {
        return gamePlayItem;
    }

    public ROUHCStatsProvider getROuhcStatsProvider() {
        return rouhcStatsProvider;
    }

    public StatsDisplayer getDisplayer() {
        return displayer;
    }

}
