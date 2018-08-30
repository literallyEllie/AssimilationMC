package net.assimilationmc.assicore.staff;

import com.google.common.collect.Sets;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.Module;
import net.assimilationmc.assicore.helpop.HelpOP;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.punish.PunishmentManager;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.redis.pubsub.PubSubRecipient;
import net.assimilationmc.assicore.redis.pubsub.RedisChannelSubscriber;
import net.assimilationmc.assicore.redis.pubsub.RedisPubSubMessage;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.D;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import redis.clients.jedis.Jedis;

import java.util.Set;
import java.util.UUID;

public class StaffChatManager extends Module implements RedisChannelSubscriber {

    public static final String REDIS_CHANNEL = "staffchat";
    private final String SET_DISABLED = "staffchat_ignored";

    private final String HOT_KEY = "!", FORMAT =
            ChatColor.BLUE + "Staff Chat " + C.II + "{server}" + C.SS + ChatColor.RESET + "{display_name}" + ChatColor.RESET + ": " + C.II + "{message}";

    private Set<UUID> toggled, disabled;

    public StaffChatManager(AssiPlugin plugin) {
        super(plugin, "Staff Chat");
    }

    @Override
    protected void start() {
        toggled = Sets.newHashSet();
        disabled = Sets.newHashSet();

        if (getPlugin().getRedisManager().getPool() != null) {
            getPlugin().getRedisManager().registerChannelSubscriber(REDIS_CHANNEL, this);

            try (Jedis jedis = getPlugin().getRedisManager().getPool().getResource()) {
                jedis.smembers(SET_DISABLED).forEach(s -> disabled.add(UUID.fromString(s)));
            }
        }

    }

    @Override
    protected void end() {
        toggled.clear();
        disabled.clear();
    }

    @Override
    public void onChannelMessage(RedisPubSubMessage message) {
        String sender = message.getArgs()[0];

        if (message.getSubject().equals("MESSAGE_SEND")) {
            // arg1 is the raw username
            final String server = message.getArgs()[2];
            final String staffMessage = message.getArgs()[3];

            getPlugin().getPlayerManager().getOnlinePlayers().values().stream().filter(player -> player.getRank().isHigherThanOrEqualTo(Rank.HELPER))
                    .forEach(player -> player.sendMessage(FORMAT.replace("{server}", server)
                            .replace("{display_name}", sender).replace("{message}", staffMessage)));
        }

        if (message.getSubject().equals("HELPOP_SEND")) {
            final HelpOP helpOP = new HelpOP(message.getArgs());

            messageGenericLocal(helpOP.pretty());
            getPlugin().getHelpOPManager().getHelpOPs().put(helpOP.getSender(), helpOP);
            getPlugin().getHelpOPManager().setHelpOPCounter(helpOP.getId());
        }

        if (message.getSubject().equals("HELPOP_HANDLE")) {
            HelpOP helpOP = new HelpOP(message.getArgs());
            getPlugin().getHelpOPManager().handleHelpOP(helpOP.getSender(), helpOP.getHandler(), true);
        }

        if (message.getSubject().equals("STAFF_JOIN")) {
            String displayName = message.getArgs()[0];
            msgStaffJoin(displayName);
        }

        if (message.getSubject().equals("STAFF_LEAVE")) {
            String displayName = message.getArgs()[0];
            msgStaffLeave(displayName);
        }

        if (message.getSubject().equals("PUNISH")) {
            String doer = message.getArgs()[0];
            String verb = message.getArgs()[1];
            String thing = message.getArgs()[2];
            String category = message.getArgs()[3];
            String reason = message.getArgs()[4];

            msgPunishUpdate(doer, verb, thing, category, reason);
        }

        if (message.getSubject().equals("TOGGLE")) {
            final UUID uuidSender = UUID.fromString(sender);

            switch (message.getArgs()[1].toLowerCase()) {
                case "off":
                    disabled.remove(uuidSender);
                    break;
                case "on":
                    disabled.add(uuidSender);
                    break;
            }
        }
    }

    /**
     * Send a message to the global staff chat, as well as sends to the local chat.
     *
     * @param sender  the sender, can either be a {@link Player} object or a username.
     *                If the sender is instance of a {@link Player}: the message will be processed to check for action words.
     * @param content the message they sent.
     */
    public void messageGlobalStaffChat(Object sender, String content) {
        if (messageLocalStaffChat(sender, content)) {
            getPlugin().getRedisManager().sendPubSubMessage(REDIS_CHANNEL, new RedisPubSubMessage(PubSubRecipient.ALL, getPlugin().getServerData().getId(),
                    "MESSAGE_SEND", new String[]{(sender instanceof Player ? ((Player) sender).getDisplayName() : String.valueOf(sender)),
                    (sender instanceof Player ? ((Player) sender).getName() : String.valueOf(sender)),
                    getPlugin().getServerData().getId(), content}));
        }
    }

    /**
     * Send a general message to the staff chat.
     *
     * @param message The message to send.
     */
    public void messageGenericLocal(String message) {
        getPlugin().getPlayerManager().getOnlinePlayers().values().forEach(assiPlayer -> {
            if ((assiPlayer.getRank().isHigherThanOrEqualTo(Rank.HELPER) || assiPlayer.getRank() == Rank.DEVELOPER)&& !disabled.contains(assiPlayer.getUuid())) {
                assiPlayer.sendMessage(message);
            }
        });
    }

