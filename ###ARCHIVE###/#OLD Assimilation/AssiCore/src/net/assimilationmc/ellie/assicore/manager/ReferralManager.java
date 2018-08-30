package net.assimilationmc.ellie.assicore.manager;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.assimilationmc.ellie.assicore.api.AssiPlayer;
import net.assimilationmc.ellie.assicore.api.Referral;
import net.assimilationmc.ellie.assicore.util.ColorChart;
import net.assimilationmc.ellie.assicore.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.sql2o.Connection;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ellie on 29/08/2017 for AssimilationMC.
 * <p>
 * Copyright 2017 Ellie
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class ReferralManager implements IManager, Listener {

    private SQLManager sqlManager;
    private LoadingCache<UUID, Referral> referralCache;

    ReferralManager() {
        sqlManager = ModuleManager.getModuleManager().getSQLManager();
    }

    @Override
    public boolean load() {
        referralCache = CacheBuilder.newBuilder()
                .maximumSize(100)
                .expireAfterAccess(5, TimeUnit.MINUTES)
                .build(new CacheLoader<UUID, Referral>() {

                    @Override
                    public Referral load(final UUID uuid) throws Exception {

                        try (Connection connection = sqlManager.getSql2o().open()) {
                            List<Referral> referrals = connection.createQuery("SELECT * FROM `referrals` WHERE uuid = :u;")
                                    .addParameter("u", uuid.toString())
                                    .executeAndFetch(Referral.class);

                            if (!referrals.isEmpty()) {
                                connection.close();
                                return referrals.get(0);
                            }
                            connection.close();
                        }

                        return new Referral();
                    }

                });

        try (Connection connection = sqlManager.getSql2o().open()) {
            connection.createQuery("CREATE TABLE IF NOT EXISTS `referrals` (" +
                    "`id` INT(100) NOT NULL AUTO_INCREMENT, " +
                    "`uuid` VARCHAR(100) NOT NULL PRIMARY KEY UNIQUE, " +
                    "`referred_by` VARCHAR(100) NULL, " +
                    "INDEX(id)) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;").executeUpdate().close();
        }

        return true;
    }

    @Override
    public boolean unload() {

        referralCache.invalidateAll();
        return true;
    }

    @Override
    public String getModuleID() {
        return "referral";
    }

    public String refer(AssiPlayer player, String referredBy) {
        try {
            if (getReferOf(player).getReferredBy() != null)
                return "You have already been refereed by " + referralCache.get(player.getUuid()).getReferredBy();

            AssiPlayer referrer = ModuleManager.getModuleManager().getPlayerManager().getPlayer(referredBy);
            if (referrer == null) return "Player not found!";

            try (Connection connection = sqlManager.getSql2o().open()) {
                connection.createQuery("INSERT INTO `referrals` (uuid, referred_by) VALUES (:u, :r);")
                        .addParameter("u", player.getUuid().toString())
                        .addParameter("r", referredBy)
                        .executeUpdate().close();
            }

            reward(referrer, player.getName());

        } catch (ExecutionException e) {
            Util.handleException("referral args{" + player.getName() + "-to-" + referredBy, e);
            return ChatColor.RED + "An error occured whilst doing this. Contact staff if this persists.";
        }

        return ChatColor.GREEN + "Success. " + ChatColor.BLUE + "Welcome to AssimilationMC!";
    }

    public Referral getReferOf(AssiPlayer player) {
        try {
            return referralCache.get(player.getUuid());
        } catch (ExecutionException e) {
            Util.handleException("getReferOf " + player.getName(), e);
        }
        return new Referral();
    }

    private void reward(AssiPlayer referrer, String who) {
        ModuleManager.getModuleManager().getEconomyManager().getEconomy().giveMoney(referrer.getUuid(), 500);
        if (referrer.isOnline()) {
            referrer.sendMessage(ChatColor.GOLD + "Thank you for referring " + ColorChart.VARIABLE + who + ChatColor.GOLD + " to AssimilationMC!");
        }
    }

}
