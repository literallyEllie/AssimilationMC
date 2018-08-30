package net.assimilationmc.assicore.web;

import com.google.common.collect.Maps;
import net.assimilationmc.assicore.server.ServerPropertyReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class WebDataPropertyReader extends ServerPropertyReader {

    public WebDataPropertyReader(File file) {
        super(file);
    }

    @Override
    public WebServerData readWebData() {
        Map<String, String> values = Maps.newHashMap();
        try {
            BufferedReader reader = new
                    BufferedReader(new FileReader(getFile()));

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.replace("\n", "");
                values.put(line.split("=")[0], line.split("=")[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new WebServerData(values.get("ADDRESS"), Integer.valueOf(values.get("PORT")), values.get("TOKEN"));
    }
}
