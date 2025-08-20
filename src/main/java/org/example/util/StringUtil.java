package org.example.util;

public class StringUtil {

    public static boolean isEmpty(String s) {

        return s == null || s.trim().length() == 0;
    }

    public static String toLowerCasee(String s){
        return s == null ? null : s.trim().toLowerCase();
    }
}
