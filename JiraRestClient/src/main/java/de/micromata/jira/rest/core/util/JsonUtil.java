package de.micromata.jira.rest.core.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonUtil {
    private static final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    private static final Gson dbgGson = new GsonBuilder().create();

    public static String dbgStr(Object obj) {
        return dbgGson.toJson(obj);
    }

    public static String obj2Str(Object obj) {
        return gson.toJson(obj);
    }
}
