package net.assimilationmc.assicore.command.commands.eco;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.AssiCommand;
import org.bukkit.command.CommandSender;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class CmdBaltop extends AssiCommand {

    private Map<String, Integer> lastQuery;

    public CmdBaltop(AssiPlugin plugin) {
        super(plugin, "baltop", "Gets the top 10 richest Bucks people on the network (updated every 30 seconds)", Lists.newArrayList());
        this.lastQuery = Maps.newHashMap();
    }

    @Override
    public void onCommand(CommandSender sender, String usedLabel, String[] args) {

    }

    private void update() {
        lastQuery.clear();

        try (Connection connection = plugin.getSqlManager().getConnection()) {
// FUCK

        } catch (SQLException e) {
            plugin.getLogger().severe("");
            e.printStackTrace();
        }

    }

}
