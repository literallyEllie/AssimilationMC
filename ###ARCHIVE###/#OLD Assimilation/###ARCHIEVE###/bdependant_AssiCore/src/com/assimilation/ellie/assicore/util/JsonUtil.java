package com.assimilation.ellie.assicore.util;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by Ellie on 11/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class JsonUtil {

    private static Gson gson = new Gson();

    public static boolean array_contains(String jsonString, String query){
        return gson.fromJson(jsonString, List.class).contains(query);
    }

    public static String array_append(String jsonString, String append){
        List<String> a = gson.fromJson(jsonString, List.class);
        a.add(append);
        return gson.toJson(a);
    }

    public static String array_remove(String jsonString, String remove){
        List<String> a = gson.fromJson(jsonString, List.class);
        a.remove(remove);
        return gson.toJson(a);
    }

}
