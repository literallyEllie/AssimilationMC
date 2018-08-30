package net.assimilationmc.assicore.command.commands;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdHeal extends AssiCommand {

    public CmdHeal(AssiPlugin plugin) {
        super(plugin, "heal", "Heal", Rank.ADMIN, Lists.newArrayList(), "[player]");
        requirePlayer();
    }

    @Override
    public void onCommand(CommandSender sender, String usedLabel, String[] args) {

        Player target = null;
        if (args.length > 0) {
            target = UtilPlayer.get(args[0]);

            if (target == null) {
                couldNotFind(sender, args[0]);
                return;
            }
        }

        if (target == null) target = (Player) sender;

        target.setFoodLevel(20);
        target.setFireTicks(0);
        target.setHealth(target.getMaxHealth());
        target.setSaturation(20);

        if (target == sender) {
            target.sendMessage(C.C + ChatColor.ITALIC + "Medic!");
            return;
        }

        target.sendMessage(C.C + ChatColor.ITALIC + "A bright light appeared in the sky and you find yourself feeling better than ever...");
        sender.sendMessage(C.C + "Healed " + target.getName());
    }

}
