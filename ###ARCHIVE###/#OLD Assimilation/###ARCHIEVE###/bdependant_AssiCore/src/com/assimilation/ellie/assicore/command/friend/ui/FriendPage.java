package com.assimilation.ellie.assicore.command.friend.ui;

import com.assimilation.ellie.assicore.api.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Ellie on 16/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public enum FriendPage {

    FRIENDS(new ItemBuilder(new ItemStack(Material.SKULL_ITEM, 1, (short) 3)).setDisplay("Friends").build(), "Friends");

    private ItemStack itemStack;
    private String name;

    FriendPage(ItemStack itemStack, String name){
        this.itemStack = itemStack;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

}
