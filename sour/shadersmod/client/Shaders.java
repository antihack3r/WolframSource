/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.IOUtils
 *  org.lwjgl.BufferUtils
 *  org.lwjgl.opengl.ARBShaderObjects
 *  org.lwjgl.opengl.ARBVertexShader
 *  org.lwjgl.opengl.ContextCapabilities
 *  org.lwjgl.opengl.EXTFramebufferObject
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL20
 *  org.lwjgl.opengl.GL30
 *  org.lwjgl.opengl.GLContext
 *  org.lwjgl.util.glu.GLU
 */
package shadersmod.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.src.Config;
import net.minecraft.src.CustomColors;
import net.minecraft.src.EntityUtils;
import net.minecraft.src.Lang;
import net.minecraft.src.PropertiesOrdered;
import net.minecraft.src.Reflector;
import net.minecraft.src.StrUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.GLU;
import shadersmod.client.BlockAliases;
import shadersmod.client.CustomTexture;
import shadersmod.client.EnumShaderOption;
import shadersmod.client.HFNoiseTexture;
import shadersmod.client.IShaderPack;
import shadersmod.client.PropertyDefaultFastFancyOff;
import shadersmod.client.PropertyDefaultTrueFalse;
import shadersmod.client.SMath;
import shadersmod.client.ShaderOption;
import shadersmod.client.ShaderOptionProfile;
import shadersmod.client.ShaderOptionRest;
import shadersmod.client.ShaderPackDefault;
import shadersmod.client.ShaderPackFolder;
import shadersmod.client.ShaderPackNone;
import shadersmod.client.ShaderPackParser;
import shadersmod.client.ShaderPackZip;
import shadersmod.client.ShaderProfile;
import shadersmod.client.ShaderUniformFloat4;
import shadersmod.client.ShaderUniformInt;
import shadersmod.client.ShaderUtils;
import shadersmod.client.ShadersRender;
import shadersmod.client.ShadersTex;
import shadersmod.client.SimpleShaderTexture;
import shadersmod.common.SMCLog;

public class Shaders {
    static Minecraft mc = Minecraft.getMinecraft();
    static EntityRenderer entityRenderer;
    public static boolean isInitializedOnce;
    public static boolean isShaderPackInitialized;
    public static ContextCapabilities capabilities;
    public static String glVersionString;
    public static String glVendorString;
    public static String glRendererString;
    public static boolean hasGlGenMipmap;
    public static boolean hasForge;
    public static int numberResetDisplayList;
    static boolean needResetModels;
    private static int renderDisplayWidth;
    private static int renderDisplayHeight;
    public static int renderWidth;
    public static int renderHeight;
    public static boolean isRenderingWorld;
    public static boolean isRenderingSky;
    public static boolean isCompositeRendered;
    public static boolean isRenderingDfb;
    public static boolean isShadowPass;
    public static boolean isSleeping;
    private static boolean isRenderingFirstPersonHand;
    private static boolean isHandRenderedMain;
    private static boolean isHandRenderedOff;
    private static boolean skipRenderHandMain;
    private static boolean skipRenderHandOff;
    public static boolean renderItemKeepDepthMask;
    public static boolean itemToRenderMainTranslucent;
    public static boolean itemToRenderOffTranslucent;
    static float[] sunPosition;
    static float[] moonPosition;
    static float[] shadowLightPosition;
    static float[] upPosition;
    static float[] shadowLightPositionVector;
    static float[] upPosModelView;
    static float[] sunPosModelView;
    static float[] moonPosModelView;
    private static float[] tempMat;
    static float clearColorR;
    static float clearColorG;
    static float clearColorB;
    static float skyColorR;
    static float skyColorG;
    static float skyColorB;
    static long worldTime;
    static long lastWorldTime;
    static long diffWorldTime;
    static float celestialAngle;
    static float sunAngle;
    static float shadowAngle;
    static int moonPhase;
    static long systemTime;
    static long lastSystemTime;
    static long diffSystemTime;
    static int frameCounter;
    static float frameTime;
    static float frameTimeCounter;
    static int systemTimeInt32;
    static float rainStrength;
    static float wetness;
    public static float wetnessHalfLife;
    public static float drynessHalfLife;
    public static float eyeBrightnessHalflife;
    static boolean usewetness;
    static int isEyeInWater;
    static int eyeBrightness;
    static float eyeBrightnessFadeX;
    static float eyeBrightnessFadeY;
    static float eyePosY;
    static float centerDepth;
    static float centerDepthSmooth;
    static float centerDepthSmoothHalflife;
    static boolean centerDepthSmoothEnabled;
    static int superSamplingLevel;
    static float nightVision;
    static float blindness;
    static boolean updateChunksErrorRecorded;
    static boolean lightmapEnabled;
    static boolean fogEnabled;
    public static int entityAttrib;
    public static int midTexCoordAttrib;
    public static int tangentAttrib;
    public static boolean useEntityAttrib;
    public static boolean useMidTexCoordAttrib;
    public static boolean useMultiTexCoord3Attrib;
    public static boolean useTangentAttrib;
    public static boolean progUseEntityAttrib;
    public static boolean progUseMidTexCoordAttrib;
    public static boolean progUseTangentAttrib;
    public static int atlasSizeX;
    public static int atlasSizeY;
    public static ShaderUniformFloat4 uniformEntityColor;
    public static ShaderUniformInt uniformEntityId;
    public static ShaderUniformInt uniformBlockEntityId;
    static double previousCameraPositionX;
    static double previousCameraPositionY;
    static double previousCameraPositionZ;
    static double cameraPositionX;
    static double cameraPositionY;
    static double cameraPositionZ;
    static int shadowPassInterval;
    public static boolean needResizeShadow;
    static int shadowMapWidth;
    static int shadowMapHeight;
    static int spShadowMapWidth;
    static int spShadowMapHeight;
    static float shadowMapFOV;
    static float shadowMapHalfPlane;
    static boolean shadowMapIsOrtho;
    static float shadowDistanceRenderMul;
    static int shadowPassCounter;
    static int preShadowPassThirdPersonView;
    public static boolean shouldSkipDefaultShadow;
    static boolean waterShadowEnabled;
    static final int MaxDrawBuffers = 8;
    static final int MaxColorBuffers = 8;
    static final int MaxDepthBuffers = 3;
    static final int MaxShadowColorBuffers = 8;
    static final int MaxShadowDepthBuffers = 2;
    static int usedColorBuffers;
    static int usedDepthBuffers;
    static int usedShadowColorBuffers;
    static int usedShadowDepthBuffers;
    static int usedColorAttachs;
    static int usedDrawBuffers;
    static int dfb;
    static int sfb;
    private static int[] gbuffersFormat;
    private static boolean[] gbuffersClear;
    public static int activeProgram;
    public static final int ProgramNone = 0;
    public static final int ProgramBasic = 1;
    public static final int ProgramTextured = 2;
    public static final int ProgramTexturedLit = 3;
    public static final int ProgramSkyBasic = 4;
    public static final int ProgramSkyTextured = 5;
    public static final int ProgramClouds = 6;
    public static final int ProgramTerrain = 7;
    public static final int ProgramTerrainSolid = 8;
    public static final int ProgramTerrainCutoutMip = 9;
    public static final int ProgramTerrainCutout = 10;
    public static final int ProgramDamagedBlock = 11;
    public static final int ProgramWater = 12;
    public static final int ProgramBlock = 13;
    public static final int ProgramBeaconBeam = 14;
    public static final int ProgramItem = 15;
    public static final int ProgramEntities = 16;
    public static final int ProgramArmorGlint = 17;
    public static final int ProgramSpiderEyes = 18;
    public static final int ProgramHand = 19;
    public static final int ProgramWeather = 20;
    public static final int ProgramComposite = 21;
    public static final int ProgramComposite1 = 22;
    public static final int ProgramComposite2 = 23;
    public static final int ProgramComposite3 = 24;
    public static final int ProgramComposite4 = 25;
    public static final int ProgramComposite5 = 26;
    public static final int ProgramComposite6 = 27;
    public static final int ProgramComposite7 = 28;
    public static final int ProgramFinal = 29;
    public static final int ProgramShadow = 30;
    public static final int ProgramShadowSolid = 31;
    public static final int ProgramShadowCutout = 32;
    public static final int ProgramCount = 33;
    public static final int MaxCompositePasses = 8;
    private static final String[] programNames;
    private static final int[] programBackups;
    static int[] programsID;
    private static int[] programsRef;
    private static int programIDCopyDepth;
    private static String[] programsDrawBufSettings;
    private static String newDrawBufSetting;
    static IntBuffer[] programsDrawBuffers;
    static IntBuffer activeDrawBuffers;
    private static String[] programsColorAtmSettings;
    private static String newColorAtmSetting;
    private static String activeColorAtmSettings;
    private static int[] programsCompositeMipmapSetting;
    private static int newCompositeMipmapSetting;
    private static int activeCompositeMipmapSetting;
    public static Properties loadedShaders;
    public static Properties shadersConfig;
    public static ITextureObject defaultTexture;
    public static boolean normalMapEnabled;
    public static boolean[] shadowHardwareFilteringEnabled;
    public static boolean[] shadowMipmapEnabled;
    public static boolean[] shadowFilterNearest;
    public static boolean[] shadowColorMipmapEnabled;
    public static boolean[] shadowColorFilterNearest;
    public static boolean configTweakBlockDamage;
    public static boolean configCloudShadow;
    public static float configHandDepthMul;
    public static float configRenderResMul;
    public static float configShadowResMul;
    public static int configTexMinFilB;
    public static int configTexMinFilN;
    public static int configTexMinFilS;
    public static int configTexMagFilB;
    public static int configTexMagFilN;
    public static int configTexMagFilS;
    public static boolean configShadowClipFrustrum;
    public static boolean configNormalMap;
    public static boolean configSpecularMap;
    public static PropertyDefaultTrueFalse configOldLighting;
    public static PropertyDefaultTrueFalse configOldHandLight;
    public static int configAntialiasingLevel;
    public static final int texMinFilRange = 3;
    public static final int texMagFilRange = 2;
    public static final String[] texMinFilDesc;
    public static final String[] texMagFilDesc;
    public static final int[] texMinFilValue;
    public static final int[] texMagFilValue;
    static IShaderPack shaderPack;
    public static boolean shaderPackLoaded;
    static File currentshader;
    static String currentshadername;
    public static String packNameNone;
    static String packNameDefault;
    static String shaderpacksdirname;
    static String optionsfilename;
    static File shadersdir;
    static File shaderpacksdir;
    static File configFile;
    static ShaderOption[] shaderPackOptions;
    static Set<String> shaderPackOptionSliders;
    static ShaderProfile[] shaderPackProfiles;
    static Map<String, ShaderOption[]> shaderPackGuiScreens;
    public static PropertyDefaultFastFancyOff shaderPackClouds;
    public static PropertyDefaultTrueFalse shaderPackOldLighting;
    public static PropertyDefaultTrueFalse shaderPackOldHandLight;
    public static PropertyDefaultTrueFalse shaderPackDynamicHandLight;
    public static PropertyDefaultTrueFalse shaderPackShadowTranslucent;
    public static PropertyDefaultTrueFalse shaderPackUnderwaterOverlay;
    public static PropertyDefaultTrueFalse shaderPackSun;
    public static PropertyDefaultTrueFalse shaderPackMoon;
    public static PropertyDefaultTrueFalse shaderPackVignette;
    public static PropertyDefaultTrueFalse shaderPackBackFaceSolid;
    public static PropertyDefaultTrueFalse shaderPackBackFaceCutout;
    public static PropertyDefaultTrueFalse shaderPackBackFaceCutoutMipped;
    public static PropertyDefaultTrueFalse shaderPackBackFaceTranslucent;
    private static Map<String, String> shaderPackResources;
    private static World currentWorld;
    private static List<Integer> shaderPackDimensions;
    private static CustomTexture[] customTexturesGbuffers;
    private static CustomTexture[] customTexturesComposite;
    private static final int STAGE_GBUFFERS = 0;
    private static final int STAGE_COMPOSITE = 1;
    private static final String[] STAGE_NAMES;
    public static final boolean enableShadersOption = true;
    private static final boolean enableShadersDebug = true;
    private static final boolean saveFinalShaders;
    public static float blockLightLevel05;
    public static float blockLightLevel06;
    public static float blockLightLevel08;
    public static float aoLevel;
    public static float sunPathRotation;
    public static float shadowAngleInterval;
    public static int fogMode;
    public static float fogColorR;
    public static float fogColorG;
    public static float fogColorB;
    public static float shadowIntervalSize;
    public static int terrainIconSize;
    public static int[] terrainTextureSize;
    private static HFNoiseTexture noiseTexture;
    private static boolean noiseTextureEnabled;
    private static int noiseTextureResolution;
    static final int[] dfbColorTexturesA;
    static final int[] colorTexturesToggle;
    static final int[] colorTextureTextureImageUnit;
    static final boolean[][] programsToggleColorTextures;
    private static final int bigBufferSize = 2196;
    private static final ByteBuffer bigBuffer;
    static final float[] faProjection;
    static final float[] faProjectionInverse;
    static final float[] faModelView;
    static final float[] faModelViewInverse;
    static final float[] faShadowProjection;
    static final float[] faShadowProjectionInverse;
    static final float[] faShadowModelView;
    static final float[] faShadowModelViewInverse;
    static final FloatBuffer projection;
    static final FloatBuffer projectionInverse;
    static final FloatBuffer modelView;
    static final FloatBuffer modelViewInverse;
    static final FloatBuffer shadowProjection;
    static final FloatBuffer shadowProjectionInverse;
    static final FloatBuffer shadowModelView;
    static final FloatBuffer shadowModelViewInverse;
    static final FloatBuffer previousProjection;
    static final FloatBuffer previousModelView;
    static final FloatBuffer tempMatrixDirectBuffer;
    static final FloatBuffer tempDirectFloatBuffer;
    static final IntBuffer dfbColorTextures;
    static final IntBuffer dfbDepthTextures;
    static final IntBuffer sfbColorTextures;
    static final IntBuffer sfbDepthTextures;
    static final IntBuffer dfbDrawBuffers;
    static final IntBuffer sfbDrawBuffers;
    static final IntBuffer drawBuffersNone;
    static final IntBuffer drawBuffersAll;
    static final IntBuffer drawBuffersClear0;
    static final IntBuffer drawBuffersClear1;
    static final IntBuffer drawBuffersClearColor;
    static final IntBuffer drawBuffersColorAtt0;
    static final IntBuffer[] drawBuffersBuffer;
    static Map<Block, Integer> mapBlockToEntityData;
    private static final Pattern gbufferFormatPattern;
    private static final Pattern gbufferClearPattern;
    private static final Pattern gbufferMipmapEnabledPattern;
    private static final String[] formatNames;
    private static final int[] formatIds;
    private static final Pattern patternLoadEntityDataMap;
    public static int[] entityData;
    public static int entityDataIndex;

