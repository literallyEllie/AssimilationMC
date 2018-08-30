package net.assimilationmc.assicore.punish.cmd;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.punish.model.PunishmentCategory;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.UtilPlayer;
import net.assimilationmc.assicore.util.UtilString;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdConsolePunish extends AssiCommand {

    public CmdConsolePunish(AssiPlugin plugin) {
        super (plugin, "consolepunish", "Command to be run by anticheat when banning", Rank.OWNER, Lists.newArrayList(),
                "<player>", "<category>", "<reason>");
    }

    @Override
    public void onCommand(CommandSender sender, String usedLabel, String[] args) {

        Player player = UtilPlayer.get(args[0]);
        if (player == null) {
            couldNotFind(sender, args[0]);
            return;
        }

        PunishmentCategory category;
        try {
            category = PunishmentCategory.valueOf(args[1]);
        } catch (IllegalArgumentException e) {
            sender.sendMessage("Invalid category.");
            return;
        }

        String reason = UtilString.getFinalArg(args, 2);

        plugin.getPunishmentManager().punish(plugin.getPunishmentManager().getConsole(), plugin.getPlayerManager().getPlayer(player),
                category, reason);
    }

}
