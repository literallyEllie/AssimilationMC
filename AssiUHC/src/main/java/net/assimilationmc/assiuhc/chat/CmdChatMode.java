package net.assimilationmc.assiuhc.chat;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assiuhc.game.UHCTeamedGame;
import net.assimilationmc.gameapi.util.GC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdChatMode extends AssiCommand {

    private final UHCTeamedGame teamedGame;

    public CmdChatMode(UHCTeamedGame teamedGame) {
        super(teamedGame.getPlugin(), "chatMode", "Command to switch between Game chat channels", Lists.newArrayList("cm", "c"));
        requirePlayer();
        this.teamedGame = teamedGame;
    }

    @Override
    public void onCommand(CommandSender commandSender, String s, String[] strings) {
        Player player = (Player) commandSender;

        if (teamedGame.getTeamChatPolicy().toggleChat(player)) {
            player.sendMessage(prefix(s) + GC.C + "You have toggled on global chat.");
        } else player.sendMessage(prefix(s) + GC.C + "You have toggled off global chat.");

    }

}
