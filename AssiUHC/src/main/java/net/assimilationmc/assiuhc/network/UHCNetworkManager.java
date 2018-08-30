package net.assimilationmc.assiuhc.network;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.event.GamePingEvent;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.punish.PunishProfile;
import net.assimilationmc.assicore.punish.model.PunishmentCategory;
import net.assimilationmc.assicore.punish.model.PunishmentData;
import net.assimilationmc.assicore.punish.model.PunishmentOutcome;
import net.assimilationmc.assicore.redis.pubsub.PubSubRecipient;
import net.assimilationmc.assicore.redis.pubsub.RedisChannelSubscriber;
import net.assimilationmc.assicore.redis.pubsub.RedisPubSubMessage;
import net.assimilationmc.assicore.util.*;
import net.assimilationmc.assicore.web.request.RequestMethod;
import net.assimilationmc.assicore.web.request.WebEndpoint;
import net.assimilationmc.assiuhc.event.SinglesWinEvent;
import net.assimilationmc.assiuhc.event.TeamedPlayerWinEvent;
import net.assimilationmc.assiuhc.game.UHCGame;
import net.assimilationmc.assiuhc.network.web.RequestServerExpire;
import net.assimilationmc.gameapi.module.GameModule;
import net.assimilationmc.gameapi.module.ModuleActivePolicy;
import net.assimilationmc.gameapi.phase.GamePhase;
import net.assimilationmc.gameapi.phase.GamePhaseChangeEvent;
import net.assimilationmc.gameapi.spectate.GameSpectateManager;
import net.assimilationmc.gameapi.team.GameTeam;
import net.assimilationmc.gameapi.util.GC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UHCNetworkManager extends GameModule implements RedisChannelSubscriber {

    private List<String> teamWinners;
    private boolean collecting;

    public UHCNetworkManager(UHCGame game) {
        super(game, "UHC Network Manager", ModuleActivePolicy.PERMANENT);
    }

    @Override
    public void start() {
        String world = getAssiGame().getGameMapManager().getSelectedWorld() == null ? "unknown" : getAssiGame().getGameMapManager().getSelectedWorld().getName();

        getAssiGame().getPlugin().getRedisManager().registerChannelSubscriber("UHC", this);

        // let child setup + reflex setup
        getAssiGame().getPlugin().getServer().getScheduler().runTaskLater(getAssiGame().getPlugin(),
                () -> getAssiGame().getPlugin().getRedisManager().sendPubSubMessage("UHC", new RedisPubSubMessage(PubSubRecipient.ALL,
                        getAssiGame().getPlugin().getServerData().getId(), "HELLO", new String[]{String.valueOf(getAssiGame().getAssiGameSettings().getMaxPlayers()), world,
                        getAssiGame().getAssiGameMeta().getSubType(),
                        String.valueOf(((UHCGame) getAssiGame()).getServerProperties() != null && !((UHCGame) getAssiGame()).getServerProperties().isEmpty())})), 30L);

        this.teamWinners = Lists.newArrayList();

    }

    @Override
    public void end() {
        getAssiGame().getPlugin().getRedisManager().sendPubSubMessage("UHC", new RedisPubSubMessage(PubSubRecipient.ALL,
                getAssiGame().getPlugin().getServerData().getId(), "BYE", new String[]{"memes"}));

        if (getAssiGame().getPlugin().getWebAPIManager().isEnabled()) {
            getAssiGame().getPlugin().getWebAPIManager().sendSyncOneWayRequest(getAssiGame().getPlugin().getWebAPIManager().defaultBuilder()
                    .setEndpoint(WebEndpoint.SERVER).setMethod(RequestMethod.EXPIRE_SERVER)
                    .addParameter(RequestServerExpire.NAME.name(), getAssiGame().getPlugin().getServerData().getId()));
        }
    }


    @Override
    public void onChannelMessage(RedisPubSubMessage redisPubSubMessage) {
        if (redisPubSubMessage.getSubject().equals("BLACKLIST")) {

            if (!((UHCGame) getAssiGame()).getGameSubType().isTeamed()) return;
            Player player = UtilPlayer.get(UUID.fromString(redisPubSubMessage.getArgs()[0]));
            if (player == null) return;
            GameTeam team = getAssiGame().getTeamManager().getTeam(player);
            if (team != null && team.getName().equals(GameSpectateManager.SPECTATOR_TEAM_NAME)) return;
            AssiPlayer assiPlayer = getAssiGame().getPlugin().getPlayerManager().getPlayer(player);

            getAssiGame().getPlugin().getPlayerManager().sendLobby(assiPlayer, C.II + ChatColor.BOLD + "You have been removed from this game as you are no longer " +
                    "eligible to participate in team games.");
            // lol
            UtilServer.broadcast((team != null ? team.getColor() + player.getName() : player.getDisplayName()) + GC.II + " has been removed from the game.");
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(final PlayerJoinEvent e) {

        if (((UHCGame) getAssiGame()).getGameSubType().isTeamed()) {
            Bukkit.getScheduler().runTaskLater(getAssiGame().getPlugin(), () -> {
                AssiPlayer player = getAssiGame().getPlugin().getPlayerManager().getPlayer(e.getPlayer());
                PunishProfile punishProfile = player.getPunishProfile();

                if (punishProfile.getActivePunishments().containsKey(PunishmentCategory.BAD_UHC_TEAM_NAME)) {
                    for (PunishmentData punishmentData : punishProfile.getActivePunishments().get(PunishmentCategory.BAD_UHC_TEAM_NAME)) {
                        D.d(punishmentData);
                        if (punishmentData.getPunishmentType() == PunishmentOutcome.UHC_TEAM_BLACKLIST) {

                            getAssiGame().getPlugin().getPlayerManager().sendLobby(player, C.II + ChatColor.BOLD + "You have a current punishment disallowing you from " +
                                    "playing this game type. You can only play Singles. If you want to play teams again, you can apply at " + C.V +
                                    Domain.PROT_FORUM);
                            return;
                        }
                    }

                }

                getAssiGame().getPlugin().getRedisManager().sendPubSubMessage("UHC", new RedisPubSubMessage(PubSubRecipient.SPIGOT,
                        getAssiGame().getPlugin().getServerData().getId(), "UPDATE", new String[]{"ONLINE", String.valueOf(getAssiGame().getLivePlayers().size())}));

            }, 10L);


        } else {
            getAssiGame().getPlugin().getRedisManager().sendPubSubMessage("UHC", new RedisPubSubMessage(PubSubRecipient.SPIGOT,
                    getAssiGame().getPlugin().getServerData().getId(), "UPDATE", new String[]{"ONLINE", String.valueOf(getAssiGame().getLivePlayers().size())}));
        }

    }

    @EventHandler
    public void on(final GamePhaseChangeEvent e) {
        getAssiGame().getPlugin().getRedisManager().sendPubSubMessage("UHC", new RedisPubSubMessage(PubSubRecipient.ALL,
                getAssiGame().getPlugin().getServerData().getId(), "UPDATE", new String[]{"GAME_PHASE", e.getTo().toString()}));
    }

    @EventHandler
    public void on(final PlayerQuitEvent e) {
        getAssiGame().getPlugin().getServer().getScheduler().runTaskLater(getAssiGame().getPlugin(), () ->
                getAssiGame().getPlugin().getRedisManager().sendPubSubMessage("UHC", new RedisPubSubMessage(PubSubRecipient.SPIGOT,
                        getAssiGame().getPlugin().getServerData().getId(), "UPDATE", new String[]{"ONLINE",
                        String.valueOf(getAssiGame().getLivePlayers().size())})), 5L);
    }

    @EventHandler
    public void on(final GamePingEvent e) {
        final Map<String, Object> serverProperties = ((UHCGame) getAssiGame()).getServerProperties();
        if (serverProperties != null && !serverProperties.isEmpty()) {
            e.addAttribute("custom", serverProperties);
        }
    }

    @EventHandler
    public void on(final SinglesWinEvent e) {
        getAssiGame().getPlugin().getRedisManager().sendPubSubMessage("UHC", new RedisPubSubMessage(PubSubRecipient.ALL,
                getAssiGame().getPlugin().getServerData().getId(), "UPDATE", new String[]{"WINNER", e.getPlayer().getName()}));
    }

    @EventHandler
    public void on(final TeamedPlayerWinEvent e) {
        teamWinners.add(e.getPlayer().getName());

        if (!collecting) {
            getAssiGame().getPlugin().getServer().getScheduler().runTaskLater(getAssiGame().getPlugin(), () -> {
                getAssiGame().getPlugin().getRedisManager().sendPubSubMessage("UHC", new RedisPubSubMessage(PubSubRecipient.ALL,
                        getAssiGame().getPlugin().getServerData().getId(), "UPDATE", new String[]{"WINNERS", getAssiGame().getPlugin().getPlayerManager().getGson().toJson(teamWinners)}));
                teamWinners.clear();
            }, 40);
            collecting = true;
        }
    }

}
