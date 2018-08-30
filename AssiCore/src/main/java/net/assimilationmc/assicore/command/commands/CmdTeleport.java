package net.assimilationmc.assicore.command.commands;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdTeleport extends AssiCommand {

    public CmdTeleport(AssiPlugin plugin) {
        super(plugin, "teleport", "Teleport to another player", Rank.HELPER, Lists.newArrayList("tp"), "<player> [player2]");
        requirePlayer();
    }

    @Override
    public void onCommand(CommandSender commandSender, String usedLabel, String[] args) {
        Player sender = (Player) commandSender;

        Player player1 = UtilPlayer.get(args[0]);
        if (player1 == null) {
            couldNotFind(sender, args[0]);
            return;
        }

        Player player2 = null;

        if (args.length > 2 && asPlayer(commandSender).getRank().isHigherThanOrEqualTo(Rank.ADMIN)) {
            player2 = UtilPlayer.get(args[0]);
            if (player2 == null) {
                couldNotFind(sender, args[1]);
                return;
            }
        }

        if (player2 == null) {
            sender.teleport(player1);
            sender.sendMessage(C.C + "You have teleported to " + player1.getDisplayName() + C.C + ".");
        } else {

            player1.teleport(player2);
            sender.sendMessage(C.C + "You have teleported " + player1.getDisplayName() + C.C + " to " + player2.getDisplayName() + C.C + ".");

        }

    }

}
