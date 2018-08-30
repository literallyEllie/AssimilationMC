package net.assimilationmc.assibungee.announce;

import com.google.common.collect.Lists;
import net.assimilationmc.assibungee.AssiBungee;
import net.assimilationmc.assibungee.command.BungeeCommand;
import net.assimilationmc.assibungee.rank.BungeeGroup;
import net.assimilationmc.assibungee.util.C;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class CmdGAnnounce extends BungeeCommand {

    public CmdGAnnounce(AssiBungee assiBungee) {
        super(assiBungee, "gAnnounce", BungeeGroup.ADMIN, Lists.newArrayList("gAnnoucements", "globalAnnouncements"), "<reload>");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if (args[0].equals("reload")) {
            plugin.getAnnouceManager().reload();
            sender.sendMessage(new TextComponent(C.C + "Reloaded the global announcements. They will begin in 1 minute."));
        }

    }

}
