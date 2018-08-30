package net.assimilationmc.assicore.redis;

public interface RedisObjectHolder {

    /**
     * @return The prefix for all keys said holder holds in the Redis database.
     */
    String getObjectPrefix();

    /**
     * Get the fully formatted redis key of the object.
     *
     * @param object the object should be that if you combine it with getObjectPrefix, you get a complete key.
     * @return The built key.
     */
    String redisKey(Object object);


}
