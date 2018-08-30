package net.assimilationmc.assicore.punish;

import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.punish.model.PunishmentCategory;
import net.assimilationmc.assicore.punish.model.PunishmentData;
import net.assimilationmc.assicore.punish.model.UnpunishData;
import net.assimilationmc.assicore.redis.RedisDatabaseIndex;
import net.assimilationmc.assicore.redis.pubsub.RedisChannelSubscriber;
import net.assimilationmc.assicore.redis.pubsub.RedisPubSubMessage;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilPlayer;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;

import java.util.UUID;

public class PunishSync implements RedisChannelSubscriber {

    private PunishmentManager manager;

    /**
     * The object to keep punishments in sync and execute commands sent by other punishment managers.
     *
     * @param punishmentManager our instance of punishmentManager
     */
    public PunishSync(PunishmentManager punishmentManager) {
        this.manager = punishmentManager;

        manager.getPlugin().getRedisManager().registerChannelSubscriber(PunishmentManager.REDIS_CHANNEL, this);
    }

    @Override
    public void onChannelMessage(RedisPubSubMessage message) {
        String[] args = message.getArgs();

        // Punishment has been published and we are being notified of this event, we should simply log this to the player.
        if (message.getSubject().equals("NEW_PUNISH")) {
            UUID uuid = UUID.fromString(args[0]);

            AssiPlayer player = manager.getPlugin().getPlayerManager().getOnlinePlayers().get(uuid);
            if (player == null) {
                player = manager.getPlugin().getPlayerManager().getOfflinePlayerCache().get(uuid);
                if (player == null) return;
            }

            int internalId = Integer.parseInt(args[1]);

            try (Jedis jedis = manager.getPlugin().getRedisManager().getPool().getResource()) {
                jedis.select(RedisDatabaseIndex.DATA_USERS);

                String uuidRedis = manager.getPlugin().getPlayerManager().redisKey(uuid);

                if (!jedis.exists(uuidRedis) || !jedis.hexists(uuidRedis, manager.redisKey(internalId))) return;

                String serializedPunishment = jedis.hget(uuidRedis, manager.redisKey(internalId));

                PunishmentData punishmentData = new PunishmentData(serializedPunishment);

                if (player.getPunishmentProfile(false) != null) {
                    player.getPunishmentProfile(false).addPunishment(punishmentData.getPunishmentCategory(), punishmentData);
                }

            }

        }

        // Punishment has been published and we are being told to notify the player of the punishment.
        if (message.getSubject().equals("CARRY_PUNISH")) {
            final UUID uuid = UUID.fromString(args[0]);
            final AssiPlayer player = manager.getPlugin().getPlayerManager().getOnlinePlayers().get(uuid);
            if (player == null) return;

            PunishmentCategory category = PunishmentCategory.valueOf(args[1]);
            int severity = Integer.parseInt(args[2]);
            String reason = args[3];

            category.carryOut(manager.getPlugin(), uuid, severity, reason);
        }

        // The punishment has been revoked and we need to update our entities to know, and also notify if necessary.
        if (message.getSubject().equals("UNPUNISH")) {
            String unpunisher = args[0];
            UUID unpunishedUuid = UUID.fromString(args[1]);
            int id = Integer.parseInt(args[2]);
            long time = Long.valueOf(args[3]);
            boolean notify = Boolean.valueOf(args[4]);
            String reason = args[5];

            UnpunishData unpunishData = new UnpunishData(unpunisher, time, reason);

            Player player = UtilPlayer.get(unpunishedUuid);

            if (player != null) {
                if (notify) {
                    player.sendMessage(PunishmentManager.PREFIX + "Its your lucky day! " + C.C + "You have been unpunished for " + C.V
                            + reason);
                }

                AssiPlayer assiPlayer = manager.getPlugin().getPlayerManager().getPlayer(player);

                if (assiPlayer.getPunishmentProfile(false) != null) {
                    PunishmentData punishmentData = assiPlayer.getPunishProfile().getPunishmentId(id);
                    if (punishmentData != null) {
                        assiPlayer.getPunishProfile().getActivePunishments().get(punishmentData.getPunishmentCategory()).remove(punishmentData);
                        punishmentData.setUnpunishData(unpunishData);

                        assiPlayer.getPunishProfile().addOldPunishment(false, punishmentData.getPunishmentCategory(), punishmentData);
                    } // else uh oh
                }

            }

            if (manager.getPlugin().getPlayerManager().getOfflinePlayerCache().containsKey(unpunishedUuid)) {

                AssiPlayer target = manager.getPlugin().getPlayerManager().getOfflinePlayerCache().get(unpunishedUuid);
                if (target.getPunishmentProfile(false) != null) {

                    PunishmentData punishmentData = target.getPunishProfile().getPunishmentId(id);
                    if (punishmentData != null) {
                        target.getPunishProfile().getActivePunishments().get(punishmentData.getPunishmentCategory()).remove(punishmentData);
                        punishmentData.setUnpunishData(unpunishData);

                        target.getPunishProfile().addOldPunishment(false, punishmentData.getPunishmentCategory(), punishmentData);
                    } // else uh oh

                }

            }

        }

    }

}
