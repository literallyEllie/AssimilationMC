package net.assimilationmc.ellie.assiuhc.game.score;

import net.assimilationmc.ellie.assicore.AssiPlugin;
import net.assimilationmc.ellie.assicore.util.Util;
import net.assimilationmc.ellie.assiuhc.game.UHCGame;
import net.assimilationmc.ellie.assiuhc.games.util.UHCTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;

/**
 * Created by Ellie on 16.7.17 for votifier.
 * Affiliated with www.minevelop.com
 */
public class UAssiScoreboard {

    private UHCScoreboardManager scoreboardManager;

    private final String scoreData;
    private final boolean forTeam;
    private final UHCGame game;

    private Scoreboard scoreboard;
    private Objective objective;

    private ArrayList<String> lines;

    private String[] teams;

    public UAssiScoreboard(UHCScoreboardManager scoreboardManager, Player player, boolean forTeam, UHCGame game) {
        this.scoreboardManager = scoreboardManager;
        this.lines = new ArrayList<>();
        this.forTeam = forTeam;
        this.game = game;
        this.scoreData = String.valueOf(game.getId());
        this.assign(player);
    }

    public void setup(Player player) {

        if (forTeam) {

            for (UHCTeam uhcTeam : game.getTeamManager().getTeams().values()) {
                try {
                    if (scoreboard.getTeam(uhcTeam.getName()) != null) {
                        scoreboard.getTeam(uhcTeam.getName()).setPrefix(uhcTeam.getTeamColor().getChatColor() + "" +
                                ChatColor.BOLD + uhcTeam.getName() + ChatColor.RESET + " ");
                    } else
                        scoreboard.registerNewTeam(uhcTeam.getName()).setPrefix(uhcTeam.getTeamColor().getChatColor() + "" +
                                ChatColor.BOLD + uhcTeam.getName() + ChatColor.RESET + " ");
                } catch (IllegalArgumentException iProbablyShouldntDoThis) {
                    iProbablyShouldntDoThis.printStackTrace();
                }
            }

            for (String s : game.getPlayers()) {

                Player oPlayer = Bukkit.getPlayer(s);
                UHCTeam team = game.getTeamManager().getTeam(player);
                UHCTeam oTeam = game.getTeamManager().getTeam(oPlayer);

                if (oTeam != null) {

                    scoreboard.getTeam(oTeam.getName()).addPlayer(oPlayer);
                }
                if (oPlayer == player || oPlayer.getScoreboard() == null)
                    continue;
                if (team != null) {
                    if (oPlayer.getScoreboard().getTeam(team.getName()) != null) {
                        oPlayer.getScoreboard().getTeam(team.getName()).addPlayer(player);
                    } else {
                        oPlayer.getScoreboard().registerNewTeam(team.getName()).setPrefix(team.getTeamColor().getChatColor() + "" +
                                ChatColor.BOLD + team.getName() + ChatColor.RESET + " ");
                        oPlayer.getScoreboard().getTeam(team.getName()).addPlayer(player);
                    }

                }

            }
        }

    }


    /*
    public void updateTeams(Player player) {
        Validate.isTrue(forTeam, "UAssiScoreboard#updateTeams cannot be called for singles!");
        for (UHCTeam uhcTeam : game.getTeamManager().getTeams().values()) {
                scoreboard.registerNewTeam(uhcTeam.getName()).setPrefix(uhcTeam.getTeamColor().getChatColor() + "" + ChatColor.BOLD + uhcTeam.getName() + ChatColor.RESET + " ");
        }

        for (String s : game.getPlayers()) {
            Player oPlayer = Bukkit.getPlayer(s);
            if(oPlayer == null) continue;
            UHCTeam team = game.getTeamManager().getTeam(player);
            UHCTeam oTeam = game.getTeamManager().getTeam(oPlayer);

            if(oTeam != null) {
                scoreboard.getTeam(oTeam.getName()).addPlayer(oPlayer);
            }
            if(oPlayer == player) continue;

            if(team != null) {
                oPlayer.getScoreboard().getTeam(team.getName()).addPlayer(player);
            }

        }
    }
    */

    public void refresh(Player player) {
        build(scoreboardManager, player);
        setup(player);
    }

/*
    public void setTeam(Player player, String team) {

        for (Team t : scoreboard.getTeams())
            t.removePlayer(player);

        if (team == null)
            team = "";

        String teamName = parseTeamName(team);
        scoreboard.getTeam(teamName).addPlayer(player);
        player.setScoreboard(scoreboard);
    }

    public void unsetTeam(Player player, String team) {
        for (Team t : scoreboard.getTeams())
            t.removePlayer(player);

        String teamName = parseTeamName(team);
        scoreboard.getTeam(teamName).removePlayer(player);
        player.setScoreboard(scoreboard);
    }
    */

    private UScoreboardData getData() {
        UScoreboardData data = scoreboardManager.getData(scoreData, false);
        if (data != null)
            return data;
        throw new IllegalArgumentException(scoreData + " cannot be null!");
    }

    public void build(UHCScoreboardManager scoreboardManager, Player player) {
        UScoreboardData data = getData();

        if (data == null)
            return;

        for (int i = 0; i < data.getLines(scoreboardManager, player, game).size(); i++) {

            String newLine = data.getLines(scoreboardManager, player, game).get(i);

            if (lines.size() > i) {

                String oldLine = lines.get(i);

                if (oldLine.equals(newLine))
                    continue;
            }

            Team team = scoreboard.getTeam(teams[i]);
            if (team == null) {
                AssiPlugin.getPlugin(AssiPlugin.class).logE("Scoreboard team wasn't found for team " + teams[i]);
                return;
            }

            team.setPrefix(newLine.substring(0, Math.min(newLine.length(), 16)));
            team.setSuffix(ChatColor.getLastColors(newLine) + newLine.substring(Math.min(newLine.length(), 32)));

            objective.getScore(teams[i]).setScore(15 - i);
        }

        if (lines.size() > data.getLines(scoreboardManager, player, game).size()) {

            for (int i = data.getLines(scoreboardManager, player, game).size(); i < lines.size(); i++) {
                scoreboard.resetScores(teams[i]);
            }

        }

    }

    private void setTitle(String title) {
        objective.setDisplayName(Util.color(title));
    }

    private void assign(Player player) {
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        objective = scoreboard.registerNewObjective(player.getName(), "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        setTitle("     &aAssi&c&lUHC     ");
        setup(player);

        teams = new String[16];

        for (int i = 0; i < 16; i++) {
            String name = ChatColor.COLOR_CHAR + "" + ("1234567890abcdefghijklmnopqrstuvwxyz".toCharArray())[i] + ChatColor.RESET;
            teams[i] = name;

            Team team = scoreboard.registerNewTeam(name);
            team.addEntry(name);
        }

        player.setScoreboard(scoreboard);

    }

    public String getScoreData() {
        return scoreData;
    }

    private String parseTeamName(String name) {
        return name.substring(0, Math.min(16, name.length()));
    }

}
