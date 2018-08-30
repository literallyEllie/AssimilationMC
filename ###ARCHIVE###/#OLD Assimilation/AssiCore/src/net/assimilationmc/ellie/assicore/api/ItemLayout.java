package net.assimilationmc.ellie.assicore.api;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class ItemLayout {

    private int invSize = 0;
    private ArrayList<Integer> size;

    public ItemLayout(String... strings) {
        size = new ArrayList<>();
        invSize = strings.length * 9;
        for (int row = 0; row < strings.length; row++) {
            String string = strings[row];
            if (string.length() != 9)
                throw new IllegalArgumentException("String '" + string + "' must have a length of 9 characters");
            char[] cArray = string.toCharArray();
            for (int slot = 0; slot < 9; slot++) {
                char letter = cArray[slot];
                if ('x' == Character.toLowerCase(letter)) {
                } else if ('o' == Character.toLowerCase(letter)) {
                    size.add((row * 9) + slot);
                } else
                    throw new IllegalArgumentException("Unrecognised character " + letter);
            }
        }
    }

    public ArrayList<Integer> getItemSlots() {
        return size;
    }

    public ItemStack[] generate(ArrayList<ItemStack> items) {
        return generate(items.toArray(new ItemStack[0]));
    }

    public ItemStack[] generate(ItemStack... items) {
        return generate(true, items);
    }

    public ItemStack[] generate(boolean doRepeats, ItemStack... items) {
        ItemStack[] itemArray = new ItemStack[invSize];

        if (items.length == 0)
            return itemArray;

        int i;
        for (int slot : size) {
            if (doRepeats)
                i = 0;
            else
                break;

            itemArray[slot] = items[i];

        }
        return itemArray;
    }
}