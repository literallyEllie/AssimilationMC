package net.assimilationmc.assicore.scoreboard;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.UtilPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;

public class AssiScore {

    private final ScoreboardManager scoreboardManager;
    private final AssiPlayer player;

    private Scoreboard scoreboard;
    private Objective objSidebar;
    private List<String> sidebar;
    private Objective objUndername;
    private String currentTab;

    public AssiScore(ScoreboardManager scoreboardManager, AssiPlayer player) {
        this.scoreboardManager = scoreboardManager;
        this.player = player;
        this.scoreboard = scoreboardManager.getPlugin().getServer().getScoreboardManager().getNewScoreboard();
        this.sidebar = Lists.newArrayList();
        player.getBase().setScoreboard(scoreboard);
        update();
    }

    public static String getTeamName(AssiPlayer player) {
        Rank rank = player.getRank();

        String sort = String.valueOf(Rank.values().length - rank.ordinal());
        if (sort.length() < String.valueOf(Rank.values().length).length())
            sort = "0" + sort;

        String displayName = sort + "-" + player.getName();
        displayName = displayName.substring(0, Math.min(displayName.length(), 16));
        return displayName;
    }

    public void unregister() {
        if (objSidebar != null) {
            objSidebar.unregister();
            objSidebar = null;
        }

        if (objUndername != null) {
            objUndername.unregister();
            objUndername = null;
        }

        sidebar.clear();
        currentTab = null;

    }

    public void update() {
        updateTab();
        updateSidebar();
        updateUnderName();
        updateTeams();
    }

    public void updateTab() {
        String name = scoreboardManager.getScoreboardPolicy().getPlayerTabName(player);
        if (name != null && !name.isEmpty()) {
            this.currentTab = name;
            player.getBase().setPlayerListName(name);
        }

        if (name == null && currentTab != null) {
            this.currentTab = null;
            player.getBase().setPlayerListName(player.getName());
        }
    }

    public void updateSidebar() {
        final List<String> lines = scoreboardManager.getScoreboardPolicy().getSideBar(player);
        if (lines == null || lines.isEmpty()) {
            if (objSidebar != null) {
                objSidebar.unregister();
                objSidebar = null;
            }
        } else
            setSidebar(lines);
    }

    public void setSidebar(List<String> lines) {
        if (objSidebar == null) {
            objSidebar = scoreboard.registerNewObjective("title", "dummy");
            objSidebar.setDisplayName(scoreboardManager.getSidebarTitle());
            objSidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        for (int i = 0; i < lines.size(); i++) {
            lines.set(i, lines.get(i).substring(0, Math.min(lines.get(i).length(), 40)));
        }

        final List<String> newLines = Lists.newArrayList();
        if (!lines.isEmpty()) {
            int score = 15;

            for (String line : lines) {
                if (score < 1)
                    break;

                if (!newLines.contains(line)) {
                    newLines.add(line);
                    objSidebar.getScore(line).setScore(score);
                    --score;
                }
            }
        }

        this.sidebar.stream().filter(s -> !newLines.contains(s)).forEach(s -> scoreboard.resetScores(s));
        this.sidebar = newLines;
    }

    public void updateUnderName() {
        if (scoreboardManager.getScoreboardPolicy().getUnderName(player) == null || scoreboardManager.getScoreboardPolicy().getUnderName(player).isEmpty()) {
            if (objUndername != null) {
                objUndername.unregister();
                objUndername = null;
            }
            return;
        }

        if (objUndername == null) {
            objUndername = scoreboard.registerNewObjective("undername", "dummy");
            objUndername.setDisplaySlot(DisplaySlot.BELOW_NAME);
        }

        if (objUndername.getDisplayName().equals(scoreboardManager.getScoreboardPolicy().getUnderName(player)))
            objUndername.setDisplayName(scoreboardManager.getScoreboardPolicy().getUnderName(player));

    }

    public void updateTeams() {
        for (Team team : scoreboard.getTeams()) {
            Player player = UtilPlayer.get(team.getName().split("-")[1]);
            if (player == null) {
                team.unregister();
                continue;
            }
            AssiPlayer player2 = scoreboardManager.getPlugin().getPlayerManager().getPlayer(player);
            String prefix = scoreboardManager.getScoreboardPolicy().getPlayerTagPrefix(this.player, player2);

            if (prefix != null && !team.getPrefix().equals(prefix))
                team.setPrefix(scoreboardManager.trimPrefix(prefix));
            else if (prefix == null && team.getPrefix() != null)
                team.setPrefix("");

            String suffix = scoreboardManager.getScoreboardPolicy().getPlayerTagSuffix(this.player, player2);
            if (suffix != null && !team.getSuffix().equals(suffix))
                team.setSuffix(scoreboardManager.trimPrefix(suffix));
            else if (suffix == null && team.getSuffix() != null)
                team.setSuffix("");

        }

        for (AssiPlayer assiPlayer : scoreboardManager.getPlugin().getPlayerManager().getOnlinePlayers().values()) {
            if (assiPlayer == this.player) continue;
            String teamName = getTeamName(assiPlayer);

            if (scoreboard.getTeam(teamName) == null) {
                Team team = scoreboard.registerNewTeam(teamName);
                String prefix = scoreboardManager.getScoreboardPolicy().getPlayerTagPrefix(this.player, assiPlayer);

                if (prefix != null) {
                    team.setPrefix(scoreboardManager.trimPrefix(prefix));
                }

                String suffix = scoreboardManager.getScoreboardPolicy().getPlayerTagSuffix(this.player, assiPlayer);
                if (suffix != null) {
                    team.setSuffix(scoreboardManager.trimPrefix(suffix));
                }

                team.addEntry(assiPlayer.getName());
            }

        }

    }

    public void setTitle(String title) {
        if (objSidebar != null)
            objSidebar.setDisplayName(title);
    }

    public AssiPlayer getPlayer() {
        return player;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

}
