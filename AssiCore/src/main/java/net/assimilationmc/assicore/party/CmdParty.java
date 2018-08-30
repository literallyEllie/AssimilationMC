package net.assimilationmc.assicore.party;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class CmdParty extends AssiCommand {

    private PartyManager partyManager;

    public CmdParty(PartyManager partyManager) {
        super(partyManager.getPlugin(), "party", "Party management", Lists.newArrayList(), "[create | leave | chat | invite [name] | uninvite [name] | " +
                "accept [name] | deny [name]]");
        this.partyManager = partyManager;
        requirePlayer();
    }

    @Override
    public void onCommand(CommandSender commandSender, String usedLabel, String[] args) {
        Player sender = (Player) commandSender;
        Party party = partyManager.getPartyOf(sender, false);

        if (args.length == 0) {
            if (party != null) {
                sender.sendMessage(PartyManager.PREFIX + "You are currently in a party!");
                sender.sendMessage(ChatColor.GREEN + "Leader: " + C.V + ChatColor.BOLD + UtilPlayer.get(party.getLeader()).getName());

                if (party.getMembers().isEmpty()) {
                    sender.sendMessage(C.II + "Looks like there are no members. Time to do some inviting!");
                } else
                    sender.sendMessage(ChatColor.GREEN + "Members: " + C.V + Joiner.on(C.C + ", " + C.V).join(party.getMemberNames(plugin)));
                return;
            }
            sender.sendMessage(PartyManager.PREFIX + "You are not in a party. Time to create one!");
            sender.sendMessage(C.II + "/party create");
            return;
        }

        if (args.length == 1) {

            if (args[0].equalsIgnoreCase("create")) {
                if (party != null) {
                    sender.sendMessage(PartyManager.PREFIX + "You are already in a party!");
                    return;
                }
                partyManager.createParty(sender);
            } else if (args[0].equalsIgnoreCase("leave")) {
                if (party == null) {
                    sender.sendMessage(PartyManager.PREFIX + "You are not currently in a party.");
                    return;
                }

                partyManager.leaveParty(sender, party);
            } else if (args[0].equalsIgnoreCase("chat")) {
                if (party == null) {
                    sender.sendMessage(PartyManager.PREFIX + "You are not currently in a party.");
                    return;
                }

                sender.sendMessage(PartyManager.PREFIX + C.V + (party.toggleChat(sender) ? "Toggled into" : "Untoggled out of") + C.II + " the party chat.");
            } else usage(commandSender, usedLabel);


        } else if (args.length == 2) {

            if (args[0].equalsIgnoreCase("invite") || args[0].equalsIgnoreCase("uninvite")) {

                if (party == null) {
                    sender.sendMessage(PartyManager.PREFIX + "You are not currently in a party.");
                    return;
                }

                if (!party.getLeader().equals(sender.getUniqueId())) {
                    sender.sendMessage(PartyManager.PREFIX + C.II + "You can't invite to this party.");
                    return;
                }

                if (args[1].equalsIgnoreCase(sender.getName())) {
                    sender.sendMessage(PartyManager.PREFIX + C.II + "You can't invite yourself to your own party, that's sad! :(");
                    return;
                }

                Player player = UtilPlayer.get(args[1]);
                if (player == null) {
                    couldNotFind(sender, args[1]);
                    return;
                }

                Party oParty = partyManager.getPartyOf(player, false);
                if (oParty != null) {
                    sender.sendMessage(PartyManager.PREFIX + C.V + player.getName() + C.II + " already has a party!");
                    return;
                }

                if (args[0].equalsIgnoreCase("invite")) {
                    if (party.isInvited(player)) {
                        sender.sendMessage(PartyManager.PREFIX + C.V + player.getName() + C.II + " has already been invited to this party.");
                        return;
                    }

                    party.invite(player);

                    player.sendMessage(PartyManager.PREFIX + "You have received an invite from " + C.V + sender.getName() + C.II + " to join their party.");
                    player.sendMessage(C.II + "To accept it do " + C.V + "/party accept " + sender.getName() + C.II + " or to decline do " + C.V + "/party deny " +
                            sender.getName());

                    party.messageParty(PartyManager.PREFIX + party.formatName(sender) + C.II + " has invited " + C.V + player.getName() + C.II + " to the party.");

                } else if (args[0].equalsIgnoreCase("uninvite")) {
                    if (!party.isInvited(player)) {
                        sender.sendMessage(PartyManager.PREFIX + C.II + "There is no outgoing invite for " + C.V + player.getName() + C.II + " to join this party.");
                        return;
                    }

                    party.uninvite(player);
                    party.messageParty(PartyManager.PREFIX + party.formatName(sender) + C.II + " has uninvited " + C.V + player.getName() + C.II + " to the party.");

                }
            } else if (args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("deny")) {

                if (party != null) {
                    sender.sendMessage(PartyManager.PREFIX + "You are already in a party.");
                    return;
                }

                Player player = UtilPlayer.get(args[1]);
                if (player == null) {
                    couldNotFind(sender, args[1]);
                    return;
                }

                Party oParty = partyManager.getPartyOf(player, false);
                if (oParty == null) {
                    sender.sendMessage(PartyManager.PREFIX + C.V + player.getName() + C.II + " has no party.");
                    return;
                }

                if (!oParty.isInvited(sender)) {
                    sender.sendMessage(PartyManager.PREFIX + C.II + "You have no pending invite to join this party.");
                    return;
                }

                if (args[0].equalsIgnoreCase("accept")) {
                    partyManager.joinParty(sender, oParty);

                } else if (args[0].equalsIgnoreCase("deny")) {
                    oParty.uninvite(sender);
                    sender.sendMessage(PartyManager.PREFIX + C.II + "Rejected invite to join the party of " + C.V + player.getName() + C.II + ".");
                }

            }

        } else usage(commandSender, usedLabel);


    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return Lists.newArrayList("create", "chat", "leave", "invite", "uninvite", "accept", "deny")
                    .stream().filter(s -> s.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        }

        if (args.length == 2) {
            return UtilPlayer.filterPlayers(args[1]);
        }

        return Lists.newArrayList();
    }

}
