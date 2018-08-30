package com.assimilation.ellie.assibungee.manager;

import com.assimilation.ellie.assibungee.AssiBungee;
import com.assimilation.ellie.assibungee.util.Util;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Ellie on 21/11/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class BroadcastManager implements IManager, FileHandler {

    private File file;
    private Configuration configuration;

    private List<String> exemptServer;
    private List<String> messages;

    private int next_message;

    private ScheduledTask task;
    private Runnable runnable;

    @Override
    public boolean load() {
        file = new File(AssiBungee.getAssiBungee().getDataFolder(), "broadcast.yml");

        try{
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
            assign(false);
        }catch(IOException e){
            try {
                if(!file.exists()) {
                    file.createNewFile();
                    ConfigurationProvider.getProvider(YamlConfiguration.class).save(defaults(), file);
                    assign(true);
                }else{
                    throw new IOException("Failed to load broadcast file.");
                }
            }catch(IOException e1){
                e1.printStackTrace();
                AssiBungee.getAssiBungee().logW("Failed to load broadcast file.");
            }
        }

        task = ProxyServer.getInstance().getScheduler().schedule(AssiBungee.getAssiBungee(), runnable, 1, 2, TimeUnit.MINUTES);
        return true;
    }

    public Configuration defaults() throws IOException {
        configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        configuration.set("exempt-servers", Arrays.asList("example-server"));
        configuration.set("messages", Arrays.asList("\nMessage with new lines\nSecond line\nThird line", "\tMessage with tab indents"));
        return configuration;
    }

    public void assign(boolean first) throws IOException {
        if(configuration != null){

            this.exemptServer = configuration.getStringList("messages");


            this.messages = new ArrayList<>();
            configuration.getStringList("messages").forEach(s -> messages.add(s.replace("%prefix%", Util.prefix())));

            this.next_message = 0;

            Set<String> servers = ProxyServer.getInstance().getServers().values().stream().filter(serverInfo -> !exemptServer.contains(serverInfo.getName()))
                    .map(ServerInfo::getName).collect(Collectors.toSet());

            runnable = () -> {

                if(next_message > messages.size()-1)
                    next_message = 0;

                servers.forEach(s -> ProxyServer.getInstance().getServerInfo(s).getPlayers().forEach(pp -> pp.sendMessage(new TextComponent(Util.color(messages.get(next_message))))));
                next_message++;
            };

        }
    }

    @Override
    public boolean unload() {
        this.file = null;
        this.configuration= null;
        this.exemptServer = null;
        this.messages = null;

        if(task != null){
            task.cancel();
        }

        this.runnable = null;

        return true;
    }

    @Override
    public String getModuleID() {
        return "broadcast";
    }

    public List<String> getExemptServers() {
        return exemptServer;
    }

    public List<String> getMessages() {
        return messages;
    }


}
