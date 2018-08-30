package net.assimilationmc.assibungee.server.balancer;

import com.google.common.collect.Maps;
import net.assimilationmc.assibungee.AssiBungee;
import net.assimilationmc.assibungee.Module;
import net.assimilationmc.assibungee.server.ServerName;
import net.assimilationmc.assibungee.util.D;
import net.md_5.bungee.api.AbstractReconnectHandler;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.event.EventHandler;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class BalancerManager extends Module {

    private Map<String, ServerBalancer> serverBalancerMap;

    public BalancerManager(AssiBungee assiBungee) {
        super(assiBungee, "Balancer Manager");
    }

    @Override
    protected void start() {
        this.serverBalancerMap = Maps.newHashMap();
        this.serverBalancerMap.put("hub", new ServerBalancer(getPlugin(), ServerName.HUB));
    }

    public void offlineCheck(ServerBalancer serverBalancer) {

        for (ServerInfo serverInfo : serverBalancer.getOffline()) {
            serverInfo.ping((serverPing, throwable) -> {
                if (throwable != null) {
                    serverBalancer.getOffline().add(serverInfo);
                    return;
                }
                serverBalancer.getOffline().remove(serverInfo);
                serverBalancer.getServers().add(serverInfo);
            });
        }

    }

    public void checkOnline(ServerBalancer serverBalancer) {

        for (ServerInfo serverInfo : serverBalancer.getServers()) {
            serverInfo.ping((serverPing, throwable) -> {
                if (throwable != null) {
                    serverBalancer.getServers().remove(serverInfo);
                    serverBalancer.getOffline().add(serverInfo);
                    return;
                }
                serverBalancer.getServers().add(serverInfo);
            });
        }

    }

    @Override
    protected void end() {
        serverBalancerMap.values().forEach(serverBalancer -> {
            serverBalancer.getServers().clear();
            serverBalancer.getOffline().clear();
        });
        serverBalancerMap.clear();
    }

    public ServerBalancer getServerBalancer(String key) {
        return serverBalancerMap.get(key.toLowerCase());
    }

    @EventHandler
    public void on(final ServerConnectEvent e) {
        final ProxiedPlayer player = e.getPlayer();

        if (player != null && player.getServer() == null) {
            final ServerInfo hub = serverBalancerMap.get("hub").process(getPlugin(), player, false);
            if (hub == null) {
                log(Level.SEVERE, "Failed to find server for " + player.getName());
                return;
            }
            e.setTarget(hub);
        }

    }

    @EventHandler
    public void on(final ServerKickEvent e) {
        final ProxiedPlayer player = e.getPlayer();
        if (!player.isConnected()) return;

        ServerInfo kickedFrom;

        if (e.getPlayer().getServer() != null) {
            kickedFrom = player.getServer().getInfo();
        } else if (getPlugin().getProxy().getReconnectHandler() != null) {
            kickedFrom = getPlugin().getProxy().getReconnectHandler().getServer(player);
        } else {
            kickedFrom = AbstractReconnectHandler.getForcedHost(player.getPendingConnection());
            if (kickedFrom == null) {
                kickedFrom = getPlugin().getProxy().getServerInfo(player.getPendingConnection().getListener().getDefaultServer());
            }
        }

        final ServerInfo hub = serverBalancerMap.get("hub").process(getPlugin(), player, false);
        if (kickedFrom != null && kickedFrom.equals(hub)) return;

        e.setCancelled(true);
        e.setCancelServer(hub);
    }

}