    static {
        isInitializedOnce = false;
        isShaderPackInitialized = false;
        hasGlGenMipmap = false;
        hasForge = false;
        numberResetDisplayList = 0;
        needResetModels = false;
        renderDisplayWidth = 0;
        renderDisplayHeight = 0;
        renderWidth = 0;
        renderHeight = 0;
        isRenderingWorld = false;
        isRenderingSky = false;
        isCompositeRendered = false;
        isRenderingDfb = false;
        isShadowPass = false;
        renderItemKeepDepthMask = false;
        itemToRenderMainTranslucent = false;
        itemToRenderOffTranslucent = false;
        sunPosition = new float[4];
        moonPosition = new float[4];
        shadowLightPosition = new float[4];
        upPosition = new float[4];
        shadowLightPositionVector = new float[4];
        upPosModelView = new float[]{0.0f, 100.0f, 0.0f, 0.0f};
        sunPosModelView = new float[]{0.0f, 100.0f, 0.0f, 0.0f};
        moonPosModelView = new float[]{0.0f, -100.0f, 0.0f, 0.0f};
        tempMat = new float[16];
        worldTime = 0L;
        lastWorldTime = 0L;
        diffWorldTime = 0L;
        celestialAngle = 0.0f;
        sunAngle = 0.0f;
        shadowAngle = 0.0f;
        moonPhase = 0;
        systemTime = 0L;
        lastSystemTime = 0L;
        diffSystemTime = 0L;
        frameCounter = 0;
        frameTime = 0.0f;
        frameTimeCounter = 0.0f;
        systemTimeInt32 = 0;
        rainStrength = 0.0f;
        wetness = 0.0f;
        wetnessHalfLife = 600.0f;
        drynessHalfLife = 200.0f;
        eyeBrightnessHalflife = 10.0f;
        usewetness = false;
        isEyeInWater = 0;
        eyeBrightness = 0;
        eyeBrightnessFadeX = 0.0f;
        eyeBrightnessFadeY = 0.0f;
        eyePosY = 0.0f;
        centerDepth = 0.0f;
        centerDepthSmooth = 0.0f;
        centerDepthSmoothHalflife = 1.0f;
        centerDepthSmoothEnabled = false;
        superSamplingLevel = 1;
        nightVision = 0.0f;
        blindness = 0.0f;
        updateChunksErrorRecorded = false;
        lightmapEnabled = false;
        fogEnabled = true;
        entityAttrib = 10;
        midTexCoordAttrib = 11;
        tangentAttrib = 12;
        useEntityAttrib = false;
        useMidTexCoordAttrib = false;
        useMultiTexCoord3Attrib = false;
        useTangentAttrib = false;
        progUseEntityAttrib = false;
        progUseMidTexCoordAttrib = false;
        progUseTangentAttrib = false;
        atlasSizeX = 0;
        atlasSizeY = 0;
        uniformEntityColor = new ShaderUniformFloat4("entityColor");
        uniformEntityId = new ShaderUniformInt("entityId");
        uniformBlockEntityId = new ShaderUniformInt("blockEntityId");
        shadowPassInterval = 0;
        needResizeShadow = false;
        shadowMapWidth = 1024;
        shadowMapHeight = 1024;
        spShadowMapWidth = 1024;
        spShadowMapHeight = 1024;
        shadowMapFOV = 90.0f;
        shadowMapHalfPlane = 160.0f;
        shadowMapIsOrtho = true;
        shadowDistanceRenderMul = -1.0f;
        shadowPassCounter = 0;
        shouldSkipDefaultShadow = false;
        waterShadowEnabled = false;
        usedColorBuffers = 0;
        usedDepthBuffers = 0;
        usedShadowColorBuffers = 0;
        usedShadowDepthBuffers = 0;
        usedColorAttachs = 0;
        usedDrawBuffers = 0;
        dfb = 0;
        sfb = 0;
        gbuffersFormat = new int[8];
        gbuffersClear = new boolean[8];
        activeProgram = 0;
        programNames = new String[]{"", "gbuffers_basic", "gbuffers_textured", "gbuffers_textured_lit", "gbuffers_skybasic", "gbuffers_skytextured", "gbuffers_clouds", "gbuffers_terrain", "gbuffers_terrain_solid", "gbuffers_terrain_cutout_mip", "gbuffers_terrain_cutout", "gbuffers_damagedblock", "gbuffers_water", "gbuffers_block", "gbuffers_beaconbeam", "gbuffers_item", "gbuffers_entities", "gbuffers_armor_glint", "gbuffers_spidereyes", "gbuffers_hand", "gbuffers_weather", "composite", "composite1", "composite2", "composite3", "composite4", "composite5", "composite6", "composite7", "final", "shadow", "shadow_solid", "shadow_cutout"};
        int[] nArray = new int[33];
        nArray[2] = 1;
        nArray[3] = 2;
        nArray[4] = 1;
        nArray[5] = 2;
        nArray[6] = 2;
        nArray[7] = 3;
        nArray[8] = 7;
        nArray[9] = 7;
        nArray[10] = 7;
        nArray[11] = 7;
        nArray[12] = 7;
        nArray[13] = 7;
        nArray[14] = 2;
        nArray[15] = 3;
        nArray[16] = 3;
        nArray[17] = 2;
        nArray[18] = 2;
        nArray[19] = 3;
        nArray[20] = 3;
        nArray[31] = 30;
        nArray[32] = 30;
        programBackups = nArray;
        programsID = new int[33];
        programsRef = new int[33];
        programIDCopyDepth = 0;
        programsDrawBufSettings = new String[33];
        newDrawBufSetting = null;
        programsDrawBuffers = new IntBuffer[33];
        activeDrawBuffers = null;
        programsColorAtmSettings = new String[33];
        newColorAtmSetting = null;
        activeColorAtmSettings = null;
        programsCompositeMipmapSetting = new int[33];
        newCompositeMipmapSetting = 0;
        activeCompositeMipmapSetting = 0;
        loadedShaders = null;
        shadersConfig = null;
        defaultTexture = null;
        normalMapEnabled = false;
        shadowHardwareFilteringEnabled = new boolean[2];
        shadowMipmapEnabled = new boolean[2];
        shadowFilterNearest = new boolean[2];
        shadowColorMipmapEnabled = new boolean[8];
        shadowColorFilterNearest = new boolean[8];
        configTweakBlockDamage = true;
        configCloudShadow = true;
        configHandDepthMul = 0.125f;
        configRenderResMul = 1.0f;
        configShadowResMul = 1.0f;
        configTexMinFilB = 0;
        configTexMinFilN = 0;
        configTexMinFilS = 0;
        configTexMagFilB = 0;
        configTexMagFilN = 0;
        configTexMagFilS = 0;
        configShadowClipFrustrum = true;
        configNormalMap = true;
        configSpecularMap = true;
        configOldLighting = new PropertyDefaultTrueFalse("oldLighting", "Classic Lighting", 0);
        configOldHandLight = new PropertyDefaultTrueFalse("oldHandLight", "Old Hand Light", 0);
        configAntialiasingLevel = 0;
        texMinFilDesc = new String[]{"Nearest", "Nearest-Nearest", "Nearest-Linear"};
        texMagFilDesc = new String[]{"Nearest", "Linear"};
        texMinFilValue = new int[]{9728, 9984, 9986};
        texMagFilValue = new int[]{9728, 9729};
        shaderPack = null;
        shaderPackLoaded = false;
        packNameNone = "OFF";
        packNameDefault = "(internal)";
        shaderpacksdirname = "shaderpacks";
        optionsfilename = "optionsshaders.txt";
        shaderPackOptions = null;
        shaderPackOptionSliders = null;
        shaderPackProfiles = null;
        shaderPackGuiScreens = null;
        shaderPackClouds = new PropertyDefaultFastFancyOff("clouds", "Clouds", 0);
        shaderPackOldLighting = new PropertyDefaultTrueFalse("oldLighting", "Classic Lighting", 0);
        shaderPackOldHandLight = new PropertyDefaultTrueFalse("oldHandLight", "Old Hand Light", 0);
        shaderPackDynamicHandLight = new PropertyDefaultTrueFalse("dynamicHandLight", "Dynamic Hand Light", 0);
        shaderPackShadowTranslucent = new PropertyDefaultTrueFalse("shadowTranslucent", "Shadow Translucent", 0);
        shaderPackUnderwaterOverlay = new PropertyDefaultTrueFalse("underwaterOverlay", "Underwater Overlay", 0);
        shaderPackSun = new PropertyDefaultTrueFalse("sun", "Sun", 0);
        shaderPackMoon = new PropertyDefaultTrueFalse("moon", "Moon", 0);
        shaderPackVignette = new PropertyDefaultTrueFalse("vignette", "Vignette", 0);
        shaderPackBackFaceSolid = new PropertyDefaultTrueFalse("backFace.solid", "Back-face Solid", 0);
        shaderPackBackFaceCutout = new PropertyDefaultTrueFalse("backFace.cutout", "Back-face Cutout", 0);
        shaderPackBackFaceCutoutMipped = new PropertyDefaultTrueFalse("backFace.cutoutMipped", "Back-face Cutout Mipped", 0);
        shaderPackBackFaceTranslucent = new PropertyDefaultTrueFalse("backFace.translucent", "Back-face Translucent", 0);
        shaderPackResources = new HashMap<String, String>();
        currentWorld = null;
        shaderPackDimensions = new ArrayList<Integer>();
        customTexturesGbuffers = null;
        customTexturesComposite = null;
        STAGE_NAMES = new String[]{"gbuffers", "composite"};
        saveFinalShaders = System.getProperty("shaders.debug.save", "false").equals("true");
        blockLightLevel05 = 0.5f;
        blockLightLevel06 = 0.6f;
        blockLightLevel08 = 0.8f;
        aoLevel = -1.0f;
        sunPathRotation = 0.0f;
        shadowAngleInterval = 0.0f;
        fogMode = 0;
        shadowIntervalSize = 2.0f;
        terrainIconSize = 16;
        terrainTextureSize = new int[2];
        noiseTextureEnabled = false;
        noiseTextureResolution = 256;
        dfbColorTexturesA = new int[16];
        colorTexturesToggle = new int[8];
        int[] nArray2 = new int[8];
        nArray2[1] = 1;
        nArray2[2] = 2;
        nArray2[3] = 3;
        nArray2[4] = 7;
        nArray2[5] = 8;
        nArray2[6] = 9;
        nArray2[7] = 10;
        colorTextureTextureImageUnit = nArray2;
        programsToggleColorTextures = new boolean[33][8];
        bigBuffer = (ByteBuffer)BufferUtils.createByteBuffer((int)2196).limit(0);
        faProjection = new float[16];
        faProjectionInverse = new float[16];
        faModelView = new float[16];
        faModelViewInverse = new float[16];
        faShadowProjection = new float[16];
        faShadowProjectionInverse = new float[16];
        faShadowModelView = new float[16];
        faShadowModelViewInverse = new float[16];
        projection = Shaders.nextFloatBuffer(16);
        projectionInverse = Shaders.nextFloatBuffer(16);
        modelView = Shaders.nextFloatBuffer(16);
        modelViewInverse = Shaders.nextFloatBuffer(16);
        shadowProjection = Shaders.nextFloatBuffer(16);
        shadowProjectionInverse = Shaders.nextFloatBuffer(16);
        shadowModelView = Shaders.nextFloatBuffer(16);
        shadowModelViewInverse = Shaders.nextFloatBuffer(16);
        previousProjection = Shaders.nextFloatBuffer(16);
        previousModelView = Shaders.nextFloatBuffer(16);
        tempMatrixDirectBuffer = Shaders.nextFloatBuffer(16);
        tempDirectFloatBuffer = Shaders.nextFloatBuffer(16);
        dfbColorTextures = Shaders.nextIntBuffer(16);
        dfbDepthTextures = Shaders.nextIntBuffer(3);
        sfbColorTextures = Shaders.nextIntBuffer(8);
        sfbDepthTextures = Shaders.nextIntBuffer(2);
        dfbDrawBuffers = Shaders.nextIntBuffer(8);
        sfbDrawBuffers = Shaders.nextIntBuffer(8);
        drawBuffersNone = Shaders.nextIntBuffer(8);
        drawBuffersAll = Shaders.nextIntBuffer(8);
        drawBuffersClear0 = Shaders.nextIntBuffer(8);
        drawBuffersClear1 = Shaders.nextIntBuffer(8);
        drawBuffersClearColor = Shaders.nextIntBuffer(8);
        drawBuffersColorAtt0 = Shaders.nextIntBuffer(8);
        drawBuffersBuffer = Shaders.nextIntBufferArray(33, 8);
        gbufferFormatPattern = Pattern.compile("[ \t]*const[ \t]*int[ \t]*(\\w+)Format[ \t]*=[ \t]*([RGBA0123456789FUI_SNORM]*)[ \t]*;.*");
        gbufferClearPattern = Pattern.compile("[ \t]*const[ \t]*bool[ \t]*(\\w+)Clear[ \t]*=[ \t]*false[ \t]*;.*");
        gbufferMipmapEnabledPattern = Pattern.compile("[ \t]*const[ \t]*bool[ \t]*(\\w+)MipmapEnabled[ \t]*=[ \t]*true[ \t]*;.*");
        formatNames = new String[]{"R8", "RG8", "RGB8", "RGBA8", "R8_SNORM", "RG8_SNORM", "RGB8_SNORM", "RGBA8_SNORM", "R16", "RG16", "RGB16", "RGBA16", "R16_SNORM", "RG16_SNORM", "RGB16_SNORM", "RGBA16_SNORM", "R16F", "RG16F", "RGB16F", "RGBA16F", "R32F", "RG32F", "RGB32F", "RGBA32F", "R32I", "RG32I", "RGB32I", "RGBA32I", "R32UI", "RG32UI", "RGB32UI", "RGBA32UI", "R3_G3_B2", "RGB5_A1", "RGB10_A2", "R11F_G11F_B10F", "RGB9_E5"};
        formatIds = new int[]{33321, 33323, 32849, 32856, 36756, 36757, 36758, 36759, 33322, 33324, 32852, 32859, 36760, 36761, 36762, 36763, 33325, 33327, 34843, 34842, 33326, 33328, 34837, 34836, 33333, 33339, 36227, 36226, 33334, 33340, 36209, 36208, 10768, 32855, 32857, 35898, 35901};
        patternLoadEntityDataMap = Pattern.compile("\\s*([\\w:]+)\\s*=\\s*([-]?\\d+)\\s*");
        entityData = new int[32];
        entityDataIndex = 0;
        shadersdir = new File(Minecraft.getMinecraft().mcDataDir, "shaders");
        shaderpacksdir = new File(Minecraft.getMinecraft().mcDataDir, shaderpacksdirname);
        configFile = new File(Minecraft.getMinecraft().mcDataDir, optionsfilename);
        drawBuffersNone.limit(0);
        drawBuffersColorAtt0.put(36064).position(0).limit(1);
    }

    private static ByteBuffer nextByteBuffer(int size) {
        ByteBuffer bytebuffer = bigBuffer;
        int i = bytebuffer.limit();
        bytebuffer.position(i).limit(i + size);
        return bytebuffer.slice();
    }

    private static IntBuffer nextIntBuffer(int size) {
        ByteBuffer bytebuffer = bigBuffer;
        int i = bytebuffer.limit();
        bytebuffer.position(i).limit(i + size * 4);
        return bytebuffer.asIntBuffer();
    }

    private static FloatBuffer nextFloatBuffer(int size) {
        ByteBuffer bytebuffer = bigBuffer;
        int i = bytebuffer.limit();
        bytebuffer.position(i).limit(i + size * 4);
        return bytebuffer.asFloatBuffer();
    }

    private static IntBuffer[] nextIntBufferArray(int count, int size) {
        IntBuffer[] aintbuffer = new IntBuffer[count];
        int i = 0;
        while (i < count) {
            aintbuffer[i] = Shaders.nextIntBuffer(size);
            ++i;
        }
        return aintbuffer;
    }

    public static void loadConfig() {
        SMCLog.info("Load ShadersMod configuration.");
        try {
            if (!shaderpacksdir.exists()) {
                shaderpacksdir.mkdir();
            }
        }
        catch (Exception var8) {
            SMCLog.severe("Failed to open the shaderpacks directory: " + shaderpacksdir);
        }
        shadersConfig = new PropertiesOrdered();
        shadersConfig.setProperty(EnumShaderOption.SHADER_PACK.getPropertyKey(), "");
        if (configFile.exists()) {
            try {
                FileReader filereader = new FileReader(configFile);
                shadersConfig.load(filereader);
                filereader.close();
            }
            catch (Exception filereader) {
                // empty catch block
            }
        }
        if (!configFile.exists()) {
            try {
                Shaders.storeConfig();
            }
            catch (Exception filereader) {
                // empty catch block
            }
        }
        EnumShaderOption[] aenumshaderoption = EnumShaderOption.values();
        int i = 0;
        while (i < aenumshaderoption.length) {
            EnumShaderOption enumshaderoption = aenumshaderoption[i];
            String s = enumshaderoption.getPropertyKey();
            String s1 = enumshaderoption.getValueDefault();
            String s2 = shadersConfig.getProperty(s, s1);
            Shaders.setEnumShaderOption(enumshaderoption, s2);
            ++i;
        }
        Shaders.loadShaderPack();
    }

    private static void setEnumShaderOption(EnumShaderOption eso, String str) {
        if (str == null) {
            str = eso.getValueDefault();
        }
        switch (eso) {
            case ANTIALIASING: {
                configAntialiasingLevel = Config.parseInt(str, 0);
                break;
            }
            case NORMAL_MAP: {
                configNormalMap = Config.parseBoolean(str, true);
                break;
            }
            case SPECULAR_MAP: {
                configSpecularMap = Config.parseBoolean(str, true);
                break;
            }
            case RENDER_RES_MUL: {
                configRenderResMul = Config.parseFloat(str, 1.0f);
                break;
            }
            case SHADOW_RES_MUL: {
                configShadowResMul = Config.parseFloat(str, 1.0f);
                break;
            }
            case HAND_DEPTH_MUL: {
                configHandDepthMul = Config.parseFloat(str, 0.125f);
                break;
            }
            case CLOUD_SHADOW: {
                configCloudShadow = Config.parseBoolean(str, true);
                break;
            }
            case OLD_HAND_LIGHT: {
                configOldHandLight.setPropertyValue(str);
                break;
            }
            case OLD_LIGHTING: {
                configOldLighting.setPropertyValue(str);
                break;
            }
            case SHADER_PACK: {
                currentshadername = str;
                break;
            }
            case TWEAK_BLOCK_DAMAGE: {
                configTweakBlockDamage = Config.parseBoolean(str, true);
                break;
            }
            case SHADOW_CLIP_FRUSTRUM: {
                configShadowClipFrustrum = Config.parseBoolean(str, true);
                break;
            }
            case TEX_MIN_FIL_B: {
                configTexMinFilB = Config.parseInt(str, 0);
                break;
            }
            case TEX_MIN_FIL_N: {
                configTexMinFilN = Config.parseInt(str, 0);
                break;
            }
            case TEX_MIN_FIL_S: {
                configTexMinFilS = Config.parseInt(str, 0);
                break;
            }
            case TEX_MAG_FIL_B: {
                configTexMagFilB = Config.parseInt(str, 0);
                break;
            }
            case TEX_MAG_FIL_N: {
                configTexMagFilB = Config.parseInt(str, 0);
                break;
            }
            case TEX_MAG_FIL_S: {
                configTexMagFilB = Config.parseInt(str, 0);
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown option: " + (Object)((Object)eso));
            }
        }
    }

    public static void storeConfig() {
        SMCLog.info("Save ShadersMod configuration.");
        if (shadersConfig == null) {
            shadersConfig = new PropertiesOrdered();
        }
        EnumShaderOption[] aenumshaderoption = EnumShaderOption.values();
        int i = 0;
        while (i < aenumshaderoption.length) {
            EnumShaderOption enumshaderoption = aenumshaderoption[i];
            String s = enumshaderoption.getPropertyKey();
            String s1 = Shaders.getEnumShaderOption(enumshaderoption);
            shadersConfig.setProperty(s, s1);
            ++i;
        }
        try {
            FileWriter filewriter = new FileWriter(configFile);
            shadersConfig.store(filewriter, null);
            filewriter.close();
        }
        catch (Exception exception) {
            SMCLog.severe("Error saving configuration: " + exception.getClass().getName() + ": " + exception.getMessage());
        }
    }

    public static String getEnumShaderOption(EnumShaderOption eso) {
        switch (eso) {
            case ANTIALIASING: {
                return Integer.toString(configAntialiasingLevel);
            }
            case NORMAL_MAP: {
                return Boolean.toString(configNormalMap);
            }
            case SPECULAR_MAP: {
                return Boolean.toString(configSpecularMap);
            }
            case RENDER_RES_MUL: {
                return Float.toString(configRenderResMul);
            }
            case SHADOW_RES_MUL: {
                return Float.toString(configShadowResMul);
            }
            case HAND_DEPTH_MUL: {
                return Float.toString(configHandDepthMul);
            }
            case CLOUD_SHADOW: {
                return Boolean.toString(configCloudShadow);
            }
            case OLD_HAND_LIGHT: {
                return configOldHandLight.getPropertyValue();
            }
            case OLD_LIGHTING: {
                return configOldLighting.getPropertyValue();
            }
            case SHADER_PACK: {
                return currentshadername;
            }
            case TWEAK_BLOCK_DAMAGE: {
                return Boolean.toString(configTweakBlockDamage);
            }
            case SHADOW_CLIP_FRUSTRUM: {
                return Boolean.toString(configShadowClipFrustrum);
            }
            case TEX_MIN_FIL_B: {
                return Integer.toString(configTexMinFilB);
            }
            case TEX_MIN_FIL_N: {
                return Integer.toString(configTexMinFilN);
            }
            case TEX_MIN_FIL_S: {
                return Integer.toString(configTexMinFilS);
            }
            case TEX_MAG_FIL_B: {
                return Integer.toString(configTexMagFilB);
            }
            case TEX_MAG_FIL_N: {
                return Integer.toString(configTexMagFilB);
            }
            case TEX_MAG_FIL_S: {
                return Integer.toString(configTexMagFilB);
            }
        }
        throw new IllegalArgumentException("Unknown option: " + (Object)((Object)eso));
    }

    public static void setShaderPack(String par1name) {
        currentshadername = par1name;
        shadersConfig.setProperty(EnumShaderOption.SHADER_PACK.getPropertyKey(), par1name);
        Shaders.loadShaderPack();
    }

    public static void loadShaderPack() {
        String s;
        boolean flag = shaderPackLoaded;
        boolean flag1 = Shaders.isOldLighting();
        shaderPackLoaded = false;
        if (shaderPack != null) {
            shaderPack.close();
            shaderPack = null;
            shaderPackResources.clear();
            shaderPackDimensions.clear();
            shaderPackOptions = null;
            shaderPackOptionSliders = null;
            shaderPackProfiles = null;
            shaderPackGuiScreens = null;
            shaderPackClouds.resetValue();
            shaderPackOldHandLight.resetValue();
            shaderPackDynamicHandLight.resetValue();
            shaderPackOldLighting.resetValue();
            Shaders.resetCustomTextures();
        }
        boolean flag2 = false;
        if (Config.isAntialiasing()) {
            SMCLog.info("Shaders can not be loaded, Antialiasing is enabled: " + Config.getAntialiasingLevel() + "x");
            flag2 = true;
        }
        if (Config.isAnisotropicFiltering()) {
            SMCLog.info("Shaders can not be loaded, Anisotropic Filtering is enabled: " + Config.getAnisotropicFilterLevel() + "x");
            flag2 = true;
        }
        if (Config.isFastRender()) {
            SMCLog.info("Shaders can not be loaded, Fast Render is enabled.");
            flag2 = true;
        }
        if (!((s = shadersConfig.getProperty(EnumShaderOption.SHADER_PACK.getPropertyKey(), packNameDefault)).isEmpty() || s.equals(packNameNone) || flag2)) {
            if (s.equals(packNameDefault)) {
                shaderPack = new ShaderPackDefault();
                shaderPackLoaded = true;
            } else {
                try {
                    File file1 = new File(shaderpacksdir, s);
                    if (file1.isDirectory()) {
                        shaderPack = new ShaderPackFolder(s, file1);
                        shaderPackLoaded = true;
                    } else if (file1.isFile() && s.toLowerCase().endsWith(".zip")) {
                        shaderPack = new ShaderPackZip(s, file1);
                        shaderPackLoaded = true;
                    }
                }
                catch (Exception file1) {
                    // empty catch block
                }
            }
        }
        if (shaderPack != null) {
            SMCLog.info("Loaded shaderpack: " + Shaders.getShaderPackName());
        } else {
            SMCLog.info("No shaderpack loaded.");
            shaderPack = new ShaderPackNone();
        }
        Shaders.loadShaderPackResources();
        Shaders.loadShaderPackDimensions();
        shaderPackOptions = Shaders.loadShaderPackOptions();
        Shaders.loadShaderPackProperties();
        boolean flag4 = shaderPackLoaded ^ flag;
        boolean flag3 = Shaders.isOldLighting() ^ flag1;
        if (flag4 || flag3) {
            DefaultVertexFormats.updateVertexFormats();
            if (Reflector.LightUtil.exists()) {
                Reflector.LightUtil_itemConsumer.setValue(null);
                Reflector.LightUtil_tessellator.setValue(null);
            }
            Shaders.updateBlockLightLevel();
            mc.scheduleResourcesRefresh();
        }
    }

    private static void loadShaderPackDimensions() {
        shaderPackDimensions.clear();
        int i = -128;
        while (i <= 128) {
            String s = "/shaders/world" + i;
            if (shaderPack.hasDirectory(s)) {
                shaderPackDimensions.add(i);
            }
            ++i;
        }
        if (shaderPackDimensions.size() > 0) {
            Object[] ainteger = shaderPackDimensions.toArray(new Integer[shaderPackDimensions.size()]);
            Config.dbg("[Shaders] Worlds: " + Config.arrayToString(ainteger));
        }
    }

    private static void loadShaderPackProperties() {
        shaderPackClouds.resetValue();
        shaderPackOldHandLight.resetValue();
        shaderPackDynamicHandLight.resetValue();
        shaderPackOldLighting.resetValue();
        shaderPackShadowTranslucent.resetValue();
        shaderPackUnderwaterOverlay.resetValue();
        shaderPackSun.resetValue();
        shaderPackMoon.resetValue();
        shaderPackVignette.resetValue();
        shaderPackBackFaceSolid.resetValue();
        shaderPackBackFaceCutout.resetValue();
        shaderPackBackFaceCutoutMipped.resetValue();
        shaderPackBackFaceTranslucent.resetValue();
        BlockAliases.reset();
        if (shaderPack != null) {
            BlockAliases.update(shaderPack);
            String s = "/shaders/shaders.properties";
            try {
                InputStream inputstream = shaderPack.getResourceAsStream(s);
                if (inputstream == null) {
                    return;
                }
                PropertiesOrdered properties = new PropertiesOrdered();
                properties.load(inputstream);
                inputstream.close();
                shaderPackClouds.loadFrom(properties);
                shaderPackOldHandLight.loadFrom(properties);
                shaderPackDynamicHandLight.loadFrom(properties);
                shaderPackOldLighting.loadFrom(properties);
                shaderPackShadowTranslucent.loadFrom(properties);
                shaderPackUnderwaterOverlay.loadFrom(properties);
                shaderPackSun.loadFrom(properties);
                shaderPackVignette.loadFrom(properties);
                shaderPackMoon.loadFrom(properties);
                shaderPackBackFaceSolid.loadFrom(properties);
                shaderPackBackFaceCutout.loadFrom(properties);
                shaderPackBackFaceCutoutMipped.loadFrom(properties);
                shaderPackBackFaceTranslucent.loadFrom(properties);
                shaderPackOptionSliders = ShaderPackParser.parseOptionSliders(properties, shaderPackOptions);
                shaderPackProfiles = ShaderPackParser.parseProfiles(properties, shaderPackOptions);
                shaderPackGuiScreens = ShaderPackParser.parseGuiScreens(properties, shaderPackProfiles, shaderPackOptions);
                customTexturesGbuffers = Shaders.loadCustomTextures(properties, 0);
                customTexturesComposite = Shaders.loadCustomTextures(properties, 1);
            }
            catch (IOException var3) {
                Config.warn("[Shaders] Error reading: " + s);
            }
        }
    }

