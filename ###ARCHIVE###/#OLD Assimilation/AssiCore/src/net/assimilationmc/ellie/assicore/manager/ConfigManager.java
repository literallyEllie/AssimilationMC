package net.assimilationmc.ellie.assicore.manager;

import net.assimilationmc.ellie.assicore.api.AssiCore;
import net.assimilationmc.ellie.assicore.api.SerializableLocation;
import net.assimilationmc.ellie.assicore.api.ServerState;
import net.assimilationmc.ellie.assicore.api.file.HelpFile;
import net.assimilationmc.ellie.assicore.util.Util;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Ellie on 20/11/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class ConfigManager implements IManager, FileHandler {

    private AssiCore assiCore;
    private File file;
    private YamlConfiguration configuration;

    private HelpFile helpFile;

    private transient String host, database, username, password, enjinAuth;
    private transient int port;
    private ServerState serverState;

    private String serverid, maintenanceReason, joinMessage, leaveMessage, economy;
    private boolean disableWeather, disablePvP, disableDangerousBlocks, forceSpawn;

    private List<String> authorisedOps, maintenanceWhitelist;

    private HashMap<String, String> chatFormats;

    private SerializableLocation spawn;


    private int ssid;

    ConfigManager(AssiCore assiCore){
        this.assiCore = assiCore;
    }

    @Override
    public String getModuleID() {
        return "config";
    }

    @Override
    public boolean load() {
        file = new File(assiCore.getAssiPlugin().getDataFolder(), "config.yml");

        chatFormats = new HashMap<>();

        try{
            configuration = YamlConfiguration.loadConfiguration(file);
            assign(false);
        }catch(IOException e){
            try {
                if(!file.exists()) {
                    assiCore.getAssiPlugin().getDataFolder().mkdirs();
                    file.createNewFile();
                    save(file, defaults());
                    assign(true);
                }else{
                    throw new IOException("Failed to load configuration file.");
                }
            }catch(IOException e1){
                e1.printStackTrace();
                assiCore.logE("Failed to load configuration file.");
            }
        }

        return true;
    }

    public YamlConfiguration defaults() throws IOException {
        configuration = YamlConfiguration.loadConfiguration(file);
        configuration.set("serverid", "unnamed");
        configuration.set("ssid", 1);
        configuration.set("enjinAuth", "null");
        configuration.set("sql.host", "localhost");
        configuration.set("sql.port", 3306);
        configuration.set("sql.database", "database");
        configuration.set("sql.username", "root");
        configuration.set("sql.password", "root");
        configuration.set("authorisedOperators", Arrays.asList());
        configuration.set("serverState", ServerState.STABLE.name());
        configuration.set("maintenanceWhitelist", Collections.singletonList("xeline"));
        configuration.set("maintenanceReason", "&cThe server is currently in maintenance! Check back soon!");
        configuration.set("lang.join", "%prefix%%player% joined");
        configuration.set("lang.leave", "%prefix%%player% left");
        configuration.set("settings.disable-weather", false);
        configuration.set("settings.disable-pvp", false);
        configuration.set("settings.disable-dangerous-blocks", true);
        configuration.set("settings.force-spawn", false);
        configuration.set("economy", "CoinEconomy");
        configuration.set("chat.default", "{display}&7 {message}");
        return configuration;
    }

    public void assign(boolean first) throws IOException {
        this.helpFile = new HelpFile();
        if(configuration != null){
            this.host = configuration.getString("sql.host");
            this.port = configuration.getInt("sql.port");
            this.database = configuration.getString("sql.database");
            this.username = configuration.getString("sql.username");
            this.password = configuration.getString("sql.password");

            this.serverid = configuration.getString("serverid");

            if(configuration.get("enjinAuth") == null){
                configuration.set("enjinAuth", "");
                save();
            }

            this.enjinAuth = configuration.getString("enjinAuth");

            if(!first) {
                this.ssid = (configuration.getInt("ssid") + 1);
                configuration.set("ssid", ssid);
                save(file, configuration);
            }else {
                this.ssid = configuration.getInt("ssid");
            }
            this.authorisedOps = configuration.getStringList("authorisedOperators");
            this.serverState = ServerState.valueOf(configuration.getString("serverState"));

            this.maintenanceReason = configuration.getString("maintenanceReason");

            this.maintenanceWhitelist = configuration.getStringList("maintenanceWhitelist");

            this.joinMessage = configuration.getString("lang.join").replace("%prefix%", Util.prefix());
            this.leaveMessage = configuration.getString("lang.leave").replace("%prefix%", Util.prefix());

            this.disableWeather = configuration.getBoolean("settings.disable-weather");
            this.disablePvP = configuration.getBoolean("settings.disable-pvp");
            this.disableDangerousBlocks = configuration.getBoolean("settings.disable-dangerous-blocks");
            this.forceSpawn = configuration.getBoolean("settings.force-spawn");

            for (String s : configuration.getConfigurationSection("chat").getKeys(false)) {
                chatFormats.put(s, configuration.getString("chat."+s));
            }

            if(configuration.get("spawn") != null){
                this.spawn = new SerializableLocation(configuration.getString("spawn"));
            }

            this.economy = configuration.getString("economy");

        }
    }

    public HelpFile getHelpFile() {
        return helpFile;
    }

    String getHost() {
        return host;
    }

    int getPort() {
        return port;
    }

    String getDatabase() {
        return database;
    }

    String getUsername(){
        return username;
    }

    String getPassword(){
        return password;
    }

    public int getSSID() {
        return ssid;
    }

    public String getServerID() {
        return serverid;
    }

    public String getEnjinAuth() {
        return enjinAuth;
    }

    public String getMaintenanceReason() {
        return maintenanceReason;
    }

    public void setMaintenanceReason(String maintenanceReason) {
        this.maintenanceReason = maintenanceReason;
        configuration.set("maintenanceReason", maintenanceReason);
        save();
    }

    public List<String> getMaintenanceWhitelist() {
        return maintenanceWhitelist;
    }

    public void add(String name){
        maintenanceWhitelist.add(name.toLowerCase());
        configuration.set("maintenanceWhitelist", maintenanceWhitelist);
    }

    public void remove(String name){
        maintenanceWhitelist.remove(name.toLowerCase());
        configuration.set("maintenanceWhitelist", maintenanceWhitelist);
    }

    public ServerState getServerState() {
        return serverState;
    }

    public void setServerState(ServerState serverState) {
        this.serverState = serverState;
        configuration.set("serverState", serverState.toString());
        save();
    }

    public boolean isDisableWeather() {
        return disableWeather;
    }

    public boolean isDisablePvP() {
        return disablePvP;
    }

    public boolean isDisableDangerousBlocks() {
        return disableDangerousBlocks;
    }

    public boolean isForceSpawn() {
        return forceSpawn;
    }

    public SerializableLocation getSpawn() {
        return spawn;
    }

    public void setSpawn(SerializableLocation spawn) {
        this.spawn = spawn;
    }

    public Collection<String> getAuthorisedOperators() {
        return Collections.unmodifiableCollection(authorisedOps);
    }

    public String getJoinMessage(String player) {
        return joinMessage.isEmpty() ? null : joinMessage.replace("%player%", player);
    }

    public String getLeaveMessage(String player) {
        return leaveMessage.isEmpty() ? null : leaveMessage.replace("%player%", player);
    }

    public String getEconomy() {
        return economy;
    }

    public HashMap<String, String> getChatFormats() {
        return chatFormats;
    }

    @Override
    public boolean unload() {
        this.host = null;
        this.port = -1;
        this.database = null;
        this.username = null;
        this.password = null;
        this.authorisedOps.clear();
        this.joinMessage = null;
        this.leaveMessage = null;
        chatFormats.clear();
        chatFormats = null;
        if(spawn != null){
            configuration.set("spawn", spawn.toString());
            save();
        }
        spawn = null;
        configuration = null;
        file = null;
        return true;
    }

    public YamlConfiguration getConfiguration() {
        return configuration;
    }

    public File getFile() {
        return file;
    }

    public void save(){
        if(configuration != null){
            try{
                configuration.save(file);
            }catch(IOException e){
                e.printStackTrace();
                assiCore.logE("Failed to save configuration file.");
            }
        }
    }

    public void save(File file, YamlConfiguration configuration){
        try{
        configuration.save(file);
        }catch(IOException e){
            e.printStackTrace();
            assiCore.logE("Failed to save configuration file.");
        }
    }



}
