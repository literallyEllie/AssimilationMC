package net.assimilationmc.assiuhc.game;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.event.update.UpdateEvent;
import net.assimilationmc.assicore.event.update.UpdateType;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.scoreboard.ScoreboardPolicy;
import net.assimilationmc.assicore.util.*;
import net.assimilationmc.assicore.world.WorldData;
import net.assimilationmc.assiuhc.chat.CmdChatMode;
import net.assimilationmc.assiuhc.chat.UHCTeamChatPolicy;
import net.assimilationmc.assiuhc.event.TeamedPlayerWinEvent;
import net.assimilationmc.assiuhc.player.UHCPlayer;
import net.assimilationmc.assiuhc.reward.XPRewards;
import net.assimilationmc.assiuhc.team.UHCTeamManager;
import net.assimilationmc.assiuhc.team.reconnect.TeamReconnectManager;
import net.assimilationmc.gameapi.GamePlugin;
import net.assimilationmc.gameapi.game.AssiGameMeta;
import net.assimilationmc.gameapi.phase.GamePhase;
import net.assimilationmc.gameapi.phase.GamePhaseChangeEvent;
import net.assimilationmc.gameapi.spectate.GameSpectateManager;
import net.assimilationmc.gameapi.team.GameTeam;
import net.assimilationmc.gameapi.util.GC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public abstract class UHCTeamedGame extends UHCGame {

    private UHCTeamManager uhcTeamManager;
    private TeamReconnectManager teamReconnectManager;
    private UHCTeamChatPolicy uhcTeamChatPolicy;
    private GameTeam winner;

    public UHCTeamedGame(GamePlugin plugin, AssiGameMeta assiGameMeta) {
        super(plugin, assiGameMeta);

        setAutoTeam(false);

        this.uhcTeamManager = new UHCTeamManager(this);
        this.teamReconnectManager = new TeamReconnectManager(this);
        this.uhcTeamChatPolicy = new UHCTeamChatPolicy(this);

        plugin.getChatManager().setChatPolicy(uhcTeamChatPolicy);
        getTeamManager().setAllowTeamReconnect(true);

        plugin.getCommandManager().registerCommand(new CmdChatMode(this));
    }

    @EventHandler
    public void on(final GamePhaseChangeEvent e) {
        if (e.getTo() == GamePhase.WARMUP) {
            uhcTeamChatPolicy.getGlobalChat().clear();
            getLivePlayers().forEach(player -> {
                player.getInventory().setArmorContents(null);
                player.getInventory().clear();
                player.setGameMode(GameMode.SURVIVAL);
            });

            getLivePlayers().forEach(player -> player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, getAssiGameSettings().getWarmUpTime() * 20, 3)));

            teleportAll();
        }

        if (e.getTo() == GamePhase.WARMUP || e.getTo() == GamePhase.IN_GAME) {
            getPlugin().getScoreboardManager().setScoreboardPolicy(new ScoreboardPolicy(getPlugin()) {

                @Override
                public List<String> getSideBar(AssiPlayer player) {
                    List<String> lines = Lists.newArrayList();
                    lines.add(empty(0));
                    lines.add(GC.C + "Remaining: " + GC.V + getLivePlayers().size());
                    lines.add(GC.C + "Remaining teams: " + GC.V + getUHCTeamManager().getRemainingTeams().size());

                    int teamKills = 0;

                    GameTeam team = getTeamManager().getTeam(player.getBase());
                    if (team != null) {
                        if (team.getName().equals(GameSpectateManager.SPECTATOR_TEAM_NAME)) {
                            team = getDeathLogger().getOldTeam(player.getBase());
                        }

                        if (team != null) {

                            lines.add(empty(1));
                            lines.add(team.getColor() + team.getName());
                            lines.add(GC.C + "Alive: " + GC.V + getUHCTeamManager().getLivePlayersOf(team).size());

                            for (UUID uuid : team.getPlayers()) {
                                teamKills += getDeathLogger().getKills().getOrDefault(uuid, 0);
                            }

                        }

                    }

                    lines.add(empty(2));
                    lines.add(GC.C + "Your kills: " + GC.V + getDeathLogger().getKillsOf(player.getBase()));
                    lines.add(GC.C + "Team kills: " + GC.V + teamKills);

                    lines.add(empty(3));

                    if (getGameBorder() != null && getGameBorder().getWorldBorder() != null) {
                        lines.add(GC.C + "Border size: " + GC.V + (int) Math.ceil(getGameBorder().getWorldBorder().getSize()));
                    }

                    lines.add(GC.C + "Game ends in...");
                    lines.add(GC.V + UtilTime.formatMinutes(getAssiGameSettings().getMaxGameTime() - getCounter(), false));
                    lines.add(empty(5));


                    lines.add(GC.V + Domain.WEB);

                    return lines;
                }

                @Override
                public String getPlayerTagPrefix(AssiPlayer viewerPlayer, AssiPlayer player) {
                    GameTeam team = getTeamManager().getTeam(player.getBase());
                    return team != null ? team.getColor() + team.getName() + " " : C.C;
                }

                @Override
                public String getPlayerTabName(AssiPlayer player) {
                    GameTeam team = getTeamManager().getTeam(player.getBase());
                    return (team != null ? team.getColor() : C.C) + player.getName();
                }
            });
        }

        if (e.getTo() == GamePhase.END) {
            getPlugin().getScoreboardManager().setScoreboardPolicy(new ScoreboardPolicy(getPlugin()) {

                private List<String> winners;

                private List<String> getWinners() {
                    if (winners != null) {
                        return winners;
                    }

                    winners = Lists.newArrayList();
                    final GameTeam team = getWinner();

                    if (team == null)
                        return winners;

                    winners.add(ChatColor.BOLD + team.getColor().toString() + team.getName());

                    StringBuilder currLine = new StringBuilder();
                    int index = 0;

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        final GameTeam oldTeam = getDeathLogger().getOldTeam(player);
                        if (oldTeam != null && oldTeam == winner) {
                            team.getPlayers().add(player.getUniqueId());
                        }
                    }

                    for (UUID uuid : team.getPlayers()) {
                        index++;
                        Player mem = UtilPlayer.get(uuid);
                        if (mem == null) continue;
                        if (currLine.toString().toCharArray().length + mem.getName().toCharArray().length > 16) {
                            winners.add(currLine.toString());
                            currLine = new StringBuilder(ChatColor.GREEN + mem.getName() + GC.II + ", ");
                            continue;
                        }

                        currLine.append(GC.V).append(mem.getName());

                        if (team.getPlayers().size() - index >= 1) {
                            currLine.append(GC.II).append(", ").append(ChatColor.GREEN);
                        }

                        D.d(3);
                    }

                    winners.add(currLine.toString());

                    D.d(4);
                    return winners;
                }

                @Override
                public List<String> getSideBar(AssiPlayer player) {
                    List<String> lines = Lists.newArrayList();
                    lines.add(empty(0));
                    lines.add(ChatColor.GREEN + "Winner:");
                    GameTeam team = getWinner();
                    if (team == null) {
                        lines.add(GC.II + "No winner!");
                    } else {
                        lines.addAll(getWinners());
                    }

                    lines.add(empty(1));
                    lines.add(GC.V + Domain.WEB);

                    return lines;
                }

                @Override
                public String getPlayerTagPrefix(AssiPlayer viewerPlayer, AssiPlayer player) {
                    GameTeam team = getTeamManager().getTeam(player.getBase());
                    return team != null ? team.getColor() + team.getName() + " " : C.C;
                }

                @Override
                public String getPlayerTabName(AssiPlayer player) {
                    GameTeam team = getTeamManager().getTeam(player.getBase());
                    return (team != null ? team.getColor() : C.C) + player.getName();
                }

            });

        }

    }

    public abstract GameTeam electWinner();

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

            if (getGamePhase() == GamePhase.IN_GAME && e.getType() == UpdateType.SEC && !isDeathmatch()) {
                if (!callDeathMatch()) return;

            }
        }
    }

    public final UHCTeamManager getUHCTeamManager() {
        return uhcTeamManager;
    }

    public final TeamReconnectManager getTeamReconnectManager() {
        return teamReconnectManager;
    }

    public final UHCTeamChatPolicy getTeamChatPolicy() {
        return uhcTeamChatPolicy;
    }

    public GameTeam getWinner() {
        return winner;
    }

    @Override
    public void onGameEnd() {
        final GameTeam winner = getWinner();

        UtilServer.broadcast("");
        UtilServer.broadcast("");

        if (winner != null) {
            // server bc

            List<Player> winners = winner.getPlayers().stream().filter(uuid -> UtilPlayer.get(uuid) != null).map(UtilPlayer::get).collect(Collectors.toList());

            UtilServer.broadcast(GC.II + "The team " + winner.getColor() + winner.getName() + GC.II + " won the game!");
            UtilServer.broadcast(GC.C + "Winners: " + GC.V + Joiner.on(C.C + ", " + GC.V).join(winners.stream().map(Player::getName).collect(Collectors.toList())));
            UtilServer.broadcast("");
            UtilServer.broadcast("");

            winner.message(C.II + ChatColor.BOLD + "Congratulations! You have won!");

            // personal msg

            for (UUID uuid : winner.getPlayers()) {
                Player player = UtilPlayer.get(uuid);
                if (player == null) return;
                UtilFirework.spawnRandomFirework(player.getLocation());

                for (Location location : UtilMath.generateCircmerfence(player.getLocation(), 10)) {
                    UtilFirework.spawnRandomFirework(location);
                }
            }

        } else {
            UtilServer.broadcast(GC.II + "There was no winning team!");
            UtilServer.broadcast("");
            UtilServer.broadcast("");
        }

        D.d("looping players");
        for (Player player : Bukkit.getOnlinePlayers()) {
            GameTeam oldTeam = getDeathLogger().getOldTeam(player);
            if (oldTeam == null)
                oldTeam = getTeamManager().getTeam(player);

            if (oldTeam != null) {
                int xpToGive = XPRewards.PARTICIPATE + getDeathLogger().getKillsOf(player);
                int bucksToGive = 0;
                int ucToGive = 0;

                UHCPlayer uhcPlayer = getPlayerManager().getPlayer(player);
                AssiPlayer assiPlayer = getPlugin().getPlayerManager().getPlayer(player);
                if (winner != null && oldTeam.getName().equals(winner.getName())) {
                    D.d("given game won to " + assiPlayer.getName());
                    uhcPlayer.addGameWon(getGameSubType());

                    if (getRewarded().contains(player.getUniqueId())) {
                        getXpManager().giveXP(uhcPlayer, winnerXp());
                    } else {
                        xpToGive += winnerXp();
                        bucksToGive += BuckRewards.GENERIC_UHC_WIN + winnerBucks();
                        ucToGive += winnerUC();
                    }

                    UtilServer.callEvent(new TeamedPlayerWinEvent(player, assiPlayer));
                }

                if (!getRewarded().contains(player.getUniqueId())) {
                    getXpManager().giveXP(uhcPlayer, xpToGive);
                    if (bucksToGive > 0) {
                        getPlugin().getRewardManager().giveBucks(assiPlayer, bucksToGive);
                    }
                    if (ucToGive > 0) {
                        getPlugin().getRewardManager().giveUltraCoins(assiPlayer, ucToGive);
                    }
                    uhcPlayer.addGamePlayed(getGameSubType());
                }

                player.sendMessage(ChatColor.GOLD + "Thanks for playing, come again! :D");
            }
        }

    }

    private void teleportAll() {
        WorldData map = getGameMapManager().getSelectedWorld();
        if (map == null) return;

        int teamIndex = 0;
        for (GameTeam team : getUHCTeamManager().getRemainingTeams()) {
            // D.d("team " + team.getName());
            int spawnIndex = 0;
            // D.d("spawn index =" +  spawnIndex);
            for (int p = 0; p < team.getPlayers().size(); p++) {
                // D.d("player " + p);
                SerializedLocation location = map.getSpawns().get("T_" + teamIndex + "_S_" + spawnIndex);
                if (location == null && spawnIndex > 0) {
                    spawnIndex = 0;
                    location = map.getSpawns().get("T_" + teamIndex + "_S_" + spawnIndex);
                    if (location == null) continue;
                } else if (location == null && spawnIndex == 0) {
                    team.message(GC.II + "Your team spawns have been incorrectly setup. Please contact a member of staff.");
                    break;
                }
                final Location spawn = location.toLocation();
                Player player = UtilPlayer.get(team.getPlayers().get(p));
                if (player == null) continue;

                player.teleport(spawn);
                player.setHealth(player.getMaxHealth());

                spawnIndex++;
            }

            teamIndex++;
        }

        // teleport spectators
        SerializedLocation location = map.getSpawns().get(GameSpectateManager.SPECTATOR_SPAWN);
        if (location != null) {
            final Location toLocation = location.toLocation();
            for (UUID uuid : getSpectateManager().getSpectatorTeam().getPlayers()) {
                final Player player = UtilPlayer.get(uuid);
                player.teleport(toLocation);
            }
        }
    }



}
