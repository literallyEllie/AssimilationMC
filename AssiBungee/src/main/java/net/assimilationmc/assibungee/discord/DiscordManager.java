package net.assimilationmc.assibungee.discord;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.assimilationmc.assibungee.AssiBungee;
import net.assimilationmc.assibungee.Module;
import net.assimilationmc.assibungee.discord.command.DMicroCommand;
import net.assimilationmc.assibungee.discord.command.DiscordCommand;
import net.assimilationmc.assibungee.discord.uhc.GameFeedHandle;
import net.assimilationmc.assibungee.hook.AssiHook;
import net.assimilationmc.assibungee.player.BungeePlayerManager;
import net.assimilationmc.assibungee.rank.Rank;
import net.assimilationmc.assibungee.redis.RedisDatabaseIndex;
import net.assimilationmc.assibungee.redis.pubsub.RedisChannelSubscriber;
import net.assimilationmc.assibungee.redis.pubsub.RedisPubSubMessage;
import net.assimilationmc.assibungee.util.Domain;
import net.assimilationmc.assibungee.util.TempMap;
import net.assimilationmc.assibungee.util.UtilPlayer;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.*;
import net.md_5.bungee.api.plugin.Listener;
import org.reflections.Reflections;
import redis.clients.jedis.Jedis;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class DiscordManager extends Module implements AssiHook<JDA>, Listener, RedisChannelSubscriber {

    public static final File FILE = new File("DISCORD");
    public static final String CAKE_EMBED = "http://assimilationmc.net/assets/images/cake500x500.png";
    public static long PRESUMED_SELF = 301722680028037130L;
    private final Pattern PATTERN_USER = Pattern.compile("<@!?\\d{17,20}>");

    private JDA jda;
    private DiscordBotData discordBotData;

    private Map<String, DiscordCommand> commandMap;
    private Map<Long, DiscordLinkData> linkDataMap;

    private GameFeedHandle gameFeedHandle;

    public DiscordManager(AssiBungee plugin) {
        super(plugin, "Discord");
    }

    @Override
    protected void start() {
        this.discordBotData = new DiscordPropertyReader(FILE).readDiscord();
        if (this.discordBotData == null) throw new IllegalArgumentException("Discord request invalid!");

        try {

            jda = new JDABuilder(AccountType.BOT)
                    .setToken(discordBotData.getToken())
                    .setGame(discordBotData.getGame())
                    .setStatus(discordBotData.getOnlineStatus())
                    .addEventListener(new DiscordListener(this))
                    .buildBlocking();

            commandMap = Maps.newHashMap();

            new Reflections("net.assimilationmc.assibungee.discord.command")
                    .getSubTypesOf(DiscordCommand.class).forEach(commandClass -> {
                try {
                    final DiscordCommand discordCommand = (DiscordCommand) commandClass.getConstructors()[0].newInstance(this);
                    registerCommand(discordCommand);
                } catch (InstantiationException | IllegalAccessException | IllegalArgumentException |
                        InvocationTargetException e) {
                    if (!e.getMessage().equals("wrong number of arguments")) {
                        log(Level.WARNING, "Failed to load Discord command " + commandClass.getSimpleName());
                        e.printStackTrace();
                    }

                }
            });

            registerMicroCommands();

            getPlugin().getRedisManager().registerChannelSubscriber("DISCORD", this);

            this.linkDataMap = new TempMap<>(getPlugin(), TempMap.ExpireTimerPolicy.ACCESS, 5, TimeUnit.MINUTES);

            this.gameFeedHandle = new GameFeedHandle(this);

        } catch (LoginException | InterruptedException e) {
            log(Level.SEVERE, "Failed to log in to Discord!");
            e.printStackTrace();
        }

    }

    @Override
    protected void end() {
        if (jda != null) jda.shutdown();
        if (gameFeedHandle != null) gameFeedHandle.cleanup();
    }

    @Override
    public JDA getHook() {
        return jda;
    }

    public DiscordLinkData getDiscordAccountOf(String mcName) {

        UUID uuid;
        if (UtilPlayer.get(mcName) != null) {
            uuid = UtilPlayer.get(mcName).getUniqueId();
        } else {
            uuid = getPlugin().getPlayerManager().getUuidSql(mcName);

            if (uuid == null)
                return null;

        }

        long discordId = 0;
        String name = null;
        Rank rank = null;

        try (Jedis jedis = getPlugin().getRedisManager().getPool().getResource()) {
            jedis.select(RedisDatabaseIndex.DATA_USERS);
            String key = getPlugin().getPlayerManager().redisKey(uuid);
            if (jedis.hexists(key, "discord")) {
                discordId = Long.parseLong(jedis.hget(key, "discord"));
                name = jedis.hget(key, "name");
                rank = Rank.fromString(jedis.hget(key, "rank"));
            }
        }

        if (name == null) {

            try (Connection connection = getPlugin().getSqlManager().getConnection()) {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT name, rank, discord_account FROM " + BungeePlayerManager.TABLE_PLAYERS + " WHERE " +
                        "uuid = ?");
                preparedStatement.setString(1, uuid.toString());
                final ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    mcName = resultSet.getString("name");
                    rank = Rank.fromString(resultSet.getString("rank"));
                    discordId = Long.parseLong(resultSet.getString("discord_account"));
                }
                resultSet.close();
                preparedStatement.close();

            } catch (SQLException e) {
                getPlugin().getLogger().severe("Failed get discord account of " + mcName + "!");
                e.printStackTrace();
                return null;
            }
        }

        final DiscordLinkData discordLinkData = new DiscordLinkData(discordId, uuid, mcName, rank);
        linkDataMap.put(discordId, discordLinkData);
        return discordLinkData;
    }

    public DiscordLinkData getData(long discordId) {
        if (linkDataMap.containsKey(discordId))
            return linkDataMap.get(discordId);

        UUID uuid = null;
        String name = null;
        Rank rank = null;

        try (Connection connection = getPlugin().getSqlManager().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT uuid, name, rank FROM " + BungeePlayerManager.TABLE_PLAYERS + " WHERE " +
                    "discord_account = ?");
            preparedStatement.setLong(1, discordId);
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                uuid = UUID.fromString(resultSet.getString("uuid"));
                name = resultSet.getString("name");
                rank = Rank.fromString(resultSet.getString("rank"));
            }
            resultSet.close();
            preparedStatement.close();

        } catch (SQLException e) {
            getPlugin().getLogger().severe("Failed get data of Discord account " + discordId + "!");
            e.printStackTrace();
            return null;
        }

        if (uuid == null) return null;

        DiscordLinkData discordLinkData = new DiscordLinkData(discordId, uuid, name, rank);
        linkDataMap.put(discordId, discordLinkData);
        return discordLinkData;
    }

    private void registerMicroCommands() {

        registerCommand(new DMicroCommand(this, "web", "You can find our website at " + Domain.PROT_WEB, Lists.newArrayList()));
        registerCommand(new DMicroCommand(this, "forum", "You can find our forums at " + Domain.PROT_FORUM,
                Lists.newArrayList("forums")));
        registerCommand(new DMicroCommand(this, "store", "Thanks for looking to donate! " +
                "You can find our **Donation Store** at " + Domain.PROT_STORE, Lists.newArrayList("donate")));
        registerCommand(new DMicroCommand(this, "ip", "Our server IP is " + Domain.IP, Lists.newArrayList()));

    }

    @Override
    public void onChannelMessage(RedisPubSubMessage message) {
        final String[] args = message.getArgs();

        switch (message.getSubject().toUpperCase()) {
            case "MESSAGE":
                try {
                    DiscordPresetChannel presetChannel = DiscordPresetChannel.valueOf(args[0]);
                    messageChannel(presetChannel.getId(), args[1]);
                } catch (IllegalArgumentException e) {
                    getPlugin().getLogger().severe("Invalid discord channel to send to at " + args[0] + "!");
                }
                break;
        }

    }

    /**
     * The regex pattern for matching to a user ID
     *
     * @return regex pattern for Discord user ID
     */
    public Pattern getPatternUser() {
        return PATTERN_USER;
    }

    /***
     * Get the read-only discord bot request. Modifying it directly will have no effect.
     * @return An instance of a {@link DiscordBotData} generated on startup
     */
    public DiscordBotData getDiscordBotData() {
        return discordBotData;
    }

    /**
     * Get all registered commands. Discord commands are registered on startup using Reflections API.
     *
     * @return An unmodifiable map of the command label, lower-case and the corresponding command object.
     */
    public Map<String, DiscordCommand> getCommandMap() {
        return Collections.unmodifiableMap(commandMap);
    }

    /**
     * Get a Discord command object
     *
     * @param label      The label of command
     * @param checkAlias Should it check the aliases of every command if there is no match for the label
     * @return The command object, or null if there was no result.
     */
    public DiscordCommand getCommand(String label, boolean checkAlias) {
        if (commandMap.containsKey(label.toLowerCase())) return commandMap.get(label.toLowerCase());
        return checkAlias ? commandMap.values().stream().filter(discordCommand -> discordCommand.getAliases().contains(label))
                .findFirst().orElse(null) : null;
    }

    /**
     * Get a Discord command object. Not checking aliases.
     *
     * @param label The label of the command
     * @return of getCommand, but not checking for aliases
     */
    public DiscordCommand getCommand(String label) {
        return getCommand(label, false);
    }

    /**
     * Register a Discord command
     *
     * @param command the command instance.
     */
    public void registerCommand(DiscordCommand command) {
        this.commandMap.put(command.getLabel().toLowerCase(), command);
    }

    public GameFeedHandle getGameFeedHandle() {
        return gameFeedHandle;
    }

    /**
     * Message a discord channel
     *
     * @param channel Channel ID
     * @param message Message to send
     */
    public Message messageChannel(long channel, String message) {
        return jda.getTextChannelById(channel).sendMessage(message).complete();
    }

    /**
     * Message a discord channel with a preset ID
     *
     * @param channel channel to message
     * @param message Message to send
     */
    public Message messageChannel(DiscordPresetChannel channel, String message) {
        return messageChannel(channel.getId(), message);
    }

    /**
     * Message any channel that implements {@link Channel}
     *
     * @param channel Channel ID
     * @param message Message to send
     */
    public Message messageChannel(Channel channel, String message) {
        return messageChannel(channel.getIdLong(), message);
    }

    /**
     * Message a discord channel
     *
     * @param channel Channel ID
     * @param message embed to send
     */
    public long messageChannel(long channel, MessageEmbed message) {
        return jda.getTextChannelById(channel).sendMessage(message).complete().getIdLong();
    }

    /**
     * Message a discord channel with a preset ID
     *
     * @param channel channel to message
     * @param message embed to send
     */
    public long messageChannel(DiscordPresetChannel channel, MessageEmbed message) {
        return messageChannel(channel.getId(), message);
    }

    /**
     * Message any channel that implements {@link Channel}
     *
     * @param channel Channel ID
     * @param message embed to send
     */
    public void messageChannel(Channel channel, MessageEmbed message) {
        messageChannel(channel.getIdLong(), message);
    }

    /**
     * Send a temporary message to a discord channel
     *
     * @param channel    The channel to send to
     * @param message    The message content
     * @param expireTime After what delay should the message be deleted
     * @param unit       the time unit of the expireTime
     */
    public void tempMessage(long channel, String message, int expireTime, TimeUnit unit, Message cleanupMsg) {
        jda.getTextChannelById(channel).sendMessage(message).queue(message1 -> {
            message1.delete().queueAfter(expireTime, unit);
            if (cleanupMsg != null) cleanupMsg.delete().queueAfter(expireTime, unit);
        });
    }

    /**
     * Send a temporary message to a discord channel that is preset
     *
     * @param channel    The channel to send to
     * @param message    The message content
     * @param expireTime After what delay should the message be deleted
     * @param unit       the time unit of the expireTime
     */
    public void tempMessage(DiscordPresetChannel channel, String message, int expireTime, TimeUnit unit, Message cleanupMsg) {
        tempMessage(channel.getId(), message, expireTime, unit, cleanupMsg);
    }

    /**
     * Send a temporary message to a discord channel that implements {@link Channel} (aka all of them)
     *
     * @param channel    The channel to send to
     * @param message    The message content
     * @param expireTime After what delay should the message be deleted
     * @param unit       the time unit of the expireTime
     */
    public void tempMessage(Channel channel, String message, int expireTime, TimeUnit unit, Message cleanupMsg) {
        tempMessage(channel.getIdLong(), message, expireTime, unit, cleanupMsg);
    }

    /**
     * Send a private message to a user
     *
     * @param user    User to message
     * @param content the message content
     */
    public void privateMessage(User user, String content) {
        user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(content).queue());
    }

    /**
     * Send a private message to a member
     *
     * @param member  Member to message
     * @param content the message content
     */
    public void privateMessage(Member member, String content) {
        privateMessage(member.getUser(), content);
    }

    /**
     * Attempt to parse from an input string to a {@link User}.
     * Will attempt to parse from: a raw ID, a mention or a User#Discrim
     *
     * @param input The input to parse from.
     * @return the user or null if failed to parse
     */
    public User parseUser(String input) {

        // raw id
        long id;
        try {
            id = Long.parseLong(input);
            return jda.getUserById(id);
        } catch (NumberFormatException e) {
        }

        // a mention
        if (PATTERN_USER.matcher(input).matches()) {
            try {
                id = Long.parseLong((input.replace("<", "")
                        .replace(">", "").replace("@", "")
                        .replace("!", ""))); // idk
                return jda.getUserById(id);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        // accept user#discrim
        if (input.contains("#")) {

            final String[] parts = input.split("#");
            final String name = parts[0];
            final String discrim = parts[1]; // not parsing to int cus 0006 == 6

            for (final User user : jda.getUsersByName(name, true)) {
                if (user.getName().equalsIgnoreCase(name) && user.getDiscriminator().equals(discrim)) {
                    return user;
                }
            }

        }

        return null;
    }

    /**
     * Log a moderation action performed by the bot.
     *
     * @param embedBuilder The embed to log
     */
    public void modLog(EmbedBuilder embedBuilder) {
        messageChannel(DiscordPresetChannel.BOT_LOGS, embedBuilder.build());
    }

    /**
     * Get the default embed builder.
     *
     * @param discordColor The color for the side bit to be
     * @return A embed builder set with a timestamp, color of choose
     * and footer of "AssimilationMC Development Team" and a lovely cake picture.
     */
    public EmbedBuilder getEmbedBuilder(DiscordColor discordColor) {
        return new EmbedBuilder()
                .setColor(discordColor.color)
                .setTimestamp(Instant.now())
                .setFooter("AssimilationMC Development Team", CAKE_EMBED);
    }

    /**
     * Get the default embed builder with a {@link DiscordColor#NEUTRAL} color.
     *
     * @return A embed builder set with a timestamp, {@link DiscordColor#NEUTRAL} color,
     * and footer of "AssimilationMC Development Team" and a lovely cake picture.
     */
    public EmbedBuilder getEmbedBuilder() {
        return getEmbedBuilder(DiscordColor.NEUTRAL);
    }

    public enum DiscordColor {

        KICK(new Color(232, 97, 39)),
        BAN(new Color(183, 39, 11)),

        MESSAGE_DELETE(new Color(32, 73, 155)),

        HELPOP_RECEIVED(new Color(255, 89, 220)),
        HELPOP_UPDATE(new Color(255, 46, 109)),

        RANK_CHANGE(new Color(85, 178, 255)),

        NEUTRAL(new Color(24, 165, 45));

        private Color color;

        DiscordColor(Color color) {
            this.color = color;
        }

    }

}
