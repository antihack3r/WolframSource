/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Sets;
import java.nio.FloatBuffer;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.chunk.ChunkCompileTaskGenerator;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.src.BlockPosM;
import net.minecraft.src.ChunkCacheOF;
import net.minecraft.src.Config;
import net.minecraft.src.Reflector;
import net.minecraft.src.ReflectorForge;
import net.minecraft.src.RenderEnv;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import shadersmod.client.SVertexBuilder;

public class RenderChunk {
    private World world;
    private final RenderGlobal renderGlobal;
    public static int renderChunksUpdated;
    public CompiledChunk compiledChunk = CompiledChunk.DUMMY;
    private final ReentrantLock lockCompileTask = new ReentrantLock();
    private final ReentrantLock lockCompiledChunk = new ReentrantLock();
    private ChunkCompileTaskGenerator compileTask;
    private final Set<TileEntity> setTileEntities = Sets.newHashSet();
    private final int index;
    private final FloatBuffer modelviewMatrix = GLAllocation.createDirectFloatBuffer(16);
    private final VertexBuffer[] vertexBuffers = new VertexBuffer[BlockRenderLayer.values().length];
    public AxisAlignedBB boundingBox;
    private int frameIndex = -1;
    private boolean needsUpdate = true;
    private final BlockPos.MutableBlockPos position = new BlockPos.MutableBlockPos(-1, -1, -1);
    private final BlockPos.MutableBlockPos[] mapEnumFacing = new BlockPos.MutableBlockPos[6];
    private boolean needsUpdateCustom;
    private static BlockRenderLayer[] ENUM_WORLD_BLOCK_LAYERS;
    private BlockRenderLayer[] blockLayersSingle = new BlockRenderLayer[1];
    private boolean isMipmaps = Config.isMipmaps();
    private boolean fixBlockLayer = !Reflector.BetterFoliageClient.exists();
    private boolean playerUpdate = false;
    private RenderChunk[] renderChunksOfset16 = new RenderChunk[6];
    private Chunk chunk;

    static {
        ENUM_WORLD_BLOCK_LAYERS = BlockRenderLayer.values();
    }

    public RenderChunk(World p_i47120_1_, RenderGlobal p_i47120_2_, int p_i47120_3_) {
        int i = 0;
        while (i < this.mapEnumFacing.length) {
            this.mapEnumFacing[i] = new BlockPos.MutableBlockPos();
            ++i;
        }
        this.world = p_i47120_1_;
        this.renderGlobal = p_i47120_2_;
        this.index = p_i47120_3_;
        if (OpenGlHelper.useVbo()) {
            int j = 0;
            while (j < BlockRenderLayer.values().length) {
                this.vertexBuffers[j] = new VertexBuffer(DefaultVertexFormats.BLOCK);
                ++j;
            }
        }
    }

    public boolean setFrameIndex(int frameIndexIn) {
        if (this.frameIndex == frameIndexIn) {
            return false;
        }
        this.frameIndex = frameIndexIn;
        return true;
    }

    public VertexBuffer getVertexBufferByLayer(int layer) {
        return this.vertexBuffers[layer];
    }

    public void setPosition(int p_189562_1_, int p_189562_2_, int p_189562_3_) {
        if (p_189562_1_ != this.position.getX() || p_189562_2_ != this.position.getY() || p_189562_3_ != this.position.getZ()) {
            this.stopCompileTask();
            this.position.setPos(p_189562_1_, p_189562_2_, p_189562_3_);
            this.boundingBox = new AxisAlignedBB(p_189562_1_, p_189562_2_, p_189562_3_, p_189562_1_ + 16, p_189562_2_ + 16, p_189562_3_ + 16);
            EnumFacing[] enumFacingArray = EnumFacing.VALUES;
            int n = EnumFacing.VALUES.length;
            int n2 = 0;
            while (n2 < n) {
                EnumFacing enumfacing = enumFacingArray[n2];
                this.mapEnumFacing[enumfacing.ordinal()].setPos(this.position).move(enumfacing, 16);
                this.renderChunksOfset16[enumfacing.ordinal()] = null;
                ++n2;
            }
            this.chunk = null;
            this.initModelviewMatrix();
        }
    }

