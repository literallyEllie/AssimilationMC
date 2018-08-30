package net.assimilationmc.assicore.redis;

public interface RedisObject {

    /**
     * @return The reference key in the database. (The {@link RedisObjectHolder} will come before this)
     */
    String asRedisKey();

}
