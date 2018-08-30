package net.assimilationmc.assicore.command.commands.ambience;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.C;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdDay extends AssiCommand {

    public CmdDay(AssiPlugin plugin) {
        super(plugin, "day", "Set the time to day.", Rank.ADMIN, Lists.newArrayList("daytime"));
        requirePlayer();
    }

    @Override
    public void onCommand(CommandSender sender, String usedLabel, String[] args) {
        Player player = (Player) sender;

        player.getWorld().setTime(0);
        player.sendMessage(C.C + "Time set to day.");
    }

}
