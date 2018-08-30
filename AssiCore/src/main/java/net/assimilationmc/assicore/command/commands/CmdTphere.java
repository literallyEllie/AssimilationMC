package net.assimilationmc.assicore.command.commands;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdTphere extends AssiCommand {

    public CmdTphere(AssiPlugin plugin) {
        super(plugin, "tpHere", "Teleport a player to you", Rank.MOD, Lists.newArrayList("teleportHere", "summon", "s"), "<player>");
        requirePlayer();
    }


    @Override
    public void onCommand(CommandSender sender, String usedLabel, String[] args) {
        Player player = (Player) sender;
        Player target = UtilPlayer.get(args[0]);
        if (target == null) {
            couldNotFind(sender, args[0]);
            return;
        }

        target.teleport(player);
        player.sendMessage(C.C + "Teleported " + target.getDisplayName() + C.C + " to you.");
    }

}
