package net.assimilationmc.ellie.assicore.command.permission;

import net.assimilationmc.ellie.assicore.api.AssiPlayer;
import net.assimilationmc.ellie.assicore.command.AssiCommand;
import net.assimilationmc.ellie.assicore.event.ScoreboardUpdateEvent;
import net.assimilationmc.ellie.assicore.manager.ModuleManager;
import net.assimilationmc.ellie.assicore.permission.Rank;
import net.assimilationmc.ellie.assicore.util.ColorChart;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ellie on 27/08/2017 for AssimilationMC.
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
public class CmdSetRank extends AssiCommand {

    public CmdSetRank() {
        super("setRank", Rank.MANAGER, "setrank <rank> <user>", "Set a users rank");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if(args.length != 2) {
            sendMessage(sender, correctUsage());
            return;
        }

        Rank rank = Rank.fromString(args[0]);

        if(rank == null) {
            sendPMessage(sender, "Rank " + ColorChart.VARIABLE + args[0] + ColorChart.R + " does not exist.");
            return;
        }

        AssiPlayer player = ModuleManager.getModuleManager().getPlayerManager().getPlayer(args[1]);
        player.setRank(rank.getName());

        player.getBase().setDisplayName((rank.isDefault() ? "" : rank.getPrefix() + " " + ChatColor.RESET) + ChatColor.GRAY + player.getName());
        Bukkit.getPluginManager().callEvent(new ScoreboardUpdateEvent(player.getName(), ScoreboardUpdateEvent.UpdateElement.RANK));

        sendMessage(player, ChatColor.DARK_GRAY + ChatColor.BOLD.toString() + "» "+ ColorChart.R + "Rank updated to " + rank.getPrefix() + ColorChart.R + "!");
        sendPMessage(sender, "Set rank of "+player.getName() + " to "+rank.getName()+".");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        final List<String> completions = new ArrayList<>();

        if(args.length == 1) {

            Rank[] ranks = Rank.values();
            for (Rank rank : ranks) {
                if(rank.getName().toLowerCase().startsWith(args[0].toLowerCase()))
                    completions.add(rank.getName());
            }
        }

        if(args.length == 2) {

            for (Player player : Bukkit.getOnlinePlayers()) {
                if(player.getName().toLowerCase().startsWith(args[1].toLowerCase()))
                    completions.add(player.getName());
            }

        }

        return completions;
    }
}
