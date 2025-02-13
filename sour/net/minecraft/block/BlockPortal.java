/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.LoadingCache
 *  javax.annotation.Nullable
 */
package net.minecraft.block;

import com.google.common.cache.LoadingCache;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakable;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPortal
extends BlockBreakable {
    public static final PropertyEnum<EnumFacing.Axis> AXIS = PropertyEnum.create((String)"axis", EnumFacing.Axis.class, (Enum[])new EnumFacing.Axis[]{EnumFacing.Axis.X, EnumFacing.Axis.Z});
    protected static final AxisAlignedBB X_AABB = new AxisAlignedBB(0.0, 0.0, 0.375, 1.0, 1.0, 0.625);
    protected static final AxisAlignedBB Z_AABB = new AxisAlignedBB(0.375, 0.0, 0.0, 0.625, 1.0, 1.0);
    protected static final AxisAlignedBB Y_AABB = new AxisAlignedBB(0.375, 0.0, 0.375, 0.625, 1.0, 0.625);

    public BlockPortal() {
        super(Material.PORTAL, false);
        this.setDefaultState(this.blockState.getBaseState().withProperty(AXIS, EnumFacing.Axis.X));
        this.setTickRandomly(true);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        switch (state.getValue(AXIS)) {
            case X: {
                return X_AABB;
            }
            default: {
                return Y_AABB;
            }
            case Z: 
        }
        return Z_AABB;
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        super.updateTick(worldIn, pos, state, rand);
        if (worldIn.provider.isSurfaceWorld() && worldIn.getGameRules().getBoolean("doMobSpawning") && rand.nextInt(2000) < worldIn.getDifficulty().getDifficultyId()) {
            Entity entity;
            int i = pos.getY();
            BlockPos blockpos = pos;
            while (!worldIn.getBlockState(blockpos).isFullyOpaque() && blockpos.getY() > 0) {
                blockpos = blockpos.down();
            }
            if (i > 0 && !worldIn.getBlockState(blockpos.up()).isNormalCube() && (entity = ItemMonsterPlacer.spawnCreature(worldIn, EntityList.func_191306_a(EntityPigZombie.class), (double)blockpos.getX() + 0.5, (double)blockpos.getY() + 1.1, (double)blockpos.getZ() + 0.5)) != null) {
                entity.timeUntilPortal = entity.getPortalCooldown();
            }
        }
    }

    @Override
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return NULL_AABB;
    }

    public static int getMetaForAxis(EnumFacing.Axis axis) {
        if (axis == EnumFacing.Axis.X) {
            return 1;
        }
        return axis == EnumFacing.Axis.Z ? 2 : 0;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    public boolean trySpawnPortal(World worldIn, BlockPos pos) {
        Size blockportal$size = new Size(worldIn, pos, EnumFacing.Axis.X);
        if (blockportal$size.isValid() && blockportal$size.portalBlockCount == 0) {
            blockportal$size.placePortalBlocks();
            return true;
        }
        Size blockportal$size1 = new Size(worldIn, pos, EnumFacing.Axis.Z);
        if (blockportal$size1.isValid() && blockportal$size1.portalBlockCount == 0) {
            blockportal$size1.placePortalBlocks();
            return true;
        }
        return false;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos p_189540_5_) {
        Size blockportal$size1;
        EnumFacing.Axis enumfacing$axis = state.getValue(AXIS);
        if (enumfacing$axis == EnumFacing.Axis.X) {
            Size blockportal$size = new Size(worldIn, pos, EnumFacing.Axis.X);
            if (!blockportal$size.isValid() || blockportal$size.portalBlockCount < blockportal$size.width * blockportal$size.height) {
                worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
            }
        } else if (!(enumfacing$axis != EnumFacing.Axis.Z || (blockportal$size1 = new Size(worldIn, pos, EnumFacing.Axis.Z)).isValid() && blockportal$size1.portalBlockCount >= blockportal$size1.width * blockportal$size1.height)) {
            worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
        }
    }

    @Override
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        boolean flag5;
        pos = pos.offset(side);
        EnumFacing.Axis enumfacing$axis = null;
        if (blockState.getBlock() == this) {
            enumfacing$axis = blockState.getValue(AXIS);
            if (enumfacing$axis == null) {
                return false;
            }
            if (enumfacing$axis == EnumFacing.Axis.Z && side != EnumFacing.EAST && side != EnumFacing.WEST) {
                return false;
            }
            if (enumfacing$axis == EnumFacing.Axis.X && side != EnumFacing.SOUTH && side != EnumFacing.NORTH) {
                return false;
            }
        }
        boolean flag = blockAccess.getBlockState(pos.west()).getBlock() == this && blockAccess.getBlockState(pos.west(2)).getBlock() != this;
        boolean flag1 = blockAccess.getBlockState(pos.east()).getBlock() == this && blockAccess.getBlockState(pos.east(2)).getBlock() != this;
        boolean flag2 = blockAccess.getBlockState(pos.north()).getBlock() == this && blockAccess.getBlockState(pos.north(2)).getBlock() != this;
        boolean flag3 = blockAccess.getBlockState(pos.south()).getBlock() == this && blockAccess.getBlockState(pos.south(2)).getBlock() != this;
        boolean flag4 = flag || flag1 || enumfacing$axis == EnumFacing.Axis.X;
        boolean bl = flag5 = flag2 || flag3 || enumfacing$axis == EnumFacing.Axis.Z;
        if (flag4 && side == EnumFacing.WEST) {
            return true;
        }
        if (flag4 && side == EnumFacing.EAST) {
            return true;
        }
        if (flag5 && side == EnumFacing.NORTH) {
            return true;
        }
        return flag5 && side == EnumFacing.SOUTH;
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (!entityIn.isRiding() && !entityIn.isBeingRidden() && entityIn.isNonBoss()) {
            entityIn.setPortal(pos);
        }
    }

    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (rand.nextInt(100) == 0) {
            worldIn.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.BLOCKS, 0.5f, rand.nextFloat() * 0.4f + 0.8f, false);
        }
        int i = 0;
        while (i < 4) {
            double d0 = (float)pos.getX() + rand.nextFloat();
            double d1 = (float)pos.getY() + rand.nextFloat();
            double d2 = (float)pos.getZ() + rand.nextFloat();
            double d3 = ((double)rand.nextFloat() - 0.5) * 0.5;
            double d4 = ((double)rand.nextFloat() - 0.5) * 0.5;
            double d5 = ((double)rand.nextFloat() - 0.5) * 0.5;
            int j = rand.nextInt(2) * 2 - 1;
            if (worldIn.getBlockState(pos.west()).getBlock() != this && worldIn.getBlockState(pos.east()).getBlock() != this) {
                d0 = (double)pos.getX() + 0.5 + 0.25 * (double)j;
                d3 = rand.nextFloat() * 2.0f * (float)j;
            } else {
                d2 = (double)pos.getZ() + 0.5 + 0.25 * (double)j;
                d5 = rand.nextFloat() * 2.0f * (float)j;
            }
            worldIn.spawnParticle(EnumParticleTypes.PORTAL, d0, d1, d2, d3, d4, d5, new int[0]);
            ++i;
        }
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return ItemStack.EMPTY;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(AXIS, (meta & 3) == 2 ? EnumFacing.Axis.Z : EnumFacing.Axis.X);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return BlockPortal.getMetaForAxis(state.getValue(AXIS));
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        switch (rot) {
            case CLOCKWISE_90: 
            case COUNTERCLOCKWISE_90: {
                switch (state.getValue(AXIS)) {
                    case X: {
                        return state.withProperty(AXIS, EnumFacing.Axis.Z);
                    }
                    case Z: {
                        return state.withProperty(AXIS, EnumFacing.Axis.X);
                    }
                }
                return state;
            }
        }
        return state;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer((Block)this, AXIS);
    }

    public BlockPattern.PatternHelper createPatternHelper(World worldIn, BlockPos p_181089_2_) {
        EnumFacing.Axis enumfacing$axis = EnumFacing.Axis.Z;
        Size blockportal$size = new Size(worldIn, p_181089_2_, EnumFacing.Axis.X);
        LoadingCache<BlockPos, BlockWorldState> loadingcache = BlockPattern.createLoadingCache(worldIn, true);
        if (!blockportal$size.isValid()) {
            enumfacing$axis = EnumFacing.Axis.X;
            blockportal$size = new Size(worldIn, p_181089_2_, EnumFacing.Axis.Z);
        }
        if (!blockportal$size.isValid()) {
            return new BlockPattern.PatternHelper(p_181089_2_, EnumFacing.NORTH, EnumFacing.UP, loadingcache, 1, 1, 1);
        }
        int[] aint = new int[EnumFacing.AxisDirection.values().length];
        EnumFacing enumfacing = blockportal$size.rightDir.rotateYCCW();
        BlockPos blockpos = blockportal$size.bottomLeft.up(blockportal$size.getHeight() - 1);
        EnumFacing.AxisDirection[] axisDirectionArray = EnumFacing.AxisDirection.values();
        int n = axisDirectionArray.length;
        int n2 = 0;
        while (n2 < n) {
            EnumFacing.AxisDirection enumfacing$axisdirection = axisDirectionArray[n2];
            BlockPattern.PatternHelper blockpattern$patternhelper = new BlockPattern.PatternHelper(enumfacing.getAxisDirection() == enumfacing$axisdirection ? blockpos : blockpos.offset(blockportal$size.rightDir, blockportal$size.getWidth() - 1), EnumFacing.getFacingFromAxis(enumfacing$axisdirection, enumfacing$axis), EnumFacing.UP, loadingcache, blockportal$size.getWidth(), blockportal$size.getHeight(), 1);
            int i = 0;
            while (i < blockportal$size.getWidth()) {
                int j = 0;
                while (j < blockportal$size.getHeight()) {
                    BlockWorldState blockworldstate = blockpattern$patternhelper.translateOffset(i, j, 1);
                    if (blockworldstate.getBlockState() != null && blockworldstate.getBlockState().getMaterial() != Material.AIR) {
                        int n3 = enumfacing$axisdirection.ordinal();
                        aint[n3] = aint[n3] + 1;
                    }
                    ++j;
                }
                ++i;
            }
            ++n2;
        }
        EnumFacing.AxisDirection enumfacing$axisdirection1 = EnumFacing.AxisDirection.POSITIVE;
        EnumFacing.AxisDirection[] axisDirectionArray2 = EnumFacing.AxisDirection.values();
        int n4 = axisDirectionArray2.length;
        n = 0;
        while (n < n4) {
            EnumFacing.AxisDirection enumfacing$axisdirection2 = axisDirectionArray2[n];
            if (aint[enumfacing$axisdirection2.ordinal()] < aint[enumfacing$axisdirection1.ordinal()]) {
                enumfacing$axisdirection1 = enumfacing$axisdirection2;
            }
            ++n;
        }
        return new BlockPattern.PatternHelper(enumfacing.getAxisDirection() == enumfacing$axisdirection1 ? blockpos : blockpos.offset(blockportal$size.rightDir, blockportal$size.getWidth() - 1), EnumFacing.getFacingFromAxis(enumfacing$axisdirection1, enumfacing$axis), EnumFacing.UP, loadingcache, blockportal$size.getWidth(), blockportal$size.getHeight(), 1);
    }

    @Override
    public BlockFaceShape func_193383_a(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
        return BlockFaceShape.UNDEFINED;
    }

    public static class Size {
        private final World world;
        private final EnumFacing.Axis axis;
        private final EnumFacing rightDir;
        private final EnumFacing leftDir;
        private int portalBlockCount;
        private BlockPos bottomLeft;
        private int height;
        private int width;

        public Size(World worldIn, BlockPos p_i45694_2_, EnumFacing.Axis p_i45694_3_) {
            this.world = worldIn;
            this.axis = p_i45694_3_;
            if (p_i45694_3_ == EnumFacing.Axis.X) {
                this.leftDir = EnumFacing.EAST;
                this.rightDir = EnumFacing.WEST;
            } else {
                this.leftDir = EnumFacing.NORTH;
                this.rightDir = EnumFacing.SOUTH;
            }
            BlockPos blockpos = p_i45694_2_;
            while (p_i45694_2_.getY() > blockpos.getY() - 21 && p_i45694_2_.getY() > 0 && this.isEmptyBlock(worldIn.getBlockState(p_i45694_2_.down()).getBlock())) {
                p_i45694_2_ = p_i45694_2_.down();
            }
            int i = this.getDistanceUntilEdge(p_i45694_2_, this.leftDir) - 1;
            if (i >= 0) {
                this.bottomLeft = p_i45694_2_.offset(this.leftDir, i);
                this.width = this.getDistanceUntilEdge(this.bottomLeft, this.rightDir);
                if (this.width < 2 || this.width > 21) {
                    this.bottomLeft = null;
                    this.width = 0;
                }
            }
            if (this.bottomLeft != null) {
                this.height = this.calculatePortalHeight();
            }
        }

        protected int getDistanceUntilEdge(BlockPos p_180120_1_, EnumFacing p_180120_2_) {
            Block block;
            int i = 0;
            while (i < 22) {
                BlockPos blockpos = p_180120_1_.offset(p_180120_2_, i);
                if (!this.isEmptyBlock(this.world.getBlockState(blockpos).getBlock()) || this.world.getBlockState(blockpos.down()).getBlock() != Blocks.OBSIDIAN) break;
                ++i;
            }
            return (block = this.world.getBlockState(p_180120_1_.offset(p_180120_2_, i)).getBlock()) == Blocks.OBSIDIAN ? i : 0;
        }

        public int getHeight() {
            return this.height;
        }

        public int getWidth() {
            return this.width;
        }

        protected int calculatePortalHeight() {
            this.height = 0;
            block0: while (this.height < 21) {
                int i = 0;
                while (i < this.width) {
                    BlockPos blockpos = this.bottomLeft.offset(this.rightDir, i).up(this.height);
                    Block block = this.world.getBlockState(blockpos).getBlock();
                    if (!this.isEmptyBlock(block)) break block0;
                    if (block == Blocks.PORTAL) {
                        ++this.portalBlockCount;
                    }
                    if (i == 0 ? (block = this.world.getBlockState(blockpos.offset(this.leftDir)).getBlock()) != Blocks.OBSIDIAN : i == this.width - 1 && (block = this.world.getBlockState(blockpos.offset(this.rightDir)).getBlock()) != Blocks.OBSIDIAN) break block0;
                    ++i;
                }
                ++this.height;
            }
            int j = 0;
            while (j < this.width) {
                if (this.world.getBlockState(this.bottomLeft.offset(this.rightDir, j).up(this.height)).getBlock() != Blocks.OBSIDIAN) {
                    this.height = 0;
                    break;
                }
                ++j;
            }
            if (this.height <= 21 && this.height >= 3) {
                return this.height;
            }
            this.bottomLeft = null;
            this.width = 0;
            this.height = 0;
            return 0;
        }

        protected boolean isEmptyBlock(Block blockIn) {
            return blockIn.blockMaterial == Material.AIR || blockIn == Blocks.FIRE || blockIn == Blocks.PORTAL;
        }

        public boolean isValid() {
            return this.bottomLeft != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
        }

        public void placePortalBlocks() {
            int i = 0;
            while (i < this.width) {
                BlockPos blockpos = this.bottomLeft.offset(this.rightDir, i);
                int j = 0;
                while (j < this.height) {
                    this.world.setBlockState(blockpos.up(j), Blocks.PORTAL.getDefaultState().withProperty(AXIS, this.axis), 2);
                    ++j;
                }
                ++i;
            }
        }
    }
}

