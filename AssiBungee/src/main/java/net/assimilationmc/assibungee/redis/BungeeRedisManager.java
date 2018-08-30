package net.assimilationmc.assibungee.redis;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.assimilationmc.assibungee.AssiBungee;
import net.assimilationmc.assibungee.Module;
import net.assimilationmc.assibungee.redis.pubsub.MalformedRedisMessageException;
import net.assimilationmc.assibungee.redis.pubsub.PubSubRecipient;
import net.assimilationmc.assibungee.redis.pubsub.RedisChannelSubscriber;
import net.assimilationmc.assibungee.redis.pubsub.RedisPubSubMessage;
import net.assimilationmc.assibungee.util.UtilJson;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public class BungeeRedisManager extends Module {

    public static final File FILE = new File("REDIS");

    private JedisPool pool;

    private Map<String, Collection<RedisChannelSubscriber>> pubSubSubscribers;

    private Set<String> channels;
    private Set<ScheduledTask> channelListeners;

    private Gson gson = new Gson();

    public BungeeRedisManager(AssiBungee plugin) {
        super(plugin, "Redis Manager");
    }

    @Override
    protected void start() {
        final RedisServerData serverData = new RedisPropertyReader(FILE).readRedis();
        Preconditions.checkNotNull(serverData, "Redis server request");

        this.pool = new JedisPool(new JedisPoolConfig(), serverData.getHost(), serverData.getPort(), 2000, serverData.getAuth());

        this.pubSubSubscribers = Maps.newHashMap();
        this.channels = Sets.newHashSet();
        this.channelListeners = Sets.newHashSet();

    }

    @Override
    protected void end() {
        pubSubSubscribers.clear();
        channels.clear();
        pool.close();

        channelListeners.forEach(ScheduledTask::cancel);
        channelListeners.clear();
    }

    /**
     * @return Jedis pool.
     */
    public JedisPool getPool() {
        return pool;
    }

    /**
     * Register a class as a channel subscriber and subsequently register a channel.
     *
     * @param channel    Channel to subscribe to.
     * @param subscriber The subscription handler.
     */
    public void registerChannelSubscriber(String channel, RedisChannelSubscriber subscriber) {
        if (subscriber != null) {
            if (pubSubSubscribers.containsKey(channel)) {
                pubSubSubscribers.get(channel).add(subscriber);
            } else pubSubSubscribers.put(channel, Lists.newArrayList(subscriber));
        }
        registerPSChannel(channel);
    }

    /**
     * Send a packet pub-sub message
     *
     * @param channel            Channel to publish to.
     * @param redisPubSubMessage The message to send.
     */
    public void sendPubSubMessage(String channel, RedisPubSubMessage redisPubSubMessage) {
        try (Jedis jedis = pool.getResource()) {
            jedis.publish(channel, gson.toJson(redisPubSubMessage));
        }
    }

    public void del(int database, String key) {
        try (Jedis jedis = pool.getResource()) {
            jedis.select(database);
            jedis.del(key);
        }
    }

    /**
     * Register a channel and setup a listener thread to notify subscribers upon message received.
     *
     * @param channel Channel to setup.
     */
    private void registerPSChannel(String channel) {
        if (channels.contains(channel)) return;
        channels.add(channel);

        final ScheduledTask bukkitRunnable = getPlugin().getProxy().getScheduler().runAsync(getPlugin(), () -> {
            try (Jedis connection = pool.getResource()) {
                connection.subscribe(new JedisPubSub() {
                    @Override
                    public void onMessage(String channel1, String message) {
                        try {
                            final RedisPubSubMessage pubSubMessage = UtilJson.deserialize(gson, new TypeToken<RedisPubSubMessage>() {
                            }, message);

                            if (pubSubMessage.getFrom().equals(getPlugin().getServerData().getId()) ||
                                    (!pubSubMessage.getTo().equalsIgnoreCase(getPlugin().getServerData().getId()) && !pubSubMessage.getTo().equals(PubSubRecipient.ALL)
                                            && !pubSubMessage.getTo().equals(PubSubRecipient.PROXY)))
                                return;

                            if (pubSubSubscribers.containsKey(channel)) {
                                pubSubSubscribers.get(channel).forEach(subscriber -> {
                                    try {
                                        subscriber.onChannelMessage(pubSubMessage);
                                    } catch (Throwable e) {
                                        log(Level.SEVERE, "Failed to notify a subscriber");
                                        e.printStackTrace();
                                    }

                                });
                            }

                        } catch (MalformedRedisMessageException e) {
                            getPlugin().getLogger().severe("Failed to parse redis message on channel " + channel1 + "! (" + e.getMessage() + ")");
                            e.printStackTrace();
                        }

                    }
                }, channel);
            }
        });

        channelListeners.add(bukkitRunnable);
    }

}
