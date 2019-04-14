package de.micromata.jira.rest.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

public class JsonUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String dbgStr(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String obj2Str(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T str2obj(String str, Class<T> tClass) {
        T t = null;
        try {
            t = mapper.readValue(str, tClass);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return t;
    }
}
