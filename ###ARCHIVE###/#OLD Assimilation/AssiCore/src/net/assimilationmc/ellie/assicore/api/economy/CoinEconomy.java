package net.assimilationmc.ellie.assicore.api.economy;

import net.assimilationmc.ellie.assicore.api.AssiPlayer;
import net.assimilationmc.ellie.assicore.event.ScoreboardUpdateEvent;
import net.assimilationmc.ellie.assicore.manager.PlayerManager;
import org.bukkit.Bukkit;

import java.util.UUID;

/**
 * Created by Ellie on 22/01/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public final class CoinEconomy implements Economy {

    private PlayerManager playerManager;
    private String currency;

    public CoinEconomy(){
        this(null);
    }

    public CoinEconomy(PlayerManager playerManager) {
        this.playerManager = playerManager;
        this.currency = "coins";
    }

    public void setPlayerManager(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public String getCurrency() {
        return currency;
    }

    @Override
    public int getBalance(UUID player) {
        return playerManager.getPlayer(player).getCoins();
    }

    @Override
    public void setBalance(UUID player, int balance) {
        AssiPlayer player1 = playerManager.getPlayer(player);
        player1.setCoins(balance);
        playerManager.pushPlayer(player1);
        Bukkit.getPluginManager().callEvent(new ScoreboardUpdateEvent(Bukkit.getPlayer(player).getName(), ScoreboardUpdateEvent.UpdateElement.COINS));
        player1.sendMessage("Your balance has been updated to &6"+balance+"&7 "+currency);
    }

    @Override
    public void giveMoney(UUID player, int money) {
        AssiPlayer player1 = playerManager.getPlayer(player);
        player1.setCoins(player1.getCoins()+money);
        playerManager.pushPlayer(player1);
        Bukkit.getPluginManager().callEvent(new ScoreboardUpdateEvent(Bukkit.getPlayer(player).getName(), ScoreboardUpdateEvent.UpdateElement.COINS));
        player1.sendMessage("&6+&f"+money+" &7"+currency);
    }

    @Override
    public void deductMoney(UUID player, int money) {
        AssiPlayer player1 = playerManager.getPlayer(player);
        if(canAfford(player, money)) {
            player1.setCoins(player1.getCoins() - money);
            playerManager.pushPlayer(player1);
            Bukkit.getPluginManager().callEvent(new ScoreboardUpdateEvent(Bukkit.getPlayer(player).getName(), ScoreboardUpdateEvent.UpdateElement.COINS));
            player1.sendMessage("&c-&f" + money +" &7"+ currency);
        }
        System.out.println("Cannot deduct money from "+player+". Insufficient funds.");

    }

    @Override
    public boolean canAfford(UUID player, int amount) {
        AssiPlayer player1 = playerManager.getPlayer(player);
        int current = player1.getCoins();
        return (current - amount) > 0;
    }

}
