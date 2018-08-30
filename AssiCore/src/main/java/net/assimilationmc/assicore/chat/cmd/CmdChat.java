package net.assimilationmc.assicore.chat.cmd;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.chat.ChatPolicy;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilServer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class CmdChat extends AssiCommand {

    public CmdChat(AssiPlugin plugin) {
        super(plugin, "chat", "Chat moderation commands", Rank.MOD, Lists.newArrayList(), "<stat | clear | slow [delay] | lock [rank]>");
    }

    @Override
    public void onCommand(CommandSender sender, String usedLabel, String[] args) {

        final ChatPolicy chatPolicy = plugin.getChatManager().getChatPolicy();

        switch (args[0].toLowerCase()) {
            case "stat":
                sender.sendMessage(prefix(usedLabel) + "Status:");
                if (chatPolicy.hasChatDelay()) {
                    sender.sendMessage(C.C + "Chat on a delay of " + C.V + chatPolicy.getChatDelay() + C.C + " messages player can send per second.");
                }

                if (chatPolicy.isRestrictedChat()) {
                    sender.sendMessage(C.C + "You must be over rank " + chatPolicy.getRequiredRankChat().getPrefix() + C.C + " to chat.");
                }

                if (!chatPolicy.isRestrictedChat() && !chatPolicy.hasChatDelay()) {
                    sender.sendMessage(C.C + "Chat has no active modifiers.");
                }
                break;
            case "clear":
                List<AssiPlayer> clear = plugin.getPlayerManager().getOnlinePlayers().values().stream().filter(player -> !player.getRank().isHigherThanOrEqualTo(Rank.HELPER))
                        .collect(Collectors.toList());
                for (int i = 0; i < 85; i++) {
                    clear.forEach(player -> player.sendMessage(C.C));
                }

                plugin.getStaffChatManager().messageGenericLocal(C.II + "[Staff] Chat was cleared by " + C.V + sender.getName() + C.C + " (for non-staff only).");

                UtilServer.broadcast(C.II);
                UtilServer.broadcast(C.II + "Chat has been cleared!");
                UtilServer.broadcast(C.II);
                return;
            case "slow":
                if (args.length < getRequiredArgs() + 1) {
                    sender.sendMessage(C.II + "Please specify a chat delay. (less than 1 to disable)");
                    return;
                }

                int delay;
                try {
                    delay = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(C.II + "Invalid delay.");
                    return;
                }

                if (chatPolicy.hasChatDelay()) {
                    if (delay <= 0) {
                        UtilServer.broadcast(C.II + "Chat slow mode has been " + C.V + "disabled" + C.II + "!");
                        plugin.getChatManager().getChatDelays().clear();
                    } else {
                        UtilServer.broadcast(C.II);
                        UtilServer.broadcast(C.II + "Chat slow mode has been " + C.V + "updated" + C.II + " to a delay of " + C.V + delay + C.II + " seconds between your messages!");
                        UtilServer.broadcast(C.II);
                    }

                } else {
                    if (delay <= 0) {
                        sender.sendMessage(C.II + "Chat delay is already off.");
                        return;
                    }

                    UtilServer.broadcast(C.II);
                    UtilServer.broadcast(C.II + "Chat slow mode has been " + C.V + "enabled" + C.II + " with a delay of " + C.V + delay + C.II +
                            " seconds between your messages.");
                    UtilServer.broadcast(C.II);
                }

                chatPolicy.setChatDelay(delay);

                plugin.getStaffChatManager().messageGenericLocal(C.II + "[Staff] Chat slow mode " + (chatPolicy.hasChatDelay() ?
                        "enabled" : "disabled") + " by " + C.V + sender.getName() + C.II + ". " + C.C + (chatPolicy.hasChatDelay() ? "Players now must wait " + C.V +
                        chatPolicy.getChatDelay() + C.C + " between messages." : ""));

                return;
            case "lock":
                if (chatPolicy.isRestrictedChat()) {
                    if (sender instanceof Player) {
                        AssiPlayer assiPlayer = asPlayer(sender);
                        if (!assiPlayer.getRank().isHigherThanOrEqualTo(chatPolicy.getRequiredRankChat())) {
                            sender.sendMessage(C.II + "You don't have permission to disable this chat lock.");
                            return;
                        }
                    }

                    chatPolicy.setRequiredRankChat(Rank.PLAYER);
                    UtilServer.broadcast(C.II + "Restricted chat has been " + C.V + "disabled" + C.II + "!");

                } else {
                    if (args.length < getRequiredArgs() + 1) {
                        sender.sendMessage(C.II + "Please specify a required rank to chat.");
                        return;
                    }

                    Rank rank = Rank.fromString(args[1]);

                    if (rank.isDefault()) {
                        sender.sendMessage(C.II + "Invalid rank.");
                        return;
                    }

                    if (sender instanceof Player) {
                        final AssiPlayer assiPlayer = asPlayer(sender);
                        if (!assiPlayer.getRank().isHigherThanOrEqualTo(rank)) {
                            sender.sendMessage(C.II + "You cannot set the required chat rank to one higher than your own.");
                            return;
                        }
                    }

                    chatPolicy.setRequiredRankChat(rank);

                    UtilServer.broadcast(C.II);
                    UtilServer.broadcast(C.II + "Restricted chat has been " + C.V + "enabled" + C.II + "! " + C.C +
                            "You now need Rank " + chatPolicy.getRequiredRankChat() + C.C + " to speak!");
                    UtilServer.broadcast(C.II);
                }

                plugin.getStaffChatManager().messageGenericLocal(C.II + "[Staff] Chat lock mode " + (chatPolicy.isRestrictedChat() ?
                        "enabled" : "disabled") + " by " + C.V + sender.getName() + C.II + ". " + (chatPolicy.isRestrictedChat() ? "You need rank " +
                        chatPolicy.getRequiredRankChat().getPrefix() + C.C + " to chat." : ""));

                return;
            default:
                usage(sender, usedLabel);

        }
    }

}
