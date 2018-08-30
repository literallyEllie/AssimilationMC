package net.assimilationmc.assicore.world.cmd;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.C;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;

import java.io.File;

public class CmdLoadWorld extends AssiCommand {

    public CmdLoadWorld(AssiPlugin plugin) {
        super(plugin, "loadworld", "Loads a world", Rank.ADMIN, Lists.newArrayList(), "<world>");
    }

    @Override
    public void onCommand(CommandSender sender, String usedLabel, String[] args) {
        String world = args[0];

        for (World bWorld : Bukkit.getWorlds()) {
            if (bWorld.getName().equalsIgnoreCase(world)) {
                sender.sendMessage(C.C + "World already loaded. Do /sworld to go to it.");
                return;
            }
        }

        File wFile = new File(world);
        if (!wFile.isDirectory()) {
            couldNotFind(sender, world);
            return;
        }

        if (!new File(wFile, "level.dat").exists()) {
            sender.sendMessage(C.II + "World is not valid.");
            return;
        }

        Bukkit.createWorld(new WorldCreator(world));
        sender.sendMessage(C.C + "World loaded. You can go to it with /sworld");
    }

}
