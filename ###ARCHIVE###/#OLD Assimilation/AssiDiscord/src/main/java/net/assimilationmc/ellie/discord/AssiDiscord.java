package net.assimilationmc.ellie.discord;

import net.assimilationmc.ellie.discord.backend.JsonBotConfig;
import net.assimilationmc.ellie.discord.command.DCmdIP;
import net.assimilationmc.ellie.discord.command.DCmdOnline;
import net.assimilationmc.ellie.discord.command.DisCommand;
import net.assimilationmc.ellie.discord.listener.ChatListener;
import net.assimilationmc.ellie.discord.listener.OtherListeners;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.util.HashMap;

/**
 * Created by Ellie on 14/04/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class AssiDiscord extends JavaPlugin {

    private static AssiDiscord assiDiscord;
    private JsonBotConfig jsonBotConfig;

    private HashMap<String, DisCommand> commands = new HashMap<>();

    private JDA jda;

    @Override
    public void onEnable() {
        assiDiscord = this;

        jsonBotConfig = new JsonBotConfig();

        try {

            JDABuilder builder = new JDABuilder(AccountType.BOT);
            builder.setToken(jsonBotConfig.getBotToken());
            builder.addEventListener(new OtherListeners(jsonBotConfig));
            builder.addEventListener(new ChatListener(this, jsonBotConfig.getBotPrefix()));
            builder.setGame(Game.of(jsonBotConfig.getBotGame()));
            builder.setStatus(OnlineStatus.fromKey(jsonBotConfig.getBotState()));

            jda = builder.buildBlocking();
        }catch(LoginException | RateLimitedException | InterruptedException  e){
            e.printStackTrace();
            logE("Failed to startup!");
        }

        loadCommands();
    }

    @Override
    public void onDisable() {
        if(jda != null){
           // jda.getTextChannelById(Channels.BOT_LOGS).sendMessage("AssiDiscord has now shutdown").queue();
            jda.shutdown();
            jda = null;
        }
        assiDiscord = null;
    }

    private void loadCommands(){
        commands.put("online", new DCmdOnline());
        commands.put("ip", new DCmdIP());
    }

    public void messageChannel(long channel, String message){
        jda.getTextChannelById(channel).sendMessage(message).queue();
    }

    public JDA getDiscord() {
        return jda;
    }

    public static AssiDiscord getAssiDiscord() {
        return assiDiscord;
    }

    public void logI(String info){
        Bukkit.getLogger().info("[AssiDiscord] "+info);
    }

    public void logW(String warn){
        Bukkit.getLogger().warning("[AssiDiscord] "+warn);
    }

    public void logE(String error){
        Bukkit.getLogger().severe("[AssiDiscord] "+error);
    }

    public JsonBotConfig getJsonBotConfig() {
        return jsonBotConfig;
    }

    public HashMap<String, DisCommand> getCommands() {
        return commands;
    }
}