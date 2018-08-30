package net.assimilationmc.assicore.mysql;

import com.google.common.collect.Maps;
import net.assimilationmc.assicore.server.ServerPropertyReader;
import net.assimilationmc.assicore.util.D;

import java.beans.Transient;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class SQLPropertyReader extends ServerPropertyReader {

    public SQLPropertyReader(File file) {
        super(file);
    }

    @Transient
    @Override
    public Map<String, String> readSQL() {
        final Map<String, String> values = Maps.newHashMap();

        try {
            final BufferedReader reader = new
                    BufferedReader(new FileReader(getFile()));

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.replaceFirst("=", "¤");
                final String[] args = line.split("¤");
                values.put(args[0].toLowerCase(), args[1]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return values;
    }

}
