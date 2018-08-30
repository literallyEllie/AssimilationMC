package net.assimilationmc.assicore.command.commands.eco;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilServer;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class CmdEconomy extends AssiCommand {

    public CmdEconomy(AssiPlugin plugin) {
        super(plugin, "economy", "Edit the bucks balance of a player", Rank.ADMIN, Lists.newArrayList("eco"),
                "<bucks | uc>", "<set | reset | add | take>", "<amount (if you're resetting balance, put as 0)>", "<player>");
    }

    @Override
    public void onCommand(CommandSender sender, String usedLabel, String[] args) {
        String moneyType = args[0];
        if (moneyType.startsWith("b")) moneyType = "bucks";
        else if (moneyType.startsWith("u")) moneyType = "uc";
        else {
            sender.sendMessage(C.II + "Please choose a valid currency: bucks or uc (ultra coins)");
            return;
        }

        String action = args[1];

        int amount;
        try {
            amount = Math.abs(Integer.parseInt(args[2]));
        } catch (NumberFormatException e) {
            sender.sendMessage(C.II + "Please provide a valid integer for the amount to give.");
            return;
        }

        UUID uuid = plugin.getPlayerManager().getUUID(args[3]);

        if (uuid == null) {
            sender.sendMessage(C.C + "It doesn't look like anyone with the name " + C.V + args[1] + C.C + " has ever joined the network.");
            return;
        }
        AssiPlayer target = plugin.getPlayerManager().getOfflinePlayer(uuid);

        switch (action.toLowerCase()) {
            case "set":
                if (moneyType.equals("bucks"))
                    target.setBucks(amount);
                else target.setUltraCoins(amount);
                break;
            case "reset":
                if (moneyType.equals("bucks"))
                    target.setBucks(UtilServer.DEFAULT_BALANCE_BUCKS);
                else target.setUltraCoins(UtilServer.DEFAULT_BALANCE_UC);
                break;
            case "add":
                if (moneyType.equals("bucks"))
                    target.addBucks(amount);
                else target.addUltraCoins(amount);
                break;
            case "take":
                if (moneyType.equals("bucks"))
                    target.takeBucks(amount);
                else target.takeUltraCoins(amount);
                break;
            default:
                sender.sendMessage(C.II + "Please select a valid action between: set, reset, add and take.");
                return;
        }

        sender.sendMessage(C.C + "Updated the " + C.V + moneyType + C.C + " balance of " + target.getDisplayName() + C.C +
                " to " + (moneyType.equals("bucks") ? C.BUCKS + target.getBucks() + "B" : C.UC + target.getUltraCoins() + "UC") + ".");
    }

}