    private static CustomTexture[] loadCustomTextures(Properties props, int stage) {
        String s = "texture." + STAGE_NAMES[stage] + ".";
        Set<Object> set = props.keySet();
        ArrayList<CustomTexture> list = new ArrayList<CustomTexture>();
        for (Object s10 : set) {
            String s1 = (String)s10;
            if (!s1.startsWith(s)) continue;
            String s2 = s1.substring(s.length());
            String s3 = props.getProperty(s1).trim();
            int i = Shaders.getTextureIndex(stage, s2);
            if (i < 0) {
                SMCLog.warning("Invalid texture name: " + s1);
                continue;
            }
            try {
                String s4 = "shaders/" + StrUtils.removePrefix(s3, "/");
                InputStream inputstream = shaderPack.getResourceAsStream(s4);
                if (inputstream == null) {
                    SMCLog.warning("Texture not found: " + s3);
                    continue;
                }
                IOUtils.closeQuietly((InputStream)inputstream);
                SimpleShaderTexture simpleshadertexture = new SimpleShaderTexture(s4);
                simpleshadertexture.loadTexture(mc.getResourceManager());
                CustomTexture customtexture = new CustomTexture(i, s4, simpleshadertexture);
                list.add(customtexture);
            }
            catch (IOException ioexception) {
                SMCLog.warning("Error loading texture: " + s3);
                SMCLog.warning(ioexception.getClass().getName() + ": " + ioexception.getMessage());
            }
        }
        if (list.size() <= 0) {
            return null;
        }
        CustomTexture[] acustomtexture = list.toArray(new CustomTexture[list.size()]);
        return acustomtexture;
    }

    private static int getTextureIndex(int stage, String name) {
        if (stage == 0) {
            if (name.equals("texture")) {
                return 0;
            }
            if (name.equals("lightmap")) {
                return 1;
            }
            if (name.equals("normals")) {
                return 2;
            }
            if (name.equals("specular")) {
                return 3;
            }
            if (name.equals("shadowtex0") || name.equals("watershadow")) {
                return 4;
            }
            if (name.equals("shadow")) {
                return waterShadowEnabled ? 5 : 4;
            }
            if (name.equals("shadowtex1")) {
                return 5;
            }
            if (name.equals("depthtex0")) {
                return 6;
            }
            if (name.equals("gaux1")) {
                return 7;
            }
            if (name.equals("gaux2")) {
                return 8;
            }
            if (name.equals("gaux3")) {
                return 9;
            }
            if (name.equals("gaux4")) {
                return 10;
            }
            if (name.equals("depthtex1")) {
                return 12;
            }
            if (name.equals("shadowcolor0") || name.equals("shadowcolor")) {
                return 13;
            }
            if (name.equals("shadowcolor1")) {
                return 14;
            }
            if (name.equals("noisetex")) {
                return 15;
            }
        }
        if (stage == 1) {
            if (name.equals("colortex0") || name.equals("colortex0")) {
                return 0;
            }
            if (name.equals("colortex1") || name.equals("gdepth")) {
                return 1;
            }
            if (name.equals("colortex2") || name.equals("gnormal")) {
                return 2;
            }
            if (name.equals("colortex3") || name.equals("composite")) {
                return 3;
            }
            if (name.equals("shadowtex0") || name.equals("watershadow")) {
                return 4;
            }
            if (name.equals("shadow")) {
                return waterShadowEnabled ? 5 : 4;
            }
            if (name.equals("shadowtex1")) {
                return 5;
            }
            if (name.equals("depthtex0") || name.equals("gdepthtex")) {
                return 6;
            }
            if (name.equals("colortex4") || name.equals("gaux1")) {
                return 7;
            }
            if (name.equals("colortex5") || name.equals("gaux2")) {
                return 8;
            }
            if (name.equals("colortex6") || name.equals("gaux3")) {
                return 9;
            }
            if (name.equals("colortex7") || name.equals("gaux4")) {
                return 10;
            }
            if (name.equals("depthtex1")) {
                return 11;
            }
            if (name.equals("depthtex2")) {
                return 12;
            }
            if (name.equals("shadowcolor0") || name.equals("shadowcolor")) {
                return 13;
            }
            if (name.equals("shadowcolor1")) {
                return 14;
            }
            if (name.equals("noisetex")) {
                return 15;
            }
        }
        return -1;
    }

    private static void bindCustomTextures(CustomTexture[] cts) {
        if (cts != null) {
            int i = 0;
            while (i < cts.length) {
                CustomTexture customtexture = cts[i];
                GlStateManager.setActiveTexture(33984 + customtexture.getTextureUnit());
                ITextureObject itextureobject = customtexture.getTexture();
                GlStateManager.bindTexture(itextureobject.getGlTextureId());
                ++i;
            }
        }
    }

    private static void resetCustomTextures() {
        Shaders.deleteCustomTextures(customTexturesGbuffers);
        Shaders.deleteCustomTextures(customTexturesComposite);
        customTexturesGbuffers = null;
        customTexturesComposite = null;
    }

    private static void deleteCustomTextures(CustomTexture[] cts) {
        if (cts != null) {
            int i = 0;
            while (i < cts.length) {
                CustomTexture customtexture = cts[i];
                ITextureObject itextureobject = customtexture.getTexture();
                TextureUtil.deleteTexture(itextureobject.getGlTextureId());
                ++i;
            }
        }
    }

    public static ShaderOption[] getShaderPackOptions(String screenName) {
        Object[] ashaderoption = (ShaderOption[])shaderPackOptions.clone();
        if (shaderPackGuiScreens == null) {
            if (shaderPackProfiles != null) {
                ShaderOptionProfile shaderoptionprofile = new ShaderOptionProfile(shaderPackProfiles, (ShaderOption[])ashaderoption);
                ashaderoption = (ShaderOption[])Config.addObjectToArray(ashaderoption, shaderoptionprofile, 0);
            }
            ashaderoption = Shaders.getVisibleOptions((ShaderOption[])ashaderoption);
            return ashaderoption;
        }
        String s = screenName != null ? "screen." + screenName : "screen";
        ShaderOption[] ashaderoption1 = shaderPackGuiScreens.get(s);
        if (ashaderoption1 == null) {
            return new ShaderOption[0];
        }
        ArrayList<ShaderOption> list = new ArrayList<ShaderOption>();
        int i = 0;
        while (i < ashaderoption1.length) {
            ShaderOption shaderoption = ashaderoption1[i];
            if (shaderoption == null) {
                list.add(null);
            } else if (shaderoption instanceof ShaderOptionRest) {
                ShaderOption[] ashaderoption2 = Shaders.getShaderOptionsRest(shaderPackGuiScreens, (ShaderOption[])ashaderoption);
                list.addAll(Arrays.asList(ashaderoption2));
            } else {
                list.add(shaderoption);
            }
            ++i;
        }
        ShaderOption[] ashaderoption3 = list.toArray(new ShaderOption[list.size()]);
        return ashaderoption3;
    }

    private static ShaderOption[] getShaderOptionsRest(Map<String, ShaderOption[]> mapScreens, ShaderOption[] ops) {
        HashSet<String> set = new HashSet<String>();
        for (String s : mapScreens.keySet()) {
            ShaderOption[] ashaderoption = mapScreens.get(s);
            int i = 0;
            while (i < ashaderoption.length) {
                ShaderOption shaderoption = ashaderoption[i];
                if (shaderoption != null) {
                    set.add(shaderoption.getName());
                }
                ++i;
            }
        }
        ArrayList<ShaderOption> list = new ArrayList<ShaderOption>();
        int j = 0;
        while (j < ops.length) {
            String s1;
            ShaderOption shaderoption1 = ops[j];
            if (shaderoption1.isVisible() && !set.contains(s1 = shaderoption1.getName())) {
                list.add(shaderoption1);
            }
            ++j;
        }
        ShaderOption[] ashaderoption1 = list.toArray(new ShaderOption[list.size()]);
        return ashaderoption1;
    }

    public static ShaderOption getShaderOption(String name) {
        return ShaderUtils.getShaderOption(name, shaderPackOptions);
    }

    public static ShaderOption[] getShaderPackOptions() {
        return shaderPackOptions;
    }

    public static boolean isShaderPackOptionSlider(String name) {
        return shaderPackOptionSliders == null ? false : shaderPackOptionSliders.contains(name);
    }

    private static ShaderOption[] getVisibleOptions(ShaderOption[] ops) {
        ArrayList<ShaderOption> list = new ArrayList<ShaderOption>();
        int i = 0;
        while (i < ops.length) {
            ShaderOption shaderoption = ops[i];
            if (shaderoption.isVisible()) {
                list.add(shaderoption);
            }
            ++i;
        }
        ShaderOption[] ashaderoption = list.toArray(new ShaderOption[list.size()]);
        return ashaderoption;
    }

    public static void saveShaderPackOptions() {
        Shaders.saveShaderPackOptions(shaderPackOptions, shaderPack);
    }

    private static void saveShaderPackOptions(ShaderOption[] sos, IShaderPack sp) {
        Properties properties = new Properties();
        if (shaderPackOptions != null) {
            int i = 0;
            while (i < sos.length) {
                ShaderOption shaderoption = sos[i];
                if (shaderoption.isChanged() && shaderoption.isEnabled()) {
                    properties.setProperty(shaderoption.getName(), shaderoption.getValue());
                }
                ++i;
            }
        }
        try {
            Shaders.saveOptionProperties(sp, properties);
        }
        catch (IOException ioexception) {
            Config.warn("[Shaders] Error saving configuration for " + shaderPack.getName());
            ioexception.printStackTrace();
        }
    }

    private static void saveOptionProperties(IShaderPack sp, Properties props) throws IOException {
        String s = String.valueOf(shaderpacksdirname) + "/" + sp.getName() + ".txt";
        File file1 = new File(Minecraft.getMinecraft().mcDataDir, s);
        if (props.isEmpty()) {
            file1.delete();
        } else {
            FileOutputStream fileoutputstream = new FileOutputStream(file1);
            props.store(fileoutputstream, null);
            fileoutputstream.flush();
            fileoutputstream.close();
        }
    }

    private static ShaderOption[] loadShaderPackOptions() {
        try {
            ShaderOption[] ashaderoption = ShaderPackParser.parseShaderPackOptions(shaderPack, programNames, shaderPackDimensions);
            Properties properties = Shaders.loadOptionProperties(shaderPack);
            int i = 0;
            while (i < ashaderoption.length) {
                ShaderOption shaderoption = ashaderoption[i];
                String s = properties.getProperty(shaderoption.getName());
                if (s != null) {
                    shaderoption.resetValue();
                    if (!shaderoption.setValue(s)) {
                        Config.warn("[Shaders] Invalid value, option: " + shaderoption.getName() + ", value: " + s);
                    }
                }
                ++i;
            }
            return ashaderoption;
        }
        catch (IOException ioexception) {
            Config.warn("[Shaders] Error reading configuration for " + shaderPack.getName());
            ioexception.printStackTrace();
            return null;
        }
    }

    private static Properties loadOptionProperties(IShaderPack sp) throws IOException {
        Properties properties = new Properties();
        String s = String.valueOf(shaderpacksdirname) + "/" + sp.getName() + ".txt";
        File file1 = new File(Minecraft.getMinecraft().mcDataDir, s);
        if (file1.exists() && file1.isFile() && file1.canRead()) {
            FileInputStream fileinputstream = new FileInputStream(file1);
            properties.load(fileinputstream);
            fileinputstream.close();
            return properties;
        }
        return properties;
    }

    public static ShaderOption[] getChangedOptions(ShaderOption[] ops) {
        ArrayList<ShaderOption> list = new ArrayList<ShaderOption>();
        int i = 0;
        while (i < ops.length) {
            ShaderOption shaderoption = ops[i];
            if (shaderoption.isEnabled() && shaderoption.isChanged()) {
                list.add(shaderoption);
            }
            ++i;
        }
        ShaderOption[] ashaderoption = list.toArray(new ShaderOption[list.size()]);
        return ashaderoption;
    }

    private static String applyOptions(String line, ShaderOption[] ops) {
        if (ops != null && ops.length > 0) {
            int i = 0;
            while (i < ops.length) {
                ShaderOption shaderoption = ops[i];
                String s = shaderoption.getName();
                if (shaderoption.matchesLine(line)) {
                    line = shaderoption.getSourceLine();
                    break;
                }
                ++i;
            }
            return line;
        }
        return line;
    }

    static ArrayList listOfShaders() {
        ArrayList<String> arraylist = new ArrayList<String>();
        arraylist.add(packNameNone);
        arraylist.add(packNameDefault);
        try {
            if (!shaderpacksdir.exists()) {
                shaderpacksdir.mkdir();
            }
            File[] afile = shaderpacksdir.listFiles();
            int i = 0;
            while (i < afile.length) {
                File file1 = afile[i];
                String s = file1.getName();
                if (file1.isDirectory()) {
                    File file2 = new File(file1, "shaders");
                    if (file2.exists() && file2.isDirectory()) {
                        arraylist.add(s);
                    }
                } else if (file1.isFile() && s.toLowerCase().endsWith(".zip")) {
                    arraylist.add(s);
                }
                ++i;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return arraylist;
    }

    static String versiontostring(int vv) {
        String s = Integer.toString(vv);
        return String.valueOf(Integer.toString(Integer.parseInt(s.substring(1, 3)))) + "." + Integer.toString(Integer.parseInt(s.substring(3, 5))) + "." + Integer.toString(Integer.parseInt(s.substring(5)));
    }

    static void checkOptifine() {
    }

    public static int checkFramebufferStatus(String location) {
        int i = EXTFramebufferObject.glCheckFramebufferStatusEXT((int)36160);
        if (i != 36053) {
            System.err.format("FramebufferStatus 0x%04X at %s\n", i, location);
        }
        return i;
    }

    public static int checkGLError(String location) {
        boolean flag;
        int i = GL11.glGetError();
        if (i != 0 && !(flag = false)) {
            if (i == 1286) {
                int j = EXTFramebufferObject.glCheckFramebufferStatusEXT((int)36160);
                System.err.format("GL error 0x%04X: %s (Fb status 0x%04X) at %s\n", i, GLU.gluErrorString((int)i), j, location);
            } else {
                System.err.format("GL error 0x%04X: %s at %s\n", i, GLU.gluErrorString((int)i), location);
            }
        }
        return i;
    }

    public static int checkGLError(String location, String info) {
        int i = GL11.glGetError();
        if (i != 0) {
            System.err.format("GL error 0x%04x: %s at %s %s\n", i, GLU.gluErrorString((int)i), location, info);
        }
        return i;
    }

    public static int checkGLError(String location, String info1, String info2) {
        int i = GL11.glGetError();
        if (i != 0) {
            System.err.format("GL error 0x%04x: %s at %s %s %s\n", i, GLU.gluErrorString((int)i), location, info1, info2);
        }
        return i;
    }

    private static void printChat(String str) {
        Shaders.mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentString(str));
    }

    private static void printChatAndLogError(String str) {
        SMCLog.severe(str);
        Shaders.mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentString(str));
    }

    public static void printIntBuffer(String title, IntBuffer buf) {
        StringBuilder stringbuilder = new StringBuilder(128);
        stringbuilder.append(title).append(" [pos ").append(buf.position()).append(" lim ").append(buf.limit()).append(" cap ").append(buf.capacity()).append(" :");
        int i = buf.limit();
        int j = 0;
        while (j < i) {
            stringbuilder.append(" ").append(buf.get(j));
            ++j;
        }
        stringbuilder.append("]");
        SMCLog.info(stringbuilder.toString());
    }

    public static void startup(Minecraft mc) {
        Shaders.checkShadersModInstalled();
        mc = Minecraft.getMinecraft();
        capabilities = GLContext.getCapabilities();
        glVersionString = GL11.glGetString((int)7938);
        glVendorString = GL11.glGetString((int)7936);
        glRendererString = GL11.glGetString((int)7937);
        SMCLog.info("ShadersMod version: 2.4.12");
        SMCLog.info("OpenGL Version: " + glVersionString);
        SMCLog.info("Vendor:  " + glVendorString);
        SMCLog.info("Renderer: " + glRendererString);
        SMCLog.info("Capabilities: " + (Shaders.capabilities.OpenGL20 ? " 2.0 " : " - ") + (Shaders.capabilities.OpenGL21 ? " 2.1 " : " - ") + (Shaders.capabilities.OpenGL30 ? " 3.0 " : " - ") + (Shaders.capabilities.OpenGL32 ? " 3.2 " : " - ") + (Shaders.capabilities.OpenGL40 ? " 4.0 " : " - "));
        SMCLog.info("GL_MAX_DRAW_BUFFERS: " + GL11.glGetInteger((int)34852));
        SMCLog.info("GL_MAX_COLOR_ATTACHMENTS_EXT: " + GL11.glGetInteger((int)36063));
        SMCLog.info("GL_MAX_TEXTURE_IMAGE_UNITS: " + GL11.glGetInteger((int)34930));
        hasGlGenMipmap = Shaders.capabilities.OpenGL30;
        Shaders.loadConfig();
    }

    private static String toStringYN(boolean b) {
        return b ? "Y" : "N";
    }

    public static void updateBlockLightLevel() {
        if (Shaders.isOldLighting()) {
            blockLightLevel05 = 0.5f;
            blockLightLevel06 = 0.6f;
            blockLightLevel08 = 0.8f;
        } else {
            blockLightLevel05 = 1.0f;
            blockLightLevel06 = 1.0f;
            blockLightLevel08 = 1.0f;
        }
    }

    public static boolean isOldHandLight() {
        if (!configOldHandLight.isDefault()) {
            return configOldHandLight.isTrue();
        }
        return !shaderPackOldHandLight.isDefault() ? shaderPackOldHandLight.isTrue() : true;
    }

    public static boolean isDynamicHandLight() {
        return !shaderPackDynamicHandLight.isDefault() ? shaderPackDynamicHandLight.isTrue() : true;
    }

    public static boolean isOldLighting() {
        if (!configOldLighting.isDefault()) {
            return configOldLighting.isTrue();
        }
        return !shaderPackOldLighting.isDefault() ? shaderPackOldLighting.isTrue() : true;
    }

    public static boolean isRenderShadowTranslucent() {
        return !shaderPackShadowTranslucent.isFalse();
    }

    public static boolean isUnderwaterOverlay() {
        return !shaderPackUnderwaterOverlay.isFalse();
    }

    public static boolean isSun() {
        return !shaderPackSun.isFalse();
    }

    public static boolean isMoon() {
        return !shaderPackMoon.isFalse();
    }

    public static boolean isVignette() {
        return !shaderPackVignette.isFalse();
    }

    public static boolean isRenderBackFace(BlockRenderLayer blockLayerIn) {
        switch (blockLayerIn) {
            case SOLID: {
                return shaderPackBackFaceSolid.isTrue();
            }
            case CUTOUT: {
                return shaderPackBackFaceCutout.isTrue();
            }
            case CUTOUT_MIPPED: {
                return shaderPackBackFaceCutoutMipped.isTrue();
            }
            case TRANSLUCENT: {
                return shaderPackBackFaceTranslucent.isTrue();
            }
        }
        return false;
    }

