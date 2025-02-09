/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.utils;

public class TextUtils {
    public static String chatFormatChar = "\u00a7";

    public static String toChatFormatter(String s) {
        return String.valueOf(chatFormatChar) + s;
    }
}

