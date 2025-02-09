/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package net.minecraft.world;

import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.FunctionManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockObserver;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.pathfinding.PathWorldListener;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.ReportedException;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.VillageCollection;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraft.world.storage.loot.LootTableManager;

public abstract class World
implements IBlockAccess {
    private int seaLevel = 63;
    protected boolean scheduledUpdatesAreImmediate;
    public final List<Entity> loadedEntityList = Lists.newArrayList();
    protected final List<Entity> unloadedEntityList = Lists.newArrayList();
    public final List<TileEntity> loadedTileEntityList = Lists.newArrayList();
    public final List<TileEntity> tickableTileEntities = Lists.newArrayList();
    private final List<TileEntity> addedTileEntityList = Lists.newArrayList();
    private final List<TileEntity> tileEntitiesToBeRemoved = Lists.newArrayList();
    public final List<EntityPlayer> playerEntities = Lists.newArrayList();
    public final List<Entity> weatherEffects = Lists.newArrayList();
    protected final IntHashMap<Entity> entitiesById = new IntHashMap();
    private final long cloudColour = 0xFFFFFFL;
    private int skylightSubtracted;
    protected int updateLCG = new Random().nextInt();
    protected final int DIST_HASH_MAGIC = 1013904223;
    protected float prevRainingStrength;
    protected float rainingStrength;
    protected float prevThunderingStrength;
    protected float thunderingStrength;
    private int lastLightningBolt;
    public final Random rand = new Random();
    public final WorldProvider provider;
    protected PathWorldListener pathListener = new PathWorldListener();
    protected List<IWorldEventListener> eventListeners = Lists.newArrayList((Object[])new IWorldEventListener[]{this.pathListener});
    protected IChunkProvider chunkProvider;
    protected final ISaveHandler saveHandler;
    protected WorldInfo worldInfo;
    protected boolean findingSpawnPoint;
    protected MapStorage mapStorage;
    protected VillageCollection villageCollectionObj;
    protected LootTableManager lootTable;
    protected AdvancementManager field_191951_C;
    protected FunctionManager field_193036_D;
    public final Profiler theProfiler;
    private final Calendar theCalendar = Calendar.getInstance();
    protected Scoreboard worldScoreboard = new Scoreboard();
    public final boolean isRemote;
    protected boolean spawnHostileMobs = true;
    protected boolean spawnPeacefulMobs = true;
    private boolean processingLoadedTiles;
    private final WorldBorder worldBorder;
    int[] lightUpdateBlockList = new int[32768];

    protected World(ISaveHandler saveHandlerIn, WorldInfo info, WorldProvider providerIn, Profiler profilerIn, boolean client) {
        this.saveHandler = saveHandlerIn;
        this.theProfiler = profilerIn;
        this.worldInfo = info;
        this.provider = providerIn;
        this.isRemote = client;
        this.worldBorder = providerIn.createWorldBorder();
    }

    public World init() {
        return this;
    }

    @Override
    public Biome getBiome(final BlockPos pos) {
        if (this.isBlockLoaded(pos)) {
            Chunk chunk = this.getChunkFromBlockCoords(pos);
            try {
                return chunk.getBiome(pos, this.provider.getBiomeProvider());
            }
            catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Getting biome");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Coordinates of biome request");
                crashreportcategory.setDetail("Location", new ICrashReportDetail<String>(){

                    @Override
                    public String call() throws Exception {
                        return CrashReportCategory.getCoordinateInfo(pos);
                    }
                });
                throw new ReportedException(crashreport);
            }
        }
        return this.provider.getBiomeProvider().getBiome(pos, Biomes.PLAINS);
    }

    public BiomeProvider getBiomeProvider() {
        return this.provider.getBiomeProvider();
    }

    protected abstract IChunkProvider createChunkProvider();

    public void initialize(WorldSettings settings) {
        this.worldInfo.setServerInitialized(true);
    }

    @Nullable
    public MinecraftServer getMinecraftServer() {
        return null;
    }

    public void setInitialSpawnLocation() {
        this.setSpawnPoint(new BlockPos(8, 64, 8));
    }

    public IBlockState getGroundAboveSeaLevel(BlockPos pos) {
        BlockPos blockpos = new BlockPos(pos.getX(), this.getSeaLevel(), pos.getZ());
        while (!this.isAirBlock(blockpos.up())) {
            blockpos = blockpos.up();
        }
        return this.getBlockState(blockpos);
    }

    private boolean isValid(BlockPos pos) {
        return !this.isOutsideBuildHeight(pos) && pos.getX() >= -30000000 && pos.getZ() >= -30000000 && pos.getX() < 30000000 && pos.getZ() < 30000000;
    }

    private boolean isOutsideBuildHeight(BlockPos pos) {
        return pos.getY() < 0 || pos.getY() >= 256;
    }

    @Override
    public boolean isAirBlock(BlockPos pos) {
        return this.getBlockState(pos).getMaterial() == Material.AIR;
    }

    public boolean isBlockLoaded(BlockPos pos) {
        return this.isBlockLoaded(pos, true);
    }

    public boolean isBlockLoaded(BlockPos pos, boolean allowEmpty) {
        return this.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4, allowEmpty);
    }

    public boolean isAreaLoaded(BlockPos center, int radius) {
        return this.isAreaLoaded(center, radius, true);
    }

    public boolean isAreaLoaded(BlockPos center, int radius, boolean allowEmpty) {
        return this.isAreaLoaded(center.getX() - radius, center.getY() - radius, center.getZ() - radius, center.getX() + radius, center.getY() + radius, center.getZ() + radius, allowEmpty);
    }

    public boolean isAreaLoaded(BlockPos from, BlockPos to) {
        return this.isAreaLoaded(from, to, true);
    }

    public boolean isAreaLoaded(BlockPos from, BlockPos to, boolean allowEmpty) {
        return this.isAreaLoaded(from.getX(), from.getY(), from.getZ(), to.getX(), to.getY(), to.getZ(), allowEmpty);
    }

    public boolean isAreaLoaded(StructureBoundingBox box) {
        return this.isAreaLoaded(box, true);
    }

    public boolean isAreaLoaded(StructureBoundingBox box, boolean allowEmpty) {
        return this.isAreaLoaded(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, allowEmpty);
    }

    private boolean isAreaLoaded(int xStart, int yStart, int zStart, int xEnd, int yEnd, int zEnd, boolean allowEmpty) {
        if (yEnd >= 0 && yStart < 256) {
            zStart >>= 4;
            xEnd >>= 4;
            zEnd >>= 4;
            int i = xStart >>= 4;
            while (i <= xEnd) {
                int j = zStart;
                while (j <= zEnd) {
                    if (!this.isChunkLoaded(i, j, allowEmpty)) {
                        return false;
                    }
                    ++j;
                }
                ++i;
            }
            return true;
        }
        return false;
    }

    protected abstract boolean isChunkLoaded(int var1, int var2, boolean var3);

    public Chunk getChunkFromBlockCoords(BlockPos pos) {
        return this.getChunkFromChunkCoords(pos.getX() >> 4, pos.getZ() >> 4);
    }

    public Chunk getChunkFromChunkCoords(int chunkX, int chunkZ) {
        return this.chunkProvider.provideChunk(chunkX, chunkZ);
    }

    public boolean func_190526_b(int p_190526_1_, int p_190526_2_) {
        return this.isChunkLoaded(p_190526_1_, p_190526_2_, false) ? true : this.chunkProvider.func_191062_e(p_190526_1_, p_190526_2_);
    }

    public boolean setBlockState(BlockPos pos, IBlockState newState, int flags) {
        if (this.isOutsideBuildHeight(pos)) {
            return false;
        }
        if (!this.isRemote && this.worldInfo.getTerrainType() == WorldType.DEBUG_WORLD) {
            return false;
        }
        Chunk chunk = this.getChunkFromBlockCoords(pos);
        Block block = newState.getBlock();
        IBlockState iblockstate = chunk.setBlockState(pos, newState);
        if (iblockstate == null) {
            return false;
        }
        if (newState.getLightOpacity() != iblockstate.getLightOpacity() || newState.getLightValue() != iblockstate.getLightValue()) {
            this.theProfiler.startSection("checkLight");
            this.checkLight(pos);
            this.theProfiler.endSection();
        }
        if ((flags & 2) != 0 && (!this.isRemote || (flags & 4) == 0) && chunk.isPopulated()) {
            this.notifyBlockUpdate(pos, iblockstate, newState, flags);
        }
        if (!this.isRemote && (flags & 1) != 0) {
            this.notifyNeighborsRespectDebug(pos, iblockstate.getBlock(), true);
            if (newState.hasComparatorInputOverride()) {
                this.updateComparatorOutputLevel(pos, block);
            }
        } else if (!this.isRemote && (flags & 0x10) == 0) {
            this.func_190522_c(pos, block);
        }
        return true;
    }

    public boolean setBlockToAir(BlockPos pos) {
        return this.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
    }

    public boolean destroyBlock(BlockPos pos, boolean dropBlock) {
        IBlockState iblockstate = this.getBlockState(pos);
        Block block = iblockstate.getBlock();
        if (iblockstate.getMaterial() == Material.AIR) {
            return false;
        }
        this.playEvent(2001, pos, Block.getStateId(iblockstate));
        if (dropBlock) {
            block.dropBlockAsItem(this, pos, iblockstate, 0);
        }
        return this.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
    }

    public boolean setBlockState(BlockPos pos, IBlockState state) {
        return this.setBlockState(pos, state, 3);
    }

    public void notifyBlockUpdate(BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
        int i = 0;
        while (i < this.eventListeners.size()) {
            this.eventListeners.get(i).notifyBlockUpdate(this, pos, oldState, newState, flags);
            ++i;
        }
    }

    public void notifyNeighborsRespectDebug(BlockPos pos, Block blockType, boolean p_175722_3_) {
        if (this.worldInfo.getTerrainType() != WorldType.DEBUG_WORLD) {
            this.notifyNeighborsOfStateChange(pos, blockType, p_175722_3_);
        }
    }

    public void markBlocksDirtyVertical(int x1, int z1, int x2, int z2) {
        if (x2 > z2) {
            int i = z2;
            z2 = x2;
            x2 = i;
        }
        if (this.provider.func_191066_m()) {
            int j = x2;
            while (j <= z2) {
                this.checkLightFor(EnumSkyBlock.SKY, new BlockPos(x1, j, z1));
                ++j;
            }
        }
        this.markBlockRangeForRenderUpdate(x1, x2, z1, x1, z2, z1);
    }

    public void markBlockRangeForRenderUpdate(BlockPos rangeMin, BlockPos rangeMax) {
        this.markBlockRangeForRenderUpdate(rangeMin.getX(), rangeMin.getY(), rangeMin.getZ(), rangeMax.getX(), rangeMax.getY(), rangeMax.getZ());
    }

    public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
        int i = 0;
        while (i < this.eventListeners.size()) {
            this.eventListeners.get(i).markBlockRangeForRenderUpdate(x1, y1, z1, x2, y2, z2);
            ++i;
        }
    }

    public void func_190522_c(BlockPos p_190522_1_, Block p_190522_2_) {
        this.func_190529_b(p_190522_1_.west(), p_190522_2_, p_190522_1_);
        this.func_190529_b(p_190522_1_.east(), p_190522_2_, p_190522_1_);
        this.func_190529_b(p_190522_1_.down(), p_190522_2_, p_190522_1_);
        this.func_190529_b(p_190522_1_.up(), p_190522_2_, p_190522_1_);
        this.func_190529_b(p_190522_1_.north(), p_190522_2_, p_190522_1_);
        this.func_190529_b(p_190522_1_.south(), p_190522_2_, p_190522_1_);
    }

    public void notifyNeighborsOfStateChange(BlockPos pos, Block blockType, boolean p_175685_3_) {
        this.func_190524_a(pos.west(), blockType, pos);
        this.func_190524_a(pos.east(), blockType, pos);
        this.func_190524_a(pos.down(), blockType, pos);
        this.func_190524_a(pos.up(), blockType, pos);
        this.func_190524_a(pos.north(), blockType, pos);
        this.func_190524_a(pos.south(), blockType, pos);
        if (p_175685_3_) {
            this.func_190522_c(pos, blockType);
        }
    }

    public void notifyNeighborsOfStateExcept(BlockPos pos, Block blockType, EnumFacing skipSide) {
        if (skipSide != EnumFacing.WEST) {
            this.func_190524_a(pos.west(), blockType, pos);
        }
        if (skipSide != EnumFacing.EAST) {
            this.func_190524_a(pos.east(), blockType, pos);
        }
        if (skipSide != EnumFacing.DOWN) {
            this.func_190524_a(pos.down(), blockType, pos);
        }
        if (skipSide != EnumFacing.UP) {
            this.func_190524_a(pos.up(), blockType, pos);
        }
        if (skipSide != EnumFacing.NORTH) {
            this.func_190524_a(pos.north(), blockType, pos);
        }
        if (skipSide != EnumFacing.SOUTH) {
            this.func_190524_a(pos.south(), blockType, pos);
        }
    }

    public void func_190524_a(BlockPos p_190524_1_, final Block p_190524_2_, BlockPos p_190524_3_) {
        if (!this.isRemote) {
            IBlockState iblockstate = this.getBlockState(p_190524_1_);
            try {
                iblockstate.neighborChanged(this, p_190524_1_, p_190524_2_, p_190524_3_);
            }
            catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception while updating neighbours");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being updated");
                crashreportcategory.setDetail("Source block type", new ICrashReportDetail<String>(){

                    @Override
                    public String call() throws Exception {
                        try {
                            return String.format("ID #%d (%s // %s)", Block.getIdFromBlock(p_190524_2_), p_190524_2_.getUnlocalizedName(), p_190524_2_.getClass().getCanonicalName());
                        }
                        catch (Throwable var2) {
                            return "ID #" + Block.getIdFromBlock(p_190524_2_);
                        }
                    }
                });
                CrashReportCategory.addBlockInfo(crashreportcategory, p_190524_1_, iblockstate);
                throw new ReportedException(crashreport);
            }
        }
    }

    public void func_190529_b(BlockPos p_190529_1_, final Block p_190529_2_, BlockPos p_190529_3_) {
        IBlockState iblockstate;
        if (!this.isRemote && (iblockstate = this.getBlockState(p_190529_1_)).getBlock() == Blocks.field_190976_dk) {
            try {
                ((BlockObserver)iblockstate.getBlock()).func_190962_b(iblockstate, this, p_190529_1_, p_190529_2_, p_190529_3_);
            }
            catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception while updating neighbours");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being updated");
                crashreportcategory.setDetail("Source block type", new ICrashReportDetail<String>(){

                    @Override
                    public String call() throws Exception {
                        try {
                            return String.format("ID #%d (%s // %s)", Block.getIdFromBlock(p_190529_2_), p_190529_2_.getUnlocalizedName(), p_190529_2_.getClass().getCanonicalName());
                        }
                        catch (Throwable var2) {
                            return "ID #" + Block.getIdFromBlock(p_190529_2_);
                        }
                    }
                });
                CrashReportCategory.addBlockInfo(crashreportcategory, p_190529_1_, iblockstate);
                throw new ReportedException(crashreport);
            }
        }
    }

    public boolean isBlockTickPending(BlockPos pos, Block blockType) {
        return false;
    }

    public boolean canSeeSky(BlockPos pos) {
        return this.getChunkFromBlockCoords(pos).canSeeSky(pos);
    }

    public boolean canBlockSeeSky(BlockPos pos) {
        if (pos.getY() >= this.getSeaLevel()) {
            return this.canSeeSky(pos);
        }
        BlockPos blockpos = new BlockPos(pos.getX(), this.getSeaLevel(), pos.getZ());
        if (!this.canSeeSky(blockpos)) {
            return false;
        }
        BlockPos blockpos1 = blockpos.down();
        while (blockpos1.getY() > pos.getY()) {
            IBlockState iblockstate = this.getBlockState(blockpos1);
            if (iblockstate.getLightOpacity() > 0 && !iblockstate.getMaterial().isLiquid()) {
                return false;
            }
            blockpos1 = blockpos1.down();
        }
        return true;
    }

    public int getLight(BlockPos pos) {
        if (pos.getY() < 0) {
            return 0;
        }
        if (pos.getY() >= 256) {
            pos = new BlockPos(pos.getX(), 255, pos.getZ());
        }
        return this.getChunkFromBlockCoords(pos).getLightSubtracted(pos, 0);
    }

    public int getLightFromNeighbors(BlockPos pos) {
        return this.getLight(pos, true);
    }

    public int getLight(BlockPos pos, boolean checkNeighbors) {
        if (pos.getX() >= -30000000 && pos.getZ() >= -30000000 && pos.getX() < 30000000 && pos.getZ() < 30000000) {
            if (checkNeighbors && this.getBlockState(pos).useNeighborBrightness()) {
                int i1 = this.getLight(pos.up(), false);
                int i = this.getLight(pos.east(), false);
                int j = this.getLight(pos.west(), false);
                int k = this.getLight(pos.south(), false);
                int l = this.getLight(pos.north(), false);
                if (i > i1) {
                    i1 = i;
                }
                if (j > i1) {
                    i1 = j;
                }
                if (k > i1) {
                    i1 = k;
                }
                if (l > i1) {
                    i1 = l;
                }
                return i1;
            }
            if (pos.getY() < 0) {
                return 0;
            }
            if (pos.getY() >= 256) {
                pos = new BlockPos(pos.getX(), 255, pos.getZ());
            }
            Chunk chunk = this.getChunkFromBlockCoords(pos);
            return chunk.getLightSubtracted(pos, this.skylightSubtracted);
        }
        return 15;
    }

    public BlockPos getHeight(BlockPos pos) {
        return new BlockPos(pos.getX(), this.getHeight(pos.getX(), pos.getZ()), pos.getZ());
    }

    public int getHeight(int x, int z) {
        int i = x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000 ? (this.isChunkLoaded(x >> 4, z >> 4, true) ? this.getChunkFromChunkCoords(x >> 4, z >> 4).getHeightValue(x & 0xF, z & 0xF) : 0) : this.getSeaLevel() + 1;
        return i;
    }

    @Deprecated
    public int getChunksLowestHorizon(int x, int z) {
        if (x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000) {
            if (!this.isChunkLoaded(x >> 4, z >> 4, true)) {
                return 0;
            }
            Chunk chunk = this.getChunkFromChunkCoords(x >> 4, z >> 4);
            return chunk.getLowestHeight();
        }
        return this.getSeaLevel() + 1;
    }

    public int getLightFromNeighborsFor(EnumSkyBlock type, BlockPos pos) {
        if (!this.provider.func_191066_m() && type == EnumSkyBlock.SKY) {
            return 0;
        }
        if (pos.getY() < 0) {
            pos = new BlockPos(pos.getX(), 0, pos.getZ());
        }
        if (!this.isValid(pos)) {
            return type.defaultLightValue;
        }
        if (!this.isBlockLoaded(pos)) {
            return type.defaultLightValue;
        }
        if (this.getBlockState(pos).useNeighborBrightness()) {
            int i1 = this.getLightFor(type, pos.up());
            int i = this.getLightFor(type, pos.east());
            int j = this.getLightFor(type, pos.west());
            int k = this.getLightFor(type, pos.south());
            int l = this.getLightFor(type, pos.north());
            if (i > i1) {
                i1 = i;
            }
            if (j > i1) {
                i1 = j;
            }
            if (k > i1) {
                i1 = k;
            }
            if (l > i1) {
                i1 = l;
            }
            return i1;
        }
        Chunk chunk = this.getChunkFromBlockCoords(pos);
        return chunk.getLightFor(type, pos);
    }

    public int getLightFor(EnumSkyBlock type, BlockPos pos) {
        if (pos.getY() < 0) {
            pos = new BlockPos(pos.getX(), 0, pos.getZ());
        }
        if (!this.isValid(pos)) {
            return type.defaultLightValue;
        }
        if (!this.isBlockLoaded(pos)) {
            return type.defaultLightValue;
        }
        Chunk chunk = this.getChunkFromBlockCoords(pos);
        return chunk.getLightFor(type, pos);
    }

    public void setLightFor(EnumSkyBlock type, BlockPos pos, int lightValue) {
        if (this.isValid(pos) && this.isBlockLoaded(pos)) {
            Chunk chunk = this.getChunkFromBlockCoords(pos);
            chunk.setLightFor(type, pos, lightValue);
            this.notifyLightSet(pos);
        }
    }

    public void notifyLightSet(BlockPos pos) {
        int i = 0;
        while (i < this.eventListeners.size()) {
            this.eventListeners.get(i).notifyLightSet(pos);
            ++i;
        }
    }

    @Override
    public int getCombinedLight(BlockPos pos, int lightValue) {
        int i = this.getLightFromNeighborsFor(EnumSkyBlock.SKY, pos);
        int j = this.getLightFromNeighborsFor(EnumSkyBlock.BLOCK, pos);
        if (j < lightValue) {
            j = lightValue;
        }
        return i << 20 | j << 4;
    }

    public float getLightBrightness(BlockPos pos) {
        return this.provider.getLightBrightnessTable()[this.getLightFromNeighbors(pos)];
    }

    @Override
    public IBlockState getBlockState(BlockPos pos) {
        if (this.isOutsideBuildHeight(pos)) {
            return Blocks.AIR.getDefaultState();
        }
        Chunk chunk = this.getChunkFromBlockCoords(pos);
        return chunk.getBlockState(pos);
    }

    public boolean isDaytime() {
        return this.skylightSubtracted < 4;
    }

    @Nullable
    public RayTraceResult rayTraceBlocks(Vec3d start, Vec3d end) {
        return this.rayTraceBlocks(start, end, false, false, false);
    }

    @Nullable
    public RayTraceResult rayTraceBlocks(Vec3d start, Vec3d end, boolean stopOnLiquid) {
        return this.rayTraceBlocks(start, end, stopOnLiquid, false, false);
    }

    @Nullable
    public RayTraceResult rayTraceBlocks(Vec3d vec31, Vec3d vec32, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock) {
        if (!(Double.isNaN(vec31.xCoord) || Double.isNaN(vec31.yCoord) || Double.isNaN(vec31.zCoord))) {
            if (!(Double.isNaN(vec32.xCoord) || Double.isNaN(vec32.yCoord) || Double.isNaN(vec32.zCoord))) {
                RayTraceResult raytraceresult;
                int i = MathHelper.floor(vec32.xCoord);
                int j = MathHelper.floor(vec32.yCoord);
                int k = MathHelper.floor(vec32.zCoord);
                int l = MathHelper.floor(vec31.xCoord);
                int i1 = MathHelper.floor(vec31.yCoord);
                int j1 = MathHelper.floor(vec31.zCoord);
                BlockPos blockpos = new BlockPos(l, i1, j1);
                IBlockState iblockstate = this.getBlockState(blockpos);
                Block block = iblockstate.getBlock();
                if ((!ignoreBlockWithoutBoundingBox || iblockstate.getCollisionBoundingBox(this, blockpos) != Block.NULL_AABB) && block.canCollideCheck(iblockstate, stopOnLiquid) && (raytraceresult = iblockstate.collisionRayTrace(this, blockpos, vec31, vec32)) != null) {
                    return raytraceresult;
                }
                RayTraceResult raytraceresult2 = null;
                int k1 = 200;
                while (k1-- >= 0) {
                    EnumFacing enumfacing;
                    if (Double.isNaN(vec31.xCoord) || Double.isNaN(vec31.yCoord) || Double.isNaN(vec31.zCoord)) {
                        return null;
                    }
                    if (l == i && i1 == j && j1 == k) {
                        return returnLastUncollidableBlock ? raytraceresult2 : null;
                    }
                    boolean flag2 = true;
                    boolean flag = true;
                    boolean flag1 = true;
                    double d0 = 999.0;
                    double d1 = 999.0;
                    double d2 = 999.0;
                    if (i > l) {
                        d0 = (double)l + 1.0;
                    } else if (i < l) {
                        d0 = (double)l + 0.0;
                    } else {
                        flag2 = false;
                    }
                    if (j > i1) {
                        d1 = (double)i1 + 1.0;
                    } else if (j < i1) {
                        d1 = (double)i1 + 0.0;
                    } else {
                        flag = false;
                    }
                    if (k > j1) {
                        d2 = (double)j1 + 1.0;
                    } else if (k < j1) {
                        d2 = (double)j1 + 0.0;
                    } else {
                        flag1 = false;
                    }
                    double d3 = 999.0;
                    double d4 = 999.0;
                    double d5 = 999.0;
                    double d6 = vec32.xCoord - vec31.xCoord;
                    double d7 = vec32.yCoord - vec31.yCoord;
                    double d8 = vec32.zCoord - vec31.zCoord;
                    if (flag2) {
                        d3 = (d0 - vec31.xCoord) / d6;
                    }
                    if (flag) {
                        d4 = (d1 - vec31.yCoord) / d7;
                    }
                    if (flag1) {
                        d5 = (d2 - vec31.zCoord) / d8;
                    }
                    if (d3 == -0.0) {
                        d3 = -1.0E-4;
                    }
                    if (d4 == -0.0) {
                        d4 = -1.0E-4;
                    }
                    if (d5 == -0.0) {
                        d5 = -1.0E-4;
                    }
                    if (d3 < d4 && d3 < d5) {
                        enumfacing = i > l ? EnumFacing.WEST : EnumFacing.EAST;
                        vec31 = new Vec3d(d0, vec31.yCoord + d7 * d3, vec31.zCoord + d8 * d3);
                    } else if (d4 < d5) {
                        enumfacing = j > i1 ? EnumFacing.DOWN : EnumFacing.UP;
                        vec31 = new Vec3d(vec31.xCoord + d6 * d4, d1, vec31.zCoord + d8 * d4);
                    } else {
                        enumfacing = k > j1 ? EnumFacing.NORTH : EnumFacing.SOUTH;
                        vec31 = new Vec3d(vec31.xCoord + d6 * d5, vec31.yCoord + d7 * d5, d2);
                    }
                    l = MathHelper.floor(vec31.xCoord) - (enumfacing == EnumFacing.EAST ? 1 : 0);
                    i1 = MathHelper.floor(vec31.yCoord) - (enumfacing == EnumFacing.UP ? 1 : 0);
                    j1 = MathHelper.floor(vec31.zCoord) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
                    blockpos = new BlockPos(l, i1, j1);
                    IBlockState iblockstate1 = this.getBlockState(blockpos);
                    Block block1 = iblockstate1.getBlock();
                    if (ignoreBlockWithoutBoundingBox && iblockstate1.getMaterial() != Material.PORTAL && iblockstate1.getCollisionBoundingBox(this, blockpos) == Block.NULL_AABB) continue;
                    if (block1.canCollideCheck(iblockstate1, stopOnLiquid)) {
                        RayTraceResult raytraceresult1 = iblockstate1.collisionRayTrace(this, blockpos, vec31, vec32);
                        if (raytraceresult1 == null) continue;
                        return raytraceresult1;
                    }
                    raytraceresult2 = new RayTraceResult(RayTraceResult.Type.MISS, vec31, enumfacing, blockpos);
                }
                return returnLastUncollidableBlock ? raytraceresult2 : null;
            }
            return null;
        }
        return null;
    }

    public void playSound(@Nullable EntityPlayer player, BlockPos pos, SoundEvent soundIn, SoundCategory category, float volume, float pitch) {
        this.playSound(player, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, soundIn, category, volume, pitch);
    }

    public void playSound(@Nullable EntityPlayer player, double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch) {
        int i = 0;
        while (i < this.eventListeners.size()) {
            this.eventListeners.get(i).playSoundToAllNearExcept(player, soundIn, category, x, y, z, volume, pitch);
            ++i;
        }
    }

    public void playSound(double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch, boolean distanceDelay) {
    }

    public void playRecord(BlockPos blockPositionIn, @Nullable SoundEvent soundEventIn) {
        int i = 0;
        while (i < this.eventListeners.size()) {
            this.eventListeners.get(i).playRecord(soundEventIn, blockPositionIn);
            ++i;
        }
    }

    public void spawnParticle(EnumParticleTypes particleType, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int ... parameters) {
        this.spawnParticle(particleType.getParticleID(), particleType.getShouldIgnoreRange(), xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
    }

    public void func_190523_a(int p_190523_1_, double p_190523_2_, double p_190523_4_, double p_190523_6_, double p_190523_8_, double p_190523_10_, double p_190523_12_, int ... p_190523_14_) {
        int i = 0;
        while (i < this.eventListeners.size()) {
            this.eventListeners.get(i).func_190570_a(p_190523_1_, false, true, p_190523_2_, p_190523_4_, p_190523_6_, p_190523_8_, p_190523_10_, p_190523_12_, p_190523_14_);
            ++i;
        }
    }

    public void spawnParticle(EnumParticleTypes particleType, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int ... parameters) {
        this.spawnParticle(particleType.getParticleID(), particleType.getShouldIgnoreRange() || ignoreRange, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
    }

    private void spawnParticle(int particleID, boolean ignoreRange, double xCood, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int ... parameters) {
        int i = 0;
        while (i < this.eventListeners.size()) {
            this.eventListeners.get(i).spawnParticle(particleID, ignoreRange, xCood, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
            ++i;
        }
    }

    public boolean addWeatherEffect(Entity entityIn) {
        this.weatherEffects.add(entityIn);
        return true;
    }

    public boolean spawnEntityInWorld(Entity entityIn) {
        int i = MathHelper.floor(entityIn.posX / 16.0);
        int j = MathHelper.floor(entityIn.posZ / 16.0);
        boolean flag = entityIn.forceSpawn;
        if (entityIn instanceof EntityPlayer) {
            flag = true;
        }
        if (!flag && !this.isChunkLoaded(i, j, false)) {
            return false;
        }
        if (entityIn instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer)entityIn;
            this.playerEntities.add(entityplayer);
            this.updateAllPlayersSleepingFlag();
        }
        this.getChunkFromChunkCoords(i, j).addEntity(entityIn);
        this.loadedEntityList.add(entityIn);
        this.onEntityAdded(entityIn);
        return true;
    }

    protected void onEntityAdded(Entity entityIn) {
        int i = 0;
        while (i < this.eventListeners.size()) {
            this.eventListeners.get(i).onEntityAdded(entityIn);
            ++i;
        }
    }

    protected void onEntityRemoved(Entity entityIn) {
        int i = 0;
        while (i < this.eventListeners.size()) {
            this.eventListeners.get(i).onEntityRemoved(entityIn);
            ++i;
        }
    }

    public void removeEntity(Entity entityIn) {
        if (entityIn.isBeingRidden()) {
            entityIn.removePassengers();
        }
        if (entityIn.isRiding()) {
            entityIn.dismountRidingEntity();
        }
        entityIn.setDead();
        if (entityIn instanceof EntityPlayer) {
            this.playerEntities.remove(entityIn);
            this.updateAllPlayersSleepingFlag();
            this.onEntityRemoved(entityIn);
        }
    }

    public void removeEntityDangerously(Entity entityIn) {
        entityIn.setDropItemsWhenDead(false);
        entityIn.setDead();
        if (entityIn instanceof EntityPlayer) {
            this.playerEntities.remove(entityIn);
            this.updateAllPlayersSleepingFlag();
        }
        int i = entityIn.chunkCoordX;
        int j = entityIn.chunkCoordZ;
        if (entityIn.addedToChunk && this.isChunkLoaded(i, j, true)) {
            this.getChunkFromChunkCoords(i, j).removeEntity(entityIn);
        }
        this.loadedEntityList.remove(entityIn);
        this.onEntityRemoved(entityIn);
    }

    public void addEventListener(IWorldEventListener listener) {
        this.eventListeners.add(listener);
    }

    public void removeEventListener(IWorldEventListener listener) {
        this.eventListeners.remove(listener);
    }

    private boolean func_191504_a(@Nullable Entity p_191504_1_, AxisAlignedBB p_191504_2_, boolean p_191504_3_, @Nullable List<AxisAlignedBB> p_191504_4_) {
        int i = MathHelper.floor(p_191504_2_.minX) - 1;
        int j = MathHelper.ceil(p_191504_2_.maxX) + 1;
        int k = MathHelper.floor(p_191504_2_.minY) - 1;
        int l = MathHelper.ceil(p_191504_2_.maxY) + 1;
        int i1 = MathHelper.floor(p_191504_2_.minZ) - 1;
        int j1 = MathHelper.ceil(p_191504_2_.maxZ) + 1;
        WorldBorder worldborder = this.getWorldBorder();
        boolean flag = p_191504_1_ != null && p_191504_1_.isOutsideBorder();
        boolean flag1 = p_191504_1_ != null && this.func_191503_g(p_191504_1_);
        IBlockState iblockstate = Blocks.STONE.getDefaultState();
        BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();
        try {
            int k1 = i;
            while (k1 < j) {
                int l1 = i1;
                while (l1 < j1) {
                    boolean flag3;
                    boolean flag2 = k1 == i || k1 == j - 1;
                    boolean bl = flag3 = l1 == i1 || l1 == j1 - 1;
                    if (!(flag2 && flag3 || !this.isBlockLoaded(blockpos$pooledmutableblockpos.setPos(k1, 64, l1)))) {
                        int i2 = k;
                        while (i2 < l) {
                            if (!flag2 && !flag3 || i2 != l - 1) {
                                if (p_191504_3_) {
                                    if (k1 < -30000000 || k1 >= 30000000 || l1 < -30000000 || l1 >= 30000000) {
                                        boolean lvt_21_2_;
                                        boolean bl2 = lvt_21_2_ = true;
                                        return bl2;
                                    }
                                } else if (p_191504_1_ != null && flag == flag1) {
                                    p_191504_1_.setOutsideBorder(!flag1);
                                }
                                blockpos$pooledmutableblockpos.setPos(k1, i2, l1);
                                IBlockState iblockstate1 = !p_191504_3_ && !worldborder.contains(blockpos$pooledmutableblockpos) && flag1 ? iblockstate : this.getBlockState(blockpos$pooledmutableblockpos);
                                iblockstate1.addCollisionBoxToList(this, blockpos$pooledmutableblockpos, p_191504_2_, p_191504_4_, p_191504_1_, false);
                                if (p_191504_3_ && !p_191504_4_.isEmpty()) {
                                    boolean flag5;
                                    boolean bl3 = flag5 = true;
                                    return bl3;
                                }
                            }
                            ++i2;
                        }
                    }
                    ++l1;
                }
                ++k1;
            }
        }
        finally {
            blockpos$pooledmutableblockpos.release();
        }
        return !p_191504_4_.isEmpty();
    }

    public List<AxisAlignedBB> getCollisionBoxes(@Nullable Entity entityIn, AxisAlignedBB aabb) {
        ArrayList list = Lists.newArrayList();
        this.func_191504_a(entityIn, aabb, false, list);
        if (entityIn != null) {
            List<Entity> list1 = this.getEntitiesWithinAABBExcludingEntity(entityIn, aabb.expandXyz(0.25));
            int i = 0;
            while (i < list1.size()) {
                Entity entity = list1.get(i);
                if (!entityIn.isRidingSameEntity(entity)) {
                    AxisAlignedBB axisalignedbb = entity.getCollisionBoundingBox();
                    if (axisalignedbb != null && axisalignedbb.intersectsWith(aabb)) {
                        list.add(axisalignedbb);
                    }
                    if ((axisalignedbb = entityIn.getCollisionBox(entity)) != null && axisalignedbb.intersectsWith(aabb)) {
                        list.add(axisalignedbb);
                    }
                }
                ++i;
            }
        }
        return list;
    }

    public boolean func_191503_g(Entity p_191503_1_) {
        double d0 = this.worldBorder.minX();
        double d1 = this.worldBorder.minZ();
        double d2 = this.worldBorder.maxX();
        double d3 = this.worldBorder.maxZ();
        if (p_191503_1_.isOutsideBorder()) {
            d0 += 1.0;
            d1 += 1.0;
            d2 -= 1.0;
            d3 -= 1.0;
        } else {
            d0 -= 1.0;
            d1 -= 1.0;
            d2 += 1.0;
            d3 += 1.0;
        }
        return p_191503_1_.posX > d0 && p_191503_1_.posX < d2 && p_191503_1_.posZ > d1 && p_191503_1_.posZ < d3;
    }

    public boolean collidesWithAnyBlock(AxisAlignedBB bbox) {
        return this.func_191504_a(null, bbox, true, Lists.newArrayList());
    }

    public int calculateSkylightSubtracted(float partialTicks) {
        float f = this.getCelestialAngle(partialTicks);
        float f1 = 1.0f - (MathHelper.cos(f * ((float)Math.PI * 2)) * 2.0f + 0.5f);
        f1 = MathHelper.clamp(f1, 0.0f, 1.0f);
        f1 = 1.0f - f1;
        f1 = (float)((double)f1 * (1.0 - (double)(this.getRainStrength(partialTicks) * 5.0f) / 16.0));
        f1 = (float)((double)f1 * (1.0 - (double)(this.getThunderStrength(partialTicks) * 5.0f) / 16.0));
        f1 = 1.0f - f1;
        return (int)(f1 * 11.0f);
    }

    public float getSunBrightness(float p_72971_1_) {
        float f = this.getCelestialAngle(p_72971_1_);
        float f1 = 1.0f - (MathHelper.cos(f * ((float)Math.PI * 2)) * 2.0f + 0.2f);
        f1 = MathHelper.clamp(f1, 0.0f, 1.0f);
        f1 = 1.0f - f1;
        f1 = (float)((double)f1 * (1.0 - (double)(this.getRainStrength(p_72971_1_) * 5.0f) / 16.0));
        f1 = (float)((double)f1 * (1.0 - (double)(this.getThunderStrength(p_72971_1_) * 5.0f) / 16.0));
        return f1 * 0.8f + 0.2f;
    }

    public Vec3d getSkyColor(Entity entityIn, float partialTicks) {
        float f10;
        float f = this.getCelestialAngle(partialTicks);
        float f1 = MathHelper.cos(f * ((float)Math.PI * 2)) * 2.0f + 0.5f;
        f1 = MathHelper.clamp(f1, 0.0f, 1.0f);
        int i = MathHelper.floor(entityIn.posX);
        int j = MathHelper.floor(entityIn.posY);
        int k = MathHelper.floor(entityIn.posZ);
        BlockPos blockpos = new BlockPos(i, j, k);
        Biome biome = this.getBiome(blockpos);
        float f2 = biome.getFloatTemperature(blockpos);
        int l = biome.getSkyColorByTemp(f2);
        float f3 = (float)(l >> 16 & 0xFF) / 255.0f;
        float f4 = (float)(l >> 8 & 0xFF) / 255.0f;
        float f5 = (float)(l & 0xFF) / 255.0f;
        f3 *= f1;
        f4 *= f1;
        f5 *= f1;
        float f6 = this.getRainStrength(partialTicks);
        if (f6 > 0.0f) {
            float f7 = (f3 * 0.3f + f4 * 0.59f + f5 * 0.11f) * 0.6f;
            float f8 = 1.0f - f6 * 0.75f;
            f3 = f3 * f8 + f7 * (1.0f - f8);
            f4 = f4 * f8 + f7 * (1.0f - f8);
            f5 = f5 * f8 + f7 * (1.0f - f8);
        }
        if ((f10 = this.getThunderStrength(partialTicks)) > 0.0f) {
            float f11 = (f3 * 0.3f + f4 * 0.59f + f5 * 0.11f) * 0.2f;
            float f9 = 1.0f - f10 * 0.75f;
            f3 = f3 * f9 + f11 * (1.0f - f9);
            f4 = f4 * f9 + f11 * (1.0f - f9);
            f5 = f5 * f9 + f11 * (1.0f - f9);
        }
        if (this.lastLightningBolt > 0) {
            float f12 = (float)this.lastLightningBolt - partialTicks;
            if (f12 > 1.0f) {
                f12 = 1.0f;
            }
            f3 = f3 * (1.0f - (f12 *= 0.45f)) + 0.8f * f12;
            f4 = f4 * (1.0f - f12) + 0.8f * f12;
            f5 = f5 * (1.0f - f12) + 1.0f * f12;
        }
        return new Vec3d(f3, f4, f5);
    }

    public float getCelestialAngle(float partialTicks) {
        return this.provider.calculateCelestialAngle(this.worldInfo.getWorldTime(), partialTicks);
    }

    public int getMoonPhase() {
        return this.provider.getMoonPhase(this.worldInfo.getWorldTime());
    }

    public float getCurrentMoonPhaseFactor() {
        return WorldProvider.MOON_PHASE_FACTORS[this.provider.getMoonPhase(this.worldInfo.getWorldTime())];
    }

    public float getCelestialAngleRadians(float partialTicks) {
        float f = this.getCelestialAngle(partialTicks);
        return f * ((float)Math.PI * 2);
    }

    public Vec3d getCloudColour(float partialTicks) {
        float f = this.getCelestialAngle(partialTicks);
        float f1 = MathHelper.cos(f * ((float)Math.PI * 2)) * 2.0f + 0.5f;
        f1 = MathHelper.clamp(f1, 0.0f, 1.0f);
        float f2 = 1.0f;
        float f3 = 1.0f;
        float f4 = 1.0f;
        float f5 = this.getRainStrength(partialTicks);
        if (f5 > 0.0f) {
            float f6 = (f2 * 0.3f + f3 * 0.59f + f4 * 0.11f) * 0.6f;
            float f7 = 1.0f - f5 * 0.95f;
            f2 = f2 * f7 + f6 * (1.0f - f7);
            f3 = f3 * f7 + f6 * (1.0f - f7);
            f4 = f4 * f7 + f6 * (1.0f - f7);
        }
        f2 *= f1 * 0.9f + 0.1f;
        f3 *= f1 * 0.9f + 0.1f;
        f4 *= f1 * 0.85f + 0.15f;
        float f9 = this.getThunderStrength(partialTicks);
        if (f9 > 0.0f) {
            float f10 = (f2 * 0.3f + f3 * 0.59f + f4 * 0.11f) * 0.2f;
            float f8 = 1.0f - f9 * 0.95f;
            f2 = f2 * f8 + f10 * (1.0f - f8);
            f3 = f3 * f8 + f10 * (1.0f - f8);
            f4 = f4 * f8 + f10 * (1.0f - f8);
        }
        return new Vec3d(f2, f3, f4);
    }

    public Vec3d getFogColor(float partialTicks) {
        float f = this.getCelestialAngle(partialTicks);
        return this.provider.getFogColor(f, partialTicks);
    }

    public BlockPos getPrecipitationHeight(BlockPos pos) {
        return this.getChunkFromBlockCoords(pos).getPrecipitationHeight(pos);
    }

    public BlockPos getTopSolidOrLiquidBlock(BlockPos pos) {
        Chunk chunk = this.getChunkFromBlockCoords(pos);
        BlockPos blockpos = new BlockPos(pos.getX(), chunk.getTopFilledSegment() + 16, pos.getZ());
        while (blockpos.getY() >= 0) {
            BlockPos blockpos1 = blockpos.down();
            Material material = chunk.getBlockState(blockpos1).getMaterial();
            if (material.blocksMovement() && material != Material.LEAVES) break;
            blockpos = blockpos1;
        }
        return blockpos;
    }

    public float getStarBrightness(float partialTicks) {
        float f = this.getCelestialAngle(partialTicks);
        float f1 = 1.0f - (MathHelper.cos(f * ((float)Math.PI * 2)) * 2.0f + 0.25f);
        f1 = MathHelper.clamp(f1, 0.0f, 1.0f);
        return f1 * f1 * 0.5f;
    }

    public boolean isUpdateScheduled(BlockPos pos, Block blk) {
        return true;
    }

    public void scheduleUpdate(BlockPos pos, Block blockIn, int delay) {
    }

    public void updateBlockTick(BlockPos pos, Block blockIn, int delay, int priority) {
    }

    public void scheduleBlockUpdate(BlockPos pos, Block blockIn, int delay, int priority) {
    }

    public void updateEntities() {
        this.theProfiler.startSection("entities");
        this.theProfiler.startSection("global");
        int i = 0;
        while (i < this.weatherEffects.size()) {
            Entity entity = this.weatherEffects.get(i);
            try {
                ++entity.ticksExisted;
                entity.onUpdate();
            }
            catch (Throwable throwable2) {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable2, "Ticking entity");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being ticked");
                if (entity == null) {
                    crashreportcategory.addCrashSection("Entity", "~~NULL~~");
                } else {
                    entity.addEntityCrashInfo(crashreportcategory);
                }
                throw new ReportedException(crashreport);
            }
            if (entity.isDead) {
                this.weatherEffects.remove(i--);
            }
            ++i;
        }
        this.theProfiler.endStartSection("remove");
        this.loadedEntityList.removeAll(this.unloadedEntityList);
        int k = 0;
        while (k < this.unloadedEntityList.size()) {
            Entity entity1 = this.unloadedEntityList.get(k);
            int j = entity1.chunkCoordX;
            int k1 = entity1.chunkCoordZ;
            if (entity1.addedToChunk && this.isChunkLoaded(j, k1, true)) {
                this.getChunkFromChunkCoords(j, k1).removeEntity(entity1);
            }
            ++k;
        }
        int l = 0;
        while (l < this.unloadedEntityList.size()) {
            this.onEntityRemoved(this.unloadedEntityList.get(l));
            ++l;
        }
        this.unloadedEntityList.clear();
        this.tickPlayers();
        this.theProfiler.endStartSection("regular");
        int i1 = 0;
        while (i1 < this.loadedEntityList.size()) {
            block27: {
                Entity entity2;
                block26: {
                    entity2 = this.loadedEntityList.get(i1);
                    Entity entity3 = entity2.getRidingEntity();
                    if (entity3 == null) break block26;
                    if (!entity3.isDead && entity3.isPassenger(entity2)) break block27;
                    entity2.dismountRidingEntity();
                }
                this.theProfiler.startSection("tick");
                if (!entity2.isDead && !(entity2 instanceof EntityPlayerMP)) {
                    try {
                        this.updateEntity(entity2);
                    }
                    catch (Throwable throwable1) {
                        CrashReport crashreport1 = CrashReport.makeCrashReport(throwable1, "Ticking entity");
                        CrashReportCategory crashreportcategory1 = crashreport1.makeCategory("Entity being ticked");
                        entity2.addEntityCrashInfo(crashreportcategory1);
                        throw new ReportedException(crashreport1);
                    }
                }
                this.theProfiler.endSection();
                this.theProfiler.startSection("remove");
                if (entity2.isDead) {
                    int l1 = entity2.chunkCoordX;
                    int i2 = entity2.chunkCoordZ;
                    if (entity2.addedToChunk && this.isChunkLoaded(l1, i2, true)) {
                        this.getChunkFromChunkCoords(l1, i2).removeEntity(entity2);
                    }
                    this.loadedEntityList.remove(i1--);
                    this.onEntityRemoved(entity2);
                }
                this.theProfiler.endSection();
            }
            ++i1;
        }
        this.theProfiler.endStartSection("blockEntities");
        this.processingLoadedTiles = true;
        Iterator<TileEntity> iterator = this.tickableTileEntities.iterator();
        while (iterator.hasNext()) {
            BlockPos blockpos;
            TileEntity tileentity = iterator.next();
            if (!tileentity.isInvalid() && tileentity.hasWorldObj() && this.isBlockLoaded(blockpos = tileentity.getPos()) && this.worldBorder.contains(blockpos)) {
                try {
                    this.theProfiler.startSection(tileentity.getClass().getSimpleName());
                    ((ITickable)((Object)tileentity)).update();
                    this.theProfiler.endSection();
                }
                catch (Throwable throwable) {
                    CrashReport crashreport2 = CrashReport.makeCrashReport(throwable, "Ticking block entity");
                    CrashReportCategory crashreportcategory2 = crashreport2.makeCategory("Block entity being ticked");
                    tileentity.addInfoToCrashReport(crashreportcategory2);
                    throw new ReportedException(crashreport2);
                }
            }
            if (!tileentity.isInvalid()) continue;
            iterator.remove();
            this.loadedTileEntityList.remove(tileentity);
            if (!this.isBlockLoaded(tileentity.getPos())) continue;
            this.getChunkFromBlockCoords(tileentity.getPos()).removeTileEntity(tileentity.getPos());
        }
        this.processingLoadedTiles = false;
        if (!this.tileEntitiesToBeRemoved.isEmpty()) {
            this.tickableTileEntities.removeAll(this.tileEntitiesToBeRemoved);
            this.loadedTileEntityList.removeAll(this.tileEntitiesToBeRemoved);
            this.tileEntitiesToBeRemoved.clear();
        }
        this.theProfiler.endStartSection("pendingBlockEntities");
        if (!this.addedTileEntityList.isEmpty()) {
            int j1 = 0;
            while (j1 < this.addedTileEntityList.size()) {
                TileEntity tileentity1 = this.addedTileEntityList.get(j1);
                if (!tileentity1.isInvalid()) {
                    if (!this.loadedTileEntityList.contains(tileentity1)) {
                        this.addTileEntity(tileentity1);
                    }
                    if (this.isBlockLoaded(tileentity1.getPos())) {
                        Chunk chunk = this.getChunkFromBlockCoords(tileentity1.getPos());
                        IBlockState iblockstate = chunk.getBlockState(tileentity1.getPos());
                        chunk.addTileEntity(tileentity1.getPos(), tileentity1);
                        this.notifyBlockUpdate(tileentity1.getPos(), iblockstate, iblockstate, 3);
                    }
                }
                ++j1;
            }
            this.addedTileEntityList.clear();
        }
        this.theProfiler.endSection();
        this.theProfiler.endSection();
    }

    protected void tickPlayers() {
    }

    public boolean addTileEntity(TileEntity tile) {
        boolean flag = this.loadedTileEntityList.add(tile);
        if (flag && tile instanceof ITickable) {
            this.tickableTileEntities.add(tile);
        }
        if (this.isRemote) {
            BlockPos blockpos = tile.getPos();
            IBlockState iblockstate = this.getBlockState(blockpos);
            this.notifyBlockUpdate(blockpos, iblockstate, iblockstate, 2);
        }
        return flag;
    }

    public void addTileEntities(Collection<TileEntity> tileEntityCollection) {
        if (this.processingLoadedTiles) {
            this.addedTileEntityList.addAll(tileEntityCollection);
        } else {
            for (TileEntity tileentity : tileEntityCollection) {
                this.addTileEntity(tileentity);
            }
        }
    }

    public void updateEntity(Entity ent) {
        this.updateEntityWithOptionalForce(ent, true);
    }

    public void updateEntityWithOptionalForce(Entity entityIn, boolean forceUpdate) {
        if (!(entityIn instanceof EntityPlayer)) {
            int i = MathHelper.floor(entityIn.posX);
            int j = MathHelper.floor(entityIn.posZ);
            int k = 32;
            if (forceUpdate && !this.isAreaLoaded(i - 32, 0, j - 32, i + 32, 0, j + 32, true)) {
                return;
            }
        }
        entityIn.lastTickPosX = entityIn.posX;
        entityIn.lastTickPosY = entityIn.posY;
        entityIn.lastTickPosZ = entityIn.posZ;
        entityIn.prevRotationYaw = entityIn.rotationYaw;
        entityIn.prevRotationPitch = entityIn.rotationPitch;
        if (forceUpdate && entityIn.addedToChunk) {
            ++entityIn.ticksExisted;
            if (entityIn.isRiding()) {
                entityIn.updateRidden();
            } else {
                entityIn.onUpdate();
            }
        }
        this.theProfiler.startSection("chunkCheck");
        if (Double.isNaN(entityIn.posX) || Double.isInfinite(entityIn.posX)) {
            entityIn.posX = entityIn.lastTickPosX;
        }
        if (Double.isNaN(entityIn.posY) || Double.isInfinite(entityIn.posY)) {
            entityIn.posY = entityIn.lastTickPosY;
        }
        if (Double.isNaN(entityIn.posZ) || Double.isInfinite(entityIn.posZ)) {
            entityIn.posZ = entityIn.lastTickPosZ;
        }
        if (Double.isNaN(entityIn.rotationPitch) || Double.isInfinite(entityIn.rotationPitch)) {
            entityIn.rotationPitch = entityIn.prevRotationPitch;
        }
        if (Double.isNaN(entityIn.rotationYaw) || Double.isInfinite(entityIn.rotationYaw)) {
            entityIn.rotationYaw = entityIn.prevRotationYaw;
        }
        int l = MathHelper.floor(entityIn.posX / 16.0);
        int i1 = MathHelper.floor(entityIn.posY / 16.0);
        int j1 = MathHelper.floor(entityIn.posZ / 16.0);
        if (!entityIn.addedToChunk || entityIn.chunkCoordX != l || entityIn.chunkCoordY != i1 || entityIn.chunkCoordZ != j1) {
            if (entityIn.addedToChunk && this.isChunkLoaded(entityIn.chunkCoordX, entityIn.chunkCoordZ, true)) {
                this.getChunkFromChunkCoords(entityIn.chunkCoordX, entityIn.chunkCoordZ).removeEntityAtIndex(entityIn, entityIn.chunkCoordY);
            }
            if (!entityIn.setPositionNonDirty() && !this.isChunkLoaded(l, j1, true)) {
                entityIn.addedToChunk = false;
            } else {
                this.getChunkFromChunkCoords(l, j1).addEntity(entityIn);
            }
        }
        this.theProfiler.endSection();
        if (forceUpdate && entityIn.addedToChunk) {
            for (Entity entity : entityIn.getPassengers()) {
                if (!entity.isDead && entity.getRidingEntity() == entityIn) {
                    this.updateEntity(entity);
                    continue;
                }
                entity.dismountRidingEntity();
            }
        }
    }

    public boolean checkNoEntityCollision(AxisAlignedBB bb) {
        return this.checkNoEntityCollision(bb, null);
    }

    public boolean checkNoEntityCollision(AxisAlignedBB bb, @Nullable Entity entityIn) {
        List<Entity> list = this.getEntitiesWithinAABBExcludingEntity(null, bb);
        int i = 0;
        while (i < list.size()) {
            Entity entity = list.get(i);
            if (!entity.isDead && entity.preventEntitySpawning && entity != entityIn && (entityIn == null || entity.isRidingSameEntity(entityIn))) {
                return false;
            }
            ++i;
        }
        return true;
    }

    public boolean checkBlockCollision(AxisAlignedBB bb) {
        int i = MathHelper.floor(bb.minX);
        int j = MathHelper.ceil(bb.maxX);
        int k = MathHelper.floor(bb.minY);
        int l = MathHelper.ceil(bb.maxY);
        int i1 = MathHelper.floor(bb.minZ);
        int j1 = MathHelper.ceil(bb.maxZ);
        BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();
        int k1 = i;
        while (k1 < j) {
            int l1 = k;
            while (l1 < l) {
                int i2 = i1;
                while (i2 < j1) {
                    IBlockState iblockstate = this.getBlockState(blockpos$pooledmutableblockpos.setPos(k1, l1, i2));
                    if (iblockstate.getMaterial() != Material.AIR) {
                        blockpos$pooledmutableblockpos.release();
                        return true;
                    }
                    ++i2;
                }
                ++l1;
            }
            ++k1;
        }
        blockpos$pooledmutableblockpos.release();
        return false;
    }

    public boolean containsAnyLiquid(AxisAlignedBB bb) {
        int i = MathHelper.floor(bb.minX);
        int j = MathHelper.ceil(bb.maxX);
        int k = MathHelper.floor(bb.minY);
        int l = MathHelper.ceil(bb.maxY);
        int i1 = MathHelper.floor(bb.minZ);
        int j1 = MathHelper.ceil(bb.maxZ);
        BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();
        int k1 = i;
        while (k1 < j) {
            int l1 = k;
            while (l1 < l) {
                int i2 = i1;
                while (i2 < j1) {
                    IBlockState iblockstate = this.getBlockState(blockpos$pooledmutableblockpos.setPos(k1, l1, i2));
                    if (iblockstate.getMaterial().isLiquid()) {
                        blockpos$pooledmutableblockpos.release();
                        return true;
                    }
                    ++i2;
                }
                ++l1;
            }
            ++k1;
        }
        blockpos$pooledmutableblockpos.release();
        return false;
    }

    public boolean isFlammableWithin(AxisAlignedBB bb) {
        int j1;
        int i = MathHelper.floor(bb.minX);
        int j = MathHelper.ceil(bb.maxX);
        int k = MathHelper.floor(bb.minY);
        int l = MathHelper.ceil(bb.maxY);
        int i1 = MathHelper.floor(bb.minZ);
        if (this.isAreaLoaded(i, k, i1, j, l, j1 = MathHelper.ceil(bb.maxZ), true)) {
            BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();
            int k1 = i;
            while (k1 < j) {
                int l1 = k;
                while (l1 < l) {
                    int i2 = i1;
                    while (i2 < j1) {
                        Block block = this.getBlockState(blockpos$pooledmutableblockpos.setPos(k1, l1, i2)).getBlock();
                        if (block == Blocks.FIRE || block == Blocks.FLOWING_LAVA || block == Blocks.LAVA) {
                            blockpos$pooledmutableblockpos.release();
                            return true;
                        }
                        ++i2;
                    }
                    ++l1;
                }
                ++k1;
            }
            blockpos$pooledmutableblockpos.release();
        }
        return false;
    }

    public boolean handleMaterialAcceleration(AxisAlignedBB bb, Material materialIn, Entity entityIn) {
        int j1;
        int i = MathHelper.floor(bb.minX);
        int j = MathHelper.ceil(bb.maxX);
        int k = MathHelper.floor(bb.minY);
        int l = MathHelper.ceil(bb.maxY);
        int i1 = MathHelper.floor(bb.minZ);
        if (!this.isAreaLoaded(i, k, i1, j, l, j1 = MathHelper.ceil(bb.maxZ), true)) {
            return false;
        }
        boolean flag = false;
        Vec3d vec3d = Vec3d.ZERO;
        BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();
        int k1 = i;
        while (k1 < j) {
            int l1 = k;
            while (l1 < l) {
                int i2 = i1;
                while (i2 < j1) {
                    double d0;
                    blockpos$pooledmutableblockpos.setPos(k1, l1, i2);
                    IBlockState iblockstate = this.getBlockState(blockpos$pooledmutableblockpos);
                    Block block = iblockstate.getBlock();
                    if (iblockstate.getMaterial() == materialIn && (double)l >= (d0 = (double)((float)(l1 + 1) - BlockLiquid.getLiquidHeightPercent(iblockstate.getValue(BlockLiquid.LEVEL))))) {
                        flag = true;
                        vec3d = block.modifyAcceleration(this, blockpos$pooledmutableblockpos, entityIn, vec3d);
                    }
                    ++i2;
                }
                ++l1;
            }
            ++k1;
        }
        blockpos$pooledmutableblockpos.release();
        if (vec3d.lengthVector() > 0.0 && entityIn.isPushedByWater()) {
            vec3d = vec3d.normalize();
            double d1 = 0.014;
            entityIn.motionX += vec3d.xCoord * 0.014;
            entityIn.motionY += vec3d.yCoord * 0.014;
            entityIn.motionZ += vec3d.zCoord * 0.014;
        }
        return flag;
    }

    public boolean isMaterialInBB(AxisAlignedBB bb, Material materialIn) {
        int i = MathHelper.floor(bb.minX);
        int j = MathHelper.ceil(bb.maxX);
        int k = MathHelper.floor(bb.minY);
        int l = MathHelper.ceil(bb.maxY);
        int i1 = MathHelper.floor(bb.minZ);
        int j1 = MathHelper.ceil(bb.maxZ);
        BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();
        int k1 = i;
        while (k1 < j) {
            int l1 = k;
            while (l1 < l) {
                int i2 = i1;
                while (i2 < j1) {
                    if (this.getBlockState(blockpos$pooledmutableblockpos.setPos(k1, l1, i2)).getMaterial() == materialIn) {
                        blockpos$pooledmutableblockpos.release();
                        return true;
                    }
                    ++i2;
                }
                ++l1;
            }
            ++k1;
        }
        blockpos$pooledmutableblockpos.release();
        return false;
    }

    public Explosion createExplosion(@Nullable Entity entityIn, double x, double y, double z, float strength, boolean isSmoking) {
        return this.newExplosion(entityIn, x, y, z, strength, false, isSmoking);
    }

    public Explosion newExplosion(@Nullable Entity entityIn, double x, double y, double z, float strength, boolean isFlaming, boolean isSmoking) {
        Explosion explosion = new Explosion(this, entityIn, x, y, z, strength, isFlaming, isSmoking);
        explosion.doExplosionA();
        explosion.doExplosionB(true);
        return explosion;
    }

    public float getBlockDensity(Vec3d vec, AxisAlignedBB bb) {
        double d0 = 1.0 / ((bb.maxX - bb.minX) * 2.0 + 1.0);
        double d1 = 1.0 / ((bb.maxY - bb.minY) * 2.0 + 1.0);
        double d2 = 1.0 / ((bb.maxZ - bb.minZ) * 2.0 + 1.0);
        double d3 = (1.0 - Math.floor(1.0 / d0) * d0) / 2.0;
        double d4 = (1.0 - Math.floor(1.0 / d2) * d2) / 2.0;
        if (d0 >= 0.0 && d1 >= 0.0 && d2 >= 0.0) {
            int i = 0;
            int j = 0;
            float f = 0.0f;
            while (f <= 1.0f) {
                float f1 = 0.0f;
                while (f1 <= 1.0f) {
                    float f2 = 0.0f;
                    while (f2 <= 1.0f) {
                        double d5 = bb.minX + (bb.maxX - bb.minX) * (double)f;
                        double d6 = bb.minY + (bb.maxY - bb.minY) * (double)f1;
                        double d7 = bb.minZ + (bb.maxZ - bb.minZ) * (double)f2;
                        if (this.rayTraceBlocks(new Vec3d(d5 + d3, d6, d7 + d4), vec) == null) {
                            ++i;
                        }
                        ++j;
                        f2 = (float)((double)f2 + d2);
                    }
                    f1 = (float)((double)f1 + d1);
                }
                f = (float)((double)f + d0);
            }
            return (float)i / (float)j;
        }
        return 0.0f;
    }

    public boolean extinguishFire(@Nullable EntityPlayer player, BlockPos pos, EnumFacing side) {
        if (this.getBlockState(pos = pos.offset(side)).getBlock() == Blocks.FIRE) {
            this.playEvent(player, 1009, pos, 0);
            this.setBlockToAir(pos);
            return true;
        }
        return false;
    }

    public String getDebugLoadedEntities() {
        return "All: " + this.loadedEntityList.size();
    }

    public String getProviderName() {
        return this.chunkProvider.makeString();
    }

    @Override
    @Nullable
    public TileEntity getTileEntity(BlockPos pos) {
        if (this.isOutsideBuildHeight(pos)) {
            return null;
        }
        TileEntity tileentity = null;
        if (this.processingLoadedTiles) {
            tileentity = this.getPendingTileEntityAt(pos);
        }
        if (tileentity == null) {
            tileentity = this.getChunkFromBlockCoords(pos).getTileEntity(pos, Chunk.EnumCreateEntityType.IMMEDIATE);
        }
        if (tileentity == null) {
            tileentity = this.getPendingTileEntityAt(pos);
        }
        return tileentity;
    }

    @Nullable
    private TileEntity getPendingTileEntityAt(BlockPos p_189508_1_) {
        int i = 0;
        while (i < this.addedTileEntityList.size()) {
            TileEntity tileentity = this.addedTileEntityList.get(i);
            if (!tileentity.isInvalid() && tileentity.getPos().equals(p_189508_1_)) {
                return tileentity;
            }
            ++i;
        }
        return null;
    }

    public void setTileEntity(BlockPos pos, @Nullable TileEntity tileEntityIn) {
        if (!this.isOutsideBuildHeight(pos) && tileEntityIn != null && !tileEntityIn.isInvalid()) {
            if (this.processingLoadedTiles) {
                tileEntityIn.setPos(pos);
                Iterator<TileEntity> iterator = this.addedTileEntityList.iterator();
                while (iterator.hasNext()) {
                    TileEntity tileentity = iterator.next();
                    if (!tileentity.getPos().equals(pos)) continue;
                    tileentity.invalidate();
                    iterator.remove();
                }
                this.addedTileEntityList.add(tileEntityIn);
            } else {
                this.getChunkFromBlockCoords(pos).addTileEntity(pos, tileEntityIn);
                this.addTileEntity(tileEntityIn);
            }
        }
    }

    public void removeTileEntity(BlockPos pos) {
        TileEntity tileentity = this.getTileEntity(pos);
        if (tileentity != null && this.processingLoadedTiles) {
            tileentity.invalidate();
            this.addedTileEntityList.remove(tileentity);
        } else {
            if (tileentity != null) {
                this.addedTileEntityList.remove(tileentity);
                this.loadedTileEntityList.remove(tileentity);
                this.tickableTileEntities.remove(tileentity);
            }
            this.getChunkFromBlockCoords(pos).removeTileEntity(pos);
        }
    }

    public void markTileEntityForRemoval(TileEntity tileEntityIn) {
        this.tileEntitiesToBeRemoved.add(tileEntityIn);
    }

    public boolean isBlockFullCube(BlockPos pos) {
        AxisAlignedBB axisalignedbb = this.getBlockState(pos).getCollisionBoundingBox(this, pos);
        return axisalignedbb != Block.NULL_AABB && axisalignedbb.getAverageEdgeLength() >= 1.0;
    }

    public boolean isBlockNormalCube(BlockPos pos, boolean _default) {
        if (this.isOutsideBuildHeight(pos)) {
            return false;
        }
        Chunk chunk = this.chunkProvider.getLoadedChunk(pos.getX() >> 4, pos.getZ() >> 4);
        if (chunk != null && !chunk.isEmpty()) {
            IBlockState iblockstate = this.getBlockState(pos);
            return iblockstate.getMaterial().isOpaque() && iblockstate.isFullCube();
        }
        return _default;
    }

    public void calculateInitialSkylight() {
        int i = this.calculateSkylightSubtracted(1.0f);
        if (i != this.skylightSubtracted) {
            this.skylightSubtracted = i;
        }
    }

    public void setAllowedSpawnTypes(boolean hostile, boolean peaceful) {
        this.spawnHostileMobs = hostile;
        this.spawnPeacefulMobs = peaceful;
    }

    public void tick() {
        this.updateWeather();
    }

    protected void calculateInitialWeather() {
        if (this.worldInfo.isRaining()) {
            this.rainingStrength = 1.0f;
            if (this.worldInfo.isThundering()) {
                this.thunderingStrength = 1.0f;
            }
        }
    }

    protected void updateWeather() {
        if (this.provider.func_191066_m() && !this.isRemote) {
            boolean flag = this.getGameRules().getBoolean("doWeatherCycle");
            if (flag) {
                int j;
                int i = this.worldInfo.getCleanWeatherTime();
                if (i > 0) {
                    this.worldInfo.setCleanWeatherTime(--i);
                    this.worldInfo.setThunderTime(this.worldInfo.isThundering() ? 1 : 2);
                    this.worldInfo.setRainTime(this.worldInfo.isRaining() ? 1 : 2);
                }
                if ((j = this.worldInfo.getThunderTime()) <= 0) {
                    if (this.worldInfo.isThundering()) {
                        this.worldInfo.setThunderTime(this.rand.nextInt(12000) + 3600);
                    } else {
                        this.worldInfo.setThunderTime(this.rand.nextInt(168000) + 12000);
                    }
                } else {
                    this.worldInfo.setThunderTime(--j);
                    if (j <= 0) {
                        this.worldInfo.setThundering(!this.worldInfo.isThundering());
                    }
                }
                int k = this.worldInfo.getRainTime();
                if (k <= 0) {
                    if (this.worldInfo.isRaining()) {
                        this.worldInfo.setRainTime(this.rand.nextInt(12000) + 12000);
                    } else {
                        this.worldInfo.setRainTime(this.rand.nextInt(168000) + 12000);
                    }
                } else {
                    this.worldInfo.setRainTime(--k);
                    if (k <= 0) {
                        this.worldInfo.setRaining(!this.worldInfo.isRaining());
                    }
                }
            }
            this.prevThunderingStrength = this.thunderingStrength;
            this.thunderingStrength = this.worldInfo.isThundering() ? (float)((double)this.thunderingStrength + 0.01) : (float)((double)this.thunderingStrength - 0.01);
            this.thunderingStrength = MathHelper.clamp(this.thunderingStrength, 0.0f, 1.0f);
            this.prevRainingStrength = this.rainingStrength;
            this.rainingStrength = this.worldInfo.isRaining() ? (float)((double)this.rainingStrength + 0.01) : (float)((double)this.rainingStrength - 0.01);
            this.rainingStrength = MathHelper.clamp(this.rainingStrength, 0.0f, 1.0f);
        }
    }

    protected void playMoodSoundAndCheckLight(int p_147467_1_, int p_147467_2_, Chunk chunkIn) {
        chunkIn.enqueueRelightChecks();
    }

    protected void updateBlocks() {
    }

    public void immediateBlockTick(BlockPos pos, IBlockState state, Random random) {
        this.scheduledUpdatesAreImmediate = true;
        state.getBlock().updateTick(this, pos, state, random);
        this.scheduledUpdatesAreImmediate = false;
    }

    public boolean canBlockFreezeWater(BlockPos pos) {
        return this.canBlockFreeze(pos, false);
    }

    public boolean canBlockFreezeNoWater(BlockPos pos) {
        return this.canBlockFreeze(pos, true);
    }

    public boolean canBlockFreeze(BlockPos pos, boolean noWaterAdj) {
        IBlockState iblockstate;
        Block block;
        Biome biome = this.getBiome(pos);
        float f = biome.getFloatTemperature(pos);
        if (f >= 0.15f) {
            return false;
        }
        if (pos.getY() >= 0 && pos.getY() < 256 && this.getLightFor(EnumSkyBlock.BLOCK, pos) < 10 && ((block = (iblockstate = this.getBlockState(pos)).getBlock()) == Blocks.WATER || block == Blocks.FLOWING_WATER) && iblockstate.getValue(BlockLiquid.LEVEL) == 0) {
            boolean flag;
            if (!noWaterAdj) {
                return true;
            }
            boolean bl = flag = this.isWater(pos.west()) && this.isWater(pos.east()) && this.isWater(pos.north()) && this.isWater(pos.south());
            if (!flag) {
                return true;
            }
        }
        return false;
    }

    private boolean isWater(BlockPos pos) {
        return this.getBlockState(pos).getMaterial() == Material.WATER;
    }

    public boolean canSnowAt(BlockPos pos, boolean checkLight) {
        IBlockState iblockstate;
        Biome biome = this.getBiome(pos);
        float f = biome.getFloatTemperature(pos);
        if (f >= 0.15f) {
            return false;
        }
        if (!checkLight) {
            return true;
        }
        return pos.getY() >= 0 && pos.getY() < 256 && this.getLightFor(EnumSkyBlock.BLOCK, pos) < 10 && (iblockstate = this.getBlockState(pos)).getMaterial() == Material.AIR && Blocks.SNOW_LAYER.canPlaceBlockAt(this, pos);
    }

    public boolean checkLight(BlockPos pos) {
        boolean flag = false;
        if (this.provider.func_191066_m()) {
            flag |= this.checkLightFor(EnumSkyBlock.SKY, pos);
        }
        return flag |= this.checkLightFor(EnumSkyBlock.BLOCK, pos);
    }

    private int getRawLight(BlockPos pos, EnumSkyBlock lightType) {
        if (lightType == EnumSkyBlock.SKY && this.canSeeSky(pos)) {
            return 15;
        }
        IBlockState iblockstate = this.getBlockState(pos);
        int i = lightType == EnumSkyBlock.SKY ? 0 : iblockstate.getLightValue();
        int j = iblockstate.getLightOpacity();
        if (j >= 15 && iblockstate.getLightValue() > 0) {
            j = 1;
        }
        if (j < 1) {
            j = 1;
        }
        if (j >= 15) {
            return 0;
        }
        if (i >= 14) {
            return i;
        }
        BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();
        try {
            EnumFacing[] enumFacingArray = EnumFacing.values();
            int n = enumFacingArray.length;
            int n2 = 0;
            while (n2 < n) {
                EnumFacing enumfacing = enumFacingArray[n2];
                blockpos$pooledmutableblockpos.setPos(pos).move(enumfacing);
                int k = this.getLightFor(lightType, blockpos$pooledmutableblockpos) - j;
                if (k > i) {
                    i = k;
                }
                if (i >= 14) {
                    int l;
                    int n3 = l = i;
                    return n3;
                }
                ++n2;
            }
            int n4 = i;
            return n4;
        }
        finally {
            blockpos$pooledmutableblockpos.release();
        }
    }

    public boolean checkLightFor(EnumSkyBlock lightType, BlockPos pos) {
        if (!this.isAreaLoaded(pos, 17, false)) {
            return false;
        }
        int i = 0;
        int j = 0;
        this.theProfiler.startSection("getBrightness");
        int k = this.getLightFor(lightType, pos);
        int l = this.getRawLight(pos, lightType);
        int i1 = pos.getX();
        int j1 = pos.getY();
        int k1 = pos.getZ();
        if (l > k) {
            this.lightUpdateBlockList[j++] = 133152;
        } else if (l < k) {
            this.lightUpdateBlockList[j++] = 0x20820 | k << 18;
            while (i < j) {
                int l3;
                int k3;
                int j3;
                int l1 = this.lightUpdateBlockList[i++];
                int i2 = (l1 & 0x3F) - 32 + i1;
                int j2 = (l1 >> 6 & 0x3F) - 32 + j1;
                int k2 = (l1 >> 12 & 0x3F) - 32 + k1;
                int l2 = l1 >> 18 & 0xF;
                BlockPos blockpos = new BlockPos(i2, j2, k2);
                int i3 = this.getLightFor(lightType, blockpos);
                if (i3 != l2) continue;
                this.setLightFor(lightType, blockpos, 0);
                if (l2 <= 0 || (j3 = MathHelper.abs(i2 - i1)) + (k3 = MathHelper.abs(j2 - j1)) + (l3 = MathHelper.abs(k2 - k1)) >= 17) continue;
                BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();
                EnumFacing[] enumFacingArray = EnumFacing.values();
                int n = enumFacingArray.length;
                int n2 = 0;
                while (n2 < n) {
                    EnumFacing enumfacing = enumFacingArray[n2];
                    int i4 = i2 + enumfacing.getFrontOffsetX();
                    int j4 = j2 + enumfacing.getFrontOffsetY();
                    int k4 = k2 + enumfacing.getFrontOffsetZ();
                    blockpos$pooledmutableblockpos.setPos(i4, j4, k4);
                    int l4 = Math.max(1, this.getBlockState(blockpos$pooledmutableblockpos).getLightOpacity());
                    i3 = this.getLightFor(lightType, blockpos$pooledmutableblockpos);
                    if (i3 == l2 - l4 && j < this.lightUpdateBlockList.length) {
                        this.lightUpdateBlockList[j++] = i4 - i1 + 32 | j4 - j1 + 32 << 6 | k4 - k1 + 32 << 12 | l2 - l4 << 18;
                    }
                    ++n2;
                }
                blockpos$pooledmutableblockpos.release();
            }
            i = 0;
        }
        this.theProfiler.endSection();
        this.theProfiler.startSection("checkedPosition < toCheckCount");
        while (i < j) {
            boolean flag;
            int i5 = this.lightUpdateBlockList[i++];
            int j5 = (i5 & 0x3F) - 32 + i1;
            int k5 = (i5 >> 6 & 0x3F) - 32 + j1;
            int l5 = (i5 >> 12 & 0x3F) - 32 + k1;
            BlockPos blockpos1 = new BlockPos(j5, k5, l5);
            int i6 = this.getLightFor(lightType, blockpos1);
            int j6 = this.getRawLight(blockpos1, lightType);
            if (j6 == i6) continue;
            this.setLightFor(lightType, blockpos1, j6);
            if (j6 <= i6) continue;
            int k6 = Math.abs(j5 - i1);
            int l6 = Math.abs(k5 - j1);
            int i7 = Math.abs(l5 - k1);
            boolean bl = flag = j < this.lightUpdateBlockList.length - 6;
            if (k6 + l6 + i7 >= 17 || !flag) continue;
            if (this.getLightFor(lightType, blockpos1.west()) < j6) {
                this.lightUpdateBlockList[j++] = j5 - 1 - i1 + 32 + (k5 - j1 + 32 << 6) + (l5 - k1 + 32 << 12);
            }
            if (this.getLightFor(lightType, blockpos1.east()) < j6) {
                this.lightUpdateBlockList[j++] = j5 + 1 - i1 + 32 + (k5 - j1 + 32 << 6) + (l5 - k1 + 32 << 12);
            }
            if (this.getLightFor(lightType, blockpos1.down()) < j6) {
                this.lightUpdateBlockList[j++] = j5 - i1 + 32 + (k5 - 1 - j1 + 32 << 6) + (l5 - k1 + 32 << 12);
            }
            if (this.getLightFor(lightType, blockpos1.up()) < j6) {
                this.lightUpdateBlockList[j++] = j5 - i1 + 32 + (k5 + 1 - j1 + 32 << 6) + (l5 - k1 + 32 << 12);
            }
            if (this.getLightFor(lightType, blockpos1.north()) < j6) {
                this.lightUpdateBlockList[j++] = j5 - i1 + 32 + (k5 - j1 + 32 << 6) + (l5 - 1 - k1 + 32 << 12);
            }
            if (this.getLightFor(lightType, blockpos1.south()) >= j6) continue;
            this.lightUpdateBlockList[j++] = j5 - i1 + 32 + (k5 - j1 + 32 << 6) + (l5 + 1 - k1 + 32 << 12);
        }
        this.theProfiler.endSection();
        return true;
    }

    public boolean tickUpdates(boolean p_72955_1_) {
        return false;
    }

    @Nullable
    public List<NextTickListEntry> getPendingBlockUpdates(Chunk chunkIn, boolean p_72920_2_) {
        return null;
    }

    @Nullable
    public List<NextTickListEntry> getPendingBlockUpdates(StructureBoundingBox structureBB, boolean p_175712_2_) {
        return null;
    }

    public List<Entity> getEntitiesWithinAABBExcludingEntity(@Nullable Entity entityIn, AxisAlignedBB bb) {
        return this.getEntitiesInAABBexcluding(entityIn, bb, EntitySelectors.NOT_SPECTATING);
    }

    public List<Entity> getEntitiesInAABBexcluding(@Nullable Entity entityIn, AxisAlignedBB boundingBox, @Nullable Predicate<? super Entity> predicate) {
        ArrayList list = Lists.newArrayList();
        int i = MathHelper.floor((boundingBox.minX - 2.0) / 16.0);
        int j = MathHelper.floor((boundingBox.maxX + 2.0) / 16.0);
        int k = MathHelper.floor((boundingBox.minZ - 2.0) / 16.0);
        int l = MathHelper.floor((boundingBox.maxZ + 2.0) / 16.0);
        int i1 = i;
        while (i1 <= j) {
            int j1 = k;
            while (j1 <= l) {
                if (this.isChunkLoaded(i1, j1, true)) {
                    this.getChunkFromChunkCoords(i1, j1).getEntitiesWithinAABBForEntity(entityIn, boundingBox, list, predicate);
                }
                ++j1;
            }
            ++i1;
        }
        return list;
    }

    public <T extends Entity> List<T> getEntities(Class<? extends T> entityType, Predicate<? super T> filter) {
        ArrayList list = Lists.newArrayList();
        for (Entity entity : this.loadedEntityList) {
            if (!entityType.isAssignableFrom(entity.getClass()) || !filter.apply((Object)entity)) continue;
            list.add(entity);
        }
        return list;
    }

    public <T extends Entity> List<T> getPlayers(Class<? extends T> playerType, Predicate<? super T> filter) {
        ArrayList list = Lists.newArrayList();
        for (Entity entity : this.playerEntities) {
            if (!playerType.isAssignableFrom(entity.getClass()) || !filter.apply((Object)entity)) continue;
            list.add(entity);
        }
        return list;
    }

    public <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> classEntity, AxisAlignedBB bb) {
        return this.getEntitiesWithinAABB(classEntity, bb, EntitySelectors.NOT_SPECTATING);
    }

    public <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> clazz, AxisAlignedBB aabb, @Nullable Predicate<? super T> filter) {
        int i = MathHelper.floor((aabb.minX - 2.0) / 16.0);
        int j = MathHelper.ceil((aabb.maxX + 2.0) / 16.0);
        int k = MathHelper.floor((aabb.minZ - 2.0) / 16.0);
        int l = MathHelper.ceil((aabb.maxZ + 2.0) / 16.0);
        ArrayList list = Lists.newArrayList();
        int i1 = i;
        while (i1 < j) {
            int j1 = k;
            while (j1 < l) {
                if (this.isChunkLoaded(i1, j1, true)) {
                    this.getChunkFromChunkCoords(i1, j1).getEntitiesOfTypeWithinAAAB(clazz, aabb, list, filter);
                }
                ++j1;
            }
            ++i1;
        }
        return list;
    }

    @Nullable
    public <T extends Entity> T findNearestEntityWithinAABB(Class<? extends T> entityType, AxisAlignedBB aabb, T closestTo) {
        List<T> list = this.getEntitiesWithinAABB(entityType, aabb);
        Entity t = null;
        double d0 = Double.MAX_VALUE;
        int i = 0;
        while (i < list.size()) {
            double d1;
            Entity t1 = (Entity)list.get(i);
            if (t1 != closestTo && EntitySelectors.NOT_SPECTATING.apply((Object)t1) && (d1 = closestTo.getDistanceSqToEntity(t1)) <= d0) {
                t = t1;
                d0 = d1;
            }
            ++i;
        }
        return (T)t;
    }

    @Nullable
    public Entity getEntityByID(int id) {
        return this.entitiesById.lookup(id);
    }

    public List<Entity> getLoadedEntityList() {
        return this.loadedEntityList;
    }

    public void markChunkDirty(BlockPos pos, TileEntity unusedTileEntity) {
        if (this.isBlockLoaded(pos)) {
            this.getChunkFromBlockCoords(pos).setChunkModified();
        }
    }

    public int countEntities(Class<?> entityType) {
        int i = 0;
        for (Entity entity : this.loadedEntityList) {
            if (entity instanceof EntityLiving && ((EntityLiving)entity).isNoDespawnRequired() || !entityType.isAssignableFrom(entity.getClass())) continue;
            ++i;
        }
        return i;
    }

    public void loadEntities(Collection<Entity> entityCollection) {
        this.loadedEntityList.addAll(entityCollection);
        for (Entity entity : entityCollection) {
            this.onEntityAdded(entity);
        }
    }

    public void unloadEntities(Collection<Entity> entityCollection) {
        this.unloadedEntityList.addAll(entityCollection);
    }

    public boolean func_190527_a(Block p_190527_1_, BlockPos p_190527_2_, boolean p_190527_3_, EnumFacing p_190527_4_, @Nullable Entity p_190527_5_) {
        AxisAlignedBB axisalignedbb;
        IBlockState iblockstate = this.getBlockState(p_190527_2_);
        AxisAlignedBB axisAlignedBB = axisalignedbb = p_190527_3_ ? null : p_190527_1_.getDefaultState().getCollisionBoundingBox(this, p_190527_2_);
        if (axisalignedbb != Block.NULL_AABB && !this.checkNoEntityCollision(axisalignedbb.offset(p_190527_2_), p_190527_5_)) {
            return false;
        }
        if (iblockstate.getMaterial() == Material.CIRCUITS && p_190527_1_ == Blocks.ANVIL) {
            return true;
        }
        return iblockstate.getMaterial().isReplaceable() && p_190527_1_.canPlaceBlockOnSide(this, p_190527_2_, p_190527_4_);
    }

    public int getSeaLevel() {
        return this.seaLevel;
    }

    public void setSeaLevel(int seaLevelIn) {
        this.seaLevel = seaLevelIn;
    }

    @Override
    public int getStrongPower(BlockPos pos, EnumFacing direction) {
        return this.getBlockState(pos).getStrongPower(this, pos, direction);
    }

    @Override
    public WorldType getWorldType() {
        return this.worldInfo.getTerrainType();
    }

    public int getStrongPower(BlockPos pos) {
        int i = 0;
        if ((i = Math.max(i, this.getStrongPower(pos.down(), EnumFacing.DOWN))) >= 15) {
            return i;
        }
        if ((i = Math.max(i, this.getStrongPower(pos.up(), EnumFacing.UP))) >= 15) {
            return i;
        }
        if ((i = Math.max(i, this.getStrongPower(pos.north(), EnumFacing.NORTH))) >= 15) {
            return i;
        }
        if ((i = Math.max(i, this.getStrongPower(pos.south(), EnumFacing.SOUTH))) >= 15) {
            return i;
        }
        if ((i = Math.max(i, this.getStrongPower(pos.west(), EnumFacing.WEST))) >= 15) {
            return i;
        }
        return (i = Math.max(i, this.getStrongPower(pos.east(), EnumFacing.EAST))) >= 15 ? i : i;
    }

    public boolean isSidePowered(BlockPos pos, EnumFacing side) {
        return this.getRedstonePower(pos, side) > 0;
    }

    public int getRedstonePower(BlockPos pos, EnumFacing facing) {
        IBlockState iblockstate = this.getBlockState(pos);
        return iblockstate.isNormalCube() ? this.getStrongPower(pos) : iblockstate.getWeakPower(this, pos, facing);
    }

    public boolean isBlockPowered(BlockPos pos) {
        if (this.getRedstonePower(pos.down(), EnumFacing.DOWN) > 0) {
            return true;
        }
        if (this.getRedstonePower(pos.up(), EnumFacing.UP) > 0) {
            return true;
        }
        if (this.getRedstonePower(pos.north(), EnumFacing.NORTH) > 0) {
            return true;
        }
        if (this.getRedstonePower(pos.south(), EnumFacing.SOUTH) > 0) {
            return true;
        }
        if (this.getRedstonePower(pos.west(), EnumFacing.WEST) > 0) {
            return true;
        }
        return this.getRedstonePower(pos.east(), EnumFacing.EAST) > 0;
    }

    public int isBlockIndirectlyGettingPowered(BlockPos pos) {
        int i = 0;
        EnumFacing[] enumFacingArray = EnumFacing.values();
        int n = enumFacingArray.length;
        int n2 = 0;
        while (n2 < n) {
            EnumFacing enumfacing = enumFacingArray[n2];
            int j = this.getRedstonePower(pos.offset(enumfacing), enumfacing);
            if (j >= 15) {
                return 15;
            }
            if (j > i) {
                i = j;
            }
            ++n2;
        }
        return i;
    }

    @Nullable
    public EntityPlayer getClosestPlayerToEntity(Entity entityIn, double distance) {
        return this.getClosestPlayer(entityIn.posX, entityIn.posY, entityIn.posZ, distance, false);
    }

    @Nullable
    public EntityPlayer getNearestPlayerNotCreative(Entity entityIn, double distance) {
        return this.getClosestPlayer(entityIn.posX, entityIn.posY, entityIn.posZ, distance, true);
    }

    @Nullable
    public EntityPlayer getClosestPlayer(double posX, double posY, double posZ, double distance, boolean spectator) {
        Predicate<Entity> predicate = spectator ? EntitySelectors.CAN_AI_TARGET : EntitySelectors.NOT_SPECTATING;
        return this.func_190525_a(posX, posY, posZ, distance, predicate);
    }

    @Nullable
    public EntityPlayer func_190525_a(double p_190525_1_, double p_190525_3_, double p_190525_5_, double p_190525_7_, Predicate<Entity> p_190525_9_) {
        double d0 = -1.0;
        EntityPlayer entityplayer = null;
        int i = 0;
        while (i < this.playerEntities.size()) {
            EntityPlayer entityplayer1 = this.playerEntities.get(i);
            if (p_190525_9_.apply((Object)entityplayer1)) {
                double d1 = entityplayer1.getDistanceSq(p_190525_1_, p_190525_3_, p_190525_5_);
                if ((p_190525_7_ < 0.0 || d1 < p_190525_7_ * p_190525_7_) && (d0 == -1.0 || d1 < d0)) {
                    d0 = d1;
                    entityplayer = entityplayer1;
                }
            }
            ++i;
        }
        return entityplayer;
    }

    public boolean isAnyPlayerWithinRangeAt(double x, double y, double z, double range) {
        int i = 0;
        while (i < this.playerEntities.size()) {
            EntityPlayer entityplayer = this.playerEntities.get(i);
            if (EntitySelectors.NOT_SPECTATING.apply((Object)entityplayer)) {
                double d0 = entityplayer.getDistanceSq(x, y, z);
                if (range < 0.0 || d0 < range * range) {
                    return true;
                }
            }
            ++i;
        }
        return false;
    }

    @Nullable
    public EntityPlayer getNearestAttackablePlayer(Entity entityIn, double maxXZDistance, double maxYDistance) {
        return this.getNearestAttackablePlayer(entityIn.posX, entityIn.posY, entityIn.posZ, maxXZDistance, maxYDistance, null, null);
    }

    @Nullable
    public EntityPlayer getNearestAttackablePlayer(BlockPos pos, double maxXZDistance, double maxYDistance) {
        return this.getNearestAttackablePlayer((float)pos.getX() + 0.5f, (float)pos.getY() + 0.5f, (float)pos.getZ() + 0.5f, maxXZDistance, maxYDistance, null, null);
    }

    @Nullable
    public EntityPlayer getNearestAttackablePlayer(double posX, double posY, double posZ, double maxXZDistance, double maxYDistance, @Nullable Function<EntityPlayer, Double> playerToDouble, @Nullable Predicate<EntityPlayer> p_184150_12_) {
        double d0 = -1.0;
        EntityPlayer entityplayer = null;
        int i = 0;
        while (i < this.playerEntities.size()) {
            EntityPlayer entityplayer1 = this.playerEntities.get(i);
            if (!entityplayer1.capabilities.disableDamage && entityplayer1.isEntityAlive() && !entityplayer1.isSpectator() && (p_184150_12_ == null || p_184150_12_.apply((Object)entityplayer1))) {
                double d1 = entityplayer1.getDistanceSq(posX, entityplayer1.posY, posZ);
                double d2 = maxXZDistance;
                if (entityplayer1.isSneaking()) {
                    d2 = maxXZDistance * (double)0.8f;
                }
                if (entityplayer1.isInvisible()) {
                    float f = entityplayer1.getArmorVisibility();
                    if (f < 0.1f) {
                        f = 0.1f;
                    }
                    d2 *= (double)(0.7f * f);
                }
                if (playerToDouble != null) {
                    d2 *= ((Double)MoreObjects.firstNonNull((Object)((Double)playerToDouble.apply((Object)entityplayer1)), (Object)1.0)).doubleValue();
                }
                if ((maxYDistance < 0.0 || Math.abs(entityplayer1.posY - posY) < maxYDistance * maxYDistance) && (maxXZDistance < 0.0 || d1 < d2 * d2) && (d0 == -1.0 || d1 < d0)) {
                    d0 = d1;
                    entityplayer = entityplayer1;
                }
            }
            ++i;
        }
        return entityplayer;
    }

    @Nullable
    public EntityPlayer getPlayerEntityByName(String name) {
        int i = 0;
        while (i < this.playerEntities.size()) {
            EntityPlayer entityplayer = this.playerEntities.get(i);
            if (name.equals(entityplayer.getName())) {
                return entityplayer;
            }
            ++i;
        }
        return null;
    }

    @Nullable
    public EntityPlayer getPlayerEntityByUUID(UUID uuid) {
        int i = 0;
        while (i < this.playerEntities.size()) {
            EntityPlayer entityplayer = this.playerEntities.get(i);
            if (uuid.equals(entityplayer.getUniqueID())) {
                return entityplayer;
            }
            ++i;
        }
        return null;
    }

    public void sendQuittingDisconnectingPacket() {
    }

    public void checkSessionLock() throws MinecraftException {
        this.saveHandler.checkSessionLock();
    }

    public void setTotalWorldTime(long worldTime) {
        this.worldInfo.setWorldTotalTime(worldTime);
    }

    public long getSeed() {
        return this.worldInfo.getSeed();
    }

    public long getTotalWorldTime() {
        return this.worldInfo.getWorldTotalTime();
    }

    public long getWorldTime() {
        return this.worldInfo.getWorldTime();
    }

    public void setWorldTime(long time) {
        this.worldInfo.setWorldTime(time);
    }

    public BlockPos getSpawnPoint() {
        BlockPos blockpos = new BlockPos(this.worldInfo.getSpawnX(), this.worldInfo.getSpawnY(), this.worldInfo.getSpawnZ());
        if (!this.getWorldBorder().contains(blockpos)) {
            blockpos = this.getHeight(new BlockPos(this.getWorldBorder().getCenterX(), 0.0, this.getWorldBorder().getCenterZ()));
        }
        return blockpos;
    }

    public void setSpawnPoint(BlockPos pos) {
        this.worldInfo.setSpawn(pos);
    }

    public void joinEntityInSurroundings(Entity entityIn) {
        int i = MathHelper.floor(entityIn.posX / 16.0);
        int j = MathHelper.floor(entityIn.posZ / 16.0);
        int k = 2;
        int l = -2;
        while (l <= 2) {
            int i1 = -2;
            while (i1 <= 2) {
                this.getChunkFromChunkCoords(i + l, j + i1);
                ++i1;
            }
            ++l;
        }
        if (!this.loadedEntityList.contains(entityIn)) {
            this.loadedEntityList.add(entityIn);
        }
    }

    public boolean isBlockModifiable(EntityPlayer player, BlockPos pos) {
        return true;
    }

    public void setEntityState(Entity entityIn, byte state) {
    }

    public IChunkProvider getChunkProvider() {
        return this.chunkProvider;
    }

    public void addBlockEvent(BlockPos pos, Block blockIn, int eventID, int eventParam) {
        this.getBlockState(pos).onBlockEventReceived(this, pos, eventID, eventParam);
    }

    public ISaveHandler getSaveHandler() {
        return this.saveHandler;
    }

    public WorldInfo getWorldInfo() {
        return this.worldInfo;
    }

    public GameRules getGameRules() {
        return this.worldInfo.getGameRulesInstance();
    }

    public void updateAllPlayersSleepingFlag() {
    }

    public float getThunderStrength(float delta) {
        return (this.prevThunderingStrength + (this.thunderingStrength - this.prevThunderingStrength) * delta) * this.getRainStrength(delta);
    }

    public void setThunderStrength(float strength) {
        this.prevThunderingStrength = strength;
        this.thunderingStrength = strength;
    }

    public float getRainStrength(float delta) {
        return this.prevRainingStrength + (this.rainingStrength - this.prevRainingStrength) * delta;
    }

    public void setRainStrength(float strength) {
        this.prevRainingStrength = strength;
        this.rainingStrength = strength;
    }

    public boolean isThundering() {
        return (double)this.getThunderStrength(1.0f) > 0.9;
    }

    public boolean isRaining() {
        return (double)this.getRainStrength(1.0f) > 0.2;
    }

    public boolean isRainingAt(BlockPos strikePosition) {
        if (!this.isRaining()) {
            return false;
        }
        if (!this.canSeeSky(strikePosition)) {
            return false;
        }
        if (this.getPrecipitationHeight(strikePosition).getY() > strikePosition.getY()) {
            return false;
        }
        Biome biome = this.getBiome(strikePosition);
        if (biome.getEnableSnow()) {
            return false;
        }
        return this.canSnowAt(strikePosition, false) ? false : biome.canRain();
    }

    public boolean isBlockinHighHumidity(BlockPos pos) {
        Biome biome = this.getBiome(pos);
        return biome.isHighHumidity();
    }

    @Nullable
    public MapStorage getMapStorage() {
        return this.mapStorage;
    }

    public void setItemData(String dataID, WorldSavedData worldSavedDataIn) {
        this.mapStorage.setData(dataID, worldSavedDataIn);
    }

    @Nullable
    public WorldSavedData loadItemData(Class<? extends WorldSavedData> clazz, String dataID) {
        return this.mapStorage.getOrLoadData(clazz, dataID);
    }

    public int getUniqueDataId(String key) {
        return this.mapStorage.getUniqueDataId(key);
    }

    public void playBroadcastSound(int id, BlockPos pos, int data) {
        int i = 0;
        while (i < this.eventListeners.size()) {
            this.eventListeners.get(i).broadcastSound(id, pos, data);
            ++i;
        }
    }

    public void playEvent(int type, BlockPos pos, int data) {
        this.playEvent(null, type, pos, data);
    }

    public void playEvent(@Nullable EntityPlayer player, int type, BlockPos pos, int data) {
        try {
            int i = 0;
            while (i < this.eventListeners.size()) {
                this.eventListeners.get(i).playEvent(player, type, pos, data);
                ++i;
            }
        }
        catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Playing level event");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Level event being played");
            crashreportcategory.addCrashSection("Block coordinates", CrashReportCategory.getCoordinateInfo(pos));
            crashreportcategory.addCrashSection("Event source", player);
            crashreportcategory.addCrashSection("Event type", type);
            crashreportcategory.addCrashSection("Event data", data);
            throw new ReportedException(crashreport);
        }
    }

    public int getHeight() {
        return 256;
    }

    public int getActualHeight() {
        return this.provider.getHasNoSky() ? 128 : 256;
    }

    public Random setRandomSeed(int p_72843_1_, int p_72843_2_, int p_72843_3_) {
        long i = (long)p_72843_1_ * 341873128712L + (long)p_72843_2_ * 132897987541L + this.getWorldInfo().getSeed() + (long)p_72843_3_;
        this.rand.setSeed(i);
        return this.rand;
    }

    public double getHorizon() {
        return this.worldInfo.getTerrainType() == WorldType.FLAT ? 0.0 : 63.0;
    }

    public CrashReportCategory addWorldInfoToCrashReport(CrashReport report) {
        CrashReportCategory crashreportcategory = report.makeCategoryDepth("Affected level", 1);
        crashreportcategory.addCrashSection("Level name", this.worldInfo == null ? "????" : this.worldInfo.getWorldName());
        crashreportcategory.setDetail("All players", new ICrashReportDetail<String>(){

            @Override
            public String call() {
                return String.valueOf(World.this.playerEntities.size()) + " total; " + World.this.playerEntities;
            }
        });
        crashreportcategory.setDetail("Chunk stats", new ICrashReportDetail<String>(){

            @Override
            public String call() {
                return World.this.chunkProvider.makeString();
            }
        });
        try {
            this.worldInfo.addToCrashReport(crashreportcategory);
        }
        catch (Throwable throwable) {
            crashreportcategory.addCrashSectionThrowable("Level Data Unobtainable", throwable);
        }
        return crashreportcategory;
    }

    public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
        int i = 0;
        while (i < this.eventListeners.size()) {
            IWorldEventListener iworldeventlistener = this.eventListeners.get(i);
            iworldeventlistener.sendBlockBreakProgress(breakerId, pos, progress);
            ++i;
        }
    }

    public Calendar getCurrentDate() {
        if (this.getTotalWorldTime() % 600L == 0L) {
            this.theCalendar.setTimeInMillis(MinecraftServer.getCurrentTimeMillis());
        }
        return this.theCalendar;
    }

    public void makeFireworks(double x, double y, double z, double motionX, double motionY, double motionZ, @Nullable NBTTagCompound compund) {
    }

    public Scoreboard getScoreboard() {
        return this.worldScoreboard;
    }

    public void updateComparatorOutputLevel(BlockPos pos, Block blockIn) {
        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            BlockPos blockpos = pos.offset(enumfacing);
            if (!this.isBlockLoaded(blockpos)) continue;
            IBlockState iblockstate = this.getBlockState(blockpos);
            if (Blocks.UNPOWERED_COMPARATOR.isSameDiode(iblockstate)) {
                iblockstate.neighborChanged(this, blockpos, blockIn, pos);
                continue;
            }
            if (!iblockstate.isNormalCube() || !Blocks.UNPOWERED_COMPARATOR.isSameDiode(iblockstate = this.getBlockState(blockpos = blockpos.offset(enumfacing)))) continue;
            iblockstate.neighborChanged(this, blockpos, blockIn, pos);
        }
    }

    public DifficultyInstance getDifficultyForLocation(BlockPos pos) {
        long i = 0L;
        float f = 0.0f;
        if (this.isBlockLoaded(pos)) {
            f = this.getCurrentMoonPhaseFactor();
            i = this.getChunkFromBlockCoords(pos).getInhabitedTime();
        }
        return new DifficultyInstance(this.getDifficulty(), this.getWorldTime(), i, f);
    }

    public EnumDifficulty getDifficulty() {
        return this.getWorldInfo().getDifficulty();
    }

    public int getSkylightSubtracted() {
        return this.skylightSubtracted;
    }

    public void setSkylightSubtracted(int newSkylightSubtracted) {
        this.skylightSubtracted = newSkylightSubtracted;
    }

    public int getLastLightningBolt() {
        return this.lastLightningBolt;
    }

    public void setLastLightningBolt(int lastLightningBoltIn) {
        this.lastLightningBolt = lastLightningBoltIn;
    }

    public VillageCollection getVillageCollection() {
        return this.villageCollectionObj;
    }

    public WorldBorder getWorldBorder() {
        return this.worldBorder;
    }

    public boolean isSpawnChunk(int x, int z) {
        BlockPos blockpos = this.getSpawnPoint();
        int i = x * 16 + 8 - blockpos.getX();
        int j = z * 16 + 8 - blockpos.getZ();
        int k = 128;
        return i >= -128 && i <= 128 && j >= -128 && j <= 128;
    }

    public void sendPacketToServer(Packet<?> packetIn) {
        throw new UnsupportedOperationException("Can't send packets to server unless you're on the client.");
    }

    public LootTableManager getLootTableManager() {
        return this.lootTable;
    }

    @Nullable
    public BlockPos func_190528_a(String p_190528_1_, BlockPos p_190528_2_, boolean p_190528_3_) {
        return null;
    }
}

