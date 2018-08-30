package net.assimilationmc.assicore.command.commands;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.party.Party;
import net.assimilationmc.assicore.party.PartyManager;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.util.C;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdHub extends AssiCommand {

    public CmdHub(AssiPlugin plugin) {
        super(plugin, "hub", "Teleport to Hub", Lists.newArrayList());
        requirePlayer();
    }

    @Override
    public void onCommand(CommandSender sender, String usedLabel, String[] args) {
        AssiPlayer assiPlayer = asPlayer(sender);
        if (plugin.getServerData().isLobby()) {
            assiPlayer.sendMessage(prefix(usedLabel) + "You are already in a Hub!");
            return;
        }

        final Party party = plugin.getPartyManager().getPartyOf((Player) sender, false);
        if (party != null && party.getMembers().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(PartyManager.PREFIX + "You are you in a party and cannot do this currently. " +
                    "To get your freedom back you can do " + C.V + "/party leave");
            return;
        }

        plugin.getPlayerManager().sendLobby(assiPlayer, "");
    }

}
