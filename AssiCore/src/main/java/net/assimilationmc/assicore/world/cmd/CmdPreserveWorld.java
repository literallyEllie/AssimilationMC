package net.assimilationmc.assicore.world.cmd;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.world.WorldPreserver;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

import java.util.Set;

public class CmdPreserveWorld extends AssiCommand {

    private Set<WorldPreserver> worlds;

    public CmdPreserveWorld(AssiPlugin assiPlugin) {
        super(assiPlugin, "preserveworld", "Preserve a world from destruction", Lists.newArrayList(), "<world> " +
                "[-p[rotectplayer]] [-n[ointeract]]");
        this.worlds = Sets.newHashSet();
    }

    @Override
    public void onCommand(CommandSender sender, String usedLabel, String[] args) {
        String world = args[0];

        WorldPreserver existing = null;

        for (WorldPreserver worldPreserver : worlds) {
            if (worldPreserver.getWorld().getName().equalsIgnoreCase(world)) {
                existing = worldPreserver;
                break;
            }
        }

        if (existing != null) {
            HandlerList.unregisterAll(existing);
            worlds.remove(existing);
            sender.sendMessage(C.II + "Unprotected world " + existing.getWorld().getName());
            return;
        }

        World bWorld = Bukkit.getWorld(world);
        if (bWorld == null) {
            couldNotFind(sender, world);
            return;
        }

        boolean protectPlayers = false;
        boolean noInteract = false;

        if (args.length > 1) {

            for (int i = 1; i < args.length; i++) {
                String arg = args[i];
                if (arg.startsWith("-p")) {
                    protectPlayers = true;
                } else if (arg.startsWith("-n")) {
                    noInteract = true;
                } else {
                    sender.sendMessage(C.II + "Unrecognised parameter at " + C.V + arg);
                    return;
                }
            }
        }

        WorldPreserver worldPreserver = new WorldPreserver(bWorld);
        worldPreserver.setProtectPlayers(protectPlayers);
        worldPreserver.setStopPlayerInteract(noInteract);

        worlds.add(worldPreserver);
        plugin.registerListener(worldPreserver);

        sender.sendMessage(C.C + "Now protecting world " + C.V + bWorld.getName() + C.C + ". (protecting players? " + protectPlayers + ", no interact? " + noInteract + ")");
    }

}
