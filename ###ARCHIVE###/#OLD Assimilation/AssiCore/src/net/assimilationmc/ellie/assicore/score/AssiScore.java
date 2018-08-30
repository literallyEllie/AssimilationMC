package net.assimilationmc.ellie.assicore.score;

import com.google.common.collect.Lists;
import net.assimilationmc.ellie.assicore.AssiPlugin;
import net.assimilationmc.ellie.assicore.api.AssiPlayer;
import net.assimilationmc.ellie.assicore.manager.ModuleManager;
import net.assimilationmc.ellie.assicore.permission.Rank;
import net.assimilationmc.ellie.assicore.util.PermissionLib;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
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
public class AssiScore {

    private BetterScoreboardManager manager;
    private Player player;
    private Scoreboard scoreboard;
    private Objective sideBarObjective;
    private Objective undernameObjective;
    private Objective tabObjective;
    private List<String> sidebar;

    public AssiScore(BetterScoreboardManager betterScoreboardManager, Player player) {
        this.manager = betterScoreboardManager;
        this.player = player;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.sidebar = Lists.newArrayList();
        this.player.setScoreboard(scoreboard);
    }

    public void update() {
        updateSidebar();
        updateTeams();
    }

    public void updateSidebar() {
        List<String> lines = Lists.newArrayList();
        this.manager.getScoreboardPolicy().getSideBar(player, lines);
        if(manager.getCustomlines() == null && lines.isEmpty()) {
            if(sideBarObjective != null) {
                sideBarObjective.unregister();
                sideBarObjective = null;
            }
        }
        else
            setSidebar((manager.getCustomlines() == null ? lines : manager.getCustomlines()));
    }

    public void updateTeams() {
        for (Team team : scoreboard.getTeams()) {


            Player player = Bukkit.getPlayer(team.getName().split("-")[1]);
            if (player == null) team.unregister();
            else {
                String prefix = this.manager.getScoreboardPolicy().getPrefix(this.player, player);
                if (prefix != null && !team.getPrefix().equals(prefix))
                    team.setPrefix(manager.trimPrefix(prefix));
                else if (prefix == null && team.getPrefix() != null)
                    team.setPrefix("");

                String suffix = manager.getScoreboardPolicy().getSuffix(this.player, player);
                if (suffix != null && !team.getSuffix().equals(suffix))
                    team.setSuffix(manager.trimPrefix(suffix));
                else if (suffix == null && team.getSuffix() != null)
                    team.setSuffix("");
            }
        }

        for (Player oPlayer : Bukkit.getOnlinePlayers()) {
            String team = getTeamName(oPlayer);
            if(scoreboard.getTeam(team) == null) {
                Team t = scoreboard.registerNewTeam(team);
                String prefix = manager.getScoreboardPolicy().getPrefix(this.player, oPlayer);
                String suffix = manager.getScoreboardPolicy().getSuffix(this.player, oPlayer);

                if(prefix != null) t.setPrefix(manager.trimPrefix(prefix));
                if(suffix != null) t.setSuffix(manager.trimPrefix(suffix));

                t.addEntry(player.getDisplayName());
            }

        }


    }

    public void setTitle(String title) {
        if(sideBarObjective != null) sideBarObjective.setDisplayName(title);
    }

    public void setSidebar(List<String> sidebar) {
        if (sideBarObjective == null) {
            sideBarObjective = scoreboard.registerNewObjective("title", "dummy");
            sideBarObjective.setDisplayName(manager.getTitle());
            sideBarObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        for (int i = 0; i < sidebar.size(); i++) {
            sidebar.set(i, manager.trimPrefix(sidebar.get(i)));
        }

        List<String> set = new ArrayList<>();
        if (sidebar.size() > 0) {
            int score = 15;

            for (String s : sidebar) {
                if (score < 1) break;


                if (!set.contains(s)) {
                    set.add(s);
                    sideBarObjective.getScore(s).setScore(score);
                    --score;

                }

            }
        }


        for (String s : sidebar) {
            if(!set.contains(s))
                scoreboard.resetScores(s);
        }


        this.sidebar = set;
    }

    public static String getTeamName(Player p) {
        AssiPlayer account = ModuleManager.getModuleManager().getPlayerManager().getPlayer(p.getUniqueId());
        Rank rank = account == null ? Rank.USER : account.getRank();

        String sort;
        for(sort = String.valueOf(Rank.values().length - rank.ordinal()); sort.length() < String.valueOf(Rank.values().length).length(); sort = "0" + sort) {
            ;
        }

        String displayName = sort + "-" + p.getName().toLowerCase();
        displayName = displayName.substring(0, Math.min(displayName.length(), 16));
        return displayName;
    }

    public Player getPlayer() {
        return player;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

}
