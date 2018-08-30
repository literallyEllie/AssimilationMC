package net.assimilationmc.ellie.assicore.listener;

import net.assimilationmc.ellie.assicore.manager.ModuleManager;
import net.assimilationmc.ellie.assicore.permission.AssiPermGroup;
import net.assimilationmc.ellie.assicore.util.MessageLib;
import net.assimilationmc.ellie.assicore.util.PermissionLib;
import net.assimilationmc.ellie.assicore.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.HashMap;

/**
 * Created by Ellie on 13/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class ChatListener implements Listener {

    private String[] local_disallowed = {"me", "pl", "?", "plugins", "version", "ver", "icanhasbukkit"};
    private String[] prefix_disallowed = {"minecraft", "bukkit"};

    private String[] color = {"0", "a", "b", "c", "d", "e", "f", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
    private String[] format = {"l", "m", "n", "o", "r", "L", "M", "N", "O", "R"};

    private HashMap<String, String> chatFormat;

    public ChatListener(HashMap<String, String> chatFormat){
        this.chatFormat = chatFormat;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncPlayerChatEvent e){

        Player player = e.getPlayer();

        try {
            if (player.hasPermission(PermissionLib.STAFF_CHAT) && e.getMessage().split("")[0].equals("!") && !e.getMessage().split("")[1].equals("")) {
                ModuleManager.getModuleManager().getStaffChatManager().message(player, e.getMessage().replaceFirst("!", ""));
                e.setCancelled(true);
                return;
            }
        }catch(ArrayIndexOutOfBoundsException ex){}

        AssiPermGroup group = ModuleManager.getModuleManager().getPlayerManager().getGroupOf(player.getUniqueId());

        if(group != null) {

            String message = e.getMessage();

            if (player.hasPermission(PermissionLib.CHAT.COLOR)) {
                for (String s : color) {
                    message = message.replace("&" + s, "§" + s);
                }
            }

            if (player.hasPermission(PermissionLib.CHAT.FORMAT)) {
                for (String s : format) {
                    message = message.replace("&" + s, "§" + s);
                }
            }
            e.setMessage(message);

            if (chatFormat.get(group.getName()) != null) {
                e.setFormat(
                        Util.color(chatFormat.get(group.getName()).replace("{display}", group.getPrefix().replace("%space%", " ") + player.getName() + group.getSuffix()
                                .replace("%space%", " ")).replace("{smallArrow}", "»"))
                                .replace("{message}", "%2$s"));


            } else
                e.setFormat(Util.color(group.getPrefix().replace("%space%", " ") + player.getName() + group.getSuffix().replace("%space%", " ") + "&7» &7%2$s"));
        }
        else e.setFormat(Util.color(player.getName()+"&7» &7%2$s"));


    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e){

        String command = e.getMessage().split(" ")[0];
        if (Bukkit.getServer().getHelpMap().getHelpTopic(command) == null){
            Util.mINFO_noP(e.getPlayer(), MessageLib.UNKNOWN_CMD);
            e.setCancelled(true);
            return;
        }

        if(!e.getPlayer().hasPermission(PermissionLib.BYPASS.COMMAND_FILTER)) {
            boolean colon = command.contains(":");
            for (String s : local_disallowed) {
                try {
                    if ((colon && command.split(":")[1].equalsIgnoreCase(s)) || e.getMessage().split(" ")[0].equalsIgnoreCase("/" + s)) {
                        Util.mINFO_noP(e.getPlayer(), MessageLib.CMD_DISABLED);
                        e.setCancelled(true);
                        return;
                    }
                } catch (ArrayIndexOutOfBoundsException ex){
                    break;
                }
            }

            for (String s : prefix_disallowed) {
                if (colon && command.split(":")[0].equalsIgnoreCase("/"+s)){
                    Util.mINFO_noP(e.getPlayer(), MessageLib.CMD_DISABLED);
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }

}
