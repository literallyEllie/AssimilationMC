package net.assimilationmc.assicore.player;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.C;
import org.bukkit.command.CommandSender;

public class CmdFind extends AssiCommand {

    public CmdFind(AssiPlugin plugin) {
        super(plugin, "find", "Find a player on the network", Rank.HELPER, Lists.newArrayList("playerFind"), "<name>");
    }

    @Override
    public void onCommand(CommandSender sender, String usedLabel, String[] args) {
        String name = args[0];

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> plugin.getServer().getScheduler().runTask(plugin, () -> {

            String server = plugin.getPlayerFinder().findPlayer(name);

            if (server == null) {
                sender.sendMessage(C.V + name + C.C + " was not found on the network.");
                return;
            }

            sender.sendMessage(C.V + name + C.C + " is currently online at " + C.V + server + C.C + ".");

        }));


    }

}
