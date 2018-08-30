package net.assimilationmc.uhclobbyadaptor.cmd;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.util.UtilBungee;
import net.assimilationmc.uhclobbyadaptor.UHCLobbyAdaptor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class CmdReconnect extends AssiCommand {

    private UHCLobbyAdaptor uhcLobbyAdaptor;

    public CmdReconnect(UHCLobbyAdaptor uhcLobbyAdaptor) {
        super(uhcLobbyAdaptor.getAssiPlugin(), "reconnect", "Reconnect to your last game", Lists.newArrayList());
        this.uhcLobbyAdaptor = uhcLobbyAdaptor;
        requirePlayer();
    }

    @Override
    public void onCommand(CommandSender commandSender, String s, String[] strings) {
        final Map<UUID, String> midgameLeavers = uhcLobbyAdaptor.getMidgameLeavers();
        Player player = (Player) commandSender;

        if (!midgameLeavers.containsKey(player.getUniqueId())) {
            commandSender.sendMessage(prefix(s) + "You have no game to reconnect to - you might have already missed the party.");
            return;
        }

        player.sendMessage(prefix(s) + ChatColor.ITALIC + "Beaming you up and out...");
        UtilBungee.sendPlayer(plugin, player, midgameLeavers.get(player.getUniqueId()));
    }

}
