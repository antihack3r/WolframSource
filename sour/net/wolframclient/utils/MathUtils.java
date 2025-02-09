/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.utils;

public class MathUtils {
    public static float map(float x, float prev_min, float prev_max, float new_min, float new_max) {
        return (x - prev_min) / (prev_max - prev_min) * (new_max - new_min) + new_min;
    }

    public static boolean contains(float x, float y, float minX, float minY, float maxX, float maxY) {
        return x > minX && x < maxX && y > minY && y < maxY;
    }
}

