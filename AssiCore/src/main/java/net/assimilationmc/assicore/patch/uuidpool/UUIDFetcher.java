package net.assimilationmc.assicore.patch.uuidpool;

import com.google.gson.Gson;
import net.assimilationmc.assicore.util.UtilJson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;

public class UUIDFetcher {

    private final Gson gson;

    UUIDFetcher() {
        this.gson = new Gson();
    }

    public String fetchUUID(String username) {

        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
            InputStream stream = url.openStream();
            InputStreamReader inr = new InputStreamReader(stream);
            BufferedReader reader = new BufferedReader(inr);
            String s;
            StringBuilder sb = new StringBuilder();
            while ((s = reader.readLine()) != null) {
                sb.append(s);
            }
            Map<String, String> uuidResponse = UtilJson.deserialize(gson, sb.toString());
            return uuidResponse.get("id");
        } catch (IOException ignored) {
        }

        return null;
    }

}
