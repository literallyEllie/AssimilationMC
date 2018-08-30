package net.assimilationmc.ellie.assicore.api.economy;

import java.util.UUID;

/**
 * Created by Ellie on 18/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public interface Economy {

    String getCurrency();

    void setCurrency(String currency);

    int getBalance(UUID player);

    void setBalance(UUID player, int balance);

    void giveMoney(UUID player, int money);

    void deductMoney(UUID player, int money);

    boolean canAfford(UUID player, int amount);

}
