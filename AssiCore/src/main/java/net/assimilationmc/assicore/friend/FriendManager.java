package net.assimilationmc.assicore.friend;

import com.google.gson.reflect.TypeToken;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.Module;
import net.assimilationmc.assicore.event.PlayerJoinNetworkEvent;
import net.assimilationmc.assicore.friend.command.CmdFriend;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.redis.pubsub.PubSubRecipient;
import net.assimilationmc.assicore.redis.pubsub.RedisChannelSubscriber;
import net.assimilationmc.assicore.redis.pubsub.RedisPubSubMessage;
import net.assimilationmc.assicore.util.*;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class FriendManager extends Module implements RedisChannelSubscriber {

    private final int FRIENDS_DEFAULT = 20, FRIENDS_DEMONIC = 50, FRIENDS_INFERNAL = 100;

    public FriendManager(AssiPlugin plugin) {
        super(plugin, "Friend Manager");
    }

    @Override
    protected void start() {

        getPlugin().getCommandManager().registerCommand(new CmdFriend(getPlugin(), this));
        getPlugin().getRedisManager().registerChannelSubscriber("FRIEND", this);

    }

    @Override
    protected void end() {

    }

    @EventHandler
    public void on(final PlayerJoinNetworkEvent e) {
        final AssiPlayer player = e.getPlayer();

        if (player.getFriendData().isSendJoinLeave()) {
            this.broadcast(player, C.C + "[" + ChatColor.GOLD + "Friend Join" + C.C + "] "
                    + player.getDisplayName() + C.C + ChatColor.ITALIC + " joined " + getPlugin().getServerData().getId(), "JL");
        }
    }

    /**
     * Method to call when sending a friend request.
     *
     * @param sender the person sending the request.
     * @param target the person who is the target of said request.
     */
    public void sendFriendRequest(AssiPlayer sender, UUID target) {

        if (sender.getUuid().equals(target)) {
            sender.sendMessage(C.II + "You can't add yourself :(");
            return;
        }

        FriendData data = sender.getFriendData();

        if ((sender.getRank() == Rank.PLAYER && data.getFriends().size() > FRIENDS_DEFAULT)
                || (sender.getRank() == Rank.DEMONIC && data.getFriends().size() > FRIENDS_DEMONIC)
                || (sender.getRank().isHigherThanOrEqualTo(Rank.INFERNAL) && data.getFriends().size() > FRIENDS_INFERNAL)) {
            sender.sendMessage(C.II + "Sorry! It looks like you've hit the max friend limit for your rank.");
            if (!sender.getRank().isHigherThanOrEqualTo(Rank.INFERNAL)) {
                sender.sendMessage(ChatColor.AQUA + "No worries! You can get some more with one of our donator ranks. See " + C.V + Domain.PROT_STORE);
            }
            return;
        }

        if (data.getFriends().containsKey(target)) {
            sender.sendMessage(C.C + "You are already friends with that player.");
            return;
        }

        if (UtilPlayer.get(target) == null) {
            final String server = getPlugin().getPlayerFinder().findPlayer(target);
            if (server == null) {
                sender.sendMessage(C.C + "That player doesn't appear to be online.");
                return;
            }

            getPlugin().getRedisManager().sendPubSubMessage("FRIEND", new RedisPubSubMessage(server, getPlugin().getServerData().getId(),
                    "SEND", new String[]{sender.getUuid().toString(), target.toString(), sender.getName()}));

            return;
        }

        AssiPlayer assiTarget = getPlugin().getPlayerManager().getPlayer(target);
        handleSend(assiTarget, sender.getUuid(), sender.getName());
    }

    private void handleSend(AssiPlayer target, UUID sender, String senderName) {

        if (!target.getFriendData().isAllowRequests()) {
            getPlugin().getPlayerManager().attemptGlobalPlayerMessage(sender, false, C.II + "You may not send requests to this player.");
            return;
        }

        if (target.getFriendData().getIncoming().contains(sender)) {
            getPlugin().getPlayerManager().attemptGlobalPlayerMessage(sender, false, C.II + "You have already sent a friend request to that player.");
            return;
        }

        // TODO set expire
        target.getFriendData().addIncoming(sender);

        target.sendMessage(C.C);
        target.sendMessage(C.C + "You have received a friend request from " + C.II + senderName);
        target.getBase().spigot().sendMessage(new ComponentBuilder("          ACCEPT").color(net.md_5.bungee.api.ChatColor.GREEN).bold(true)
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fr accept " + sender))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to accept this request").color(net.md_5.bungee.api.ChatColor.GRAY).create()))
                .append("     ")
                .append("DECLINE").color(net.md_5.bungee.api.ChatColor.RED).bold(true)
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fr decline " + sender))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to decline this request").color(net.md_5.bungee.api.ChatColor.GRAY).create()))
                .create());
        target.sendMessage(C.C);

        getPlugin().getPlayerManager().attemptGlobalPlayerMessage(sender, false, C.II + "Friend request sent to " + target.getDisplayName());
    }

    /**
     * Method to be called when a player wishes to accept a request sent to them.
     *
     * @param acceptor the person who is accepting the request.
     * @param target   the UUID of the person who sent it.
     */
    public void acceptRequest(AssiPlayer acceptor, UUID target) {

        FriendData data = acceptor.getFriendData();
        if (data.getFriends().containsKey(target)) {
            acceptor.sendMessage(C.C + "You are already friends with that player.");
            return;
        }

        if ((acceptor.getRank() == Rank.PLAYER && data.getFriends().size() > FRIENDS_DEFAULT)
                || (acceptor.getRank() == Rank.DEMONIC && data.getFriends().size() > FRIENDS_DEMONIC)
                || (acceptor.getRank().isHigherThanOrEqualTo(Rank.INFERNAL) && data.getFriends().size() > FRIENDS_INFERNAL)) {
            acceptor.sendMessage(C.II + "Sorry! It looks like you've hit the max friend limit for your rank.");
            if (!acceptor.getRank().isHigherThanOrEqualTo(Rank.INFERNAL)) {
                acceptor.sendMessage(ChatColor.AQUA + "No worries! You can get some more with one of our donator ranks. See " + C.V + Domain.PROT_STORE);
            }

            data.removeIncoming(target);
            return;
        }

        if (!data.getIncoming().contains(target)) {
            acceptor.sendMessage(C.C + "You have no incoming friend requests from that player.");
            return;
        }

        if (UtilPlayer.get(target) == null) {
            final String server = getPlugin().getPlayerFinder().findPlayer(target);
            if (server == null) {
                acceptor.sendMessage(C.C + "That player doesn't appear to be online.");
                data.removeIncoming(target);
                return;
            }

            getPlugin().getRedisManager().sendPubSubMessage("FRIEND", new RedisPubSubMessage(server, getPlugin().getServerData().getId(),
                    "ACCEPT", new String[]{acceptor.getUuid().toString(), target.toString(), acceptor.getName()}));

            return;
        }

        AssiPlayer assiTarget = getPlugin().getPlayerManager().getPlayer(target);
        handleAccept(assiTarget, acceptor.getUuid(), acceptor.getName());
    }

    private void handleAccept(AssiPlayer target, UUID sender, String senderName) {
        if (target.getFriendData().getFriends().containsKey(sender)) {
            getPlugin().getPlayerManager().attemptGlobalPlayerMessage(sender, false, C.II + "You are already friends with that player.");
            return;
        }

        target.getFriendData().removeIncoming(sender);
        target.getFriendData().addFriend(sender, senderName);

        final AssiPlayer player = getPlugin().getPlayerManager().getPlayer(sender);
        player.getFriendData().addFriend(target.getUuid(), target.getName());

        UtilServer.callEvent(new FriendMakeEvent(player, target));

        target.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "You are now friends with " + C.V + senderName);
        target.getBase().playSound(target.getBase().getLocation(), Sound.LEVEL_UP, 30, 20);

        getPlugin().getPlayerManager().attemptGlobalPlayerMessage(sender, false,
                ChatColor.GREEN + ChatColor.BOLD.toString() + "You are now friends with " + C.V + target.getName());

    }

    /**
     * The method to call when a player wants to decline a request from a player.
     *
     * @param decliner the person declining the request.
     * @param target   the person who sent the request.
     */
    public void declineRequest(AssiPlayer decliner, UUID target) {

        FriendData data = decliner.getFriendData();
        if (data.getFriends().containsKey(target)) {
            decliner.sendMessage(C.C + "You are already friends with that player.");
            return;
        }

        if (!data.getIncoming().contains(target)) {
            decliner.sendMessage(C.C + "You have no incoming friend requests from that player.");
            return;
        }

        if (UtilPlayer.get(target) == null) {
            final String server = getPlugin().getPlayerFinder().findPlayer(target);
            if (server == null) {
                decliner.sendMessage(C.C + "That player doesn't appear to be online.");
                data.removeIncoming(target);
                return;
            }

            getPlugin().getRedisManager().sendPubSubMessage("FRIEND", new RedisPubSubMessage(server, getPlugin().getServerData().getId(),
                    "DECLINE", new String[]{decliner.getUuid().toString(), target.toString(), decliner.getName()}));
            return;
        }

        AssiPlayer assiTarget = getPlugin().getPlayerManager().getPlayer(target);
        handleDecline(assiTarget, decliner.getUuid());

    }

    private void handleDecline(AssiPlayer target, UUID decliner) {

        if (target.getFriendData().getFriends().containsKey(decliner)) {
            getPlugin().getPlayerManager().attemptGlobalPlayerMessage(decliner, false, C.II + "You are already friends with that player.");
            return;
        }

        final FriendData friendData = getPlugin().getPlayerManager().getPlayer(decliner).getFriendData();

        if (!friendData.getIncoming().contains(target.getUuid())) {
            getPlugin().getPlayerManager().attemptGlobalPlayerMessage(decliner, false, C.II + "You have no incoming requests from that player.");
            return;
        }

        friendData.removeIncoming(target.getUuid());
        getPlugin().getPlayerManager().attemptGlobalPlayerMessage(decliner, false, C.II + "You declined the friend request from " + C.V + target.getDisplayName());
    }

    public String removeFriend(AssiPlayer player, UUID toRemove) {

        final FriendData friendData = player.getFriendData();
        if (!friendData.getFriends().containsKey(toRemove)) {
            return "REFRESH_NFS";
        }

        // Remove self
        friendData.removeFriend(toRemove);

        // Remove target
        FriendData targetData = getPlugin().getPlayerManager().getPlayer(toRemove).getFriendData();
        targetData.removeFriend(player.getUuid());

        player.sendMessage(C.II + "You are no longer friends with that player.");

        return "S";
    }

    public void friendBroadcast(AssiPlayer player, String message) {
        String toSend = ChatColor.YELLOW + "Friends" + C.SS + C.V + player.getName() + ChatColor.GRAY + ": " + ChatColor.GREEN + message;
        player.sendMessage(toSend);

        broadcast(player, toSend, "BC");
    }

    public void broadcast(AssiPlayer sender, String message, String topic) {
        getPlugin().getRedisManager().sendPubSubMessage("FRIEND", new RedisPubSubMessage(PubSubRecipient.SPIGOT, getPlugin().getServerData().getId(),
                "BROADCAST", new String[]{sender.getUuid().toString(), message}));

        for (UUID uuid : sender.getFriendData().getFriends().keySet()) {
            Player tPlayer = UtilPlayer.get(uuid);
            if (tPlayer == null) continue;
            AssiPlayer assiPlayer = getPlugin().getPlayerManager().getPlayer(uuid);

            if ((topic.equals("JL") && assiPlayer.getFriendData().isSeeJoinLeave())
                    || (topic.equals("BC") && assiPlayer.getFriendData().isSeeFriendBroadcast())) {
                tPlayer.sendMessage(message);
            }
        }
    }

    @Override
    public void onChannelMessage(RedisPubSubMessage message) {
        final String[] args = message.getArgs();

        if (message.getSubject().equals("SEND")) {
            UUID sender = UUID.fromString(args[0]);
            UUID target = UUID.fromString(args[1]);
            String senderName = args[2];

            if (UtilPlayer.get(target) == null) {
                getPlugin().getPlayerManager().attemptGlobalPlayerMessage(sender, false, C.C + "That player doesn't appear to be online.");
                return;
            }

            handleSend(getPlugin().getPlayerManager().getPlayer(target), sender, senderName);
        }

        if (message.getSubject().equals("ACCEPT")) {
            UUID sender = UUID.fromString(args[0]);
            UUID target = UUID.fromString(args[1]);
            String senderName = args[2];

            if (UtilPlayer.get(target) == null) {
                getPlugin().getPlayerManager().attemptGlobalPlayerMessage(sender, false, C.C + "That player doesn't appear to be online.");
                return;
            }

            handleAccept(getPlugin().getPlayerManager().getPlayer(target), sender, senderName);
        }

        if (message.getSubject().equals("DECLINE")) {
            UUID sender = UUID.fromString(args[0]);
            UUID target = UUID.fromString(args[1]);

            if (UtilPlayer.get(target) == null) {
                getPlugin().getPlayerManager().attemptGlobalPlayerMessage(sender, false, C.C + "That player doesn't appear to be online.");
                return;
            }

            handleDecline(getPlugin().getPlayerManager().getPlayer(target), sender);
        }

        if (message.getSubject().equals("BROADCAST")) {
            UUID sender = UUID.fromString(args[0]);
            String toSend = args[1];

            for (AssiPlayer assiPlayer : getPlugin().getPlayerManager().getOnlinePlayers().values()) {
                if (assiPlayer.getFriendData().getFriends().containsKey(sender) && assiPlayer.getFriendData().isSeeFriendBroadcast()) {
                    assiPlayer.sendMessage(toSend);
                }
            }

        }

        if (message.getSubject().equals("LEAVE")) {
            String leaverName = args[0];
            final List<UUID> uuid = UtilJson.deserialize(getPlugin().getPlayerManager().getGson(), new TypeToken<List<String>>() {
            }, args[1])
                    .stream().map(UUID::fromString).collect(Collectors.toList());

            for (UUID friendUuid : uuid) {
                Player friend = UtilPlayer.get(friendUuid);
                if (friend == null) continue;

                friend.sendMessage(C.C + "[" + ChatColor.GOLD + "Friend Leave" + C.C + "] " + leaverName + C.C + ChatColor.ITALIC + " left the network.");
            }


        }


    }

}
