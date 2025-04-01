package com.event_manager.photo_hub.services.utils;

public class StringUtil {

    private StringUtil() {
    }

    public static String normalizeFolderName(String input) {
        if (input == null) {
            return "";
        }
        return input.trim().toLowerCase().replaceAll("\\s+", "_");
    }
}
