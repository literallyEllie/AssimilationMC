package net.assimilationmc.ellie.assicore.command;

import net.assimilationmc.ellie.assicore.api.economy.Economy;
import net.assimilationmc.ellie.assicore.util.ColorChart;
import net.assimilationmc.ellie.assicore.util.MessageLib;
import net.assimilationmc.ellie.assicore.util.PermissionLib;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

/**
 * Created by Ellie on 14/03/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdEconomy extends AssiCommand {

    public CmdEconomy(){
        super("economy", PermissionLib.CMD.ECO, "economy <set|give|take> <player> <amount>", "Server economy management", Collections.singletonList("eco"));
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        // /eco set <player> <amount>

        String type;
        Player player;
        int amount;

        if (args.length >= 3) {
            player = Bukkit.getPlayer(args[1]);
            if (player == null) {
                sendPMessage(sender, MessageLib.PLAYER_OFFLINE);
                return;
            }

            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                sendMessage(sender, MessageLib.INVALID_NUMBER);
                return;
            }
            type = args[0];

            Economy economy = getCore().getModuleManager().getEconomyManager().getEconomy();

            switch (type.toLowerCase()) {
                case "set":
                    economy.setBalance(player.getUniqueId(), amount);
                    sendPMessage(sender, "Set the balance of " + ColorChart.VARIABLE + player.getName() + ColorChart.R + " to " + ColorChart.VARIABLE + amount + ColorChart.R + ".");
                    break;
                case "give":
                    economy.giveMoney(player.getUniqueId(), amount);
                    sendPMessage(sender, "Given " + ColorChart.VARIABLE + player.getName() + ColorChart.R + " " + amount + ColorChart.R + ".");
                    break;
                case "take":
                    if (!economy.canAfford(player.getUniqueId(), amount)) {
                        sendPMessage(sender, ColorChart.VARIABLE + player.getName() + ColorChart.R + " cannot afford that.");
                        break;
                    }
                    economy.deductMoney(player.getUniqueId(), amount);
                    sendPMessage(sender, "Deducted " + ColorChart.VARIABLE + amount + ColorChart.R + " from " + ColorChart.VARIABLE + player.getName() + ColorChart.R + "'s account.");
                    break;

                default:
                    sendMessage(sender, correctUsage());
                    break;
            }
            return;
        }
        sendMessage(sender, correctUsage());
    }

}
