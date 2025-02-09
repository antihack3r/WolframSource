/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.BlockVine;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenHugeTrees;

public class WorldGenMegaJungle
extends WorldGenHugeTrees {
    public WorldGenMegaJungle(boolean p_i46448_1_, int p_i46448_2_, int p_i46448_3_, IBlockState p_i46448_4_, IBlockState p_i46448_5_) {
        super(p_i46448_1_, p_i46448_2_, p_i46448_3_, p_i46448_4_, p_i46448_5_);
    }

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        int i = this.getHeight(rand);
        if (!this.ensureGrowable(worldIn, rand, position, i)) {
            return false;
        }
        this.createCrown(worldIn, position.up(i), 2);
        int j = position.getY() + i - 2 - rand.nextInt(4);
        while (j > position.getY() + i / 2) {
            float f = rand.nextFloat() * ((float)Math.PI * 2);
            int k = position.getX() + (int)(0.5f + MathHelper.cos(f) * 4.0f);
            int l = position.getZ() + (int)(0.5f + MathHelper.sin(f) * 4.0f);
            int i1 = 0;
            while (i1 < 5) {
                k = position.getX() + (int)(1.5f + MathHelper.cos(f) * (float)i1);
                l = position.getZ() + (int)(1.5f + MathHelper.sin(f) * (float)i1);
                this.setBlockAndNotifyAdequately(worldIn, new BlockPos(k, j - 3 + i1 / 2, l), this.woodMetadata);
                ++i1;
            }
            int j2 = 1 + rand.nextInt(2);
            int j1 = j;
            int k1 = j - j2;
            while (k1 <= j1) {
                int l1 = k1 - j1;
                this.growLeavesLayer(worldIn, new BlockPos(k, k1, l), 1 - l1);
                ++k1;
            }
            j -= 2 + rand.nextInt(4);
        }
        int i2 = 0;
        while (i2 < i) {
            BlockPos blockpos = position.up(i2);
            if (this.canGrowInto(worldIn.getBlockState(blockpos).getBlock())) {
                this.setBlockAndNotifyAdequately(worldIn, blockpos, this.woodMetadata);
                if (i2 > 0) {
                    this.placeVine(worldIn, rand, blockpos.west(), BlockVine.EAST);
                    this.placeVine(worldIn, rand, blockpos.north(), BlockVine.SOUTH);
                }
            }
            if (i2 < i - 1) {
                BlockPos blockpos3;
                BlockPos blockpos2;
                BlockPos blockpos1 = blockpos.east();
                if (this.canGrowInto(worldIn.getBlockState(blockpos1).getBlock())) {
                    this.setBlockAndNotifyAdequately(worldIn, blockpos1, this.woodMetadata);
                    if (i2 > 0) {
                        this.placeVine(worldIn, rand, blockpos1.east(), BlockVine.WEST);
                        this.placeVine(worldIn, rand, blockpos1.north(), BlockVine.SOUTH);
                    }
                }
                if (this.canGrowInto(worldIn.getBlockState(blockpos2 = blockpos.south().east()).getBlock())) {
                    this.setBlockAndNotifyAdequately(worldIn, blockpos2, this.woodMetadata);
                    if (i2 > 0) {
                        this.placeVine(worldIn, rand, blockpos2.east(), BlockVine.WEST);
                        this.placeVine(worldIn, rand, blockpos2.south(), BlockVine.NORTH);
                    }
                }
                if (this.canGrowInto(worldIn.getBlockState(blockpos3 = blockpos.south()).getBlock())) {
                    this.setBlockAndNotifyAdequately(worldIn, blockpos3, this.woodMetadata);
                    if (i2 > 0) {
                        this.placeVine(worldIn, rand, blockpos3.west(), BlockVine.EAST);
                        this.placeVine(worldIn, rand, blockpos3.south(), BlockVine.NORTH);
                    }
                }
            }
            ++i2;
        }
        return true;
    }

    private void placeVine(World p_181632_1_, Random p_181632_2_, BlockPos p_181632_3_, PropertyBool p_181632_4_) {
        if (p_181632_2_.nextInt(3) > 0 && p_181632_1_.isAirBlock(p_181632_3_)) {
            this.setBlockAndNotifyAdequately(p_181632_1_, p_181632_3_, Blocks.VINE.getDefaultState().withProperty(p_181632_4_, true));
        }
    }

    private void createCrown(World worldIn, BlockPos p_175930_2_, int p_175930_3_) {
        int i = 2;
        int j = -2;
        while (j <= 0) {
            this.growLeavesLayerStrict(worldIn, p_175930_2_.up(j), p_175930_3_ + 1 - j);
            ++j;
        }
    }
}

