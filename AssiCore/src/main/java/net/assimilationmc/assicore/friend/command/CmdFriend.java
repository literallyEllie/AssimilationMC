package net.assimilationmc.assicore.friend.command;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.friend.FriendManager;
import net.assimilationmc.assicore.friend.UIFriendMain;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilString;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class CmdFriend extends AssiCommand {

    private FriendManager friendManager;

    public CmdFriend(AssiPlugin plugin, FriendManager friendManager) {
        super(plugin, "friend", "Friend system", Lists.newArrayList("fr"), "[add | broadcast | bc (broadcast)] [player | message]");
        requirePlayer();
        this.friendManager = friendManager;
    }

    @Override
    public void onCommand(CommandSender commandSender, String usedLabel, String[] args) {
        AssiPlayer sender = asPlayer(commandSender);

        if (args.length > 0) {

            if (args[0].equalsIgnoreCase("add") && args.length > 1) {

                final UUID uuid = friendManager.getPlugin().getPlayerManager().getUUID(args[1]);
                if (uuid == null) {
                    sender.sendMessage(C.C + "That player doesn't seem to have ever joined the network.");
                    return;
                }

                friendManager.sendFriendRequest(sender, uuid);
                return;
            } else if (args[0].toLowerCase().startsWith("b") && args.length > 1) {

                String message = UtilString.getFinalArg(args, 1);

                friendManager.friendBroadcast(sender, message);
                return;
            }

            //
            if (args[0].equalsIgnoreCase("accept") && args.length > 1) {

                UUID uuid;
                try {
                    uuid = UUID.fromString(args[1]);
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(C.II + "Invalid UUID, use the GUI.");
                    return;
                }

                friendManager.acceptRequest(sender, uuid);
                return;
            } else if (args[0].equalsIgnoreCase("decline") && args.length > 1) {
                UUID uuid;
                try {
                    uuid = UUID.fromString(args[1]);
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(C.II + "Invalid UUID, use the GUI.");
                    return;
                }

                friendManager.declineRequest(sender, uuid);
                return;
            } else if (args.length == 1) {
                usage(commandSender, "friend");
                return;
            }

        }

        new UIFriendMain(plugin, sender.getFriendData()).open(sender.getBase());

    }


}
