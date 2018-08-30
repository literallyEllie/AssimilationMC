package net.assimilationmc.assibungee.redis.pubsub;

public interface RedisChannelSubscriber {

    /**
     * The event called when a message was received from redis on a channel.
     *
     * @param message The message received.
     */
    void onChannelMessage(RedisPubSubMessage message);

}
