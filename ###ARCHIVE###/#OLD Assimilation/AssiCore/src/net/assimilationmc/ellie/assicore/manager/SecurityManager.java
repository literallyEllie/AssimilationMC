package net.assimilationmc.ellie.assicore.manager;

import net.assimilationmc.ellie.assicore.api.AssiCore;
import net.assimilationmc.ellie.assicore.task.SecurityTask;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;
import java.util.UUID;

/**
 * Created by Ellie on 11/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public final class SecurityManager implements IManager {

    private Collection<String> authorisedOperators;

    private BukkitTask bukkitTask;

    @Override
    public boolean load() {

        authorisedOperators = ModuleManager.getModuleManager().getConfigManager().getAuthorisedOperators();

        bukkitTask = new SecurityTask(authorisedOperators).runTaskTimerAsynchronously(AssiCore.getCore().getAssiPlugin(),  20L, 1200L);

        return true;
    }

    @Override
    public boolean unload() {

        if(bukkitTask != null){
            bukkitTask.cancel();
            bukkitTask = null;
        }

        authorisedOperators = null;

        return true;
    }

    public boolean checkPlayer(UUID uuid){
        return authorisedOperators.contains(uuid.toString());
    }

    @Override
    public String getModuleID() {
        return "security";
    }
}
