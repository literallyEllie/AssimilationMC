package com.assimilation.ellie.assibungee.listener;

import com.assimilation.ellie.assibungee.manager.ModuleManager;
import com.assimilation.ellie.assibungee.server.AssiPlayer;
import com.assimilation.ellie.assibungee.util.MessageLib;
import com.assimilation.ellie.assibungee.util.PermissionLib;
import com.assimilation.ellie.assibungee.util.Util;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Created by Ellie on 20/11/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class InputListener implements Listener {

    String[] global_disallowed = {"op", "deop"};

    @EventHandler
    public void onChat(ChatEvent e){

        if(e.isCommand()){
            boolean colon = e.getMessage().split(" ")[0].contains(":");
            for (String s : global_disallowed) {

                try {
                    if ((colon && e.getMessage().split(" ")[0].split(":")[1].equalsIgnoreCase(s)) || e.getMessage().split(" ")[0].equalsIgnoreCase("/" + s)) {
                        Util.mWARN((CommandSender) e.getSender(), MessageLib.CMD_DISABLED);
                        e.setCancelled(true);
                    }
                }catch(ArrayIndexOutOfBoundsException ex){
                    break;
                }
            }
            return;
        }

        AssiPlayer player = ModuleManager.getModuleManager().getPlayerManager().getOnlinePlayer(((ProxiedPlayer)e.getSender()).getName());

        if(player.hasPermission(PermissionLib.STAFF_CHAT)){
            try {
                if (e.getMessage().split("")[0].equals("!") && !e.getMessage().split("")[1].equals("")) {
                    ModuleManager.getModuleManager().getStaffChatManager().message(player, player.getBase().getServer().getInfo().getName(), e.getMessage().replaceFirst("!", ""));
                    e.setCancelled(true);
                }
            }catch(ArrayIndexOutOfBoundsException ex){}
            return;
        }
    }

}
