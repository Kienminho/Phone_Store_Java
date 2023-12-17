package com.tdtu.phone_store_java.Common;

import java.security.SecureRandom;

public class Utils {
    static SecureRandom random = new SecureRandom();
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    public static String GetUserNameByEmail(String email) {
        int index = email.indexOf("@");
        return email.substring(0,index);
    }

    //random token
    public static String GenerateRandomToken(int length) {
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            token.append(CHARACTERS.charAt(randomIndex));
        }
        return token.toString();
    }
}
