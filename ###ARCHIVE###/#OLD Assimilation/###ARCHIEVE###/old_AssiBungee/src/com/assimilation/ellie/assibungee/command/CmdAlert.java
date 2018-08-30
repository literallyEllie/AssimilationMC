package com.assimilation.ellie.assibungee.command;

import com.assimilation.ellie.assibungee.util.MessageLib;
import com.assimilation.ellie.assibungee.util.PermissionLib;
import com.assimilation.ellie.assibungee.util.Util;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Ellie on 20/11/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdAlert extends Command {

    public CmdAlert(){
        super("alert", (String)null, "gbc", "gbroadcast");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!sender.hasPermission(PermissionLib.CMD.ALERT)){
            Util.mWARN(sender, MessageLib.NO_PERMISSION);
            return;
        }

        if(args.length < 1){
            Util.mINFO(sender, String.format(MessageLib.CORRECT_USAGE, "alert <message>", "Broadcast message to whole network."));
            return;
        }

        String message = Util.getFinalArg(args, 0);

        String alert = String.format(MessageLib.BROADCAST, message);

        ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(Util.color(alert)));
    }
}
