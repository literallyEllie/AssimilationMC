package net.assimilationmc.uhclobbyadaptor.cmd;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.uhclobbyadaptor.UHCLobbyAdaptor;
import org.bukkit.command.CommandSender;

public class CmdWebErrorLog extends AssiCommand {

    private UHCLobbyAdaptor uhcLobbyAdaptor;

    public CmdWebErrorLog(UHCLobbyAdaptor plugin) {
        super(plugin.getAssiPlugin(), "webErrorLog", "View error log from bad web responses", Rank.MANAGER, Lists.newArrayList(), "clear | [refId]");
        this.uhcLobbyAdaptor = plugin;
    }

    @Override
    public void onCommand(CommandSender commandSender, String s, String[] args) {

        int refId = -1;

        if (args.length > 0) {
            try {
                refId = Integer.parseInt(args[0]);
                if (refId < 1) throw new NumberFormatException();
            } catch (NumberFormatException e) {

                if (args[0].equalsIgnoreCase("clear")) {
                    uhcLobbyAdaptor.getCreationFactory().getErrorCache().clear();
                    commandSender.sendMessage(C.II + "Cleared cache.");
                    return;
                }

                commandSender.sendMessage(C.II + "Invalid integer.");
            }
        }

        commandSender.sendMessage(C.C + "Viewing " + (refId == -1 ? "all" : "ref ID " + C.V + refId) + C.C + ":");

        if (refId == -1) {
            uhcLobbyAdaptor.getCreationFactory().getErrorCache().forEach((key, value) -> commandSender.sendMessage(C.C + "#" + key + " " + C.V + value));
            return;
        }

        if (!uhcLobbyAdaptor.getCreationFactory().getErrorCache().containsKey(refId)) {
            commandSender.sendMessage(C.II + "No ref ID by that found.");
            return;
        }
        commandSender.sendMessage(C.C + "#" + refId + " " + C.V + uhcLobbyAdaptor.getCreationFactory().getErrorCache().get(refId));
    }

}
