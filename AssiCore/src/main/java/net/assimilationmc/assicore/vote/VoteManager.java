package net.assimilationmc.assicore.vote;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.Module;
import net.assimilationmc.assicore.event.VoteEvent;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.redis.pubsub.RedisChannelSubscriber;
import net.assimilationmc.assicore.redis.pubsub.RedisPubSubMessage;
import net.assimilationmc.assicore.util.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class VoteManager extends Module implements RedisChannelSubscriber {

    private static final long STREAK_EXPIRE = TimeUnit.DAYS.toMillis(2);
    private List<String> voteLinks;

    public VoteManager(AssiPlugin plugin) {
        super(plugin, "Vote Manager");
    }

    @Override
    protected void start() {
        this.voteLinks = Lists.newArrayList("https://minecraftservers.org/server/510497",
                "https://www.planetminecraft.com/server/assimilationmc/vote/", "https://minecraft-mp.com/server/202351/vote/");

        getPlugin().getRedisManager().registerChannelSubscriber("VOTE", this);

        getPlugin().getCommandManager().registerCommand(new CmdVote(getPlugin()));
    }

    @Override
    protected void end() {

    }

    @Override
    public void onChannelMessage(RedisPubSubMessage message) {
        if (!message.getSubject().equals("NEW")) return;
        String who = message.getArgs()[0];

        // it will only be handled by the server they're online
        Player bPlayer = UtilPlayer.get(who);
        if (bPlayer == null) return;

        AssiPlayer player = getPlugin().getPlayerManager().getOnlinePlayers().get(bPlayer.getUniqueId());
        player.addVote();
        if (player.getLastVote() != 0 && streakExpired(player.getLastVote())) {
            player.sendMessage(C.II + "Your last vote streak has expired! It will now be reset to day 1.");
            player.addVoteStreak();
        } else if (player.getLastVote() == 0 || (player.getLastVote() != 0 && UtilTime.elapsed(player.getLastVote(), TimeUnit.DAYS.toMillis(1)))) {
            player.addVoteStreak();
        }
        player.setLastVote(UtilTime.now());

        UtilServer.callEvent(new VoteEvent(player));
    }

    public List<String> getVoteLinks() {
        return voteLinks;
    }

    @EventHandler
    public void on(final PlayerJoinEvent e) {
        Player player = e.getPlayer();

        AssiPlayer assiPlayer = getPlugin().getPlayerManager().getPlayer(player);
        if (assiPlayer.hasVoteStreak()) {
            long lastVote = assiPlayer.getLastVote();

            if (streakExpired(lastVote)) {
                assiPlayer.setVoteStreak(0);
                assiPlayer.sendMessage(C.II + "You have lost your vote streak!");
            }

        }

    }

    private boolean streakExpired(long lastVote) {
        return UtilTime.elapsed(lastVote, STREAK_EXPIRE);
    }

    @EventHandler
    public void on(final VoteEvent e) {
        final AssiPlayer player = e.getPlayer();

        int giveAmountBucks = BuckRewards.BASE_VOTE;
        int bonusBucks = 0;

        if (player.getVoteStreak() > 10) {
            bonusBucks += BuckRewards.BONUS_VOTE;
        }

        player.addBucks(giveAmountBucks + bonusBucks);
        if (bonusBucks != 0) {
            player.sendMessage(C.II + ChatColor.ITALIC + "(+" + bonusBucks + " bucks for your vote streak!)");
        }

        if (player.getVoteStreak() > 15) {
            int giveAmountUC = UltraCoinRewards.BASE_VOTE;
            player.addUltraCoins(giveAmountUC);
        }

    }

}
