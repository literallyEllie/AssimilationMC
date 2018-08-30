package net.assimilationmc.uhclobbyadaptor.cmd;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.uhclobbyadaptor.UHCLobbyAdaptor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdToggleStats extends AssiCommand {

    private final UHCLobbyAdaptor uhcLobbyAdaptor;

    public CmdToggleStats(UHCLobbyAdaptor uhcLobbyAdaptor) {
        super(uhcLobbyAdaptor.getAssiPlugin(), "togglestats", "Toggle the hotbar stats message", Lists.newArrayList("nostats"));
        this.uhcLobbyAdaptor = uhcLobbyAdaptor;
        requirePlayer();
    }

    @Override
    public void onCommand(CommandSender commandSender, String s, String[] strings) {
        Player sender = (Player) commandSender;

        boolean on = !uhcLobbyAdaptor.getDisplayer().getTasks().containsKey(sender.getUniqueId());

        if (on) {
            uhcLobbyAdaptor.getDisplayer().restartTaskHotbar(sender);
        } else {
            uhcLobbyAdaptor.getDisplayer().stopPlayerTasks(sender);
        }

        sender.sendMessage(C.C + "Stats showing on your hotbar have been " + C.V + (on ? "enabled" : "disabled (Only for this session)") + ".");
    }

}
