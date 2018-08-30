package net.assimilationmc.ellie.assicore.command;

import com.google.common.base.Joiner;
import net.assimilationmc.ellie.assicore.api.AssiCore;
import net.assimilationmc.ellie.assicore.util.ColorChart;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

/**
 * Created by Ellie on 21/08/2017 for AssimilationMC.
 * <p>
 * Copyright 2017 Ellie
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class CmdList extends AssiCommand {

    public CmdList() {
        super("list", "list", "Shows online players", Collections.singletonList("online"));
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        int online = Bukkit.getOnlinePlayers().size() - AssiCore.getCore().getVanishedPlayers().size();
        sendPMessage(sender, "Online players (" + ColorChart.VARIABLE + online + ColorChart.R + ") : "
                + Joiner.on(ColorChart.R + ", ").join(Bukkit.getOnlinePlayers().stream().map(Player::getDisplayName).toArray()));
    }

}
