package net.assimilationmc.ellie.assicore.score;

import net.assimilationmc.ellie.assicore.manager.IManager;
import org.bukkit.entity.Player;

import java.util.*;

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
public class BetterScoreboardManager implements IManager {

    private final Map<UUID, AssiScore> scores = new HashMap<>();
    private List<String> customlines;
    private ScoreboardPolicy scoreboardPolicy;
    private String title = " AssimilationMC ";
    private long lastUpdate = System.currentTimeMillis();

    @Override
    public boolean load() {
        return true;
    }

    @Override
    public boolean unload() {
        return true;
    }

    public void update() {
        this.update(true);
    }

    public void update(boolean cooldown) {
        if(!cooldown || System.currentTimeMillis() - lastUpdate >= 1000L) {
            lastUpdate = System.currentTimeMillis();
            scores.values().forEach(AssiScore::update);
        }
    }

    public void update(Player player) {
        if(this.scores.containsKey(player.getUniqueId())) {
            scores.get(player.getUniqueId()).update();
        }
    }

    public Map<UUID, AssiScore> getScores() {
        return scores;
    }

    public ScoreboardPolicy getScoreboardPolicy() {
        return scoreboardPolicy;
    }

    public void setScoreboardPolicy(ScoreboardPolicy scoreboardPolicy) {
        this.scoreboardPolicy = scoreboardPolicy;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        scores.values().forEach(score -> score.setTitle(title));
    }

    public List<String> getCustomlines() {
        return customlines;
    }

    public void addCustomlines(List<String> lines) {
        if(lines == null) {
            customlines = null;
        }else if(customlines == null) {
            customlines = lines;
        } else customlines.addAll(lines);
    }

    public String trimPrefix(String prefix) {
        return prefix == null ? null : prefix.substring(0, Math.min(prefix.length(), 16));
    }

    @Override
    public String getModuleID() {
        return "Scoreboard Manager";
    }
}
