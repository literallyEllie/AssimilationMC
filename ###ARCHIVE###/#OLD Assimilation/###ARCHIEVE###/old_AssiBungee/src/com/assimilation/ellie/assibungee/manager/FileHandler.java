package com.assimilation.ellie.assibungee.manager;

import net.md_5.bungee.config.Configuration;

import java.io.IOException;

/**
 * Created by Ellie on 21/11/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public interface FileHandler {

    Configuration defaults() throws IOException;
    void assign(boolean first) throws IOException;

}
