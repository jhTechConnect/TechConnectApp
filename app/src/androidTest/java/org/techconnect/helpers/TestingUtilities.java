package org.techconnect.helpers;

/**
 * Created by dwalsten5 on 4/29/2017.
 */

public class TestingUtilities {

    public static String getRandomString() {
        String validChars = "23456789ABCDEFGHJKLMNPQRSTWXYZabcdefghijkmnopqrstuvwxyz";
        char chars[] = new char[5];
        for (int i = 0; i < chars.length; i++) {
            int rand = (int) (Math.random() * validChars.length());
            chars[i] = validChars.charAt(rand);
        }
        return new String(chars);
    }
}
