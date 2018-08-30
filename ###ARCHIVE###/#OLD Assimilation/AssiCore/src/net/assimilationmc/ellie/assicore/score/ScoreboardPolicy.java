package net.assimilationmc.ellie.assicore.score;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * Created by Ellie on 30/09/2017 for AssimilationMC.
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
public abstract class ScoreboardPolicy {

    protected final JavaPlugin plugin;

    public ScoreboardPolicy(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public abstract void getSideBar(Player player, List<String> lines);

    public abstract String getPrefix(Player perspective, Player subject);

    public abstract String getSuffix(Player perspective, Player subject);

    public abstract String getUnderName(Player player);

    public abstract int getUndernameScore(Player perspective, Player subject);

    public abstract String getTablist(Player player);

    public abstract String getTablistScore(Player perspective, Player subject);

}
