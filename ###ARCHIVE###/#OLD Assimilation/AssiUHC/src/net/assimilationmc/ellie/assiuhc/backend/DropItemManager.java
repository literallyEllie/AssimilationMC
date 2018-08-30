package net.assimilationmc.ellie.assiuhc.backend;

import net.assimilationmc.ellie.assicore.api.ItemBuilder;
import net.assimilationmc.ellie.assicore.util.UtilFile;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
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
public class DropItemManager {

    private File file;
    private HashMap<String, HashMap<ItemStack, Double>> dropOptions = new HashMap<>();

    public DropItemManager(File root) {
        this.file = new File(root, "drops.yml");

        final boolean first = root.exists();

        if(!first){
            UtilFile.createFile(root);
        }

        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        if(first){
            configuration.createSection("drops");

            ItemStack defaultSword = new ItemBuilder(Material.WOOD_SWORD).setAmount(3)
                    .setDisplay("&cA line of text")
                    .setLore("lore line1", "lore line2").build();

            ItemStack defaultHead = new ItemBuilder(Material.SKULL_ITEM).asPlayerHead("xEline")
                    .setDisplay("&cA line of text")
                    .setLore("lore line1", "lore line2").build();

            ItemStack defaultEnchantedItem = new ItemBuilder(Material.GOLD_SWORD)
                    .setDisplay("&cA line of text")
                    .setLore("lore line1", "lore line2").build();
            defaultEnchantedItem.addEnchantment(Enchantment.DAMAGE_ALL, 2);

            configuration.set("drops.REGULAR.1", itemToStringBlob(defaultSword)+"###"+0.9d);
            configuration.set("drops.REGULAR.2", itemToStringBlob(defaultHead)+"###"+0.6d);
            configuration.set("drops.REGULAR.3", itemToStringBlob(defaultEnchantedItem)+"###"+0.3d);
            save(configuration);
        }

        for (String dropType : configuration.getConfigurationSection("drops").getKeys(false)) {
            HashMap<ItemStack, Double> drops = new HashMap<>();

            for (String item : configuration.getConfigurationSection("drops." + dropType).getKeys(false)) {
                final String[] params = configuration.getString("drops."+dropType+"."+item).split("###");
                final String serialisedItem = params[0];
                final double chance = Double.parseDouble(params[1]);
                drops.put(stringBlobToItem(serialisedItem), chance);
            }

            dropOptions.put(dropType, drops);
        }
    }

    public void finish(){
        dropOptions.clear();
    }

    private void save(YamlConfiguration configuration){
        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String itemToStringBlob(ItemStack itemStack) {
        YamlConfiguration config = new YamlConfiguration();
        config.set("i", itemStack);
        return config.saveToString();
    }

    private ItemStack stringBlobToItem(String stringBlob) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(stringBlob);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return config.getItemStack("i", null);
    }

    public HashMap<String, HashMap<ItemStack, Double>> getDropOptions() {
        return dropOptions;
    }

}
