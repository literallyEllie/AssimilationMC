package net.assimilationmc.assiuhc.reward;

import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.D;
import net.assimilationmc.assicore.util.UtilPlayer;
import net.assimilationmc.assiuhc.player.UHCPlayer;
import net.assimilationmc.gameapi.game.AssiGame;
import net.assimilationmc.gameapi.module.GameModule;
import net.assimilationmc.gameapi.module.ModuleActivePolicy;
import net.assimilationmc.gameapi.util.GC;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class XPManager extends GameModule {

    public static final int DEFAULT_LEVEL = 0;
    public static final int DEFAULT_XP = 0;

    private static final String REWARD_NAME = "xp";

    public XPManager(AssiGame game) {
        super(game, "XP Manager", ModuleActivePolicy.PERMANENT);
    }

    @Override
    public void start() {
    }

    @Override
    public void end() {
    }

    public void giveXP(UHCPlayer player, int baseAmount) {
        double booster = getAssiGame().getPlugin().getRewardManager().getCustomBoosters().getOrDefault(REWARD_NAME, 0d);

        if (booster == 0d && getAssiGame().getPlugin().getBoosterManager().getActiveBooster() != null) {
            baseAmount = getAssiGame().getPlugin().getBoosterManager().getActiveBooster().getBooster().processUHCXp(baseAmount);
        }

        int toGive = (int) Math.round(booster == 0d ? baseAmount : baseAmount * booster);

        final Player bPlayer = UtilPlayer.get(player.getUuid());
        if (bPlayer != null) {
            bPlayer.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "+" + toGive + " XP " +
                    (booster == 0d ? ChatColor.GRAY + "You could have earned more if there was a booster active!" : "(Multiplier: x" + booster + ")"));
        }
        player.addXP(toGive);
        checkLevelUp(player, player.getLevel());
    }

    public void takeXP(UHCPlayer player, int amount) {
        player.setXp(Math.max(player.getXp() - amount, 0));
    }

    public int getXpToNextLevel(int level, int xp) {
        // level = 4, xp = 100. 4 * 50 - 100
        return (level == 0 ? 20 : level * 30) - xp;
    }

    private void checkLevelUp(UHCPlayer player, int initLevel) {
        // D.d("checking level up");
        final int level = player.getLevel();
        final int xp = player.getXp();

        int requireXp = getXpToNextLevel(level, xp);
        // D.d("required xp " + requireXp);
        if (requireXp <= 0) {
            D.d("less than or equal to 0");
            player.setLevel(level + 1);
            player.setXp(Math.abs(requireXp));

           // D.d("get player");
            final Player p = UtilPlayer.get(player.getUuid());
            if (p == null) return;
            // D.d("player offline");

            final double xpToNextLevel = getXpToNextLevel(player.getLevel(), player.getXp());
            if (xpToNextLevel <= 0) {
                checkLevelUp(player, initLevel);
                // exhaust.
                return;
            }

            // D.d("message send");

            p.sendMessage(C.C);
            p.sendMessage(C.SS + GC.II + "You have leveled up! " + GC.C + "Now level " + GC.V + player.getLevel() + GC.C
                    + (player.getLevel() - initLevel > 1 ? " (+" + GC.V + (player.getLevel() - initLevel) + GC.C + " levels)" : "") + "!");
            p.sendMessage(GC.C + "Required XP to level up: " + GC.V + xpToNextLevel);
            p.sendMessage(C.C);

            p.playSound(p.getLocation(), Sound.LEVEL_UP, 3F, 5);
        }

    }

}
