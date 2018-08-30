package net.assimilationmc.ellie.assicore.command;

import net.assimilationmc.ellie.assicore.util.ColorChart;
import net.assimilationmc.ellie.assicore.util.PermissionLib;
import net.assimilationmc.ellie.assicore.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.lang.management.ManagementFactory;
import java.util.Arrays;

/**
 * Created by Ellie on 17/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdMemory extends AssiCommand {

    public CmdMemory(){
        super("memory", PermissionLib.CMD.MEMORY, "memory", "Displays internal information about the server", Arrays.asList("usage", "tps", "mem"));
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        String r = ColorChart.R;
        String var = ColorChart.VARIABLE;

        sendPMessage(sender, "Information about this server:");
        sendMessage(sender, "Uptime: " + var + Util.formatDateDiff(ManagementFactory.getRuntimeMXBean().getStartTime()));
        sendMessage(sender, "Allocated memory: " + var + (Runtime.getRuntime().maxMemory() / 1024 / 1024) + r + "mb");
        sendMessage(sender, "Usage: " + var + (Runtime.getRuntime().totalMemory() / 1024 / 1024) + r + "mb");
        sendMessage(sender, "Free memory: " + var + (Runtime.getRuntime().freeMemory() / 1024 / 1024) + r + "mb");
        sendMessage(sender, "Online players: " + var + Bukkit.getOnlinePlayers().size());
        sendMessage(sender, "Max player count: " + var + Bukkit.getMaxPlayers());
        sendMessage(sender, "Average TPS: " + var + getCore().getMonitorTask().getAvgTPS());
    }
}
