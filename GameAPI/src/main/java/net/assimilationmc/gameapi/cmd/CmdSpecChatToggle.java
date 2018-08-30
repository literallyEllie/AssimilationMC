package net.assimilationmc.gameapi.cmd;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.gameapi.game.AssiGame;
import net.assimilationmc.gameapi.util.GC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class CmdSpecChatToggle extends AssiCommand {

    private final Set<UUID> toggled;

    public CmdSpecChatToggle(AssiGame assiGame) {
        super(assiGame.getPlugin(), "specChatToggle", "Toggle into the spectator chat (Read-only)", Rank.ADMIN, Lists.newArrayList());
        this.toggled = Sets.newHashSet();
        requirePlayer();
    }

    @Override
    public void onCommand(CommandSender commandSender, String s, String[] strings) {
        Player sender = (Player) commandSender;

        if (toggled.contains(sender.getUniqueId())) {
            toggled.remove(sender.getUniqueId());
        } else toggled.add(sender.getUniqueId());

        commandSender.sendMessage(GC.C + "Spectator Toggle Chat " + GC.V + (toggled.contains(sender.getUniqueId()) ? "enabled" : "disabled" + GC.C + ". (Read-only)"));
    }

    public Set<UUID> getToggled() {
        return toggled;
    }

}
