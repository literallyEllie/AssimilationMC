package net.assimilationmc.assicore.leaderboard;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.C;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdLeaderboardAdmin extends AssiCommand {

    private final LeaderboardManager leaderboardManager;

    public CmdLeaderboardAdmin(LeaderboardManager leaderboardManager) {
        super(leaderboardManager.getPlugin(), "leaderboardadmin", "Leaderboard admin commands. Have it selected", Rank.ADMIN,
                Lists.newArrayList(), "<create [type] [place] | delete>");
        requirePlayer();
        this.leaderboardManager = leaderboardManager;
    }

    @Override
    public void onCommand(CommandSender sender, String usedLabel, String[] args) {
        Player player = (Player) sender;

        final NPC selected = CitizensAPI.getDefaultNPCSelector().getSelected(sender);
        if (selected == null) {
            sender.sendMessage(C.II + "Please select an NPC");
            return;
        }

        if (args[0].equalsIgnoreCase("create")) {
            if (args.length < getRequiredArgs() + 2) {
                usage(sender, usedLabel);
                return;
            }

            String type = args[1];
            int place;

            try {
                place = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(C.II + "Invalid number.");
                return;
            }

            leaderboardManager.create(player, selected.getId(), type, place);
            return;
        }

        leaderboardManager.delete(player, selected.getId());
    }

}
