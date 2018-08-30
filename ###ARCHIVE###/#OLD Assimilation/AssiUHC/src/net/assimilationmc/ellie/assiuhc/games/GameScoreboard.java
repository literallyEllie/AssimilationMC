package net.assimilationmc.ellie.assiuhc.games;

import net.assimilationmc.ellie.assicore.manager.ModuleManager;
import net.assimilationmc.ellie.assicore.util.Util;
import net.assimilationmc.ellie.assiuhc.game.GameState;
import net.assimilationmc.ellie.assiuhc.game.UHCGame;
import net.assimilationmc.ellie.assiuhc.game.UHCTeam;
import net.assimilationmc.ellie.assiuhc.games.score.UHCScore;
import net.assimilationmc.ellie.assiuhc.games.score.UHCScoreScores;
import net.assimilationmc.ellie.assiuhc.games.score.UHCScoreText;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * Created by Ellie on 22/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class GameScoreboard {

    private UHCGame game;

    private Scoreboard scoreboard;
    private Objective objective;

    private List<UHCScore> assets;
    private String[] current;

    private String title;

    public GameScoreboard(UHCGame game) {

        this.game = game;
        this.assets = new ArrayList<>();
        this.current = new String[14];
        this.title = "&2Assi&c&lUHC";

        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        objective = scoreboard.registerNewObjective("uhc" + new Random(22).nextInt(999999999), "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(Util.color(title));
        objective.getScore(Bukkit.getOfflinePlayer(" "));

    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public Objective getObjective() {
        return objective;
    }

    private String parseTeamName(String name) {
        return name.substring(0, Math.min(16, name.length()));
    }

    public void createTeams() {

        scoreboard.registerNewTeam("spec").setPrefix(Util.color("&7"));
        for (UHCTeam uhcTeam : game.getTeamManager().getTeams().values()) {
            scoreboard.registerNewTeam(parseTeamName(uhcTeam.getName())).setPrefix(uhcTeam.getTeamColor().getChatColor() + "" + ChatColor.BOLD + uhcTeam.getName() + ChatColor.RESET + " ");
        }
        updateTeams();

    }

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

    public void reset(String line) {
        scoreboard.resetScores(line);
    }

    public String clean(String line) {
        if (line.length() > 16)
            line = line.substring(0, 16);
        return line;
    }

    public void write(String line) {
        line = clean(line);
        assets.add(new UHCScoreText(line));
    }

    public void writeOrdered(String key, String line, int value, boolean prependScore) {

        if (prependScore)
            line = value + " " + line;

        line = clean(line);

        for (UHCScore score : assets) {
            if (score instanceof UHCScoreScores) {

                UHCScoreScores s = (UHCScoreScores) score;

                if (s.isKey(key)) {
                    s.addScore(line, value);
                    return;
                }
            }

        }
        assets.add(new UHCScoreScores(key, line, value, true));

    }

    public void writeEmpty() {
        assets.add(new UHCScoreText(" "));
    }

    public void build() {

        ArrayList<String> newLines = new ArrayList<>();

        for (UHCScore elem : assets) {
            for (String line : elem.getLines()) {

                while (true) {
                    boolean matched = false;

                    for (String otherLine : newLines) {
                        if (line.equals(otherLine)) {
                            line += ChatColor.RESET;
                            matched = true;
                        }
                    }

                    if (!matched)
                        break;
                }

                newLines.add(line);
            }
        }
        HashSet<Integer> toAdd = new HashSet<>();
        HashSet<Integer> toDelete = new HashSet<>();

        for (int i = 0; i < 15; i++) {
            if (i >= newLines.size()) {
                if (current[i] != null) {
                    toDelete.add(i);
                }
                continue;
            }

            if (current[i] == null || !current[i].equals(newLines.get(i))) {
                toDelete.add(i);
                toAdd.add(i);
            }
        }

        for (int i : toDelete) {

            if (current[i] != null) {
                reset(current[i]);
                current[i] = null;
            }
        }

        for (int i : toAdd) {
            String newLine = newLines.get(i);
            objective.getScore(newLine).setScore(15 - i);
            current[i] = newLine;
        }

    }

    public void reset() {
        assets.clear();
    }

    public void updateTeams() {
        if (game.getGameState() != GameState.WAITING) {
            return;
        }
        for (UHCTeam uhcTeam : game.getTeamManager().getTeams().values()) {
            if (scoreboard.getTeam(parseTeamName(uhcTeam.getName())) != null) {
                scoreboard.getTeam(parseTeamName(uhcTeam.getName())).setPrefix(uhcTeam.getTeamColor().getChatColor() + "" + ChatColor.BOLD + uhcTeam.getName() + ChatColor.RESET + " ");
            } else {
                scoreboard.registerNewTeam(parseTeamName(uhcTeam.getName())).setPrefix(uhcTeam.getTeamColor().getChatColor() + "" + ChatColor.BOLD + uhcTeam.getName() + ChatColor.RESET + " ");
            }
            uhcTeam.getMembers().keySet().forEach(s -> {
                if (Bukkit.getPlayer(s) != null) {
                    setTeam(Bukkit.getPlayer(s), uhcTeam.getName());
                    ModuleManager.getModuleManager().getScoreboardManager().getPlayerScores().remove(s);
                    Bukkit.getPlayer(s).setScoreboard(scoreboard);
                }
            });
        }

        write("a");

    }

}
