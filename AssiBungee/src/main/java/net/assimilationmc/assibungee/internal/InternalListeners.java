package net.assimilationmc.assibungee.internal;

import com.google.common.collect.Sets;
import net.assimilationmc.assibungee.AssiBungee;
import net.assimilationmc.assibungee.discord.DiscordManager;
import net.assimilationmc.assibungee.discord.DiscordPresetChannel;
import net.assimilationmc.assibungee.internal.objects.HelpOP;
import net.assimilationmc.assibungee.player.AssiPlayer;
import net.assimilationmc.assibungee.redis.pubsub.RedisChannelSubscriber;
import net.assimilationmc.assibungee.redis.pubsub.RedisPubSubMessage;
import net.assimilationmc.assibungee.stafflog.StaffLog;
import net.assimilationmc.assibungee.util.D;
import net.assimilationmc.assibungee.util.UtilPlayer;
import net.assimilationmc.assibungee.util.UtilTime;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class InternalListeners implements Listener, RedisChannelSubscriber {

    private final AssiBungee assiBungee;
    private Set<UUID> recentlyConnected = Sets.newHashSet();

    public InternalListeners(AssiBungee bungee) {
        this.assiBungee = bungee;
        assiBungee.registerListener(this);

        assiBungee.getRedisManager().registerChannelSubscriber("INTERNAL", this);
        assiBungee.getRedisManager().registerChannelSubscriber("staffchat", this);
    }

    @Override
    public void onChannelMessage(RedisPubSubMessage message) {
        final String subject = message.getSubject();
        final String[] args = message.getArgs();

        // Staff chat
        if (subject.equals("MESSAGE_SEND") && assiBungee.getDiscordManager() != null) {
            assiBungee.getDiscordManager().messageChannel(DiscordPresetChannel.STAFF_CHAT,
                    "[" + UtilTime.getTime() + "] @ " + args[2] + " **" + args[1] + "**: `" + args[3].replace("`", "'") + "`");
        }

        if (subject.equals("HELPOP_SEND") && assiBungee.getDiscordManager() != null) {
            final HelpOP helpOP = new HelpOP(args);

            assiBungee.getDiscordManager().messageChannel(DiscordPresetChannel.HELPOP, assiBungee.getDiscordManager()
                    .getEmbedBuilder(DiscordManager.DiscordColor.HELPOP_RECEIVED)
                    .setTitle("[New] HelpOP #" + helpOP.getId())
                    .addField("From:", helpOP.getSenderOfflineName(), true)
                    .addField("Server:", helpOP.getServer(), true)
                    .addField("Content:", helpOP.getContent(), false)
                    .setFooter("Handle it in-game with /helpop handle <" + helpOP.getId() + " | " + helpOP.getSenderOfflineName() + ">", DiscordManager.CAKE_EMBED)
                    .build());

        }

        if (subject.equals("HELPOP_HANDLE")) {
            if (assiBungee.getDiscordManager() != null) {
                final HelpOP helpOP = new HelpOP(args);

                assiBungee.getDiscordManager().messageChannel(DiscordPresetChannel.HELPOP, assiBungee.getDiscordManager()
                        .getEmbedBuilder(DiscordManager.DiscordColor.HELPOP_UPDATE)
                        .setTitle("[Status Update] HelpOP #" + helpOP.getId())
                        .addField("From:", UtilPlayer.nameOrDefault(helpOP.getSender(), helpOP.getSenderOfflineName()), true)
                        .addField("Update:", "Unhandled -> Handled", true)
                        .addField("Content:", helpOP.getContent(), false)
                        .setFooter("Handled by " + helpOP.getHandler(), DiscordManager.CAKE_EMBED)
                        .build());

                final ProxiedPlayer player = UtilPlayer.get(helpOP.getHandler());
                if (player == null || !player.hasPermission("staff.track")) return;

                final StaffLog log = assiBungee.getLoggerManager().getLog(player.getUniqueId());
                if (log == null) return;
                log.write("HELPOP HANDLE", "#" + helpOP.getId());
            }
            return;
        }

        // Internal

        if (subject.equals("RANK_CHANGE") && assiBungee.getDiscordManager() != null) {

            assiBungee.getDiscordManager().modLog(assiBungee.getDiscordManager().getEmbedBuilder(DiscordManager.DiscordColor.RANK_CHANGE)
                    .setTitle("Rank update for " + args[0] + ":")
                    .addField("From:", args[1], true)
                    .addField("To:", args[2], true)
                    .addField("By:", args[3], true));

            return;
        }

        if (subject.equals("SEND_HUB")) {
            ProxiedPlayer player = assiBungee.getProxy().getPlayer(UUID.fromString(args[0]));

            if (player != null) {
                assiBungee.getBalancerManager().getServerBalancer("hub").process(assiBungee, player, true);
            }
            return;
        }

        if (subject.equals("MESSAGE_PLAYER")) {
            ProxiedPlayer player = assiBungee.getProxy().getPlayer(UUID.fromString(args[0]));
            if (player != null) {
                boolean ignoreIfOnServer = Boolean.valueOf(args[1]);
                if (ignoreIfOnServer && player.getServer().getInfo().getName().equals(message.getFrom())) return;

                player.sendMessage(new TextComponent(args[2]));
            }

            return;
        }

        if (subject.equals("IP_KICK")) {
            String ip = args[0];
            String reason = args[1];

            for (ProxiedPlayer player : assiBungee.getProxy().getPlayers()) {
                if (player.getAddress().getAddress().getHostAddress().equals(ip)) {
                    player.disconnect(new TextComponent(reason));
                }
            }

            return;
        }

        if (subject.equals("IP_BAN")) {
            String ip = args[0];
            String dispatcher = args[1];

            assiBungee.getPlayerManager().ipBan(ip, dispatcher);
        }

        // Hub gone offline
        if (subject.equals("OFFLINE")) {
            assiBungee.getBalancerManager().getServerBalancer("hub").notifyOffline(assiBungee.getProxy().getServerInfo(message.getFrom()));
            assiBungee.getLogger().info("Notification from " + message.getFrom() + " that it is going offine.");
        }

        // Hub online
        if (subject.equals("ONLINE")) {
            assiBungee.getBalancerManager().getServerBalancer("hub").notifyOnline(assiBungee.getProxy().getServerInfo(message.getFrom()));
            assiBungee.getLogger().info("Notification from " + message.getFrom() + " that it is now online.");
        }

    }

    @EventHandler
    public void on(final PostLoginEvent e) {
        final UUID uuid = e.getPlayer().getUniqueId();
        recentlyConnected.add(uuid);
        assiBungee.getProxy().getScheduler().schedule(assiBungee, () -> recentlyConnected.remove(uuid), 2, TimeUnit.SECONDS);
    }

    @EventHandler
    public void on(final ServerSwitchEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        if (!recentlyConnected.contains(uuid)) {
            assiBungee.getProxy().getScheduler().schedule(assiBungee, () -> {
                AssiPlayer assiPlayer = assiBungee.getPlayerManager().getPlayerRedis(uuid);
                if (assiPlayer == null) return;
                assiPlayer.setLastSeen(System.currentTimeMillis());

                assiBungee.getPlayerManager().pushPlayer(assiPlayer, false);
            }, 5L, TimeUnit.MILLISECONDS);
        }
    }

    @EventHandler(priority = 32)
    public void on(final PlayerDisconnectEvent e) {
        final ProxiedPlayer player = e.getPlayer();
        recentlyConnected.remove(player.getUniqueId());

        assiBungee.getProxy().getScheduler().schedule(assiBungee, () -> {
            AssiPlayer assiPlayer = assiBungee.getPlayerManager().getPlayerRedis(player.getUniqueId());
            if (assiPlayer == null) return;
            assiPlayer.setLastSeen(System.currentTimeMillis());

            assiBungee.getPlayerManager().pushPlayer(assiPlayer, true);
        }, 5L, TimeUnit.MILLISECONDS);
    }

}
