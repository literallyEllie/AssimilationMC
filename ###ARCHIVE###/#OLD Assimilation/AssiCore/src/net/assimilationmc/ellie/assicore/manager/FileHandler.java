package net.assimilationmc.ellie.assicore.manager;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;

/**
 * Created by Ellie on 21/11/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public interface FileHandler {

    YamlConfiguration defaults() throws IOException;
    void assign(boolean first) throws IOException;

}