    public void resortTransparency(float x, float y, float z, ChunkCompileTaskGenerator generator) {
        CompiledChunk compiledchunk = generator.getCompiledChunk();
        if (compiledchunk.getState() != null && !compiledchunk.isLayerEmpty(BlockRenderLayer.TRANSLUCENT)) {
            BufferBuilder bufferbuilder = generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(BlockRenderLayer.TRANSLUCENT);
            this.preRenderBlocks(bufferbuilder, this.position);
            bufferbuilder.setVertexState(compiledchunk.getState());
            this.postRenderBlocks(BlockRenderLayer.TRANSLUCENT, x, y, z, bufferbuilder, compiledchunk);
        }
    }

    public void rebuildChunk(float x, float y, float z, ChunkCompileTaskGenerator generator) {
        CompiledChunk compiledchunk = new CompiledChunk();
        boolean i = true;
        BlockPos.MutableBlockPos blockpos = this.position;
        BlockPos blockpos1 = ((BlockPos)blockpos).add(15, 15, 15);
        generator.getLock().lock();
        try {
            if (generator.getStatus() != ChunkCompileTaskGenerator.Status.COMPILING) {
                return;
            }
            generator.setCompiledChunk(compiledchunk);
        }
        finally {
            generator.getLock().unlock();
        }
        VisGraph lvt_9_1_ = new VisGraph();
        HashSet lvt_10_1_ = Sets.newHashSet();
        if (this.world != null) {
            ChunkCacheOF chunkcacheof = this.makeChunkCacheOF();
            if (!chunkcacheof.isEmpty()) {
                ++renderChunksUpdated;
                chunkcacheof.renderStart();
                boolean[] aboolean = new boolean[ENUM_WORLD_BLOCK_LAYERS.length];
                BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
                boolean flag = Reflector.ForgeBlock_canRenderInLayer.exists();
                boolean flag1 = Reflector.ForgeHooksClient_setRenderLayer.exists();
                for (Object blockposm0 : BlockPosM.getAllInBoxMutable(blockpos, blockpos1)) {
                    BlockRenderLayer[] ablockrenderlayer;
                    TileEntitySpecialRenderer<TileEntity> tileentityspecialrenderer;
                    TileEntity tileentity;
                    BlockPosM blockposm = (BlockPosM)blockposm0;
                    IBlockState iblockstate = chunkcacheof.getBlockState(blockposm);
                    Block block = iblockstate.getBlock();
                    if (iblockstate.isOpaqueCube()) {
                        lvt_9_1_.setOpaqueCube(blockposm);
                    }
                    if (ReflectorForge.blockHasTileEntity(iblockstate) && (tileentity = chunkcacheof.getTileEntity(blockposm, Chunk.EnumCreateEntityType.CHECK)) != null && (tileentityspecialrenderer = TileEntityRendererDispatcher.instance.getSpecialRenderer(tileentity)) != null) {
                        if (tileentityspecialrenderer.isGlobalRenderer(tileentity)) {
                            lvt_10_1_.add(tileentity);
                        } else {
                            compiledchunk.addTileEntity(tileentity);
                        }
                    }
                    if (flag) {
                        ablockrenderlayer = ENUM_WORLD_BLOCK_LAYERS;
                    } else {
                        ablockrenderlayer = this.blockLayersSingle;
                        ablockrenderlayer[0] = block.getBlockLayer();
                    }
                    int j = 0;
                    while (j < ablockrenderlayer.length) {
                        boolean flag2;
                        BlockRenderLayer blockrenderlayer = ablockrenderlayer[j];
                        if (!flag || (flag2 = Reflector.callBoolean(block, Reflector.ForgeBlock_canRenderInLayer, new Object[]{iblockstate, blockrenderlayer}))) {
                            if (flag1) {
                                Reflector.callVoid(Reflector.ForgeHooksClient_setRenderLayer, new Object[]{blockrenderlayer});
                            }
                            if (this.fixBlockLayer) {
                                blockrenderlayer = this.fixBlockLayer(block, blockrenderlayer);
                            }
                            int k = blockrenderlayer.ordinal();
                            if (block.getDefaultState().getRenderType() != EnumBlockRenderType.INVISIBLE) {
                                BufferBuilder bufferbuilder = generator.getRegionRenderCacheBuilder().getWorldRendererByLayerId(k);
                                bufferbuilder.setBlockLayer(blockrenderlayer);
                                RenderEnv renderenv = bufferbuilder.getRenderEnv(chunkcacheof, iblockstate, blockposm);
                                renderenv.setRegionRenderCacheBuilder(generator.getRegionRenderCacheBuilder());
                                if (!compiledchunk.isLayerStarted(blockrenderlayer)) {
                                    compiledchunk.setLayerStarted(blockrenderlayer);
                                    this.preRenderBlocks(bufferbuilder, blockpos);
                                }
                                int n = k;
                                aboolean[n] = aboolean[n] | blockrendererdispatcher.renderBlock(iblockstate, blockposm, chunkcacheof, bufferbuilder);
                                if (renderenv.isOverlaysRendered()) {
                                    this.postRenderOverlays(generator.getRegionRenderCacheBuilder(), compiledchunk, aboolean);
                                    renderenv.setOverlaysRendered(false);
                                }
                            }
                        }
                        ++j;
                    }
                    if (!flag1) continue;
                    Reflector.callVoid(Reflector.ForgeHooksClient_setRenderLayer, null);
                }
                BlockRenderLayer[] blockRenderLayerArray = ENUM_WORLD_BLOCK_LAYERS;
                int n = ENUM_WORLD_BLOCK_LAYERS.length;
                int n2 = 0;
                while (n2 < n) {
                    BlockRenderLayer blockrenderlayer1 = blockRenderLayerArray[n2];
                    if (aboolean[blockrenderlayer1.ordinal()]) {
                        compiledchunk.setLayerUsed(blockrenderlayer1);
                    }
                    if (compiledchunk.isLayerStarted(blockrenderlayer1)) {
                        if (Config.isShaders()) {
                            SVertexBuilder.calcNormalChunkLayer(generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(blockrenderlayer1));
                        }
                        this.postRenderBlocks(blockrenderlayer1, x, y, z, generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(blockrenderlayer1), compiledchunk);
                    }
                    ++n2;
                }
                chunkcacheof.renderFinish();
            }
            compiledchunk.setVisibility(lvt_9_1_.computeVisibility());
            this.lockCompileTask.lock();
            try {
                HashSet set = Sets.newHashSet((Iterable)lvt_10_1_);
                HashSet set1 = Sets.newHashSet(this.setTileEntities);
                set.removeAll(this.setTileEntities);
                set1.removeAll(lvt_10_1_);
                this.setTileEntities.clear();
                this.setTileEntities.addAll(lvt_10_1_);
                this.renderGlobal.updateTileEntities(set1, set);
            }
            finally {
                this.lockCompileTask.unlock();
            }
        }
    }

