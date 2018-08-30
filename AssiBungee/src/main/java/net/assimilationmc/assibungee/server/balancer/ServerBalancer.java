package net.assimilationmc.assibungee.server.balancer;

import com.google.common.collect.Sets;
import net.assimilationmc.assibungee.AssiBungee;
import net.assimilationmc.assibungee.util.C;
import net.assimilationmc.assibungee.util.D;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ServerBalancer {

    private Set<ServerInfo> servers;
    private int lowestConnectThreshold;

    private Set<ServerInfo> offline;

    /**
     * The thing to balance out servers. Mainly will be used for Hub servers.
     * When a player joins it will be run through this and the server returned from getLowestCount
     * will be the server they join.
     *
     * @param serverPattern The pattern the collection of the initial servers must follow to qualify
     *                      to be included in this balancer.
     */
    public ServerBalancer(AssiBungee assiBungee, String serverPattern, int lowestConnectThreshold) {
        this.servers = Sets.newHashSet();
        this.lowestConnectThreshold = lowestConnectThreshold;
        this.offline = Sets.newHashSet();

        for (Map.Entry<String, ServerInfo> stringServerInfoEntry : ProxyServer.getInstance().getServersCopy().entrySet()) {
            final String sName = stringServerInfoEntry.getKey();
            final ServerInfo value = stringServerInfoEntry.getValue();

            if (sName.matches(serverPattern)) {
                offline.add(value);

                assiBungee.getOneWayPingHandle().ping(sName, serverPing -> {
                    servers.add(ProxyServer.getInstance().getServerInfo(serverPing.getServerId()));
                    offline.remove(value);
                });
            }
        }

    }

    /**
     * The thing to balance out servers where the lowestConnectThreshold is 10.
     * This means when selecting a server it will get the lowest,
     * if it is less than the lowestConnectThreshold (10) it will get the player count of the highest
     * server and compare the player counts.
     * This is to connect the player to the most moderately populated server.
     *
     * @param serverPattern The pattern the collection of the initial servers must follow to qualify
     *                      to be included in this balancer.
     */
    public ServerBalancer(AssiBungee assiBungee, String serverPattern) {
        this(assiBungee, serverPattern, 15);
    }

    /**
     * @return The server in the collection with the lowest player count.
     */
    public ServerInfo getLowestCount() {
        ServerInfo lowestServer = null;
        int lowest = -1;

        for (ServerInfo server : servers) {
            if (lowest == -1) {
                lowest = server.getPlayers().size();
                lowestServer = server;
                continue;
            }

            if (server.getPlayers().size() < lowest) {
                lowestServer = server;
                lowest = server.getPlayers().size();
            }
        }

        return lowestServer;
    }

    public ServerInfo getHighestCount() {
        ServerInfo highestServer = null;
        int highest = -1;

        for (ServerInfo server : servers) {
            if (server.getPlayers().size() > highest) {
                highestServer = server;
                highest = server.getPlayers().size();
            }
        }

        return highestServer;
    }

    /**
     * The method to pick a target.
     * It loops through the servers. Firstly it will get the server with the lowest player count.
     * If this server count is lower than the lowestConnectThreshold, it will get the player count of the
     * highest server and see if it is less than or equal to the lowestConnectThreshold, if it is, that will be their target.
     * Else, the server with the lowest count will be their target.
     *
     * @return the server selected.
     */
    public ServerInfo pickTarget() {
        if (servers.isEmpty())
            return null;

        ServerInfo target;
        ServerInfo lowest = getLowestCount();

        if (lowest != null && lowest.getPlayers().size() < lowestConnectThreshold) {
            ServerInfo highest = getHighestCount();

            if (highest != null && highest.getPlayers().size() <= lowestConnectThreshold)
                target = highest;
            else
                target = lowest;

        } else
            target = getHighestCount();

        return target;
    }

    /**
     * Process the login.
     *
     * @param player the player to connect to.
     */
    public ServerInfo process(AssiBungee assiBungee, ProxiedPlayer player, boolean connect) {
        ServerInfo serverInfo = pickTarget();

        if (serverInfo != null) {

            if (connect) {
                assiBungee.getPartyCleaner().handleForceConnect(player.getUniqueId(), serverInfo);
                player.connect(serverInfo);
            }

            player.sendMessage(new ComponentBuilder("You have been connected to ").color(C.C)
                    .append(serverInfo.getName()).color(C.V).append(".").color(C.C).create());
            return serverInfo;
        }

        player.disconnect(new ComponentBuilder("All our Hubs are currently offline.\n").color(C.II)
                .append("This could be planned maintenance or an error, go to our discord for more info: ").append("discord.gg/bhyDjPJ\n").color(C.V)
                .append(" Include this number: " + servers.size()).color(C.II).create());
        return null;
    }

    /**
     * @return All the servers included in this balancer.
     */
    public Set<ServerInfo> getServers() {
        return servers;
    }

    /**
     * @return the minimum count for lower populated servers to be potentially ignored.
     */
    public int getLowestConnectThreshold() {
        return lowestConnectThreshold;
    }

    public Set<ServerInfo> getOffline() {
        return offline;
    }

    public void notifyOffline(ServerInfo info) {
        offline.add(info);
        servers.remove(info);
    }

    public void notifyOnline(ServerInfo serverInfo) {
        offline.remove(serverInfo);
        servers.add(serverInfo);
    }

}
