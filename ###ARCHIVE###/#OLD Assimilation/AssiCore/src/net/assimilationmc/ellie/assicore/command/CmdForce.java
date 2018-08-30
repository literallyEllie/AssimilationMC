package net.assimilationmc.ellie.assicore.command;

import net.assimilationmc.ellie.assicore.permission.Rank;
import net.assimilationmc.ellie.assicore.util.ColorChart;
import net.assimilationmc.ellie.assicore.util.MessageLib;
import net.assimilationmc.ellie.assicore.util.Util;
import net.assimilationmc.ellie.discord.AssiDiscord;
import net.assimilationmc.ellie.discord.util.Channels;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Ellie on 12/09/2017 for AssimilationMC.
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
public class CmdForce extends AssiCommand {

    public CmdForce() {
        super("force", Rank.ADMIN, "force <player> <message", "Force a player to do a command/chat message");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(args.length < 2) {
            sendMessage(sender, correctUsage());
            return;
        }

        Player player = Bukkit.getPlayer(args[0]);
        if(player == null) {
            sendMessage(sender, MessageLib.PLAYER_OFFLINE);
            return;
        }

        String toRun = Util.getFinalArg(args, 1);
        sendPMessage(sender, "Forcing " + player.getDisplayName() + " to run '" + ColorChart.VARIABLE + toRun + ColorChart.R + "'");
        player.chat(toRun);
        AssiDiscord.getAssiDiscord().messageChannel(Channels.BOT_LOGS, "`" + sender.getName() + "` forced `" + player.getName() + "` to run `" + toRun + "`");
    }

}
