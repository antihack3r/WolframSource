/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.chunk.storage;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.src.Reflector;
import net.minecraft.world.chunk.BlockStateContainer;
import net.minecraft.world.chunk.NibbleArray;

public class ExtendedBlockStorage {
    private final int yBase;
    private int blockRefCount;
    private int tickRefCount;
    private final BlockStateContainer data;
    private NibbleArray blocklightArray;
    private NibbleArray skylightArray;

    public ExtendedBlockStorage(int y, boolean storeSkylight) {
        this.yBase = y;
        this.data = new BlockStateContainer();
        this.blocklightArray = new NibbleArray();
        if (storeSkylight) {
            this.skylightArray = new NibbleArray();
        }
    }

    public IBlockState get(int x, int y, int z) {
        return this.data.get(x, y, z);
    }

    public void set(int x, int y, int z, IBlockState state) {
        if (Reflector.IExtendedBlockState.isInstance(state)) {
            state = (IBlockState)Reflector.call(state, Reflector.IExtendedBlockState_getClean, new Object[0]);
        }
        IBlockState iblockstate = this.get(x, y, z);
        Block block = iblockstate.getBlock();
        Block block1 = state.getBlock();
        if (block != Blocks.AIR) {
            --this.blockRefCount;
            if (block.getTickRandomly()) {
                --this.tickRefCount;
            }
        }
        if (block1 != Blocks.AIR) {
            ++this.blockRefCount;
            if (block1.getTickRandomly()) {
                ++this.tickRefCount;
            }
        }
        this.data.set(x, y, z, state);
    }

    public boolean isEmpty() {
        return this.blockRefCount == 0;
    }

    public boolean getNeedsRandomTick() {
        return this.tickRefCount > 0;
    }

    public int getYLocation() {
        return this.yBase;
    }

    public void setExtSkylightValue(int x, int y, int z, int value) {
        this.skylightArray.set(x, y, z, value);
    }

    public int getExtSkylightValue(int x, int y, int z) {
        return this.skylightArray.get(x, y, z);
    }

    public void setExtBlocklightValue(int x, int y, int z, int value) {
        this.blocklightArray.set(x, y, z, value);
    }

    public int getExtBlocklightValue(int x, int y, int z) {
        return this.blocklightArray.get(x, y, z);
    }

    public void removeInvalidBlocks() {
        IBlockState iblockstate = Blocks.AIR.getDefaultState();
        int i = 0;
        int j = 0;
        int k = 0;
        while (k < 16) {
            int l = 0;
            while (l < 16) {
                int i1 = 0;
                while (i1 < 16) {
                    IBlockState iblockstate1 = this.data.get(i1, k, l);
                    if (iblockstate1 != iblockstate) {
                        ++i;
                        Block block = iblockstate1.getBlock();
                        if (block.getTickRandomly()) {
                            ++j;
                        }
                    }
                    ++i1;
                }
                ++l;
            }
            ++k;
        }
        this.blockRefCount = i;
        this.tickRefCount = j;
    }

    public BlockStateContainer getData() {
        return this.data;
    }

    public NibbleArray getBlocklightArray() {
        return this.blocklightArray;
    }

    public NibbleArray getSkylightArray() {
        return this.skylightArray;
    }

    public void setBlocklightArray(NibbleArray newBlocklightArray) {
        this.blocklightArray = newBlocklightArray;
    }

    public void setSkylightArray(NibbleArray newSkylightArray) {
        this.skylightArray = newSkylightArray;
    }
}

