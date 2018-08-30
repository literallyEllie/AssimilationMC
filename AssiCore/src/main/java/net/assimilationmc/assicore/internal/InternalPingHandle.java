package net.assimilationmc.assicore.internal;

import com.google.common.collect.Maps;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.event.GamePingEvent;
import net.assimilationmc.assicore.redis.pubsub.PubSubRecipient;
import net.assimilationmc.assicore.redis.pubsub.RedisChannelSubscriber;
import net.assimilationmc.assicore.redis.pubsub.RedisPubSubMessage;
import net.assimilationmc.assicore.server.ServerName;
import net.assimilationmc.assicore.util.Callback;
import net.assimilationmc.assicore.util.D;
import net.assimilationmc.assicore.util.UtilServer;

import java.util.Map;

public class InternalPingHandle implements RedisChannelSubscriber {

    private final AssiPlugin plugin;
    private final Map<Integer, String> outGoingRequest;
    private final Map<Integer, Callback<ServerPing>> callBacks;
    private int counter;

    public InternalPingHandle(AssiPlugin plugin) {
        this.plugin = plugin;
        this.outGoingRequest = Maps.newHashMap();
        this.callBacks = Maps.newHashMap();
        this.counter = 0;
        plugin.getRedisManager().registerChannelSubscriber("INTERNAL", this);
    }

    public int ping(String target, Callback<ServerPing> callback) {
        outGoingRequest.put(counter, target);
        callBacks.put(counter, callback);

        plugin.getRedisManager().sendPubSubMessage("INTERNAL", new RedisPubSubMessage(PubSubRecipient.ALL, plugin.getServerData().getId(),
                "PING_SEND", new String[]{String.valueOf(counter), target}));

        return counter++;
    }

    private void reply(ServerPing serverPing) {
//        D.d("SENT REPLY");
        plugin.getRedisManager().sendPubSubMessage("INTERNAL", new RedisPubSubMessage(PubSubRecipient.ALL,
                plugin.getServerData().getId(), "PING_REPLY", serverPing.serialize()));
//        D.d("sent reply with id " + serverPing.getId());
    }

    @Override
    public void onChannelMessage(RedisPubSubMessage message) {
//        D.d("msg downstream");
        if (message.getSubject().equals("PING_SEND")) {
            int id = Integer.parseInt(message.getArgs()[0]);
            String target = message.getArgs()[1];

//            D.d("new message to " + target + "!");

            if (!plugin.getServerData().getId().equalsIgnoreCase(target) &&
                    (plugin.getServerData().isLobby() && !target.equalsIgnoreCase("LOBBY") ||
                            plugin.getServerData().isUhc() && !target.equalsIgnoreCase("UHC"))) {
//                D.d("ignoring " + target);
                return;
            }

            reply(buildReply(id));
        }

        if (message.getSubject().equals("PING_REPLY")) {
            ServerPing ping = new ServerPing(message.getArgs());
            final int id = ping.getId();
            final String serverId = ping.getServerId();

//            D.d("Received ping response for " + serverId + " id = " + ping.getId());

            if (outGoingRequest.containsKey(id)) {
                String outReq = outGoingRequest.get(id);

                if (outReq.equalsIgnoreCase(serverId) ||
                        (outReq.equalsIgnoreCase("LOBBY") && serverId.matches(ServerName.HUB)) ||
                        outReq.equalsIgnoreCase("UHC") && serverId.matches(ServerName.UHC)) {

//                    D.d("this ping is for us!");

                    if (callBacks.containsKey(id)) {
                        callBacks.get(id).callback(ping);
                    } else outGoingRequest.remove(id);

//                } else D.d("this ping isn't for us.");
                } // else D.d("not expecting, ignoring");

            }
        }
    }

    private ServerPing buildReply(int id) {
        ServerPing serverPing = new ServerPing(id, plugin.getServerData().getId());
        serverPing.addAttribute("online", UtilServer.getOnlinePlayers());
        serverPing.addAttribute("max_players", UtilServer.getMaxPlayers());

        if (plugin.getServerData().isUhc()) {
//            D.d("calling game ping event");
            GamePingEvent gamePingEvent = new GamePingEvent();
            UtilServer.callEvent(gamePingEvent);
            serverPing.getAttributes().putAll(gamePingEvent.getAttributes());
//            D.d("put in attributes: " + gamePingEvent.getAttributes());
        }

//        D.d("ping built! with id" + id);
        return serverPing;
    }

    public void unregisterCallback(int id) {
        callBacks.remove(id);
        outGoingRequest.remove(id);
    }

}
