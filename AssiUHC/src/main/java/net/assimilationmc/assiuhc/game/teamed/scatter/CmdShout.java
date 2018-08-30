package net.assimilationmc.assiuhc.game.teamed.scatter;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilString;
import net.assimilationmc.assicore.util.UtilTime;
import net.assimilationmc.gameapi.game.AssiGame;
import net.assimilationmc.gameapi.phase.GamePhase;
import net.assimilationmc.gameapi.spectate.GameSpectateManager;
import net.assimilationmc.gameapi.team.GameTeam;
import net.assimilationmc.gameapi.util.GC;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CmdShout extends AssiCommand {

    private AssiGame assiGame;

    private Map<UUID, Long> lastShout;

    public CmdShout(AssiGame game) {
        super(game.getPlugin(), "shout", "Send a call out, everyone will hear in a 50 block radius", Lists.newArrayList(), "<message>");
        requirePlayer();
        this.assiGame = game;

        this.lastShout = Maps.newHashMap();
    }

    @Override
    public void onCommand(CommandSender commandSender, String s, String[] strings) {
        Player sender = (Player) commandSender;

        if (assiGame.getGamePhase() == GamePhase.LOBBY || assiGame.getGamePhase() == GamePhase.END) {
            sender.sendMessage(prefix(s) + "There is currently no need to shout.");
            return;
        }

        GameTeam team = assiGame.getTeamManager().getTeam(sender);

        if (team == null || team.getName().equals(GameSpectateManager.SPECTATOR_TEAM_NAME)) {
            sender.sendMessage(prefix(s) + "You cannot use this command if you are not playing.");
            return;
        }

        String toSay = UtilString.getFinalArg(strings, 0);

        if (!canShout(sender) || plugin.getChatManager().passesChatFilter(toSay) != null) {
            sender.sendMessage(C.C + ChatColor.ITALIC + "Drowsiness flushes over as you try to reach for the air to make another shout.");
            sender.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 3, false, false));
            return;
        }

        lastShout.put(sender.getUniqueId(), UtilTime.now());

        String msg = C.II + "[Shout] " + ChatColor.BOLD + team.getColor().toString() + team.getName() + ChatColor.RESET + GC.C + " " + sender.getName() + " " + C.SS + GC.C +
                toSay;

        sender.sendMessage(msg);

        for (Entity entity : sender.getNearbyEntities(50, 50, 50)) {
            if (!(entity instanceof Player)) continue;
            Player listener = (Player) entity;
            listener.sendMessage(msg);
        }

        plugin.getLogger().info("[SHOUT] " + sender.getName() + ": " + msg);
    }

    private boolean canShout(Player player) {
        if (!lastShout.containsKey(player.getUniqueId())) {
            return true;
        }
        return UtilTime.elapsed(lastShout.get(player.getUniqueId()), TimeUnit.SECONDS.toMillis(3));
    }

}
