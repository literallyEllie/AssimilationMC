package net.assimilationmc.assiuhc.reward;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilPlayer;
import net.assimilationmc.assiuhc.game.UHCGame;
import net.assimilationmc.assiuhc.player.UHCPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdXP extends AssiCommand {

    private UHCGame game;

    public CmdXP(UHCGame game) {
        super(game.getPlugin(), "xp", "Show XP and level", Lists.newArrayList("level"), "[player]");
        this.game = game;
    }

    @Override
    public void onCommand(CommandSender commandSender, String s, String[] args) {

        Player queryPlayer = (args.length > 0 ? UtilPlayer.get(args[0]) : (isPlayer(commandSender) ? (Player) commandSender : null));
        if (queryPlayer == null && !isPlayer(commandSender)) {
            commandSender.sendMessage("As console you must provide a player to check.");
            return;
        } else if (queryPlayer == null) {
            couldNotFind(commandSender, args[0]);
            return;
        }

        UHCPlayer player = game.getPlayerManager().getPlayer(queryPlayer);
        if (player == null) {
            couldNotFind(commandSender, args[0]);
            return;
        }

        commandSender.sendMessage(C.SS + C.V + (player.getName().equals(commandSender.getName()) ? "Your" : player.getName() + "'s") + C.C + " UHC XP stats:");
        commandSender.sendMessage(C.C + "Level: " + C.V + player.getLevel());
        commandSender.sendMessage(C.C + "XP: " + C.V + player.getXp());
        commandSender.sendMessage(C.C + "XP needed to level up: " + C.V + game.getXpManager().getXpToNextLevel(player.getLevel(), player.getXp()));

    }

}
