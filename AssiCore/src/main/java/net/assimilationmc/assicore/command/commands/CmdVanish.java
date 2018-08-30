package net.assimilationmc.assicore.command.commands;

import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class CmdVanish extends AssiCommand {

    public CmdVanish(AssiPlugin plugin) {
        super(plugin, "vanish", "Vanish from regular players", Rank.YOUTUBE,
                Collections.singletonList("v"));
        requirePlayer();
    }

    @Override
    public void onCommand(CommandSender sender, String usedLabel, String[] args) {
        final AssiPlayer player = asPlayer(sender);

        if (args.length > 0) {
            final Player target = UtilPlayer.get(args[0]);
            if (target == null) {
                couldNotFind(sender, "Player " + args[0]);
                return;
            }

            AssiPlayer targetPlayer = asPlayer(target);
            targetPlayer.setVanished(!targetPlayer.isVanished());

            player.sendMessage(C.C + (targetPlayer.isVanished() ? "Vanished " : "Unvanished") + C.V + targetPlayer.getDisplayName());
            return;
        }

        player.setVanished(!player.isVanished());
    }


}
