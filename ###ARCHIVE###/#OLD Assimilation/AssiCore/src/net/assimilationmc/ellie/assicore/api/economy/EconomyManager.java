package net.assimilationmc.ellie.assicore.api.economy;

import net.assimilationmc.ellie.assicore.manager.IManager;

/**
 * Created by Ellie on 22/01/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class EconomyManager implements IManager {

    private Economy economy;

    public EconomyManager(Economy economy){
        this.economy = economy;
    }

    @Override
    public boolean load() {
        return true;
    }

    @Override
    public boolean unload() {
        return true;
    }

    @Override
    public String getModuleID() {
        return "economy";
    }

    public Economy getEconomy() {
        return economy;
    }

    public void setEconomy(Economy economy) {
        this.economy = economy;
    }

}
