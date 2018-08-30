package com.assimilation.ellie.assihub.score;

import com.assimilation.ellie.assicore.manager.ModuleManager;
import com.assimilation.ellie.assicore.util.Util;
import com.assimilation.ellie.assihub.AssiHub;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;

/**
 * Created by Ellie on 17/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class AssiScoreboard {

    private ScoreboardManager scoreboardManager;

    private String scoreData = "default";

    private Scoreboard scoreboard;
    private Objective objective;

    private ArrayList<String> lines;

    private String[] teams;

    public AssiScoreboard(ScoreboardManager scoreboardManager, Player player){
        this.scoreboardManager = scoreboardManager;
        this.lines = new ArrayList<>();
        this.assign(player);
    }

    private void setup(Player player){

        ModuleManager.getModuleManager().getPermissionManager().getGroups().forEach((s, assiPermGroup) -> {
            System.out.println(s+" "+assiPermGroup.getPrefix().length());
            scoreboard.registerNewTeam(s).
                    setPrefix(Util.color(assiPermGroup.getPrefix().replace("%space%", " "))+ (assiPermGroup.getPrefix().length() == 1 ? null : ChatColor.RESET));
        });

        for (Player p : Bukkit.getOnlinePlayers()) {

            String rank = ModuleManager.getModuleManager().getPlayerManager().getGroupOf(player.getUniqueId()).getName().toLowerCase();
            String oRank = ModuleManager.getModuleManager().getPlayerManager().getGroupOf(p.getUniqueId()).getName().toLowerCase();

            scoreboard.getTeam(oRank).addPlayer(p);
            if(p == player) continue;

            p.getScoreboard().getTeam(rank).addPlayer(player);

        }

    }

    private ScoreboardData getData(){
        ScoreboardData data = scoreboardManager.getData(scoreData, false);
        if(data != null)
            return data;

        scoreData = "default";
        return scoreboardManager.getData(scoreData, false);
    }

    public void build(ScoreboardManager scoreboardManager, Player player){
        ScoreboardData data = getData();

        if(data == null)
            return;

        for (int i = 0; i < data.getLines(scoreboardManager, player).size(); i++) {

            String newLine = data.getLines(scoreboardManager, player).get(i);

            if (lines.size() > i) {

                String oldLine = lines.get(i);

                if (oldLine.equals(newLine))
                    continue;
            }

            Team team = scoreboard.getTeam(teams[i]);
            if(team == null){
                AssiHub.getPlugin(AssiHub.class).logE("Scoreboard team wasn't found for team "+teams[i]);
                return;
            }

            team.setPrefix(newLine.substring(0, Math.min(newLine.length(), 16)));
            team.setSuffix(ChatColor.getLastColors(newLine) + newLine.substring(Math.min(newLine.length(), 32)));

            objective.getScore(teams[i]).setScore(15-i);
        }

        if(lines.size() > data.getLines(scoreboardManager, player).size()){

            for (int i = data.getLines(scoreboardManager, player).size(); i < lines.size(); i++) {
                scoreboard.resetScores(teams[i]);
            }
            
        }

    }

    private void setTitle(String title){
        objective.setDisplayName(Util.color(title));
    }

    public void assign(Player player){
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        objective = scoreboard.registerNewObjective(player.getName(), "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        setTitle("   &2Assi&amilation   ");
        setup(player);

        teams = new String[16];

        for (int i = 0; i < 16; i++) {

            String name = ChatColor.COLOR_CHAR +""+("1234567890abcdefghijklmnopqrstuvwxyz".toCharArray())[i] + ChatColor.RESET;

            teams[i] = name;

            Team team = scoreboard.registerNewTeam(name);
            team.addEntry(name);
        }

        player.setScoreboard(scoreboard);

    }




}
