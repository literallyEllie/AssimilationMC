package net.assimilationmc.assicore.party;

import com.google.common.collect.Sets;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.redis.RedisObject;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class Party implements RedisObject {

    private UUID leader;
    private Set<UUID> members;
    private Set<UUID> invites;
    private Set<Player> chatToggled;
    private String target;

    public Party(UUID leader) {
        this.leader = leader;
        this.members = Sets.newHashSet();
        this.invites = Sets.newHashSet();
        this.chatToggled = Sets.newHashSet();
    }

    public UUID getLeader() {
        return leader;
    }

    public Set<UUID> getMembers() {
        return members;
    }

    public Set<String> getMemberNames(AssiPlugin plugin) {
        Set<String> memberNames = Sets.newHashSet();
        for (UUID member : members) {
            Player player = UtilPlayer.get(member);
            if (player == null) {
                memberNames.add(plugin.getPlayerManager().getOfflinePlayer(member).getName());
                continue;
            }
            memberNames.add(player.getName());
        }
        return memberNames;
    }

    public void addMember(Player player) {
        this.members.add(player.getUniqueId());
    }

    public void removeMember(Player player) {
        this.members.remove(player.getUniqueId());
    }

    public void messageParty(String message) {
        final Player leaderPlayer = UtilPlayer.get(leader);
        if (leaderPlayer != null) leaderPlayer.sendMessage(message);
        for (UUID member : members) {
            Player playerMember = UtilPlayer.get(member);
            if (playerMember == null) continue;
            playerMember.sendMessage(message);
        }
    }

    public Set<UUID> getInvites() {
        return invites;
    }

    public void invite(Player player) {
        invites.add(player.getUniqueId());
    }

    public void uninvite(Player player) {
        invites.remove(player.getUniqueId());
    }

    public boolean isInvited(Player player) {
        return invites.contains(player.getUniqueId());
    }

    public Set<Player> getChatToggled() {
        return chatToggled;
    }

    public boolean toggleChat(Player player) {
        if (chatToggled.contains(player)) {
            chatToggled.remove(player);
            return false;
        }
        chatToggled.add(player);
        return true;
    }

    public void chat(Player sender, String message) {
        messageParty(PartyManager.PREFIX + formatName(sender) + C.SS + ChatColor.RESET + message);
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public boolean hasTarget() {
        return target != null;
    }

    public boolean includes(Player player) {
        return leader.equals(player.getUniqueId()) || members.contains(player.getUniqueId());
    }

    @Override
    public String asRedisKey() {
        return leader.toString();
    }

    public String formatName(Player player) {
        return C.V + (player.getUniqueId().equals(player.getUniqueId()) ? ChatColor.BOLD.toString() : "") + player.getName();
    }

}
