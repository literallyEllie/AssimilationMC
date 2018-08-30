package net.assimilationmc.assicore.helpop;

import com.google.common.collect.Maps;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.Module;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.redis.pubsub.PubSubRecipient;
import net.assimilationmc.assicore.redis.pubsub.RedisPubSubMessage;
import net.assimilationmc.assicore.staff.StaffChatManager;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilPlayer;
import net.assimilationmc.assicore.util.UtilTime;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class HelpOPManager extends Module {

    private final Map<UUID, HelpOP> helpOPs = Maps.newHashMap();
    private int helpOPcounter;

    public HelpOPManager(AssiPlugin plugin) {
        super(plugin, "HelpOP");
        this.helpOPcounter = 0;
    }

    @Override
    protected void start() {
        getPlugin().getCommandManager().registerCommand(new CmdHelpop(this));
    }

    @Override
    protected void end() {
        helpOPs.clear();
    }

    /**
     * Get a list of pending HelpOPs. <Sender, HelpOP object>
     *
     * @return Pending HelpOPs
     */
    public Map<UUID, HelpOP> getHelpOPs() {
        return helpOPs;
    }

    public HelpOP getHelpOP(int id) {
        final Optional<Map.Entry<UUID, HelpOP>> helpOPEntry = helpOPs.entrySet().stream().filter(uuidHelpOPEntry -> uuidHelpOPEntry.getValue().getId() == id).findFirst();
        if (helpOPEntry.isPresent())
            return helpOPEntry.get().getValue();
        return null;
    }

    /**
     * Send in a new HelpOP
     *
     * @param sender  Sender of the HelpOP
     * @param content Contents of the HelpOP
     */
    public void postHelpOP(Player sender, String content) {
        final HelpOP lastHelpOP = lastHelpOP(sender.getUniqueId());
        if (lastHelpOP != null) {
            long sent = lastHelpOP.getSent();

            if (!UtilTime.elapsed(sent, TimeUnit.MINUTES.toMillis(5))) {
                sender.sendMessage(new String[]{C.II + "You cannot send another HelpOP until " + C.V +
                        (TimeUnit.MINUTES.toSeconds(5) - TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - sent)) + " seconds.",
                        C.C + "Pending HelpOP (#" + C.V + lastHelpOP.getId() + C.C + ")" + ChatColor.DARK_PURPLE + ":",
                        C.C + "Content: " + C.V + lastHelpOP.getContent()});
                return;
            }
            sender.sendMessage(C.C + "Expired your old HelpOP.");
            helpOPs.remove(sender.getUniqueId());
        }

        final HelpOP helpOP = new HelpOP(++helpOPcounter, sender.getUniqueId(), getPlugin().getServerData().getId(), content);
        helpOP.setSenderOfflineName(sender.getName());
        helpOPs.put(sender.getUniqueId(), helpOP);

        sender.sendMessage(C.C + "You have sent a HelpOP to online staff (#" + C.V + helpOPcounter + C.V + ") of: " + C.V + content);

        getPlugin().getStaffChatManager().messageGenericLocal(helpOP.pretty());

        // send
        getPlugin().getRedisManager().sendPubSubMessage(StaffChatManager.REDIS_CHANNEL, new RedisPubSubMessage(PubSubRecipient.ALL,
                getPlugin().getServerData().getId(), "HELPOP_SEND", helpOP.serialise()));
    }

    /**
     * Set a HelpOP handled.
     *
     * @param uuid    Person-needing-help's-uuid
     * @param handler The name of the person handling it.
     * @param rem     Is it a remote handling? If it isn't, other servers will be notified.
     */
    public void handleHelpOP(UUID uuid, String handler, boolean rem) {
        final HelpOP helpOP = helpOPs.get(uuid);
        if (helpOP == null) return;

        helpOP.handle(handler);
        final Player player = UtilPlayer.get(helpOP.getSender());
        if (player != null) {
            player.sendMessage(C.II + "Your HelpOP (#" + C.V + helpOP.getId() + C.II + ") " +
                    "is now being handled by " + C.V + helpOP.getHandler() + C.II + ".");
        }

        if (UtilPlayer.get(handler) != null) {
            AssiPlayer handle = getPlugin().getPlayerManager().getOnlinePlayers().get(UtilPlayer.get(handler).getUniqueId());
            if (handle != null) {
                handle.addHelpopHandle();
            }
        }

        if (!rem) {
            getPlugin().getRedisManager().sendPubSubMessage(StaffChatManager.REDIS_CHANNEL, new RedisPubSubMessage(PubSubRecipient.ALL,
                    getPlugin().getServerData().getId(), "HELPOP_HANDLE", helpOP.serialise()));
        }

        getPlugin().getStaffChatManager().messageGenericLocal(C.II + ChatColor.BOLD.toString() + "[HANDLED] " + ChatColor.RESET + C.C + "HelpOP #"
                + C.V + helpOP.getId() + ChatColor.DARK_PURPLE + " - " + C.C + "Handler: " + C.V +
                helpOP.getHandler() + ChatColor.DARK_PURPLE + " - " + C.V + helpOP.getContent());

    }

    /**
     * Set a HelpOP handled but by ID. Don't call from redis.
     *
     * @param id           HelpOP ID
     * @param handler      The name of the person handling it.
     * @param sayIfInvalid If its invalid, try and find the attempted handler and notify them
     */
    public void handleHelpOP(int id, String handler, boolean sayIfInvalid) {
        HelpOP helpOP = getHelpOP(id);

        if (helpOP != null) {
            if (helpOP.isHandled()) {
                final Player resp = UtilPlayer.get(handler);
                if (resp == null) return;
                resp.sendMessage(C.II + "This HelpOP has already been handled by " + C.V + helpOP.getHandler() + C.II + ".");
                return;
            }

            handleHelpOP(helpOP.getSender(), handler, false);
            // send message to staff chat

        } else if (sayIfInvalid) {
            Player resp = UtilPlayer.get(handler);
            if (resp == null) return;
            resp.sendMessage(C.II + "HelpOP with id " + C.V + id + C.II + " does not exist.");
        }
    }

    /**
     * Set a HelpOP handled but by the person's name who sent it
     *
     * @param sender       Name of HelpOP sender
     * @param handler      The name of the person handling it.
     * @param sayIfInvalid If its invalid, try and find the attempted handler and notify them.
     */
    public void handleHelpOP(String sender, String handler, boolean sayIfInvalid) {
        final Optional<Map.Entry<UUID, HelpOP>> helpOPEntry = helpOPs.entrySet().stream().filter(uuidHelpOPEntry ->
                uuidHelpOPEntry.getValue().getSenderOfflineName().equalsIgnoreCase(sender)).findFirst();
        if (helpOPEntry != null && helpOPEntry.isPresent()) {
            Map.Entry<UUID, HelpOP> helpop = helpOPEntry.get();
            handleHelpOP(helpop.getKey(), handler, false);

            helpOPs.remove(helpop.getKey());

            // send message to staff chat
        } else if (sayIfInvalid) {
            Player resp = UtilPlayer.get(handler);
            if (resp == null) return;
            resp.sendMessage(C.II + "There are no pending HelpOPs from " + C.V + sender + C.II + ".");
        }
    }

    /**
     * Get the last HelpOP of a player.
     *
     * @param uuid The uuid of the player to get.
     * @return The last HelpOp they sent, or null of there is none.
     */
    public HelpOP lastHelpOP(UUID uuid) {
        return helpOPs.get(uuid);
    }

    public void setHelpOPCounter(int helpOPCounter) {
        if (this.helpOPcounter <= helpOPCounter) return;
        this.helpOPcounter = helpOPCounter;
    }


}