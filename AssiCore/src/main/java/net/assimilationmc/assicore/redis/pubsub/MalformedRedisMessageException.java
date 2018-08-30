package net.assimilationmc.assicore.redis.pubsub;

public class MalformedRedisMessageException extends IllegalArgumentException {

    /**
     * The exception throw when a redis pubsub message is malformed.
     *
     * @param problem The problem with the message.
     */
    public MalformedRedisMessageException(String problem) {
        super(problem);
    }

}
