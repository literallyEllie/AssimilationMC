package net.assimilationmc.ellie.discord.backend;

import net.assimilationmc.ellie.assicore.api.AssiCore;
import net.assimilationmc.ellie.discord.AssiDiscord;
import org.bukkit.Bukkit;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;


/**
 * Created by Ellie on 17/04/2017 for Discord.
 * Affiliated with www.minevelop.com
 */
public class JsonBotConfig {

    private File config;

    private transient String botToken;

    private String botPrefix, botGame, joinMessage, leaveMessage, botState;

    public JsonBotConfig(){
        config = new File(AssiCore.getCore().getAssiPlugin().getDataFolder(), "discord.json");

        if(!config.exists()){
            try{
                if(config.createNewFile()){
                    String jsonConfig = "{\n"+
                            "\t\"botToken\":\"none\",\n"+
                            "\t\"botPrefix\":\"!\",\n"+
                            "\t\"botGame\":\"Working...\",\n"+
                            "\t\"botState\":\"idle\",\n"+
                            "\t\"joinMessage\":\"Hey {taggedUser}! Welcome to the **{guild}** Discord!\",\n"+
                            "\t\"leaveMessage\":\"{namedUser} has left.\",\n"+
                            "}";
                    BufferedWriter writer = new BufferedWriter(new FileWriter(config));
                    writer.write(jsonConfig);
                    writer.close();
                    AssiDiscord.getAssiDiscord().logW("Config created to, please fill in fields... disabling...");
                    Bukkit.getPluginManager().disablePlugin(AssiDiscord.getAssiDiscord());
                }else{
                    throw new IllegalArgumentException("Configuration failed to be created!");
                }


            }catch(Exception e){
                e.printStackTrace();
                AssiDiscord.getAssiDiscord().logE("Failed to create config file!");
            }
        }else{
            try {
                JSONParser jsonParser = new JSONParser();
                JSONObject object = (JSONObject) jsonParser.parse(new FileReader(config));
                botToken = (String) object.get("botToken");
                botPrefix = (String) object.get("botPrefix");
                botGame = (String) object.get("botGame");
                botState = (String) object.get("botState");
                joinMessage = (String) object.get("joinMessage");
                leaveMessage = (String) object.get("leaveMessage");

            }catch(IOException | ParseException e){
                e.printStackTrace();
                AssiDiscord.getAssiDiscord().logE("Failed to parse config!");
                Bukkit.getPluginManager().disablePlugin(AssiDiscord.getAssiDiscord());
            }
        }
    }

    public void finish(){
        config = null;
    }

    public String getBotPrefix() {
        return botPrefix;
    }

    public String getBotToken() {
        return botToken;
    }

    public String getBotGame() {
        return botGame;
    }

    public String getBotState() {
        return botState;
    }

    public String getJoinMessage() {
        return joinMessage;
    }

    public String getLeaveMessage() {
        return leaveMessage;
    }

    public File getConfig() {
        return config;
    }
}
