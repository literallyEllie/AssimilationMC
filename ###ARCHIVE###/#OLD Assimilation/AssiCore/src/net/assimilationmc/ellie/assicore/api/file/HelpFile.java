package net.assimilationmc.ellie.assicore.api.file;

import net.assimilationmc.ellie.assicore.api.AssiCore;
import net.assimilationmc.ellie.assicore.manager.FileHandler;
import net.assimilationmc.ellie.assicore.util.Util;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by Ellie on 13/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class HelpFile implements FileHandler {

    private File file;
    private LinkedList<String> help;
    private BufferedReader bufferedReader;

    public HelpFile(){
        this.file = new File(AssiCore.getCore().getAssiPlugin().getDataFolder(), "help.txt");
        this.help = new LinkedList<>();
        boolean first = false;
        try {
            if(!file.exists()){
                file.createNewFile();
                first = true;
            }
            assign(first);
        }catch(IOException e){
            e.printStackTrace();
            AssiCore.getCore().logE("Failed to do config: "+e.getLocalizedMessage());
        }
    }

    @Override
    public void assign(boolean first) throws IOException {

        openStream();

        String line = bufferedReader.readLine();

        while(line != null){
            help.add(Util.color(line.replace("%prefix%", Util.prefix())));
            line = bufferedReader.readLine();
        }

        closeStream();
    }

    public LinkedList<String> getHelp() {
        return help;
    }

    private BufferedReader openStream(){
        if(this.bufferedReader != null){
            try {
                this.bufferedReader.close();
            }catch (IOException e){
                e.printStackTrace();
                AssiCore.getCore().logE("Failed to safely close stream "+e.getLocalizedMessage());
            }finally {
                this.bufferedReader = null;
            }
        }
        try {
            this.bufferedReader = new BufferedReader(new FileReader(file));
        }catch (IOException e){
            e.printStackTrace();
            AssiCore.getCore().logE("Failed to open stream "+e.getLocalizedMessage());
        }
        return this.bufferedReader;
    }

    private void closeStream(){
        if(this.bufferedReader != null){
            try {
                this.bufferedReader.close();
            }catch (IOException e){
                e.printStackTrace();
                AssiCore.getCore().logE("Failed to safely close stream "+e.getLocalizedMessage());
            }finally {
                this.bufferedReader = null;
            }
        }
    }

    @Override
    public YamlConfiguration defaults() throws IOException {
        return null;
    }
}
