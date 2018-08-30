package net.assimilationmc.ellie.assicore.score;

import net.assimilationmc.ellie.assicore.AssiPlugin;
import net.assimilationmc.ellie.assicore.api.AssiCore;
import net.assimilationmc.ellie.assicore.api.AssiPlayer;
import net.assimilationmc.ellie.assicore.manager.ModuleManager;
import net.assimilationmc.ellie.assicore.util.ColorChart;
import net.assimilationmc.ellie.assicore.util.Domain;
import net.assimilationmc.ellie.assicore.util.UtilString;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by Ellie on 30/09/2017 for AssimilationMC.
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
public class LobbyScoreboard extends ScoreboardPolicy {

    public LobbyScoreboard(AssiPlugin plugin) {
        super(plugin);
    }

    @Override
    public void getSideBar(Player player, List<String> lines) {
        final AssiPlayer assiPlayer = ModuleManager.getModuleManager().getPlayerManager().getPlayer(player.getUniqueId());


        lines.add(UtilString.getWhitespace(0));
        lines.add(ColorChart.R + ChatColor.BOLD + "Rank " + ChatColor.RESET + ColorChart.VARIABLE + assiPlayer.getRank().getPrefix());
        lines.add(UtilString.getWhitespace(1));
        lines.add(ColorChart.R + ChatColor.BOLD + "Coins " + ChatColor.RESET + ColorChart.VARIABLE + assiPlayer.getCoins());
        lines.add(UtilString.getWhitespace(2));
        lines.add(ColorChart.R + ChatColor.BOLD + "Players " + ChatColor.RESET + ColorChart.VARIABLE + Bukkit.getOnlinePlayers().size());
        lines.add(UtilString.getWhitespace(3));
        lines.add(ChatColor.BLUE.toString() + ChatColor.BOLD + "Discord " + ChatColor.RESET + ColorChart.VARIABLE + Domain.DISCORD);
        lines.add(UtilString.getWhitespace(4));
        lines.add(ColorChart.VARIABLE + Domain.WEBSITE);

    }

    @Override
    public String getPrefix(Player perspective, Player subject) {
        return subject.getDisplayName().split(" ")[0] + " ";
    }

    @Override
    public String getSuffix(Player perspective, Player subject) {
        return null;
    }

    @Override
    public String getUnderName(Player player) {
        return null;
    }

    @Override
    public int getUndernameScore(Player perspective, Player subject) {
        return 0;
    }

    @Override
    public String getTablist(Player player) {
        return null;
    }

    @Override
    public String getTablistScore(Player perspective, Player subject) {
        return null;
    }

}
