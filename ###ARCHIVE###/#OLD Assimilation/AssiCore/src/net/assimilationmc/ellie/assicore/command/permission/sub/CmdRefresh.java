package net.assimilationmc.ellie.assicore.command.permission.sub;

import net.assimilationmc.ellie.assicore.AssiPlugin;
import net.assimilationmc.ellie.assicore.command.permission.SubCommand;
import net.assimilationmc.ellie.assicore.util.PermissionLib;
import net.assimilationmc.ellie.discord.AssiDiscord;
import net.assimilationmc.ellie.discord.util.Channels;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

/**
 * Created by Ellie on 16/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdRefresh extends SubCommand {

    public CmdRefresh(){
        super("refresh", PermissionLib.CMD.PERM.REFRESH, "refresh", "Runs update task, use in moderation and only when necessary");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        sendPMessage(sender, "Attempting to refresh permissions asynchronously... &lDo not spam this command");
        AssiDiscord.getAssiDiscord().messageChannel(Channels.BOT_LOGS, sender.getName()+" called a permission reload.");
        Bukkit.getScheduler().runTaskAsynchronously(AssiPlugin.getPlugin(AssiPlugin.class), getPermissionManager().getGroupSyncTask());
        sendPMessage(sender, "Refreshed.");

    }

}
