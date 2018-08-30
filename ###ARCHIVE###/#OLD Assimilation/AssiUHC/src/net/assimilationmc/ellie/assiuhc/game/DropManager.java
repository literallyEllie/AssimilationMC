package net.assimilationmc.ellie.assiuhc.game;

import net.assimilationmc.ellie.assiuhc.backend.IUHCDropPackage;

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
public class DropManager {

    private UHCGame game;
    private HashMap<Integer, IUHCDropPackage> drops;

    public DropManager(UHCGame game) {
        this.game = game;
        this.drops = game.getMap().getDropLocations();
    }

    public void init(){
        drops.values().forEach(iuhcDropPackage -> iuhcDropPackage.getLocation().setWorld(game.getDedicatedWorld().getCBWorld()));
    }

    public void debugSpawn() {
        System.out.println(drops);
        drops.get(0).drop();
    }

}
