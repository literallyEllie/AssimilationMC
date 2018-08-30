package com.assimilation.ellie.assibungee.manager;

import com.assimilation.ellie.assibungee.AssiBungee;
import com.assimilation.ellie.assibungee.server.ServerState;
import com.assimilation.ellie.assibungee.server.ServerType;
import com.assimilation.ellie.assibungee.util.AssiServerInfo;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by Ellie on 20/11/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class ConfigManager implements IManager, FileHandler {

    private AssiBungee assiBungee;
    private File file;
    private Configuration configuration;

    private transient String host, database, username, password;
    private transient int port;

    private int ssid;

    public ConfigManager(AssiBungee assiBungee){
        this.assiBungee = assiBungee;
    }

    @Override
    public String getModuleID() {
        return "config";
    }

    @Override
    public boolean load() {
        file = new File(assiBungee.getDataFolder(), "config.yml");

        try{
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
            assign(false);
        }catch(IOException e){
            try {
                if(!file.exists()) {
                    assiBungee.getDataFolder().mkdirs();
                    file.createNewFile();
                    save();
                    assign(true);
                }else{
                    throw new IOException("Failed to load configuration file.");
                }
            }catch(IOException e1){
                e1.printStackTrace();
                assiBungee.logE("Failed to load configuration file.");
            }
        }

        return true;
    }

    public Configuration defaults() throws IOException {
        configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        configuration.set("ssid", 1);
        configuration.set("sql.host", "localhost");
        configuration.set("sql.port", 3306);
        configuration.set("sql.database", "database");
        configuration.set("sql.username", "root");
        configuration.set("sql.password", "root");
        configuration.set("servers", Arrays.asList());
        return configuration;
    }

    public void assign(boolean first) throws IOException {
        if(configuration != null){
            this.host = configuration.getString("sql.host");
            this.port = configuration.getInt("sql.port");
            this.database = configuration.getString("sql.database");
            this.username = configuration.getString("sql.username");
            this.password = configuration.getString("sql.password");

            if(!first) {
                this.ssid = (configuration.getInt("ssid") + 1);
                configuration.set("ssid", ssid);
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, file);
            }else{
                this.ssid = configuration.getInt("ssid");
            }

            configuration.getStringList("servers").forEach(s ->  ModuleManager.getModuleManager().getServerManager().putServer(new AssiServerInfo(s)));

            List<String> servers = configuration.getStringList("servers");

            ProxyServer.getInstance().getServers().forEach((s, serverInfo) -> {
                if(ModuleManager.getModuleManager().getServerManager().getServer(s) == null){
                    AssiServerInfo assiServerInfo = new AssiServerInfo(s, ServerType.LOBBY, ServerState.MAINTENANCE);
                    ModuleManager.getModuleManager().getServerManager().putServer(assiServerInfo);
                    servers.add(assiServerInfo.toString());
                }
            });

            if(servers.size() != configuration.getStringList("servers").size()) {
                configuration.set("servers", servers);
                save();
            }
            servers.clear();


        }
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

    @Override
    public boolean unload() {

        List<String> servers = new ArrayList<>();
        ModuleManager.getModuleManager().getServerManager().getServers().values().forEach(serverInfo -> servers.add(serverInfo.toString()));
        save();


        this.host = null;
        this.database = null;
        this.username = null;
        this.password = null;
        return true;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public File getFile() {
        return file;
    }

    void updateServers(Collection<AssiServerInfo> serverInfo){
        List<String> servers = new ArrayList<>();
        serverInfo.forEach(si -> servers.add(si.toString()));
        configuration.set("servers", servers);
        save();
    }

    private void save(){
        if(configuration != null){
            try{
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, file);
            }catch(IOException e){
                e.printStackTrace();
                assiBungee.logE("Failed to save configuration file.");
            }
        }
    }

}
