package com.gabrielhd.claimcore.utils;

public class Utils {

    public static boolean parseBoolean(String s) {
        return s.equalsIgnoreCase("yes") || s.equalsIgnoreCase("true") || s.equalsIgnoreCase("si");
    }

    public static boolean isNotInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            return true;
        }
        return false;
    }
}
