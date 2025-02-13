/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockChorusFlower
extends Block {
    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 5);

    protected BlockChorusFlower() {
        super(Material.PLANTS, MapColor.PURPLE);
        this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, 0));
        this.setCreativeTab(CreativeTabs.DECORATIONS);
        this.setTickRandomly(true);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Items.field_190931_a;
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!this.canSurvive(worldIn, pos)) {
            worldIn.destroyBlock(pos, true);
        } else {
            int i;
            BlockPos blockpos = pos.up();
            if (worldIn.isAirBlock(blockpos) && blockpos.getY() < 256 && (i = state.getValue(AGE).intValue()) < 5 && rand.nextInt(1) == 0) {
                boolean flag = false;
                boolean flag1 = false;
                IBlockState iblockstate = worldIn.getBlockState(pos.down());
                Block block = iblockstate.getBlock();
                if (block == Blocks.END_STONE) {
                    flag = true;
                } else if (block == Blocks.CHORUS_PLANT) {
                    int j = 1;
                    int k = 0;
                    while (k < 4) {
                        Block block1 = worldIn.getBlockState(pos.down(j + 1)).getBlock();
                        if (block1 != Blocks.CHORUS_PLANT) {
                            if (block1 != Blocks.END_STONE) break;
                            flag1 = true;
                            break;
                        }
                        ++j;
                        ++k;
                    }
                    int i1 = 4;
                    if (flag1) {
                        ++i1;
                    }
                    if (j < 2 || rand.nextInt(i1) >= j) {
                        flag = true;
                    }
                } else if (iblockstate.getMaterial() == Material.AIR) {
                    flag = true;
                }
                if (flag && BlockChorusFlower.areAllNeighborsEmpty(worldIn, blockpos, null) && worldIn.isAirBlock(pos.up(2))) {
                    worldIn.setBlockState(pos, Blocks.CHORUS_PLANT.getDefaultState(), 2);
                    this.placeGrownFlower(worldIn, blockpos, i);
                } else if (i < 4) {
                    int l = rand.nextInt(4);
                    boolean flag2 = false;
                    if (flag1) {
                        ++l;
                    }
                    int j1 = 0;
                    while (j1 < l) {
                        EnumFacing enumfacing = EnumFacing.Plane.HORIZONTAL.random(rand);
                        BlockPos blockpos1 = pos.offset(enumfacing);
                        if (worldIn.isAirBlock(blockpos1) && worldIn.isAirBlock(blockpos1.down()) && BlockChorusFlower.areAllNeighborsEmpty(worldIn, blockpos1, enumfacing.getOpposite())) {
                            this.placeGrownFlower(worldIn, blockpos1, i + 1);
                            flag2 = true;
                        }
                        ++j1;
                    }
                    if (flag2) {
                        worldIn.setBlockState(pos, Blocks.CHORUS_PLANT.getDefaultState(), 2);
                    } else {
                        this.placeDeadFlower(worldIn, pos);
                    }
                } else if (i == 4) {
                    this.placeDeadFlower(worldIn, pos);
                }
            }
        }
    }

    private void placeGrownFlower(World p_185602_1_, BlockPos p_185602_2_, int p_185602_3_) {
        p_185602_1_.setBlockState(p_185602_2_, this.getDefaultState().withProperty(AGE, p_185602_3_), 2);
        p_185602_1_.playEvent(1033, p_185602_2_, 0);
    }

    private void placeDeadFlower(World p_185605_1_, BlockPos p_185605_2_) {
        p_185605_1_.setBlockState(p_185605_2_, this.getDefaultState().withProperty(AGE, 5), 2);
        p_185605_1_.playEvent(1034, p_185605_2_, 0);
    }

    private static boolean areAllNeighborsEmpty(World p_185604_0_, BlockPos p_185604_1_, EnumFacing p_185604_2_) {
        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            if (enumfacing == p_185604_2_ || p_185604_0_.isAirBlock(p_185604_1_.offset(enumfacing))) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return super.canPlaceBlockAt(worldIn, pos) && this.canSurvive(worldIn, pos);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos p_189540_5_) {
        if (!this.canSurvive(worldIn, pos)) {
            worldIn.scheduleUpdate(pos, this, 1);
        }
    }

    public boolean canSurvive(World worldIn, BlockPos pos) {
        IBlockState iblockstate = worldIn.getBlockState(pos.down());
        Block block = iblockstate.getBlock();
        if (block != Blocks.CHORUS_PLANT && block != Blocks.END_STONE) {
            if (iblockstate.getMaterial() == Material.AIR) {
                int i = 0;
                for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
                    IBlockState iblockstate1 = worldIn.getBlockState(pos.offset(enumfacing));
                    Block block1 = iblockstate1.getBlock();
                    if (block1 == Blocks.CHORUS_PLANT) {
                        ++i;
                        continue;
                    }
                    if (iblockstate1.getMaterial() == Material.AIR) continue;
                    return false;
                }
                return i == 1;
            }
            return false;
        }
        return true;
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
        super.harvestBlock(worldIn, player, pos, state, te, stack);
        BlockChorusFlower.spawnAsEntity(worldIn, pos, new ItemStack(Item.getItemFromBlock(this)));
    }

    @Override
    protected ItemStack getSilkTouchDrop(IBlockState state) {
        return ItemStack.EMPTY;
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(AGE, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(AGE);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer((Block)this, AGE);
    }

    public static void generatePlant(World worldIn, BlockPos pos, Random rand, int p_185603_3_) {
        worldIn.setBlockState(pos, Blocks.CHORUS_PLANT.getDefaultState(), 2);
        BlockChorusFlower.growTreeRecursive(worldIn, pos, rand, pos, p_185603_3_, 0);
    }

    private static void growTreeRecursive(World worldIn, BlockPos p_185601_1_, Random rand, BlockPos p_185601_3_, int p_185601_4_, int p_185601_5_) {
        int i = rand.nextInt(4) + 1;
        if (p_185601_5_ == 0) {
            ++i;
        }
        int j = 0;
        while (j < i) {
            BlockPos blockpos = p_185601_1_.up(j + 1);
            if (!BlockChorusFlower.areAllNeighborsEmpty(worldIn, blockpos, null)) {
                return;
            }
            worldIn.setBlockState(blockpos, Blocks.CHORUS_PLANT.getDefaultState(), 2);
            ++j;
        }
        boolean flag = false;
        if (p_185601_5_ < 4) {
            int l = rand.nextInt(4);
            if (p_185601_5_ == 0) {
                ++l;
            }
            int k = 0;
            while (k < l) {
                EnumFacing enumfacing = EnumFacing.Plane.HORIZONTAL.random(rand);
                BlockPos blockpos1 = p_185601_1_.up(i).offset(enumfacing);
                if (Math.abs(blockpos1.getX() - p_185601_3_.getX()) < p_185601_4_ && Math.abs(blockpos1.getZ() - p_185601_3_.getZ()) < p_185601_4_ && worldIn.isAirBlock(blockpos1) && worldIn.isAirBlock(blockpos1.down()) && BlockChorusFlower.areAllNeighborsEmpty(worldIn, blockpos1, enumfacing.getOpposite())) {
                    flag = true;
                    worldIn.setBlockState(blockpos1, Blocks.CHORUS_PLANT.getDefaultState(), 2);
                    BlockChorusFlower.growTreeRecursive(worldIn, blockpos1, rand, p_185601_3_, p_185601_4_, p_185601_5_ + 1);
                }
                ++k;
            }
        }
        if (!flag) {
            worldIn.setBlockState(p_185601_1_.up(i), Blocks.CHORUS_FLOWER.getDefaultState().withProperty(AGE, 5), 2);
        }
    }

    @Override
    public BlockFaceShape func_193383_a(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
        return BlockFaceShape.UNDEFINED;
    }
}

