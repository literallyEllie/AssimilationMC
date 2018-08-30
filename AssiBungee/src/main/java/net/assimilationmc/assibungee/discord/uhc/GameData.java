package net.assimilationmc.assibungee.discord.uhc;

import java.util.List;

public class GameData {

    private long recordingMessage;
    private String gamePhase, map, type;
    private int maxPlayers;
    private boolean custom;
    private List<String> winners;

    public GameData(long recordingMessage, String map, int maxPlayers, String type, boolean custom) {
        this.recordingMessage = recordingMessage;
        this.gamePhase = "Lobby";
        this.map = map;
        this.maxPlayers = maxPlayers;
        this.type = type.substring(0, 1).toUpperCase() + type.toLowerCase().replace("_", " ").substring(1);
        this.custom = custom;
    }

    public long getRecordingMessage() {
        return recordingMessage;
    }

    public void setRecordingMessage(long recordingMessage) {
        this.recordingMessage = recordingMessage;
    }

    public String getGamePhase() {
        return gamePhase;
    }

    public void setGamePhase(String gamePhase) {
        this.gamePhase = gamePhase;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public boolean isCustom() {
        return custom;
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }

    public List<String> getWinners() {
        return winners;
    }

    public void setWinners(List<String> winners) {
        this.winners = winners;
    }

}
