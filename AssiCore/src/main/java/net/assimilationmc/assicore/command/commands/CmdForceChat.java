package net.assimilationmc.assicore.command.commands;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilPlayer;
import net.assimilationmc.assicore.util.UtilString;
import org.bukkit.command.CommandSender;

public class CmdForceChat extends AssiCommand {

    public CmdForceChat(AssiPlugin plugin) {
        super(plugin, "forceChat", "Force a player to execute a command", Rank.ADMIN, Lists.newArrayList(), "<who>", "<args>");
    }

    @Override
    public void onCommand(CommandSender sender, String usedLabel, String[] args) {
        AssiPlayer target;
        if (UtilPlayer.get(args[0]) == null) {
            couldNotFind(sender, args[0]);
            return;
        }

        target = plugin.getPlayerManager().getPlayer(UtilPlayer.get(args[0]));
        if (isPlayer(sender) && target.getRank().isHigherThanOrEqualTo(asPlayer(sender).getRank())) {
            sender.sendMessage(C.II + "No permission.");
            return;
        }

        String forceArgs = UtilString.getFinalArg(args, 1);

        sender.sendMessage(C.C + "Forced " + C.V + target.getName() + C.C + " to run " + C.V + forceArgs);
        target.getBase().chat(forceArgs);
    }

}
