package net.assimilationmc.assibungee.discord;

import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;

public class DiscordBotData {

    private transient String token;
    private String commandPrefix;
    private Game game;
    private OnlineStatus onlineStatus;

    public DiscordBotData() {
    }

    String getToken() {
        return token;
    }

    void setToken(String token) {
        this.token = token;
    }

    public String getCommandPrefix() {
        return commandPrefix;
    }

    void setCommandPrefix(String commandPrefix) {
        this.commandPrefix = commandPrefix;
    }

    public Game getGame() {
        return game;
    }

    void setGame(String game) {
        this.game = Game.playing(game);
    }

    void setListening(String game) {
        this.game = Game.listening(game);
    }

    void setWatching(String game) {
        this.game = Game.watching(game);
    }

    public OnlineStatus getOnlineStatus() {
        return onlineStatus;
    }

    void setOnlineStatus(OnlineStatus onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

}
