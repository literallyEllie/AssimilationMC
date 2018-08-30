package net.assimilationmc.ellie.assiuhc.command.admin;

import net.assimilationmc.ellie.assicore.permission.Rank;
import net.assimilationmc.ellie.assicore.util.Callback;
import net.assimilationmc.ellie.assiuhc.UHC;
import net.assimilationmc.ellie.assiuhc.command.SubCommand;
import net.assimilationmc.ellie.assiuhc.util.DropSetupTool;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ellie on 2.8.17 for AssimilationMC.
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
public class CmdDrops extends SubCommand {

    private HashMap<String, Callback<DropSetupTool>> dropSetups = new HashMap<>();

    public CmdDrops() {
        super("drops", Rank.ADMIN, "uhc drops <map>", "Begins initiation process of setting up drops", new ArrayList<>());
        setPlayerOnly(true);
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(args.length == 2){

            String map = args[1];
            if (getMapManager().getMap(map) == null && getMapManager().getConstructionMap(map) == null) {
                sendPMessage(sender, "Map doesn't exist.");
                return;
            }

            if(dropSetups.containsKey(map)){
                sendPMessage(sender, "A map with that name is already being modified.");
                return;
            }

            sendPMessage(sender, "Entering drop edit mode.");
            setEditing(map, ((Player) sender));
        }else{
            sendMessage(sender, correctUsage());
        }
    }

    private void setEditing(String map, Player player) {
        new DropSetupTool(UHC.getPlugin(UHC.class).getDropItemManager(), this, map, player);
        dropSetups.put(map, (DropSetupTool data) -> {
            dropSetups.remove(map);
            getMapManager().setDrops(map, data.getSavedDrops(), getMapManager().getConstructionMap(map) != null);
            sendPMessage(player, "Drops setup.");
        });

    }

    public HashMap<String, Callback<DropSetupTool>> getDropSetups() {
        return dropSetups;
    }
}
