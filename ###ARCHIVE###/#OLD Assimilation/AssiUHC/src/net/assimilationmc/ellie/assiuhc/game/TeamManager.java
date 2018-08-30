package net.assimilationmc.ellie.assiuhc.game;

import com.google.common.collect.Iterators;
import net.assimilationmc.ellie.assicore.api.ItemBuilder;
import net.assimilationmc.ellie.assicore.util.Util;
import net.assimilationmc.ellie.assiuhc.UHC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Ellie on 19/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class TeamManager {

    private final UHCGame game;
    private HashMap<String, UHCTeam> teams;
    private boolean closed;

    TeamManager(UHCGame game){
        this.game = game;
        this.teams = new HashMap<>();
        closed = false;
    }

    public HashMap<String, UHCTeam> getTeams() {
        return teams;
    }

    public UHCTeam getTeam(String name){
        return teams.get(name.toLowerCase());
    }

    public boolean passJoin(Player player){

        int freeTeams = game.getMap().isForTeams() ? Long.valueOf(teams.values().stream().filter(team ->
                team.getMembers().size() < game.getMap().getTeamedGameTypes().get(game.getMap().getSelectedTeamed()).getMaxTeamSize()).count()).intValue() : 100;

        if(closed || (game.getMap().isForTeams() && teams.size() == game.getMap().getTeamedGameTypes().get(game.getMap().getSelectedTeamed()).getMaxTeams() && freeTeams == 0)){
            kickGamePlayer(player, "No space");
            closed = true;
            return false;
        }

        return true;
    }

    public void kickGamePlayer(Player player, String reason){

    }

    public boolean isTeamFull(UHCTeam team){
        return !game.getMap().isForTeams() || team.getMembers().size() <= game.getMap().getTeamedGameTypes().get(game.getMap().getSelectedTeamed()).getMaxTeamSize();
    }

    public boolean allowJoins(){
        return !closed;
    }

    public boolean addTeam(String name, Player creator){
        if(getTeam(name) == null && (!game.getMap().isForTeams() || teams.size() <= game.getMap().getTeamedGameTypes().get(game.getMap().getSelectedTeamed()).getMaxTeams())) {

            //filter // TODO: 19/12/2016

            UHCTeam team = new UHCTeam(teams.size(), this, name, creator.getName());
            this.teams.put(name.toLowerCase(), team);

            if(getTakenTeamColors().contains(ItemBuilder.StackColor.WHITE)){
                team.setTeamColor(Iterators.get(getUntakenTeamColors().iterator(), 0));
            }
            return true;
        }
        return false;
    }

    public boolean quitTeam(String team, Player player){
        if(getTeam(team) != null){

            UHCTeam uhcTeam = getTeam(team);

            if(uhcTeam.getMembers().containsKey(player.getName())){
                if(game.getGameState() == GameState.WAITING || game.getGameState() == GameState.FINISHED){
                    uhcTeam.getMembers().remove(player.getName());

                    if(uhcTeam.getMembers().size() == 0){
                        teams.remove(team.toLowerCase());

                    }else{
                        game.getPlayers().forEach(s -> Util.mINFO_noP(Bukkit.getPlayer(s), UHC.prefix+player.getName()+" left the team."));
                    }
                    game.getScoreboard().unsetTeam(player, team);

                }
                return true;
            }
        }
        return false;
    }

    public boolean setTeamColor(String team, String player, ItemBuilder.StackColor teamColor){
        if(getTeam(team) != null) {

            if (isTeamLeader(team, player)) {
                UHCTeam uteam = getTeam(team);

                if(getTakenTeamColors().contains(teamColor)){
                    return false;
                }
                uteam.setTeamColor(teamColor);
                getTeams().put(team.toLowerCase(), uteam);
                return true;
            }
        }
        return false;
    }

    public List<ItemBuilder.StackColor> getUntakenTeamColors(){
        List<ItemBuilder.StackColor> colors = new ArrayList<>();
        ItemBuilder.StackColor[] chatColors = ItemBuilder.StackColor.values();
        List<ItemBuilder.StackColor> taken = getTakenTeamColors();

        for (ItemBuilder.StackColor color : chatColors) {
            if(!taken.contains(color))
                colors.add(color);
        }
        return colors;
    }

    public List<ItemBuilder.StackColor> getTakenTeamColors(){
        return teams.values().stream().map(UHCTeam::getTeamColor).collect(Collectors.toList());
    }

    public boolean isTeamLeader(String team, String player){
        return getTeam(team) != null && getTeam(team).getLeader().equals(player);
    }

    public void voteKick(String player, String starter, String reason){

    }

    public void balanceTeams() {
        UHCGame game = getGame();

        List<String> utenLag = new ArrayList<>();

        game.getPlayers().forEach(s -> {
            Player player = Bukkit.getPlayer(s);
            if (player != null && getTeam(player) == null) {
                utenLag.add(player.getName());
            }
        });

        List<Integer> sizes = new ArrayList<>();

        getTeams().forEach((s, team) -> sizes.add(team.getMembers().size()));
    }


    private UHCTeam getLowestTeam(){
        UHCTeam lowestTeam = null;
        for(UHCTeam uhcTeam: getTeams().values()){
            if(!isTeamFull(uhcTeam)){
                if(lowestTeam == null) lowestTeam = uhcTeam;
                if(lowestTeam.getMembers().size() > uhcTeam.getMembers().size()){
                    lowestTeam = uhcTeam;
                }
            }
        }
        return lowestTeam;
    }


    public boolean hasTeam(Player player){
        return getTeam(player) != null;
    }

    public UHCTeam getTeam(Player player){
        try {
            return teams.values().stream().filter(team -> team.getMembers().containsKey(player.getName())).limit(1).collect(Collectors.toList()).get(0);
        }catch(Exception e){
            return null;
        }
    }

    public UHCGame getGame() {
        return game;
    }
}
