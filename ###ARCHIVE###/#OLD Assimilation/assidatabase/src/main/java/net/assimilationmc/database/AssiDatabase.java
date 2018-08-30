package net.assimilationmc.database;

import net.assimilationmc.database.lookup.PlayerLookup;

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
public class AssiDatabase {

    private static JedisUtil jedisUtil;
    private static PlayerLookup playerLookup;

    public static void main(String[] args) {
        jedisUtil = new JedisUtil();
        jedisUtil.getJedis().set("009aea6fef9a46688d6f1090320946c8", "xEline");
        playerLookup = new PlayerLookup(jedisUtil);

        final long start = System.currentTimeMillis();

        try {

            playerLookup.getPlayerByUuid("009aea6fef9a46688d6f1090320946c8",
                    new JCallback() {
                        @Override
                        public void accept(Object o) {
                            System.out.println("name is " + o);
                        }
                    });


            System.out.println("Done in " + (System.currentTimeMillis() - start)+"ms");


        }catch(Exception e) {
            e.printStackTrace();
        }

        jedisUtil.getJedis().disconnect();

    }

    public AssiDatabase() {
    }

}