    protected void finishCompileTask() {
        this.lockCompileTask.lock();
        try {
            if (this.compileTask != null && this.compileTask.getStatus() != ChunkCompileTaskGenerator.Status.DONE) {
                this.compileTask.finish();
                this.compileTask = null;
            }
        }
        finally {
            this.lockCompileTask.unlock();
        }
    }

    public ReentrantLock getLockCompileTask() {
        return this.lockCompileTask;
    }

    public ChunkCompileTaskGenerator makeCompileTaskChunk() {
        ChunkCompileTaskGenerator chunkcompiletaskgenerator;
        this.lockCompileTask.lock();
        try {
            this.finishCompileTask();
            this.compileTask = new ChunkCompileTaskGenerator(this, ChunkCompileTaskGenerator.Type.REBUILD_CHUNK, this.getDistanceSq());
            this.resetChunkCache();
            chunkcompiletaskgenerator = this.compileTask;
        }
        finally {
            this.lockCompileTask.unlock();
        }
        return chunkcompiletaskgenerator;
    }

    private void resetChunkCache() {
        boolean i = true;
    }

    @Nullable
    public ChunkCompileTaskGenerator makeCompileTaskTransparency() {
        ChunkCompileTaskGenerator chunkcompiletaskgenerator1;
        this.lockCompileTask.lock();
        try {
            ChunkCompileTaskGenerator chunkcompiletaskgenerator;
            if (this.compileTask != null && this.compileTask.getStatus() == ChunkCompileTaskGenerator.Status.PENDING) {
                ChunkCompileTaskGenerator chunkcompiletaskgenerator2;
                ChunkCompileTaskGenerator chunkCompileTaskGenerator = chunkcompiletaskgenerator2 = null;
                return chunkCompileTaskGenerator;
            }
            if (this.compileTask != null && this.compileTask.getStatus() != ChunkCompileTaskGenerator.Status.DONE) {
                this.compileTask.finish();
                this.compileTask = null;
            }
            this.compileTask = new ChunkCompileTaskGenerator(this, ChunkCompileTaskGenerator.Type.RESORT_TRANSPARENCY, this.getDistanceSq());
            this.compileTask.setCompiledChunk(this.compiledChunk);
            chunkcompiletaskgenerator1 = chunkcompiletaskgenerator = this.compileTask;
        }
        finally {
            this.lockCompileTask.unlock();
        }
        return chunkcompiletaskgenerator1;
    }

