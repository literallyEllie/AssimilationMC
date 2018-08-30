package net.assimilationmc.ellie.assicore.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Ellie on 11/12/2016 for AssimilationMC.
 * <p>
 * Copyright 2017 Ellie
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class UtilJson {

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
