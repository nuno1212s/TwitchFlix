package com.twitchflix.applicationclient.authentication;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class PasswordHandler {

    private static final char[] charSet = "123456789abcdefghijklmnopqrstuvyxwzABCDEFGHIJKLMNOPQRSTUVYXWZ".toCharArray();

    private static final Random random = new Random();

    public static String hashPassword(String password, String salt) {

        try {
            MessageDigest instance = MessageDigest.getInstance("SHA-256");

            return new String(instance.digest((password + salt).getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getSalt(int size) {

        StringBuilder builder = new StringBuilder("");

        for (int i = 0; i < size; i++) {

            builder.append(charSet[random.nextInt(charSet.length)]);

        }

        return builder.toString();
    }

}
