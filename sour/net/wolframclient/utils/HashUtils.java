/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.utils;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class HashUtils {
    private static final SecureRandom random = new SecureRandom();

    public static String sha256(String s) {
        try {
            MessageDigest mDigest = MessageDigest.getInstance("SHA-256");
            byte[] shaByteArr = mDigest.digest(s.getBytes(Charset.forName("UTF-8")));
            StringBuilder hexStrBuilder = new StringBuilder();
            byte[] byArray = shaByteArr;
            int n = shaByteArr.length;
            int n2 = 0;
            while (n2 < n) {
                byte element = byArray[n2];
                hexStrBuilder.append(String.format("%02x", element));
                ++n2;
            }
            return hexStrBuilder.toString();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String nextRandomString() {
        return new BigInteger(130, random).toString(32);
    }
}

