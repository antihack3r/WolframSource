/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.pathfinding;

import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.NodeProcessor;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;

public class SwimNodeProcessor
extends NodeProcessor {
    @Override
    public PathPoint getStart() {
        return this.openPoint(MathHelper.floor(this.entity.getEntityBoundingBox().minX), MathHelper.floor(this.entity.getEntityBoundingBox().minY + 0.5), MathHelper.floor(this.entity.getEntityBoundingBox().minZ));
    }

    @Override
    public PathPoint getPathPointToCoords(double x, double y, double z) {
        return this.openPoint(MathHelper.floor(x - (double)(this.entity.width / 2.0f)), MathHelper.floor(y + 0.5), MathHelper.floor(z - (double)(this.entity.width / 2.0f)));
    }

    @Override
    public int findPathOptions(PathPoint[] pathOptions, PathPoint currentPoint, PathPoint targetPoint, float maxDistance) {
        int i = 0;
        EnumFacing[] enumFacingArray = EnumFacing.values();
        int n = enumFacingArray.length;
        int n2 = 0;
        while (n2 < n) {
            EnumFacing enumfacing = enumFacingArray[n2];
            PathPoint pathpoint = this.getWaterNode(currentPoint.xCoord + enumfacing.getFrontOffsetX(), currentPoint.yCoord + enumfacing.getFrontOffsetY(), currentPoint.zCoord + enumfacing.getFrontOffsetZ());
            if (pathpoint != null && !pathpoint.visited && pathpoint.distanceTo(targetPoint) < maxDistance) {
                pathOptions[i++] = pathpoint;
            }
            ++n2;
        }
        return i;
    }

    @Override
    public PathNodeType getPathNodeType(IBlockAccess blockaccessIn, int x, int y, int z, Entity entitylivingIn, int xSize, int ySize, int zSize, boolean canBreakDoorsIn, boolean canEnterDoorsIn) {
        return PathNodeType.WATER;
    }

    @Override
    public PathNodeType getPathNodeType(IBlockAccess blockaccessIn, int x, int y, int z) {
        return PathNodeType.WATER;
    }

    @Nullable
    private PathPoint getWaterNode(int p_186328_1_, int p_186328_2_, int p_186328_3_) {
        PathNodeType pathnodetype = this.isFree(p_186328_1_, p_186328_2_, p_186328_3_);
        return pathnodetype == PathNodeType.WATER ? this.openPoint(p_186328_1_, p_186328_2_, p_186328_3_) : null;
    }

    private PathNodeType isFree(int p_186327_1_, int p_186327_2_, int p_186327_3_) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        int i = p_186327_1_;
        while (i < p_186327_1_ + this.entitySizeX) {
            int j = p_186327_2_;
            while (j < p_186327_2_ + this.entitySizeY) {
                int k = p_186327_3_;
                while (k < p_186327_3_ + this.entitySizeZ) {
                    IBlockState iblockstate = this.blockaccess.getBlockState(blockpos$mutableblockpos.setPos(i, j, k));
                    if (iblockstate.getMaterial() != Material.WATER) {
                        return PathNodeType.BLOCKED;
                    }
                    ++k;
                }
                ++j;
            }
            ++i;
        }
        return PathNodeType.WATER;
    }
}