    public static void init() {
        boolean flag;
        if (!isInitializedOnce) {
            isInitializedOnce = true;
            flag = true;
        } else {
            flag = false;
        }
        if (!isShaderPackInitialized) {
            int i;
            Shaders.checkGLError("Shaders.init pre");
            if (Shaders.getShaderPackName() != null) {
                // empty if block
            }
            if (!Shaders.capabilities.OpenGL20) {
                Shaders.printChatAndLogError("No OpenGL 2.0");
            }
            if (!Shaders.capabilities.GL_EXT_framebuffer_object) {
                Shaders.printChatAndLogError("No EXT_framebuffer_object");
            }
            dfbDrawBuffers.position(0).limit(8);
            dfbColorTextures.position(0).limit(16);
            dfbDepthTextures.position(0).limit(3);
            sfbDrawBuffers.position(0).limit(8);
            sfbDepthTextures.position(0).limit(2);
            sfbColorTextures.position(0).limit(8);
            usedColorBuffers = 4;
            usedDepthBuffers = 1;
            usedShadowColorBuffers = 0;
            usedShadowDepthBuffers = 0;
            usedColorAttachs = 1;
            usedDrawBuffers = 1;
            Arrays.fill(gbuffersFormat, 6408);
            Arrays.fill(gbuffersClear, true);
            Arrays.fill(shadowHardwareFilteringEnabled, false);
            Arrays.fill(shadowMipmapEnabled, false);
            Arrays.fill(shadowFilterNearest, false);
            Arrays.fill(shadowColorMipmapEnabled, false);
            Arrays.fill(shadowColorFilterNearest, false);
            centerDepthSmoothEnabled = false;
            noiseTextureEnabled = false;
            sunPathRotation = 0.0f;
            shadowIntervalSize = 2.0f;
            shadowDistanceRenderMul = -1.0f;
            aoLevel = -1.0f;
            useEntityAttrib = false;
            useMidTexCoordAttrib = false;
            useMultiTexCoord3Attrib = false;
            useTangentAttrib = false;
            waterShadowEnabled = false;
            updateChunksErrorRecorded = false;
            Shaders.updateBlockLightLevel();
            ShaderProfile shaderprofile = ShaderUtils.detectProfile(shaderPackProfiles, shaderPackOptions, false);
            String s = "";
            if (currentWorld != null && shaderPackDimensions.contains(i = Shaders.currentWorld.provider.getDimensionType().getId())) {
                s = "world" + i + "/";
            }
            if (saveFinalShaders) {
                Shaders.clearDirectory(new File(shaderpacksdir, "debug"));
            }
            int k1 = 0;
            while (k1 < 33) {
                String s1 = programNames[k1];
                if (s1.equals("")) {
                    Shaders.programsRef[k1] = 0;
                    Shaders.programsID[k1] = 0;
                    Shaders.programsDrawBufSettings[k1] = null;
                    Shaders.programsColorAtmSettings[k1] = null;
                    Shaders.programsCompositeMipmapSetting[k1] = 0;
                } else {
                    String s3;
                    int j;
                    newDrawBufSetting = null;
                    newColorAtmSetting = null;
                    newCompositeMipmapSetting = 0;
                    String s2 = String.valueOf(s) + s1;
                    if (shaderprofile != null && shaderprofile.isProgramDisabled(s2)) {
                        SMCLog.info("Program disabled: " + s2);
                        s1 = "<disabled>";
                        s2 = String.valueOf(s) + s1;
                    }
                    if ((j = Shaders.setupProgram(k1, String.valueOf(s3 = "/shaders/" + s2) + ".vsh", String.valueOf(s3) + ".fsh")) > 0) {
                        SMCLog.info("Program loaded: " + s2);
                    }
                    Shaders.programsID[k1] = Shaders.programsRef[k1] = j;
                    Shaders.programsDrawBufSettings[k1] = j != 0 ? newDrawBufSetting : null;
                    Shaders.programsColorAtmSettings[k1] = j != 0 ? newColorAtmSetting : null;
                    Shaders.programsCompositeMipmapSetting[k1] = j != 0 ? newCompositeMipmapSetting : 0;
                }
                ++k1;
            }
            int l1 = GL11.glGetInteger((int)34852);
            new HashMap();
            int i2 = 0;
            while (i2 < 33) {
                Arrays.fill(programsToggleColorTextures[i2], false);
                if (i2 == 29) {
                    Shaders.programsDrawBuffers[i2] = null;
                } else if (programsID[i2] == 0) {
                    Shaders.programsDrawBuffers[i2] = i2 == 30 ? drawBuffersNone : drawBuffersColorAtt0;
                } else {
                    String s4 = programsDrawBufSettings[i2];
                    if (s4 != null) {
                        IntBuffer intbuffer = drawBuffersBuffer[i2];
                        int k = s4.length();
                        if (k > usedDrawBuffers) {
                            usedDrawBuffers = k;
                        }
                        if (k > l1) {
                            k = l1;
                        }
                        Shaders.programsDrawBuffers[i2] = intbuffer;
                        intbuffer.limit(k);
                        int l = 0;
                        while (l < k) {
                            int i1 = 0;
                            if (s4.length() > l) {
                                int j1 = s4.charAt(l) - 48;
                                if (i2 != 30) {
                                    if (j1 >= 0 && j1 <= 7) {
                                        Shaders.programsToggleColorTextures[i2][j1] = true;
                                        i1 = j1 + 36064;
                                        if (j1 > usedColorAttachs) {
                                            usedColorAttachs = j1;
                                        }
                                        if (j1 > usedColorBuffers) {
                                            usedColorBuffers = j1;
                                        }
                                    }
                                } else if (j1 >= 0 && j1 <= 1) {
                                    i1 = j1 + 36064;
                                    if (j1 > usedShadowColorBuffers) {
                                        usedShadowColorBuffers = j1;
                                    }
                                }
                            }
                            intbuffer.put(l, i1);
                            ++l;
                        }
                    } else if (i2 != 30 && i2 != 31 && i2 != 32) {
                        Shaders.programsDrawBuffers[i2] = dfbDrawBuffers;
                        usedDrawBuffers = usedColorBuffers;
                        Arrays.fill(programsToggleColorTextures[i2], 0, usedColorBuffers, true);
                    } else {
                        Shaders.programsDrawBuffers[i2] = sfbDrawBuffers;
                    }
                }
                ++i2;
            }
            usedColorAttachs = usedColorBuffers;
            shadowPassInterval = usedShadowDepthBuffers > 0 ? 1 : 0;
            shouldSkipDefaultShadow = usedShadowDepthBuffers > 0;
            SMCLog.info("usedColorBuffers: " + usedColorBuffers);
            SMCLog.info("usedDepthBuffers: " + usedDepthBuffers);
            SMCLog.info("usedShadowColorBuffers: " + usedShadowColorBuffers);
            SMCLog.info("usedShadowDepthBuffers: " + usedShadowDepthBuffers);
            SMCLog.info("usedColorAttachs: " + usedColorAttachs);
            SMCLog.info("usedDrawBuffers: " + usedDrawBuffers);
            dfbDrawBuffers.position(0).limit(usedDrawBuffers);
            dfbColorTextures.position(0).limit(usedColorBuffers * 2);
            int j2 = 0;
            while (j2 < usedDrawBuffers) {
                dfbDrawBuffers.put(j2, 36064 + j2);
                ++j2;
            }
            if (usedDrawBuffers > l1) {
                Shaders.printChatAndLogError("[Shaders] Error: Not enough draw buffers, needed: " + usedDrawBuffers + ", available: " + l1);
            }
            sfbDrawBuffers.position(0).limit(usedShadowColorBuffers);
            int k2 = 0;
            while (k2 < usedShadowColorBuffers) {
                sfbDrawBuffers.put(k2, 36064 + k2);
                ++k2;
            }
            int l2 = 0;
            while (l2 < 33) {
                int i3 = l2;
                while (programsID[i3] == 0 && programBackups[i3] != i3) {
                    i3 = programBackups[i3];
                }
                if (i3 != l2 && l2 != 30) {
                    Shaders.programsID[l2] = programsID[i3];
                    Shaders.programsDrawBufSettings[l2] = programsDrawBufSettings[i3];
                    Shaders.programsDrawBuffers[l2] = programsDrawBuffers[i3];
                }
                ++l2;
            }
            Shaders.resize();
            Shaders.resizeShadow();
            if (noiseTextureEnabled) {
                Shaders.setupNoiseTexture();
            }
            if (defaultTexture == null) {
                defaultTexture = ShadersTex.createDefaultTexture();
            }
            GlStateManager.pushMatrix();
            GlStateManager.rotate(-90.0f, 0.0f, 1.0f, 0.0f);
            Shaders.preCelestialRotate();
            Shaders.postCelestialRotate();
            GlStateManager.popMatrix();
            isShaderPackInitialized = true;
            Shaders.loadEntityDataMap();
            Shaders.resetDisplayList();
            if (!flag) {
                // empty if block
            }
            Shaders.checkGLError("Shaders.init");
        }
    }

    public static void resetDisplayList() {
        ++numberResetDisplayList;
        needResetModels = true;
        SMCLog.info("Reset world renderers");
        Shaders.mc.renderGlobal.loadRenderers();
    }

    public static void resetDisplayListModels() {
        if (needResetModels) {
            needResetModels = false;
            SMCLog.info("Reset model renderers");
            for (Render render : mc.getRenderManager().getEntityRenderMap().values()) {
                if (!(render instanceof RenderLiving)) continue;
                RenderLiving renderliving = (RenderLiving)render;
                Shaders.resetDisplayListModel(renderliving.getMainModel());
            }
        }
    }

    public static void resetDisplayListModel(ModelBase model) {
        if (model != null) {
            for (ModelRenderer object : model.boxList) {
                if (!(object instanceof ModelRenderer)) continue;
                Shaders.resetDisplayListModelRenderer(object);
            }
        }
    }

    public static void resetDisplayListModelRenderer(ModelRenderer mrr) {
        mrr.resetDisplayList();
        if (mrr.childModels != null) {
            int i = 0;
            int j = mrr.childModels.size();
            while (i < j) {
                Shaders.resetDisplayListModelRenderer(mrr.childModels.get(i));
                ++i;
            }
        }
    }

    private static int setupProgram(int program, String vShaderPath, String fShaderPath) {
        Shaders.checkGLError("pre setupProgram");
        int i = ARBShaderObjects.glCreateProgramObjectARB();
        Shaders.checkGLError("create");
        if (i != 0) {
            progUseEntityAttrib = false;
            progUseMidTexCoordAttrib = false;
            progUseTangentAttrib = false;
            int j = Shaders.createVertShader(vShaderPath);
            int k = Shaders.createFragShader(fShaderPath);
            Shaders.checkGLError("create");
            if (j == 0 && k == 0) {
                ARBShaderObjects.glDeleteObjectARB((int)i);
                i = 0;
            } else {
                if (j != 0) {
                    ARBShaderObjects.glAttachObjectARB((int)i, (int)j);
                    Shaders.checkGLError("attach");
                }
                if (k != 0) {
                    ARBShaderObjects.glAttachObjectARB((int)i, (int)k);
                    Shaders.checkGLError("attach");
                }
                if (progUseEntityAttrib) {
                    ARBVertexShader.glBindAttribLocationARB((int)i, (int)entityAttrib, (CharSequence)"mc_Entity");
                    Shaders.checkGLError("mc_Entity");
                }
                if (progUseMidTexCoordAttrib) {
                    ARBVertexShader.glBindAttribLocationARB((int)i, (int)midTexCoordAttrib, (CharSequence)"mc_midTexCoord");
                    Shaders.checkGLError("mc_midTexCoord");
                }
                if (progUseTangentAttrib) {
                    ARBVertexShader.glBindAttribLocationARB((int)i, (int)tangentAttrib, (CharSequence)"at_tangent");
                    Shaders.checkGLError("at_tangent");
                }
                ARBShaderObjects.glLinkProgramARB((int)i);
                if (GL20.glGetProgrami((int)i, (int)35714) != 1) {
                    SMCLog.severe("Error linking program: " + i);
                }
                Shaders.printLogInfo(i, String.valueOf(vShaderPath) + ", " + fShaderPath);
                if (j != 0) {
                    ARBShaderObjects.glDetachObjectARB((int)i, (int)j);
                    ARBShaderObjects.glDeleteObjectARB((int)j);
                }
                if (k != 0) {
                    ARBShaderObjects.glDetachObjectARB((int)i, (int)k);
                    ARBShaderObjects.glDeleteObjectARB((int)k);
                }
                Shaders.programsID[program] = i;
                Shaders.useProgram(program);
                ARBShaderObjects.glValidateProgramARB((int)i);
                Shaders.useProgram(0);
                Shaders.printLogInfo(i, String.valueOf(vShaderPath) + ", " + fShaderPath);
                int l = GL20.glGetProgrami((int)i, (int)35715);
                if (l != 1) {
                    String s = "\"";
                    Shaders.printChatAndLogError("[Shaders] Error: Invalid program " + s + programNames[program] + s);
                    ARBShaderObjects.glDeleteObjectARB((int)i);
                    i = 0;
                }
            }
        }
        return i;
    }

    private static int createVertShader(String filename) {
        int i = ARBShaderObjects.glCreateShaderObjectARB((int)35633);
        if (i == 0) {
            return 0;
        }
        StringBuilder stringbuilder = new StringBuilder(131072);
        BufferedReader bufferedreader = null;
        try {
            bufferedreader = new BufferedReader(new InputStreamReader(shaderPack.getResourceAsStream(filename)));
        }
        catch (Exception var8) {
            try {
                bufferedreader = new BufferedReader(new FileReader(new File(filename)));
            }
            catch (Exception var7) {
                ARBShaderObjects.glDeleteObjectARB((int)i);
                return 0;
            }
        }
        ShaderOption[] ashaderoption = Shaders.getChangedOptions(shaderPackOptions);
        ArrayList<String> list = new ArrayList<String>();
        if (bufferedreader != null) {
            try {
                bufferedreader = ShaderPackParser.resolveIncludes(bufferedreader, filename, shaderPack, 0, list, 0);
                while (true) {
                    String s;
                    if ((s = bufferedreader.readLine()) == null) {
                        bufferedreader.close();
                        break;
                    }
                    s = Shaders.applyOptions(s, ashaderoption);
                    stringbuilder.append(s).append('\n');
                    if (s.matches("attribute [_a-zA-Z0-9]+ mc_Entity.*")) {
                        useEntityAttrib = true;
                        progUseEntityAttrib = true;
                        continue;
                    }
                    if (s.matches("attribute [_a-zA-Z0-9]+ mc_midTexCoord.*")) {
                        useMidTexCoordAttrib = true;
                        progUseMidTexCoordAttrib = true;
                        continue;
                    }
                    if (s.matches(".*gl_MultiTexCoord3.*")) {
                        useMultiTexCoord3Attrib = true;
                        continue;
                    }
                    if (!s.matches("attribute [_a-zA-Z0-9]+ at_tangent.*")) continue;
                    useTangentAttrib = true;
                    progUseTangentAttrib = true;
                }
            }
            catch (Exception exception) {
                SMCLog.severe("Couldn't read " + filename + "!");
                exception.printStackTrace();
                ARBShaderObjects.glDeleteObjectARB((int)i);
                return 0;
            }
        }
        if (saveFinalShaders) {
            Shaders.saveShader(filename, stringbuilder.toString());
        }
        ARBShaderObjects.glShaderSourceARB((int)i, (CharSequence)stringbuilder);
        ARBShaderObjects.glCompileShaderARB((int)i);
        if (GL20.glGetShaderi((int)i, (int)35713) != 1) {
            SMCLog.severe("Error compiling vertex shader: " + filename);
        }
        Shaders.printShaderLogInfo(i, filename, list);
        return i;
    }

