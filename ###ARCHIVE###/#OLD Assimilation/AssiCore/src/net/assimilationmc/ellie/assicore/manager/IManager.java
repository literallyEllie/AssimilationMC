package net.assimilationmc.ellie.assicore.manager;

/**
 * Created by Ellie on 19/11/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public interface IManager {

    String getModuleID();
    boolean load();
    boolean unload();

}
