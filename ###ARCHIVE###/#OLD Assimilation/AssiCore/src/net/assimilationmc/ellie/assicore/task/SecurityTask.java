package net.assimilationmc.ellie.assicore.task;

import net.assimilationmc.ellie.assicore.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;

/**
 * Created by Ellie on 11/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class SecurityTask extends BukkitRunnable {

    private Collection<String> auth;

    public SecurityTask(Collection<String> auth){
        this.auth = auth;
    }

    @Override
    public void run() {

        Bukkit.getOnlinePlayers().forEach(o -> {
            if(o.isOp() && !auth.contains(o.getUniqueId().toString())){
                o.setOp(false);
                o.sendMessage(Util.prefix()+Util.color("&cYou have been automatically deoped for security precautions."));
            }
        });


    }
}
