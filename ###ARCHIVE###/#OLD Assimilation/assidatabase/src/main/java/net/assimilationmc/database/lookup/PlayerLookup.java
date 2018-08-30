package net.assimilationmc.database.lookup;

import net.assimilationmc.database.JCallback;
import net.assimilationmc.database.JedisUtil;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Ellie on 06/09/2017 for AssimilationMC.
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
public class PlayerLookup {

    private JedisUtil jedisUtil;

    public PlayerLookup(JedisUtil jedisUtil) {
        this.jedisUtil = jedisUtil;
    }

    public String getPlayerByUuid(String uuid, JCallback jCallback) throws ExecutionException, InterruptedException, TimeoutException {
        return (String) jedisUtil.queue(() -> jedisUtil.getJedis().get(uuid), jCallback).get(10, TimeUnit.SECONDS);
    }

    public String getUuidByName(String name, JCallback callback) throws ExecutionException, InterruptedException, TimeoutException {
        return (String) jedisUtil.queue(() -> jedisUtil.getJedis().get(name), callback).get(10, TimeUnit.SECONDS);

    }

}
