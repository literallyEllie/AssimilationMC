package net.assimilationmc.ellie.assicore.command;


import net.assimilationmc.ellie.assicore.util.MessageLib;
import net.assimilationmc.ellie.assicore.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * Created by Ellie on 19/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdReply extends AssiCommand {

    public CmdReply(){
        super("reply", "reply <message>", "Reply to the last message you received", Arrays.asList("r"));
    }

    @Override
    public void onCommand(CommandSender s, String[] args) {
        if(args.length < 1){
            sendMessage(s, String.format(MessageLib.CORRECT_USAGE, "reply <message>", "Reply to the last message you received."));
            return;
        }

        if(CmdMessage.message_map.containsKey(s.getName())){

            Player t = Bukkit.getPlayer(CmdMessage.message_map.get(s.getName()));

            if(t == null){
                sendPMessage(s, MessageLib.PLAYER_OFFLINE);
                return;
            }

            String message = Util.getFinalArg(args, 0);

            Bukkit.dispatchCommand(s, "msg "+t.getName()+" "+message);

        }else{
            sendPMessage(s, "You have no one to reply to.");
        }
    }




}
