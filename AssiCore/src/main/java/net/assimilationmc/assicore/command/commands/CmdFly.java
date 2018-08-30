package net.assimilationmc.assicore.command.commands;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdFly extends AssiCommand {

    public CmdFly(AssiPlugin plugin) {
        super(plugin, "fly", "Toggle fly mode", Rank.MOD, Lists.newArrayList(), "[player]");
        requirePlayer();
    }

    @Override
    public void onCommand(CommandSender commandSender, String usedLabel, String[] args) {
        final AssiPlayer sender = asPlayer(commandSender);

        Player target = sender.getBase();
        if (args.length > 0 && sender.getRank().isHigherThanOrEqualTo(Rank.ADMIN)) {
            if (getPlayer(commandSender, args[0])) {
                target = UtilPlayer.get(args[0]);
            }
        }

        target.setAllowFlight(!target.getAllowFlight());
        target.setFlying(target.getAllowFlight());

        if (!target.getUniqueId().equals(sender.getUuid())) {
            target.sendMessage(C.C + "Fly mode " + C.V + (target.getAllowFlight() ? "enabled"
                    : "disabled") + C.C + ".");
        }

        sender.sendMessage(C.V + (target.getAllowFlight() ? "Enabled" : "Disabled") + C.C + " fly mode " +
                "for " + target.getDisplayName() + C.C + ".");
    }

}