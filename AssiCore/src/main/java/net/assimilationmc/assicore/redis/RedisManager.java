package net.assimilationmc.assicore.redis;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.Module;
import net.assimilationmc.assicore.redis.pubsub.MalformedRedisMessageException;
import net.assimilationmc.assicore.redis.pubsub.PubSubRecipient;
import net.assimilationmc.assicore.redis.pubsub.RedisChannelSubscriber;
import net.assimilationmc.assicore.redis.pubsub.RedisPubSubMessage;
import net.assimilationmc.assicore.util.UtilJson;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public class RedisManager extends Module {

    public static final File FILE = new File("REDIS");
    private boolean enabled;

    private JedisPool pool;

    private Map<String, Collection<RedisChannelSubscriber>> pubSubSubscribers;

    private Set<String> channels;
    private Set<BukkitTask> channelListeners;

    private Gson gson = new Gson();

    public RedisManager(AssiPlugin plugin) {
        super(plugin, "Redis Manager");
    }

    @Override
    protected void start() {
        if (!FILE.exists()) {
            log("Redis module will remain idle.");
            this.enabled = false;
            return;
        }
        enabled = true;

        final RedisServerData serverData = new RedisPropertyReader(FILE).readRedis();

        Preconditions.checkNotNull(serverData, "Redis server request");

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(200);
        poolConfig.setMaxTotal(300);
        poolConfig.setTestOnBorrow(false);
        poolConfig.setTestOnReturn(false);

        this.pool = new JedisPool(poolConfig, serverData.getHost(), serverData.getPort(), 2000, serverData.getAuth());

        this.pubSubSubscribers = Maps.newHashMap();
        this.channels = Sets.newHashSet();
        this.channelListeners = Sets.newHashSet();

    }

    @Override
    protected void end() {
        pubSubSubscribers.clear();
        channels.clear();
        pool.close();

        channelListeners.forEach(BukkitTask::cancel);
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
        if (!enabled) return;
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
        if (!enabled) return;
        try (Jedis jedis = pool.getResource()) {
            jedis.publish(channel, gson.toJson(redisPubSubMessage));
        }
    }

    /**
     * Purge the cache for values with that prefix.
     *
     * @param prefix The ObjectHolder key of values to be deleted.
     */
    public void purge(String prefix) {
        if (!enabled) return;
        if (prefix.equals(getPlugin().getPlayerManager().getObjectPrefix())) return;

        try (Jedis jedis = pool.getResource()) {
            Set<String> keys = jedis.keys(prefix);
            keys.forEach(jedis::del);
        }
    }

    /**
     * Register a channel and setup a listener thread to notify subscribers upon message received.
     *
     * @param channel Channel to setup.
     */
    private void registerPSChannel(String channel) {
        if (!enabled) return;
        if (channels.contains(channel)) return;
        channels.add(channel);

        final BukkitRunnable bukkitRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("AssiPubSubThread-" + channel);
                try (Jedis connection = pool.getResource()) {
                    connection.subscribe(new JedisPubSub() {
                        @Override
                        public void onMessage(String channel, String message) {
                            try {

                                final RedisPubSubMessage pubSubMessage = UtilJson.deserialize(gson, new TypeToken<RedisPubSubMessage>() {
                                }, message);

                                if (pubSubMessage.getFrom().equals(getPlugin().getServerData().getId()) ||
                                        (!pubSubMessage.getTo().equalsIgnoreCase(getPlugin().getServerData().getId()) && !pubSubMessage.getTo().equals(PubSubRecipient.ALL)
                                                && !pubSubMessage.getTo().equals(PubSubRecipient.SPIGOT)))
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
                                getPlugin().getLogger().severe("Failed to parse redis message on channel " + channel + "! (" + e.getMessage() + ")");
                                e.printStackTrace();
                            }

                        }
                    }, channel);
                }
            }
        };

        channelListeners.add(bukkitRunnable.runTaskAsynchronously(getPlugin()));
    }


}
