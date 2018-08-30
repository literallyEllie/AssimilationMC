package com.assimilation.ellie.assicore.util;

import com.assimilation.ellie.assicore.api.AssiCore;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * Created by Ellie on 20/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class FileUtil {

    public static void saveFile(YamlConfiguration configuration, File file){
        try{
            configuration.save(file);
        }catch(IOException e){
            e.printStackTrace();
            AssiCore.getCore().logW("Failed to save file "+file.getName());
        }
    }

    public static void createDirectory(File file){
        file.mkdirs();
    }

    public static void deleteFile(File file){
        file.delete();
    }

    public static void createFile(File file){
        try {
            file.createNewFile();
        }catch(IOException e){
            AssiCore.getCore().logW("Failed to create file "+file.getName());
        }
    }

}

