package net.assimilationmc.assicore.reward;

import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.rank.Rank;

public enum RewardType {

    DAILY,
    MONTHLY,
    DONATE_MONTHLY,;

    public void give(AssiPlayer player) {

        switch (this) {
            case DAILY:
                player.addBucks(10);
                player.addUltraCoins(1);
                break;
            case MONTHLY:

                break;
            case DONATE_MONTHLY:

                if (player.getRank() == Rank.DEMONIC) {
                    // Coins
                    player.addBucks(150);
                    player.addUltraCoins(25);

                    // Boosters
                    player.addBooster("15_DOUBLE_BUCKS");
                    player.addBooster("45_DOUBLE_XP");
                }

                if (player.getRank() == Rank.INFERNAL) {
                    // Coins
                    player.addBucks(200);
                    player.addUltraCoins(50);

                    // Boosters
                    player.addBooster("15_DOUBLE_BUCKS");
                    player.addBooster("30_DOUBLE_BUCKS");
                    player.addBooster("45_DOUBLE_XP");
                    player.addBooster("15_DOUBLE_UC");

                }

                break;

        }

    }

}
