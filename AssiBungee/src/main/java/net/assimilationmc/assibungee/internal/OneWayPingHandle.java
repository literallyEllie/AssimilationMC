package net.assimilationmc.assibungee.internal;

import com.google.common.collect.Maps;
import net.assimilationmc.assibungee.AssiBungee;
import net.assimilationmc.assibungee.redis.pubsub.PubSubRecipient;
import net.assimilationmc.assibungee.redis.pubsub.RedisChannelSubscriber;
import net.assimilationmc.assibungee.redis.pubsub.RedisPubSubMessage;
import net.assimilationmc.assibungee.util.D;

import java.util.Map;
import java.util.function.Consumer;

public class OneWayPingHandle implements RedisChannelSubscriber {

    private final AssiBungee plugin;
    private final Map<Integer, String> outGoingRequest;
    private final Map<Integer, Consumer<ServerPing>> callBacks;
    private int counter;

    public OneWayPingHandle(AssiBungee plugin) {
        this.plugin = plugin;
        this.outGoingRequest = Maps.newHashMap();
        this.callBacks = Maps.newHashMap();
        this.counter = 0;
        plugin.getRedisManager().registerChannelSubscriber("INTERNAL", this);
    }

    public int ping(String target, Consumer<ServerPing> callback) {
        outGoingRequest.put(counter, target);
        callBacks.put(counter, callback);

        plugin.getRedisManager().sendPubSubMessage("INTERNAL", new RedisPubSubMessage(target, plugin.getServerData().getId(),
                "PING_SEND", new String[]{String.valueOf(counter), target}));

        return counter++;
    }

    @Override
    public void onChannelMessage(RedisPubSubMessage message) {
        if (message.getSubject().equals("PING_REPLY")) {
            // D.d("message on channel");
            ServerPing ping = new ServerPing(message.getArgs());
            final int id = ping.getId();
            final String serverId = ping.getServerId();

            // D.d("id = " + id);

            if (outGoingRequest.containsKey(id)) {
               //  D.d("good");
                String outReq = outGoingRequest.get(id);

                if (outReq.equalsIgnoreCase(serverId) || outReq.equalsIgnoreCase(PubSubRecipient.PROXY)) {
                  //   D.d("match");

                    if (callBacks.containsKey(id)) {
                        callBacks.get(id).accept(ping);
                    } else outGoingRequest.remove(id);

                }

            }
        }
    }

    public void unregisterCallback(int id) {
        callBacks.remove(id);
        outGoingRequest.remove(id);
    }

}
