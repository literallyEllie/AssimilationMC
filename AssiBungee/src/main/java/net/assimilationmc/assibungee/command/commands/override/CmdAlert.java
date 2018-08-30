package net.assimilationmc.assibungee.command.commands.override;

import com.google.common.collect.Lists;
import net.assimilationmc.assibungee.AssiBungee;
import net.assimilationmc.assibungee.command.BungeeCommand;
import net.assimilationmc.assibungee.discord.DiscordPresetChannel;
import net.assimilationmc.assibungee.rank.BungeeGroup;
import net.assimilationmc.assibungee.util.C;
import net.assimilationmc.assibungee.util.UtilString;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class CmdAlert extends BungeeCommand {

    public static final String FORMAT = "\n" + C.II + ChatColor.BOLD.toString() + ChatColor.ITALIC + "Global Announcement" + C.SS + C.II + "{message}\n";

    public CmdAlert(AssiBungee assiBungee) {
        super(assiBungee, "alert", BungeeGroup.ADMIN, Lists.newArrayList("gbroadcast", "gbc"), "<message>");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        String message = UtilString.getFinalArg(args, 0);

        plugin.getProxy().broadcast(new TextComponent(FORMAT.replace("{message}", message)));
        if (plugin.getDiscordManager() != null) {
            plugin.getDiscordManager().messageChannel(DiscordPresetChannel.BROADCAST,
                    "`" + sender.getName() + "` sent a broadcast of `" + message + "` to the whole network.");
        }
    }

}
