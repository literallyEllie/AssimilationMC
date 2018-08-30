package net.assimilationmc.assicore.booster.command;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.booster.Booster;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CmdBoosterAdmin extends AssiCommand {

    public CmdBoosterAdmin(AssiPlugin plugin) {
        super(plugin, "boosterAdmin", "Booster admin options", Rank.ADMIN, Lists.newArrayList(), "<give | take> <booster> <player>");
    }

    @Override
    public void onCommand(CommandSender sender, String usedLabel, String[] args) {

        String action = args[0].toLowerCase();
        if (!action.equals("give") && !action.equals("take")) {
            usage(sender, usedLabel);
            return;
        }

        Booster booster = plugin.getBoosterManager().getBooster(args[1]);
        if (booster == null) {
            sender.sendMessage(C.II + "Invalid booster.");
            sender.sendMessage(C.C + "Valid boosters: " + C.V + Joiner.on(C.C + ", " + C.V).join(plugin.getBoosterManager().getBoosters().values()));
            return;
        }

        Player onlineP = UtilPlayer.get(args[2]);
        AssiPlayer target;
        if (onlineP != null) {
            target = plugin.getPlayerManager().getPlayer(onlineP);
        } else {

            UUID uuid = plugin.getPlayerManager().getUUID(args[2]);
            if (uuid == null) {
                sender.sendMessage(C.II + "Invalid player.");
                return;
            }

            target = plugin.getPlayerManager().getPlayer(uuid);
        }

        if (action.equals("give")) {

            target.addBooster(booster.getId());
            plugin.getPlayerManager().attemptGlobalPlayerMessage(target.getUuid(), false, prefix("Boosters") +
                    ChatColor.AQUA + "You have just received a " + booster.getPretty() + ChatColor.AQUA + " booster!");
            sender.sendMessage(C.C + "Given " + target.getDisplayName() + C.C + " the " + booster.getPretty() + C.C + " booster.");
        }

        if (action.equals("take")) {
            if (!target.getBoosters().containsKey(booster.getId().toLowerCase())) {
                sender.sendMessage(C.II + "Target has no boosters of that kind.");
                return;
            }

            target.removeBooster(booster.getId());
            plugin.getPlayerManager().attemptGlobalPlayerMessage(target.getUuid(), false, prefix("Boosters") +
                    ChatColor.AQUA + "You have been stripped of one " + booster.getPretty() + ChatColor.AQUA + " booster!");

            sender.sendMessage(C.C + "Removed 1 " + booster.getPretty() + C.C + " booster from " + target.getName());

        }

    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {

        if (args.length == 2) {
            return plugin.getBoosterManager().getBoosters().values().stream().map(Booster::getId).filter(s -> s.startsWith(args[1].toUpperCase())).collect(Collectors.toList());
        }

        if (args.length == 3) {
            return UtilPlayer.filterPlayers(args[2]);
        }

        return Lists.newArrayList();

    }

}
