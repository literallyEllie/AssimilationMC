package net.assimilationmc.assicore.command.commands;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilServer;
import net.assimilationmc.assicore.util.UtilString;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CmdBroadcast extends AssiCommand {

    public CmdBroadcast(AssiPlugin plugin) {
        super(plugin, "broadcast", "Broadcast to the local server.", Rank.ADMIN, Lists.newArrayList("bc", "smessage"), "<message>");
    }

    @Override
    public void onCommand(CommandSender sender, String usedLabel, String[] args) {
        String message = UtilString.getFinalArg(args, 0);
        UtilServer.broadcast(prefix("broadcast") + C.II + ChatColor.translateAlternateColorCodes('&', message));
    }

}
