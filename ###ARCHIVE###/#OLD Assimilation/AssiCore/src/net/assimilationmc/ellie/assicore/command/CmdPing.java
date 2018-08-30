package net.assimilationmc.ellie.assicore.command;

import net.assimilationmc.ellie.assicore.util.ColorChart;
import net.assimilationmc.ellie.assicore.util.MessageLib;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Created by Ellie on 01/08/17 for AssimilationMC.
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
public class CmdPing extends AssiCommand  {

    public CmdPing(){
        super("ping", "ping [player]", "Ping");
        setPlayerOnly(true);
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if(args.length == 0){
            sendPMessage(player, "Your ping is "+ ColorChart.VARIABLE+((CraftPlayer)player).getHandle().ping+ColorChart.R+"ms.");
            return;
        }
        if(args.length == 1){
            Player t = Bukkit.getPlayer(args[0]);
            if(t == null){
                sendPMessage(player, MessageLib.PLAYER_OFFLINE);
                return;
            }
            sendPMessage(player, ColorChart.VARIABLE+t.getName()+ColorChart.R+"'s ping is "+ ColorChart.VARIABLE+((CraftPlayer)t).getHandle().ping+ColorChart.R+"ms.");
        }

    }

}
