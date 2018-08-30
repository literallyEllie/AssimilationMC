package net.assimilationmc.assicore.command.commands;


import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilTime;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.UUID;

public class CmdSeen extends AssiCommand {

    public CmdSeen(AssiPlugin plugin) {
        super(plugin, "seen", "See when a player was last online", Lists.newArrayList(), "<player>");
    }

    @Override
    public void onCommand(CommandSender commandSender, String usedLabel, String[] args) {

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> plugin.getServer().getScheduler().runTask(plugin, () -> {
            AssiPlayer target;

            UUID uuid = plugin.getPlayerManager().getUUID(args[0]);

            if (uuid == null) {
                commandSender.sendMessage(C.C + "It doesn't look like anyone with the name " + C.V + args[0] + C.C + " has ever joined the network.");
                return;
            }

            target = plugin.getPlayerManager().getOfflinePlayer(uuid);

            if (target == null) {
                commandSender.sendMessage(C.C + "It doesn't look like anyone with the name " + C.V + args[0] + C.C + " has ever joined the network.");
                return;
            }

            String server = plugin.getPlayerFinder().findPlayer(target.getName());

            if (server == null ||
                    (target.isVanished() && ((isPlayer(commandSender) && !asPlayer(commandSender).getRank().isHigherThanOrEqualTo(target.getRank())) || commandSender instanceof
                            ConsoleCommandSender))) {
                commandSender.sendMessage(prefix(usedLabel) + C.V + target.getName() + C.C + " was last seen " + UtilTime.formatTimeStamp(target.getLastSeen()));
                return;
            }

            commandSender.sendMessage(prefix(usedLabel) + C.V + target.getName() + C.C + " is online.");

        }));


    }

}
