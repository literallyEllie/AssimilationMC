package com.assimilation.ellie.assicore.command;

import com.assimilation.ellie.assicore.util.PermissionLib;
import com.assimilation.ellie.assicore.util.Util;
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

        sendPMessage(sender, "Information about this server:");
        sendMessage(sender, "Uptime: &9"+ Util.formatDateDiff(ManagementFactory.getRuntimeMXBean().getStartTime()));
        sendMessage(sender, "Allocated memory: &9"+ (Runtime.getRuntime().maxMemory() / 1024 / 1024)+"&fmb");
        sendMessage(sender, "Usage: &9"+ (Runtime.getRuntime().totalMemory() / 1024 / 1024)+"&fmb");
        sendMessage(sender, "Free memory: &9"+ (Runtime.getRuntime().freeMemory() / 1024 / 1024)+"&fmb\n");
        sendMessage(sender, "Online players: &9"+ Bukkit.getOnlinePlayers().size());
        sendMessage(sender, "Max player count: &9"+ Bukkit.getMaxPlayers());
        sendMessage(sender, "Average TPS: &9"+ getCore().getMonitorTask().getAvgTPS());

    }
}
