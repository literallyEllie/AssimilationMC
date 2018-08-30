package net.assimilationmc.ellie.assiuhc.backend;

import net.assimilationmc.ellie.assicore.util.FileUtil;
import net.assimilationmc.ellie.assiuhc.UHC;

/**
 * Created by Ellie on 19/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class SettingManager {

    public SettingManager(UHC uhc){

        if(!uhc.getDataFolder().exists()) FileUtil.createDirectory(uhc.getDataFolder());

    }

}
