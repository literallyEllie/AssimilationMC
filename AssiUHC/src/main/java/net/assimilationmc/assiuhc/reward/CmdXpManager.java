package net.assimilationmc.assiuhc.reward;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilPlayer;
import net.assimilationmc.assiuhc.game.UHCGame;
import net.assimilationmc.assiuhc.player.UHCPlayer;
import net.assimilationmc.gameapi.util.GC;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdXpManager extends AssiCommand {

    private UHCGame game;

    public CmdXpManager(UHCGame game) {
        super(game.getPlugin(), "xPManager", "XP Manager.", Rank.ADMIN, Lists.newArrayList(), "<xp | level>",
                "<set | reset | add | take>", "<amount>", "<player>");
        this.game = game;
    }

    @Override
    public void onCommand(CommandSender commandSender, String s, String[] args) {
        String giveType = args[0];
        if (giveType.startsWith("l")) giveType = "level";
        else if (giveType.startsWith("x")) giveType = "xp";
        else {
            commandSender.sendMessage(GC.II + "Please choose a valid type: level or xp");
            return;
        }
        String action = args[1];

        int amount;
        try {
            amount = Math.abs(Integer.parseInt(args[2]));
        } catch (NumberFormatException e) {
            commandSender.sendMessage(GC.II + "Please provide a valid integer for the amount to give.");
            return;
        }

        Player bPlayer = UtilPlayer.get(args[3]);
        if (bPlayer == null) {
            couldNotFind(commandSender, args[3]);
            return;
        }

        UHCPlayer player = game.getPlayerManager().getPlayer(bPlayer);
        if (player == null) {
            couldNotFind(commandSender, args[3]);
            return;
        }

        switch (action.toLowerCase()) {
            case "set":
                if (giveType.equals("level")) {
                    player.setLevel(amount);
                    bPlayer.sendMessage(C.SS + GC.C + "New level: " + GC.V + player.getLevel());
                } else {
                    player.setXp(amount);
                    bPlayer.sendMessage(C.SS + GC.C + "New XP count: " + GC.V + player.getXp());
                }
                break;
            case "reset":
                if (giveType.equals("level")) {
                    player.setLevel(XPManager.DEFAULT_LEVEL);
                    bPlayer.sendMessage(C.SS + GC.C + "New level: " + GC.V + player.getLevel());
                } else {
                    player.setXp(XPManager.DEFAULT_XP);
                    bPlayer.sendMessage(C.SS + GC.C + "New XP count: " + GC.V + player.getXp());
                }
                break;
            case "add":
                if (giveType.equals("level")) {
                    player.setLevel(amount);
                    bPlayer.sendMessage(C.SS + GC.C + "New level: " + GC.V + player.getLevel());
                } else {
                    player.setXp(amount);
                    bPlayer.sendMessage(C.SS + ChatColor.GREEN + "+" + GC.V + player.getXp() + " XP");
                }
                break;
            case "take":
                if (giveType.equals("level")) {
                    player.setLevel(amount);
                    bPlayer.sendMessage(C.SS + GC.C + "New level: " + GC.V + player.getLevel());
                } else {
                    player.setXp(amount);
                    bPlayer.sendMessage(C.SS + ChatColor.RED + "-" + GC.V + player.getXp() + " XP");
                }
                break;
            default:
                commandSender.sendMessage(GC.II + "Please select a valid action between: set, reset, add and take.");
                return;
        }

        commandSender.sendMessage(GC.C + "Updated the " + GC.V + giveType + GC.C + " of " + bPlayer.getDisplayName() + GC.C +
                " to " + (giveType.equals("level") ? GC.V + player.getLevel() : GC.V + player.getXp()) + GC.C + ".");

    }

}
