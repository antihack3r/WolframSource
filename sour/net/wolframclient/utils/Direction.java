/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.utils;

public class Direction {
    public static final int[] offsetX;
    public static final int[] offsetZ;
    public static final String[] directions;
    public static final int[] directionToFacing;
    public static final int[] facingToDirection;
    public static final int[] rotateOpposite;
    public static final int[] rotateRight;
    public static final int[] rotateLeft;
    public static final int[][] bedDirection;

    static {
        int[] nArray = new int[4];
        nArray[1] = -1;
        nArray[3] = 1;
        offsetX = nArray;
        int[] nArray2 = new int[4];
        nArray2[0] = 1;
        nArray2[2] = -1;
        offsetZ = nArray2;
        directions = new String[]{"SOUTH", "WEST", "NORTH", "EAST"};
        directionToFacing = new int[]{3, 4, 2, 5};
        int[] nArray3 = new int[6];
        nArray3[0] = -1;
        nArray3[1] = -1;
        nArray3[2] = 2;
        nArray3[4] = 1;
        nArray3[5] = 3;
        facingToDirection = nArray3;
        int[] nArray4 = new int[4];
        nArray4[0] = 2;
        nArray4[1] = 3;
        nArray4[3] = 1;
        rotateOpposite = nArray4;
        int[] nArray5 = new int[4];
        nArray5[0] = 1;
        nArray5[1] = 2;
        nArray5[2] = 3;
        rotateRight = nArray5;
        int[] nArray6 = new int[4];
        nArray6[0] = 3;
        nArray6[2] = 1;
        nArray6[3] = 2;
        rotateLeft = nArray6;
        int[][] nArrayArray = new int[4][];
        int[] nArray7 = new int[6];
        nArray7[0] = 1;
        nArray7[2] = 3;
        nArray7[3] = 2;
        nArray7[4] = 5;
        nArray7[5] = 4;
        nArrayArray[0] = nArray7;
        int[] nArray8 = new int[6];
        nArray8[0] = 1;
        nArray8[2] = 5;
        nArray8[3] = 4;
        nArray8[4] = 2;
        nArray8[5] = 3;
        nArrayArray[1] = nArray8;
        int[] nArray9 = new int[6];
        nArray9[0] = 1;
        nArray9[2] = 2;
        nArray9[3] = 3;
        nArray9[4] = 4;
        nArray9[5] = 5;
        nArrayArray[2] = nArray9;
        int[] nArray10 = new int[6];
        nArray10[0] = 1;
        nArray10[2] = 4;
        nArray10[3] = 5;
        nArray10[4] = 3;
        nArray10[5] = 2;
        nArrayArray[3] = nArray10;
        bedDirection = nArrayArray;
    }
}

