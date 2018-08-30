package net.assimilationmc.assibungee.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class UtilJson {

    /**
     * De-serialize a JSON string to its request type.
     *
     * @param gson       A GSON instance.
     * @param jsonString The JSON string to deserialize.
     * @param <T>        The type to deserialize to.
     * @return The deserialize-d object of T.
     */
    public static <T> T deserialize(Gson gson, String jsonString) {
        return deserialize(gson, new TypeToken<T>() {
        }, jsonString);
    }

    public static <T> T deserialize(Gson gson, TypeToken<T> typeToken, String jsonString) {
        return gson.fromJson(jsonString, typeToken.getType());
    }


}
