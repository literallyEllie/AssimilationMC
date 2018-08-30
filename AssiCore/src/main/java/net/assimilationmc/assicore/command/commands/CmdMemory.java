package net.assimilationmc.assicore.command.commands;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilMath;
import net.assimilationmc.assicore.util.UtilServer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CmdMemory extends AssiCommand {

    public CmdMemory(AssiPlugin plugin) {
        super(plugin, "memory", "Show the memory stuff of the server", Rank.ADMIN, Lists.newArrayList("mem"));
    }

    @Override
    public void onCommand(CommandSender sender, String usedLabel, String[] args) {
        sender.sendMessage(ChatColor.GRAY.toString());
        sender.sendMessage(C.C + "Server information of " + C.V + plugin.getServerData().getId());

        if (plugin.getServerData().isDev()) sender.sendMessage("   " + C.II + ChatColor.BOLD + "DEV SERVER");
        if (plugin.getServerData().hasRequiredRank())
            sender.sendMessage("   " + C.II + "Required rank: " + plugin.getServerData().getRequiredRank().getPrefix());
        sender.sendMessage(ChatColor.GRAY.toString());

        sender.sendMessage(C.C + "Allocated/Free/Used memory: " + C.V + UtilServer.getAllocatedMemory()
                + C.C + "mb/" + C.V + UtilServer.getFreeMemory() + C.C + "mb/" + C.V + UtilServer.getUsedMemory() + C.C + "mb");
        sender.sendMessage(C.C + "Average TPS: " + C.V + UtilMath.trim((float) plugin.getTpsTask().getAverage()));
        sender.sendMessage(ChatColor.GRAY.toString());
    }

}
