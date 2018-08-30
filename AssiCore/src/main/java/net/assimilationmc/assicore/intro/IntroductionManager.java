package net.assimilationmc.assicore.intro;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.Module;
import net.assimilationmc.assicore.util.UtilMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class IntroductionManager extends Module {

    private Map<UUID, Integer> intro;

    public IntroductionManager(AssiPlugin plugin) {
        super (plugin, "Introduction Manager");
    }

    @Override
    protected void start() {
        this.intro = Maps.newHashMap();

    }

    @Override
    protected void end() {

        intro.clear();
    }

    public void begin(Player player) {
        if (intro.containsKey(player.getUniqueId())) {
            return;
        }

        intro.put(player.getUniqueId(), Bukkit.getScheduler().runTask(getPlugin(), () -> {
            UtilMessage.sendFullTitle(player, ChatColor.BLUE + "Welcome to " + ChatColor.DARK_GREEN + "Assi" + ChatColor.GREEN + "miliationMC" + ChatColor.BLUE + "!",
                    ChatColor.BOLD + "Lets get started...", 10, 20 * 6, 10);

            intro.put(player.getUniqueId(), Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {


            }, 40 + (20 * 6)).getTaskId());

        }).getTaskId());

    }


}
