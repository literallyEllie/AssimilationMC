package net.assimilationmc.ellie.assicore.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Ellie on 11/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class JsonUtil {

    private static Gson gson = new Gson();

    public static <T> T from_gson(String string){
        Type token = new TypeToken<T>(){}.getType();
        return gson.fromJson(string, token);
    }

    public static boolean array_contains(String jsonString, String query){
        return gson.fromJson(jsonString, List.class).contains(query);
    }

    public static <T> String array_append(String jsonString, T append){
        Type token = new TypeToken<T>(){}.getType();
        List<T> a = gson.fromJson(jsonString, token);
        a.add(append);
        return gson.toJson(a);
    }

    public static <T> String array_remove(String jsonString, T remove){
        Type token = new TypeToken<T>(){}.getType();
        List<T> a = gson.fromJson(jsonString, token);
        a.remove(remove);
        return gson.toJson(a);
    }

}
