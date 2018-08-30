package net.assimilationmc.assicore.util;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlayerVote<T> {

    private Set<UUID> voted;
    private Map<T, Integer> votes;

    /**
     * Initiate a vote backbone.
     * <p>
     * Can be used for polls.
     */
    public PlayerVote() {
        this.voted = Sets.newHashSet();
        this.votes = Maps.newHashMap();
    }

    /**
     * @return the UUIDs of those voted.
     */
    public Set<UUID> getVoted() {
        return voted;
    }

    /**
     * Check if a player has already voted.
     *
     * @param player the player to check.
     * @return if they've voted or not.
     */
    public boolean hasVoted(Player player) {
        return voted.contains(player.getUniqueId());
    }

    /**
     * Represents the player voting.
     * If they've already voted it will be returned before added.
     *
     * @param player the player voting.
     * @param choice their choice.
     */
    public void vote(Player player, T choice) {
        if (hasVoted(player)) return;
        votes.put(choice, votes.getOrDefault(choice, 0) + 1);
        voted.add(player.getUniqueId());
    }

    /**
     * Calculates the item that has been most voted for.
     *
     * @return the most voted item.
     */
    public T getMostVoted() {
        int topVote = 0;
        T chosen = null;
        for (Map.Entry<T, Integer> tIntegerEntry : votes.entrySet()) {
            final T choice = tIntegerEntry.getKey();
            final Integer votes = tIntegerEntry.getValue();
            if (chosen == null || votes > topVote) {
                chosen = choice;
                topVote = votes;
            }
        }
        return chosen;
    }

}
