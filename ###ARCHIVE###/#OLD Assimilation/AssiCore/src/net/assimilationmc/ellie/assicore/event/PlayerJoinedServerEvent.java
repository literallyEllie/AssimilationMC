package net.assimilationmc.ellie.assicore.event;

import net.assimilationmc.ellie.assicore.api.AssiPlayer;

/**
 * Created by Ellie on 02/09/2017 for AssimilationMC.
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
public class PlayerJoinedServerEvent extends AssiEvent {

    private final AssiPlayer player;

    public PlayerJoinedServerEvent(AssiPlayer player) {
        this.player = player;
    }

    public AssiPlayer getPlayer() {
        return player;
    }

}
