package net.assimilationmc.ellie.assicore.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ellie on 02/08/2017 for AssimilationMC.
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
public class WeightedRandom<T> {

    public Map.Entry<T, Double> getWeightedItem(HashMap<T, Double> items) {

        double totalWeight = 0.0d;
        for (Double i : items.values()) {
            totalWeight += i;
        }
        double random = Math.random() * totalWeight;

        Map.Entry<T, Double> returnWeight = null;
        for (Map.Entry<T, Double> objectDoubleEntry : items.entrySet()) {
            random -= objectDoubleEntry.getValue();
            if (random <= 0.0d) {
                returnWeight = objectDoubleEntry;
                break;
            }
        }
        return returnWeight;
    }

}
