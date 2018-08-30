package net.assimilationmc.assicore.command.commands;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilServer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CmdServerRestrict extends AssiCommand {

    public CmdServerRestrict(AssiPlugin plugin) {
        super(plugin, "serverrestrict", "Make the server into a rank restricted environment, it will kick any players who do not meet the requirement.",
                Rank.ADMIN, Lists.newArrayList("rankrestrict"), "<rank>");
    }

    @Override
    public void onCommand(CommandSender sender, String usedLabel, String[] args) {

        Rank requiredRank = Rank.fromString(args[0]);
        sender.sendMessage(C.C + "Setting the server required rank to " + C.II + requiredRank.getName() + C.C + ".");

        if (!requiredRank.isDefault()) {

            String reason = C.II + "This server is now restricted; you are now going to be directed to a lobby.";

            plugin.getServerData().setRequiredRank(requiredRank);
            plugin.getPlayerManager().getOnlinePlayers().values().stream().filter(player -> !player.getRank().isHigherThanOrEqualTo(requiredRank))
                    .forEach(player -> plugin.getPlayerManager().sendLobby(player, reason));

            UtilServer.broadcast(ChatColor.RED.toString());
            UtilServer.broadcast(C.II + ChatColor.BOLD.toString() + "THIS SERVER IS NOW IN RESTRICTED MODE");
            UtilServer.broadcast(C.II + "You need the rank " + requiredRank.getPrefix() + C.II + " or above to join.");
            UtilServer.broadcast(ChatColor.RED.toString());
        } else {
            plugin.getServerData().setRequiredRank(null);
            UtilServer.broadcast(C.II + ChatColor.BOLD.toString() + "This server is no longer in restricted mode.");
        }
    }

}
