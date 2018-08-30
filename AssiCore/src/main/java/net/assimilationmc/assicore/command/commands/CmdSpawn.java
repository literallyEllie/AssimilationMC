package net.assimilationmc.assicore.command.commands;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilPlayer;
import net.assimilationmc.assicore.world.WorldData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdSpawn extends AssiCommand {

    public CmdSpawn(AssiPlugin plugin) {
        super(plugin, "spawn", "Teleport to spawn", Lists.newArrayList(), "[player]");
    }

    @Override
    public void onCommand(CommandSender commandSender, String usedLabel, String[] args) {
        final AssiPlayer sender = asPlayer(commandSender);

        Player target = sender.getBase();
        if (args.length > 0 && sender.getRank().isHigherThanOrEqualTo(Rank.MOD)) {
            if (getPlayer(commandSender, args[0])) {
                target = UtilPlayer.get(args[0]);
            }
        }

        WorldData worldData = plugin.getWorldManager().getPrimaryWorld();

        if (worldData != null && worldData.getSpawns().containsKey("spawn")) {
            target.teleport(worldData.getSpawns().get("spawn").toLocation());

            if (!target.equals(commandSender)) {
                commandSender.sendMessage(C.C + "You have teleported " + target.getDisplayName() + C.C + " to spawn.");
            }

            target.sendMessage(prefix(usedLabel) + "You have been teleported to spawn.");
        } else
            sender.sendMessage(C.II + "There is no spawn defined. If this is a problem, please contact a member of staff.");


    }
}
