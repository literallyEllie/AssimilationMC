package com.assimilation.ellie.assibungee.script;

import java.io.File;
import java.io.IOException;

/**
 * Created by Ellie on 12/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public interface SHScript {

    File getLocation();
    void run() throws IOException, InterruptedException;
}