    /**
     * Send a general message to staff chat.
     *
     * @param baseComponent The message to send.
     */
    public void messageGenericLocal(BaseComponent... baseComponent) {
        getPlugin().getPlayerManager().getOnlinePlayers().values().forEach(assiPlayer -> {
            if ((assiPlayer.getRank().isHigherThanOrEqualTo(Rank.HELPER) || assiPlayer.getRank() == Rank.DEVELOPER) && !disabled.contains(assiPlayer.getUuid())) {
                assiPlayer.getBase().spigot().sendMessage(baseComponent);
            }
        });
    }

    /**
     * Send a local message to notify that a staff member has joined the network.
     *
     * @param displayName The staff display name.
     */
    public void msgStaffJoin(String displayName) {
        messageGenericLocal(FORMAT.replace("{server}", getPlugin().getServerData().getId()).replace("{display_name}", displayName)
                .replace("{message}", C.II + ChatColor.ITALIC + " has joined the network."));
    }

    /**
     * Send a local message to notify that a staff member has left the network.
     *
     * @param displayName The staff display name.
     */
    public void msgStaffLeave(String displayName) {
        messageGenericLocal(FORMAT.replace("{server}", getPlugin().getServerData().getId()).replace("{display_name}", displayName)
                .replace("{message}", C.II + ChatColor.ITALIC + " has left the network."));
    }


    /**
     * Send a local message to notify a player has been punished/unpunished.
     *
     * @param doer   the person who did the action.
     * @param verb   the verb of what they did.
     * @param thing  the thing they did it to.
     * @param reason the reason the action was done.
     */
    public void msgPunishUpdate(String doer, String verb, String thing, String category, String reason) {
        messageGenericLocal(PunishmentManager.PREFIX + C.V + thing + C.II + " was " + C.V + verb + C.II + " by " + C.V + doer + C.II +
                " for " + C.V + "(" + category + ") " + reason);
    }

    /**
     * Send a message to the local staff chat.
     *
     * @param sender  the sender, can either be a {@link Player} object or a username.
     *                If the sender is instance of a {@link Player}: the message will be processed to check for action words.
     * @param content the message they sent.
     * @return If the message should be forwarded or not.
     */
    private boolean messageLocalStaffChat(Object sender, String content) {
        final String arg0 = content.split(" ")[0];

        if (sender instanceof Player) {

            final Player player = (Player) sender;

            if (arg0.equalsIgnoreCase("toggle")) {
                if (toggled.contains(player.getUniqueId())) {
                    toggled.remove(player.getUniqueId());
                } else toggled.add(player.getUniqueId());

                player.sendMessage(C.C + "Staff chat toggled " + C.V + (toggled.contains(player.getUniqueId()) ? "on" : "off") + C.C + ". " +
                        "All your messages will now go directly to the staff chat.");
                return false;
            } else if (arg0.equalsIgnoreCase("off") || arg0.equalsIgnoreCase("on")) {
                if (disabled.contains(player.getUniqueId())) {
                    disabled.remove(player.getUniqueId());

                    if (!getPlugin().getServerData().isLocal()) {
                        try (Jedis jedis = getPlugin().getRedisManager().getPool().getResource()) {
                            jedis.srem(SET_DISABLED, player.getUniqueId().toString());
                        }
                    }
                } else {
                    disabled.add(player.getUniqueId());
                    if (!getPlugin().getServerData().isLocal()) {
                        try (Jedis jedis = getPlugin().getRedisManager().getPool().getResource()) {
                            jedis.sadd(SET_DISABLED, player.getUniqueId().toString());
                        }
                    }
                }

                getPlugin().getRedisManager().sendPubSubMessage(REDIS_CHANNEL, new RedisPubSubMessage(PubSubRecipient.SPIGOT,
                        getPlugin().getServerData().getId(), "TOGGLE", new String[]{player.getUniqueId().toString(), arg0}));

                player.sendMessage(C.C + "Staff chat " + C.V + (disabled.contains(player.getUniqueId()) ? "disabled" : "enabled") + C.C + ". You will " +
                        C.V + (disabled.contains(player.getUniqueId()) ? "no longer" : "now") + C.C + " receive messages from staff chat.");
                return false;
            }
        }

        messageGenericLocal(FORMAT.replace("{server}", getPlugin().getServerData().getId())
                .replace("{display_name}", (sender instanceof Player ? ((Player) sender).getDisplayName() : String.valueOf(sender)))
                .replace("{message}", content));

        return true;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(final AsyncPlayerChatEvent e) {
        final Player bukkitPlayer = e.getPlayer();
        String message = e.getMessage();

        if (message.startsWith(HOT_KEY) && message.length() > HOT_KEY.length()) {
            message = e.getMessage().replaceFirst(HOT_KEY, "");

            final AssiPlayer player = getPlugin().getPlayerManager().getPlayer(bukkitPlayer);

            if (player.getRank().isHigherThanOrEqualTo(Rank.HELPER) || player.getRank() == Rank.DEVELOPER) {
                messageGlobalStaffChat(bukkitPlayer, message);
                e.setCancelled(true);
                return;
            }
        }

        if (toggled.contains(bukkitPlayer.getUniqueId())) {
            messageGlobalStaffChat(bukkitPlayer, message);
            e.setCancelled(true);
        }

    }

}
