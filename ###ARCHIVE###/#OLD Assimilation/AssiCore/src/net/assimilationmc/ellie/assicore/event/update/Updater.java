package net.assimilationmc.ellie.assicore.event.update;

import net.assimilationmc.ellie.assicore.util.UtilServer;
import org.bukkit.plugin.java.JavaPlugin;


public class Updater implements Runnable {
    
    public Updater(JavaPlugin plugin) {
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 0L, 1L);
    }

    @Override
    public void run() {
        for (int i = 0; i < UpdateType.values().length; i++) {
            UpdateType type = UpdateType.values()[i];
            if(type.elapsed()) {
                UtilServer.callEvent(new UpdateEvent(type));
            }
        }
    }
}
