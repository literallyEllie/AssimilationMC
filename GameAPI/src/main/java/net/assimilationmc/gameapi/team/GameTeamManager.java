package net.assimilationmc.gameapi.team;

import com.google.common.collect.Maps;
import net.assimilationmc.assicore.redis.pubsub.PubSubRecipient;
import net.assimilationmc.assicore.redis.pubsub.RedisPubSubMessage;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.SerializedLocation;
import net.assimilationmc.assicore.util.UtilPlayer;
import net.assimilationmc.assicore.util.UtilServer;
import net.assimilationmc.gameapi.game.AssiGame;
import net.assimilationmc.gameapi.module.GameModule;
import net.assimilationmc.gameapi.module.ModuleActivePolicy;
import net.assimilationmc.gameapi.phase.GamePhase;
import net.assimilationmc.gameapi.spectate.GameSpectateManager;
import net.assimilationmc.gameapi.util.GC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class GameTeamManager extends GameModule {

    public static final String DEFAULT_PLAYER_TEAM = "Players";

    private Map<String, GameTeam> teams;
    private Map<UUID, String> teamReconnects;

    private boolean allowTeamReconnect;
    private TeamPlayerDisconnectSubscriber disconnectSubscriber;

    public GameTeamManager(AssiGame game) {
        super(game, "Team Manager", ModuleActivePolicy.PERMANENT);
    }

    @Override
    public void start() {
        this.teams = Maps.newHashMap();
        this.teamReconnects = Maps.newHashMap();
    }

    @Override
    public void end() {
        teams.values().forEach(team -> team.getPlayers().clear());
        teams.clear();
        teamReconnects.clear();
    }

    @EventHandler
    public void on(final PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (getAssiGame().getGamePhase() == GamePhase.LOBBY) return;

        if (!teamReconnects.isEmpty() && teamReconnects.containsKey(player.getUniqueId())
                && !getAssiGame().getPlugin().getServerData().isLocal()) {

            getAssiGame().getPlugin().getRedisManager().sendPubSubMessage("UHC",
                    new RedisPubSubMessage(PubSubRecipient.SPIGOT, getAssiGame().getPlugin().getServerData().getId(), "RECONNECTED",
                            new String[]{player.getUniqueId().toString()}));
        }

        if (allowTeamReconnect && teamReconnects.containsKey(player.getUniqueId())) {
            GameTeam team = getTeam(teamReconnects.get(player.getUniqueId()));

            if (team != null && !team.getPlayers().isEmpty()) {
                team.add(player);
                player.sendMessage(GC.C + "You have joined the team " + team.getColor() + team.getName());

                if (team.isAutoAdd())
                UtilServer.broadcast((team.isAutoAdd() ? team.getColor() +  team.getName() : ChatColor.GREEN.toString())
                        + " " + C.V + player.getName() + ChatColor.DARK_RED + " has reconnected.");

            } else {
                player.sendMessage(GC.II + "Couldn't find your old team, it may have been knocked out.");
                getAssiGame().getSpectateManager().setSpectator(player);

                SerializedLocation location = getAssiGame().getGameMapManager().getSelectedWorld().getSpawns().get(GameSpectateManager.SPECTATOR_SPAWN);
                if (location != null)
                    player.teleport(location.toLocation());
            }

            teamReconnects.remove(player.getUniqueId());

        } else {
            player.sendMessage(GC.C + "The game is currently in progress, you will now be set as a Spectator.");
            getAssiGame().getSpectateManager().setSpectator(player);

            SerializedLocation location = getAssiGame().getGameMapManager().getSelectedWorld().getSpawns().get(GameSpectateManager.SPECTATOR_SPAWN);
            if (location != null)
                player.teleport(location.toLocation());

        }

        GameTeam joinTeam = getTeam(player);

        for (GameTeam gameTeam : teams.values()) {
            if (gameTeam.isHidden() && joinTeam != gameTeam) {
                for (UUID uuid : gameTeam.getPlayers()) {
                    Player p = UtilPlayer.get(uuid);
                    if (p == null) continue;
                    player.hidePlayer(p);
                }
            }
        }

        teamReconnects.remove(player.getUniqueId());
    }

    @EventHandler
    public void on(final PlayerQuitEvent e) {
        if (getAssiGame().getGamePhase() == GamePhase.END) return;
        Player player = e.getPlayer();
        GameTeam team = getTeam(player);
        if (team == null) return;

        if (team.getName().equals(GameSpectateManager.SPECTATOR_TEAM_NAME)) return;

        if (allowTeamReconnect) {
            teamReconnects.put(player.getUniqueId(), team.getName());

            if (!getAssiGame().getPlugin().getServerData().isLocal()) {
                getAssiGame().getPlugin().getRedisManager().sendPubSubMessage("UHC",
                        new RedisPubSubMessage(PubSubRecipient.SPIGOT, getAssiGame().getPlugin().getServerData().getId(), "LOOK_RECONNECT",
                                new String[]{player.getUniqueId().toString()}));
            }

            if (disconnectSubscriber != null) {
                try {
                    disconnectSubscriber.onDisconnect(player, team);
                } catch (Throwable ex) {
                    log(Level.SEVERE, "Error when running disconnect handler.");
                    ex.printStackTrace();
                }
            }
        }

        team.remove(player);
    }

    public Map<String, GameTeam> getTeams() {
        return Collections.unmodifiableMap(teams);
    }

    public GameTeam getTeam(String name) {
        return teams.get(name.toLowerCase());
    }

    public GameTeam getTeam(Player player) {
        for (GameTeam gameTeam : teams.values()) {
            if (gameTeam.getPlayers().contains(player.getUniqueId()))
                return gameTeam;
        }
        return null;
    }

    public GameTeam getDefaultTeam() {
        for (GameTeam gameTeam : teams.values()) {
            if (gameTeam.isAutoAdd())
                return gameTeam;
        }
        return null;
    }

    public GameTeam getMostEmptyTeam() {
        GameTeam lowestTeam = null;
        int lowest = 0;
        for (GameTeam team : teams.values()) {
            if (team.getName().equals(GameSpectateManager.SPECTATOR_TEAM_NAME)) continue;
            if (lowestTeam == null) {
                lowestTeam = team;
                lowest = team.getPlayers().size();
                continue;
            }
            if (team.getPlayers().size() < lowest) {
                lowestTeam = team;
                lowest = team.getPlayers().size();
            }
        }
        return lowestTeam;
    }

    public void removeFromAnyTeam(Player player) {
        for (GameTeam team : teams.values()) {
            if (team.contains(player))
                team.remove(player);
        }
    }

    public boolean hasTeam(Player player) {
        return getTeam(player) != null;
    }

    public Set<Player> getTeamless() {
        return getAssiGame().getLivePlayers().stream().filter(player -> !getAssiGame().getTeamManager().hasTeam(player))
                .collect(Collectors.toSet());
    }

    public boolean addTeam(String name, ChatColor chatColor) {
        return addTeam(new GameTeam(name, chatColor));
    }

    public boolean addTeam(GameTeam gameTeam) {
        if (teams.containsKey(gameTeam.getName())) return false;
        teams.put(gameTeam.getName().toLowerCase(), gameTeam);
        return true;
    }

    public boolean removeTeam(String name) {
        if (!teams.containsKey(name.toLowerCase())) return false;
        teams.remove(name.toLowerCase());
        return true;
    }

    public boolean removeTeam(GameTeam team) {
        return removeTeam(team.getName());
    }

    public boolean isTeam(String name) {
        return teams.containsKey(name.toLowerCase());
    }

    public boolean isAllowTeamReconnect() {
        return allowTeamReconnect;
    }

    public void setAllowTeamReconnect(boolean allowTeamReconnect) {
        this.allowTeamReconnect = allowTeamReconnect;
    }

    public TeamPlayerDisconnectSubscriber getDisconnectSubscriber() {
        return disconnectSubscriber;
    }

    public void setDisconnectSubscriber(TeamPlayerDisconnectSubscriber disconnectSubscriber) {
        this.disconnectSubscriber = disconnectSubscriber;
    }

}
