/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.utils;

import net.minecraft.util.EnumFacing;

public class SwitchEnumFacing {
    public static final int[] values = new int[EnumFacing.values().length];

    static {
        try {
            SwitchEnumFacing.values[EnumFacing.NORTH.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            SwitchEnumFacing.values[EnumFacing.SOUTH.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            SwitchEnumFacing.values[EnumFacing.WEST.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            SwitchEnumFacing.values[EnumFacing.EAST.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}

