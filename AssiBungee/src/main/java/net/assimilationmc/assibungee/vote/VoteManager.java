package net.assimilationmc.assibungee.vote;

import com.google.common.collect.Sets;
import com.vexsoftware.votifier.bungee.events.VotifierEvent;
import com.vexsoftware.votifier.model.Vote;
import net.assimilationmc.assibungee.AssiBungee;
import net.assimilationmc.assibungee.Module;
import net.assimilationmc.assibungee.redis.pubsub.RedisPubSubMessage;
import net.assimilationmc.assibungee.util.C;
import net.assimilationmc.assibungee.util.UtilPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.event.EventHandler;

import java.util.Set;

public class VoteManager extends Module {

    private Set<String> missedVotes;

    private BaseComponent[] voteMessage;

    public VoteManager(AssiBungee assiBungee) {
        super(assiBungee, "Vote Manager");
    }

    @Override
    protected void start() {
        this.missedVotes = Sets.newHashSet();

        this.voteMessage = new ComponentBuilder(C.SS).append("Thank you for voting for us! ").color(ChatColor.AQUA).append("In return, have some gifts...").color(C.C).create();
    }

    @Override
    protected void end() {
        missedVotes.clear();
    }

    @EventHandler
    public void on(final VotifierEvent e) {
        final Vote vote = e.getVote();

        ProxiedPlayer player = UtilPlayer.get(vote.getUsername());

        if (player == null) {
            log(vote.getUsername() + " sent in vote but are offline.");
            missedVotes.add(vote.getUsername());
            return;
        }

        player.sendMessage(new TextComponent());
        player.sendMessage(voteMessage);

        getPlugin().getRedisManager().sendPubSubMessage("VOTE", new RedisPubSubMessage(player.getServer().getInfo().getName(),
                getPlugin().getServerData().getId(), "NEW", new String[]{player.getName()}));
        log("Vote from recorded " + vote.getUsername() + " [" + vote.getServiceName() + "]");
    }

    @EventHandler
    public void on(final PostLoginEvent e) {
        final ProxiedPlayer player = e.getPlayer();
        if (!missedVotes.contains(player.getName())) return;
        missedVotes.remove(player.getName());

        player.sendMessage(new TextComponent());
        player.sendMessage(voteMessage);
        getPlugin().getRedisManager().sendPubSubMessage("VOTE", new RedisPubSubMessage(player.getServer().getInfo().getName(),
                getPlugin().getServerData().getId(), "NEW", new String[]{player.getName()}));
    }

}
