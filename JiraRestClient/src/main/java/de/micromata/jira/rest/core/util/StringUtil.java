package de.micromata.jira.rest.core.util;

public class StringUtil {
    public static String filterSpecialChar(String src) {
        return filterSpecialChar(src,"");
    }
    public static String filterSpecialChar(String src,String replacement) {
        if (src == null) {
            return null;
        }
        return src.trim().replaceAll("[^a-zA-Z0-9 ]", replacement);
    }
}
