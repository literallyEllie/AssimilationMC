package net.assimilationmc.assibungee.discord.uhc;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.assimilationmc.assibungee.discord.DiscordManager;
import net.assimilationmc.assibungee.discord.DiscordPresetChannel;
import net.assimilationmc.assibungee.redis.pubsub.RedisChannelSubscriber;
import net.assimilationmc.assibungee.redis.pubsub.RedisPubSubMessage;
import net.assimilationmc.assibungee.util.UtilJson;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.util.Map;

public class GameFeedHandle implements RedisChannelSubscriber {

    private final DiscordManager discordManager;

    private Map<String, GameData> liveFeeds;

    public GameFeedHandle(DiscordManager discordManager) {
        this.discordManager = discordManager;
        this.liveFeeds = Maps.newHashMap();

        discordManager.getPlugin().getRedisManager().registerChannelSubscriber("UHC", this);
    }

    public void cleanup() {
        liveFeeds.clear();
    }

    @Override
    public void onChannelMessage(RedisPubSubMessage message) {
        final String from = message.getFrom();
        final String[] args = message.getArgs();

        if (message.getSubject().equals("HELLO")) {
            final GameData gameData = new GameData(-1, args[1], Integer.parseInt(args[0]), args[2], Boolean.valueOf(args[3]));

            final EmbedBuilder embedBuilder = discordManager.getEmbedBuilder(DiscordManager.DiscordColor.NEUTRAL);
            embedBuilder.setTitle(from);
            embedBuilder.setDescription("A new game is happening!");
            embedBuilder.addField("Map:", gameData.getMap(), true)
                .addField("Game type:", gameData.getType() + (gameData.isCustom() ? " [CUSTOM]" : ""), true)
                .addField("Max players:", String.valueOf(gameData.getMaxPlayers()), true);
            embedBuilder.setFooter("Join " + from + " now!", DiscordManager.CAKE_EMBED);

            gameData.setRecordingMessage(discordManager.messageChannel(DiscordPresetChannel.GAME_FEED, embedBuilder.build()));

            discordManager.messageChannel(DiscordPresetChannel.GAME_FEED, "<@472823425732313088>").delete().queue();

            liveFeeds.put(from, gameData);
            return;
        }

        if (message.getSubject().equals("UPDATE")) {
            final GameData gameData = liveFeeds.get(from);
            if (gameData == null) return;

            final Message complete = discordManager.getHook().getTextChannelById(DiscordPresetChannel.GAME_FEED.getId()).getMessageById(gameData.getRecordingMessage()).complete();
            if (complete == null) {
                liveFeeds.remove(from);
                return;
            }

            String updated = message.getArgs()[0];
            if (updated.equals("ONLINE")) return;

            switch (updated.toUpperCase()) {
                case "GAME_PHASE":
                    gameData.setGamePhase(args[1]);
                    break;
                case "MAX_PLAYERS":
                    gameData.setMaxPlayers(Integer.parseInt(args[1]));
                    break;
                case "WINNER":
                    gameData.setWinners(Lists.newArrayList(args[1]));
                    break;
                case "WINNERS":
                    gameData.setWinners(UtilJson.deserialize(discordManager.getPlugin().getPlayerManager().getGson(), args[1]));
                    break;
            }

            final EmbedBuilder embedBuilder = discordManager.getEmbedBuilder(DiscordManager.DiscordColor.NEUTRAL);
            embedBuilder.setTitle(from);
            embedBuilder.addField("Map:", gameData.getMap(), true)
                    .addField("Game type:", gameData.getType() + (gameData.isCustom() ? " [CUSTOM]" : ""), true);

            switch (gameData.getGamePhase()) {
                case "WARMUP":
                    embedBuilder.setDescription("Warming up");
                    break;
                case "IN_GAME":
                    embedBuilder.setDescription("In-game");
                    break;
                case "END":
                    embedBuilder.setDescription("Ended");
                    embedBuilder.addField("Winner:", gameData.getWinners() != null ? Joiner.on(", ").join(gameData.getWinners()) : "None", true);
            }

            complete.editMessage(embedBuilder.build()).queue();

            if (gameData.getGamePhase().equals("END"))
                liveFeeds.remove(from);
            return;
        }

        if (message.getSubject().equals("BYE")) {
            final GameData gameData = liveFeeds.get(from);
            if (gameData == null) return;

            final Message complete = discordManager.getHook().getTextChannelById(DiscordPresetChannel.GAME_FEED.getId()).getMessageById(gameData.getRecordingMessage()).complete();
            if (complete == null) {
                liveFeeds.remove(from);
                return;
            }

            complete.delete().queue();
            liveFeeds.remove(from);
            return;
        }

    }




}
