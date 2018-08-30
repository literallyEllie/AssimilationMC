package com.assimilation.ellie.assikits.kit;

import com.assimilation.ellie.assicore.api.SerializableInventory;
import org.bukkit.inventory.Inventory;

import java.io.IOException;

/**
 * Created by Ellie on 18/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class AssiKit {

    private final int id;
    private String name;
    private String permission;
    private int price;
    private String serializedInventory;
    private boolean enabled;

    public AssiKit(int id, String name, String serializedInventory){
        this.id = id;
        this.name = name;
        this.permission = "";
        this.price = 0;
        this.serializedInventory = serializedInventory;
        this.enabled = true;
    }

    public AssiKit(int id, String name, Inventory inventory){
        this(id, name, SerializableInventory.toBase64(inventory));
    }

    public int getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isFree(){
        return price == -1;
    }

    public String getSerializedInventory() {
        return serializedInventory;
    }

    public void setSerializedInventory(String serializedInventory) {
        this.serializedInventory = serializedInventory;
    }

    public Inventory getInventory() throws IOException {
        return SerializableInventory.fromBase64(serializedInventory);
    }

    public void setInventory(Inventory inventory){
        this.serializedInventory = SerializableInventory.toBase64(inventory);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
