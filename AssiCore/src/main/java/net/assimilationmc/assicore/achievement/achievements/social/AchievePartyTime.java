package net.assimilationmc.assicore.achievement.achievements.social;

import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.achievement.Achievement;
import net.assimilationmc.assicore.achievement.AchievementCategory;
import net.assimilationmc.assicore.achievement.achievements.AchievementProgressList;
import net.assimilationmc.assicore.party.Party;
import net.assimilationmc.assicore.party.PartyJoinEvent;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.util.UtilPlayer;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;

import java.util.Set;
import java.util.UUID;

public class AchievePartyTime extends Achievement {

    public AchievePartyTime(AssiPlugin plugin) {
        super(plugin, "PARTY_TIME", AchievementCategory.SOCIAL, "Party time!", ChatColor.GREEN + "80 bucks",
                "Join/Make a party with at least 3 members");
    }

    @Override
    public Set<String> showProgress(AssiPlayer player) {
        AchievementProgressList progressList = new AchievementProgressList();

        Party party = getPlugin().getPartyManager().getPartyOf(player.getBase(), false);
        if (party != null) {
            progressList.addDone("Make a party");
        } else progressList.addTodo("Make a party");

        progressList.addTodo("Party size: " + (party == null ? "0" : party.getMembers().size() + 1) + "/3");

        return progressList.compile();
    }

    @Override
    public void giveReward(AssiPlayer player) {
        player.addBucks(80);
    }

    @EventHandler
    public void on(final PartyJoinEvent e) {
        final Party party = e.getParty();
        if (party.getMembers().size() + 1 >= 3) {

            checkGive(party.getLeader());
            party.getMembers().forEach(this::checkGive);

        }

    }

    private void checkGive(UUID uuid) {
        if (UtilPlayer.get(uuid) == null) return;

        AssiPlayer player = getPlugin().getPlayerManager().getPlayer(uuid);

        if (!player.hasAchievement(getId())) {
            give(player);
        }
    }

}
