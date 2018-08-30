package net.assimilationmc.assicore.helpop;

import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilString;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CmdHelpop extends AssiCommand {

    private HelpOPManager helpOPManager;

    public CmdHelpop(HelpOPManager manager) {
        super(manager.getPlugin(), "helpop", "Send a HelpOP to staff", Collections.emptyList(), "[message]");
        this.helpOPManager = manager;
        requirePlayer();
    }

    @Override
    public void onCommand(CommandSender sender, String usedLabel, String[] args) {
        final AssiPlayer player = asPlayer(sender);

        if (args.length < 1) {
            usage(sender, usedLabel);
            return;
        }

        // staff wont be able to send helpops

        if (args[0].equalsIgnoreCase("handle") && player.getRank().isHigherThanOrEqualTo(Rank.HELPER)) {

            if (args.length < 2) {
                sender.sendMessage(C.C + "Usage: " + C.II + "/helpop handle <id | name>");
                return;
            }

            try {
                final int id = Integer.parseInt(args[1]);
                helpOPManager.handleHelpOP(id, sender.getName(), true);
            } catch (NumberFormatException e) {
                final String name = args[1];
                helpOPManager.handleHelpOP(name, player.getName(), true);
            }

            return;
        } else if (args[0].equalsIgnoreCase("list") && player.getRank().isHigherThanOrEqualTo(Rank.HELPER)) {

            if (args.length > 1) {

                HelpOP helpOP;

                try {
                    final int id = Integer.parseInt(args[1]);

                    helpOP = helpOPManager.getHelpOP(id);
                    if (helpOP == null) {
                        sender.sendMessage(C.C + "No HelpOP with that ID was found.");
                    }

                } catch (NumberFormatException e) {
                    try {
                        helpOP = helpOPManager.getHelpOPs().get(UUID.fromString(args[1]));
                        if (helpOP == null) {
                            sender.sendMessage(C.C + "No HelpOP was found for your query.");
                            return;
                        }
                    } catch (IllegalArgumentException ex) {
                        sender.sendMessage(C.C + "Provide either a UUID or an ID.");
                        return;
                    }

                }
                detailHelpOP(helpOP, player.getBase());

                return;
            }

            List<HelpOP> helpOPList = helpOPManager.getHelpOPs().values().stream().filter(helpOP -> !helpOP.isHandled()).collect(Collectors.toList());

            sender.sendMessage(C.C + "All unhandled HelpOPs (" + C.V + helpOPList.size() + C.C + ")");

            if (!helpOPList.isEmpty()) {
                sender.sendMessage(C.II + "Click any HelpOP to view more.");
                helpOPList.forEach(helpOP -> player.getBase().spigot().sendMessage(new ComponentBuilder("#")
                        .color(net.md_5.bungee.api.ChatColor.GRAY).append(String.valueOf(helpOP.getId())).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/helpop list " + helpOP.getSender()))
                        .append(" - ").event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/helpop list " + helpOP.getSender()))
                        .color(net.md_5.bungee.api.ChatColor.DARK_PURPLE).append(helpOP.getServer()).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/helpop list " + helpOP.getSender()))
                        .color(net.md_5.bungee.api.ChatColor.RED)
                        .append(" - ").color(net.md_5.bungee.api.ChatColor.DARK_PURPLE)
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/helpop list " + helpOP.getSender()))
                        .append(TimeUnit.MILLISECONDS.toMinutes(
                                System.currentTimeMillis() - helpOP.getSent()) + " minutes ago.").color(net.md_5.bungee.api.ChatColor.GRAY)
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/helpop list " + helpOP.getSender()))
                        .create()));
            }

            return;
        } else if (player.getRank().isHigherThanOrEqualTo(Rank.HELPER)) {
            player.sendMessage(C.II + "Correct usge: /helpop <handle | list>");
            return;
        }

        helpOPManager.postHelpOP(player.getBase(), UtilString.getFinalArg(args, 0));
    }

    public void detailHelpOP(HelpOP helpOP, Player player) {
        player.sendMessage(ChatColor.RESET.toString());
        player.sendMessage(C.C + "HelpOP #" + C.V + helpOP.getId());
        player.sendMessage(C.C + "Sender: " + C.V + helpOP.getSenderOfflineName());
        player.sendMessage(C.C + "Server: " + C.V + helpOP.getServer());
        player.sendMessage(C.C + "Content: " + C.V + helpOP.getServer());
        player.sendMessage(C.C + "Sent: " + C.V + TimeUnit.MILLISECONDS.toMinutes(
                System.currentTimeMillis() - helpOP.getSent()) + " minutes ago.");
        player.sendMessage(C.C + "Handled? " + C.V + helpOP.isHandled());
        if (helpOP.isHandled()) player.sendMessage(C.C + "Handler: " + C.V + helpOP.getHandler());
        else
            player.spigot().sendMessage(new ComponentBuilder("Click this message to handle the HelpOP.").color(net.md_5.bungee.api.ChatColor.GRAY)
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/helpop handle " + helpOP.getId())).create());
        player.sendMessage(ChatColor.RESET.toString());
    }

}
