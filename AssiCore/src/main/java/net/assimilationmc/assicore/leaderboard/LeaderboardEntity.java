package net.assimilationmc.assicore.leaderboard;

import org.bukkit.Location;

public class LeaderboardEntity {

    private final int citizensId;

    private String leaderboardType;
    private int place;
    private Location updateSign;

    public LeaderboardEntity(int citizensId, Location updateSign, String leaderboardType, int place) {
        this.citizensId = citizensId;
        this.updateSign = updateSign;
        this.leaderboardType = leaderboardType;
        this.place = place;
    }

    public LeaderboardEntity(int citizensId) {
        this(citizensId, null, "", -1);
    }

    public int getCitizensId() {
        return citizensId;
    }

    public String getLeaderboardType() {
        return leaderboardType;
    }

    public void setLeaderboardType(String leaderboardType) {
        this.leaderboardType = leaderboardType;
    }

    public int getPlace() {
        return place;
    }

    public void setPlace(int place) {
        this.place = place;
    }

    public Location getUpdateSign() {
        return updateSign;
    }

    public void setUpdateSign(Location updateSign) {
        this.updateSign = updateSign;
    }

}
