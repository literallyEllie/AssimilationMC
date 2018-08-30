package net.assimilationmc.assicore.event.update;

import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.util.UtilServer;
import org.bukkit.Bukkit;

public class Updater implements Runnable {

    /**
     * THe Updater runnable which runs every single tick.
     *
     * @param plugin The plugin instance.
     */
    public Updater(AssiPlugin plugin) {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, 1);
    }

    @Override
    public void run() {
        for (final UpdateType updateType : UpdateType.values()) {
            if (updateType.elapsed()) {
                UtilServer.callEvent(new UpdateEvent(updateType));
            }
        }
    }

}