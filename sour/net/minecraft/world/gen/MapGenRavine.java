/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenBase;

public class MapGenRavine
extends MapGenBase {
    protected static final IBlockState FLOWING_LAVA = Blocks.FLOWING_LAVA.getDefaultState();
    protected static final IBlockState AIR = Blocks.AIR.getDefaultState();
    private final float[] rs = new float[1024];

    protected void addTunnel(long p_180707_1_, int p_180707_3_, int p_180707_4_, ChunkPrimer p_180707_5_, double p_180707_6_, double p_180707_8_, double p_180707_10_, float p_180707_12_, float p_180707_13_, float p_180707_14_, int p_180707_15_, int p_180707_16_, double p_180707_17_) {
        Random random = new Random(p_180707_1_);
        double d0 = p_180707_3_ * 16 + 8;
        double d1 = p_180707_4_ * 16 + 8;
        float f = 0.0f;
        float f1 = 0.0f;
        if (p_180707_16_ <= 0) {
            int i = this.range * 16 - 16;
            p_180707_16_ = i - random.nextInt(i / 4);
        }
        boolean flag1 = false;
        if (p_180707_15_ == -1) {
            p_180707_15_ = p_180707_16_ / 2;
            flag1 = true;
        }
        float f2 = 1.0f;
        int j = 0;
        while (j < 256) {
            if (j == 0 || random.nextInt(3) == 0) {
                f2 = 1.0f + random.nextFloat() * random.nextFloat();
            }
            this.rs[j] = f2 * f2;
            ++j;
        }
        while (p_180707_15_ < p_180707_16_) {
            double d9 = 1.5 + (double)(MathHelper.sin((float)p_180707_15_ * (float)Math.PI / (float)p_180707_16_) * p_180707_12_);
            double d2 = d9 * p_180707_17_;
            d9 *= (double)random.nextFloat() * 0.25 + 0.75;
            d2 *= (double)random.nextFloat() * 0.25 + 0.75;
            float f3 = MathHelper.cos(p_180707_14_);
            float f4 = MathHelper.sin(p_180707_14_);
            p_180707_6_ += (double)(MathHelper.cos(p_180707_13_) * f3);
            p_180707_8_ += (double)f4;
            p_180707_10_ += (double)(MathHelper.sin(p_180707_13_) * f3);
            p_180707_14_ *= 0.7f;
            p_180707_14_ += f1 * 0.05f;
            p_180707_13_ += f * 0.05f;
            f1 *= 0.8f;
            f *= 0.5f;
            f1 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0f;
            f += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0f;
            if (flag1 || random.nextInt(4) != 0) {
                double d3 = p_180707_6_ - d0;
                double d4 = p_180707_10_ - d1;
                double d5 = p_180707_16_ - p_180707_15_;
                double d6 = p_180707_12_ + 2.0f + 16.0f;
                if (d3 * d3 + d4 * d4 - d5 * d5 > d6 * d6) {
                    return;
                }
                if (p_180707_6_ >= d0 - 16.0 - d9 * 2.0 && p_180707_10_ >= d1 - 16.0 - d9 * 2.0 && p_180707_6_ <= d0 + 16.0 + d9 * 2.0 && p_180707_10_ <= d1 + 16.0 + d9 * 2.0) {
                    int k2 = MathHelper.floor(p_180707_6_ - d9) - p_180707_3_ * 16 - 1;
                    int k = MathHelper.floor(p_180707_6_ + d9) - p_180707_3_ * 16 + 1;
                    int l2 = MathHelper.floor(p_180707_8_ - d2) - 1;
                    int l = MathHelper.floor(p_180707_8_ + d2) + 1;
                    int i3 = MathHelper.floor(p_180707_10_ - d9) - p_180707_4_ * 16 - 1;
                    int i1 = MathHelper.floor(p_180707_10_ + d9) - p_180707_4_ * 16 + 1;
                    if (k2 < 0) {
                        k2 = 0;
                    }
                    if (k > 16) {
                        k = 16;
                    }
                    if (l2 < 1) {
                        l2 = 1;
                    }
                    if (l > 248) {
                        l = 248;
                    }
                    if (i3 < 0) {
                        i3 = 0;
                    }
                    if (i1 > 16) {
                        i1 = 16;
                    }
                    boolean flag2 = false;
                    int j1 = k2;
                    while (!flag2 && j1 < k) {
                        int k1 = i3;
                        while (!flag2 && k1 < i1) {
                            int l1 = l + 1;
                            while (!flag2 && l1 >= l2 - 1) {
                                if (l1 >= 0 && l1 < 256) {
                                    IBlockState iblockstate = p_180707_5_.getBlockState(j1, l1, k1);
                                    if (iblockstate.getBlock() == Blocks.FLOWING_WATER || iblockstate.getBlock() == Blocks.WATER) {
                                        flag2 = true;
                                    }
                                    if (l1 != l2 - 1 && j1 != k2 && j1 != k - 1 && k1 != i3 && k1 != i1 - 1) {
                                        l1 = l2;
                                    }
                                }
                                --l1;
                            }
                            ++k1;
                        }
                        ++j1;
                    }
                    if (!flag2) {
                        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
                        int j3 = k2;
                        while (j3 < k) {
                            double d10 = ((double)(j3 + p_180707_3_ * 16) + 0.5 - p_180707_6_) / d9;
                            int i2 = i3;
                            while (i2 < i1) {
                                double d7 = ((double)(i2 + p_180707_4_ * 16) + 0.5 - p_180707_10_) / d9;
                                boolean flag = false;
                                if (d10 * d10 + d7 * d7 < 1.0) {
                                    int j2 = l;
                                    while (j2 > l2) {
                                        double d8 = ((double)(j2 - 1) + 0.5 - p_180707_8_) / d2;
                                        if ((d10 * d10 + d7 * d7) * (double)this.rs[j2 - 1] + d8 * d8 / 6.0 < 1.0) {
                                            IBlockState iblockstate1 = p_180707_5_.getBlockState(j3, j2, i2);
                                            if (iblockstate1.getBlock() == Blocks.GRASS) {
                                                flag = true;
                                            }
                                            if (iblockstate1.getBlock() == Blocks.STONE || iblockstate1.getBlock() == Blocks.DIRT || iblockstate1.getBlock() == Blocks.GRASS) {
                                                if (j2 - 1 < 10) {
                                                    p_180707_5_.setBlockState(j3, j2, i2, FLOWING_LAVA);
                                                } else {
                                                    p_180707_5_.setBlockState(j3, j2, i2, AIR);
                                                    if (flag && p_180707_5_.getBlockState(j3, j2 - 1, i2).getBlock() == Blocks.DIRT) {
                                                        blockpos$mutableblockpos.setPos(j3 + p_180707_3_ * 16, 0, i2 + p_180707_4_ * 16);
                                                        p_180707_5_.setBlockState(j3, j2 - 1, i2, this.worldObj.getBiome((BlockPos)blockpos$mutableblockpos).topBlock);
                                                    }
                                                }
                                            }
                                        }
                                        --j2;
                                    }
                                }
                                ++i2;
                            }
                            ++j3;
                        }
                        if (flag1) break;
                    }
                }
            }
            ++p_180707_15_;
        }
    }

    @Override
    protected void recursiveGenerate(World worldIn, int chunkX, int chunkZ, int p_180701_4_, int p_180701_5_, ChunkPrimer chunkPrimerIn) {
        if (this.rand.nextInt(50) == 0) {
            double d0 = chunkX * 16 + this.rand.nextInt(16);
            double d1 = this.rand.nextInt(this.rand.nextInt(40) + 8) + 20;
            double d2 = chunkZ * 16 + this.rand.nextInt(16);
            boolean i = true;
            int j = 0;
            while (j < 1) {
                float f = this.rand.nextFloat() * ((float)Math.PI * 2);
                float f1 = (this.rand.nextFloat() - 0.5f) * 2.0f / 8.0f;
                float f2 = (this.rand.nextFloat() * 2.0f + this.rand.nextFloat()) * 2.0f;
                this.addTunnel(this.rand.nextLong(), p_180701_4_, p_180701_5_, chunkPrimerIn, d0, d1, d2, f2, f, f1, 0, 0, 3.0);
                ++j;
            }
        }
    }
}
