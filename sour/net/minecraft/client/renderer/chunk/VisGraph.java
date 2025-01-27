/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.chunk;

import java.util.ArrayDeque;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.client.renderer.chunk.SetVisibility;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IntegerCache;
import net.minecraft.util.math.BlockPos;
import net.wolframclient.Wolfram;
import net.wolframclient.module.modules.render.Xray;

public class VisGraph {
    private static final int DX = (int)Math.pow(16.0, 0.0);
    private static final int DZ = (int)Math.pow(16.0, 1.0);
    private static final int DY = (int)Math.pow(16.0, 2.0);
    private final BitSet bitSet = new BitSet(4096);
    private static final int[] INDEX_OF_EDGES = new int[1352];
    private int empty = 4096;

    static {
        boolean i = false;
        int j = 15;
        int k = 0;
        int l = 0;
        while (l < 16) {
            int i1 = 0;
            while (i1 < 16) {
                int j1 = 0;
                while (j1 < 16) {
                    if (l == 0 || l == 15 || i1 == 0 || i1 == 15 || j1 == 0 || j1 == 15) {
                        VisGraph.INDEX_OF_EDGES[k++] = VisGraph.getIndex(l, i1, j1);
                    }
                    ++j1;
                }
                ++i1;
            }
            ++l;
        }
    }

    public void setOpaqueCube(BlockPos pos) {
        if (Xray.getInstance().isEnabled() || Wolfram.getWolfram().moduleManager.isEnabled("cavefinder")) {
            return;
        }
        this.bitSet.set(VisGraph.getIndex(pos), true);
        --this.empty;
    }

    private static int getIndex(BlockPos pos) {
        return VisGraph.getIndex(pos.getX() & 0xF, pos.getY() & 0xF, pos.getZ() & 0xF);
    }

    private static int getIndex(int x, int y, int z) {
        return x << 0 | y << 8 | z << 4;
    }

    public SetVisibility computeVisibility() {
        SetVisibility setvisibility = new SetVisibility();
        if (4096 - this.empty < 256) {
            setvisibility.setAllVisible(true);
        } else if (this.empty == 0) {
            setvisibility.setAllVisible(false);
        } else {
            int[] nArray = INDEX_OF_EDGES;
            int n = INDEX_OF_EDGES.length;
            int n2 = 0;
            while (n2 < n) {
                int i = nArray[n2];
                if (!this.bitSet.get(i)) {
                    setvisibility.setManyVisible(this.floodFill(i));
                }
                ++n2;
            }
        }
        return setvisibility;
    }

    public Set<EnumFacing> getVisibleFacings(BlockPos pos) {
        return this.floodFill(VisGraph.getIndex(pos));
    }

    private Set<EnumFacing> floodFill(int p_178604_1_) {
        EnumSet<EnumFacing> set = EnumSet.noneOf(EnumFacing.class);
        ArrayDeque<Integer> arraydeque = new ArrayDeque<Integer>(384);
        arraydeque.add(IntegerCache.getInteger(p_178604_1_));
        this.bitSet.set(p_178604_1_, true);
        while (!arraydeque.isEmpty()) {
            int i = (Integer)arraydeque.poll();
            this.addEdges(i, set);
            EnumFacing[] enumFacingArray = EnumFacing.VALUES;
            int n = EnumFacing.VALUES.length;
            int n2 = 0;
            while (n2 < n) {
                EnumFacing enumfacing = enumFacingArray[n2];
                int j = this.getNeighborIndexAtFace(i, enumfacing);
                if (j >= 0 && !this.bitSet.get(j)) {
                    this.bitSet.set(j, true);
                    arraydeque.add(IntegerCache.getInteger(j));
                }
                ++n2;
            }
        }
        return set;
    }

    private void addEdges(int p_178610_1_, Set<EnumFacing> p_178610_2_) {
        int i = p_178610_1_ >> 0 & 0xF;
        if (i == 0) {
            p_178610_2_.add(EnumFacing.WEST);
        } else if (i == 15) {
            p_178610_2_.add(EnumFacing.EAST);
        }
        int j = p_178610_1_ >> 8 & 0xF;
        if (j == 0) {
            p_178610_2_.add(EnumFacing.DOWN);
        } else if (j == 15) {
            p_178610_2_.add(EnumFacing.UP);
        }
        int k = p_178610_1_ >> 4 & 0xF;
        if (k == 0) {
            p_178610_2_.add(EnumFacing.NORTH);
        } else if (k == 15) {
            p_178610_2_.add(EnumFacing.SOUTH);
        }
    }

    private int getNeighborIndexAtFace(int p_178603_1_, EnumFacing p_178603_2_) {
        switch (p_178603_2_) {
            case DOWN: {
                if ((p_178603_1_ >> 8 & 0xF) == 0) {
                    return -1;
                }
                return p_178603_1_ - DY;
            }
            case UP: {
                if ((p_178603_1_ >> 8 & 0xF) == 15) {
                    return -1;
                }
                return p_178603_1_ + DY;
            }
            case NORTH: {
                if ((p_178603_1_ >> 4 & 0xF) == 0) {
                    return -1;
                }
                return p_178603_1_ - DZ;
            }
            case SOUTH: {
                if ((p_178603_1_ >> 4 & 0xF) == 15) {
                    return -1;
                }
                return p_178603_1_ + DZ;
            }
            case WEST: {
                if ((p_178603_1_ >> 0 & 0xF) == 0) {
                    return -1;
                }
                return p_178603_1_ - DX;
            }
            case EAST: {
                if ((p_178603_1_ >> 0 & 0xF) == 15) {
                    return -1;
                }
                return p_178603_1_ + DX;
            }
        }
        return -1;
    }
}

