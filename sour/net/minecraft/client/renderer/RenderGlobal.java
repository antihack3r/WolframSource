/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonSyntaxException
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.lwjgl.input.Keyboard
 *  org.lwjgl.util.vector.Matrix4f
 *  org.lwjgl.util.vector.Vector3f
 *  org.lwjgl.util.vector.Vector4f
 */
package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockSign;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.ChunkRenderContainer;
import net.minecraft.client.renderer.DestroyBlockProgress;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderList;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VboRenderList;
import net.minecraft.client.renderer.Vector3d;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.IRenderChunkFactory;
import net.minecraft.client.renderer.chunk.ListChunkFactory;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.chunk.VboChunkFactory;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemRecord;
import net.minecraft.src.ChunkUtils;
import net.minecraft.src.CloudRenderer;
import net.minecraft.src.Config;
import net.minecraft.src.CustomColors;
import net.minecraft.src.CustomSky;
import net.minecraft.src.DynamicLights;
import net.minecraft.src.Lagometer;
import net.minecraft.src.RandomMobs;
import net.minecraft.src.Reflector;
import net.minecraft.src.RenderEnv;
import net.minecraft.src.RenderInfoLazy;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.wolframclient.Wolfram;
import net.wolframclient.event.Dispatcher;
import net.wolframclient.event.events.EntityRenderEvent;
import net.wolframclient.gui.GuiManager;
import net.wolframclient.utils.RenderUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import shadersmod.client.Shaders;
import shadersmod.client.ShadersRender;
import shadersmod.client.ShadowUtils;

