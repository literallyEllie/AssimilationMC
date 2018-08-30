package net.assimilationmc.assicore.cosmetic.command;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.cosmetic.CosmeticType;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CmdCosmetic extends AssiCommand {

    public CmdCosmetic(AssiPlugin plugin) {
        super(plugin, "cosmetic", "Display the cosmetic menu", Lists.newArrayList("cosmetics",
                "perks"), "[give [player] [cosmetic] | take [player] [cosmetic] | toggle [player] [cosmetic]]");
    }

    @Override
    public void onCommand(CommandSender sender, String usedLabel, String[] args) {

        if (isPlayer(sender) && (!asPlayer(sender).getRank().isHigherThanOrEqualTo(Rank.ADMIN) ||
                args.length < 1)) {
            plugin.getCosmeticManager().getMenuCosmetics().open((Player) sender);
            return;
        }

        if (args.length < 3) {
            usage(sender, usedLabel);
            return;
        }

        Player bPlayer = UtilPlayer.get(args[1]);

        if (bPlayer == null) {
            couldNotFind(sender, args[1]);
            return;
        }

        AssiPlayer player = plugin.getPlayerManager().getPlayer(bPlayer);

        if (player == null) {
            couldNotFind(sender, args[1]);
            return;
        }

        CosmeticType cosmeticType = CosmeticType.fromInput(args[2]);
        if (cosmeticType == null) {
            couldNotFind(sender, args[2]);
            return;
        }

        switch (args[0].toLowerCase()) {

            case "give":
                if (player.hasCosmetic(cosmeticType)) {
                    sender.sendMessage(C.II + player.getName() + " already has that cosmetic.");
                    return;
                }
                player.addCosmetic(cosmeticType);
                player.sendMessage(prefix("Cosmetics") + "You have been granted permission to use the " +
                        "cosmetic " + C.V + cosmeticType.getPrettyName());
                sender.sendMessage(C.C + "Given " + player.getName() + " the cosmetic " + C.V + cosmeticType.getPrettyName() + C.C + ".");
                break;
            case "take":
                if (!player.hasCosmetic(cosmeticType)) {
                    sender.sendMessage(C.II + player.getName() + " doesn't have that cosmetic.");
                    return;
                }
                plugin.getCosmeticManager().removePlayerCosmetic(bPlayer, cosmeticType);
                player.removeCosmetic(cosmeticType);
                player.sendMessage(prefix("Cosmetics") + "You no longer have access to use the " +
                        "cosmetic " + C.V + cosmeticType.getPrettyName());
                sender.sendMessage(C.C + "Removed " + player.getName() + " the cosmetic " + C.V + cosmeticType.getPrettyName() + C.C + ".");
                break;
            case "toggle":
                if (plugin.getCosmeticManager().hasActiveCosmetic(bPlayer, cosmeticType)) {
                    plugin.getCosmeticManager().removePlayerCosmetic(bPlayer, cosmeticType);
                    sender.sendMessage(C.C + "Toggled off " + bPlayer.getName() + "'s cosmetic of " + C.V +
                            cosmeticType.getPrettyName() + C.C + ".");
                    player.sendMessage(prefix("Cosmetics") +
                            "Your cosmetic " + C.V + cosmeticType.getPrettyName() + C.C + " has been disabled.");
                    return;
                }
                plugin.getCosmeticManager().playPlayerCosmetic(bPlayer, cosmeticType);
                sender.sendMessage(C.C + "Toggled on " + bPlayer.getName() + "'s cosmetic.");
                player.sendMessage(prefix("Cosmetics") + "The cosmetic " + C.V + cosmeticType.getPrettyName() +
                        C.C + " has been enabled.");
                break;
            default:
                usage(sender, usedLabel);

        }


    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (!isPlayer(sender)) return Lists.newArrayList();

        if (args.length == 1 && asPlayer(sender).getRank().isHigherThanOrEqualTo(Rank.ADMIN)) {
            return Lists.newArrayList("give", "take", "toggle")
                    .stream().filter(s -> s.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        }

        if (args.length == 2) {
            return UtilPlayer.filterPlayers(args[1]);
        }

        if (args.length == 3) {
            return Arrays.stream(CosmeticType.values()).filter(type -> type.name().toLowerCase()
                    .startsWith(args[2].toLowerCase())).map(CosmeticType::name).collect(Collectors.toList());
        }

        return Lists.newArrayList();
    }

}
