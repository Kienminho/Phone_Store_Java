package com.tdtu.phone_store_java.Common;

import java.security.SecureRandom;
import org.mindrot.jbcrypt.BCrypt;

public class Utils {

    public static String userNameLogin = "";
    public static Long idUserLogin = 0L;
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

    //hash password
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    // Verify a password
    public static boolean verifyPassword(String password, String hashedPassword) {
        // Check if the provided password matches the hashed password
        return BCrypt.checkpw(password, hashedPassword);
    }
}
