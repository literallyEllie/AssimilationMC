package net.assimilationmc.assicore.player;

import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.redis.RedisDatabaseIndex;
import net.assimilationmc.assicore.util.UtilPlayer;
import redis.clients.jedis.Jedis;

import java.util.UUID;

public class PlayerFinder {

    private final AssiPlugin assiPlugin;

    /**
     * A lib class to find a player and the server they're on, not very expensively.
     *
     * @param plugin the plugin instance.
     */
    public PlayerFinder(AssiPlugin plugin) {
        this.assiPlugin = plugin;

        plugin.getCommandManager().registerCommand(new CmdFind(plugin),
                new CmdWhereAmI(plugin));
    }

    /**
     * Quick way to get the current server of a player.
     *
     * @param uuid the uuid to find.
     * @return the server they're on, null if they're offline.
     */
    public String findPlayer(UUID uuid) {
        if (assiPlugin.getPlayerManager().isOnline(uuid)) {
            return assiPlugin.getServerData().getId();
        }

        if (assiPlugin.getPlayerManager().getOfflinePlayerCache().containsKey(uuid))
            return assiPlugin.getPlayerManager().getOfflinePlayerCache().get(uuid).getLastSeenServer();

        if (!assiPlugin.getServerData().isLocal()) {
            try (Jedis jedis = assiPlugin.getRedisManager().getPool().getResource()) {
                jedis.select(RedisDatabaseIndex.DATA_USERS);
                if (jedis.hexists(assiPlugin.getPlayerManager().redisKey(uuid), "server")) {
                    return jedis.hget(assiPlugin.getPlayerManager().redisKey(uuid), "server");
                }
            }
        }

        return null;
    }

    /**
     * Quick-ish way to get the current server of a player.
     *
     * @param name the uuid to find.
     * @return the server they're on, null if they're online.
     */
    public String findPlayer(String name) {
        if (UtilPlayer.get(name) != null)
            return findPlayer(UtilPlayer.get(name).getUniqueId());

        UUID uuid;

        if (assiPlugin.getServerData().isLocal()) return null;
        try (Jedis conn = assiPlugin.getRedisManager().getPool().getResource()) {
            conn.select(RedisDatabaseIndex.DATA_USERS);
            if (!conn.exists(name.toLowerCase())) {
                return null;
            }
            uuid = UUID.fromString(conn.get(name.toLowerCase()));
        }

        return findPlayer(uuid);
    }

}