    protected double getDistanceSq() {
        EntityPlayerSP entityplayersp = Minecraft.getMinecraft().player;
        double d0 = this.boundingBox.minX + 8.0 - entityplayersp.posX;
        double d1 = this.boundingBox.minY + 8.0 - entityplayersp.posY;
        double d2 = this.boundingBox.minZ + 8.0 - entityplayersp.posZ;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    private void preRenderBlocks(BufferBuilder worldRendererIn, BlockPos pos) {
        worldRendererIn.begin(7, DefaultVertexFormats.BLOCK);
        worldRendererIn.setTranslation(-pos.getX(), -pos.getY(), -pos.getZ());
    }

    private void postRenderBlocks(BlockRenderLayer layer, float x, float y, float z, BufferBuilder worldRendererIn, CompiledChunk compiledChunkIn) {
        if (layer == BlockRenderLayer.TRANSLUCENT && !compiledChunkIn.isLayerEmpty(layer)) {
            worldRendererIn.sortVertexData(x, y, z);
            compiledChunkIn.setState(worldRendererIn.getVertexState());
        }
        worldRendererIn.finishDrawing();
    }

    private void initModelviewMatrix() {
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        float f = 1.000001f;
        GlStateManager.translate(-8.0f, -8.0f, -8.0f);
        GlStateManager.scale(1.000001f, 1.000001f, 1.000001f);
        GlStateManager.translate(8.0f, 8.0f, 8.0f);
        GlStateManager.getFloat(2982, this.modelviewMatrix);
        GlStateManager.popMatrix();
    }

    public void multModelviewMatrix() {
        GlStateManager.multMatrix(this.modelviewMatrix);
    }

    public CompiledChunk getCompiledChunk() {
        return this.compiledChunk;
    }

    public void setCompiledChunk(CompiledChunk compiledChunkIn) {
        this.lockCompiledChunk.lock();
        try {
            this.compiledChunk = compiledChunkIn;
        }
        finally {
            this.lockCompiledChunk.unlock();
        }
    }

    public void stopCompileTask() {
        this.finishCompileTask();
        this.compiledChunk = CompiledChunk.DUMMY;
    }

    public void deleteGlResources() {
        this.stopCompileTask();
        this.world = null;
        int i = 0;
        while (i < BlockRenderLayer.values().length) {
            if (this.vertexBuffers[i] != null) {
                this.vertexBuffers[i].deleteGlBuffers();
            }
            ++i;
        }
    }

    public BlockPos getPosition() {
        return this.position;
    }

    public void setNeedsUpdate(boolean needsUpdateIn) {
        if (this.needsUpdate) {
            needsUpdateIn |= this.needsUpdateCustom;
        }
        this.needsUpdate = true;
        this.needsUpdateCustom = needsUpdateIn;
        if (this.isWorldPlayerUpdate()) {
            this.playerUpdate = true;
        }
    }

    public void clearNeedsUpdate() {
        this.needsUpdate = false;
        this.needsUpdateCustom = false;
        this.playerUpdate = false;
    }

    public boolean isNeedsUpdate() {
        return this.needsUpdate;
    }

    public boolean isNeedsUpdateCustom() {
        return this.needsUpdate && this.needsUpdateCustom;
    }

    public BlockPos getBlockPosOffset16(EnumFacing p_181701_1_) {
        return this.mapEnumFacing[p_181701_1_.ordinal()];
    }

    public World getWorld() {
        return this.world;
    }

    private boolean isWorldPlayerUpdate() {
        if (this.world instanceof WorldClient) {
            WorldClient worldclient = (WorldClient)this.world;
            return worldclient.isPlayerUpdate();
        }
        return false;
    }

    public boolean isPlayerUpdate() {
        return this.playerUpdate;
    }

    private BlockRenderLayer fixBlockLayer(Block p_fixBlockLayer_1_, BlockRenderLayer p_fixBlockLayer_2_) {
        if (this.isMipmaps) {
            if (p_fixBlockLayer_2_ == BlockRenderLayer.CUTOUT) {
                if (p_fixBlockLayer_1_ instanceof BlockRedstoneWire) {
                    return p_fixBlockLayer_2_;
                }
                if (p_fixBlockLayer_1_ instanceof BlockCactus) {
                    return p_fixBlockLayer_2_;
                }
                return BlockRenderLayer.CUTOUT_MIPPED;
            }
        } else if (p_fixBlockLayer_2_ == BlockRenderLayer.CUTOUT_MIPPED) {
            return BlockRenderLayer.CUTOUT;
        }
        return p_fixBlockLayer_2_;
    }

    private void postRenderOverlays(RegionRenderCacheBuilder p_postRenderOverlays_1_, CompiledChunk p_postRenderOverlays_2_, boolean[] p_postRenderOverlays_3_) {
        this.postRenderOverlay(BlockRenderLayer.CUTOUT, p_postRenderOverlays_1_, p_postRenderOverlays_2_, p_postRenderOverlays_3_);
        this.postRenderOverlay(BlockRenderLayer.CUTOUT_MIPPED, p_postRenderOverlays_1_, p_postRenderOverlays_2_, p_postRenderOverlays_3_);
        this.postRenderOverlay(BlockRenderLayer.TRANSLUCENT, p_postRenderOverlays_1_, p_postRenderOverlays_2_, p_postRenderOverlays_3_);
    }

    private void postRenderOverlay(BlockRenderLayer p_postRenderOverlay_1_, RegionRenderCacheBuilder p_postRenderOverlay_2_, CompiledChunk p_postRenderOverlay_3_, boolean[] p_postRenderOverlay_4_) {
        BufferBuilder bufferbuilder = p_postRenderOverlay_2_.getWorldRendererByLayer(p_postRenderOverlay_1_);
        if (bufferbuilder.isDrawing()) {
            p_postRenderOverlay_3_.setLayerStarted(p_postRenderOverlay_1_);
            p_postRenderOverlay_4_[p_postRenderOverlay_1_.ordinal()] = true;
        }
    }

    private ChunkCacheOF makeChunkCacheOF() {
        BlockPos blockpos = this.position.add(-1, -1, -1);
        ChunkCache chunkcache = this.createRegionRenderCache(this.world, blockpos, this.position.add(16, 16, 16), 1);
        if (Reflector.MinecraftForgeClient_onRebuildChunk.exists()) {
            Reflector.call(Reflector.MinecraftForgeClient_onRebuildChunk, this.world, this.position, chunkcache);
        }
        ChunkCacheOF chunkcacheof = new ChunkCacheOF(chunkcache, blockpos, 1);
        return chunkcacheof;
    }

    public RenderChunk getRenderChunkOffset16(ViewFrustum p_getRenderChunkOffset16_1_, EnumFacing p_getRenderChunkOffset16_2_) {
        RenderChunk renderchunk = this.renderChunksOfset16[p_getRenderChunkOffset16_2_.ordinal()];
        if (renderchunk == null) {
            BlockPos blockpos = this.getBlockPosOffset16(p_getRenderChunkOffset16_2_);
            this.renderChunksOfset16[p_getRenderChunkOffset16_2_.ordinal()] = renderchunk = p_getRenderChunkOffset16_1_.getRenderChunk(blockpos);
        }
        return renderchunk;
    }

    public Chunk getChunk(World p_getChunk_1_) {
        if (this.chunk != null && this.chunk.isLoaded()) {
            return this.chunk;
        }
        this.chunk = p_getChunk_1_.getChunkFromBlockCoords(this.getPosition());
        return this.chunk;
    }

    protected ChunkCache createRegionRenderCache(World p_createRegionRenderCache_1_, BlockPos p_createRegionRenderCache_2_, BlockPos p_createRegionRenderCache_3_, int p_createRegionRenderCache_4_) {
        return new ChunkCache(p_createRegionRenderCache_1_, p_createRegionRenderCache_2_, p_createRegionRenderCache_3_, p_createRegionRenderCache_4_);
    }
}

