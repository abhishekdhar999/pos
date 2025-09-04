package org.example.utils;

public class StringUtil {
    
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
    
    public static String trim(String str) {
        return str == null ? null : str.trim();
    }
    
    public static String toUpperCase(String str) {
        return str == null ? null : str.toUpperCase();
    }
    
    public static String toLowerCase(String str) {
        return str == null ? null : str.toLowerCase();
    }


}

