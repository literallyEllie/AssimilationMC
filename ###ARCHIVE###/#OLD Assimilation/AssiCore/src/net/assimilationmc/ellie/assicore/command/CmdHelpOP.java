package net.assimilationmc.ellie.assicore.command;

import net.assimilationmc.ellie.assicore.AssiPlugin;
import net.assimilationmc.ellie.assicore.util.ColorChart;
import net.assimilationmc.ellie.assicore.util.MessageLib;
import net.assimilationmc.ellie.assicore.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Ellie on 21/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdHelpOP extends AssiCommand {

    private Set<String> cooldown = new HashSet<>();

    public CmdHelpOP(){
        super("helpop", "helpop <message>", "Send a message to online staff", new ArrayList<>());
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if(!isPlayer(sender)){
            sender.sendMessage(MessageLib.NO_CONSOLE);
            return;
        }

        if(args.length < 1){
            sendMessage(sender, correctUsage());
            return;
        }

        if(cooldown.contains(sender.getName())){
            sender.sendMessage(Util.prefix()+Util.color(ColorChart.WARN + "You cannot send another HelpOP just yet (There is a 2 minute cooldown period between each HelpOP)"));
            return;
        }

        // Very basic system for now, there is a far more complex version loaded on old BungeeCord version
        String message = Util.getFinalArg(args, 0);
        sendPMessage(sender, ColorChart.R + "Your HelpOP has been sent to the online staff, they will be with you shortly!");
        getCore().getModuleManager().getStaffChatManager().helpopSentMessage(((Player) sender), message);
        cooldown.add(sender.getName());

        Bukkit.getScheduler().scheduleSyncDelayedTask(AssiPlugin.getPlugin(AssiPlugin.class), () -> cooldown.remove(sender.getName()), 1200L);


    }
}