public class RenderGlobal
implements IWorldEventListener,
IResourceManagerReloadListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceLocation MOON_PHASES_TEXTURES = new ResourceLocation("textures/environment/moon_phases.png");
    private static final ResourceLocation SUN_TEXTURES = new ResourceLocation("textures/environment/sun.png");
    private static final ResourceLocation CLOUDS_TEXTURES = new ResourceLocation("textures/environment/clouds.png");
    private static final ResourceLocation END_SKY_TEXTURES = new ResourceLocation("textures/environment/end_sky.png");
    private static final ResourceLocation FORCEFIELD_TEXTURES = new ResourceLocation("textures/misc/forcefield.png");
    public final Minecraft mc;
    private final TextureManager renderEngine;
    private final RenderManager renderManager;
    private WorldClient theWorld;
    private Set<RenderChunk> chunksToUpdate = Sets.newLinkedHashSet();
    private List<ContainerLocalRenderInformation> renderInfos = Lists.newArrayListWithCapacity((int)69696);
    private final Set<TileEntity> setTileEntities = Sets.newHashSet();
    private ViewFrustum viewFrustum;
    private int starGLCallList = -1;
    private int glSkyList = -1;
    private int glSkyList2 = -1;
    private final VertexFormat vertexBufferFormat;
    private VertexBuffer starVBO;
    private VertexBuffer skyVBO;
    private VertexBuffer sky2VBO;
    private int cloudTickCounter;
    public final Map<Integer, DestroyBlockProgress> damagedBlocks = Maps.newHashMap();
    private final Map<BlockPos, ISound> mapSoundPositions = Maps.newHashMap();
    private final TextureAtlasSprite[] destroyBlockIcons = new TextureAtlasSprite[10];
    private Framebuffer entityOutlineFramebuffer;
    private ShaderGroup entityOutlineShader;
    private double frustumUpdatePosX = Double.MIN_VALUE;
    private double frustumUpdatePosY = Double.MIN_VALUE;
    private double frustumUpdatePosZ = Double.MIN_VALUE;
    private int frustumUpdatePosChunkX = Integer.MIN_VALUE;
    private int frustumUpdatePosChunkY = Integer.MIN_VALUE;
    private int frustumUpdatePosChunkZ = Integer.MIN_VALUE;
    private double lastViewEntityX = Double.MIN_VALUE;
    private double lastViewEntityY = Double.MIN_VALUE;
    private double lastViewEntityZ = Double.MIN_VALUE;
    private double lastViewEntityPitch = Double.MIN_VALUE;
    private double lastViewEntityYaw = Double.MIN_VALUE;
    private ChunkRenderDispatcher renderDispatcher;
    private ChunkRenderContainer renderContainer;
    private int renderDistanceChunks = -1;
    private int renderEntitiesStartupCounter = 2;
    private int countEntitiesTotal;
    private int countEntitiesRendered;
    private int countEntitiesHidden;
    private boolean debugFixTerrainFrustum;
    private ClippingHelper debugFixedClippingHelper;
    private final Vector4f[] debugTerrainMatrix = new Vector4f[8];
    private final Vector3d debugTerrainFrustumPosition = new Vector3d();
    private boolean vboEnabled;
    IRenderChunkFactory renderChunkFactory;
    private double prevRenderSortX;
    private double prevRenderSortY;
    private double prevRenderSortZ;
    public boolean displayListEntitiesDirty = true;
    private boolean entityOutlinesRendered;
    private final Set<BlockPos> setLightUpdates = Sets.newHashSet();
    private CloudRenderer cloudRenderer;
    public Entity renderedEntity;
    public Set chunksToResortTransparency = new LinkedHashSet();
    public Set chunksToUpdateForced = new LinkedHashSet();
    private Deque visibilityDeque = new ArrayDeque();
    private List<ContainerLocalRenderInformation> renderInfosEntities = new ArrayList<ContainerLocalRenderInformation>(1024);
    private List<ContainerLocalRenderInformation> renderInfosTileEntities = new ArrayList<ContainerLocalRenderInformation>(1024);
    private List renderInfosNormal = new ArrayList(1024);
    private List renderInfosEntitiesNormal = new ArrayList(1024);
    private List renderInfosTileEntitiesNormal = new ArrayList(1024);
    private List renderInfosShadow = new ArrayList(1024);
    private List renderInfosEntitiesShadow = new ArrayList(1024);
    private List renderInfosTileEntitiesShadow = new ArrayList(1024);
    private int renderDistance = 0;
    private int renderDistanceSq = 0;
    private static final Set SET_ALL_FACINGS = Collections.unmodifiableSet(new HashSet<EnumFacing>(Arrays.asList(EnumFacing.VALUES)));
    private int countTileEntitiesRendered;
    private IChunkProvider worldChunkProvider = null;
    private Long2ObjectMap<Chunk> worldChunkProviderMap = null;
    private int countLoadedChunksPrev = 0;
    private RenderEnv renderEnv = new RenderEnv(this.theWorld, Blocks.AIR.getDefaultState(), new BlockPos(0, 0, 0));
    public boolean renderOverlayDamaged = false;
    public boolean renderOverlayEyes = false;
    static Deque<ContainerLocalRenderInformation> renderInfoCache = new ArrayDeque<ContainerLocalRenderInformation>();

    public RenderGlobal(Minecraft mcIn) {
        this.cloudRenderer = new CloudRenderer(mcIn);
        this.mc = mcIn;
        this.renderManager = mcIn.getRenderManager();
        this.renderEngine = mcIn.getTextureManager();
        this.renderEngine.bindTexture(FORCEFIELD_TEXTURES);
        GlStateManager.glTexParameteri(3553, 10242, 10497);
        GlStateManager.glTexParameteri(3553, 10243, 10497);
        GlStateManager.bindTexture(0);
        this.updateDestroyBlockIcons();
        this.vboEnabled = OpenGlHelper.useVbo();
        if (this.vboEnabled) {
            this.renderContainer = new VboRenderList();
            this.renderChunkFactory = new VboChunkFactory();
        } else {
            this.renderContainer = new RenderList();
            this.renderChunkFactory = new ListChunkFactory();
        }
        this.vertexBufferFormat = new VertexFormat();
        this.vertexBufferFormat.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUsage.POSITION, 3));
        this.generateStars();
        this.generateSky();
        this.generateSky2();
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        this.updateDestroyBlockIcons();
    }

    private void updateDestroyBlockIcons() {
        TextureMap texturemap = this.mc.getTextureMapBlocks();
        int i = 0;
        while (i < this.destroyBlockIcons.length) {
            this.destroyBlockIcons[i] = texturemap.getAtlasSprite("minecraft:blocks/destroy_stage_" + i);
            ++i;
        }
    }

    public void makeEntityOutlineShader() {
        if (OpenGlHelper.shadersSupported) {
            if (ShaderLinkHelper.getStaticShaderLinkHelper() == null) {
                ShaderLinkHelper.setNewStaticShaderLinkHelper();
            }
            ResourceLocation resourcelocation = new ResourceLocation("shaders/post/entity_outline.json");
            try {
                this.entityOutlineShader = new ShaderGroup(this.mc.getTextureManager(), this.mc.getResourceManager(), this.mc.getFramebuffer(), resourcelocation);
                this.entityOutlineShader.createBindFramebuffers(this.mc.displayWidth, this.mc.displayHeight);
                this.entityOutlineFramebuffer = this.entityOutlineShader.getFramebufferRaw("final");
            }
            catch (IOException ioexception) {
                LOGGER.warn("Failed to load shader: {}", (Object)resourcelocation, (Object)ioexception);
                this.entityOutlineShader = null;
                this.entityOutlineFramebuffer = null;
            }
            catch (JsonSyntaxException jsonsyntaxexception) {
                LOGGER.warn("Failed to load shader: {}", (Object)resourcelocation, (Object)jsonsyntaxexception);
                this.entityOutlineShader = null;
                this.entityOutlineFramebuffer = null;
            }
        } else {
            this.entityOutlineShader = null;
            this.entityOutlineFramebuffer = null;
        }
    }

    public void renderEntityOutlineFramebuffer() {
        if (this.isRenderEntityOutlines()) {
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
            this.entityOutlineFramebuffer.framebufferRenderExt(this.mc.displayWidth, this.mc.displayHeight, false);
            GlStateManager.disableBlend();
        }
    }

    protected boolean isRenderEntityOutlines() {
        if (!(Config.isFastRender() || Config.isShaders() || Config.isAntialiasing())) {
            return this.entityOutlineFramebuffer != null && this.entityOutlineShader != null && this.mc.player != null;
        }
        return false;
    }

    private void generateSky2() {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        if (this.sky2VBO != null) {
            this.sky2VBO.deleteGlBuffers();
        }
        if (this.glSkyList2 >= 0) {
            GLAllocation.deleteDisplayLists(this.glSkyList2);
            this.glSkyList2 = -1;
        }
        if (this.vboEnabled) {
            this.sky2VBO = new VertexBuffer(this.vertexBufferFormat);
            this.renderSky(bufferbuilder, -16.0f, true);
            bufferbuilder.finishDrawing();
            bufferbuilder.reset();
            this.sky2VBO.bufferData(bufferbuilder.getByteBuffer());
        } else {
            this.glSkyList2 = GLAllocation.generateDisplayLists(1);
            GlStateManager.glNewList(this.glSkyList2, 4864);
            this.renderSky(bufferbuilder, -16.0f, true);
            tessellator.draw();
            GlStateManager.glEndList();
        }
    }

    private void generateSky() {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        if (this.skyVBO != null) {
            this.skyVBO.deleteGlBuffers();
        }
        if (this.glSkyList >= 0) {
            GLAllocation.deleteDisplayLists(this.glSkyList);
            this.glSkyList = -1;
        }
        if (this.vboEnabled) {
            this.skyVBO = new VertexBuffer(this.vertexBufferFormat);
            this.renderSky(bufferbuilder, 16.0f, false);
            bufferbuilder.finishDrawing();
            bufferbuilder.reset();
            this.skyVBO.bufferData(bufferbuilder.getByteBuffer());
        } else {
            this.glSkyList = GLAllocation.generateDisplayLists(1);
            GlStateManager.glNewList(this.glSkyList, 4864);
            this.renderSky(bufferbuilder, 16.0f, false);
            tessellator.draw();
            GlStateManager.glEndList();
        }
    }

    private void renderSky(BufferBuilder worldRendererIn, float posY, boolean reverseX) {
        int i = 64;
        int j = 6;
        worldRendererIn.begin(7, DefaultVertexFormats.POSITION);
        int k = -384;
        while (k <= 384) {
            int l = -384;
            while (l <= 384) {
                float f = k;
                float f1 = k + 64;
                if (reverseX) {
                    f1 = k;
                    f = k + 64;
                }
                worldRendererIn.pos(f, posY, l).endVertex();
                worldRendererIn.pos(f1, posY, l).endVertex();
                worldRendererIn.pos(f1, posY, l + 64).endVertex();
                worldRendererIn.pos(f, posY, l + 64).endVertex();
                l += 64;
            }
            k += 64;
        }
    }

    private void generateStars() {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        if (this.starVBO != null) {
            this.starVBO.deleteGlBuffers();
        }
        if (this.starGLCallList >= 0) {
            GLAllocation.deleteDisplayLists(this.starGLCallList);
            this.starGLCallList = -1;
        }
        if (this.vboEnabled) {
            this.starVBO = new VertexBuffer(this.vertexBufferFormat);
            this.renderStars(bufferbuilder);
            bufferbuilder.finishDrawing();
            bufferbuilder.reset();
            this.starVBO.bufferData(bufferbuilder.getByteBuffer());
        } else {
            this.starGLCallList = GLAllocation.generateDisplayLists(1);
            GlStateManager.pushMatrix();
            GlStateManager.glNewList(this.starGLCallList, 4864);
            this.renderStars(bufferbuilder);
            tessellator.draw();
            GlStateManager.glEndList();
            GlStateManager.popMatrix();
        }
    }

    private void renderStars(BufferBuilder worldRendererIn) {
        Random random = new Random(10842L);
        worldRendererIn.begin(7, DefaultVertexFormats.POSITION);
        int i = 0;
        while (i < 1500) {
            double d0 = random.nextFloat() * 2.0f - 1.0f;
            double d1 = random.nextFloat() * 2.0f - 1.0f;
            double d2 = random.nextFloat() * 2.0f - 1.0f;
            double d3 = 0.15f + random.nextFloat() * 0.1f;
            double d4 = d0 * d0 + d1 * d1 + d2 * d2;
            if (d4 < 1.0 && d4 > 0.01) {
                d4 = 1.0 / Math.sqrt(d4);
                double d5 = (d0 *= d4) * 100.0;
                double d6 = (d1 *= d4) * 100.0;
                double d7 = (d2 *= d4) * 100.0;
                double d8 = Math.atan2(d0, d2);
                double d9 = Math.sin(d8);
                double d10 = Math.cos(d8);
                double d11 = Math.atan2(Math.sqrt(d0 * d0 + d2 * d2), d1);
                double d12 = Math.sin(d11);
                double d13 = Math.cos(d11);
                double d14 = random.nextDouble() * Math.PI * 2.0;
                double d15 = Math.sin(d14);
                double d16 = Math.cos(d14);
                int j = 0;
                while (j < 4) {
                    double d17 = 0.0;
                    double d18 = (double)((j & 2) - 1) * d3;
                    double d19 = (double)((j + 1 & 2) - 1) * d3;
                    double d20 = 0.0;
                    double d21 = d18 * d16 - d19 * d15;
                    double d22 = d19 * d16 + d18 * d15;
                    double d23 = d21 * d12 + 0.0 * d13;
                    double d24 = 0.0 * d12 - d21 * d13;
                    double d25 = d24 * d9 - d22 * d10;
                    double d26 = d22 * d9 + d24 * d10;
                    worldRendererIn.pos(d5 + d25, d6 + d23, d7 + d26).endVertex();
                    ++j;
                }
            }
            ++i;
        }
    }

    public void setWorldAndLoadRenderers(@Nullable WorldClient worldClientIn) {
        if (this.theWorld != null) {
            this.theWorld.removeEventListener(this);
        }
        this.frustumUpdatePosX = Double.MIN_VALUE;
        this.frustumUpdatePosY = Double.MIN_VALUE;
        this.frustumUpdatePosZ = Double.MIN_VALUE;
        this.frustumUpdatePosChunkX = Integer.MIN_VALUE;
        this.frustumUpdatePosChunkY = Integer.MIN_VALUE;
        this.frustumUpdatePosChunkZ = Integer.MIN_VALUE;
        this.renderManager.set(worldClientIn);
        this.theWorld = worldClientIn;
        if (Config.isDynamicLights()) {
            DynamicLights.clear();
        }
        if (worldClientIn != null) {
            worldClientIn.addEventListener(this);
            this.loadRenderers();
        } else {
            this.chunksToUpdate.clear();
            this.renderInfos.clear();
            if (this.viewFrustum != null) {
                this.viewFrustum.deleteGlResources();
                this.viewFrustum = null;
            }
            if (this.renderDispatcher != null) {
                this.renderDispatcher.stopWorkerThreads();
            }
            this.renderDispatcher = null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void loadRenderers() {
        if (this.theWorld != null) {
            Entity entity;
            if (this.renderDispatcher == null) {
                this.renderDispatcher = new ChunkRenderDispatcher();
            }
            this.displayListEntitiesDirty = true;
            Blocks.LEAVES.setGraphicsLevel(Config.isTreesFancy());
            Blocks.LEAVES2.setGraphicsLevel(Config.isTreesFancy());
            BlockModelRenderer.updateAoLightValue();
            renderInfoCache.clear();
            if (Config.isDynamicLights()) {
                DynamicLights.clear();
            }
            this.renderDistanceChunks = this.mc.gameSettings.renderDistanceChunks;
            this.renderDistance = this.renderDistanceChunks * 16;
            this.renderDistanceSq = this.renderDistance * this.renderDistance;
            boolean flag = this.vboEnabled;
            this.vboEnabled = OpenGlHelper.useVbo();
            if (flag && !this.vboEnabled) {
                this.renderContainer = new RenderList();
                this.renderChunkFactory = new ListChunkFactory();
            } else if (!flag && this.vboEnabled) {
                this.renderContainer = new VboRenderList();
                this.renderChunkFactory = new VboChunkFactory();
            }
            if (flag != this.vboEnabled) {
                this.generateStars();
                this.generateSky();
                this.generateSky2();
            }
            if (this.viewFrustum != null) {
                this.viewFrustum.deleteGlResources();
            }
            this.stopChunkUpdates();
            Set<TileEntity> set = this.setTileEntities;
            synchronized (set) {
                this.setTileEntities.clear();
            }
            this.viewFrustum = new ViewFrustum(this.theWorld, this.mc.gameSettings.renderDistanceChunks, this, this.renderChunkFactory);
            if (this.theWorld != null && (entity = this.mc.getRenderViewEntity()) != null) {
                this.viewFrustum.updateChunkPositions(entity.posX, entity.posZ);
            }
            this.renderEntitiesStartupCounter = 2;
        }
    }

    protected void stopChunkUpdates() {
        this.chunksToUpdate.clear();
        this.renderDispatcher.stopChunkUpdates();
    }

    public void createBindEntityOutlineFbs(int width, int height) {
        if (OpenGlHelper.shadersSupported && this.entityOutlineShader != null) {
            this.entityOutlineShader.createBindFramebuffers(width, height);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void renderEntities(Entity renderViewEntity, ICamera camera, float partialTicks) {
        Dispatcher.call(new EntityRenderEvent());
        int i = 0;
        if (Reflector.MinecraftForgeClient_getRenderPass.exists()) {
            i = Reflector.callInt(Reflector.MinecraftForgeClient_getRenderPass, new Object[0]);
        }
        if (this.renderEntitiesStartupCounter > 0) {
            if (i > 0) {
                return;
            }
            --this.renderEntitiesStartupCounter;
        } else {
            double d0 = renderViewEntity.prevPosX + (renderViewEntity.posX - renderViewEntity.prevPosX) * (double)partialTicks;
            double d1 = renderViewEntity.prevPosY + (renderViewEntity.posY - renderViewEntity.prevPosY) * (double)partialTicks;
            double d2 = renderViewEntity.prevPosZ + (renderViewEntity.posZ - renderViewEntity.prevPosZ) * (double)partialTicks;
            this.theWorld.theProfiler.startSection("prepare");
            TileEntityRendererDispatcher.instance.prepare(this.theWorld, this.mc.getTextureManager(), this.mc.fontRendererObj, this.mc.getRenderViewEntity(), this.mc.objectMouseOver, partialTicks);
            this.renderManager.cacheActiveRenderInfo(this.theWorld, this.mc.fontRendererObj, this.mc.getRenderViewEntity(), this.mc.pointedEntity, this.mc.gameSettings, partialTicks);
            if (i == 0) {
                this.countEntitiesTotal = 0;
                this.countEntitiesRendered = 0;
                this.countEntitiesHidden = 0;
                this.countTileEntitiesRendered = 0;
            }
            Entity entity = this.mc.getRenderViewEntity();
            double d3 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
            double d4 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
            double d5 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;
            TileEntityRendererDispatcher.staticPlayerX = d3;
            TileEntityRendererDispatcher.staticPlayerY = d4;
            TileEntityRendererDispatcher.staticPlayerZ = d5;
            this.renderManager.setRenderPosition(d3, d4, d5);
            this.mc.entityRenderer.enableLightmap();
            this.theWorld.theProfiler.endStartSection("global");
            List<Entity> list = this.theWorld.getLoadedEntityList();
            if (i == 0) {
                this.countEntitiesTotal = list.size();
            }
            if (Config.isFogOff() && this.mc.entityRenderer.fogStandard) {
                GlStateManager.disableFog();
            }
            boolean flag = Reflector.ForgeEntity_shouldRenderInPass.exists();
            boolean flag1 = Reflector.ForgeTileEntity_shouldRenderInPass.exists();
            int j = 0;
            while (j < this.theWorld.weatherEffects.size()) {
                Entity entity1 = (Entity)this.theWorld.weatherEffects.get(j);
                if (!flag || Reflector.callBoolean(entity1, Reflector.ForgeEntity_shouldRenderInPass, i)) {
                    ++this.countEntitiesRendered;
                    if (entity1.isInRangeToRender3d(d0, d1, d2)) {
                        this.renderManager.renderEntityStatic(entity1, partialTicks, false);
                    }
                }
                ++j;
            }
            this.theWorld.theProfiler.endStartSection("entities");
            boolean flag4 = Config.isShaders();
            if (flag4) {
                Shaders.beginEntities();
            }
            boolean flag5 = this.mc.gameSettings.fancyGraphics;
            this.mc.gameSettings.fancyGraphics = Config.isDroppedItemsFancy();
            ArrayList list1 = Lists.newArrayList();
            ArrayList list2 = Lists.newArrayList();
            BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();
            for (ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation : this.renderInfosEntities) {
                Chunk chunk = renderglobal$containerlocalrenderinformation.renderChunk.getChunk(this.theWorld);
                ClassInheritanceMultiMap<Entity> classinheritancemultimap = chunk.getEntityLists()[renderglobal$containerlocalrenderinformation.renderChunk.getPosition().getY() / 16];
                if (classinheritancemultimap.isEmpty()) continue;
                for (Entity entity2 : classinheritancemultimap) {
                    boolean flag3;
                    boolean flag2;
                    if (flag && !Reflector.callBoolean(entity2, Reflector.ForgeEntity_shouldRenderInPass, i)) continue;
                    boolean bl = flag2 = this.renderManager.shouldRender(entity2, camera, d0, d1, d2) || entity2.isRidingOrBeingRiddenBy(this.mc.player);
                    if (!flag2) continue;
                    boolean bl2 = flag3 = this.mc.getRenderViewEntity() instanceof EntityLivingBase ? ((EntityLivingBase)this.mc.getRenderViewEntity()).isPlayerSleeping() : false;
                    if (entity2 == this.mc.getRenderViewEntity() && this.mc.gameSettings.thirdPersonView == 0 && !flag3 || !(entity2.posY < 0.0) && !(entity2.posY >= 256.0) && !this.theWorld.isBlockLoaded(blockpos$pooledmutableblockpos.setPos(entity2))) continue;
                    ++this.countEntitiesRendered;
                    this.renderedEntity = entity2;
                    if (flag4) {
                        Shaders.nextEntity(entity2);
                    }
                    this.renderManager.renderEntityStatic(entity2, partialTicks, false);
                    this.renderedEntity = null;
                    if (this.isOutlineActive(entity2, entity, camera)) {
                        list1.add(entity2);
                    }
                    if (!this.renderManager.isRenderMultipass(entity2)) continue;
                    list2.add(entity2);
                }
            }
            blockpos$pooledmutableblockpos.release();
            if (!list2.isEmpty()) {
                for (Entity entity3 : list2) {
                    if (flag && !Reflector.callBoolean(entity3, Reflector.ForgeEntity_shouldRenderInPass, i)) continue;
                    if (flag4) {
                        Shaders.nextEntity(entity3);
                    }
                    this.renderManager.renderMultipass(entity3, partialTicks);
                }
            }
            if (i == 0 && this.isRenderEntityOutlines() && (!list1.isEmpty() || this.entityOutlinesRendered)) {
                this.theWorld.theProfiler.endStartSection("entityOutlines");
                this.entityOutlineFramebuffer.framebufferClear();
                boolean bl = this.entityOutlinesRendered = !list1.isEmpty();
                if (!list1.isEmpty()) {
                    GlStateManager.depthFunc(519);
                    GlStateManager.disableFog();
                    this.entityOutlineFramebuffer.bindFramebuffer(false);
                    RenderHelper.disableStandardItemLighting();
                    this.renderManager.setRenderOutlines(true);
                    int k = 0;
                    while (k < list1.size()) {
                        Entity entity2 = (Entity)list1.get(k);
                        if (!flag || Reflector.callBoolean(entity2, Reflector.ForgeEntity_shouldRenderInPass, i)) {
                            if (flag4) {
                                Shaders.nextEntity(entity2);
                            }
                            this.renderManager.renderEntityStatic(entity2, partialTicks, false);
                        }
                        ++k;
                    }
                    this.renderManager.setRenderOutlines(false);
                    RenderHelper.enableStandardItemLighting();
                    GlStateManager.depthMask(false);
                    this.entityOutlineShader.loadShaderGroup(partialTicks);
                    GlStateManager.enableLighting();
                    GlStateManager.depthMask(true);
                    GlStateManager.enableFog();
                    GlStateManager.enableBlend();
                    GlStateManager.enableColorMaterial();
                    GlStateManager.depthFunc(515);
                    GlStateManager.enableDepth();
                    GlStateManager.enableAlpha();
                }
                this.mc.getFramebuffer().bindFramebuffer(false);
            }
            if (!(this.isRenderEntityOutlines() || list1.isEmpty() && !this.entityOutlinesRendered)) {
                this.theWorld.theProfiler.endStartSection("entityOutlines");
                boolean bl = this.entityOutlinesRendered = !list1.isEmpty();
                if (!list1.isEmpty()) {
                    GlStateManager.disableFog();
                    GlStateManager.disableDepth();
                    this.mc.entityRenderer.disableLightmap();
                    RenderHelper.disableStandardItemLighting();
                    this.renderManager.setRenderOutlines(true);
                    int l = 0;
                    while (l < list1.size()) {
                        Entity entity3 = (Entity)list1.get(l);
                        if (!flag || Reflector.callBoolean(entity3, Reflector.ForgeEntity_shouldRenderInPass, i)) {
                            if (flag4) {
                                Shaders.nextEntity(entity3);
                            }
                            this.renderManager.renderEntityStatic(entity3, partialTicks, false);
                        }
                        ++l;
                    }
                    this.renderManager.setRenderOutlines(false);
                    RenderHelper.enableStandardItemLighting();
                    this.mc.entityRenderer.enableLightmap();
                    GlStateManager.enableDepth();
                    GlStateManager.enableFog();
                }
            }
            this.mc.gameSettings.fancyGraphics = flag5;
            FontRenderer fontrenderer = TileEntityRendererDispatcher.instance.getFontRenderer();
            if (flag4) {
                Shaders.endEntities();
                Shaders.beginBlockEntities();
            }
            this.theWorld.theProfiler.endStartSection("blockentities");
            RenderHelper.enableStandardItemLighting();
            if (Reflector.ForgeTileEntity_hasFastRenderer.exists()) {
                TileEntityRendererDispatcher.instance.preDrawBatch();
            }
            for (ContainerLocalRenderInformation containerLocalRenderInformation : this.renderInfosTileEntities) {
                List<TileEntity> list3 = containerLocalRenderInformation.renderChunk.getCompiledChunk().getTileEntities();
                if (list3.isEmpty()) continue;
                for (TileEntity tileentity1 : list3) {
                    AxisAlignedBB axisalignedbb;
                    if (flag1 && (!Reflector.callBoolean(tileentity1, Reflector.ForgeTileEntity_shouldRenderInPass, i) || (axisalignedbb = (AxisAlignedBB)Reflector.call(tileentity1, Reflector.ForgeTileEntity_getRenderBoundingBox, new Object[0])) != null && !camera.isBoundingBoxInFrustum(axisalignedbb))) continue;
                    Class<?> oclass = tileentity1.getClass();
                    if (oclass == TileEntitySign.class && !Config.zoomMode) {
                        EntityPlayerSP entityplayer = this.mc.player;
                        double d6 = tileentity1.getDistanceSq(entityplayer.posX, entityplayer.posY, entityplayer.posZ);
                        if (d6 > 256.0) {
                            fontrenderer.enabled = false;
                        }
                    }
                    if (flag4) {
                        Shaders.nextBlockEntity(tileentity1);
                    }
                    TileEntityRendererDispatcher.instance.renderTileEntity(tileentity1, partialTicks, -1);
                    ++this.countTileEntitiesRendered;
                    fontrenderer.enabled = true;
                }
            }
            Set<TileEntity> set = this.setTileEntities;
            synchronized (set) {
                for (TileEntity tileentity : this.setTileEntities) {
                    if (flag1 && !Reflector.callBoolean(tileentity, Reflector.ForgeTileEntity_shouldRenderInPass, i)) continue;
                    if (flag4) {
                        Shaders.nextBlockEntity(tileentity);
                    }
                    TileEntityRendererDispatcher.instance.renderTileEntity(tileentity, partialTicks, -1);
                }
            }
            if (Reflector.ForgeTileEntity_hasFastRenderer.exists()) {
                TileEntityRendererDispatcher.instance.drawBatch(i);
            }
            this.renderOverlayDamaged = true;
            this.preRenderDamagedBlocks();
            for (DestroyBlockProgress destroyBlockProgress : this.damagedBlocks.values()) {
                BlockPos blockpos = destroyBlockProgress.getPosition();
                if (!this.theWorld.getBlockState(blockpos).getBlock().hasTileEntity()) continue;
                TileEntity tileentity2 = this.theWorld.getTileEntity(blockpos);
                if (tileentity2 instanceof TileEntityChest) {
                    TileEntityChest tileentitychest = (TileEntityChest)tileentity2;
                    if (tileentitychest.adjacentChestXNeg != null) {
                        blockpos = blockpos.offset(EnumFacing.WEST);
                        tileentity2 = this.theWorld.getTileEntity(blockpos);
                    } else if (tileentitychest.adjacentChestZNeg != null) {
                        blockpos = blockpos.offset(EnumFacing.NORTH);
                        tileentity2 = this.theWorld.getTileEntity(blockpos);
                    }
                }
                IBlockState iblockstate = this.theWorld.getBlockState(blockpos);
                if (tileentity2 == null || !iblockstate.func_191057_i()) continue;
                if (flag4) {
                    Shaders.nextBlockEntity(tileentity2);
                }
                TileEntityRendererDispatcher.instance.renderTileEntity(tileentity2, partialTicks, destroyBlockProgress.getPartialBlockDamage());
            }
            this.postRenderDamagedBlocks();
            this.renderOverlayDamaged = false;
            this.mc.entityRenderer.disableLightmap();
            this.mc.mcProfiler.endSection();
        }
    }

    private boolean isOutlineActive(Entity p_184383_1_, Entity p_184383_2_, ICamera p_184383_3_) {
        boolean flag;
        boolean bl = flag = p_184383_2_ instanceof EntityLivingBase && ((EntityLivingBase)p_184383_2_).isPlayerSleeping();
        if (p_184383_1_ == p_184383_2_ && this.mc.gameSettings.thirdPersonView == 0 && !flag) {
            return false;
        }
        if (p_184383_1_.isGlowing()) {
            return true;
        }
        if (this.mc.player.isSpectator() && this.mc.gameSettings.keyBindSpectatorOutlines.isKeyDown() && p_184383_1_ instanceof EntityPlayer) {
            return p_184383_1_.ignoreFrustumCheck || p_184383_3_.isBoundingBoxInFrustum(p_184383_1_.getEntityBoundingBox()) || p_184383_1_.isRidingOrBeingRiddenBy(this.mc.player);
        }
        return false;
    }

    public String getDebugInfoRenders() {
        int i = this.viewFrustum.renderChunks.length;
        int j = this.getRenderedChunks();
        return String.format("C: %d/%d %sD: %d, L: %d, %s", j, i, this.mc.renderChunksMany ? "(s) " : "", this.renderDistanceChunks, this.setLightUpdates.size(), this.renderDispatcher == null ? "null" : this.renderDispatcher.getDebugInfo());
    }

    protected int getRenderedChunks() {
        int i = 0;
        for (ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation : this.renderInfos) {
            CompiledChunk compiledchunk = renderglobal$containerlocalrenderinformation.renderChunk.compiledChunk;
            if (compiledchunk == CompiledChunk.DUMMY || compiledchunk.isEmpty()) continue;
            ++i;
        }
        return i;
    }

    public String getDebugInfoEntities() {
        return "E: " + this.countEntitiesRendered + "/" + this.countEntitiesTotal + ", B: " + this.countEntitiesHidden + ", " + Config.getVersionDebug();
    }

    public void setupTerrain(Entity viewEntity, double partialTicks, ICamera camera, int frameCount, boolean playerSpectator) {
        if (this.mc.gameSettings.renderDistanceChunks != this.renderDistanceChunks) {
            this.loadRenderers();
        }
        this.theWorld.theProfiler.startSection("camera");
        double d0 = viewEntity.posX - this.frustumUpdatePosX;
        double d1 = viewEntity.posY - this.frustumUpdatePosY;
        double d2 = viewEntity.posZ - this.frustumUpdatePosZ;
        if (this.frustumUpdatePosChunkX != viewEntity.chunkCoordX || this.frustumUpdatePosChunkY != viewEntity.chunkCoordY || this.frustumUpdatePosChunkZ != viewEntity.chunkCoordZ || d0 * d0 + d1 * d1 + d2 * d2 > 16.0) {
            this.frustumUpdatePosX = viewEntity.posX;
            this.frustumUpdatePosY = viewEntity.posY;
            this.frustumUpdatePosZ = viewEntity.posZ;
            this.frustumUpdatePosChunkX = viewEntity.chunkCoordX;
            this.frustumUpdatePosChunkY = viewEntity.chunkCoordY;
            this.frustumUpdatePosChunkZ = viewEntity.chunkCoordZ;
            this.viewFrustum.updateChunkPositions(viewEntity.posX, viewEntity.posZ);
        }
        if (Config.isDynamicLights()) {
            DynamicLights.update(this);
        }
        this.theWorld.theProfiler.endStartSection("renderlistcamera");
        double d3 = viewEntity.lastTickPosX + (viewEntity.posX - viewEntity.lastTickPosX) * partialTicks;
        double d4 = viewEntity.lastTickPosY + (viewEntity.posY - viewEntity.lastTickPosY) * partialTicks;
        double d5 = viewEntity.lastTickPosZ + (viewEntity.posZ - viewEntity.lastTickPosZ) * partialTicks;
        this.renderContainer.initialize(d3, d4, d5);
        this.theWorld.theProfiler.endStartSection("cull");
        if (this.debugFixedClippingHelper != null) {
            Frustum frustum = new Frustum(this.debugFixedClippingHelper);
            frustum.setPosition(this.debugTerrainFrustumPosition.x, this.debugTerrainFrustumPosition.y, this.debugTerrainFrustumPosition.z);
            camera = frustum;
        }
        this.mc.mcProfiler.endStartSection("culling");
        BlockPos blockpos1 = new BlockPos(d3, d4 + (double)viewEntity.getEyeHeight(), d5);
        RenderChunk renderchunk = this.viewFrustum.getRenderChunk(blockpos1);
        new BlockPos(MathHelper.floor(d3 / 16.0) * 16, MathHelper.floor(d4 / 16.0) * 16, MathHelper.floor(d5 / 16.0) * 16);
        this.displayListEntitiesDirty = this.displayListEntitiesDirty || !this.chunksToUpdate.isEmpty() || viewEntity.posX != this.lastViewEntityX || viewEntity.posY != this.lastViewEntityY || viewEntity.posZ != this.lastViewEntityZ || (double)viewEntity.rotationPitch != this.lastViewEntityPitch || (double)viewEntity.rotationYaw != this.lastViewEntityYaw;
        this.lastViewEntityX = viewEntity.posX;
        this.lastViewEntityY = viewEntity.posY;
        this.lastViewEntityZ = viewEntity.posZ;
        this.lastViewEntityPitch = viewEntity.rotationPitch;
        this.lastViewEntityYaw = viewEntity.rotationYaw;
        boolean flag = this.debugFixedClippingHelper != null;
        this.mc.mcProfiler.endStartSection("update");
        Lagometer.timerVisibility.start();
        int i = this.getCountLoadedChunks();
        if (i != this.countLoadedChunksPrev) {
            this.countLoadedChunksPrev = i;
            this.displayListEntitiesDirty = true;
        }
        if (Shaders.isShadowPass) {
            this.renderInfos = this.renderInfosShadow;
            this.renderInfosEntities = this.renderInfosEntitiesShadow;
            this.renderInfosTileEntities = this.renderInfosTileEntitiesShadow;
            if (!flag && this.displayListEntitiesDirty) {
                this.renderInfos.clear();
                this.renderInfosEntities.clear();
                this.renderInfosTileEntities.clear();
                RenderInfoLazy renderinfolazy = new RenderInfoLazy();
                Iterator<Object> iterator = ShadowUtils.makeShadowChunkIterator(this.theWorld, partialTicks, viewEntity, this.renderDistanceChunks, this.viewFrustum);
                while (iterator.hasNext()) {
                    BlockPos blockpos;
                    RenderChunk renderchunk1 = (RenderChunk)iterator.next();
                    if (renderchunk1 == null) continue;
                    renderinfolazy.setRenderChunk(renderchunk1);
                    if (!renderchunk1.compiledChunk.isEmpty() || renderchunk1.isNeedsUpdate()) {
                        this.renderInfos.add(renderinfolazy.getRenderInfo());
                    }
                    if (ChunkUtils.hasEntities(this.theWorld.getChunkFromBlockCoords(blockpos = renderchunk1.getPosition()))) {
                        this.renderInfosEntities.add(renderinfolazy.getRenderInfo());
                    }
                    if (renderchunk1.getCompiledChunk().getTileEntities().size() <= 0) continue;
                    this.renderInfosTileEntities.add(renderinfolazy.getRenderInfo());
                }
            }
        } else {
            this.renderInfos = this.renderInfosNormal;
            this.renderInfosEntities = this.renderInfosEntitiesNormal;
            this.renderInfosTileEntities = this.renderInfosTileEntitiesNormal;
        }
        if (!flag && this.displayListEntitiesDirty && !Shaders.isShadowPass) {
            this.displayListEntitiesDirty = false;
            for (ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation1 : this.renderInfos) {
                this.freeRenderInformation(renderglobal$containerlocalrenderinformation1);
            }
            this.renderInfos.clear();
            this.renderInfosEntities.clear();
            this.renderInfosTileEntities.clear();
            this.visibilityDeque.clear();
            Deque deque = this.visibilityDeque;
            Entity.setRenderDistanceWeight(MathHelper.clamp((double)this.mc.gameSettings.renderDistanceChunks / 8.0, 1.0, 2.5));
            boolean flag2 = this.mc.renderChunksMany;
            if (renderchunk != null) {
                boolean flag3 = false;
                ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation3 = new ContainerLocalRenderInformation(renderchunk, null, 0);
                Set set1 = SET_ALL_FACINGS;
                if (set1.size() == 1) {
                    Vector3f vector3f = this.getViewVector(viewEntity, partialTicks);
                    EnumFacing enumfacing = EnumFacing.getFacingFromVector(vector3f.x, vector3f.y, vector3f.z).getOpposite();
                    set1.remove(enumfacing);
                }
                if (set1.isEmpty()) {
                    flag3 = true;
                }
                if (flag3 && !playerSpectator) {
                    this.renderInfos.add(renderglobal$containerlocalrenderinformation3);
                } else {
                    if (playerSpectator && this.theWorld.getBlockState(blockpos1).isOpaqueCube()) {
                        flag2 = false;
                    }
                    renderchunk.setFrameIndex(frameCount);
                    deque.add(renderglobal$containerlocalrenderinformation3);
                }
            } else {
                int i1 = blockpos1.getY() > 0 ? 248 : 8;
                int j1 = -this.renderDistanceChunks;
                while (j1 <= this.renderDistanceChunks) {
                    int j = -this.renderDistanceChunks;
                    while (j <= this.renderDistanceChunks) {
                        RenderChunk renderchunk2 = this.viewFrustum.getRenderChunk(new BlockPos((j1 << 4) + 8, i1, (j << 4) + 8));
                        if (renderchunk2 != null && camera.isBoundingBoxInFrustum(renderchunk2.boundingBox)) {
                            renderchunk2.setFrameIndex(frameCount);
                            deque.add(new ContainerLocalRenderInformation(renderchunk2, null, 0));
                        }
                        ++j;
                    }
                    ++j1;
                }
            }
            this.mc.mcProfiler.startSection("iteration");
            EnumFacing[] aenumfacing = EnumFacing.VALUES;
            int k1 = aenumfacing.length;
            while (!deque.isEmpty()) {
                ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation4 = (ContainerLocalRenderInformation)deque.poll();
                RenderChunk renderchunk5 = renderglobal$containerlocalrenderinformation4.renderChunk;
                EnumFacing enumfacing2 = renderglobal$containerlocalrenderinformation4.facing;
                boolean flag1 = false;
                CompiledChunk compiledchunk = renderchunk5.compiledChunk;
                if (!compiledchunk.isEmpty() || renderchunk5.isNeedsUpdate()) {
                    this.renderInfos.add(renderglobal$containerlocalrenderinformation4);
                    flag1 = true;
                }
                if (ChunkUtils.hasEntities(renderchunk5.getChunk(this.theWorld))) {
                    this.renderInfosEntities.add(renderglobal$containerlocalrenderinformation4);
                    flag1 = true;
                }
                if (compiledchunk.getTileEntities().size() > 0) {
                    this.renderInfosTileEntities.add(renderglobal$containerlocalrenderinformation4);
                    flag1 = true;
                }
                int k = 0;
                while (k < k1) {
                    RenderChunk renderchunk3;
                    EnumFacing enumfacing1 = aenumfacing[k];
                    if (!(flag2 && renderglobal$containerlocalrenderinformation4.hasDirection(enumfacing1.getOpposite()) || flag2 && enumfacing2 != null && !compiledchunk.isVisible(enumfacing2.getOpposite(), enumfacing1) || (renderchunk3 = this.getRenderChunkOffset(blockpos1, renderchunk5, enumfacing1)) == null || !renderchunk3.setFrameIndex(frameCount) || !camera.isBoundingBoxInFrustum(renderchunk3.boundingBox))) {
                        int l = renderglobal$containerlocalrenderinformation4.setFacing | 1 << enumfacing1.ordinal();
                        ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation = this.allocateRenderInformation(renderchunk3, enumfacing1, l);
                        deque.add(renderglobal$containerlocalrenderinformation);
                    }
                    ++k;
                }
                if (flag1) continue;
                this.freeRenderInformation(renderglobal$containerlocalrenderinformation4);
            }
            this.mc.mcProfiler.endSection();
        }
        this.mc.mcProfiler.endStartSection("captureFrustum");
        if (this.debugFixTerrainFrustum) {
            this.fixTerrainFrustum(d3, d4, d5);
            this.debugFixTerrainFrustum = false;
        }
        Lagometer.timerVisibility.end();
        if (Shaders.isShadowPass) {
            Shaders.mcProfilerEndSection();
        } else {
            this.mc.mcProfiler.endStartSection("rebuildNear");
            Set<RenderChunk> set = this.chunksToUpdate;
            this.chunksToUpdate = Sets.newLinkedHashSet();
            Lagometer.timerChunkUpdate.start();
            for (ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation2 : this.renderInfos) {
                boolean flag4;
                RenderChunk renderchunk4 = renderglobal$containerlocalrenderinformation2.renderChunk;
                if (!renderchunk4.isNeedsUpdate() && !set.contains(renderchunk4)) continue;
                this.displayListEntitiesDirty = true;
                BlockPos blockpos2 = renderchunk4.getPosition();
                boolean bl = flag4 = blockpos1.distanceSq(blockpos2.getX() + 8, blockpos2.getY() + 8, blockpos2.getZ() + 8) < 768.0;
                if (!flag4) {
                    this.chunksToUpdate.add(renderchunk4);
                    continue;
                }
                if (!renderchunk4.isPlayerUpdate()) {
                    this.chunksToUpdateForced.add(renderchunk4);
                    continue;
                }
                this.mc.mcProfiler.startSection("build near");
                this.renderDispatcher.updateChunkNow(renderchunk4);
                renderchunk4.clearNeedsUpdate();
                this.mc.mcProfiler.endSection();
            }
            Lagometer.timerChunkUpdate.end();
            this.chunksToUpdate.addAll(set);
            this.mc.mcProfiler.endSection();
        }
    }

    private Set<EnumFacing> getVisibleFacings(BlockPos pos) {
        VisGraph visgraph = new VisGraph();
        BlockPos blockpos = new BlockPos(pos.getX() >> 4 << 4, pos.getY() >> 4 << 4, pos.getZ() >> 4 << 4);
        Chunk chunk = this.theWorld.getChunkFromBlockCoords(blockpos);
        for (BlockPos.MutableBlockPos blockpos$mutableblockpos : BlockPos.getAllInBoxMutable(blockpos, blockpos.add(15, 15, 15))) {
            if (!chunk.getBlockState(blockpos$mutableblockpos).isOpaqueCube()) continue;
            visgraph.setOpaqueCube(blockpos$mutableblockpos);
        }
        return visgraph.getVisibleFacings(pos);
    }

    @Nullable
    private RenderChunk getRenderChunkOffset(BlockPos playerPos, RenderChunk renderChunkBase, EnumFacing facing) {
        BlockPos blockpos = renderChunkBase.getBlockPosOffset16(facing);
        if (blockpos.getY() >= 0 && blockpos.getY() < 256) {
            int k;
            int i = playerPos.getX() - blockpos.getX();
            int j = playerPos.getZ() - blockpos.getZ();
            if (Config.isFogOff() ? Math.abs(i) > this.renderDistance || Math.abs(j) > this.renderDistance : (k = i * i + j * j) > this.renderDistanceSq) {
                return null;
            }
            return renderChunkBase.getRenderChunkOffset16(this.viewFrustum, facing);
        }
        return null;
    }

    private void fixTerrainFrustum(double x, double y, double z) {
        this.debugFixedClippingHelper = new ClippingHelperImpl();
        ((ClippingHelperImpl)this.debugFixedClippingHelper).init();
        Matrix4f matrix4f = new Matrix4f(this.debugFixedClippingHelper.modelviewMatrix);
        matrix4f.transpose();
        Matrix4f matrix4f1 = new Matrix4f(this.debugFixedClippingHelper.projectionMatrix);
        matrix4f1.transpose();
        Matrix4f matrix4f2 = new Matrix4f();
        Matrix4f.mul((org.lwjgl.util.vector.Matrix4f)matrix4f1, (org.lwjgl.util.vector.Matrix4f)matrix4f, (org.lwjgl.util.vector.Matrix4f)matrix4f2);
        matrix4f2.invert();
        this.debugTerrainFrustumPosition.x = x;
        this.debugTerrainFrustumPosition.y = y;
        this.debugTerrainFrustumPosition.z = z;
        this.debugTerrainMatrix[0] = new Vector4f(-1.0f, -1.0f, -1.0f, 1.0f);
        this.debugTerrainMatrix[1] = new Vector4f(1.0f, -1.0f, -1.0f, 1.0f);
        this.debugTerrainMatrix[2] = new Vector4f(1.0f, 1.0f, -1.0f, 1.0f);
        this.debugTerrainMatrix[3] = new Vector4f(-1.0f, 1.0f, -1.0f, 1.0f);
        this.debugTerrainMatrix[4] = new Vector4f(-1.0f, -1.0f, 1.0f, 1.0f);
        this.debugTerrainMatrix[5] = new Vector4f(1.0f, -1.0f, 1.0f, 1.0f);
        this.debugTerrainMatrix[6] = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.debugTerrainMatrix[7] = new Vector4f(-1.0f, 1.0f, 1.0f, 1.0f);
        int i = 0;
        while (i < 8) {
            Matrix4f.transform((org.lwjgl.util.vector.Matrix4f)matrix4f2, (Vector4f)this.debugTerrainMatrix[i], (Vector4f)this.debugTerrainMatrix[i]);
            this.debugTerrainMatrix[i].x /= this.debugTerrainMatrix[i].w;
            this.debugTerrainMatrix[i].y /= this.debugTerrainMatrix[i].w;
            this.debugTerrainMatrix[i].z /= this.debugTerrainMatrix[i].w;
            this.debugTerrainMatrix[i].w = 1.0f;
            ++i;
        }
    }

    protected Vector3f getViewVector(Entity entityIn, double partialTicks) {
        float f = (float)((double)entityIn.prevRotationPitch + (double)(entityIn.rotationPitch - entityIn.prevRotationPitch) * partialTicks);
        float f1 = (float)((double)entityIn.prevRotationYaw + (double)(entityIn.rotationYaw - entityIn.prevRotationYaw) * partialTicks);
        if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 2) {
            f += 180.0f;
        }
        float f2 = MathHelper.cos(-f1 * ((float)Math.PI / 180) - (float)Math.PI);
        float f3 = MathHelper.sin(-f1 * ((float)Math.PI / 180) - (float)Math.PI);
        float f4 = -MathHelper.cos(-f * ((float)Math.PI / 180));
        float f5 = MathHelper.sin(-f * ((float)Math.PI / 180));
        return new Vector3f(f3 * f4, f5, f2 * f4);
    }

    public int renderBlockLayer(BlockRenderLayer blockLayerIn, double partialTicks, int pass, Entity entityIn) {
        RenderHelper.disableStandardItemLighting();
        if (blockLayerIn == BlockRenderLayer.TRANSLUCENT) {
            this.mc.mcProfiler.startSection("translucent_sort");
            double d0 = entityIn.posX - this.prevRenderSortX;
            double d1 = entityIn.posY - this.prevRenderSortY;
            double d2 = entityIn.posZ - this.prevRenderSortZ;
            if (d0 * d0 + d1 * d1 + d2 * d2 > 1.0) {
                this.prevRenderSortX = entityIn.posX;
                this.prevRenderSortY = entityIn.posY;
                this.prevRenderSortZ = entityIn.posZ;
                int k = 0;
                this.chunksToResortTransparency.clear();
                for (ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation : this.renderInfos) {
                    if (!renderglobal$containerlocalrenderinformation.renderChunk.compiledChunk.isLayerStarted(blockLayerIn) || k++ >= 15) continue;
                    this.chunksToResortTransparency.add(renderglobal$containerlocalrenderinformation.renderChunk);
                }
            }
            this.mc.mcProfiler.endSection();
        }
        this.mc.mcProfiler.startSection("filterempty");
        int l = 0;
        boolean flag = blockLayerIn == BlockRenderLayer.TRANSLUCENT;
        int i1 = flag ? this.renderInfos.size() - 1 : 0;
        int i = flag ? -1 : this.renderInfos.size();
        int j1 = flag ? -1 : 1;
        int j = i1;
        while (j != i) {
            RenderChunk renderchunk = this.renderInfos.get((int)j).renderChunk;
            if (!renderchunk.getCompiledChunk().isLayerEmpty(blockLayerIn)) {
                ++l;
                this.renderContainer.addRenderChunk(renderchunk, blockLayerIn);
            }
            j += j1;
        }
        if (l == 0) {
            this.mc.mcProfiler.endSection();
            return l;
        }
        if (Config.isFogOff() && this.mc.entityRenderer.fogStandard) {
            GlStateManager.disableFog();
        }
        this.mc.mcProfiler.endStartSection("render_" + (Object)((Object)blockLayerIn));
        this.renderBlockLayer(blockLayerIn);
        this.mc.mcProfiler.endSection();
        return l;
    }

    private void renderBlockLayer(BlockRenderLayer blockLayerIn) {
        this.mc.entityRenderer.enableLightmap();
        if (OpenGlHelper.useVbo()) {
            GlStateManager.glEnableClientState(32884);
            OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
            GlStateManager.glEnableClientState(32888);
            OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
            GlStateManager.glEnableClientState(32888);
            OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
            GlStateManager.glEnableClientState(32886);
        }
        if (Config.isShaders()) {
            ShadersRender.preRenderChunkLayer(blockLayerIn);
        }
        this.renderContainer.renderChunkLayer(blockLayerIn);
        if (Config.isShaders()) {
            ShadersRender.postRenderChunkLayer(blockLayerIn);
        }
        if (OpenGlHelper.useVbo()) {
            for (VertexFormatElement vertexformatelement : DefaultVertexFormats.BLOCK.getElements()) {
                VertexFormatElement.EnumUsage vertexformatelement$enumusage = vertexformatelement.getUsage();
                int i = vertexformatelement.getIndex();
                switch (vertexformatelement$enumusage) {
                    case POSITION: {
                        GlStateManager.glDisableClientState(32884);
                        break;
                    }
                    case UV: {
                        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + i);
                        GlStateManager.glDisableClientState(32888);
                        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                        break;
                    }
                    case COLOR: {
                        GlStateManager.glDisableClientState(32886);
                        GlStateManager.resetColor();
                    }
                }
            }
        }
        this.mc.entityRenderer.disableLightmap();
    }

    private void cleanupDamagedBlocks(Iterator<DestroyBlockProgress> iteratorIn) {
        while (iteratorIn.hasNext()) {
            DestroyBlockProgress destroyblockprogress = iteratorIn.next();
            int i = destroyblockprogress.getCreationCloudUpdateTick();
            if (this.cloudTickCounter - i <= 400) continue;
            iteratorIn.remove();
        }
    }

    public void updateClouds() {
        if (Config.isShaders() && Keyboard.isKeyDown((int)61) && Keyboard.isKeyDown((int)19)) {
            Shaders.uninit();
            Shaders.loadShaderPack();
            Reflector.Minecraft_actionKeyF3.setValue(this.mc, Boolean.TRUE);
        }
        ++this.cloudTickCounter;
        if (this.cloudTickCounter % 20 == 0) {
            this.cleanupDamagedBlocks(this.damagedBlocks.values().iterator());
        }
        if (!this.setLightUpdates.isEmpty() && !this.renderDispatcher.hasNoFreeRenderBuilders() && this.chunksToUpdate.isEmpty()) {
            Iterator<BlockPos> iterator = this.setLightUpdates.iterator();
            while (iterator.hasNext()) {
                BlockPos blockpos = iterator.next();
                iterator.remove();
                int i = blockpos.getX();
                int j = blockpos.getY();
                int k = blockpos.getZ();
                this.markBlocksForUpdate(i - 1, j - 1, k - 1, i + 1, j + 1, k + 1, false);
            }
        }
    }

    private void renderSkyEnd() {
        if (Config.isSkyEnabled()) {
            GlStateManager.disableFog();
            GlStateManager.disableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.depthMask(false);
            this.renderEngine.bindTexture(END_SKY_TEXTURES);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            int i = 0;
            while (i < 6) {
                GlStateManager.pushMatrix();
                if (i == 1) {
                    GlStateManager.rotate(90.0f, 1.0f, 0.0f, 0.0f);
                }
                if (i == 2) {
                    GlStateManager.rotate(-90.0f, 1.0f, 0.0f, 0.0f);
                }
                if (i == 3) {
                    GlStateManager.rotate(180.0f, 1.0f, 0.0f, 0.0f);
                }
                if (i == 4) {
                    GlStateManager.rotate(90.0f, 0.0f, 0.0f, 1.0f);
                }
                if (i == 5) {
                    GlStateManager.rotate(-90.0f, 0.0f, 0.0f, 1.0f);
                }
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                int j = 40;
                int k = 40;
                int l = 40;
                if (Config.isCustomColors()) {
                    Vec3d vec3d = new Vec3d((double)j / 255.0, (double)k / 255.0, (double)l / 255.0);
                    vec3d = CustomColors.getWorldSkyColor(vec3d, this.theWorld, this.mc.getRenderViewEntity(), 0.0f);
                    j = (int)(vec3d.xCoord * 255.0);
                    k = (int)(vec3d.yCoord * 255.0);
                    l = (int)(vec3d.zCoord * 255.0);
                }
                bufferbuilder.pos(-100.0, -100.0, -100.0).tex(0.0, 0.0).color(j, k, l, 255).endVertex();
                bufferbuilder.pos(-100.0, -100.0, 100.0).tex(0.0, 16.0).color(j, k, l, 255).endVertex();
                bufferbuilder.pos(100.0, -100.0, 100.0).tex(16.0, 16.0).color(j, k, l, 255).endVertex();
                bufferbuilder.pos(100.0, -100.0, -100.0).tex(16.0, 0.0).color(j, k, l, 255).endVertex();
                tessellator.draw();
                GlStateManager.popMatrix();
                ++i;
            }
            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.enableAlpha();
            GlStateManager.disableBlend();
        }
    }

    public void renderSky(float partialTicks, int pass) {
        WorldProvider worldprovider;
        Object object;
        if (Reflector.ForgeWorldProvider_getSkyRenderer.exists() && (object = Reflector.call(worldprovider = this.mc.world.provider, Reflector.ForgeWorldProvider_getSkyRenderer, new Object[0])) != null) {
            Reflector.callVoid(object, Reflector.IRenderHandler_render, Float.valueOf(partialTicks), this.theWorld, this.mc);
            return;
        }
        if (this.mc.world.provider.getDimensionType() == DimensionType.THE_END) {
            this.renderSkyEnd();
        } else if (this.mc.world.provider.isSurfaceWorld()) {
            float f17;
            GlStateManager.disableTexture2D();
            boolean flag = Config.isShaders();
            if (flag) {
                Shaders.disableTexture2D();
            }
            Vec3d vec3d = this.theWorld.getSkyColor(this.mc.getRenderViewEntity(), partialTicks);
            vec3d = CustomColors.getSkyColor(vec3d, this.mc.world, this.mc.getRenderViewEntity().posX, this.mc.getRenderViewEntity().posY + 1.0, this.mc.getRenderViewEntity().posZ);
            if (flag) {
                Shaders.setSkyColor(vec3d);
            }
            float f = (float)vec3d.xCoord;
            float f1 = (float)vec3d.yCoord;
            float f2 = (float)vec3d.zCoord;
            if (pass != 2) {
                float f3 = (f * 30.0f + f1 * 59.0f + f2 * 11.0f) / 100.0f;
                float f4 = (f * 30.0f + f1 * 70.0f) / 100.0f;
                float f5 = (f * 30.0f + f2 * 70.0f) / 100.0f;
                f = f3;
                f1 = f4;
                f2 = f5;
            }
            GlStateManager.color(f, f1, f2);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            GlStateManager.depthMask(false);
            GlStateManager.enableFog();
            if (flag) {
                Shaders.enableFog();
            }
            GlStateManager.color(f, f1, f2);
            if (flag) {
                Shaders.preSkyList();
            }
            if (Config.isSkyEnabled()) {
                if (this.vboEnabled) {
                    this.skyVBO.bindBuffer();
                    GlStateManager.glEnableClientState(32884);
                    GlStateManager.glVertexPointer(3, 5126, 12, 0);
                    this.skyVBO.drawArrays(7);
                    this.skyVBO.unbindBuffer();
                    GlStateManager.glDisableClientState(32884);
                } else {
                    GlStateManager.callList(this.glSkyList);
                }
            }
            GlStateManager.disableFog();
            if (flag) {
                Shaders.disableFog();
            }
            GlStateManager.disableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderHelper.disableStandardItemLighting();
            float[] afloat = this.theWorld.provider.calcSunriseSunsetColors(this.theWorld.getCelestialAngle(partialTicks), partialTicks);
            if (afloat != null && Config.isSunMoonEnabled()) {
                GlStateManager.disableTexture2D();
                if (flag) {
                    Shaders.disableTexture2D();
                }
                GlStateManager.shadeModel(7425);
                GlStateManager.pushMatrix();
                GlStateManager.rotate(90.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.rotate(MathHelper.sin(this.theWorld.getCelestialAngleRadians(partialTicks)) < 0.0f ? 180.0f : 0.0f, 0.0f, 0.0f, 1.0f);
                GlStateManager.rotate(90.0f, 0.0f, 0.0f, 1.0f);
                float f6 = afloat[0];
                float f7 = afloat[1];
                float f8 = afloat[2];
                if (pass != 2) {
                    float f9 = (f6 * 30.0f + f7 * 59.0f + f8 * 11.0f) / 100.0f;
                    float f10 = (f6 * 30.0f + f7 * 70.0f) / 100.0f;
                    float f11 = (f6 * 30.0f + f8 * 70.0f) / 100.0f;
                    f6 = f9;
                    f7 = f10;
                    f8 = f11;
                }
                bufferbuilder.begin(6, DefaultVertexFormats.POSITION_COLOR);
                bufferbuilder.pos(0.0, 100.0, 0.0).color(f6, f7, f8, afloat[3]).endVertex();
                int j = 16;
                int l = 0;
                while (l <= 16) {
                    float f18 = (float)l * ((float)Math.PI * 2) / 16.0f;
                    float f12 = MathHelper.sin(f18);
                    float f13 = MathHelper.cos(f18);
                    bufferbuilder.pos(f12 * 120.0f, f13 * 120.0f, -f13 * 40.0f * afloat[3]).color(afloat[0], afloat[1], afloat[2], 0.0f).endVertex();
                    ++l;
                }
                tessellator.draw();
                GlStateManager.popMatrix();
                GlStateManager.shadeModel(7424);
            }
            GlStateManager.enableTexture2D();
            if (flag) {
                Shaders.enableTexture2D();
            }
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.pushMatrix();
            float f15 = 1.0f - this.theWorld.getRainStrength(partialTicks);
            GlStateManager.color(1.0f, 1.0f, 1.0f, f15);
            GlStateManager.rotate(-90.0f, 0.0f, 1.0f, 0.0f);
            CustomSky.renderSky(this.theWorld, this.renderEngine, partialTicks);
            if (flag) {
                Shaders.preCelestialRotate();
            }
            GlStateManager.rotate(this.theWorld.getCelestialAngle(partialTicks) * 360.0f, 1.0f, 0.0f, 0.0f);
            if (flag) {
                Shaders.postCelestialRotate();
            }
            float f16 = 30.0f;
            if (Config.isSunTexture()) {
                this.renderEngine.bindTexture(SUN_TEXTURES);
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
                bufferbuilder.pos(-f16, 100.0, -f16).tex(0.0, 0.0).endVertex();
                bufferbuilder.pos(f16, 100.0, -f16).tex(1.0, 0.0).endVertex();
                bufferbuilder.pos(f16, 100.0, f16).tex(1.0, 1.0).endVertex();
                bufferbuilder.pos(-f16, 100.0, f16).tex(0.0, 1.0).endVertex();
                tessellator.draw();
            }
            f16 = 20.0f;
            if (Config.isMoonTexture()) {
                this.renderEngine.bindTexture(MOON_PHASES_TEXTURES);
                int i = this.theWorld.getMoonPhase();
                int k = i % 4;
                int i1 = i / 4 % 2;
                float f19 = (float)(k + 0) / 4.0f;
                float f21 = (float)(i1 + 0) / 2.0f;
                float f23 = (float)(k + 1) / 4.0f;
                float f14 = (float)(i1 + 1) / 2.0f;
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
                bufferbuilder.pos(-f16, -100.0, f16).tex(f23, f14).endVertex();
                bufferbuilder.pos(f16, -100.0, f16).tex(f19, f14).endVertex();
                bufferbuilder.pos(f16, -100.0, -f16).tex(f19, f21).endVertex();
                bufferbuilder.pos(-f16, -100.0, -f16).tex(f23, f21).endVertex();
                tessellator.draw();
            }
            GlStateManager.disableTexture2D();
            if (flag) {
                Shaders.disableTexture2D();
            }
            if ((f17 = this.theWorld.getStarBrightness(partialTicks) * f15) > 0.0f && Config.isStarsEnabled() && !CustomSky.hasSkyLayers(this.theWorld)) {
                GlStateManager.color(f17, f17, f17, f17);
                if (this.vboEnabled) {
                    this.starVBO.bindBuffer();
                    GlStateManager.glEnableClientState(32884);
                    GlStateManager.glVertexPointer(3, 5126, 12, 0);
                    this.starVBO.drawArrays(7);
                    this.starVBO.unbindBuffer();
                    GlStateManager.glDisableClientState(32884);
                } else {
                    GlStateManager.callList(this.starGLCallList);
                }
            }
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.enableFog();
            if (flag) {
                Shaders.enableFog();
            }
            GlStateManager.popMatrix();
            GlStateManager.disableTexture2D();
            if (flag) {
                Shaders.disableTexture2D();
            }
            GlStateManager.color(0.0f, 0.0f, 0.0f);
            double d0 = this.mc.player.getPositionEyes((float)partialTicks).yCoord - this.theWorld.getHorizon();
            if (d0 < 0.0) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(0.0f, 12.0f, 0.0f);
                if (this.vboEnabled) {
                    this.sky2VBO.bindBuffer();
                    GlStateManager.glEnableClientState(32884);
                    GlStateManager.glVertexPointer(3, 5126, 12, 0);
                    this.sky2VBO.drawArrays(7);
                    this.sky2VBO.unbindBuffer();
                    GlStateManager.glDisableClientState(32884);
                } else {
                    GlStateManager.callList(this.glSkyList2);
                }
                GlStateManager.popMatrix();
                float f20 = 1.0f;
                float f22 = -((float)(d0 + 65.0));
                float f24 = -1.0f;
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
                bufferbuilder.pos(-1.0, f22, 1.0).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(1.0, f22, 1.0).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(1.0, -1.0, 1.0).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(-1.0, -1.0, 1.0).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(-1.0, -1.0, -1.0).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(1.0, -1.0, -1.0).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(1.0, f22, -1.0).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(-1.0, f22, -1.0).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(1.0, -1.0, -1.0).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(1.0, -1.0, 1.0).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(1.0, f22, 1.0).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(1.0, f22, -1.0).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(-1.0, f22, -1.0).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(-1.0, f22, 1.0).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(-1.0, -1.0, 1.0).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(-1.0, -1.0, -1.0).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(-1.0, -1.0, -1.0).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(-1.0, -1.0, 1.0).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(1.0, -1.0, 1.0).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(1.0, -1.0, -1.0).color(0, 0, 0, 255).endVertex();
                tessellator.draw();
            }
            if (this.theWorld.provider.isSkyColored()) {
                GlStateManager.color(f * 0.2f + 0.04f, f1 * 0.2f + 0.04f, f2 * 0.6f + 0.1f);
            } else {
                GlStateManager.color(f, f1, f2);
            }
            if (this.mc.gameSettings.renderDistanceChunks <= 4) {
                GlStateManager.color(this.mc.entityRenderer.fogColorRed, this.mc.entityRenderer.fogColorGreen, this.mc.entityRenderer.fogColorBlue);
            }
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0f, -((float)(d0 - 16.0)), 0.0f);
            if (Config.isSkyEnabled()) {
                GlStateManager.callList(this.glSkyList2);
            }
            GlStateManager.popMatrix();
            GlStateManager.enableTexture2D();
            if (flag) {
                Shaders.enableTexture2D();
            }
            GlStateManager.depthMask(true);
        }
    }

    public void renderClouds(float partialTicks, int pass, double p_180447_3_, double p_180447_5_, double p_180447_7_) {
        if (!Config.isCloudsOff()) {
            WorldProvider worldprovider;
            Object object;
            if (Reflector.ForgeWorldProvider_getCloudRenderer.exists() && (object = Reflector.call(worldprovider = this.mc.world.provider, Reflector.ForgeWorldProvider_getCloudRenderer, new Object[0])) != null) {
                Reflector.callVoid(object, Reflector.IRenderHandler_render, Float.valueOf(partialTicks), this.theWorld, this.mc);
                return;
            }
            if (this.mc.world.provider.isSurfaceWorld()) {
                if (Config.isShaders()) {
                    Shaders.beginClouds();
                }
                if (Config.isCloudsFancy()) {
                    this.renderCloudsFancy(partialTicks, pass, p_180447_3_, p_180447_5_, p_180447_7_);
                } else {
                    float f9 = partialTicks;
                    partialTicks = 0.0f;
                    GlStateManager.disableCull();
                    int j1 = 32;
                    int i = 8;
                    Tessellator tessellator = Tessellator.getInstance();
                    BufferBuilder bufferbuilder = tessellator.getBuffer();
                    this.renderEngine.bindTexture(CLOUDS_TEXTURES);
                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                    Vec3d vec3d = this.theWorld.getCloudColour(partialTicks);
                    float f = (float)vec3d.xCoord;
                    float f1 = (float)vec3d.yCoord;
                    float f2 = (float)vec3d.zCoord;
                    this.cloudRenderer.prepareToRender(false, this.cloudTickCounter, f9, vec3d);
                    if (this.cloudRenderer.shouldUpdateGlList()) {
                        this.cloudRenderer.startUpdateGlList();
                        if (pass != 2) {
                            float f3 = (f * 30.0f + f1 * 59.0f + f2 * 11.0f) / 100.0f;
                            float f4 = (f * 30.0f + f1 * 70.0f) / 100.0f;
                            float f5 = (f * 30.0f + f2 * 70.0f) / 100.0f;
                            f = f3;
                            f1 = f4;
                            f2 = f5;
                        }
                        float f10 = 4.8828125E-4f;
                        double d2 = (float)this.cloudTickCounter + partialTicks;
                        double d0 = p_180447_3_ + d2 * (double)0.03f;
                        int j = MathHelper.floor(d0 / 2048.0);
                        int k = MathHelper.floor(p_180447_7_ / 2048.0);
                        double d1 = p_180447_7_ - (double)(k * 2048);
                        float f6 = this.theWorld.provider.getCloudHeight() - (float)p_180447_5_ + 0.33f;
                        f6 += this.mc.gameSettings.ofCloudsHeight * 128.0f;
                        float f7 = (float)((d0 -= (double)(j * 2048)) * 4.8828125E-4);
                        float f8 = (float)(d1 * 4.8828125E-4);
                        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                        int l = -256;
                        while (l < 256) {
                            int i1 = -256;
                            while (i1 < 256) {
                                bufferbuilder.pos(l + 0, f6, i1 + 32).tex((float)(l + 0) * 4.8828125E-4f + f7, (float)(i1 + 32) * 4.8828125E-4f + f8).color(f, f1, f2, 0.8f).endVertex();
                                bufferbuilder.pos(l + 32, f6, i1 + 32).tex((float)(l + 32) * 4.8828125E-4f + f7, (float)(i1 + 32) * 4.8828125E-4f + f8).color(f, f1, f2, 0.8f).endVertex();
                                bufferbuilder.pos(l + 32, f6, i1 + 0).tex((float)(l + 32) * 4.8828125E-4f + f7, (float)(i1 + 0) * 4.8828125E-4f + f8).color(f, f1, f2, 0.8f).endVertex();
                                bufferbuilder.pos(l + 0, f6, i1 + 0).tex((float)(l + 0) * 4.8828125E-4f + f7, (float)(i1 + 0) * 4.8828125E-4f + f8).color(f, f1, f2, 0.8f).endVertex();
                                i1 += 32;
                            }
                            l += 32;
                        }
                        tessellator.draw();
                        this.cloudRenderer.endUpdateGlList();
                    }
                    this.cloudRenderer.renderGlList();
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                    GlStateManager.disableBlend();
                    GlStateManager.enableCull();
                }
                if (Config.isShaders()) {
                    Shaders.endClouds();
                }
            }
        }
    }

    public boolean hasCloudFog(double x, double y, double z, float partialTicks) {
        return false;
    }

    private void renderCloudsFancy(float partialTicks, int pass, double p_180445_3_, double p_180445_5_, double p_180445_7_) {
        float f251 = 0.0f;
        GlStateManager.disableCull();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        float f = 12.0f;
        float f1 = 4.0f;
        double d0 = (float)this.cloudTickCounter + f251;
        double d1 = (p_180445_3_ + d0 * (double)0.03f) / 12.0;
        double d2 = p_180445_7_ / 12.0 + (double)0.33f;
        float f2 = this.theWorld.provider.getCloudHeight() - (float)p_180445_5_ + 0.33f;
        f2 += this.mc.gameSettings.ofCloudsHeight * 128.0f;
        int i = MathHelper.floor(d1 / 2048.0);
        int j = MathHelper.floor(d2 / 2048.0);
        d1 -= (double)(i * 2048);
        d2 -= (double)(j * 2048);
        this.renderEngine.bindTexture(CLOUDS_TEXTURES);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        Vec3d vec3d = this.theWorld.getCloudColour(f251);
        float f3 = (float)vec3d.xCoord;
        float f4 = (float)vec3d.yCoord;
        float f5 = (float)vec3d.zCoord;
        this.cloudRenderer.prepareToRender(true, this.cloudTickCounter, partialTicks, vec3d);
        if (pass != 2) {
            float f6 = (f3 * 30.0f + f4 * 59.0f + f5 * 11.0f) / 100.0f;
            float f7 = (f3 * 30.0f + f4 * 70.0f) / 100.0f;
            float f8 = (f3 * 30.0f + f5 * 70.0f) / 100.0f;
            f3 = f6;
            f4 = f7;
            f5 = f8;
        }
        float f26 = f3 * 0.9f;
        float f27 = f4 * 0.9f;
        float f28 = f5 * 0.9f;
        float f9 = f3 * 0.7f;
        float f10 = f4 * 0.7f;
        float f11 = f5 * 0.7f;
        float f12 = f3 * 0.8f;
        float f13 = f4 * 0.8f;
        float f14 = f5 * 0.8f;
        float f15 = 0.00390625f;
        float f16 = (float)MathHelper.floor(d1) * 0.00390625f;
        float f17 = (float)MathHelper.floor(d2) * 0.00390625f;
        float f18 = (float)(d1 - (double)MathHelper.floor(d1));
        float f19 = (float)(d2 - (double)MathHelper.floor(d2));
        int k = 8;
        int l = 4;
        float f20 = 9.765625E-4f;
        GlStateManager.scale(12.0f, 1.0f, 12.0f);
        int i1 = 0;
        while (i1 < 2) {
            if (i1 == 0) {
                GlStateManager.colorMask(false, false, false, false);
            } else {
                switch (pass) {
                    case 0: {
                        GlStateManager.colorMask(false, true, true, true);
                        break;
                    }
                    case 1: {
                        GlStateManager.colorMask(true, false, false, true);
                        break;
                    }
                    case 2: {
                        GlStateManager.colorMask(true, true, true, true);
                    }
                }
            }
            this.cloudRenderer.renderGlList();
            ++i1;
        }
        if (this.cloudRenderer.shouldUpdateGlList()) {
            this.cloudRenderer.startUpdateGlList();
            int l1 = -3;
            while (l1 <= 4) {
                int j1 = -3;
                while (j1 <= 4) {
                    bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
                    float f21 = l1 * 8;
                    float f22 = j1 * 8;
                    float f23 = f21 - f18;
                    float f24 = f22 - f19;
                    if (f2 > -5.0f) {
                        bufferbuilder.pos(f23 + 0.0f, f2 + 0.0f, f24 + 8.0f).tex((f21 + 0.0f) * 0.00390625f + f16, (f22 + 8.0f) * 0.00390625f + f17).color(f9, f10, f11, 0.8f).normal(0.0f, -1.0f, 0.0f).endVertex();
                        bufferbuilder.pos(f23 + 8.0f, f2 + 0.0f, f24 + 8.0f).tex((f21 + 8.0f) * 0.00390625f + f16, (f22 + 8.0f) * 0.00390625f + f17).color(f9, f10, f11, 0.8f).normal(0.0f, -1.0f, 0.0f).endVertex();
                        bufferbuilder.pos(f23 + 8.0f, f2 + 0.0f, f24 + 0.0f).tex((f21 + 8.0f) * 0.00390625f + f16, (f22 + 0.0f) * 0.00390625f + f17).color(f9, f10, f11, 0.8f).normal(0.0f, -1.0f, 0.0f).endVertex();
                        bufferbuilder.pos(f23 + 0.0f, f2 + 0.0f, f24 + 0.0f).tex((f21 + 0.0f) * 0.00390625f + f16, (f22 + 0.0f) * 0.00390625f + f17).color(f9, f10, f11, 0.8f).normal(0.0f, -1.0f, 0.0f).endVertex();
                    }
                    if (f2 <= 5.0f) {
                        bufferbuilder.pos(f23 + 0.0f, f2 + 4.0f - 9.765625E-4f, f24 + 8.0f).tex((f21 + 0.0f) * 0.00390625f + f16, (f22 + 8.0f) * 0.00390625f + f17).color(f3, f4, f5, 0.8f).normal(0.0f, 1.0f, 0.0f).endVertex();
                        bufferbuilder.pos(f23 + 8.0f, f2 + 4.0f - 9.765625E-4f, f24 + 8.0f).tex((f21 + 8.0f) * 0.00390625f + f16, (f22 + 8.0f) * 0.00390625f + f17).color(f3, f4, f5, 0.8f).normal(0.0f, 1.0f, 0.0f).endVertex();
                        bufferbuilder.pos(f23 + 8.0f, f2 + 4.0f - 9.765625E-4f, f24 + 0.0f).tex((f21 + 8.0f) * 0.00390625f + f16, (f22 + 0.0f) * 0.00390625f + f17).color(f3, f4, f5, 0.8f).normal(0.0f, 1.0f, 0.0f).endVertex();
                        bufferbuilder.pos(f23 + 0.0f, f2 + 4.0f - 9.765625E-4f, f24 + 0.0f).tex((f21 + 0.0f) * 0.00390625f + f16, (f22 + 0.0f) * 0.00390625f + f17).color(f3, f4, f5, 0.8f).normal(0.0f, 1.0f, 0.0f).endVertex();
                    }
                    if (l1 > -1) {
                        int k1 = 0;
                        while (k1 < 8) {
                            bufferbuilder.pos(f23 + (float)k1 + 0.0f, f2 + 0.0f, f24 + 8.0f).tex((f21 + (float)k1 + 0.5f) * 0.00390625f + f16, (f22 + 8.0f) * 0.00390625f + f17).color(f26, f27, f28, 0.8f).normal(-1.0f, 0.0f, 0.0f).endVertex();
                            bufferbuilder.pos(f23 + (float)k1 + 0.0f, f2 + 4.0f, f24 + 8.0f).tex((f21 + (float)k1 + 0.5f) * 0.00390625f + f16, (f22 + 8.0f) * 0.00390625f + f17).color(f26, f27, f28, 0.8f).normal(-1.0f, 0.0f, 0.0f).endVertex();
                            bufferbuilder.pos(f23 + (float)k1 + 0.0f, f2 + 4.0f, f24 + 0.0f).tex((f21 + (float)k1 + 0.5f) * 0.00390625f + f16, (f22 + 0.0f) * 0.00390625f + f17).color(f26, f27, f28, 0.8f).normal(-1.0f, 0.0f, 0.0f).endVertex();
                            bufferbuilder.pos(f23 + (float)k1 + 0.0f, f2 + 0.0f, f24 + 0.0f).tex((f21 + (float)k1 + 0.5f) * 0.00390625f + f16, (f22 + 0.0f) * 0.00390625f + f17).color(f26, f27, f28, 0.8f).normal(-1.0f, 0.0f, 0.0f).endVertex();
                            ++k1;
                        }
                    }
                    if (l1 <= 1) {
                        int i2 = 0;
                        while (i2 < 8) {
                            bufferbuilder.pos(f23 + (float)i2 + 1.0f - 9.765625E-4f, f2 + 0.0f, f24 + 8.0f).tex((f21 + (float)i2 + 0.5f) * 0.00390625f + f16, (f22 + 8.0f) * 0.00390625f + f17).color(f26, f27, f28, 0.8f).normal(1.0f, 0.0f, 0.0f).endVertex();
                            bufferbuilder.pos(f23 + (float)i2 + 1.0f - 9.765625E-4f, f2 + 4.0f, f24 + 8.0f).tex((f21 + (float)i2 + 0.5f) * 0.00390625f + f16, (f22 + 8.0f) * 0.00390625f + f17).color(f26, f27, f28, 0.8f).normal(1.0f, 0.0f, 0.0f).endVertex();
                            bufferbuilder.pos(f23 + (float)i2 + 1.0f - 9.765625E-4f, f2 + 4.0f, f24 + 0.0f).tex((f21 + (float)i2 + 0.5f) * 0.00390625f + f16, (f22 + 0.0f) * 0.00390625f + f17).color(f26, f27, f28, 0.8f).normal(1.0f, 0.0f, 0.0f).endVertex();
                            bufferbuilder.pos(f23 + (float)i2 + 1.0f - 9.765625E-4f, f2 + 0.0f, f24 + 0.0f).tex((f21 + (float)i2 + 0.5f) * 0.00390625f + f16, (f22 + 0.0f) * 0.00390625f + f17).color(f26, f27, f28, 0.8f).normal(1.0f, 0.0f, 0.0f).endVertex();
                            ++i2;
                        }
                    }
                    if (j1 > -1) {
                        int j2 = 0;
                        while (j2 < 8) {
                            bufferbuilder.pos(f23 + 0.0f, f2 + 4.0f, f24 + (float)j2 + 0.0f).tex((f21 + 0.0f) * 0.00390625f + f16, (f22 + (float)j2 + 0.5f) * 0.00390625f + f17).color(f12, f13, f14, 0.8f).normal(0.0f, 0.0f, -1.0f).endVertex();
                            bufferbuilder.pos(f23 + 8.0f, f2 + 4.0f, f24 + (float)j2 + 0.0f).tex((f21 + 8.0f) * 0.00390625f + f16, (f22 + (float)j2 + 0.5f) * 0.00390625f + f17).color(f12, f13, f14, 0.8f).normal(0.0f, 0.0f, -1.0f).endVertex();
                            bufferbuilder.pos(f23 + 8.0f, f2 + 0.0f, f24 + (float)j2 + 0.0f).tex((f21 + 8.0f) * 0.00390625f + f16, (f22 + (float)j2 + 0.5f) * 0.00390625f + f17).color(f12, f13, f14, 0.8f).normal(0.0f, 0.0f, -1.0f).endVertex();
                            bufferbuilder.pos(f23 + 0.0f, f2 + 0.0f, f24 + (float)j2 + 0.0f).tex((f21 + 0.0f) * 0.00390625f + f16, (f22 + (float)j2 + 0.5f) * 0.00390625f + f17).color(f12, f13, f14, 0.8f).normal(0.0f, 0.0f, -1.0f).endVertex();
                            ++j2;
                        }
                    }
                    if (j1 <= 1) {
                        int k2 = 0;
                        while (k2 < 8) {
                            bufferbuilder.pos(f23 + 0.0f, f2 + 4.0f, f24 + (float)k2 + 1.0f - 9.765625E-4f).tex((f21 + 0.0f) * 0.00390625f + f16, (f22 + (float)k2 + 0.5f) * 0.00390625f + f17).color(f12, f13, f14, 0.8f).normal(0.0f, 0.0f, 1.0f).endVertex();
                            bufferbuilder.pos(f23 + 8.0f, f2 + 4.0f, f24 + (float)k2 + 1.0f - 9.765625E-4f).tex((f21 + 8.0f) * 0.00390625f + f16, (f22 + (float)k2 + 0.5f) * 0.00390625f + f17).color(f12, f13, f14, 0.8f).normal(0.0f, 0.0f, 1.0f).endVertex();
                            bufferbuilder.pos(f23 + 8.0f, f2 + 0.0f, f24 + (float)k2 + 1.0f - 9.765625E-4f).tex((f21 + 8.0f) * 0.00390625f + f16, (f22 + (float)k2 + 0.5f) * 0.00390625f + f17).color(f12, f13, f14, 0.8f).normal(0.0f, 0.0f, 1.0f).endVertex();
                            bufferbuilder.pos(f23 + 0.0f, f2 + 0.0f, f24 + (float)k2 + 1.0f - 9.765625E-4f).tex((f21 + 0.0f) * 0.00390625f + f16, (f22 + (float)k2 + 0.5f) * 0.00390625f + f17).color(f12, f13, f14, 0.8f).normal(0.0f, 0.0f, 1.0f).endVertex();
                            ++k2;
                        }
                    }
                    tessellator.draw();
                    ++j1;
                }
                ++l1;
            }
            this.cloudRenderer.endUpdateGlList();
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
    }

    public void updateChunks(long finishTimeNano) {
        RenderChunk renderchunk2;
        Iterator iterator2;
        finishTimeNano = (long)((double)finishTimeNano + 1.0E8);
        this.displayListEntitiesDirty |= this.renderDispatcher.runChunkUploads(finishTimeNano);
        if (this.chunksToUpdateForced.size() > 0) {
            Iterator iterator = this.chunksToUpdateForced.iterator();
            while (iterator.hasNext()) {
                RenderChunk renderchunk = (RenderChunk)iterator.next();
                if (!this.renderDispatcher.updateChunkLater(renderchunk)) break;
                renderchunk.clearNeedsUpdate();
                iterator.remove();
                this.chunksToUpdate.remove(renderchunk);
                this.chunksToResortTransparency.remove(renderchunk);
            }
        }
        if (this.chunksToResortTransparency.size() > 0 && (iterator2 = this.chunksToResortTransparency.iterator()).hasNext() && this.renderDispatcher.updateTransparencyLater(renderchunk2 = (RenderChunk)iterator2.next())) {
            iterator2.remove();
        }
        int j = 0;
        int k = Config.getUpdatesPerFrame();
        int i = k * 2;
        if (!this.chunksToUpdate.isEmpty()) {
            Iterator<RenderChunk> iterator1 = this.chunksToUpdate.iterator();
            while (iterator1.hasNext()) {
                RenderChunk renderchunk1 = iterator1.next();
                boolean flag = renderchunk1.isNeedsUpdateCustom() ? this.renderDispatcher.updateChunkNow(renderchunk1) : this.renderDispatcher.updateChunkLater(renderchunk1);
                if (!flag) break;
                renderchunk1.clearNeedsUpdate();
                iterator1.remove();
                if (renderchunk1.getCompiledChunk().isEmpty() && k < i) {
                    ++k;
                }
                if (++j >= k) break;
            }
        }
    }

    public void renderWorldBorder(Entity entityIn, float partialTicks) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        WorldBorder worldborder = this.theWorld.getWorldBorder();
        double d0 = this.mc.gameSettings.renderDistanceChunks * 16;
        if (entityIn.posX >= worldborder.maxX() - d0 || entityIn.posX <= worldborder.minX() + d0 || entityIn.posZ >= worldborder.maxZ() - d0 || entityIn.posZ <= worldborder.minZ() + d0) {
            double d1 = 1.0 - worldborder.getClosestDistance(entityIn) / d0;
            d1 = Math.pow(d1, 4.0);
            double d2 = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * (double)partialTicks;
            double d3 = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * (double)partialTicks;
            double d4 = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * (double)partialTicks;
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            this.renderEngine.bindTexture(FORCEFIELD_TEXTURES);
            GlStateManager.depthMask(false);
            GlStateManager.pushMatrix();
            int i = worldborder.getStatus().getID();
            float f = (float)(i >> 16 & 0xFF) / 255.0f;
            float f1 = (float)(i >> 8 & 0xFF) / 255.0f;
            float f2 = (float)(i & 0xFF) / 255.0f;
            GlStateManager.color(f, f1, f2, (float)d1);
            GlStateManager.doPolygonOffset(-3.0f, -3.0f);
            GlStateManager.enablePolygonOffset();
            GlStateManager.alphaFunc(516, 0.1f);
            GlStateManager.enableAlpha();
            GlStateManager.disableCull();
            float f3 = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0f;
            float f4 = 0.0f;
            float f5 = 0.0f;
            float f6 = 128.0f;
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
            bufferbuilder.setTranslation(-d2, -d3, -d4);
            double d5 = Math.max((double)MathHelper.floor(d4 - d0), worldborder.minZ());
            double d6 = Math.min((double)MathHelper.ceil(d4 + d0), worldborder.maxZ());
            if (d2 > worldborder.maxX() - d0) {
                float f7 = 0.0f;
                double d7 = d5;
                while (d7 < d6) {
                    double d8 = Math.min(1.0, d6 - d7);
                    float f8 = (float)d8 * 0.5f;
                    bufferbuilder.pos(worldborder.maxX(), 256.0, d7).tex(f3 + f7, f3 + 0.0f).endVertex();
                    bufferbuilder.pos(worldborder.maxX(), 256.0, d7 + d8).tex(f3 + f8 + f7, f3 + 0.0f).endVertex();
                    bufferbuilder.pos(worldborder.maxX(), 0.0, d7 + d8).tex(f3 + f8 + f7, f3 + 128.0f).endVertex();
                    bufferbuilder.pos(worldborder.maxX(), 0.0, d7).tex(f3 + f7, f3 + 128.0f).endVertex();
                    d7 += 1.0;
                    f7 += 0.5f;
                }
            }
            if (d2 < worldborder.minX() + d0) {
                float f9 = 0.0f;
                double d9 = d5;
                while (d9 < d6) {
                    double d12 = Math.min(1.0, d6 - d9);
                    float f12 = (float)d12 * 0.5f;
                    bufferbuilder.pos(worldborder.minX(), 256.0, d9).tex(f3 + f9, f3 + 0.0f).endVertex();
                    bufferbuilder.pos(worldborder.minX(), 256.0, d9 + d12).tex(f3 + f12 + f9, f3 + 0.0f).endVertex();
                    bufferbuilder.pos(worldborder.minX(), 0.0, d9 + d12).tex(f3 + f12 + f9, f3 + 128.0f).endVertex();
                    bufferbuilder.pos(worldborder.minX(), 0.0, d9).tex(f3 + f9, f3 + 128.0f).endVertex();
                    d9 += 1.0;
                    f9 += 0.5f;
                }
            }
            d5 = Math.max((double)MathHelper.floor(d2 - d0), worldborder.minX());
            d6 = Math.min((double)MathHelper.ceil(d2 + d0), worldborder.maxX());
            if (d4 > worldborder.maxZ() - d0) {
                float f10 = 0.0f;
                double d10 = d5;
                while (d10 < d6) {
                    double d13 = Math.min(1.0, d6 - d10);
                    float f13 = (float)d13 * 0.5f;
                    bufferbuilder.pos(d10, 256.0, worldborder.maxZ()).tex(f3 + f10, f3 + 0.0f).endVertex();
                    bufferbuilder.pos(d10 + d13, 256.0, worldborder.maxZ()).tex(f3 + f13 + f10, f3 + 0.0f).endVertex();
                    bufferbuilder.pos(d10 + d13, 0.0, worldborder.maxZ()).tex(f3 + f13 + f10, f3 + 128.0f).endVertex();
                    bufferbuilder.pos(d10, 0.0, worldborder.maxZ()).tex(f3 + f10, f3 + 128.0f).endVertex();
                    d10 += 1.0;
                    f10 += 0.5f;
                }
            }
            if (d4 < worldborder.minZ() + d0) {
                float f11 = 0.0f;
                double d11 = d5;
                while (d11 < d6) {
                    double d14 = Math.min(1.0, d6 - d11);
                    float f14 = (float)d14 * 0.5f;
                    bufferbuilder.pos(d11, 256.0, worldborder.minZ()).tex(f3 + f11, f3 + 0.0f).endVertex();
                    bufferbuilder.pos(d11 + d14, 256.0, worldborder.minZ()).tex(f3 + f14 + f11, f3 + 0.0f).endVertex();
                    bufferbuilder.pos(d11 + d14, 0.0, worldborder.minZ()).tex(f3 + f14 + f11, f3 + 128.0f).endVertex();
                    bufferbuilder.pos(d11, 0.0, worldborder.minZ()).tex(f3 + f11, f3 + 128.0f).endVertex();
                    d11 += 1.0;
                    f11 += 0.5f;
                }
            }
            tessellator.draw();
            bufferbuilder.setTranslation(0.0, 0.0, 0.0);
            GlStateManager.enableCull();
            GlStateManager.disableAlpha();
            GlStateManager.doPolygonOffset(0.0f, 0.0f);
            GlStateManager.disablePolygonOffset();
            GlStateManager.enableAlpha();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
            GlStateManager.depthMask(true);
        }
    }

    private void preRenderDamagedBlocks() {
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.enableBlend();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 0.5f);
        GlStateManager.doPolygonOffset(-3.0f, -3.0f);
        GlStateManager.enablePolygonOffset();
        GlStateManager.alphaFunc(516, 0.1f);
        GlStateManager.enableAlpha();
        GlStateManager.pushMatrix();
        if (Config.isShaders()) {
            ShadersRender.beginBlockDamage();
        }
    }

    private void postRenderDamagedBlocks() {
        GlStateManager.disableAlpha();
        GlStateManager.doPolygonOffset(0.0f, 0.0f);
        GlStateManager.disablePolygonOffset();
        GlStateManager.enableAlpha();
        GlStateManager.depthMask(true);
        GlStateManager.popMatrix();
        if (Config.isShaders()) {
            ShadersRender.endBlockDamage();
        }
    }

    public void drawBlockDamageTexture(Tessellator tessellatorIn, BufferBuilder worldRendererIn, Entity entityIn, float partialTicks) {
        double d0 = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * (double)partialTicks;
        double d1 = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * (double)partialTicks;
        double d2 = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * (double)partialTicks;
        if (!this.damagedBlocks.isEmpty()) {
            this.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            this.preRenderDamagedBlocks();
            worldRendererIn.begin(7, DefaultVertexFormats.BLOCK);
            worldRendererIn.setTranslation(-d0, -d1, -d2);
            worldRendererIn.noColor();
            Iterator<DestroyBlockProgress> iterator = this.damagedBlocks.values().iterator();
            while (iterator.hasNext()) {
                boolean flag;
                DestroyBlockProgress destroyblockprogress = iterator.next();
                BlockPos blockpos = destroyblockprogress.getPosition();
                double d3 = (double)blockpos.getX() - d0;
                double d4 = (double)blockpos.getY() - d1;
                double d5 = (double)blockpos.getZ() - d2;
                Block block = this.theWorld.getBlockState(blockpos).getBlock();
                if (Reflector.ForgeTileEntity_canRenderBreaking.exists()) {
                    TileEntity tileentity;
                    boolean flag1;
                    boolean bl = flag1 = block instanceof BlockChest || block instanceof BlockEnderChest || block instanceof BlockSign || block instanceof BlockSkull;
                    if (!flag1 && (tileentity = this.theWorld.getTileEntity(blockpos)) != null) {
                        flag1 = Reflector.callBoolean(tileentity, Reflector.ForgeTileEntity_canRenderBreaking, new Object[0]);
                    }
                    flag = !flag1;
                } else {
                    boolean bl = flag = !(block instanceof BlockChest) && !(block instanceof BlockEnderChest) && !(block instanceof BlockSign) && !(block instanceof BlockSkull);
                }
                if (!flag) continue;
                if (d3 * d3 + d4 * d4 + d5 * d5 > 1024.0) {
                    iterator.remove();
                    continue;
                }
                IBlockState iblockstate = this.theWorld.getBlockState(blockpos);
                if (iblockstate.getMaterial() == Material.AIR) continue;
                int i = destroyblockprogress.getPartialBlockDamage();
                TextureAtlasSprite textureatlassprite = this.destroyBlockIcons[i];
                BlockRendererDispatcher blockrendererdispatcher = this.mc.getBlockRendererDispatcher();
                blockrendererdispatcher.renderBlockDamage(iblockstate, blockpos, textureatlassprite, this.theWorld);
            }
            tessellatorIn.draw();
            worldRendererIn.setTranslation(0.0, 0.0, 0.0);
            this.postRenderDamagedBlocks();
        }
    }

    public void drawSelectionBox(EntityPlayer player, RayTraceResult movingObjectPositionIn, int execute, float partialTicks) {
        if (execute == 0 && movingObjectPositionIn.typeOfHit == RayTraceResult.Type.BLOCK) {
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.glLineWidth(2.0f);
            GlStateManager.disableTexture2D();
            if (Config.isShaders()) {
                Shaders.disableTexture2D();
            }
            GlStateManager.depthMask(false);
            BlockPos blockpos = movingObjectPositionIn.getBlockPos();
            IBlockState iblockstate = this.theWorld.getBlockState(blockpos);
            if (iblockstate.getMaterial() != Material.AIR && this.theWorld.getWorldBorder().contains(blockpos)) {
                double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double)partialTicks;
                double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double)partialTicks;
                double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double)partialTicks;
                if (Wolfram.getWolfram().getClientSettings().getBoolean("block_overlay") && !Wolfram.getWolfram().moduleManager.isEnabled("freecam")) {
                    RenderUtils.drawOutlinedBox(iblockstate.getSelectedBoundingBox(this.theWorld, blockpos).expand(0.002f, 0.002f, 0.002f).offset(-d0, -d1, -d2), GuiManager.getHexMainColor() + -1610612736, false);
                    RenderUtils.drawBox(iblockstate.getSelectedBoundingBox(this.theWorld, blockpos).expand(0.002f, 0.002f, 0.002f).offset(-d0, -d1, -d2), GuiManager.getHexMainColor() + 0x20000000, false);
                } else {
                    RenderGlobal.drawSelectionBoundingBox(iblockstate.getSelectedBoundingBox(this.theWorld, blockpos).expandXyz(0.002f).offset(-d0, -d1, -d2), 0.0f, 0.0f, 0.0f, 0.4f);
                }
            }
            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            if (Config.isShaders()) {
                Shaders.enableTexture2D();
            }
            GlStateManager.disableBlend();
        }
    }

    public static void drawSelectionBoundingBox(AxisAlignedBB box, float red, float green, float blue, float alpha) {
        RenderGlobal.drawBoundingBox(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, red, green, blue, alpha);
    }

    public static void drawBoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        RenderGlobal.drawBoundingBox(bufferbuilder, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha);
        tessellator.draw();
    }

    public static void drawBoundingBox(BufferBuilder buffer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha) {
        buffer.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, maxY, minZ).color(red, green, blue, 0.0f).endVertex();
        buffer.pos(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, minY, maxZ).color(red, green, blue, 0.0f).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, maxY, minZ).color(red, green, blue, 0.0f).endVertex();
        buffer.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
    }

    public static void renderFilledBox(AxisAlignedBB p_189696_0_, float p_189696_1_, float p_189696_2_, float p_189696_3_, float p_189696_4_) {
        RenderGlobal.renderFilledBox(p_189696_0_.minX, p_189696_0_.minY, p_189696_0_.minZ, p_189696_0_.maxX, p_189696_0_.maxY, p_189696_0_.maxZ, p_189696_1_, p_189696_2_, p_189696_3_, p_189696_4_);
    }

    public static void renderFilledBox(double p_189695_0_, double p_189695_2_, double p_189695_4_, double p_189695_6_, double p_189695_8_, double p_189695_10_, float p_189695_12_, float p_189695_13_, float p_189695_14_, float p_189695_15_) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);
        RenderGlobal.addChainedFilledBoxVertices(bufferbuilder, p_189695_0_, p_189695_2_, p_189695_4_, p_189695_6_, p_189695_8_, p_189695_10_, p_189695_12_, p_189695_13_, p_189695_14_, p_189695_15_);
        tessellator.draw();
    }

    public static void addChainedFilledBoxVertices(BufferBuilder p_189693_0_, double p_189693_1_, double p_189693_3_, double p_189693_5_, double p_189693_7_, double p_189693_9_, double p_189693_11_, float p_189693_13_, float p_189693_14_, float p_189693_15_, float p_189693_16_) {
        p_189693_0_.pos(p_189693_1_, p_189693_3_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_1_, p_189693_3_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_1_, p_189693_3_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_1_, p_189693_3_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_1_, p_189693_9_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_1_, p_189693_9_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_1_, p_189693_9_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_1_, p_189693_3_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_7_, p_189693_9_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_7_, p_189693_3_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_7_, p_189693_3_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_7_, p_189693_3_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_7_, p_189693_9_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_7_, p_189693_9_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_7_, p_189693_9_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_7_, p_189693_3_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_1_, p_189693_9_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_1_, p_189693_3_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_1_, p_189693_3_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_7_, p_189693_3_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_1_, p_189693_3_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_7_, p_189693_3_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_7_, p_189693_3_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_1_, p_189693_9_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_1_, p_189693_9_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_1_, p_189693_9_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_7_, p_189693_9_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_7_, p_189693_9_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_7_, p_189693_9_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_7_, p_189693_9_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
    }

    private void markBlocksForUpdate(int p_184385_1_, int p_184385_2_, int p_184385_3_, int p_184385_4_, int p_184385_5_, int p_184385_6_, boolean p_184385_7_) {
        this.viewFrustum.markBlocksForUpdate(p_184385_1_, p_184385_2_, p_184385_3_, p_184385_4_, p_184385_5_, p_184385_6_, p_184385_7_);
    }

    @Override
    public void notifyBlockUpdate(World worldIn, BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        this.markBlocksForUpdate(i - 1, j - 1, k - 1, i + 1, j + 1, k + 1, (flags & 8) != 0);
    }

    @Override
    public void notifyLightSet(BlockPos pos) {
        this.setLightUpdates.add(pos.toImmutable());
    }

    @Override
    public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
        this.markBlocksForUpdate(x1 - 1, y1 - 1, z1 - 1, x2 + 1, y2 + 1, z2 + 1, false);
    }

    @Override
    public void playRecord(@Nullable SoundEvent soundIn, BlockPos pos) {
        ISound isound = this.mapSoundPositions.get(pos);
        if (isound != null) {
            this.mc.getSoundHandler().stopSound(isound);
            this.mapSoundPositions.remove(pos);
        }
        if (soundIn != null) {
            ItemRecord itemrecord = ItemRecord.getBySound(soundIn);
            if (itemrecord != null) {
                this.mc.ingameGUI.setRecordPlayingMessage(itemrecord.getRecordNameLocal());
            }
            PositionedSoundRecord isound1 = PositionedSoundRecord.getRecordSoundRecord(soundIn, pos.getX(), pos.getY(), pos.getZ());
            this.mapSoundPositions.put(pos, isound1);
            this.mc.getSoundHandler().playSound(isound1);
        }
        this.func_193054_a(this.theWorld, pos, soundIn != null);
    }

    private void func_193054_a(World p_193054_1_, BlockPos p_193054_2_, boolean p_193054_3_) {
        for (EntityLivingBase entitylivingbase : p_193054_1_.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(p_193054_2_).expandXyz(3.0))) {
            entitylivingbase.func_191987_a(p_193054_2_, p_193054_3_);
        }
    }

    @Override
    public void playSoundToAllNearExcept(@Nullable EntityPlayer player, SoundEvent soundIn, SoundCategory category, double x, double y, double z, float volume, float pitch) {
    }

    @Override
    public void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int ... parameters) {
        this.func_190570_a(particleID, ignoreRange, false, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
    }

    @Override
    public void func_190570_a(int p_190570_1_, boolean p_190570_2_, boolean p_190570_3_, final double p_190570_4_, final double p_190570_6_, final double p_190570_8_, double p_190570_10_, double p_190570_12_, double p_190570_14_, int ... p_190570_16_) {
        try {
            this.func_190571_b(p_190570_1_, p_190570_2_, p_190570_3_, p_190570_4_, p_190570_6_, p_190570_8_, p_190570_10_, p_190570_12_, p_190570_14_, p_190570_16_);
        }
        catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception while adding particle");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Particle being added");
            crashreportcategory.addCrashSection("ID", p_190570_1_);
            if (p_190570_16_ != null) {
                crashreportcategory.addCrashSection("Parameters", p_190570_16_);
            }
            crashreportcategory.setDetail("Position", new ICrashReportDetail<String>(){

                @Override
                public String call() throws Exception {
                    return CrashReportCategory.getCoordinateInfo(p_190570_4_, p_190570_6_, p_190570_8_);
                }
            });
            throw new ReportedException(crashreport);
        }
    }

    private void spawnParticle(EnumParticleTypes particleIn, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int ... parameters) {
        this.spawnParticle(particleIn.getParticleID(), particleIn.getShouldIgnoreRange(), xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
    }

    @Nullable
    private Particle spawnEntityFX(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int ... parameters) {
        return this.func_190571_b(particleID, ignoreRange, false, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
    }

    @Nullable
    private Particle func_190571_b(int p_190571_1_, boolean p_190571_2_, boolean p_190571_3_, double p_190571_4_, double p_190571_6_, double p_190571_8_, double p_190571_10_, double p_190571_12_, double p_190571_14_, int ... p_190571_16_) {
        Entity entity = this.mc.getRenderViewEntity();
        if (this.mc != null && entity != null && this.mc.effectRenderer != null) {
            int i = this.func_190572_a(p_190571_3_);
            double d0 = entity.posX - p_190571_4_;
            double d1 = entity.posY - p_190571_6_;
            double d2 = entity.posZ - p_190571_8_;
            if (p_190571_1_ == EnumParticleTypes.EXPLOSION_HUGE.getParticleID() && !Config.isAnimatedExplosion()) {
                return null;
            }
            if (p_190571_1_ == EnumParticleTypes.EXPLOSION_LARGE.getParticleID() && !Config.isAnimatedExplosion()) {
                return null;
            }
            if (p_190571_1_ == EnumParticleTypes.EXPLOSION_NORMAL.getParticleID() && !Config.isAnimatedExplosion()) {
                return null;
            }
            if (p_190571_1_ == EnumParticleTypes.SUSPENDED.getParticleID() && !Config.isWaterParticles()) {
                return null;
            }
            if (p_190571_1_ == EnumParticleTypes.SUSPENDED_DEPTH.getParticleID() && !Config.isVoidParticles()) {
                return null;
            }
            if (p_190571_1_ == EnumParticleTypes.SMOKE_NORMAL.getParticleID() && !Config.isAnimatedSmoke()) {
                return null;
            }
            if (p_190571_1_ == EnumParticleTypes.SMOKE_LARGE.getParticleID() && !Config.isAnimatedSmoke()) {
                return null;
            }
            if (p_190571_1_ == EnumParticleTypes.SPELL_MOB.getParticleID() && !Config.isPotionParticles()) {
                return null;
            }
            if (p_190571_1_ == EnumParticleTypes.SPELL_MOB_AMBIENT.getParticleID() && !Config.isPotionParticles()) {
                return null;
            }
            if (p_190571_1_ == EnumParticleTypes.SPELL.getParticleID() && !Config.isPotionParticles()) {
                return null;
            }
            if (p_190571_1_ == EnumParticleTypes.SPELL_INSTANT.getParticleID() && !Config.isPotionParticles()) {
                return null;
            }
            if (p_190571_1_ == EnumParticleTypes.SPELL_WITCH.getParticleID() && !Config.isPotionParticles()) {
                return null;
            }
            if (p_190571_1_ == EnumParticleTypes.PORTAL.getParticleID() && !Config.isAnimatedPortal()) {
                return null;
            }
            if (p_190571_1_ == EnumParticleTypes.FLAME.getParticleID() && !Config.isAnimatedFlame()) {
                return null;
            }
            if (p_190571_1_ == EnumParticleTypes.REDSTONE.getParticleID() && !Config.isAnimatedRedstone()) {
                return null;
            }
            if (p_190571_1_ == EnumParticleTypes.DRIP_WATER.getParticleID() && !Config.isDrippingWaterLava()) {
                return null;
            }
            if (p_190571_1_ == EnumParticleTypes.DRIP_LAVA.getParticleID() && !Config.isDrippingWaterLava()) {
                return null;
            }
            if (p_190571_1_ == EnumParticleTypes.FIREWORKS_SPARK.getParticleID() && !Config.isFireworkParticles()) {
                return null;
            }
            if (!p_190571_2_) {
                double d3 = 1024.0;
                if (p_190571_1_ == EnumParticleTypes.CRIT.getParticleID()) {
                    d3 = 38416.0;
                }
                if (d0 * d0 + d1 * d1 + d2 * d2 > d3) {
                    return null;
                }
                if (i > 1) {
                    return null;
                }
            }
            Particle particle = this.mc.effectRenderer.spawnEffectParticle(p_190571_1_, p_190571_4_, p_190571_6_, p_190571_8_, p_190571_10_, p_190571_12_, p_190571_14_, p_190571_16_);
            if (p_190571_1_ == EnumParticleTypes.WATER_BUBBLE.getParticleID()) {
                CustomColors.updateWaterFX(particle, this.theWorld, p_190571_4_, p_190571_6_, p_190571_8_, this.renderEnv);
            }
            if (p_190571_1_ == EnumParticleTypes.WATER_SPLASH.getParticleID()) {
                CustomColors.updateWaterFX(particle, this.theWorld, p_190571_4_, p_190571_6_, p_190571_8_, this.renderEnv);
            }
            if (p_190571_1_ == EnumParticleTypes.WATER_DROP.getParticleID()) {
                CustomColors.updateWaterFX(particle, this.theWorld, p_190571_4_, p_190571_6_, p_190571_8_, this.renderEnv);
            }
            if (p_190571_1_ == EnumParticleTypes.TOWN_AURA.getParticleID()) {
                CustomColors.updateMyceliumFX(particle);
            }
            if (p_190571_1_ == EnumParticleTypes.PORTAL.getParticleID()) {
                CustomColors.updatePortalFX(particle);
            }
            if (p_190571_1_ == EnumParticleTypes.REDSTONE.getParticleID()) {
                CustomColors.updateReddustFX(particle, this.theWorld, p_190571_4_, p_190571_6_, p_190571_8_);
            }
            return particle;
        }
        return null;
    }

    private int func_190572_a(boolean p_190572_1_) {
        int i = this.mc.gameSettings.particleSetting;
        if (p_190572_1_ && i == 2 && this.theWorld.rand.nextInt(10) == 0) {
            i = 1;
        }
        if (i == 1 && this.theWorld.rand.nextInt(3) == 0) {
            i = 2;
        }
        return i;
    }

    @Override
    public void onEntityAdded(Entity entityIn) {
        RandomMobs.entityLoaded(entityIn, this.theWorld);
        if (Config.isDynamicLights()) {
            DynamicLights.entityAdded(entityIn, this);
        }
    }

    @Override
    public void onEntityRemoved(Entity entityIn) {
        if (Config.isDynamicLights()) {
            DynamicLights.entityRemoved(entityIn, this);
        }
    }

    public void deleteAllDisplayLists() {
    }

    @Override
    public void broadcastSound(int soundID, BlockPos pos, int data) {
        switch (soundID) {
            case 1023: 
            case 1028: 
            case 1038: {
                Entity entity = this.mc.getRenderViewEntity();
                if (entity == null) break;
                double d0 = (double)pos.getX() - entity.posX;
                double d1 = (double)pos.getY() - entity.posY;
                double d2 = (double)pos.getZ() - entity.posZ;
                double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                double d4 = entity.posX;
                double d5 = entity.posY;
                double d6 = entity.posZ;
                if (d3 > 0.0) {
                    d4 += d0 / d3 * 2.0;
                    d5 += d1 / d3 * 2.0;
                    d6 += d2 / d3 * 2.0;
                }
                if (soundID == 1023) {
                    this.theWorld.playSound(d4, d5, d6, SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.HOSTILE, 1.0f, 1.0f, false);
                    break;
                }
                if (soundID == 1038) {
                    this.theWorld.playSound(d4, d5, d6, SoundEvents.field_193782_bq, SoundCategory.HOSTILE, 1.0f, 1.0f, false);
                    break;
                }
                this.theWorld.playSound(d4, d5, d6, SoundEvents.ENTITY_ENDERDRAGON_DEATH, SoundCategory.HOSTILE, 5.0f, 1.0f, false);
            }
        }
    }

    @Override
    public void playEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data) {
        Random random = this.theWorld.rand;
        switch (type) {
            case 1000: {
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_DISPENSER_DISPENSE, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1001: {
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_DISPENSER_FAIL, SoundCategory.BLOCKS, 1.0f, 1.2f, false);
                break;
            }
            case 1002: {
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_DISPENSER_LAUNCH, SoundCategory.BLOCKS, 1.0f, 1.2f, false);
                break;
            }
            case 1003: {
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_ENDEREYE_LAUNCH, SoundCategory.NEUTRAL, 1.0f, 1.2f, false);
                break;
            }
            case 1004: {
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_FIREWORK_SHOOT, SoundCategory.NEUTRAL, 1.0f, 1.2f, false);
                break;
            }
            case 1005: {
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_IRON_DOOR_OPEN, SoundCategory.BLOCKS, 1.0f, this.theWorld.rand.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1006: {
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_WOODEN_DOOR_OPEN, SoundCategory.BLOCKS, 1.0f, this.theWorld.rand.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1007: {
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_WOODEN_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1.0f, this.theWorld.rand.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1008: {
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_FENCE_GATE_OPEN, SoundCategory.BLOCKS, 1.0f, this.theWorld.rand.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1009: {
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5f, 2.6f + (random.nextFloat() - random.nextFloat()) * 0.8f, false);
                break;
            }
            case 1010: {
                if (Item.getItemById(data) instanceof ItemRecord) {
                    this.theWorld.playRecord(blockPosIn, ((ItemRecord)Item.getItemById(data)).getSound());
                    break;
                }
                this.theWorld.playRecord(blockPosIn, null);
                break;
            }
            case 1011: {
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundCategory.BLOCKS, 1.0f, this.theWorld.rand.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1012: {
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_WOODEN_DOOR_CLOSE, SoundCategory.BLOCKS, 1.0f, this.theWorld.rand.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1013: {
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_WOODEN_TRAPDOOR_CLOSE, SoundCategory.BLOCKS, 1.0f, this.theWorld.rand.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1014: {
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_FENCE_GATE_CLOSE, SoundCategory.BLOCKS, 1.0f, this.theWorld.rand.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1015: {
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_GHAST_WARN, SoundCategory.HOSTILE, 10.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1016: {
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.HOSTILE, 10.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1017: {
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_ENDERDRAGON_SHOOT, SoundCategory.HOSTILE, 10.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1018: {
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1019: {
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_ZOMBIE_ATTACK_DOOR_WOOD, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1020: {
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1021: {
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_ZOMBIE_BREAK_DOOR_WOOD, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1022: {
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_WITHER_BREAK_BLOCK, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1024: {
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_WITHER_SHOOT, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1025: {
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_BAT_TAKEOFF, SoundCategory.NEUTRAL, 0.05f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1026: {
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_ZOMBIE_INFECT, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1027: {
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CONVERTED, SoundCategory.NEUTRAL, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1029: {
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_ANVIL_DESTROY, SoundCategory.BLOCKS, 1.0f, this.theWorld.rand.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1030: {
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 1.0f, this.theWorld.rand.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1031: {
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.3f, this.theWorld.rand.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1032: {
                this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_PORTAL_TRAVEL, random.nextFloat() * 0.4f + 0.8f));
                break;
            }
            case 1033: {
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_CHORUS_FLOWER_GROW, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1034: {
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_CHORUS_FLOWER_DEATH, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1035: {
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1036: {
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundCategory.BLOCKS, 1.0f, this.theWorld.rand.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1037: {
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1.0f, this.theWorld.rand.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 2000: {
                int i = data % 3 - 1;
                int j = data / 3 % 3 - 1;
                double d0 = (double)blockPosIn.getX() + (double)i * 0.6 + 0.5;
                double d1 = (double)blockPosIn.getY() + 0.5;
                double d2 = (double)blockPosIn.getZ() + (double)j * 0.6 + 0.5;
                int i1 = 0;
                while (i1 < 10) {
                    double d15 = random.nextDouble() * 0.2 + 0.01;
                    double d16 = d0 + (double)i * 0.01 + (random.nextDouble() - 0.5) * (double)j * 0.5;
                    double d17 = d1 + (random.nextDouble() - 0.5) * 0.5;
                    double d18 = d2 + (double)j * 0.01 + (random.nextDouble() - 0.5) * (double)i * 0.5;
                    double d19 = (double)i * d15 + random.nextGaussian() * 0.01;
                    double d20 = -0.03 + random.nextGaussian() * 0.01;
                    double d21 = (double)j * d15 + random.nextGaussian() * 0.01;
                    this.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d16, d17, d18, d19, d20, d21, new int[0]);
                    ++i1;
                }
                return;
            }
            case 2001: {
                Block block = Block.getBlockById(data & 0xFFF);
                if (block.getDefaultState().getMaterial() != Material.AIR) {
                    SoundType soundtype = block.getSoundType();
                    if (Reflector.ForgeBlock_getSoundType.exists()) {
                        soundtype = (SoundType)Reflector.call(block, Reflector.ForgeBlock_getSoundType, Block.getStateById(data), this.theWorld, blockPosIn, null);
                    }
                    this.theWorld.playSound(blockPosIn, soundtype.getBreakSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0f) / 2.0f, soundtype.getPitch() * 0.8f, false);
                }
                this.mc.effectRenderer.addBlockDestroyEffects(blockPosIn, block.getStateFromMeta(data >> 12 & 0xFF));
                break;
            }
            case 2002: 
            case 2007: {
                double d3 = blockPosIn.getX();
                double d4 = blockPosIn.getY();
                double d5 = blockPosIn.getZ();
                int k = 0;
                while (k < 8) {
                    this.spawnParticle(EnumParticleTypes.ITEM_CRACK, d3, d4, d5, random.nextGaussian() * 0.15, random.nextDouble() * 0.2, random.nextGaussian() * 0.15, Item.getIdFromItem(Items.SPLASH_POTION));
                    ++k;
                }
                float f5 = (float)(data >> 16 & 0xFF) / 255.0f;
                float f = (float)(data >> 8 & 0xFF) / 255.0f;
                float f1 = (float)(data >> 0 & 0xFF) / 255.0f;
                EnumParticleTypes enumparticletypes = type == 2007 ? EnumParticleTypes.SPELL_INSTANT : EnumParticleTypes.SPELL;
                int j1 = 0;
                while (j1 < 100) {
                    double d7 = random.nextDouble() * 4.0;
                    double d9 = random.nextDouble() * Math.PI * 2.0;
                    double d11 = Math.cos(d9) * d7;
                    double d24 = 0.01 + random.nextDouble() * 0.5;
                    double d26 = Math.sin(d9) * d7;
                    Particle particle1 = this.spawnEntityFX(enumparticletypes.getParticleID(), enumparticletypes.getShouldIgnoreRange(), d3 + d11 * 0.1, d4 + 0.3, d5 + d26 * 0.1, d11, d24, d26, new int[0]);
                    if (particle1 != null) {
                        float f4 = 0.75f + random.nextFloat() * 0.25f;
                        particle1.setRBGColorF(f5 * f4, f * f4, f1 * f4);
                        particle1.multiplyVelocity((float)d7);
                    }
                    ++j1;
                }
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_SPLASH_POTION_BREAK, SoundCategory.NEUTRAL, 1.0f, this.theWorld.rand.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 2003: {
                double d6 = (double)blockPosIn.getX() + 0.5;
                double d8 = blockPosIn.getY();
                double d10 = (double)blockPosIn.getZ() + 0.5;
                int l1 = 0;
                while (l1 < 8) {
                    this.spawnParticle(EnumParticleTypes.ITEM_CRACK, d6, d8, d10, random.nextGaussian() * 0.15, random.nextDouble() * 0.2, random.nextGaussian() * 0.15, Item.getIdFromItem(Items.ENDER_EYE));
                    ++l1;
                }
                double d22 = 0.0;
                while (d22 < Math.PI * 2) {
                    this.spawnParticle(EnumParticleTypes.PORTAL, d6 + Math.cos(d22) * 5.0, d8 - 0.4, d10 + Math.sin(d22) * 5.0, Math.cos(d22) * -5.0, 0.0, Math.sin(d22) * -5.0, new int[0]);
                    this.spawnParticle(EnumParticleTypes.PORTAL, d6 + Math.cos(d22) * 5.0, d8 - 0.4, d10 + Math.sin(d22) * 5.0, Math.cos(d22) * -7.0, 0.0, Math.sin(d22) * -7.0, new int[0]);
                    d22 += 0.15707963267948966;
                }
                return;
            }
            case 2004: {
                int k1 = 0;
                while (k1 < 20) {
                    double d23 = (double)blockPosIn.getX() + 0.5 + ((double)this.theWorld.rand.nextFloat() - 0.5) * 2.0;
                    double d25 = (double)blockPosIn.getY() + 0.5 + ((double)this.theWorld.rand.nextFloat() - 0.5) * 2.0;
                    double d27 = (double)blockPosIn.getZ() + 0.5 + ((double)this.theWorld.rand.nextFloat() - 0.5) * 2.0;
                    this.theWorld.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d23, d25, d27, 0.0, 0.0, 0.0, new int[0]);
                    this.theWorld.spawnParticle(EnumParticleTypes.FLAME, d23, d25, d27, 0.0, 0.0, 0.0, new int[0]);
                    ++k1;
                }
                return;
            }
            case 2005: {
                ItemDye.spawnBonemealParticles(this.theWorld, blockPosIn, data);
                break;
            }
            case 2006: {
                int l = 0;
                while (l < 200) {
                    float f2 = random.nextFloat() * 4.0f;
                    float f3 = random.nextFloat() * ((float)Math.PI * 2);
                    double d12 = MathHelper.cos(f3) * f2;
                    double d13 = 0.01 + random.nextDouble() * 0.5;
                    double d14 = MathHelper.sin(f3) * f2;
                    Particle particle = this.spawnEntityFX(EnumParticleTypes.DRAGON_BREATH.getParticleID(), false, (double)blockPosIn.getX() + d12 * 0.1, (double)blockPosIn.getY() + 0.3, (double)blockPosIn.getZ() + d14 * 0.1, d12, d13, d14, new int[0]);
                    if (particle != null) {
                        particle.multiplyVelocity(f2);
                    }
                    ++l;
                }
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_ENDERDRAGON_FIREBALL_EPLD, SoundCategory.HOSTILE, 1.0f, this.theWorld.rand.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 3000: {
                this.theWorld.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, true, (double)blockPosIn.getX() + 0.5, (double)blockPosIn.getY() + 0.5, (double)blockPosIn.getZ() + 0.5, 0.0, 0.0, 0.0, new int[0]);
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_END_GATEWAY_SPAWN, SoundCategory.BLOCKS, 10.0f, (1.0f + (this.theWorld.rand.nextFloat() - this.theWorld.rand.nextFloat()) * 0.2f) * 0.7f, false);
                break;
            }
            case 3001: {
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_ENDERDRAGON_GROWL, SoundCategory.HOSTILE, 64.0f, 0.8f + this.theWorld.rand.nextFloat() * 0.3f, false);
            }
        }
    }

    @Override
    public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
        if (progress >= 0 && progress < 10) {
            DestroyBlockProgress destroyblockprogress = this.damagedBlocks.get(breakerId);
            if (destroyblockprogress == null || destroyblockprogress.getPosition().getX() != pos.getX() || destroyblockprogress.getPosition().getY() != pos.getY() || destroyblockprogress.getPosition().getZ() != pos.getZ()) {
                destroyblockprogress = new DestroyBlockProgress(breakerId, pos);
                this.damagedBlocks.put(breakerId, destroyblockprogress);
            }
            destroyblockprogress.setPartialBlockDamage(progress);
            destroyblockprogress.setCloudUpdateTick(this.cloudTickCounter);
        } else {
            this.damagedBlocks.remove(breakerId);
        }
    }

    public boolean hasNoChunkUpdates() {
        return this.chunksToUpdate.isEmpty() && this.renderDispatcher.hasChunkUpdates();
    }

    public void setDisplayListEntitiesDirty() {
        this.displayListEntitiesDirty = true;
    }

    public void resetClouds() {
        this.cloudRenderer.reset();
    }

    public int getCountRenderers() {
        return this.viewFrustum.renderChunks.length;
    }

    public int getCountActiveRenderers() {
        return this.renderInfos.size();
    }

    public int getCountEntitiesRendered() {
        return this.countEntitiesRendered;
    }

    public int getCountTileEntitiesRendered() {
        return this.countTileEntitiesRendered;
    }

    public int getCountLoadedChunks() {
        if (this.theWorld == null) {
            return 0;
        }
        ChunkProviderClient ichunkprovider = this.theWorld.getChunkProvider();
        if (ichunkprovider == null) {
            return 0;
        }
        if (ichunkprovider != this.worldChunkProvider) {
            this.worldChunkProvider = ichunkprovider;
            this.worldChunkProviderMap = (Long2ObjectMap)Reflector.getFieldValue(ichunkprovider, Reflector.ChunkProviderClient_chunkMapping);
        }
        return this.worldChunkProviderMap == null ? 0 : this.worldChunkProviderMap.size();
    }

    public int getCountChunksToUpdate() {
        return this.chunksToUpdate.size();
    }

    public RenderChunk getRenderChunk(BlockPos p_getRenderChunk_1_) {
        return this.viewFrustum.getRenderChunk(p_getRenderChunk_1_);
    }

    public WorldClient getWorld() {
        return this.theWorld;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateTileEntities(Collection<TileEntity> tileEntitiesToRemove, Collection<TileEntity> tileEntitiesToAdd) {
        Set<TileEntity> set = this.setTileEntities;
        synchronized (set) {
            this.setTileEntities.removeAll(tileEntitiesToRemove);
            this.setTileEntities.addAll(tileEntitiesToAdd);
        }
    }

    private ContainerLocalRenderInformation allocateRenderInformation(RenderChunk p_allocateRenderInformation_1_, EnumFacing p_allocateRenderInformation_2_, int p_allocateRenderInformation_3_) {
        ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation;
        if (renderInfoCache.isEmpty()) {
            renderglobal$containerlocalrenderinformation = new ContainerLocalRenderInformation(p_allocateRenderInformation_1_, p_allocateRenderInformation_2_, p_allocateRenderInformation_3_);
        } else {
            renderglobal$containerlocalrenderinformation = renderInfoCache.pollLast();
            renderglobal$containerlocalrenderinformation.initialize(p_allocateRenderInformation_1_, p_allocateRenderInformation_2_, p_allocateRenderInformation_3_);
        }
        renderglobal$containerlocalrenderinformation.cacheable = true;
        return renderglobal$containerlocalrenderinformation;
    }

    private void freeRenderInformation(ContainerLocalRenderInformation p_freeRenderInformation_1_) {
        if (p_freeRenderInformation_1_.cacheable) {
            renderInfoCache.add(p_freeRenderInformation_1_);
        }
    }

    public static class ContainerLocalRenderInformation {
        RenderChunk renderChunk;
        EnumFacing facing;
        int setFacing;
        boolean cacheable = false;

        public ContainerLocalRenderInformation(RenderChunk p_i3_1_, EnumFacing p_i3_2_, int p_i3_3_) {
            this.renderChunk = p_i3_1_;
            this.facing = p_i3_2_;
            this.setFacing = p_i3_3_;
        }

        public void setDirection(byte p_189561_1_, EnumFacing p_189561_2_) {
            this.setFacing = this.setFacing | p_189561_1_ | 1 << p_189561_2_.ordinal();
        }

        public boolean hasDirection(EnumFacing p_189560_1_) {
            return (this.setFacing & 1 << p_189560_1_.ordinal()) > 0;
        }

        private void initialize(RenderChunk p_initialize_1_, EnumFacing p_initialize_2_, int p_initialize_3_) {
            this.renderChunk = p_initialize_1_;
            this.facing = p_initialize_2_;
            this.setFacing = p_initialize_3_;
        }
    }
}

