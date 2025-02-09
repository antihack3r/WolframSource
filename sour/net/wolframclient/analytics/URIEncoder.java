/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.analytics;

public class URIEncoder {
    private static String mark = "-_.!~*'()\"";

    public static String encodeURI(String argString) {
        char[] chars;
        StringBuffer uri = new StringBuffer();
        char[] cArray = chars = argString.toCharArray();
        int n = chars.length;
        int n2 = 0;
        while (n2 < n) {
            char c = cArray[n2];
            if (c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || mark.indexOf(c) != -1) {
                uri.append(c);
            } else {
                uri.append("%");
                uri.append(Integer.toHexString(c));
            }
            ++n2;
        }
        return uri.toString();
    }
}

