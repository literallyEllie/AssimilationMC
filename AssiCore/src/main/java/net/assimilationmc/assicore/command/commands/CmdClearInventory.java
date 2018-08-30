package net.assimilationmc.assicore.command.commands;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdClearInventory extends AssiCommand {

    public CmdClearInventory(AssiPlugin plugin) {
        super(plugin, "clearinventory", "Clear the inventory", Rank.HELPER, Lists.newArrayList("ci"), "[player]");
        requirePlayer();
    }

    @Override
    public void onCommand(CommandSender sender, String usedLabel, String[] args) {
        Player whom = (Player) sender;

        if (args.length > 0) {
            whom = UtilPlayer.get(args[0]);
            if (whom == null) {
                couldNotFind(sender, args[1]);
                return;
            }
        }

        whom.getInventory().clear();
        whom.getInventory().setArmorContents(null);

        if (!whom.equals(sender)) {
            sender.sendMessage(C.C + "You have the inventory of " + whom.getDisplayName() + C.C + ".");
        }

        whom.sendMessage(C.C + "Your inventory has been cleared.");

    }

}
