/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.utils;

public final class Logger {
    public void info(String msg) {
        System.out.println("[WOLFRAM]: " + msg);
    }

    public void debug(String msg) {
        System.out.println("[WOLFRAM] [DEBUG]: " + msg);
    }

    public void warn(String msg) {
        System.out.println("[WOLFRAM] [WARNING]: " + msg);
    }

    public void error(String msg) {
        System.err.println("[WOLFRAM] [ERROR]: " + msg);
    }
}

