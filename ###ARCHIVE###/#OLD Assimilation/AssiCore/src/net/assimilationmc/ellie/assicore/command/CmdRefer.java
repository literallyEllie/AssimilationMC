package net.assimilationmc.ellie.assicore.command;

import net.assimilationmc.ellie.assicore.api.AssiPlayer;
import net.assimilationmc.ellie.assicore.api.Referral;
import net.assimilationmc.ellie.assicore.manager.ModuleManager;
import net.assimilationmc.ellie.assicore.manager.ReferralManager;
import net.assimilationmc.ellie.assicore.util.CoinRewards;
import net.assimilationmc.ellie.assicore.util.ColorChart;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

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
public class CmdRefer extends AssiCommand {

    private ReferralManager referralManager;

    public CmdRefer() {
        super("refer", "refer", "Refer players to AssimilationMC for rewards!");
        setPlayerOnly(true);
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(referralManager == null ){
            referralManager = ModuleManager.getModuleManager().getReferralManaer();
        }

        AssiPlayer player = ModuleManager.getModuleManager().getPlayerManager().getPlayer(sender.getName());

        if (args.length == 0) {
            sendMessage(sender, ChatColor.BLUE + "Refer players to AssimilationMC for rewards!");
            sendMessage(sender, ChatColor.BLUE + "You can get rewards by getting your friends to join our server and type " + ColorChart.VARIABLE + "/refer " + player.getName());
            sendMessage(sender, ChatColor.GOLD + "For each referral you will get " + ColorChart.VARIABLE + CoinRewards.REFER + ChatColor.GOLD + " coins!");

            Referral refer = referralManager.getReferOf(player);
            if (refer.getReferredBy() != null) {
                sendMessage(sender, ChatColor.DARK_AQUA + "You were referred by " + refer.getReferredBy());
            } else sendMessage(sender, ChatColor.RED + "You haven't claimed to have been referred yet.");

            return;
        }

        if(args[0].equalsIgnoreCase(sender.getName())) {
            sendMessage(sender, ColorChart.WARN + "You cannot refer yourself!");
            return;
        }

        sendMessage(sender, ChatColor.BLUE + referralManager.refer(player, args[0]));
    }

}