    private static int createFragShader(String filename) {
        int i = ARBShaderObjects.glCreateShaderObjectARB((int)35632);
        if (i == 0) {
            return 0;
        }
        StringBuilder stringbuilder = new StringBuilder(131072);
        BufferedReader bufferedreader = null;
        try {
            bufferedreader = new BufferedReader(new InputStreamReader(shaderPack.getResourceAsStream(filename)));
        }
        catch (Exception var13) {
            try {
                bufferedreader = new BufferedReader(new FileReader(new File(filename)));
            }
            catch (Exception var12) {
                ARBShaderObjects.glDeleteObjectARB((int)i);
                return 0;
            }
        }
        ShaderOption[] ashaderoption = Shaders.getChangedOptions(shaderPackOptions);
        ArrayList<String> list = new ArrayList<String>();
        if (bufferedreader != null) {
            try {
                bufferedreader = ShaderPackParser.resolveIncludes(bufferedreader, filename, shaderPack, 0, list, 0);
                while (true) {
                    String s;
                    if ((s = bufferedreader.readLine()) == null) {
                        bufferedreader.close();
                        break;
                    }
                    s = Shaders.applyOptions(s, ashaderoption);
                    stringbuilder.append(s).append('\n');
                    if (s.matches("#version .*")) continue;
                    if (s.matches("uniform [ _a-zA-Z0-9]+ shadow;.*")) {
                        if (usedShadowDepthBuffers >= 1) continue;
                        usedShadowDepthBuffers = 1;
                        continue;
                    }
                    if (s.matches("uniform [ _a-zA-Z0-9]+ watershadow;.*")) {
                        waterShadowEnabled = true;
                        if (usedShadowDepthBuffers >= 2) continue;
                        usedShadowDepthBuffers = 2;
                        continue;
                    }
                    if (s.matches("uniform [ _a-zA-Z0-9]+ shadowtex0;.*")) {
                        if (usedShadowDepthBuffers >= 1) continue;
                        usedShadowDepthBuffers = 1;
                        continue;
                    }
                    if (s.matches("uniform [ _a-zA-Z0-9]+ shadowtex1;.*")) {
                        if (usedShadowDepthBuffers >= 2) continue;
                        usedShadowDepthBuffers = 2;
                        continue;
                    }
                    if (s.matches("uniform [ _a-zA-Z0-9]+ shadowcolor;.*")) {
                        if (usedShadowColorBuffers >= 1) continue;
                        usedShadowColorBuffers = 1;
                        continue;
                    }
                    if (s.matches("uniform [ _a-zA-Z0-9]+ shadowcolor0;.*")) {
                        if (usedShadowColorBuffers >= 1) continue;
                        usedShadowColorBuffers = 1;
                        continue;
                    }
                    if (s.matches("uniform [ _a-zA-Z0-9]+ shadowcolor1;.*")) {
                        if (usedShadowColorBuffers >= 2) continue;
                        usedShadowColorBuffers = 2;
                        continue;
                    }
                    if (s.matches("uniform [ _a-zA-Z0-9]+ depthtex0;.*")) {
                        if (usedDepthBuffers >= 1) continue;
                        usedDepthBuffers = 1;
                        continue;
                    }
                    if (s.matches("uniform [ _a-zA-Z0-9]+ depthtex1;.*")) {
                        if (usedDepthBuffers >= 2) continue;
                        usedDepthBuffers = 2;
                        continue;
                    }
                    if (s.matches("uniform [ _a-zA-Z0-9]+ depthtex2;.*")) {
                        if (usedDepthBuffers >= 3) continue;
                        usedDepthBuffers = 3;
                        continue;
                    }
                    if (s.matches("uniform [ _a-zA-Z0-9]+ gdepth;.*")) {
                        if (gbuffersFormat[1] != 6408) continue;
                        Shaders.gbuffersFormat[1] = 34836;
                        continue;
                    }
                    if (usedColorBuffers < 5 && s.matches("uniform [ _a-zA-Z0-9]+ gaux1;.*")) {
                        usedColorBuffers = 5;
                        continue;
                    }
                    if (usedColorBuffers < 6 && s.matches("uniform [ _a-zA-Z0-9]+ gaux2;.*")) {
                        usedColorBuffers = 6;
                        continue;
                    }
                    if (usedColorBuffers < 7 && s.matches("uniform [ _a-zA-Z0-9]+ gaux3;.*")) {
                        usedColorBuffers = 7;
                        continue;
                    }
                    if (usedColorBuffers < 8 && s.matches("uniform [ _a-zA-Z0-9]+ gaux4;.*")) {
                        usedColorBuffers = 8;
                        continue;
                    }
                    if (usedColorBuffers < 5 && s.matches("uniform [ _a-zA-Z0-9]+ colortex4;.*")) {
                        usedColorBuffers = 5;
                        continue;
                    }
                    if (usedColorBuffers < 6 && s.matches("uniform [ _a-zA-Z0-9]+ colortex5;.*")) {
                        usedColorBuffers = 6;
                        continue;
                    }
                    if (usedColorBuffers < 7 && s.matches("uniform [ _a-zA-Z0-9]+ colortex6;.*")) {
                        usedColorBuffers = 7;
                        continue;
                    }
                    if (usedColorBuffers < 8 && s.matches("uniform [ _a-zA-Z0-9]+ colortex7;.*")) {
                        usedColorBuffers = 8;
                        continue;
                    }
                    if (s.matches("uniform [ _a-zA-Z0-9]+ centerDepthSmooth;.*")) {
                        centerDepthSmoothEnabled = true;
                        continue;
                    }
                    if (s.matches("/\\* SHADOWRES:[0-9]+ \\*/.*")) {
                        String[] astring17 = s.split("(:| )", 4);
                        SMCLog.info("Shadow map resolution: " + astring17[2]);
                        spShadowMapWidth = spShadowMapHeight = Integer.parseInt(astring17[2]);
                        shadowMapWidth = shadowMapHeight = Math.round((float)spShadowMapWidth * configShadowResMul);
                        continue;
                    }
                    if (s.matches("[ \t]*const[ \t]*int[ \t]*shadowMapResolution[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                        String[] astring16 = s.split("(=[ \t]*|;)");
                        SMCLog.info("Shadow map resolution: " + astring16[1]);
                        spShadowMapWidth = spShadowMapHeight = Integer.parseInt(astring16[1]);
                        shadowMapWidth = shadowMapHeight = Math.round((float)spShadowMapWidth * configShadowResMul);
                        continue;
                    }
                    if (s.matches("/\\* SHADOWFOV:[0-9\\.]+ \\*/.*")) {
                        String[] astring15 = s.split("(:| )", 4);
                        SMCLog.info("Shadow map field of view: " + astring15[2]);
                        shadowMapFOV = Float.parseFloat(astring15[2]);
                        shadowMapIsOrtho = false;
                        continue;
                    }
                    if (s.matches("/\\* SHADOWHPL:[0-9\\.]+ \\*/.*")) {
                        String[] astring14 = s.split("(:| )", 4);
                        SMCLog.info("Shadow map half-plane: " + astring14[2]);
                        shadowMapHalfPlane = Float.parseFloat(astring14[2]);
                        shadowMapIsOrtho = true;
                        continue;
                    }
                    if (s.matches("[ \t]*const[ \t]*float[ \t]*shadowDistance[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                        String[] astring13 = s.split("(=[ \t]*|;)");
                        SMCLog.info("Shadow map distance: " + astring13[1]);
                        shadowMapHalfPlane = Float.parseFloat(astring13[1]);
                        shadowMapIsOrtho = true;
                        continue;
                    }
                    if (s.matches("[ \t]*const[ \t]*float[ \t]*shadowDistanceRenderMul[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                        String[] astring12 = s.split("(=[ \t]*|;)");
                        SMCLog.info("Shadow distance render mul: " + astring12[1]);
                        shadowDistanceRenderMul = Float.parseFloat(astring12[1]);
                        continue;
                    }
                    if (s.matches("[ \t]*const[ \t]*float[ \t]*shadowIntervalSize[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                        String[] astring11 = s.split("(=[ \t]*|;)");
                        SMCLog.info("Shadow map interval size: " + astring11[1]);
                        shadowIntervalSize = Float.parseFloat(astring11[1]);
                        continue;
                    }
                    if (s.matches("[ \t]*const[ \t]*bool[ \t]*generateShadowMipmap[ \t]*=[ \t]*true[ \t]*;.*")) {
                        SMCLog.info("Generate shadow mipmap");
                        Arrays.fill(shadowMipmapEnabled, true);
                        continue;
                    }
                    if (s.matches("[ \t]*const[ \t]*bool[ \t]*generateShadowColorMipmap[ \t]*=[ \t]*true[ \t]*;.*")) {
                        SMCLog.info("Generate shadow color mipmap");
                        Arrays.fill(shadowColorMipmapEnabled, true);
                        continue;
                    }
                    if (s.matches("[ \t]*const[ \t]*bool[ \t]*shadowHardwareFiltering[ \t]*=[ \t]*true[ \t]*;.*")) {
                        SMCLog.info("Hardware shadow filtering enabled.");
                        Arrays.fill(shadowHardwareFilteringEnabled, true);
                        continue;
                    }
                    if (s.matches("[ \t]*const[ \t]*bool[ \t]*shadowHardwareFiltering0[ \t]*=[ \t]*true[ \t]*;.*")) {
                        SMCLog.info("shadowHardwareFiltering0");
                        Shaders.shadowHardwareFilteringEnabled[0] = true;
                        continue;
                    }
                    if (s.matches("[ \t]*const[ \t]*bool[ \t]*shadowHardwareFiltering1[ \t]*=[ \t]*true[ \t]*;.*")) {
                        SMCLog.info("shadowHardwareFiltering1");
                        Shaders.shadowHardwareFilteringEnabled[1] = true;
                        continue;
                    }
                    if (s.matches("[ \t]*const[ \t]*bool[ \t]*(shadowtex0Mipmap|shadowtexMipmap)[ \t]*=[ \t]*true[ \t]*;.*")) {
                        SMCLog.info("shadowtex0Mipmap");
                        Shaders.shadowMipmapEnabled[0] = true;
                        continue;
                    }
                    if (s.matches("[ \t]*const[ \t]*bool[ \t]*(shadowtex1Mipmap)[ \t]*=[ \t]*true[ \t]*;.*")) {
                        SMCLog.info("shadowtex1Mipmap");
                        Shaders.shadowMipmapEnabled[1] = true;
                        continue;
                    }
                    if (s.matches("[ \t]*const[ \t]*bool[ \t]*(shadowcolor0Mipmap|shadowColor0Mipmap)[ \t]*=[ \t]*true[ \t]*;.*")) {
                        SMCLog.info("shadowcolor0Mipmap");
                        Shaders.shadowColorMipmapEnabled[0] = true;
                        continue;
                    }
                    if (s.matches("[ \t]*const[ \t]*bool[ \t]*(shadowcolor1Mipmap|shadowColor1Mipmap)[ \t]*=[ \t]*true[ \t]*;.*")) {
                        SMCLog.info("shadowcolor1Mipmap");
                        Shaders.shadowColorMipmapEnabled[1] = true;
                        continue;
                    }
                    if (s.matches("[ \t]*const[ \t]*bool[ \t]*(shadowtex0Nearest|shadowtexNearest|shadow0MinMagNearest)[ \t]*=[ \t]*true[ \t]*;.*")) {
                        SMCLog.info("shadowtex0Nearest");
                        Shaders.shadowFilterNearest[0] = true;
                        continue;
                    }
                    if (s.matches("[ \t]*const[ \t]*bool[ \t]*(shadowtex1Nearest|shadow1MinMagNearest)[ \t]*=[ \t]*true[ \t]*;.*")) {
                        SMCLog.info("shadowtex1Nearest");
                        Shaders.shadowFilterNearest[1] = true;
                        continue;
                    }
                    if (s.matches("[ \t]*const[ \t]*bool[ \t]*(shadowcolor0Nearest|shadowColor0Nearest|shadowColor0MinMagNearest)[ \t]*=[ \t]*true[ \t]*;.*")) {
                        SMCLog.info("shadowcolor0Nearest");
                        Shaders.shadowColorFilterNearest[0] = true;
                        continue;
                    }
                    if (s.matches("[ \t]*const[ \t]*bool[ \t]*(shadowcolor1Nearest|shadowColor1Nearest|shadowColor1MinMagNearest)[ \t]*=[ \t]*true[ \t]*;.*")) {
                        SMCLog.info("shadowcolor1Nearest");
                        Shaders.shadowColorFilterNearest[1] = true;
                        continue;
                    }
                    if (s.matches("/\\* WETNESSHL:[0-9\\.]+ \\*/.*")) {
                        String[] astring10 = s.split("(:| )", 4);
                        SMCLog.info("Wetness halflife: " + astring10[2]);
                        wetnessHalfLife = Float.parseFloat(astring10[2]);
                        continue;
                    }
                    if (s.matches("[ \t]*const[ \t]*float[ \t]*wetnessHalflife[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                        String[] astring9 = s.split("(=[ \t]*|;)");
                        SMCLog.info("Wetness halflife: " + astring9[1]);
                        wetnessHalfLife = Float.parseFloat(astring9[1]);
                        continue;
                    }
                    if (s.matches("/\\* DRYNESSHL:[0-9\\.]+ \\*/.*")) {
                        String[] astring8 = s.split("(:| )", 4);
                        SMCLog.info("Dryness halflife: " + astring8[2]);
                        drynessHalfLife = Float.parseFloat(astring8[2]);
                        continue;
                    }
                    if (s.matches("[ \t]*const[ \t]*float[ \t]*drynessHalflife[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                        String[] astring7 = s.split("(=[ \t]*|;)");
                        SMCLog.info("Dryness halflife: " + astring7[1]);
                        drynessHalfLife = Float.parseFloat(astring7[1]);
                        continue;
                    }
                    if (s.matches("[ \t]*const[ \t]*float[ \t]*eyeBrightnessHalflife[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                        String[] astring6 = s.split("(=[ \t]*|;)");
                        SMCLog.info("Eye brightness halflife: " + astring6[1]);
                        eyeBrightnessHalflife = Float.parseFloat(astring6[1]);
                        continue;
                    }
                    if (s.matches("[ \t]*const[ \t]*float[ \t]*centerDepthHalflife[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                        String[] astring5 = s.split("(=[ \t]*|;)");
                        SMCLog.info("Center depth halflife: " + astring5[1]);
                        centerDepthSmoothHalflife = Float.parseFloat(astring5[1]);
                        continue;
                    }
                    if (s.matches("[ \t]*const[ \t]*float[ \t]*sunPathRotation[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                        String[] astring4 = s.split("(=[ \t]*|;)");
                        SMCLog.info("Sun path rotation: " + astring4[1]);
                        sunPathRotation = Float.parseFloat(astring4[1]);
                        continue;
                    }
                    if (s.matches("[ \t]*const[ \t]*float[ \t]*ambientOcclusionLevel[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                        String[] astring3 = s.split("(=[ \t]*|;)");
                        SMCLog.info("AO Level: " + astring3[1]);
                        aoLevel = Config.limit(Float.parseFloat(astring3[1]), 0.0f, 1.0f);
                        continue;
                    }
                    if (s.matches("[ \t]*const[ \t]*int[ \t]*superSamplingLevel[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                        String[] astring2 = s.split("(=[ \t]*|;)");
                        int i1 = Integer.parseInt(astring2[1]);
                        if (i1 > 1) {
                            SMCLog.info("Super sampling level: " + i1 + "x");
                            superSamplingLevel = i1;
                            continue;
                        }
                        superSamplingLevel = 1;
                        continue;
                    }
                    if (s.matches("[ \t]*const[ \t]*int[ \t]*noiseTextureResolution[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                        String[] astring1 = s.split("(=[ \t]*|;)");
                        SMCLog.info("Noise texture enabled");
                        SMCLog.info("Noise texture resolution: " + astring1[1]);
                        noiseTextureResolution = Integer.parseInt(astring1[1]);
                        noiseTextureEnabled = true;
                        continue;
                    }
                    if (s.matches("[ \t]*const[ \t]*int[ \t]*\\w+Format[ \t]*=[ \t]*[RGBA0123456789FUI_SNORM]*[ \t]*;.*")) {
                        Matcher matcher2 = gbufferFormatPattern.matcher(s);
                        matcher2.matches();
                        String s3 = matcher2.group(1);
                        String s4 = matcher2.group(2);
                        int k = Shaders.getBufferIndexFromString(s3);
                        int l = Shaders.getTextureFormatFromString(s4);
                        if (k < 0 || l == 0) continue;
                        Shaders.gbuffersFormat[k] = l;
                        SMCLog.info("%s format: %s", s3, s4);
                        continue;
                    }
                    if (s.matches("[ \t]*const[ \t]*bool[ \t]*\\w+Clear[ \t]*=[ \t]*false[ \t]*;.*")) {
                        if (!filename.matches(".*composite[0-9]?.fsh")) continue;
                        Matcher matcher1 = gbufferClearPattern.matcher(s);
                        matcher1.matches();
                        String s2 = matcher1.group(1);
                        int j1 = Shaders.getBufferIndexFromString(s2);
                        if (j1 < 0) continue;
                        Shaders.gbuffersClear[j1] = false;
                        SMCLog.info("%s clear disabled", s2);
                        continue;
                    }
                    if (s.matches("/\\* GAUX4FORMAT:RGBA32F \\*/.*")) {
                        SMCLog.info("gaux4 format : RGB32AF");
                        Shaders.gbuffersFormat[7] = 34836;
                        continue;
                    }
                    if (s.matches("/\\* GAUX4FORMAT:RGB32F \\*/.*")) {
                        SMCLog.info("gaux4 format : RGB32F");
                        Shaders.gbuffersFormat[7] = 34837;
                        continue;
                    }
                    if (s.matches("/\\* GAUX4FORMAT:RGB16 \\*/.*")) {
                        SMCLog.info("gaux4 format : RGB16");
                        Shaders.gbuffersFormat[7] = 32852;
                        continue;
                    }
                    if (s.matches("[ \t]*const[ \t]*bool[ \t]*\\w+MipmapEnabled[ \t]*=[ \t]*true[ \t]*;.*")) {
                        if (!filename.matches(".*composite[0-9]?.fsh") && !filename.matches(".*final.fsh")) continue;
                        Matcher matcher = gbufferMipmapEnabledPattern.matcher(s);
                        matcher.matches();
                        String s1 = matcher.group(1);
                        int j = Shaders.getBufferIndexFromString(s1);
                        if (j < 0) continue;
                        newCompositeMipmapSetting |= 1 << j;
                        SMCLog.info("%s mipmap enabled", s1);
                        continue;
                    }
                    if (!s.matches("/\\* DRAWBUFFERS:[0-7N]* \\*/.*")) continue;
                    String[] astring = s.split("(:| )", 4);
                    newDrawBufSetting = astring[2];
                }
            }
            catch (Exception exception) {
                SMCLog.severe("Couldn't read " + filename + "!");
                exception.printStackTrace();
                ARBShaderObjects.glDeleteObjectARB((int)i);
                return 0;
            }
        }
        if (saveFinalShaders) {
            Shaders.saveShader(filename, stringbuilder.toString());
        }
        ARBShaderObjects.glShaderSourceARB((int)i, (CharSequence)stringbuilder);
        ARBShaderObjects.glCompileShaderARB((int)i);
        if (GL20.glGetShaderi((int)i, (int)35713) != 1) {
            SMCLog.severe("Error compiling fragment shader: " + filename);
        }
        Shaders.printShaderLogInfo(i, filename, list);
        return i;
    }

    private static void saveShader(String filename, String code) {
        try {
            File file1 = new File(shaderpacksdir, "debug/" + filename);
            file1.getParentFile().mkdirs();
            Config.writeFile(file1, code);
        }
        catch (IOException ioexception) {
            Config.warn("Error saving: " + filename);
            ioexception.printStackTrace();
        }
    }

    private static void clearDirectory(File dir) {
        File[] afile;
        if (dir.exists() && dir.isDirectory() && (afile = dir.listFiles()) != null) {
            int i = 0;
            while (i < afile.length) {
                File file1 = afile[i];
                if (file1.isDirectory()) {
                    Shaders.clearDirectory(file1);
                }
                file1.delete();
                ++i;
            }
        }
    }

    private static boolean printLogInfo(int obj, String name) {
        IntBuffer intbuffer = BufferUtils.createIntBuffer((int)1);
        ARBShaderObjects.glGetObjectParameterARB((int)obj, (int)35716, (IntBuffer)intbuffer);
        int i = intbuffer.get();
        if (i > 1) {
            ByteBuffer bytebuffer = BufferUtils.createByteBuffer((int)i);
            intbuffer.flip();
            ARBShaderObjects.glGetInfoLogARB((int)obj, (IntBuffer)intbuffer, (ByteBuffer)bytebuffer);
            byte[] abyte = new byte[i];
            bytebuffer.get(abyte);
            if (abyte[i - 1] == 0) {
                abyte[i - 1] = 10;
            }
            String s = new String(abyte);
            SMCLog.info("Info log: " + name + "\n" + s);
            return false;
        }
        return true;
    }

    private static boolean printShaderLogInfo(int shader, String name, List<String> listFiles) {
        IntBuffer intbuffer = BufferUtils.createIntBuffer((int)1);
        int i = GL20.glGetShaderi((int)shader, (int)35716);
        if (i <= 1) {
            return true;
        }
        int j = 0;
        while (j < listFiles.size()) {
            String s = listFiles.get(j);
            SMCLog.info("File: " + (j + 1) + " = " + s);
            ++j;
        }
        String s1 = GL20.glGetShaderInfoLog((int)shader, (int)i);
        SMCLog.info("Shader info log: " + name + "\n" + s1);
        return false;
    }

    public static void setDrawBuffers(IntBuffer drawBuffers) {
        if (drawBuffers == null) {
            drawBuffers = drawBuffersNone;
        }
        if (activeDrawBuffers != drawBuffers) {
            activeDrawBuffers = drawBuffers;
            GL20.glDrawBuffers((IntBuffer)drawBuffers);
        }
    }

    public static void useProgram(int program) {
        Shaders.checkGLError("pre-useProgram");
        if (isShadowPass) {
            program = 30;
            if (programsID[30] == 0) {
                normalMapEnabled = false;
                return;
            }
        }
        if (activeProgram != program) {
            activeProgram = program;
            ARBShaderObjects.glUseProgramObjectARB((int)programsID[program]);
            if (programsID[program] == 0) {
                normalMapEnabled = false;
            } else {
                int l;
                if (Shaders.checkGLError("useProgram ", programNames[program]) != 0) {
                    Shaders.programsID[program] = 0;
                }
                IntBuffer intbuffer = programsDrawBuffers[program];
                if (isRenderingDfb) {
                    Shaders.setDrawBuffers(intbuffer);
                    Shaders.checkGLError(programNames[program], " draw buffers = ", programsDrawBufSettings[program]);
                }
                activeCompositeMipmapSetting = programsCompositeMipmapSetting[program];
                uniformEntityColor.setProgram(programsID[activeProgram]);
                uniformEntityId.setProgram(programsID[activeProgram]);
                uniformBlockEntityId.setProgram(programsID[activeProgram]);
                switch (program) {
                    case 1: 
                    case 2: 
                    case 3: 
                    case 4: 
                    case 5: 
                    case 6: 
                    case 7: 
                    case 8: 
                    case 9: 
                    case 10: 
                    case 11: 
                    case 12: 
                    case 13: 
                    case 16: 
                    case 18: 
                    case 19: 
                    case 20: {
                        normalMapEnabled = true;
                        Shaders.setProgramUniform1i("texture", 0);
                        Shaders.setProgramUniform1i("lightmap", 1);
                        Shaders.setProgramUniform1i("normals", 2);
                        Shaders.setProgramUniform1i("specular", 3);
                        Shaders.setProgramUniform1i("shadow", waterShadowEnabled ? 5 : 4);
                        Shaders.setProgramUniform1i("watershadow", 4);
                        Shaders.setProgramUniform1i("shadowtex0", 4);
                        Shaders.setProgramUniform1i("shadowtex1", 5);
                        Shaders.setProgramUniform1i("depthtex0", 6);
                        if (customTexturesGbuffers != null) {
                            Shaders.setProgramUniform1i("gaux1", 7);
                            Shaders.setProgramUniform1i("gaux2", 8);
                            Shaders.setProgramUniform1i("gaux3", 9);
                            Shaders.setProgramUniform1i("gaux4", 10);
                        }
                        Shaders.setProgramUniform1i("depthtex1", 12);
                        Shaders.setProgramUniform1i("shadowcolor", 13);
                        Shaders.setProgramUniform1i("shadowcolor0", 13);
                        Shaders.setProgramUniform1i("shadowcolor1", 14);
                        Shaders.setProgramUniform1i("noisetex", 15);
                        break;
                    }
                    default: {
                        normalMapEnabled = false;
                        break;
                    }
                    case 21: 
                    case 22: 
                    case 23: 
                    case 24: 
                    case 25: 
                    case 26: 
                    case 27: 
                    case 28: 
                    case 29: {
                        normalMapEnabled = false;
                        Shaders.setProgramUniform1i("gcolor", 0);
                        Shaders.setProgramUniform1i("gdepth", 1);
                        Shaders.setProgramUniform1i("gnormal", 2);
                        Shaders.setProgramUniform1i("composite", 3);
                        Shaders.setProgramUniform1i("gaux1", 7);
                        Shaders.setProgramUniform1i("gaux2", 8);
                        Shaders.setProgramUniform1i("gaux3", 9);
                        Shaders.setProgramUniform1i("gaux4", 10);
                        Shaders.setProgramUniform1i("colortex0", 0);
                        Shaders.setProgramUniform1i("colortex1", 1);
                        Shaders.setProgramUniform1i("colortex2", 2);
                        Shaders.setProgramUniform1i("colortex3", 3);
                        Shaders.setProgramUniform1i("colortex4", 7);
                        Shaders.setProgramUniform1i("colortex5", 8);
                        Shaders.setProgramUniform1i("colortex6", 9);
                        Shaders.setProgramUniform1i("colortex7", 10);
                        Shaders.setProgramUniform1i("shadow", waterShadowEnabled ? 5 : 4);
                        Shaders.setProgramUniform1i("watershadow", 4);
                        Shaders.setProgramUniform1i("shadowtex0", 4);
                        Shaders.setProgramUniform1i("shadowtex1", 5);
                        Shaders.setProgramUniform1i("gdepthtex", 6);
                        Shaders.setProgramUniform1i("depthtex0", 6);
                        Shaders.setProgramUniform1i("depthtex1", 11);
                        Shaders.setProgramUniform1i("depthtex2", 12);
                        Shaders.setProgramUniform1i("shadowcolor", 13);
                        Shaders.setProgramUniform1i("shadowcolor0", 13);
                        Shaders.setProgramUniform1i("shadowcolor1", 14);
                        Shaders.setProgramUniform1i("noisetex", 15);
                        break;
                    }
                    case 30: 
                    case 31: 
                    case 32: {
                        Shaders.setProgramUniform1i("tex", 0);
                        Shaders.setProgramUniform1i("texture", 0);
                        Shaders.setProgramUniform1i("lightmap", 1);
                        Shaders.setProgramUniform1i("normals", 2);
                        Shaders.setProgramUniform1i("specular", 3);
                        Shaders.setProgramUniform1i("shadow", waterShadowEnabled ? 5 : 4);
                        Shaders.setProgramUniform1i("watershadow", 4);
                        Shaders.setProgramUniform1i("shadowtex0", 4);
                        Shaders.setProgramUniform1i("shadowtex1", 5);
                        if (customTexturesGbuffers != null) {
                            Shaders.setProgramUniform1i("gaux1", 7);
                            Shaders.setProgramUniform1i("gaux2", 8);
                            Shaders.setProgramUniform1i("gaux3", 9);
                            Shaders.setProgramUniform1i("gaux4", 10);
                        }
                        Shaders.setProgramUniform1i("shadowcolor", 13);
                        Shaders.setProgramUniform1i("shadowcolor0", 13);
                        Shaders.setProgramUniform1i("shadowcolor1", 14);
                        Shaders.setProgramUniform1i("noisetex", 15);
                    }
                }
                ItemStack itemstack = Shaders.mc.player != null ? Shaders.mc.player.getHeldItemMainhand() : null;
                Item item = itemstack != null ? itemstack.getItem() : null;
                int i = -1;
                Block block = null;
                if (item != null) {
                    i = Item.REGISTRY.getIDForObject(item);
                    block = Block.REGISTRY.getObjectById(i);
                }
                int j = block != null ? block.getLightValue(block.getDefaultState()) : 0;
                ItemStack itemstack1 = Shaders.mc.player != null ? Shaders.mc.player.getHeldItemOffhand() : null;
                Item item1 = itemstack1 != null ? itemstack1.getItem() : null;
                int k = -1;
                Block block1 = null;
                if (item1 != null) {
                    k = Item.REGISTRY.getIDForObject(item1);
                    block1 = Block.REGISTRY.getObjectById(k);
                }
                int n = l = block1 != null ? block1.getLightValue(block1.getDefaultState()) : 0;
                if (Shaders.isOldHandLight() && l > j) {
                    i = k;
                    j = l;
                }
                Shaders.setProgramUniform1i("heldItemId", i);
                Shaders.setProgramUniform1i("heldBlockLightValue", j);
                Shaders.setProgramUniform1i("heldItemId2", k);
                Shaders.setProgramUniform1i("heldBlockLightValue2", l);
                Shaders.setProgramUniform1i("fogMode", fogEnabled ? fogMode : 0);
                Shaders.setProgramUniform3f("fogColor", fogColorR, fogColorG, fogColorB);
                Shaders.setProgramUniform3f("skyColor", skyColorR, skyColorG, skyColorB);
                Shaders.setProgramUniform1i("worldTime", (int)(worldTime % 24000L));
                Shaders.setProgramUniform1i("worldDay", (int)(worldTime / 24000L));
                Shaders.setProgramUniform1i("moonPhase", moonPhase);
                Shaders.setProgramUniform1i("frameCounter", frameCounter);
                Shaders.setProgramUniform1f("frameTime", frameTime);
                Shaders.setProgramUniform1f("frameTimeCounter", frameTimeCounter);
                Shaders.setProgramUniform1f("sunAngle", sunAngle);
                Shaders.setProgramUniform1f("shadowAngle", shadowAngle);
                Shaders.setProgramUniform1f("rainStrength", rainStrength);
                Shaders.setProgramUniform1f("aspectRatio", (float)renderWidth / (float)renderHeight);
                Shaders.setProgramUniform1f("viewWidth", renderWidth);
                Shaders.setProgramUniform1f("viewHeight", renderHeight);
                Shaders.setProgramUniform1f("near", 0.05f);
                Shaders.setProgramUniform1f("far", Shaders.mc.gameSettings.renderDistanceChunks * 16);
                Shaders.setProgramUniform3f("sunPosition", sunPosition[0], sunPosition[1], sunPosition[2]);
                Shaders.setProgramUniform3f("moonPosition", moonPosition[0], moonPosition[1], moonPosition[2]);
                Shaders.setProgramUniform3f("shadowLightPosition", shadowLightPosition[0], shadowLightPosition[1], shadowLightPosition[2]);
                Shaders.setProgramUniform3f("upPosition", upPosition[0], upPosition[1], upPosition[2]);
                Shaders.setProgramUniform3f("previousCameraPosition", (float)previousCameraPositionX, (float)previousCameraPositionY, (float)previousCameraPositionZ);
                Shaders.setProgramUniform3f("cameraPosition", (float)cameraPositionX, (float)cameraPositionY, (float)cameraPositionZ);
                Shaders.setProgramUniformMatrix4ARB("gbufferModelView", false, modelView);
                Shaders.setProgramUniformMatrix4ARB("gbufferModelViewInverse", false, modelViewInverse);
                Shaders.setProgramUniformMatrix4ARB("gbufferPreviousProjection", false, previousProjection);
                Shaders.setProgramUniformMatrix4ARB("gbufferProjection", false, projection);
                Shaders.setProgramUniformMatrix4ARB("gbufferProjectionInverse", false, projectionInverse);
                Shaders.setProgramUniformMatrix4ARB("gbufferPreviousModelView", false, previousModelView);
                if (usedShadowDepthBuffers > 0) {
                    Shaders.setProgramUniformMatrix4ARB("shadowProjection", false, shadowProjection);
                    Shaders.setProgramUniformMatrix4ARB("shadowProjectionInverse", false, shadowProjectionInverse);
                    Shaders.setProgramUniformMatrix4ARB("shadowModelView", false, shadowModelView);
                    Shaders.setProgramUniformMatrix4ARB("shadowModelViewInverse", false, shadowModelViewInverse);
                }
                Shaders.setProgramUniform1f("wetness", wetness);
                Shaders.setProgramUniform1f("eyeAltitude", eyePosY);
                Shaders.setProgramUniform2i("eyeBrightness", eyeBrightness & 0xFFFF, eyeBrightness >> 16);
                Shaders.setProgramUniform2i("eyeBrightnessSmooth", Math.round(eyeBrightnessFadeX), Math.round(eyeBrightnessFadeY));
                Shaders.setProgramUniform2i("terrainTextureSize", terrainTextureSize[0], terrainTextureSize[1]);
                Shaders.setProgramUniform1i("terrainIconSize", terrainIconSize);
                Shaders.setProgramUniform1i("isEyeInWater", isEyeInWater);
                Shaders.setProgramUniform1f("nightVision", nightVision);
                Shaders.setProgramUniform1f("blindness", blindness);
                Shaders.setProgramUniform1f("screenBrightness", Shaders.mc.gameSettings.gammaSetting);
                Shaders.setProgramUniform1i("hideGUI", Shaders.mc.gameSettings.hideGUI ? 1 : 0);
                Shaders.setProgramUniform1f("centerDepthSmooth", centerDepthSmooth);
                Shaders.setProgramUniform2i("atlasSize", atlasSizeX, atlasSizeY);
                Shaders.checkGLError("useProgram ", programNames[program]);
            }
        }
    }

    public static void setProgramUniform1i(String name, int x) {
        int i = programsID[activeProgram];
        if (i != 0) {
            int j = ARBShaderObjects.glGetUniformLocationARB((int)i, (CharSequence)name);
            ARBShaderObjects.glUniform1iARB((int)j, (int)x);
            Shaders.checkGLError(programNames[activeProgram], name);
        }
    }

    public static void setProgramUniform2i(String name, int x, int y) {
        int i = programsID[activeProgram];
        if (i != 0) {
            int j = ARBShaderObjects.glGetUniformLocationARB((int)i, (CharSequence)name);
            ARBShaderObjects.glUniform2iARB((int)j, (int)x, (int)y);
            Shaders.checkGLError(programNames[activeProgram], name);
        }
    }

    public static void setProgramUniform1f(String name, float x) {
        int i = programsID[activeProgram];
        if (i != 0) {
            int j = ARBShaderObjects.glGetUniformLocationARB((int)i, (CharSequence)name);
            ARBShaderObjects.glUniform1fARB((int)j, (float)x);
            Shaders.checkGLError(programNames[activeProgram], name);
        }
    }

    public static void setProgramUniform3f(String name, float x, float y, float z) {
        int i = programsID[activeProgram];
        if (i != 0) {
            int j = ARBShaderObjects.glGetUniformLocationARB((int)i, (CharSequence)name);
            ARBShaderObjects.glUniform3fARB((int)j, (float)x, (float)y, (float)z);
            Shaders.checkGLError(programNames[activeProgram], name);
        }
    }

    public static void setProgramUniformMatrix4ARB(String name, boolean transpose, FloatBuffer matrix) {
        int i = programsID[activeProgram];
        if (i != 0 && matrix != null) {
            int j = ARBShaderObjects.glGetUniformLocationARB((int)i, (CharSequence)name);
            ARBShaderObjects.glUniformMatrix4ARB((int)j, (boolean)transpose, (FloatBuffer)matrix);
            Shaders.checkGLError(programNames[activeProgram], name);
        }
    }

    private static int getBufferIndexFromString(String name) {
        if (!name.equals("colortex0") && !name.equals("gcolor")) {
            if (!name.equals("colortex1") && !name.equals("gdepth")) {
                if (!name.equals("colortex2") && !name.equals("gnormal")) {
                    if (!name.equals("colortex3") && !name.equals("composite")) {
                        if (!name.equals("colortex4") && !name.equals("gaux1")) {
                            if (!name.equals("colortex5") && !name.equals("gaux2")) {
                                if (!name.equals("colortex6") && !name.equals("gaux3")) {
                                    return !name.equals("colortex7") && !name.equals("gaux4") ? -1 : 7;
                                }
                                return 6;
                            }
                            return 5;
                        }
                        return 4;
                    }
                    return 3;
                }
                return 2;
            }
            return 1;
        }
        return 0;
    }

    private static int getTextureFormatFromString(String par) {
        par = par.trim();
        int i = 0;
        while (i < formatNames.length) {
            String s = formatNames[i];
            if (par.equals(s)) {
                return formatIds[i];
            }
            ++i;
        }
        return 0;
    }

    private static void setupNoiseTexture() {
        if (noiseTexture == null) {
            noiseTexture = new HFNoiseTexture(noiseTextureResolution, noiseTextureResolution);
        }
    }

    private static void loadEntityDataMap() {
        mapBlockToEntityData = new IdentityHashMap<Block, Integer>(300);
        if (mapBlockToEntityData.isEmpty()) {
            for (ResourceLocation resourcelocation : Block.REGISTRY.getKeys()) {
                Block block = Block.REGISTRY.getObject(resourcelocation);
                int i = Block.REGISTRY.getIDForObject(block);
                mapBlockToEntityData.put(block, i);
            }
        }
        BufferedReader bufferedreader = null;
        try {
            bufferedreader = new BufferedReader(new InputStreamReader(shaderPack.getResourceAsStream("/mc_Entity_x.txt")));
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (bufferedreader != null) {
            try {
                String s1;
                while ((s1 = bufferedreader.readLine()) != null) {
                    Matcher matcher = patternLoadEntityDataMap.matcher(s1);
                    if (matcher.matches()) {
                        String s2 = matcher.group(1);
                        String s = matcher.group(2);
                        int j = Integer.parseInt(s);
                        Block block1 = Block.getBlockFromName(s2);
                        if (block1 != null) {
                            mapBlockToEntityData.put(block1, j);
                            continue;
                        }
                        SMCLog.warning("Unknown block name %s", s2);
                        continue;
                    }
                    SMCLog.warning("unmatched %s\n", s1);
                }
            }
            catch (Exception var9) {
                SMCLog.warning("Error parsing mc_Entity_x.txt");
            }
        }
        if (bufferedreader != null) {
            try {
                bufferedreader.close();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    private static IntBuffer fillIntBufferZero(IntBuffer buf) {
        int i = buf.limit();
        int j = buf.position();
        while (j < i) {
            buf.put(j, 0);
            ++j;
        }
        return buf;
    }

    public static void uninit() {
        if (isShaderPackInitialized) {
            Shaders.checkGLError("Shaders.uninit pre");
            int i = 0;
            while (i < 33) {
                if (programsRef[i] != 0) {
                    ARBShaderObjects.glDeleteObjectARB((int)programsRef[i]);
                    Shaders.checkGLError("del programRef");
                }
                Shaders.programsRef[i] = 0;
                Shaders.programsID[i] = 0;
                Shaders.programsDrawBufSettings[i] = null;
                Shaders.programsDrawBuffers[i] = null;
                Shaders.programsCompositeMipmapSetting[i] = 0;
                ++i;
            }
            if (dfb != 0) {
                EXTFramebufferObject.glDeleteFramebuffersEXT((int)dfb);
                dfb = 0;
                Shaders.checkGLError("del dfb");
            }
            if (sfb != 0) {
                EXTFramebufferObject.glDeleteFramebuffersEXT((int)sfb);
                sfb = 0;
                Shaders.checkGLError("del sfb");
            }
            if (dfbDepthTextures != null) {
                GlStateManager.deleteTextures(dfbDepthTextures);
                Shaders.fillIntBufferZero(dfbDepthTextures);
                Shaders.checkGLError("del dfbDepthTextures");
            }
            if (dfbColorTextures != null) {
                GlStateManager.deleteTextures(dfbColorTextures);
                Shaders.fillIntBufferZero(dfbColorTextures);
                Shaders.checkGLError("del dfbTextures");
            }
            if (sfbDepthTextures != null) {
                GlStateManager.deleteTextures(sfbDepthTextures);
                Shaders.fillIntBufferZero(sfbDepthTextures);
                Shaders.checkGLError("del shadow depth");
            }
            if (sfbColorTextures != null) {
                GlStateManager.deleteTextures(sfbColorTextures);
                Shaders.fillIntBufferZero(sfbColorTextures);
                Shaders.checkGLError("del shadow color");
            }
            if (dfbDrawBuffers != null) {
                Shaders.fillIntBufferZero(dfbDrawBuffers);
            }
            if (noiseTexture != null) {
                noiseTexture.destroy();
                noiseTexture = null;
            }
            SMCLog.info("Uninit");
            shadowPassInterval = 0;
            shouldSkipDefaultShadow = false;
            isShaderPackInitialized = false;
            Shaders.checkGLError("Shaders.uninit");
        }
    }

    public static void scheduleResize() {
        renderDisplayHeight = 0;
    }

    public static void scheduleResizeShadow() {
        needResizeShadow = true;
    }

    private static void resize() {
        renderDisplayWidth = Shaders.mc.displayWidth;
        renderDisplayHeight = Shaders.mc.displayHeight;
        renderWidth = Math.round((float)renderDisplayWidth * configRenderResMul);
        renderHeight = Math.round((float)renderDisplayHeight * configRenderResMul);
        Shaders.setupFrameBuffer();
    }

    private static void resizeShadow() {
        needResizeShadow = false;
        shadowMapWidth = Math.round((float)spShadowMapWidth * configShadowResMul);
        shadowMapHeight = Math.round((float)spShadowMapHeight * configShadowResMul);
        Shaders.setupShadowFrameBuffer();
    }

    private static void setupFrameBuffer() {
        if (dfb != 0) {
            EXTFramebufferObject.glDeleteFramebuffersEXT((int)dfb);
            GlStateManager.deleteTextures(dfbDepthTextures);
            GlStateManager.deleteTextures(dfbColorTextures);
        }
        dfb = EXTFramebufferObject.glGenFramebuffersEXT();
        GL11.glGenTextures((IntBuffer)((IntBuffer)dfbDepthTextures.clear().limit(usedDepthBuffers)));
        GL11.glGenTextures((IntBuffer)((IntBuffer)dfbColorTextures.clear().limit(16)));
        dfbDepthTextures.position(0);
        dfbColorTextures.position(0);
        dfbColorTextures.get(dfbColorTexturesA).position(0);
        EXTFramebufferObject.glBindFramebufferEXT((int)36160, (int)dfb);
        GL20.glDrawBuffers((int)0);
        GL11.glReadBuffer((int)0);
        int i = 0;
        while (i < usedDepthBuffers) {
            GlStateManager.bindTexture(dfbDepthTextures.get(i));
            GL11.glTexParameteri((int)3553, (int)10242, (int)10496);
            GL11.glTexParameteri((int)3553, (int)10243, (int)10496);
            GL11.glTexParameteri((int)3553, (int)10241, (int)9728);
            GL11.glTexParameteri((int)3553, (int)10240, (int)9728);
            GL11.glTexParameteri((int)3553, (int)34891, (int)6409);
            GL11.glTexImage2D((int)3553, (int)0, (int)6402, (int)renderWidth, (int)renderHeight, (int)0, (int)6402, (int)5126, null);
            ++i;
        }
        EXTFramebufferObject.glFramebufferTexture2DEXT((int)36160, (int)36096, (int)3553, (int)dfbDepthTextures.get(0), (int)0);
        GL20.glDrawBuffers((IntBuffer)dfbDrawBuffers);
        GL11.glReadBuffer((int)0);
        Shaders.checkGLError("FT d");
        int k = 0;
        while (k < usedColorBuffers) {
            GlStateManager.bindTexture(dfbColorTexturesA[k]);
            GL11.glTexParameteri((int)3553, (int)10242, (int)10496);
            GL11.glTexParameteri((int)3553, (int)10243, (int)10496);
            GL11.glTexParameteri((int)3553, (int)10241, (int)9729);
            GL11.glTexParameteri((int)3553, (int)10240, (int)9729);
            GL11.glTexImage2D((int)3553, (int)0, (int)gbuffersFormat[k], (int)renderWidth, (int)renderHeight, (int)0, (int)32993, (int)33639, null);
            EXTFramebufferObject.glFramebufferTexture2DEXT((int)36160, (int)(36064 + k), (int)3553, (int)dfbColorTexturesA[k], (int)0);
            Shaders.checkGLError("FT c");
            ++k;
        }
        int l = 0;
        while (l < usedColorBuffers) {
            GlStateManager.bindTexture(dfbColorTexturesA[8 + l]);
            GL11.glTexParameteri((int)3553, (int)10242, (int)10496);
            GL11.glTexParameteri((int)3553, (int)10243, (int)10496);
            GL11.glTexParameteri((int)3553, (int)10241, (int)9729);
            GL11.glTexParameteri((int)3553, (int)10240, (int)9729);
            GL11.glTexImage2D((int)3553, (int)0, (int)gbuffersFormat[l], (int)renderWidth, (int)renderHeight, (int)0, (int)32993, (int)33639, null);
            Shaders.checkGLError("FT ca");
            ++l;
        }
        int i1 = EXTFramebufferObject.glCheckFramebufferStatusEXT((int)36160);
        if (i1 == 36058) {
            Shaders.printChatAndLogError("[Shaders] Error: Failed framebuffer incomplete formats");
            int j = 0;
            while (j < usedColorBuffers) {
                GlStateManager.bindTexture(dfbColorTextures.get(j));
                GL11.glTexImage2D((int)3553, (int)0, (int)6408, (int)renderWidth, (int)renderHeight, (int)0, (int)32993, (int)33639, null);
                EXTFramebufferObject.glFramebufferTexture2DEXT((int)36160, (int)(36064 + j), (int)3553, (int)dfbColorTextures.get(j), (int)0);
                Shaders.checkGLError("FT c");
                ++j;
            }
            i1 = EXTFramebufferObject.glCheckFramebufferStatusEXT((int)36160);
            if (i1 == 36053) {
                SMCLog.info("complete");
            }
        }
        GlStateManager.bindTexture(0);
        if (i1 != 36053) {
            Shaders.printChatAndLogError("[Shaders] Error: Failed creating framebuffer! (Status " + i1 + ")");
        } else {
            SMCLog.info("Framebuffer created.");
        }
    }

    private static void setupShadowFrameBuffer() {
        if (usedShadowDepthBuffers != 0) {
            int l;
            if (sfb != 0) {
                EXTFramebufferObject.glDeleteFramebuffersEXT((int)sfb);
                GlStateManager.deleteTextures(sfbDepthTextures);
                GlStateManager.deleteTextures(sfbColorTextures);
            }
            sfb = EXTFramebufferObject.glGenFramebuffersEXT();
            EXTFramebufferObject.glBindFramebufferEXT((int)36160, (int)sfb);
            GL11.glDrawBuffer((int)0);
            GL11.glReadBuffer((int)0);
            GL11.glGenTextures((IntBuffer)((IntBuffer)sfbDepthTextures.clear().limit(usedShadowDepthBuffers)));
            GL11.glGenTextures((IntBuffer)((IntBuffer)sfbColorTextures.clear().limit(usedShadowColorBuffers)));
            sfbDepthTextures.position(0);
            sfbColorTextures.position(0);
            int i = 0;
            while (i < usedShadowDepthBuffers) {
                GlStateManager.bindTexture(sfbDepthTextures.get(i));
                GL11.glTexParameterf((int)3553, (int)10242, (float)10496.0f);
                GL11.glTexParameterf((int)3553, (int)10243, (float)10496.0f);
                int j = shadowFilterNearest[i] ? 9728 : 9729;
                GL11.glTexParameteri((int)3553, (int)10241, (int)j);
                GL11.glTexParameteri((int)3553, (int)10240, (int)j);
                if (shadowHardwareFilteringEnabled[i]) {
                    GL11.glTexParameteri((int)3553, (int)34892, (int)34894);
                }
                GL11.glTexImage2D((int)3553, (int)0, (int)6402, (int)shadowMapWidth, (int)shadowMapHeight, (int)0, (int)6402, (int)5126, null);
                ++i;
            }
            EXTFramebufferObject.glFramebufferTexture2DEXT((int)36160, (int)36096, (int)3553, (int)sfbDepthTextures.get(0), (int)0);
            Shaders.checkGLError("FT sd");
            int k = 0;
            while (k < usedShadowColorBuffers) {
                GlStateManager.bindTexture(sfbColorTextures.get(k));
                GL11.glTexParameterf((int)3553, (int)10242, (float)10496.0f);
                GL11.glTexParameterf((int)3553, (int)10243, (float)10496.0f);
                int i1 = shadowColorFilterNearest[k] ? 9728 : 9729;
                GL11.glTexParameteri((int)3553, (int)10241, (int)i1);
                GL11.glTexParameteri((int)3553, (int)10240, (int)i1);
                GL11.glTexImage2D((int)3553, (int)0, (int)6408, (int)shadowMapWidth, (int)shadowMapHeight, (int)0, (int)32993, (int)33639, null);
                EXTFramebufferObject.glFramebufferTexture2DEXT((int)36160, (int)(36064 + k), (int)3553, (int)sfbColorTextures.get(k), (int)0);
                Shaders.checkGLError("FT sc");
                ++k;
            }
            GlStateManager.bindTexture(0);
            if (usedShadowColorBuffers > 0) {
                GL20.glDrawBuffers((IntBuffer)sfbDrawBuffers);
            }
            if ((l = EXTFramebufferObject.glCheckFramebufferStatusEXT((int)36160)) != 36053) {
                Shaders.printChatAndLogError("[Shaders] Error: Failed creating shadow framebuffer! (Status " + l + ")");
            } else {
                SMCLog.info("Shadow framebuffer created.");
            }
        }
    }

    public static void beginRender(Minecraft minecraft, float partialTicks, long finishTimeNano) {
        block25: {
            Shaders.checkGLError("pre beginRender");
            Shaders.checkWorldChanged(Shaders.mc.world);
            mc = minecraft;
            Shaders.mc.mcProfiler.startSection("init");
            entityRenderer = Shaders.mc.entityRenderer;
            if (!isShaderPackInitialized) {
                try {
                    Shaders.init();
                }
                catch (IllegalStateException illegalstateexception) {
                    if (!Config.normalize(illegalstateexception.getMessage()).equals("Function is not supported")) break block25;
                    Shaders.printChatAndLogError("[Shaders] Error: " + illegalstateexception.getMessage());
                    illegalstateexception.printStackTrace();
                    Shaders.setShaderPack(packNameNone);
                    return;
                }
            }
        }
        if (Shaders.mc.displayWidth != renderDisplayWidth || Shaders.mc.displayHeight != renderDisplayHeight) {
            Shaders.resize();
        }
        if (needResizeShadow) {
            Shaders.resizeShadow();
        }
        if ((diffWorldTime = ((worldTime = Shaders.mc.world.getWorldTime()) - lastWorldTime) % 24000L) < 0L) {
            diffWorldTime += 24000L;
        }
        lastWorldTime = worldTime;
        moonPhase = Shaders.mc.world.getMoonPhase();
        if (++frameCounter >= 720720) {
            frameCounter = 0;
        }
        systemTime = System.currentTimeMillis();
        if (lastSystemTime == 0L) {
            lastSystemTime = systemTime;
        }
        diffSystemTime = systemTime - lastSystemTime;
        lastSystemTime = systemTime;
        frameTime = (float)diffSystemTime / 1000.0f;
        frameTimeCounter += frameTime;
        frameTimeCounter %= 3600.0f;
        rainStrength = minecraft.world.getRainStrength(partialTicks);
        float f = (float)diffSystemTime * 0.01f;
        float f1 = (float)Math.exp(Math.log(0.5) * (double)f / (double)(wetness < rainStrength ? drynessHalfLife : wetnessHalfLife));
        wetness = wetness * f1 + rainStrength * (1.0f - f1);
        Entity entity = mc.getRenderViewEntity();
        if (entity != null) {
            isSleeping = entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPlayerSleeping();
            eyePosY = (float)entity.posY * partialTicks + (float)entity.lastTickPosY * (1.0f - partialTicks);
            eyeBrightness = entity.getBrightnessForRender();
            f1 = (float)diffSystemTime * 0.01f;
            float f2 = (float)Math.exp(Math.log(0.5) * (double)f1 / (double)eyeBrightnessHalflife);
            eyeBrightnessFadeX = eyeBrightnessFadeX * f2 + (float)(eyeBrightness & 0xFFFF) * (1.0f - f2);
            eyeBrightnessFadeY = eyeBrightnessFadeY * f2 + (float)(eyeBrightness >> 16) * (1.0f - f2);
            isEyeInWater = 0;
            if (Shaders.mc.gameSettings.thirdPersonView == 0 && !isSleeping) {
                if (entity.isInsideOfMaterial(Material.WATER)) {
                    isEyeInWater = 1;
                } else if (entity.isInsideOfMaterial(Material.LAVA)) {
                    isEyeInWater = 2;
                }
            }
            if (Shaders.mc.player != null) {
                nightVision = 0.0f;
                if (Shaders.mc.player.isPotionActive(MobEffects.NIGHT_VISION)) {
                    nightVision = Config.getMinecraft().entityRenderer.getNightVisionBrightness(Shaders.mc.player, partialTicks);
                }
                blindness = 0.0f;
                if (Shaders.mc.player.isPotionActive(MobEffects.BLINDNESS)) {
                    int i = Shaders.mc.player.getActivePotionEffect(MobEffects.BLINDNESS).getDuration();
                    blindness = Config.limit((float)i / 20.0f, 0.0f, 1.0f);
                }
            }
            Vec3d vec3d = Shaders.mc.world.getSkyColor(entity, partialTicks);
            vec3d = CustomColors.getWorldSkyColor(vec3d, currentWorld, entity, partialTicks);
            skyColorR = (float)vec3d.xCoord;
            skyColorG = (float)vec3d.yCoord;
            skyColorB = (float)vec3d.zCoord;
        }
        isRenderingWorld = true;
        isCompositeRendered = false;
        isHandRenderedMain = false;
        isHandRenderedOff = false;
        skipRenderHandMain = false;
        skipRenderHandOff = false;
        if (usedShadowDepthBuffers >= 1) {
            GlStateManager.setActiveTexture(33988);
            GlStateManager.bindTexture(sfbDepthTextures.get(0));
            if (usedShadowDepthBuffers >= 2) {
                GlStateManager.setActiveTexture(33989);
                GlStateManager.bindTexture(sfbDepthTextures.get(1));
            }
        }
        GlStateManager.setActiveTexture(33984);
        int j = 0;
        while (j < usedColorBuffers) {
            GlStateManager.bindTexture(dfbColorTexturesA[j]);
            GL11.glTexParameteri((int)3553, (int)10240, (int)9729);
            GL11.glTexParameteri((int)3553, (int)10241, (int)9729);
            GlStateManager.bindTexture(dfbColorTexturesA[8 + j]);
            GL11.glTexParameteri((int)3553, (int)10240, (int)9729);
            GL11.glTexParameteri((int)3553, (int)10241, (int)9729);
            ++j;
        }
        GlStateManager.bindTexture(0);
        int k = 0;
        while (k < 4 && 4 + k < usedColorBuffers) {
            GlStateManager.setActiveTexture(33991 + k);
            GlStateManager.bindTexture(dfbColorTextures.get(4 + k));
            ++k;
        }
        GlStateManager.setActiveTexture(33990);
        GlStateManager.bindTexture(dfbDepthTextures.get(0));
        if (usedDepthBuffers >= 2) {
            GlStateManager.setActiveTexture(33995);
            GlStateManager.bindTexture(dfbDepthTextures.get(1));
            if (usedDepthBuffers >= 3) {
                GlStateManager.setActiveTexture(33996);
                GlStateManager.bindTexture(dfbDepthTextures.get(2));
            }
        }
        int l = 0;
        while (l < usedShadowColorBuffers) {
            GlStateManager.setActiveTexture(33997 + l);
            GlStateManager.bindTexture(sfbColorTextures.get(l));
            ++l;
        }
        if (noiseTextureEnabled) {
            GlStateManager.setActiveTexture(33984 + Shaders.noiseTexture.textureUnit);
            GlStateManager.bindTexture(noiseTexture.getID());
            GL11.glTexParameteri((int)3553, (int)10242, (int)10497);
            GL11.glTexParameteri((int)3553, (int)10243, (int)10497);
            GL11.glTexParameteri((int)3553, (int)10240, (int)9729);
            GL11.glTexParameteri((int)3553, (int)10241, (int)9729);
        }
        Shaders.bindCustomTextures(customTexturesGbuffers);
        GlStateManager.setActiveTexture(33984);
        previousCameraPositionX = cameraPositionX;
        previousCameraPositionY = cameraPositionY;
        previousCameraPositionZ = cameraPositionZ;
        previousProjection.position(0);
        projection.position(0);
        previousProjection.put(projection);
        previousProjection.position(0);
        projection.position(0);
        previousModelView.position(0);
        modelView.position(0);
        previousModelView.put(modelView);
        previousModelView.position(0);
        modelView.position(0);
        Shaders.checkGLError("beginRender");
        ShadersRender.renderShadowMap(entityRenderer, 0, partialTicks, finishTimeNano);
        Shaders.mc.mcProfiler.endSection();
        EXTFramebufferObject.glBindFramebufferEXT((int)36160, (int)dfb);
        int i1 = 0;
        while (i1 < usedColorBuffers) {
            Shaders.colorTexturesToggle[i1] = 0;
            EXTFramebufferObject.glFramebufferTexture2DEXT((int)36160, (int)(36064 + i1), (int)3553, (int)dfbColorTexturesA[i1], (int)0);
            ++i1;
        }
        Shaders.checkGLError("end beginRender");
    }

    private static void checkWorldChanged(World world) {
        if (currentWorld != world) {
            World world1 = currentWorld;
            currentWorld = world;
            if (world != null && world1 != null) {
                int i = world.provider.getDimensionType().getId();
                int j = world1.provider.getDimensionType().getId();
                boolean flag = shaderPackDimensions.contains(i);
                boolean flag1 = shaderPackDimensions.contains(j);
                if (flag || flag1) {
                    Shaders.uninit();
                }
            }
        }
    }

    public static void beginRenderPass(int pass, float partialTicks, long finishTimeNano) {
        if (!isShadowPass) {
            EXTFramebufferObject.glBindFramebufferEXT((int)36160, (int)dfb);
            GL11.glViewport((int)0, (int)0, (int)renderWidth, (int)renderHeight);
            activeDrawBuffers = null;
            ShadersTex.bindNSTextures(defaultTexture.getMultiTexID());
            Shaders.useProgram(2);
            Shaders.checkGLError("end beginRenderPass");
        }
    }

    public static void setViewport(int vx, int vy, int vw, int vh) {
        GlStateManager.colorMask(true, true, true, true);
        if (isShadowPass) {
            GL11.glViewport((int)0, (int)0, (int)shadowMapWidth, (int)shadowMapHeight);
        } else {
            GL11.glViewport((int)0, (int)0, (int)renderWidth, (int)renderHeight);
            EXTFramebufferObject.glBindFramebufferEXT((int)36160, (int)dfb);
            isRenderingDfb = true;
            GlStateManager.enableCull();
            GlStateManager.enableDepth();
            Shaders.setDrawBuffers(drawBuffersNone);
            Shaders.useProgram(2);
            Shaders.checkGLError("beginRenderPass");
        }
    }

    public static int setFogMode(int val) {
        fogMode = val;
        return val;
    }

    public static void setFogColor(float r, float g, float b) {
        fogColorR = r;
        fogColorG = g;
        fogColorB = b;
    }

    public static void setClearColor(float red, float green, float blue, float alpha) {
        GlStateManager.clearColor(red, green, blue, alpha);
        clearColorR = red;
        clearColorG = green;
        clearColorB = blue;
    }

    public static void clearRenderBuffer() {
        if (isShadowPass) {
            Shaders.checkGLError("shadow clear pre");
            EXTFramebufferObject.glFramebufferTexture2DEXT((int)36160, (int)36096, (int)3553, (int)sfbDepthTextures.get(0), (int)0);
            GL11.glClearColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            GL20.glDrawBuffers((IntBuffer)programsDrawBuffers[30]);
            Shaders.checkFramebufferStatus("shadow clear");
            GL11.glClear((int)16640);
            Shaders.checkGLError("shadow clear");
        } else {
            Shaders.checkGLError("clear pre");
            if (gbuffersClear[0]) {
                GL20.glDrawBuffers((int)36064);
                GL11.glClear((int)16384);
            }
            if (gbuffersClear[1]) {
                GL20.glDrawBuffers((int)36065);
                GL11.glClearColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
                GL11.glClear((int)16384);
            }
            int i = 2;
            while (i < usedColorBuffers) {
                if (gbuffersClear[i]) {
                    GL20.glDrawBuffers((int)(36064 + i));
                    GL11.glClearColor((float)0.0f, (float)0.0f, (float)0.0f, (float)0.0f);
                    GL11.glClear((int)16384);
                }
                ++i;
            }
            Shaders.setDrawBuffers(dfbDrawBuffers);
            Shaders.checkFramebufferStatus("clear");
            Shaders.checkGLError("clear");
        }
    }

    public static void setCamera(float partialTicks) {
        Entity entity = mc.getRenderViewEntity();
        double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
        double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
        double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;
        cameraPositionX = d0;
        cameraPositionY = d1;
        cameraPositionZ = d2;
        GL11.glGetFloat((int)2983, (FloatBuffer)((FloatBuffer)projection.position(0)));
        SMath.invertMat4FBFA((FloatBuffer)projectionInverse.position(0), (FloatBuffer)projection.position(0), faProjectionInverse, faProjection);
        projection.position(0);
        projectionInverse.position(0);
        GL11.glGetFloat((int)2982, (FloatBuffer)((FloatBuffer)modelView.position(0)));
        SMath.invertMat4FBFA((FloatBuffer)modelViewInverse.position(0), (FloatBuffer)modelView.position(0), faModelViewInverse, faModelView);
        modelView.position(0);
        modelViewInverse.position(0);
        Shaders.checkGLError("setCamera");
    }

    public static void setCameraShadow(float partialTicks) {
        float f1;
        Entity entity = mc.getRenderViewEntity();
        double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
        double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
        double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;
        cameraPositionX = d0;
        cameraPositionY = d1;
        cameraPositionZ = d2;
        GL11.glGetFloat((int)2983, (FloatBuffer)((FloatBuffer)projection.position(0)));
        SMath.invertMat4FBFA((FloatBuffer)projectionInverse.position(0), (FloatBuffer)projection.position(0), faProjectionInverse, faProjection);
        projection.position(0);
        projectionInverse.position(0);
        GL11.glGetFloat((int)2982, (FloatBuffer)((FloatBuffer)modelView.position(0)));
        SMath.invertMat4FBFA((FloatBuffer)modelViewInverse.position(0), (FloatBuffer)modelView.position(0), faModelViewInverse, faModelView);
        modelView.position(0);
        modelViewInverse.position(0);
        GL11.glViewport((int)0, (int)0, (int)shadowMapWidth, (int)shadowMapHeight);
        GL11.glMatrixMode((int)5889);
        GL11.glLoadIdentity();
        if (shadowMapIsOrtho) {
            GL11.glOrtho((double)(-shadowMapHalfPlane), (double)shadowMapHalfPlane, (double)(-shadowMapHalfPlane), (double)shadowMapHalfPlane, (double)0.05f, (double)256.0);
        } else {
            GLU.gluPerspective((float)shadowMapFOV, (float)((float)shadowMapWidth / (float)shadowMapHeight), (float)0.05f, (float)256.0f);
        }
        GL11.glMatrixMode((int)5888);
        GL11.glLoadIdentity();
        GL11.glTranslatef((float)0.0f, (float)0.0f, (float)-100.0f);
        GL11.glRotatef((float)90.0f, (float)1.0f, (float)0.0f, (float)0.0f);
        celestialAngle = Shaders.mc.world.getCelestialAngle(partialTicks);
        sunAngle = celestialAngle < 0.75f ? celestialAngle + 0.25f : celestialAngle - 0.75f;
        float f = celestialAngle * -360.0f;
        float f2 = f1 = shadowAngleInterval > 0.0f ? f % shadowAngleInterval - shadowAngleInterval * 0.5f : 0.0f;
        if ((double)sunAngle <= 0.5) {
            GL11.glRotatef((float)(f - f1), (float)0.0f, (float)0.0f, (float)1.0f);
            GL11.glRotatef((float)sunPathRotation, (float)1.0f, (float)0.0f, (float)0.0f);
            shadowAngle = sunAngle;
        } else {
            GL11.glRotatef((float)(f + 180.0f - f1), (float)0.0f, (float)0.0f, (float)1.0f);
            GL11.glRotatef((float)sunPathRotation, (float)1.0f, (float)0.0f, (float)0.0f);
            shadowAngle = sunAngle - 0.5f;
        }
        if (shadowMapIsOrtho) {
            float f22 = shadowIntervalSize;
            float f3 = f22 / 2.0f;
            GL11.glTranslatef((float)((float)d0 % f22 - f3), (float)((float)d1 % f22 - f3), (float)((float)d2 % f22 - f3));
        }
        float f9 = sunAngle * ((float)Math.PI * 2);
        float f10 = (float)Math.cos(f9);
        float f4 = (float)Math.sin(f9);
        float f5 = sunPathRotation * ((float)Math.PI * 2);
        float f6 = f10;
        float f7 = f4 * (float)Math.cos(f5);
        float f8 = f4 * (float)Math.sin(f5);
        if ((double)sunAngle > 0.5) {
            f6 = -f10;
            f7 = -f7;
            f8 = -f8;
        }
        Shaders.shadowLightPositionVector[0] = f6;
        Shaders.shadowLightPositionVector[1] = f7;
        Shaders.shadowLightPositionVector[2] = f8;
        Shaders.shadowLightPositionVector[3] = 0.0f;
        GL11.glGetFloat((int)2983, (FloatBuffer)((FloatBuffer)shadowProjection.position(0)));
        SMath.invertMat4FBFA((FloatBuffer)shadowProjectionInverse.position(0), (FloatBuffer)shadowProjection.position(0), faShadowProjectionInverse, faShadowProjection);
        shadowProjection.position(0);
        shadowProjectionInverse.position(0);
        GL11.glGetFloat((int)2982, (FloatBuffer)((FloatBuffer)shadowModelView.position(0)));
        SMath.invertMat4FBFA((FloatBuffer)shadowModelViewInverse.position(0), (FloatBuffer)shadowModelView.position(0), faShadowModelViewInverse, faShadowModelView);
        shadowModelView.position(0);
        shadowModelViewInverse.position(0);
        Shaders.setProgramUniformMatrix4ARB("gbufferProjection", false, projection);
        Shaders.setProgramUniformMatrix4ARB("gbufferProjectionInverse", false, projectionInverse);
        Shaders.setProgramUniformMatrix4ARB("gbufferPreviousProjection", false, previousProjection);
        Shaders.setProgramUniformMatrix4ARB("gbufferModelView", false, modelView);
        Shaders.setProgramUniformMatrix4ARB("gbufferModelViewInverse", false, modelViewInverse);
        Shaders.setProgramUniformMatrix4ARB("gbufferPreviousModelView", false, previousModelView);
        Shaders.setProgramUniformMatrix4ARB("shadowProjection", false, shadowProjection);
        Shaders.setProgramUniformMatrix4ARB("shadowProjectionInverse", false, shadowProjectionInverse);
        Shaders.setProgramUniformMatrix4ARB("shadowModelView", false, shadowModelView);
        Shaders.setProgramUniformMatrix4ARB("shadowModelViewInverse", false, shadowModelViewInverse);
        Shaders.mc.gameSettings.thirdPersonView = 1;
        Shaders.checkGLError("setCamera");
    }

    public static void preCelestialRotate() {
        GL11.glRotatef((float)(sunPathRotation * 1.0f), (float)0.0f, (float)0.0f, (float)1.0f);
        Shaders.checkGLError("preCelestialRotate");
    }

    public static void postCelestialRotate() {
        FloatBuffer floatbuffer = tempMatrixDirectBuffer;
        floatbuffer.clear();
        GL11.glGetFloat((int)2982, (FloatBuffer)floatbuffer);
        floatbuffer.get(tempMat, 0, 16);
        SMath.multiplyMat4xVec4(sunPosition, tempMat, sunPosModelView);
        SMath.multiplyMat4xVec4(moonPosition, tempMat, moonPosModelView);
        System.arraycopy(shadowAngle == sunAngle ? sunPosition : moonPosition, 0, shadowLightPosition, 0, 3);
        Shaders.setProgramUniform3f("sunPosition", sunPosition[0], sunPosition[1], sunPosition[2]);
        Shaders.setProgramUniform3f("moonPosition", moonPosition[0], moonPosition[1], moonPosition[2]);
        Shaders.setProgramUniform3f("shadowLightPosition", shadowLightPosition[0], shadowLightPosition[1], shadowLightPosition[2]);
        Shaders.checkGLError("postCelestialRotate");
    }

    public static void setUpPosition() {
        FloatBuffer floatbuffer = tempMatrixDirectBuffer;
        floatbuffer.clear();
        GL11.glGetFloat((int)2982, (FloatBuffer)floatbuffer);
        floatbuffer.get(tempMat, 0, 16);
        SMath.multiplyMat4xVec4(upPosition, tempMat, upPosModelView);
        Shaders.setProgramUniform3f("upPosition", upPosition[0], upPosition[1], upPosition[2]);
    }

    public static void genCompositeMipmap() {
        if (hasGlGenMipmap) {
            int i = 0;
            while (i < usedColorBuffers) {
                if ((activeCompositeMipmapSetting & 1 << i) != 0) {
                    GlStateManager.setActiveTexture(33984 + colorTextureTextureImageUnit[i]);
                    GL11.glTexParameteri((int)3553, (int)10241, (int)9987);
                    GL30.glGenerateMipmap((int)3553);
                }
                ++i;
            }
            GlStateManager.setActiveTexture(33984);
        }
    }

    public static void drawComposite() {
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GL11.glBegin((int)7);
        GL11.glTexCoord2f((float)0.0f, (float)0.0f);
        GL11.glVertex3f((float)0.0f, (float)0.0f, (float)0.0f);
        GL11.glTexCoord2f((float)1.0f, (float)0.0f);
        GL11.glVertex3f((float)1.0f, (float)0.0f, (float)0.0f);
        GL11.glTexCoord2f((float)1.0f, (float)1.0f);
        GL11.glVertex3f((float)1.0f, (float)1.0f, (float)0.0f);
        GL11.glTexCoord2f((float)0.0f, (float)1.0f);
        GL11.glVertex3f((float)0.0f, (float)1.0f, (float)0.0f);
        GL11.glEnd();
    }

    public static void renderCompositeFinal() {
        if (!isShadowPass) {
            Shaders.checkGLError("pre-renderCompositeFinal");
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glMatrixMode((int)5889);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glOrtho((double)0.0, (double)1.0, (double)0.0, (double)1.0, (double)0.0, (double)1.0);
            GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            GlStateManager.enableTexture2D();
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.enableDepth();
            GlStateManager.depthFunc(519);
            GlStateManager.depthMask(false);
            GlStateManager.disableLighting();
            if (usedShadowDepthBuffers >= 1) {
                GlStateManager.setActiveTexture(33988);
                GlStateManager.bindTexture(sfbDepthTextures.get(0));
                if (usedShadowDepthBuffers >= 2) {
                    GlStateManager.setActiveTexture(33989);
                    GlStateManager.bindTexture(sfbDepthTextures.get(1));
                }
            }
            int i = 0;
            while (i < usedColorBuffers) {
                GlStateManager.setActiveTexture(33984 + colorTextureTextureImageUnit[i]);
                GlStateManager.bindTexture(dfbColorTexturesA[i]);
                ++i;
            }
            GlStateManager.setActiveTexture(33990);
            GlStateManager.bindTexture(dfbDepthTextures.get(0));
            if (usedDepthBuffers >= 2) {
                GlStateManager.setActiveTexture(33995);
                GlStateManager.bindTexture(dfbDepthTextures.get(1));
                if (usedDepthBuffers >= 3) {
                    GlStateManager.setActiveTexture(33996);
                    GlStateManager.bindTexture(dfbDepthTextures.get(2));
                }
            }
            int j1 = 0;
            while (j1 < usedShadowColorBuffers) {
                GlStateManager.setActiveTexture(33997 + j1);
                GlStateManager.bindTexture(sfbColorTextures.get(j1));
                ++j1;
            }
            if (noiseTextureEnabled) {
                GlStateManager.setActiveTexture(33984 + Shaders.noiseTexture.textureUnit);
                GlStateManager.bindTexture(noiseTexture.getID());
                GL11.glTexParameteri((int)3553, (int)10242, (int)10497);
                GL11.glTexParameteri((int)3553, (int)10243, (int)10497);
                GL11.glTexParameteri((int)3553, (int)10240, (int)9729);
                GL11.glTexParameteri((int)3553, (int)10241, (int)9729);
            }
            Shaders.bindCustomTextures(customTexturesComposite);
            GlStateManager.setActiveTexture(33984);
            boolean flag = true;
            int j = 0;
            while (j < usedColorBuffers) {
                EXTFramebufferObject.glFramebufferTexture2DEXT((int)36160, (int)(36064 + j), (int)3553, (int)dfbColorTexturesA[8 + j], (int)0);
                ++j;
            }
            EXTFramebufferObject.glFramebufferTexture2DEXT((int)36160, (int)36096, (int)3553, (int)dfbDepthTextures.get(0), (int)0);
            GL20.glDrawBuffers((IntBuffer)dfbDrawBuffers);
            Shaders.checkGLError("pre-composite");
            int k1 = 0;
            while (k1 < 8) {
                if (programsID[21 + k1] != 0) {
                    Shaders.useProgram(21 + k1);
                    Shaders.checkGLError(programNames[21 + k1]);
                    if (activeCompositeMipmapSetting != 0) {
                        Shaders.genCompositeMipmap();
                    }
                    Shaders.drawComposite();
                    int k = 0;
                    while (k < usedColorBuffers) {
                        if (programsToggleColorTextures[21 + k1][k]) {
                            int l = colorTexturesToggle[k];
                            int i1 = Shaders.colorTexturesToggle[k] = 8 - l;
                            GlStateManager.setActiveTexture(33984 + colorTextureTextureImageUnit[k]);
                            GlStateManager.bindTexture(dfbColorTexturesA[i1 + k]);
                            EXTFramebufferObject.glFramebufferTexture2DEXT((int)36160, (int)(36064 + k), (int)3553, (int)dfbColorTexturesA[l + k], (int)0);
                        }
                        ++k;
                    }
                    GlStateManager.setActiveTexture(33984);
                }
                ++k1;
            }
            Shaders.checkGLError("composite");
            isRenderingDfb = false;
            mc.getFramebuffer().bindFramebuffer(true);
            OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_COLOR_ATTACHMENT0, 3553, Shaders.mc.getFramebuffer().framebufferTexture, 0);
            GL11.glViewport((int)0, (int)0, (int)Shaders.mc.displayWidth, (int)Shaders.mc.displayHeight);
            if (EntityRenderer.anaglyphEnable) {
                boolean flag1 = EntityRenderer.anaglyphField != 0;
                GlStateManager.colorMask(flag1, !flag1, !flag1, true);
            }
            GlStateManager.depthMask(true);
            GL11.glClearColor((float)clearColorR, (float)clearColorG, (float)clearColorB, (float)1.0f);
            GL11.glClear((int)16640);
            GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            GlStateManager.enableTexture2D();
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.enableDepth();
            GlStateManager.depthFunc(519);
            GlStateManager.depthMask(false);
            Shaders.checkGLError("pre-final");
            Shaders.useProgram(29);
            Shaders.checkGLError("final");
            if (activeCompositeMipmapSetting != 0) {
                Shaders.genCompositeMipmap();
            }
            Shaders.drawComposite();
            Shaders.checkGLError("renderCompositeFinal");
            isCompositeRendered = true;
            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.depthFunc(515);
            GlStateManager.depthMask(true);
            GL11.glPopMatrix();
            GL11.glMatrixMode((int)5888);
            GL11.glPopMatrix();
            Shaders.useProgram(0);
        }
    }

    public static void endRender() {
        if (isShadowPass) {
            Shaders.checkGLError("shadow endRender");
        } else {
            if (!isCompositeRendered) {
                Shaders.renderCompositeFinal();
            }
            isRenderingWorld = false;
            GlStateManager.colorMask(true, true, true, true);
            Shaders.useProgram(0);
            RenderHelper.disableStandardItemLighting();
            Shaders.checkGLError("endRender end");
        }
    }

    public static void beginSky() {
        isRenderingSky = true;
        fogEnabled = true;
        Shaders.setDrawBuffers(dfbDrawBuffers);
        Shaders.useProgram(5);
        Shaders.pushEntity(-2, 0);
    }

    public static void setSkyColor(Vec3d v3color) {
        skyColorR = (float)v3color.xCoord;
        skyColorG = (float)v3color.yCoord;
        skyColorB = (float)v3color.zCoord;
        Shaders.setProgramUniform3f("skyColor", skyColorR, skyColorG, skyColorB);
    }

    public static void drawHorizon() {
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        float f = Shaders.mc.gameSettings.renderDistanceChunks * 16;
        double d0 = (double)f * 0.9238;
        double d1 = (double)f * 0.3826;
        double d2 = -d1;
        double d3 = -d0;
        double d4 = 16.0;
        double d5 = -cameraPositionY;
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(d2, d5, d3).endVertex();
        bufferbuilder.pos(d2, d4, d3).endVertex();
        bufferbuilder.pos(d3, d4, d2).endVertex();
        bufferbuilder.pos(d3, d5, d2).endVertex();
        bufferbuilder.pos(d3, d5, d2).endVertex();
        bufferbuilder.pos(d3, d4, d2).endVertex();
        bufferbuilder.pos(d3, d4, d1).endVertex();
        bufferbuilder.pos(d3, d5, d1).endVertex();
        bufferbuilder.pos(d3, d5, d1).endVertex();
        bufferbuilder.pos(d3, d4, d1).endVertex();
        bufferbuilder.pos(d2, d4, d1).endVertex();
        bufferbuilder.pos(d2, d5, d1).endVertex();
        bufferbuilder.pos(d2, d5, d1).endVertex();
        bufferbuilder.pos(d2, d4, d1).endVertex();
        bufferbuilder.pos(d1, d4, d0).endVertex();
        bufferbuilder.pos(d1, d5, d0).endVertex();
        bufferbuilder.pos(d1, d5, d0).endVertex();
        bufferbuilder.pos(d1, d4, d0).endVertex();
        bufferbuilder.pos(d0, d4, d1).endVertex();
        bufferbuilder.pos(d0, d5, d1).endVertex();
        bufferbuilder.pos(d0, d5, d1).endVertex();
        bufferbuilder.pos(d0, d4, d1).endVertex();
        bufferbuilder.pos(d0, d4, d2).endVertex();
        bufferbuilder.pos(d0, d5, d2).endVertex();
        bufferbuilder.pos(d0, d5, d2).endVertex();
        bufferbuilder.pos(d0, d4, d2).endVertex();
        bufferbuilder.pos(d1, d4, d3).endVertex();
        bufferbuilder.pos(d1, d5, d3).endVertex();
        bufferbuilder.pos(d1, d5, d3).endVertex();
        bufferbuilder.pos(d1, d4, d3).endVertex();
        bufferbuilder.pos(d2, d4, d3).endVertex();
        bufferbuilder.pos(d2, d5, d3).endVertex();
        Tessellator.getInstance().draw();
    }

    public static void preSkyList() {
        Shaders.setUpPosition();
        GL11.glColor3f((float)fogColorR, (float)fogColorG, (float)fogColorB);
        Shaders.drawHorizon();
        GL11.glColor3f((float)skyColorR, (float)skyColorG, (float)skyColorB);
    }

    public static void endSky() {
        isRenderingSky = false;
        Shaders.setDrawBuffers(dfbDrawBuffers);
        Shaders.useProgram(lightmapEnabled ? 3 : 2);
        Shaders.popEntity();
    }

    public static void beginUpdateChunks() {
        Shaders.checkGLError("beginUpdateChunks1");
        Shaders.checkFramebufferStatus("beginUpdateChunks1");
        if (!isShadowPass) {
            Shaders.useProgram(7);
        }
        Shaders.checkGLError("beginUpdateChunks2");
        Shaders.checkFramebufferStatus("beginUpdateChunks2");
    }

    public static void endUpdateChunks() {
        Shaders.checkGLError("endUpdateChunks1");
        Shaders.checkFramebufferStatus("endUpdateChunks1");
        if (!isShadowPass) {
            Shaders.useProgram(7);
        }
        Shaders.checkGLError("endUpdateChunks2");
        Shaders.checkFramebufferStatus("endUpdateChunks2");
    }

    public static boolean shouldRenderClouds(GameSettings gs) {
        if (!shaderPackLoaded) {
            return true;
        }
        Shaders.checkGLError("shouldRenderClouds");
        return isShadowPass ? configCloudShadow : gs.clouds > 0;
    }

    public static void beginClouds() {
        fogEnabled = true;
        Shaders.pushEntity(-3, 0);
        Shaders.useProgram(6);
    }

    public static void endClouds() {
        Shaders.disableFog();
        Shaders.popEntity();
        Shaders.useProgram(lightmapEnabled ? 3 : 2);
    }

    public static void beginEntities() {
        if (isRenderingWorld) {
            Shaders.useProgram(16);
            Shaders.resetDisplayListModels();
        }
    }

    public static void nextEntity(Entity entity) {
        if (isRenderingWorld) {
            Shaders.useProgram(16);
            Shaders.setEntityId(entity);
        }
    }

    public static void setEntityId(Entity entity) {
        if (isRenderingWorld && !isShadowPass && uniformEntityId.isDefined()) {
            int i = EntityUtils.getEntityIdByClass(entity);
            uniformEntityId.setValue(i);
        }
    }

    public static void beginSpiderEyes() {
        if (isRenderingWorld && programsID[18] != programsID[0]) {
            Shaders.useProgram(18);
            GlStateManager.enableAlpha();
            GlStateManager.alphaFunc(516, 0.0f);
            GlStateManager.blendFunc(770, 771);
        }
    }

    public static void endSpiderEyes() {
        if (isRenderingWorld && programsID[18] != programsID[0]) {
            Shaders.useProgram(16);
            GlStateManager.disableAlpha();
        }
    }

    public static void endEntities() {
        if (isRenderingWorld) {
            Shaders.useProgram(lightmapEnabled ? 3 : 2);
        }
    }

    public static void setEntityColor(float r, float g, float b, float a) {
        if (isRenderingWorld && !isShadowPass) {
            uniformEntityColor.setValue(r, g, b, a);
        }
    }

    public static void beginLivingDamage() {
        if (isRenderingWorld) {
            ShadersTex.bindTexture(defaultTexture);
            if (!isShadowPass) {
                Shaders.setDrawBuffers(drawBuffersColorAtt0);
            }
        }
    }

    public static void endLivingDamage() {
        if (isRenderingWorld && !isShadowPass) {
            Shaders.setDrawBuffers(programsDrawBuffers[16]);
        }
    }

    public static void beginBlockEntities() {
        if (isRenderingWorld) {
            Shaders.checkGLError("beginBlockEntities");
            Shaders.useProgram(13);
        }
    }

    public static void nextBlockEntity(TileEntity tileEntity) {
        if (isRenderingWorld) {
            Shaders.checkGLError("nextBlockEntity");
            Shaders.useProgram(13);
            Shaders.setBlockEntityId(tileEntity);
        }
    }

    public static void setBlockEntityId(TileEntity tileEntity) {
        if (isRenderingWorld && !isShadowPass && uniformBlockEntityId.isDefined()) {
            Block block = tileEntity.getBlockType();
            int i = Block.getIdFromBlock(block);
            uniformBlockEntityId.setValue(i);
        }
    }

    public static void endBlockEntities() {
        if (isRenderingWorld) {
            Shaders.checkGLError("endBlockEntities");
            Shaders.useProgram(lightmapEnabled ? 3 : 2);
            ShadersTex.bindNSTextures(defaultTexture.getMultiTexID());
        }
    }

    public static void beginLitParticles() {
        Shaders.useProgram(3);
    }

    public static void beginParticles() {
        Shaders.useProgram(2);
    }

    public static void endParticles() {
        Shaders.useProgram(3);
    }

    public static void readCenterDepth() {
        if (!isShadowPass && centerDepthSmoothEnabled) {
            tempDirectFloatBuffer.clear();
            GL11.glReadPixels((int)(renderWidth / 2), (int)(renderHeight / 2), (int)1, (int)1, (int)6402, (int)5126, (FloatBuffer)tempDirectFloatBuffer);
            centerDepth = tempDirectFloatBuffer.get(0);
            float f = (float)diffSystemTime * 0.01f;
            float f1 = (float)Math.exp(Math.log(0.5) * (double)f / (double)centerDepthSmoothHalflife);
            centerDepthSmooth = centerDepthSmooth * f1 + centerDepth * (1.0f - f1);
        }
    }

    public static void beginWeather() {
        if (!isShadowPass) {
            if (usedDepthBuffers >= 3) {
                GlStateManager.setActiveTexture(33996);
                GL11.glCopyTexSubImage2D((int)3553, (int)0, (int)0, (int)0, (int)0, (int)0, (int)renderWidth, (int)renderHeight);
                GlStateManager.setActiveTexture(33984);
            }
            GlStateManager.enableDepth();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            GlStateManager.enableAlpha();
            Shaders.useProgram(20);
        }
    }

    public static void endWeather() {
        GlStateManager.disableBlend();
        Shaders.useProgram(3);
    }

    public static void preWater() {
        if (usedDepthBuffers >= 2) {
            GlStateManager.setActiveTexture(33995);
            Shaders.checkGLError("pre copy depth");
            GL11.glCopyTexSubImage2D((int)3553, (int)0, (int)0, (int)0, (int)0, (int)0, (int)renderWidth, (int)renderHeight);
            Shaders.checkGLError("copy depth");
            GlStateManager.setActiveTexture(33984);
        }
        ShadersTex.bindNSTextures(defaultTexture.getMultiTexID());
    }

    public static void beginWater() {
        if (isRenderingWorld) {
            if (!isShadowPass) {
                Shaders.useProgram(12);
                GlStateManager.enableBlend();
                GlStateManager.depthMask(true);
            } else {
                GlStateManager.depthMask(true);
            }
        }
    }

    public static void endWater() {
        if (isRenderingWorld) {
            if (isShadowPass) {
                // empty if block
            }
            Shaders.useProgram(lightmapEnabled ? 3 : 2);
        }
    }

    public static void beginProjectRedHalo() {
        if (isRenderingWorld) {
            Shaders.useProgram(1);
        }
    }

    public static void endProjectRedHalo() {
        if (isRenderingWorld) {
            Shaders.useProgram(3);
        }
    }

    public static void applyHandDepth() {
        if ((double)configHandDepthMul != 1.0) {
            GL11.glScaled((double)1.0, (double)1.0, (double)configHandDepthMul);
        }
    }

    public static void beginHand() {
        GL11.glMatrixMode((int)5888);
        GL11.glPushMatrix();
        GL11.glMatrixMode((int)5889);
        GL11.glPushMatrix();
        GL11.glMatrixMode((int)5888);
        Shaders.useProgram(19);
        Shaders.checkGLError("beginHand");
        Shaders.checkFramebufferStatus("beginHand");
    }

    public static void endHand() {
        Shaders.checkGLError("pre endHand");
        Shaders.checkFramebufferStatus("pre endHand");
        GL11.glMatrixMode((int)5889);
        GL11.glPopMatrix();
        GL11.glMatrixMode((int)5888);
        GL11.glPopMatrix();
        GlStateManager.blendFunc(770, 771);
        Shaders.checkGLError("endHand");
    }

    public static void beginFPOverlay() {
        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
    }

    public static void endFPOverlay() {
    }

    public static void glEnableWrapper(int cap) {
        GL11.glEnable((int)cap);
        if (cap == 3553) {
            Shaders.enableTexture2D();
        } else if (cap == 2912) {
            Shaders.enableFog();
        }
    }

    public static void glDisableWrapper(int cap) {
        GL11.glDisable((int)cap);
        if (cap == 3553) {
            Shaders.disableTexture2D();
        } else if (cap == 2912) {
            Shaders.disableFog();
        }
    }

    public static void sglEnableT2D(int cap) {
        GL11.glEnable((int)cap);
        Shaders.enableTexture2D();
    }

    public static void sglDisableT2D(int cap) {
        GL11.glDisable((int)cap);
        Shaders.disableTexture2D();
    }

    public static void sglEnableFog(int cap) {
        GL11.glEnable((int)cap);
        Shaders.enableFog();
    }

    public static void sglDisableFog(int cap) {
        GL11.glDisable((int)cap);
        Shaders.disableFog();
    }

    public static void enableTexture2D() {
        if (isRenderingSky) {
            Shaders.useProgram(5);
        } else if (activeProgram == 1) {
            Shaders.useProgram(lightmapEnabled ? 3 : 2);
        }
    }

    public static void disableTexture2D() {
        if (isRenderingSky) {
            Shaders.useProgram(4);
        } else if (activeProgram == 2 || activeProgram == 3) {
            Shaders.useProgram(1);
        }
    }

    public static void beginLeash() {
        Shaders.useProgram(1);
    }

    public static void endLeash() {
        Shaders.useProgram(16);
    }

    public static void enableFog() {
        fogEnabled = true;
        Shaders.setProgramUniform1i("fogMode", fogMode);
    }

    public static void disableFog() {
        fogEnabled = false;
        Shaders.setProgramUniform1i("fogMode", 0);
    }

    public static void setFog(GlStateManager.FogMode fogMode) {
        GlStateManager.setFog(fogMode);
        if (fogEnabled) {
            Shaders.setProgramUniform1i("fogMode", fogMode.capabilityId);
        }
    }

    public static void sglFogi(int pname, int param) {
        GL11.glFogi((int)pname, (int)param);
        if (pname == 2917) {
            fogMode = param;
            if (fogEnabled) {
                Shaders.setProgramUniform1i("fogMode", fogMode);
            }
        }
    }

    public static void enableLightmap() {
        lightmapEnabled = true;
        if (activeProgram == 2) {
            Shaders.useProgram(3);
        }
    }

    public static void disableLightmap() {
        lightmapEnabled = false;
        if (activeProgram == 3) {
            Shaders.useProgram(2);
        }
    }

    public static int getEntityData() {
        return entityData[entityDataIndex * 2];
    }

    public static int getEntityData2() {
        return entityData[entityDataIndex * 2 + 1];
    }

    public static int setEntityData1(int data1) {
        Shaders.entityData[Shaders.entityDataIndex * 2] = entityData[entityDataIndex * 2] & 0xFFFF | data1 << 16;
        return data1;
    }

    public static int setEntityData2(int data2) {
        Shaders.entityData[Shaders.entityDataIndex * 2 + 1] = entityData[entityDataIndex * 2 + 1] & 0xFFFF0000 | data2 & 0xFFFF;
        return data2;
    }

    public static void pushEntity(int data0, int data1) {
        Shaders.entityData[++Shaders.entityDataIndex * 2] = data0 & 0xFFFF | data1 << 16;
        Shaders.entityData[Shaders.entityDataIndex * 2 + 1] = 0;
    }

    public static void pushEntity(int data0) {
        Shaders.entityData[++Shaders.entityDataIndex * 2] = data0 & 0xFFFF;
        Shaders.entityData[Shaders.entityDataIndex * 2 + 1] = 0;
    }

    public static void pushEntity(Block block) {
        int i = block.getRenderType(block.getDefaultState()).ordinal();
        Shaders.entityData[++Shaders.entityDataIndex * 2] = Block.REGISTRY.getIDForObject(block) & 0xFFFF | i << 16;
        Shaders.entityData[Shaders.entityDataIndex * 2 + 1] = 0;
    }

    public static void popEntity() {
        Shaders.entityData[Shaders.entityDataIndex * 2] = 0;
        Shaders.entityData[Shaders.entityDataIndex * 2 + 1] = 0;
        --entityDataIndex;
    }

    public static void mcProfilerEndSection() {
        Shaders.mc.mcProfiler.endSection();
    }

    public static String getShaderPackName() {
        if (shaderPack == null) {
            return null;
        }
        return shaderPack instanceof ShaderPackNone ? null : shaderPack.getName();
    }

    public static InputStream getShaderPackResourceStream(String path) {
        return shaderPack == null ? null : shaderPack.getResourceAsStream(path);
    }

    public static void nextAntialiasingLevel() {
        configAntialiasingLevel += 2;
        if ((configAntialiasingLevel = configAntialiasingLevel / 2 * 2) > 4) {
            configAntialiasingLevel = 0;
        }
        configAntialiasingLevel = Config.limit(configAntialiasingLevel, 0, 4);
    }

    public static void checkShadersModInstalled() {
        try {
            Class<?> clazz = Class.forName("shadersmod.transform.SMCClassTransformer");
        }
        catch (Throwable var1) {
            return;
        }
        throw new RuntimeException("Shaders Mod detected. Please remove it, OptiFine has built-in support for shaders.");
    }

    public static void resourcesReloaded() {
        Shaders.loadShaderPackResources();
    }

    private static void loadShaderPackResources() {
        shaderPackResources = new HashMap<String, String>();
        if (shaderPackLoaded) {
            ArrayList<String> list = new ArrayList<String>();
            String s = "/shaders/lang/";
            String s1 = "en_US";
            String s2 = ".lang";
            list.add(String.valueOf(s) + s1 + s2);
            if (!Config.getGameSettings().language.equals(s1)) {
                list.add(String.valueOf(s) + Config.getGameSettings().language + s2);
            }
            try {
                for (String s3 : list) {
                    InputStream inputstream = shaderPack.getResourceAsStream(s3);
                    if (inputstream == null) continue;
                    Properties properties = new Properties();
                    Lang.loadLocaleData(inputstream, properties);
                    inputstream.close();
                    for (Object s40 : properties.keySet()) {
                        String s4 = (String)s40;
                        String s5 = properties.getProperty(s4);
                        shaderPackResources.put(s4, s5);
                    }
                }
            }
            catch (IOException ioexception) {
                ioexception.printStackTrace();
            }
        }
    }

    public static String translate(String key, String def) {
        String s = shaderPackResources.get(key);
        return s == null ? def : s;
    }

    public static boolean isProgramPath(String program) {
        if (program == null) {
            return false;
        }
        if (program.length() <= 0) {
            return false;
        }
        int i = program.lastIndexOf("/");
        if (i >= 0) {
            program = program.substring(i + 1);
        }
        return Arrays.asList(programNames).contains(program);
    }

    public static void setItemToRenderMain(ItemStack itemToRenderMain) {
        itemToRenderMainTranslucent = Shaders.isTranslucentBlock(itemToRenderMain);
    }

    public static void setItemToRenderOff(ItemStack itemToRenderOff) {
        itemToRenderOffTranslucent = Shaders.isTranslucentBlock(itemToRenderOff);
    }

    public static boolean isItemToRenderMainTranslucent() {
        return itemToRenderMainTranslucent;
    }

    public static boolean isItemToRenderOffTranslucent() {
        return itemToRenderOffTranslucent;
    }

    public static boolean isBothHandsRendered() {
        return isHandRenderedMain && isHandRenderedOff;
    }

    private static boolean isTranslucentBlock(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        Item item = stack.getItem();
        if (item == null) {
            return false;
        }
        if (!(item instanceof ItemBlock)) {
            return false;
        }
        ItemBlock itemblock = (ItemBlock)item;
        Block block = itemblock.getBlock();
        if (block == null) {
            return false;
        }
        BlockRenderLayer blockrenderlayer = block.getBlockLayer();
        return blockrenderlayer == BlockRenderLayer.TRANSLUCENT;
    }

    public static boolean isSkipRenderHand(EnumHand hand) {
        if (hand == EnumHand.MAIN_HAND && skipRenderHandMain) {
            return true;
        }
        return hand == EnumHand.OFF_HAND && skipRenderHandOff;
    }

    public static boolean isRenderBothHands() {
        return !skipRenderHandMain && !skipRenderHandOff;
    }

    public static void setSkipRenderHands(boolean skipMain, boolean skipOff) {
        skipRenderHandMain = skipMain;
        skipRenderHandOff = skipOff;
    }

    public static void setHandsRendered(boolean handMain, boolean handOff) {
        isHandRenderedMain = handMain;
        isHandRenderedOff = handOff;
    }

    public static boolean isHandRenderedMain() {
        return isHandRenderedMain;
    }

    public static boolean isHandRenderedOff() {
        return isHandRenderedOff;
    }

    public static float getShadowRenderDistance() {
        return shadowDistanceRenderMul < 0.0f ? -1.0f : shadowMapHalfPlane * shadowDistanceRenderMul;
    }

    public static void setRenderingFirstPersonHand(boolean flag) {
        isRenderingFirstPersonHand = flag;
    }

    public static boolean isRenderingFirstPersonHand() {
        return isRenderingFirstPersonHand;
    }
}

