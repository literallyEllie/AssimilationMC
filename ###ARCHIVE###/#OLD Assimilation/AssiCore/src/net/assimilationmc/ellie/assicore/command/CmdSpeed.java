package net.assimilationmc.ellie.assicore.command;

import net.assimilationmc.ellie.assicore.permission.Rank;
import net.assimilationmc.ellie.assicore.util.ColorChart;
import net.assimilationmc.ellie.assicore.util.MessageLib;
import net.assimilationmc.ellie.assicore.util.PermissionLib;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * Created by Ellie on 21/08/17 for AssimilationMC.
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
public class CmdSpeed extends AssiCommand {

    public CmdSpeed(){
        super("speed", Rank.MOD, "speed <1-10> [player]", "Get some speed on", Arrays.asList("fspeed", "wspeed"));
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Player targetPlayer = null;

        if(sender instanceof Player) targetPlayer = (Player) sender;

        if(args.length < 1) {
            sendMessage(sender, correctUsage());
            return;
        }

        if(args.length == 2 && sender.hasPermission(PermissionLib.CMD.SPEED_OTHER)) {

            targetPlayer = Bukkit.getPlayer(args[1]);
            if(targetPlayer == null ){
                sendMessage(sender, MessageLib.PLAYER_OFFLINE);
                return;
            }
        }

        if(targetPlayer == null) {
            sendMessage(sender, correctUsage());
            return;
        }

        float speed;
        try {
            speed = Float.parseFloat(args[0]);
            if(speed > 10 || speed < 1) throw new NumberFormatException();
        }catch(NumberFormatException e) {
            sendMessage(sender, MessageLib.INVALID_NUMBER);
            return;
        }
        speed = speed / 10;

        //

        if(targetPlayer.isFlying()) {
            targetPlayer.setFlySpeed(speed);
            sendPMessage(targetPlayer, "Your fly speed has been set to "+ ColorChart.VARIABLE + speed * 10 + ColorChart.R +".");
            return;
        }
        targetPlayer.setWalkSpeed(speed);
        sendPMessage(targetPlayer, "Your walk speed has been set to "+ ColorChart.VARIABLE + speed * 10 + ColorChart.R +".");


    }

}
