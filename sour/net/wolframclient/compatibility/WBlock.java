/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.compatibility;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.wolframclient.compatibility.WMinecraft;

public final class WBlock {
    public static IBlockState getState(BlockPos pos) {
        return WMinecraft.getWorld().getBlockState(pos);
    }

    public static Block getBlock(BlockPos pos) {
        return WBlock.getState(pos).getBlock();
    }

    public static int getId(BlockPos pos) {
        return Block.getIdFromBlock(WBlock.getBlock(pos));
    }

    public static String getName(Block block) {
        return "" + Block.REGISTRY.getNameForObject(block);
    }

    public static Material getMaterial(BlockPos pos) {
        return WBlock.getState(pos).getMaterial();
    }

    public static AxisAlignedBB getBoundingBox(BlockPos pos) {
        return WBlock.getState(pos).getBoundingBox(WMinecraft.getWorld(), pos).offset(pos);
    }

    public static boolean canBeClicked(BlockPos pos) {
        return WBlock.getBlock(pos).canCollideCheck(WBlock.getState(pos), false);
    }

    public static float getHardness(BlockPos pos) {
        return WBlock.getState(pos).getPlayerRelativeBlockHardness(WMinecraft.getPlayer(), WMinecraft.getWorld(), pos);
    }

    public static boolean isFullyOpaque(BlockPos pos) {
        return WBlock.getState(pos).isFullyOpaque();
    }
}

