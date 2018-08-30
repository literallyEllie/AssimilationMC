package net.assimilationmc.assibungee.command.commands;

import com.google.common.collect.Lists;
import net.assimilationmc.assibungee.AssiBungee;
import net.assimilationmc.assibungee.command.BungeeCommand;
import net.assimilationmc.assibungee.rank.BungeeGroup;
import net.assimilationmc.assibungee.util.C;
import net.assimilationmc.assibungee.util.D;
import net.assimilationmc.assibungee.util.UtilPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CmdBAdmin extends BungeeCommand implements TabExecutor {

    public CmdBAdmin(AssiBungee assiBungee) {
        super(assiBungee, "badmin", BungeeGroup.ADMIN, Lists.newArrayList(), "<add | remove>", "<group>", "<player>");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        final BungeeGroup bungeeGroup = BungeeGroup.fromString(args[1]);

        if (!sender.getName().equals("CONSOLE") && !UtilPlayer.groupOf(sender).isHigherThanOrEqualTo(bungeeGroup)) {
            sender.sendMessage(new ComponentBuilder("You cannot give this group to other players").color(C.II).create());
            return;
        }

        ProxiedPlayer target = UtilPlayer.get(args[2]);
        if (target == null) {
            couldNotFind(sender, "Player " + args[2]);
            return;
        }

        switch (args[0].toLowerCase()) {
            case "add":
                if (target.getGroups().contains(bungeeGroup.name())) {
                    sender.sendMessage(new ComponentBuilder("They are already in this group.").color(C.II).create());
                    return;
                }
                
                target.addGroups(bungeeGroup.name());
                sender.sendMessage(new ComponentBuilder("[Temp] Added group ").color(C.C).append(bungeeGroup.name()).color(C.V)
                        .append(" to ").color(C.C).append(target.getName()).color(C.V).append(".").color(C.C).create());
                break;
            case "remove":
                if (!target.getGroups().contains(bungeeGroup.name())) {
                    sender.sendMessage(new ComponentBuilder("They are not in this group.").color(C.II).create());
                    return;
                }

                target.removeGroups(bungeeGroup.name());
                sender.sendMessage(new ComponentBuilder("[Temp] Removed group ").color(C.C).append(bungeeGroup.name()).color(C.V)
                        .append(" from ").color(C.C).append(target.getName()).color(C.V).append(".").color(C.C).create());
                break;
            default:
                usage(sender);

        }

    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] args) {
        List<String> tabComplete = Lists.newArrayList();

        if (args.length == 1) {
            if ("add".startsWith(args[0].toLowerCase()))
                tabComplete.add("add");
            else if ("remove".startsWith(args[0].toLowerCase()))
                tabComplete.remove("remove");
            return tabComplete;
        }

        if (args.length == 2) {
            return Arrays.stream(BungeeGroup.values()).map(BungeeGroup::name)
                    .filter(s -> s.startsWith(args[1].toUpperCase())).collect(Collectors.toList());
        }

        if (args.length == 3) {
            return plugin.getProxy().getPlayers().stream().filter(player -> player.getName().toLowerCase().startsWith(args[2].toLowerCase()))
                    .map(ProxiedPlayer::getName).collect(Collectors.toList());
        }

        return tabComplete;
    }

}
