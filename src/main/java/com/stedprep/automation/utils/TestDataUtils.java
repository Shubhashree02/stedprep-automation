package com.stedprep.automation.utils;

import java.util.UUID;

public class TestDataUtils {

    public static String generateFirstName() {
        return randomAlphabeticString(6);
    }

    public static String generateLastName() {
        return randomAlphabeticString(6);
    }

    private static String randomAlphabeticString(int length) {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * alphabet.length());
            sb.append(alphabet.charAt(index));
        }
        return sb.toString();
    }


    public static String generateEmail() {
        return "parent_" + System.currentTimeMillis() + "@yopmail.com";
    }

    public static String generatePhone() {
        return "947" + (int)(1000000 + Math.random() * 9000000);
    }

    public static String generatePassword(){
        return "Test@1234";
    }
}
