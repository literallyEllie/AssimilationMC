package com.assimilation.ellie.assibungee.script;

import java.io.File;
import java.io.IOException;

/**
 * Created by Ellie on 12/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class BackupServerScript implements SHScript {

    private final File file;

    public BackupServerScript(File file){
        this.file = file;
        if(!this.file.getName().split(".")[0].equals("sh")){
            throw new IllegalArgumentException("Invalid file: "+file);
        }

    }

    @Override
    public File getLocation() {
        return this.file;
    }

    @Override
    public void run() throws IOException, InterruptedException {
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(file.toString());
        process.waitFor();
    }

}
