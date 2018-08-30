package net.assimilationmc.ellie.assiuhc.games.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Ellie on 19/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class VoteKick {

    private UHCTeam team;
    private String starter;
    private String player;

    private int favour;
    private int not;
    private Set<String> voted;

    public VoteKick(UHCTeam team, String starter, String player) {
        this.team = team;
        this.starter = starter;
        this.player = player;
        this.favour = 1;
        this.not = 1;
        this.voted = new HashSet<>();
        this.voted.add(starter);
        this.voted.add(player);
    }

    public void addVote(String voter, boolean favour){
        if(canVote(voter)){
            if(favour) this.favour = this.favour + 1;
            else not = not +1;
            voted.add(voter);
        }
    }

    public boolean canVote(String player){
        return !voted.contains(player) && team.getMembers().entrySet().stream().filter(a -> a.getKey().equals(player) && !a.getValue()).limit(1).count() == 1L;
    }

    public boolean isPassed(){
        int teams = Long.valueOf(team.getMembers().values().stream().filter(aBoolean -> !aBoolean).count()).intValue();

        if(favour + not == teams){
            end("tie");
            return false;
        }

        if(teams - not < favour){
            end("kick");
            return true;
        }
        return false;
    }

    private void end(String result){

        team.getMembers().keySet().forEach(s -> {
            Player player = Bukkit.getPlayer(s);
            if(player != null){
                //// TODO: 19/12/2016 send @result
            }

        });

        this.voted.clear();
        this.favour = -1;
        this.not = -1;
    }

    public int getInFavour() {
        return favour;
    }

    public int getNot() {
        return not;
    }

    public String getStarter() {
        return starter;
    }

    public String getPlayer() {
        return player;
    }

}
