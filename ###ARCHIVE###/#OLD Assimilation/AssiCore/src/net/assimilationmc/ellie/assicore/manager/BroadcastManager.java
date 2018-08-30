package net.assimilationmc.ellie.assicore.manager;

import net.assimilationmc.ellie.assicore.AssiPlugin;
import net.assimilationmc.ellie.assicore.api.AssiCore;
import net.assimilationmc.ellie.assicore.util.FileUtil;
import net.assimilationmc.ellie.assicore.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Ellie on 21/11/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class BroadcastManager implements IManager, FileHandler {

    private File file;
    private YamlConfiguration configuration;

    private List<String> exemptServer;
    private List<String> messages;

    private int next_message;

    private int task = -1;
    private Runnable runnable;

    @Override
    public boolean load() {
        file = new File(AssiPlugin.getPlugin(AssiPlugin.class).getDataFolder(), "broadcast.yml");

        if(!file.exists()){
            try {
                file.createNewFile();
                FileUtil.saveFile(defaults(), file);
                assign(true);
            }catch(IOException e1){
                e1.printStackTrace();
                AssiCore.getCore().logW("Failed to load broadcast file.");
            }
        }else {
            configuration = YamlConfiguration.loadConfiguration(file);
            assign(false);
        }

        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(AssiPlugin.getPlugin(AssiPlugin.class), runnable, 1200L, 2400L);
        return true;
    }

    public YamlConfiguration defaults() {
        configuration = YamlConfiguration.loadConfiguration(file);
        configuration.set("messages", Arrays.asList("\nMessage with new lines\nSecond line\nThird line", "%t%Message with tab indents"));
        return configuration;
    }

    public void assign(boolean first) {
        if(configuration != null){

            this.exemptServer = configuration.getStringList("messages");


            this.messages = new ArrayList<>();
            configuration.getStringList("messages").forEach(s -> messages.add(s.replace("%prefix%", Util.prefix()).replace("%t%", "\t")));

            this.next_message = 0;
            runnable = () -> {
                if(next_message > messages.size()-1)
                    next_message = 0;

                Bukkit.getOnlinePlayers().forEach(pp -> pp.sendMessage(Util.color(messages.get(next_message))));
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

        if(task != -1){
            Bukkit.getScheduler().cancelTask(task);
            task = -1;
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
