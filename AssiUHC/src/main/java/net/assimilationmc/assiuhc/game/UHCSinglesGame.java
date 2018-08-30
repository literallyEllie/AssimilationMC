package net.assimilationmc.assiuhc.game;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.assimilationmc.assicore.achievement.Achievement;
import net.assimilationmc.assicore.event.update.UpdateEvent;
import net.assimilationmc.assicore.event.update.UpdateType;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.redis.pubsub.PubSubRecipient;
import net.assimilationmc.assicore.redis.pubsub.RedisPubSubMessage;
import net.assimilationmc.assicore.scoreboard.ScoreboardPolicy;
import net.assimilationmc.assicore.util.*;
import net.assimilationmc.assicore.world.WorldData;
import net.assimilationmc.assiuhc.event.SinglesWinEvent;
import net.assimilationmc.assiuhc.player.UHCPlayer;
import net.assimilationmc.assiuhc.reward.XPRewards;
import net.assimilationmc.assiuhc.team.reconnect.ReconnectData;
import net.assimilationmc.assiuhc.team.reconnect.TeamReconnectManager;
import net.assimilationmc.gameapi.GamePlugin;
import net.assimilationmc.gameapi.game.AssiGameMeta;
import net.assimilationmc.gameapi.phase.GamePhase;
import net.assimilationmc.gameapi.phase.GamePhaseChangeEvent;
import net.assimilationmc.gameapi.spectate.GameSpectateManager;
import net.assimilationmc.gameapi.team.GameTeam;
import net.assimilationmc.gameapi.team.TeamPlayerDisconnectSubscriber;
import net.assimilationmc.gameapi.util.GC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public abstract class UHCSinglesGame extends UHCGame implements TeamPlayerDisconnectSubscriber {

    private Player winner;

    private Map<UUID, ReconnectData> reconnectDataMap;
    private Map<UUID, Integer> reconnectTimeout;

    public UHCSinglesGame(GamePlugin plugin, AssiGameMeta assiGameMeta) {
        super(plugin, assiGameMeta);

        this.reconnectDataMap = Maps.newHashMap();
        this.reconnectTimeout = Maps.newHashMap();

        setAutoTeam(true);
        getTeamManager().setAllowTeamReconnect(true);
        getTeamManager().setDisconnectSubscriber(this);

        getAssiGameSettings().setFriendlyFire(true);
    }

    @EventHandler
    public void on(final GamePhaseChangeEvent e) {
        if (e.getTo() == GamePhase.WARMUP) {
            getLivePlayers().forEach(player -> {
                player.getInventory().setArmorContents(null);
                player.getInventory().clear();
                player.setGameMode(GameMode.SURVIVAL);
            });
            teleportAllTo("S_", GameSpectateManager.SPECTATOR_SPAWN);
        }

        if (e.getTo() == GamePhase.WARMUP || e.getTo() == GamePhase.IN_GAME) {

            getPlugin().getScoreboardManager().setScoreboardPolicy(new ScoreboardPolicy(getPlugin()) {

                @Override
                public List<String> getSideBar(AssiPlayer player) {
                    List<String> lines = Lists.newArrayList();

                    lines.add(empty(0));
                    List<Player> remainingPlayers = getLivePlayers();

                    lines.add(GC.C + "Remaining: " + GC.V + remainingPlayers.size());
                    lines.add(empty(1));
                    lines.add(GC.C + "Your kills: " + GC.V + getDeathLogger().getKillsOf(player.getBase()));
                    lines.add(empty(2));

                    if (getGameBorder() != null) {
                        lines.add(GC.C + "Border size: " + GC.V + (int) Math.ceil(getGameBorder().getWorldBorder().getSize()));
                    }

                    lines.add(GC.C + "Game ends in...");
                    lines.add(GC.V + UtilTime.formatMinutes(getAssiGameSettings().getMaxGameTime() - getCounter(), false));
                    lines.add(empty(5));

                    lines.add(GC.V + Domain.WEB);

                    return lines;
                }

                @Override
                public String getPlayerTabName(AssiPlayer player) {
                    GameTeam team = getTeamManager().getTeam(player.getBase());
                    if (team == getTeamManager().getDefaultTeam()) {
                        return ChatColor.GREEN + player.getName();
                    }
                    return ChatColor.GRAY + player.getName();
                }

                @Override
                public String getPlayerTagPrefix(AssiPlayer viewerPlayer, AssiPlayer player) {
                    GameTeam team = getTeamManager().getTeam(player.getBase());
                    if (team == getTeamManager().getDefaultTeam()) {
                        return ChatColor.GREEN.toString();
                    }
                    return ChatColor.GRAY.toString();
                }

            });

            if (e.getTo() == GamePhase.WARMUP) {
                getLivePlayers().forEach(player -> player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, getAssiGameSettings().getWarmUpTime() * 20, 3)));
            }

        }

        if (e.getTo() == GamePhase.END) {
            getPlugin().getScoreboardManager().setScoreboardPolicy(new ScoreboardPolicy(getPlugin()) {

                @Override
                public List<String> getSideBar(AssiPlayer player) {
                    List<String> lines = Lists.newArrayList();
                    lines.add(empty(0));
                    lines.add(GC.II + "Winner:");
                    if (winner != null) {
                        lines.add(ChatColor.GREEN + winner.getName());
                    }

                    lines.add(empty(2));
                    lines.add(GC.V + Domain.WEB);

                    return lines;
                }

                @Override
                public String getPlayerTabName(AssiPlayer player) {
                    GameTeam team = getTeamManager().getTeam(player.getBase());
                    if (team == getTeamManager().getDefaultTeam()) {
                        return ChatColor.GREEN + player.getName();
                    }
                    return ChatColor.GRAY + player.getName();
                }

                @Override
                public String getPlayerTagPrefix(AssiPlayer viewerPlayer, AssiPlayer player) {
                    GameTeam team = getTeamManager().getTeam(player.getBase());
                    if (team == getTeamManager().getDefaultTeam()) {
                        return ChatColor.GREEN.toString();
                    }
                    return ChatColor.GRAY.toString();
                }

            });

        }

    }

    @EventHandler
    public void winDetect(final UpdateEvent e) {
        if (getGamePhase() == GamePhase.IN_GAME || getGamePhase() == GamePhase.WARMUP) {
            if (e.getType() == UpdateType.TWO_SEC) {
                winner = electWinner();
                if (winner != null) {
                    onGameEnd();
                    setGamePhase(GamePhase.END);
                }
            }

        }


    }

    /**
     * Method to teleport all live players to a prefixed spawn which should be
     * predefined in the game world. This method also has the ability to teleport
     * spectators however this can be avoided by setting spectatorSpawnName to null.
     *
     * @param spawnPrefix        The spawn prefix.
     * @param spectatorSpawnName Where to send specators. Can be null.
     */
    public void teleportAllTo(String spawnPrefix, String spectatorSpawnName) {
        if (spectatorSpawnName == null) return;
        final WorldData map = getGameMapManager().getSelectedWorld();
        if (map == null) return;

        int spawnIndex = 0;
        for (Player player : getLivePlayers()) {
            SerializedLocation location = map.getSpawns().get(spawnPrefix + spawnIndex);
            if (location == null && spawnIndex > 0) {
                spawnIndex = 0;
                location = map.getSpawns().get(spawnPrefix + spawnIndex);
                if (location == null) {
                    spawnIndex++;
                    continue;
                }
            } else if (location == null && spawnIndex == 0) {
                player.sendMessage(GC.II + "The game spawns have been incorrectly setup.");
                break;
            }
            final Location spawn = location.toLocation();
            player.teleport(spawn);
            player.setHealth(player.getMaxHealth());
            spawnIndex++;
        }

        // teleport spectators
        SerializedLocation location = map.getSpawns().get(spectatorSpawnName);
        if (location != null) {
            final Location toLocation = location.toLocation();
            for (UUID uuid : getSpectateManager().getSpectatorTeam().getPlayers()) {
                final Player player = UtilPlayer.get(uuid);
                player.teleport(toLocation);
            }
        }

    }

    public abstract Player electWinner();

    public Player getWinner() {
        return winner;
    }

    @Override
    public void onGameEnd() {
        final Player winner = getWinner();

        UtilServer.broadcast("");
        UtilServer.broadcast("");

        if (winner != null) {
            // server bc
            UtilServer.broadcast(winner.getDisplayName() + GC.II + " has won the game!");
            UtilServer.broadcast("");
            UtilServer.broadcast("");

            // personal msg
            winner.sendMessage(GC.II + ChatColor.BOLD + "Congratulations! You have won!");
            UtilFirework.spawnRandomFirework(winner.getLocation());

            for (Location location : UtilMath.generateCircmerfence(winner.getLocation(), 10)) {
                UtilFirework.spawnRandomFirework(location);
            }

        } else {
            UtilServer.broadcast(GC.II + "There was no winner!");
            UtilServer.broadcast("");
            UtilServer.broadcast("");
        }

        // Give rewards
        for (Player player : Bukkit.getOnlinePlayers()) {
            GameTeam oldTeam = getDeathLogger().getOldTeam(player);
            if (oldTeam != null && !oldTeam.getName().equals(GameSpectateManager.SPECTATOR_TEAM_NAME)) continue;

            UHCPlayer uhcPlayer = getPlayerManager().getPlayer(player);

            int xpToGive = XPRewards.PARTICIPATE + getDeathLogger().getKillsOf(player);
            int bucksToGive = 0;
            int ucToGive = 0;

            if (winner != null && player.getUniqueId().equals(winner.getUniqueId())) {
                // D.d("winner found, adding game win");
                uhcPlayer.addGameWon(getGameSubType());
                xpToGive += winnerXp();
                bucksToGive += winnerBucks();
                ucToGive += winnerUC();

                UtilServer.callEvent(new SinglesWinEvent(player, uhcPlayer));
            }

            if (!getRewarded().contains(player.getUniqueId())) {
                AssiPlayer assiPlayer = getPlugin().getPlayerManager().getPlayer(player);
                getXpManager().giveXP(uhcPlayer, xpToGive);
                if (bucksToGive > 0) {
                    getPlugin().getRewardManager().giveBucks(assiPlayer, bucksToGive);
                }
                if (ucToGive > 0) {
                    getPlugin().getRewardManager().giveUltraCoins(assiPlayer, ucToGive);
                }
                uhcPlayer.addGamePlayed(getGameSubType());
            }
        }

    }

    @EventHandler
    public void onReconnect(final PlayerJoinEvent e) {
        final Player player = e.getPlayer();
        if (getGamePhase() == GamePhase.END || !reconnectDataMap.containsKey(player.getUniqueId())) return;

        ReconnectData reconnectData = reconnectDataMap.get(player.getUniqueId());

        player.getInventory().clear();

        player.getInventory().setContents(reconnectData.getPlayerInventory().getContents());
        player.getInventory().setArmorContents(reconnectData.getPlayerInventory().getArmorContents());

        player.teleport(reconnectData.getLocation());

        reconnectDataMap.remove(player.getUniqueId());

        Bukkit.getScheduler().cancelTask(reconnectTimeout.get(player.getUniqueId()));
        reconnectTimeout.remove(player.getUniqueId());

        getPlugin().getRedisManager().sendPubSubMessage("UHC",
                new RedisPubSubMessage(PubSubRecipient.SPIGOT, getPlugin().getServerData().getId(), "RECONNECTED",
                        new String[]{player.getUniqueId().toString()}));

    }

    @Override
    public void onDisconnect(Player player, GameTeam team) {
        if (team == null || team.getName().equals(GameSpectateManager.SPECTATOR_TEAM_NAME) || getGamePhase() == GamePhase.END) return;

        UtilServer.broadcast(team.getColor() + player.getName() + ChatColor.DARK_RED + " has disconnected! They have 5 minutes to return.");

        reconnectDataMap.put(player.getUniqueId(), new ReconnectData(player.getUniqueId(), player.getName(), player.getLocation(), player.getInventory()));
        this.reconnectTimeout.put(player.getUniqueId(), Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            ReconnectData reconnectData = reconnectDataMap.get(player.getUniqueId());
            if (reconnectData == null) return;

            UtilServer.broadcast(C.II + ChatColor.BOLD + reconnectData.getName() + " has been removed from the game for not connecting back.");

            reconnectDataMap.remove(reconnectData.getUuid());
            reconnectTimeout.remove(reconnectData.getUuid());

            getRewarded().add(reconnectData.getUuid());

        }, 5 * 20 * 60).getTaskId());

        if (!getPlugin().getServerData().isLocal()) {
            getPlugin().getRedisManager().sendPubSubMessage("UHC",
                    new RedisPubSubMessage(PubSubRecipient.SPIGOT, getPlugin().getServerData().getId(), "LOOK_RECONNECT",
                            new String[]{player.getUniqueId().toString()}));
        }
    }

}
