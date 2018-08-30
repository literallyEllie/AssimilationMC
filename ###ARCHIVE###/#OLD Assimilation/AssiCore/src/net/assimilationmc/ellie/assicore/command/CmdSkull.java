package net.assimilationmc.ellie.assicore.command;

import net.assimilationmc.ellie.assicore.api.ItemBuilder;
import net.assimilationmc.ellie.assicore.permission.Rank;
import net.assimilationmc.ellie.assicore.util.ColorChart;
import net.assimilationmc.ellie.assicore.util.MessageLib;
import net.assimilationmc.ellie.assicore.util.PermissionLib;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Ellie on 22.7.17 for AssimilationMC.
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
public class CmdSkull extends AssiCommand {

    public CmdSkull(){
        super("skull", Rank.ADMIN, "skull <name> [player]", "Spawn a skull");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        String name;
        Player other = null;

        if(args.length == 2 && sender.hasPermission(PermissionLib.CMD.SKULL_OTHER)){ // ADMIN
            other = Bukkit.getPlayer(args[1]);
            if(other == null){
                sendPMessage(sender, MessageLib.PLAYER_OFFLINE);
                return;
            }
        }

        if(args.length > 0){
            name = args[0];

            if(!isPlayer(sender) && other == null){
                sendMessage(sender, correctUsage());
                return;
            }

            ItemBuilder itemBuilder = new ItemBuilder(Material.SKULL_ITEM);
            ItemStack stack;

            String textures;
            if(name.contains(":")) {
                textures = name.split(":")[1];
            }

            /*
            /give @p skull 1 3 {display:{Name:"Discord"},SkullOwner:{Id:"de431cd1-ae1d-49f6-9339-a96daeacc32b",
            Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzg3M2MxMmJmZmI1MjUxYTBiODhkNWFlNzVjNzI0N2NiMzlhNzVmZjFhODFjYmU0YzhhMzliMzExZGRlZGEifX19"}]}}}
             */

            if(other != null){

                other.getInventory().addItem(itemBuilder.asPlayerHead(name).build());
                sendPMessage(sender, "Given "+ ColorChart.VARIABLE+other.getName()+" "+name+ColorChart.R+"'s head.");
            }else

            if(isPlayer(sender)){
                ((Player)sender).getInventory().addItem(itemBuilder.asPlayerHead(name).build());
                sendPMessage(sender, "Given "+ ColorChart.VARIABLE+sender.getName()+" "+name+ColorChart.R+"'s head.");
            }

        }else sendMessage(sender, correctUsage());






    }


}
