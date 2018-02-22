package com.bredeekmendes.greenhouse.utilities;

/**
 * Created by arthur on 2/21/18.
 */

public class StringUtils {
    public static String normalizeString(String string) {
        if (string == "" || string==null) {
            return "";
        }
        char[] chars = string.toLowerCase().toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; i++) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if (Character.isWhitespace(chars[i]) || chars[i] == '.' || chars[i] == '\'') { // You can add other chars here
                found = false;
            }
        }
        return String.valueOf(chars);
    }
}