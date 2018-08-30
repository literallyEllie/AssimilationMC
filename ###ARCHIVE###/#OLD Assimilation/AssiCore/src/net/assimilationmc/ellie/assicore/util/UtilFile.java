package net.assimilationmc.ellie.assicore.util;

import net.assimilationmc.ellie.assicore.api.AssiCore;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * Created by Ellie on 20/12/2016 for AssimilationMC.
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
public class UtilFile {

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

    public static boolean createFile(File file){
        try {
            return file.createNewFile();
        }catch(IOException e){
            AssiCore.getCore().logW("Failed to create file "+file.getName());
        }
        return false;
    }

}

