package net.assimilationmc.assicore.server.analytics.payload;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.server.ServerData;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public abstract class AnalyticPayload {

    private final long start;
    private String token;
    private long end;

    @SerializedName("up_time")
    private long upTime;

    private boolean opening;

    @SerializedName("server_data")
    private ServerData serverData;

    @SerializedName("online")
    private int onlinePlayers = -1;
    @SerializedName("max_players")
    private int maxPlayers = -1;

    @SerializedName("external_ip")
    private String externalIp;

    private List<String> plugins;
    private Map<String, Rank> players;

    public AnalyticPayload(long start, boolean opening) {
        this.start = start;
        this.opening = opening;
    }

    public AnalyticPayload(long start) {
        this(start, false);
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public long elapased() {
        return end - start;
    }

    public long getUpTime() {
        return upTime;
    }

    public void setUpTime(long upTime) {
        this.upTime = upTime;
    }

    public boolean isOpening() {
        return opening;
    }

    public ServerData getServerData() {
        return serverData;
    }

    public void setServerData(ServerData serverData) {
        this.serverData = serverData;
    }

    public int getOnlinePlayers() {
        return onlinePlayers;
    }

    public void setOnlinePlayers(int onlinePlayers) {
        this.onlinePlayers = onlinePlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public String getExternalIp() {
        return externalIp;
    }

    public void setExternalIp(String externalIp) {
        this.externalIp = externalIp;
    }

    public List<String> getPlugins() {
        return plugins;
    }

    public void setPlugins(List<String> plugins) {
        this.plugins = plugins;
    }

    public Map<String, Rank> getPlayers() {
        return players;
    }

    public void setPlayers(Map<String, Rank> players) {
        this.players = players;
    }

    public String serialise(String token) {
        this.token = token;
        final Gson gson = new Gson();
        Type type = new TypeToken<AnalyticPayload>() {
        }.getType();
        return gson.toJson(this, type);
    }

}
