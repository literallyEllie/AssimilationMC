package net.assimilationmc.uhclobbyadaptor.cmd;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilBungee;
import net.assimilationmc.uhclobbyadaptor.UHCLobbyAdaptor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdUHC extends AssiCommand {

    private UHCLobbyAdaptor lobbyAdaptor;

    public CmdUHC(UHCLobbyAdaptor lobbyAdaptor) {
        super(lobbyAdaptor.getAssiPlugin(), "uhc", "Opens the game GUI", Lists.newArrayList("play"));
        requirePlayer();
        this.lobbyAdaptor = lobbyAdaptor;
    }

    @Override
    public void onCommand(CommandSender commandSender, String s, String[] args) {
        if (args.length > 0) {
            String serverId = args[0];
            if (!lobbyAdaptor.getServerMap().containsKey(serverId)) {
                commandSender.sendMessage(C.II + "Server offline. Do just " + C.V + "/" + s + C.II + " to see all the current games.");
                return;
            }

            UtilBungee.sendPlayer(plugin, (Player) commandSender, serverId);
            return;
        }
        lobbyAdaptor.getGamePlayItem().onClick((Player) commandSender);
    }

}
