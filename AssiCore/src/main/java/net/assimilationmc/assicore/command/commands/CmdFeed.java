package net.assimilationmc.assicore.command.commands;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdFeed extends AssiCommand {

    public CmdFeed(AssiPlugin plugin) {
        super(plugin, "feed", "Feed yourself", Rank.ADMIN, Lists.newArrayList("eat"), "[player]");
        requirePlayer();
    }

    @Override
    public void onCommand(CommandSender sender, String usedLabel, String[] args) {

        Player target = null;
        if (args.length > 0) {
            target = UtilPlayer.get(args[0]);

            if (target == null) {
                couldNotFind(sender, args[0]);
                return;
            }
        }

        if (target == null) target = (Player) sender;

        target.setFoodLevel(20);
        target.setSaturation(20);

        if (target == sender) {
            target.sendMessage(C.C + ChatColor.ITALIC + "Nom nom...");
            return;
        }

        target.sendMessage(C.C + "You have been force-fed.");
        sender.sendMessage(C.C + "Force fed " + target.getName());
    }

}
