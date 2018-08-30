package net.assimilationmc.assicore.command.commands;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class CmdPing extends AssiCommand {

    public CmdPing(AssiPlugin plugin) {
        super(plugin, "ping", "Get yours or another player's ping.", Lists.newArrayList("latency"), "[player]");
        requirePlayer();
    }

    @Override
    public void onCommand(CommandSender sender, String usedLabel, String[] args) {
        Player player = (Player) sender;

        if (args.length > 0 && asPlayer(sender).getRank().isHigherThanOrEqualTo(Rank.HELPER)) {
            Player target = UtilPlayer.get(args[0]);
            if (target == null) {
                couldNotFind(sender, args[0]);
                return;
            }
            player.sendMessage(prefix(usedLabel) + C.V + target.getName() + C.C +
                    "'s ping is " + C.V + ((CraftPlayer) target).getHandle().ping + "ms" + C.C + ".");

            return;
        }

        player.sendMessage(prefix(usedLabel) + "Your ping is " + C.V + ((CraftPlayer) player).getHandle().ping + "ms" + C.C + ".");
    }

}
