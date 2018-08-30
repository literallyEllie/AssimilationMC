package net.assimilationmc.assicore.command.commands;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.C;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdSWorld extends AssiCommand {

    public CmdSWorld(AssiPlugin plugin) {
        super(plugin, "sWorld", "Teleport to another world", Rank.ADMIN, Lists.newArrayList("worldTp"), "<world>");
        requirePlayer();
    }

    @Override
    public void onCommand(CommandSender sender, String usedLabel, String[] args) {
        World world = Bukkit.getWorld(args[0]);
        if (world == null) {
            couldNotFind(sender, "world " + args[0]);
            return;
        }

        ((Player) sender).teleport(world.getSpawnLocation());
        sender.sendMessage(C.C + "Teleported to world " + C.V + world.getName() + C.C + ".");
    }

}
