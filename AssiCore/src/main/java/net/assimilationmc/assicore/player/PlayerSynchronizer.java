package net.assimilationmc.assicore.player;

import com.google.gson.reflect.TypeToken;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.redis.pubsub.RedisChannelSubscriber;
import net.assimilationmc.assicore.redis.pubsub.RedisPubSubMessage;
import net.assimilationmc.assicore.reward.RewardType;
import net.assimilationmc.assicore.util.UtilJson;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerSynchronizer implements RedisChannelSubscriber {

    private final AssiPlugin plugin;

    /**
     * A class to keep player request synchronized across the network.
     * It handles cross-server player modification, and this class receives updates and applies them to the player.
     *
     * @param plugin the plugin instance.
     */
    public PlayerSynchronizer(AssiPlugin plugin) {
        this.plugin = plugin;

        plugin.getRedisManager().registerChannelSubscriber("INTERNAL", this);

    }

    @Override
    public void onChannelMessage(RedisPubSubMessage message) {
        if (!message.getSubject().equals("UPDATE_PLAYER")) return;
        final String[] args = message.getArgs();

        UUID uuid = UUID.fromString(args[0]);

        AssiPlayer player = null;
        if (!plugin.getPlayerManager().getOnlinePlayers().containsKey(uuid)) {

            if (plugin.getPlayerManager().getOfflinePlayerCache().containsKey(uuid)) {
                player = plugin.getPlayerManager().getOfflinePlayerCache().get(uuid);
            } else return;
        }

        if (player == null) player = plugin.getPlayerManager().getOnlinePlayers().get(uuid);

        String key = args[1];
        String value = args[2];

        switch (key) {
            case "server":
                player.setLastSeenServer(String.valueOf(value));
                break;
            case "discord":
                player.setDiscordAccount(Long.parseLong(value));
                break;
            case "rank":
                player.setRank(Rank.fromString(value));
                break;
            case "last_seen":
                player.setLastSeen(Long.valueOf(value));
                break;
            case "bucks":
                player.setBucks(Integer.parseInt(value));
                break;
            case "ultra_coins":
                player.setUltraCoins(Integer.parseInt(value));
                break;
            case "joins":
                player.setJoins(Integer.parseInt(value));
                break;
            case "votes":
                player.setVotes(Integer.parseInt(value));
                break;
            case "vote_streak":
                player.setVoteStreak(Integer.parseInt(value));
                break;
            case "last_vote":
                player.setLastVote(Long.parseLong(value));
                break;
            case "referred_by":
                player.setReferredBy(value);
                break;
            case "helpop_handled":
                player.setHelpopsHandled(Integer.parseInt(value));
                break;
            case "friends":
                player.getFriendData().setFriends(UtilJson.deserialize(plugin.getPlayerManager().getGson(), new TypeToken<Map<UUID, String>>() {
                }, value));
                break;
            case "friend_settings":
                player.getFriendData().deserializeSettings(value);
                break;
            case "friend_incoming":
                player.getFriendData().setIncoming(UtilJson.deserialize(plugin.getPlayerManager().getGson(), new TypeToken<List<UUID>>() {
                }, value));
                break;
            case "boosters":
                player.setBoosters(UtilJson.deserialize(plugin.getPlayerManager().getGson(), new TypeToken<Map<String, Integer>>() {
                }, value));
                break;
            case "achievements":
                player.setAchievements(UtilJson.deserialize(plugin.getPlayerManager().getGson(), new TypeToken<Map<String, Long>>() {
                }, value));
                break;
            case "achievement_progress":
                player.setAchievementProgress(UtilJson.deserialize(plugin.getPlayerManager().getGson(), new TypeToken<Map<String, String>>() {
                }, value));
                break;
            case "last_reward_claim":
                player.setLastRewardClaim(UtilJson.deserialize(plugin.getPlayerManager().getGson(), new TypeToken<Map<RewardType, Long>>() {
                }, value));
                break;

        }

    }
}
