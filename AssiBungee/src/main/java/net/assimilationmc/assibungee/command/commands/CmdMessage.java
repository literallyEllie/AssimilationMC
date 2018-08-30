package net.assimilationmc.assibungee.command.commands;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.assimilationmc.assibungee.AssiBungee;
import net.assimilationmc.assibungee.command.BungeeCommand;
import net.assimilationmc.assibungee.stafflog.StaffLog;
import net.assimilationmc.assibungee.util.C;
import net.assimilationmc.assibungee.util.UtilPlayer;
import net.assimilationmc.assibungee.util.UtilString;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.event.EventHandler;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class CmdMessage extends BungeeCommand implements TabExecutor, Listener {

    private Map<UUID, UUID> messageMap;

    public CmdMessage(AssiBungee assiBungee) {
        super(assiBungee, "message", Lists.newArrayList("m", "msg", "tell", "t", "whispher"), "<player>", "<message>");
        this.messageMap = Maps.newHashMap();
        requirePlayer();
        assiBungee.registerListener(this);
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) sender;
        ProxiedPlayer target = UtilPlayer.get(args[0]);

        if (target == null) {
            couldNotFind(sender, "Player " + args[0]);
            return;
        }

        if (plugin.getMessageOff().contains(target.getUniqueId())) {
            player.sendMessage(new ComponentBuilder("This player is unable to receive messages right now. " +
                    "If you need help please use /helpop").color(ChatColor.RED).create());
            return;
        }

        String message = UtilString.getFinalArg(args, 1);

        messageMap.put(target.getUniqueId(), player.getUniqueId());

        sender.sendMessage(new ComponentBuilder("You").color(ChatColor.GREEN).bold(true).append(" -> ")
                .color(ChatColor.DARK_GRAY).bold(false).append(target.getName()).color(ChatColor.GREEN).append(": ").color(ChatColor.DARK_GRAY)
                .append(message).color(ChatColor.RESET).create());

        target.sendMessage(new ComponentBuilder(player.getName()).color(ChatColor.GREEN).append(" -> ")
                .color(ChatColor.DARK_GRAY).append("You").color(ChatColor.GREEN).bold(true).append(": ").color(ChatColor.DARK_GRAY).bold(false)
                .append(message).color(ChatColor.RESET)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to reply to this message").color(C.C).create()))
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/r " + player.getName() + " ")).create());

        if (target.hasPermission("staff.track")) {
            final StaffLog log = plugin.getLoggerManager().getLog(target.getUniqueId());
            log.write("RECV MSG", "From: " + player.getName() + " - Message: " + message);
        }

        if (plugin.getMessageOff().contains(player.getUniqueId())) {
            player.sendMessage(new ComponentBuilder("You have messages off so you will not get a reply.")
                    .color(ChatColor.RED).italic(true).create());
        }

    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] args) {
        if (args.length != 1) {
            return Lists.newArrayList();
        }

        return plugin.getProxy().getPlayers().stream().filter(player -> player.getName().toLowerCase().startsWith(args[0].toLowerCase()))
                .map(ProxiedPlayer::getName).collect(Collectors.toList());
    }

    @EventHandler
    public void on(final PlayerDisconnectEvent e) {
        messageMap.remove(e.getPlayer().getUniqueId());
    }

    public Map<UUID, UUID> getMessageMap() {
        return messageMap;
    }

}
