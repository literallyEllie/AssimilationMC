package net.assimilationmc.ellie.assicore.util;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
public class SerializeItemStackList {

    public final static List<HashMap<Map<String, Object>, Map<String, Object>>> serializeItemStackList(final ItemStack[] itemStackList) {
        final List<HashMap<Map<String, Object>, Map<String, Object>>> serializedItemStackList = new ArrayList<>();

        for (ItemStack itemStack : itemStackList) {
            Map<String, Object> serializedItemStack, serializedItemMeta;
            HashMap<Map<String, Object>, Map<String, Object>> serializedMap = new HashMap<>();

            if (itemStack == null) itemStack = new ItemStack(Material.AIR);
            serializedItemMeta = (itemStack.hasItemMeta())
                    ? itemStack.getItemMeta().serialize()
                    : null;
            itemStack.setItemMeta(null);
            serializedItemStack = itemStack.serialize();

            serializedMap.put(serializedItemStack, serializedItemMeta);
            serializedItemStackList.add(serializedMap);
        }
        return serializedItemStackList;
    }

    public final static ItemStack[] deserializeItemStackList(final List<HashMap<Map<String, Object>, Map<String, Object>>> serializedItemStackList) {
        final ItemStack[] itemStackList = new ItemStack[serializedItemStackList.size()];

        int i = 0;
        for (HashMap<Map<String, Object>, Map<String, Object>> serializedItemStackMap : serializedItemStackList) {
            Map.Entry<Map<String, Object>, Map<String, Object>> serializedItemStack = serializedItemStackMap.entrySet().iterator().next();

            ItemStack itemStack = ItemStack.deserialize(serializedItemStack.getKey());
            if (serializedItemStack.getValue() != null) {
                ItemMeta itemMeta = (ItemMeta) ConfigurationSerialization.deserializeObject(serializedItemStack.getValue(), ConfigurationSerialization.getClassByAlias("ItemMeta"));
                itemStack.setItemMeta(itemMeta);
            }

            itemStackList[i++] = itemStack;
        }
        return itemStackList;
    }

}