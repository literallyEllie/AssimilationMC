package net.assimilationmc.assicore.command.commands;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilPlayer;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CmdGamemode extends AssiCommand {

    public CmdGamemode(AssiPlugin plugin) {
        super(plugin, "gamemode", "Gamemode command", Rank.ADMIN, Lists.newArrayList("gm"), "<gamemode>", "[player]");
        requirePlayer();
    }

    @Override
    public void onCommand(CommandSender sender, String usedLabel, String[] args) {
        final GameMode gameMode = parse(args[0]);

        Player whom = (Player) sender;

        if (args.length > 1) {
            whom = UtilPlayer.get(args[1]);
            if (whom == null) {
                couldNotFind(sender, args[1]);
                return;
            }
        }

        whom.setGameMode(gameMode);
        if (!whom.equals(sender)) {
            sender.sendMessage(C.C + "Updated the GameMode of " + whom.getDisplayName() + C.C + " to " + C.V + StringUtils.capitalize(gameMode.name().toLowerCase())
                    + C.C + ".");
        }

        whom.sendMessage(C.C + "Your GameMode has been updated to " + C.V + StringUtils.capitalize(gameMode.name().toLowerCase()) + C.C + ".");
    }

    private GameMode parse(String arg) {
        arg = arg.toLowerCase();

        if (arg.startsWith("sp") || arg.equals("3")) return GameMode.SPECTATOR;
        if (arg.startsWith("s") || arg.equals("0")) return GameMode.SURVIVAL;
        if (arg.startsWith("c") || arg.equals("1")) return GameMode.CREATIVE;
        if (arg.startsWith("a") || arg.equals("2")) return GameMode.ADVENTURE;
        return GameMode.SURVIVAL;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        final List<String> completions = Lists.newArrayList();

        if (args.length == 1) {
            completions.addAll(Arrays.stream(GameMode.values()).filter(gameMode -> gameMode.name().startsWith(args[0].toUpperCase()))
                    .map(GameMode::name).collect(Collectors.toList()));
        }

        if (args.length == 2) {
            completions.addAll(UtilPlayer.filterPlayers(args[1]));
        }

        return completions;
    }
}
