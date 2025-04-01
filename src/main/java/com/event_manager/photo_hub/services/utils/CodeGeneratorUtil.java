package com.event_manager.photo_hub.services.utils;

import java.security.SecureRandom;

public class CodeGeneratorUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    private CodeGeneratorUtil() {
    }

    public static String generateRandomCode(int length) {
        StringBuilder codeBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            codeBuilder.append(RANDOM.nextInt(10));
        }
        return codeBuilder.toString();
    }
}
