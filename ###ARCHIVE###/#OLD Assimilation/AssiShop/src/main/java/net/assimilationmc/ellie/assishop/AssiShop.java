package net.assimilationmc.ellie.assishop;

import net.assimilationmc.ellie.assicore.manager.ModuleManager;
import net.assimilationmc.ellie.assishop.backend.ShopSQLStorage;
import net.assimilationmc.ellie.assishop.command.CmdNPCShop;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Ellie on 9.7.17 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class AssiShop extends JavaPlugin {

    private static AssiShop assiShop;
    private ShopSQLStorage sqlStorage;

    private CmdNPCShop shop;

    public AssiShop(){

    }

    @Override
    public void onEnable() {
        assiShop = this;
        sqlStorage = new ShopSQLStorage();
        Bukkit.getPluginManager().registerEvents(sqlStorage, this);

        shop = new CmdNPCShop();
        ModuleManager.getModuleManager().getCommandManager().registerCommand(shop);
        Bukkit.getPluginManager().registerEvents(shop, this);
    }

    @Override
    public void onDisable() {

        if(shop != null){
            shop.getTempShops().clear();
        }

        if(sqlStorage != null){
            sqlStorage.finish();
        }

        assiShop = null;
    }

    public static AssiShop getAssiShop() {
        return assiShop;
    }

    public ShopSQLStorage getSqlStorage() {
        return sqlStorage;
    }

}
