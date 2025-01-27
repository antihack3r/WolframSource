/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.lwjgl.BufferUtils
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL14
 *  org.lwjgl.opengl.GLContext
 *  org.lwjgl.util.vector.Quaternion
 */
package net.minecraft.client.renderer;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.src.Config;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.vector.Quaternion;

public class GlStateManager {
    private static final FloatBuffer BUF_FLOAT_16 = BufferUtils.createFloatBuffer((int)16);
    private static final FloatBuffer BUF_FLOAT_4 = BufferUtils.createFloatBuffer((int)4);
    private static final AlphaState alphaState = new AlphaState();
    private static final BooleanState lightingState = new BooleanState(2896);
    private static final BooleanState[] lightState = new BooleanState[8];
    private static final ColorMaterialState colorMaterialState;
    private static final BlendState blendState;
    private static final DepthState depthState;
    private static final FogState fogState;
    private static final CullState cullState;
    private static final PolygonOffsetState polygonOffsetState;
    private static final ColorLogicState colorLogicState;
    private static final TexGenState texGenState;
    private static final ClearState clearState;
    private static final StencilState stencilState;
    private static final BooleanState normalizeState;
    private static int activeTextureUnit;
    private static final TextureState[] textureState;
    private static int activeShadeModel;
    private static final BooleanState rescaleNormalState;
    private static final ColorMask colorMaskState;
    private static final Color colorState;
    public static boolean clearEnabled;

    static {
        clearEnabled = true;
        int i = 0;
        while (i < 8) {
            GlStateManager.lightState[i] = new BooleanState(16384 + i);
            ++i;
        }
        colorMaterialState = new ColorMaterialState();
        blendState = new BlendState();
        depthState = new DepthState();
        fogState = new FogState();
        cullState = new CullState();
        polygonOffsetState = new PolygonOffsetState();
        colorLogicState = new ColorLogicState();
        texGenState = new TexGenState();
        clearState = new ClearState();
        stencilState = new StencilState();
        normalizeState = new BooleanState(2977);
        textureState = new TextureState[32];
        int j = 0;
        while (j < textureState.length) {
            GlStateManager.textureState[j] = new TextureState();
            ++j;
        }
        activeShadeModel = 7425;
        rescaleNormalState = new BooleanState(32826);
        colorMaskState = new ColorMask();
        colorState = new Color();
    }

    public static void pushAttrib() {
        GL11.glPushAttrib((int)8256);
    }

    public static void popAttrib() {
        GL11.glPopAttrib();
    }

    public static void disableAlpha() {
        GlStateManager.alphaState.alphaTest.setDisabled();
    }

    public static void enableAlpha() {
        GlStateManager.alphaState.alphaTest.setEnabled();
    }

    public static void alphaFunc(int func, float ref) {
        if (func != GlStateManager.alphaState.func || ref != GlStateManager.alphaState.ref) {
            GlStateManager.alphaState.func = func;
            GlStateManager.alphaState.ref = ref;
            GL11.glAlphaFunc((int)func, (float)ref);
        }
    }

    public static void enableLighting() {
        lightingState.setEnabled();
    }

    public static void disableLighting() {
        lightingState.setDisabled();
    }

    public static void enableLight(int light) {
        lightState[light].setEnabled();
    }

    public static void disableLight(int light) {
        lightState[light].setDisabled();
    }

    public static void enableColorMaterial() {
        GlStateManager.colorMaterialState.colorMaterial.setEnabled();
    }

    public static void disableColorMaterial() {
        GlStateManager.colorMaterialState.colorMaterial.setDisabled();
    }

    public static void colorMaterial(int face, int mode) {
        if (face != GlStateManager.colorMaterialState.face || mode != GlStateManager.colorMaterialState.mode) {
            GlStateManager.colorMaterialState.face = face;
            GlStateManager.colorMaterialState.mode = mode;
            GL11.glColorMaterial((int)face, (int)mode);
        }
    }

    public static void glLight(int light, int pname, FloatBuffer params) {
        GL11.glLight((int)light, (int)pname, (FloatBuffer)params);
    }

    public static void glLightModel(int pname, FloatBuffer params) {
        GL11.glLightModel((int)pname, (FloatBuffer)params);
    }

    public static void glNormal3f(float nx, float ny, float nz) {
        GL11.glNormal3f((float)nx, (float)ny, (float)nz);
    }

    public static void disableDepth() {
        GlStateManager.depthState.depthTest.setDisabled();
    }

    public static void enableDepth() {
        GlStateManager.depthState.depthTest.setEnabled();
    }

    public static void depthFunc(int depthFunc) {
        if (depthFunc != GlStateManager.depthState.depthFunc) {
            GlStateManager.depthState.depthFunc = depthFunc;
            GL11.glDepthFunc((int)depthFunc);
        }
    }

    public static void depthMask(boolean flagIn) {
        if (flagIn != GlStateManager.depthState.maskEnabled) {
            GlStateManager.depthState.maskEnabled = flagIn;
            GL11.glDepthMask((boolean)flagIn);
        }
    }

    public static void disableBlend() {
        GlStateManager.blendState.blend.setDisabled();
    }

    public static void enableBlend() {
        GlStateManager.blendState.blend.setEnabled();
    }

    public static void blendFunc(SourceFactor srcFactor, DestFactor dstFactor) {
        GlStateManager.blendFunc(srcFactor.factor, dstFactor.factor);
    }

    public static void blendFunc(int srcFactor, int dstFactor) {
        if (srcFactor != GlStateManager.blendState.srcFactor || dstFactor != GlStateManager.blendState.dstFactor) {
            GlStateManager.blendState.srcFactor = srcFactor;
            GlStateManager.blendState.dstFactor = dstFactor;
            GL11.glBlendFunc((int)srcFactor, (int)dstFactor);
        }
    }

    public static void tryBlendFuncSeparate(SourceFactor srcFactor, DestFactor dstFactor, SourceFactor srcFactorAlpha, DestFactor dstFactorAlpha) {
        GlStateManager.tryBlendFuncSeparate(srcFactor.factor, dstFactor.factor, srcFactorAlpha.factor, dstFactorAlpha.factor);
    }

    public static void tryBlendFuncSeparate(int srcFactor, int dstFactor, int srcFactorAlpha, int dstFactorAlpha) {
        if (srcFactor != GlStateManager.blendState.srcFactor || dstFactor != GlStateManager.blendState.dstFactor || srcFactorAlpha != GlStateManager.blendState.srcFactorAlpha || dstFactorAlpha != GlStateManager.blendState.dstFactorAlpha) {
            GlStateManager.blendState.srcFactor = srcFactor;
            GlStateManager.blendState.dstFactor = dstFactor;
            GlStateManager.blendState.srcFactorAlpha = srcFactorAlpha;
            GlStateManager.blendState.dstFactorAlpha = dstFactorAlpha;
            OpenGlHelper.glBlendFunc(srcFactor, dstFactor, srcFactorAlpha, dstFactorAlpha);
        }
    }

    public static void glBlendEquation(int blendEquation) {
        GL14.glBlendEquation((int)blendEquation);
    }

    public static void enableOutlineMode(int p_187431_0_) {
        BUF_FLOAT_4.put(0, (float)(p_187431_0_ >> 16 & 0xFF) / 255.0f);
        BUF_FLOAT_4.put(1, (float)(p_187431_0_ >> 8 & 0xFF) / 255.0f);
        BUF_FLOAT_4.put(2, (float)(p_187431_0_ >> 0 & 0xFF) / 255.0f);
        BUF_FLOAT_4.put(3, (float)(p_187431_0_ >> 24 & 0xFF) / 255.0f);
        GlStateManager.glTexEnv(8960, 8705, BUF_FLOAT_4);
        GlStateManager.glTexEnvi(8960, 8704, 34160);
        GlStateManager.glTexEnvi(8960, 34161, 7681);
        GlStateManager.glTexEnvi(8960, 34176, 34166);
        GlStateManager.glTexEnvi(8960, 34192, 768);
        GlStateManager.glTexEnvi(8960, 34162, 7681);
        GlStateManager.glTexEnvi(8960, 34184, 5890);
        GlStateManager.glTexEnvi(8960, 34200, 770);
    }

    public static void disableOutlineMode() {
        GlStateManager.glTexEnvi(8960, 8704, 8448);
        GlStateManager.glTexEnvi(8960, 34161, 8448);
        GlStateManager.glTexEnvi(8960, 34162, 8448);
        GlStateManager.glTexEnvi(8960, 34176, 5890);
        GlStateManager.glTexEnvi(8960, 34184, 5890);
        GlStateManager.glTexEnvi(8960, 34192, 768);
        GlStateManager.glTexEnvi(8960, 34200, 770);
    }

    public static void enableFog() {
        GlStateManager.fogState.fog.setEnabled();
    }

    public static void disableFog() {
        GlStateManager.fogState.fog.setDisabled();
    }

    public static void setFog(FogMode fogMode) {
        GlStateManager.setFog(fogMode.capabilityId);
    }

    private static void setFog(int param) {
        if (param != GlStateManager.fogState.mode) {
            GlStateManager.fogState.mode = param;
            GL11.glFogi((int)2917, (int)param);
        }
    }

    public static void setFogDensity(float param) {
        if (param != GlStateManager.fogState.density) {
            GlStateManager.fogState.density = param;
            GL11.glFogf((int)2914, (float)param);
        }
    }

    public static void setFogStart(float param) {
        if (param != GlStateManager.fogState.start) {
            GlStateManager.fogState.start = param;
            GL11.glFogf((int)2915, (float)param);
        }
    }

    public static void setFogEnd(float param) {
        if (param != GlStateManager.fogState.end) {
            GlStateManager.fogState.end = param;
            GL11.glFogf((int)2916, (float)param);
        }
    }

    public static void glFog(int pname, FloatBuffer param) {
        GL11.glFog((int)pname, (FloatBuffer)param);
    }

    public static void glFogi(int pname, int param) {
        GL11.glFogi((int)pname, (int)param);
    }

    public static void enableCull() {
        GlStateManager.cullState.cullFace.setEnabled();
    }

    public static void disableCull() {
        GlStateManager.cullState.cullFace.setDisabled();
    }

    public static void cullFace(CullFace cullFace) {
        GlStateManager.cullFace(cullFace.mode);
    }

    private static void cullFace(int mode) {
        if (mode != GlStateManager.cullState.mode) {
            GlStateManager.cullState.mode = mode;
            GL11.glCullFace((int)mode);
        }
    }

    public static void glPolygonMode(int face, int mode) {
        GL11.glPolygonMode((int)face, (int)mode);
    }

    public static void enablePolygonOffset() {
        GlStateManager.polygonOffsetState.polygonOffsetFill.setEnabled();
    }

    public static void disablePolygonOffset() {
        GlStateManager.polygonOffsetState.polygonOffsetFill.setDisabled();
    }

    public static void doPolygonOffset(float factor, float units) {
        if (factor != GlStateManager.polygonOffsetState.factor || units != GlStateManager.polygonOffsetState.units) {
            GlStateManager.polygonOffsetState.factor = factor;
            GlStateManager.polygonOffsetState.units = units;
            GL11.glPolygonOffset((float)factor, (float)units);
        }
    }

    public static void enableColorLogic() {
        GlStateManager.colorLogicState.colorLogicOp.setEnabled();
    }

    public static void disableColorLogic() {
        GlStateManager.colorLogicState.colorLogicOp.setDisabled();
    }

    public static void colorLogicOp(LogicOp logicOperation) {
        GlStateManager.colorLogicOp(logicOperation.opcode);
    }

    public static void colorLogicOp(int opcode) {
        if (opcode != GlStateManager.colorLogicState.opcode) {
            GlStateManager.colorLogicState.opcode = opcode;
            GL11.glLogicOp((int)opcode);
        }
    }

    public static void enableTexGenCoord(TexGen texGen) {
        GlStateManager.texGenCoord((TexGen)texGen).textureGen.setEnabled();
    }

    public static void disableTexGenCoord(TexGen texGen) {
        GlStateManager.texGenCoord((TexGen)texGen).textureGen.setDisabled();
    }

    public static void texGen(TexGen texGen, int param) {
        TexGenCoord glstatemanager$texgencoord = GlStateManager.texGenCoord(texGen);
        if (param != glstatemanager$texgencoord.param) {
            glstatemanager$texgencoord.param = param;
            GL11.glTexGeni((int)glstatemanager$texgencoord.coord, (int)9472, (int)param);
        }
    }

    public static void texGen(TexGen texGen, int pname, FloatBuffer params) {
        GL11.glTexGen((int)GlStateManager.texGenCoord((TexGen)texGen).coord, (int)pname, (FloatBuffer)params);
    }

    private static TexGenCoord texGenCoord(TexGen texGen) {
        switch (texGen) {
            case S: {
                return GlStateManager.texGenState.s;
            }
            case T: {
                return GlStateManager.texGenState.t;
            }
            case R: {
                return GlStateManager.texGenState.r;
            }
            case Q: {
                return GlStateManager.texGenState.q;
            }
        }
        return GlStateManager.texGenState.s;
    }

    public static void setActiveTexture(int texture) {
        if (activeTextureUnit != texture - OpenGlHelper.defaultTexUnit) {
            activeTextureUnit = texture - OpenGlHelper.defaultTexUnit;
            OpenGlHelper.setActiveTexture(texture);
        }
    }

    public static void enableTexture2D() {
        GlStateManager.textureState[GlStateManager.activeTextureUnit].texture2DState.setEnabled();
    }

    public static void disableTexture2D() {
        GlStateManager.textureState[GlStateManager.activeTextureUnit].texture2DState.setDisabled();
    }

    public static void glTexEnv(int p_187448_0_, int p_187448_1_, FloatBuffer p_187448_2_) {
        GL11.glTexEnv((int)p_187448_0_, (int)p_187448_1_, (FloatBuffer)p_187448_2_);
    }

    public static void glTexEnvi(int p_187399_0_, int p_187399_1_, int p_187399_2_) {
        GL11.glTexEnvi((int)p_187399_0_, (int)p_187399_1_, (int)p_187399_2_);
    }

    public static void glTexEnvf(int p_187436_0_, int p_187436_1_, float p_187436_2_) {
        GL11.glTexEnvf((int)p_187436_0_, (int)p_187436_1_, (float)p_187436_2_);
    }

    public static void glTexParameterf(int p_187403_0_, int p_187403_1_, float p_187403_2_) {
        GL11.glTexParameterf((int)p_187403_0_, (int)p_187403_1_, (float)p_187403_2_);
    }

    public static void glTexParameteri(int p_187421_0_, int p_187421_1_, int p_187421_2_) {
        GL11.glTexParameteri((int)p_187421_0_, (int)p_187421_1_, (int)p_187421_2_);
    }

    public static int glGetTexLevelParameteri(int p_187411_0_, int p_187411_1_, int p_187411_2_) {
        return GL11.glGetTexLevelParameteri((int)p_187411_0_, (int)p_187411_1_, (int)p_187411_2_);
    }

    public static int generateTexture() {
        return GL11.glGenTextures();
    }

    public static void deleteTexture(int texture) {
        if (texture != 0) {
            GL11.glDeleteTextures((int)texture);
            TextureState[] textureStateArray = textureState;
            int n = textureState.length;
            int n2 = 0;
            while (n2 < n) {
                TextureState glstatemanager$texturestate = textureStateArray[n2];
                if (glstatemanager$texturestate.textureName == texture) {
                    glstatemanager$texturestate.textureName = 0;
                }
                ++n2;
            }
        }
    }

    public static void bindTexture(int texture) {
        if (texture != GlStateManager.textureState[GlStateManager.activeTextureUnit].textureName) {
            GlStateManager.textureState[GlStateManager.activeTextureUnit].textureName = texture;
            GL11.glBindTexture((int)3553, (int)texture);
        }
    }

    public static void glTexImage2D(int p_187419_0_, int p_187419_1_, int p_187419_2_, int p_187419_3_, int p_187419_4_, int p_187419_5_, int p_187419_6_, int p_187419_7_, @Nullable IntBuffer p_187419_8_) {
        GL11.glTexImage2D((int)p_187419_0_, (int)p_187419_1_, (int)p_187419_2_, (int)p_187419_3_, (int)p_187419_4_, (int)p_187419_5_, (int)p_187419_6_, (int)p_187419_7_, (IntBuffer)p_187419_8_);
    }

    public static void glTexSubImage2D(int p_187414_0_, int p_187414_1_, int p_187414_2_, int p_187414_3_, int p_187414_4_, int p_187414_5_, int p_187414_6_, int p_187414_7_, IntBuffer p_187414_8_) {
        GL11.glTexSubImage2D((int)p_187414_0_, (int)p_187414_1_, (int)p_187414_2_, (int)p_187414_3_, (int)p_187414_4_, (int)p_187414_5_, (int)p_187414_6_, (int)p_187414_7_, (IntBuffer)p_187414_8_);
    }

    public static void glCopyTexSubImage2D(int p_187443_0_, int p_187443_1_, int p_187443_2_, int p_187443_3_, int p_187443_4_, int p_187443_5_, int p_187443_6_, int p_187443_7_) {
        GL11.glCopyTexSubImage2D((int)p_187443_0_, (int)p_187443_1_, (int)p_187443_2_, (int)p_187443_3_, (int)p_187443_4_, (int)p_187443_5_, (int)p_187443_6_, (int)p_187443_7_);
    }

    public static void glGetTexImage(int p_187433_0_, int p_187433_1_, int p_187433_2_, int p_187433_3_, IntBuffer p_187433_4_) {
        GL11.glGetTexImage((int)p_187433_0_, (int)p_187433_1_, (int)p_187433_2_, (int)p_187433_3_, (IntBuffer)p_187433_4_);
    }

    public static void enableNormalize() {
        normalizeState.setEnabled();
    }

    public static void disableNormalize() {
        normalizeState.setDisabled();
    }

    public static void shadeModel(int mode) {
        if (mode != activeShadeModel) {
            activeShadeModel = mode;
            GL11.glShadeModel((int)mode);
        }
    }

    public static void enableRescaleNormal() {
        rescaleNormalState.setEnabled();
    }

    public static void disableRescaleNormal() {
        rescaleNormalState.setDisabled();
    }

    public static void viewport(int x, int y, int width, int height) {
        GL11.glViewport((int)x, (int)y, (int)width, (int)height);
    }

    public static void colorMask(boolean red, boolean green, boolean blue, boolean alpha) {
        if (red != GlStateManager.colorMaskState.red || green != GlStateManager.colorMaskState.green || blue != GlStateManager.colorMaskState.blue || alpha != GlStateManager.colorMaskState.alpha) {
            GlStateManager.colorMaskState.red = red;
            GlStateManager.colorMaskState.green = green;
            GlStateManager.colorMaskState.blue = blue;
            GlStateManager.colorMaskState.alpha = alpha;
            GL11.glColorMask((boolean)red, (boolean)green, (boolean)blue, (boolean)alpha);
        }
    }

    public static void clearDepth(double depth) {
        if (depth != GlStateManager.clearState.depth) {
            GlStateManager.clearState.depth = depth;
            GL11.glClearDepth((double)depth);
        }
    }

    public static void clearColor(float red, float green, float blue, float alpha) {
        if (red != GlStateManager.clearState.color.red || green != GlStateManager.clearState.color.green || blue != GlStateManager.clearState.color.blue || alpha != GlStateManager.clearState.color.alpha) {
            GlStateManager.clearState.color.red = red;
            GlStateManager.clearState.color.green = green;
            GlStateManager.clearState.color.blue = blue;
            GlStateManager.clearState.color.alpha = alpha;
            GL11.glClearColor((float)red, (float)green, (float)blue, (float)alpha);
        }
    }

    public static void clear(int mask) {
        if (clearEnabled) {
            GL11.glClear((int)mask);
        }
    }

    public static void matrixMode(int mode) {
        GL11.glMatrixMode((int)mode);
    }

    public static void loadIdentity() {
        GL11.glLoadIdentity();
    }

    public static void pushMatrix() {
        GL11.glPushMatrix();
    }

    public static void popMatrix() {
        GL11.glPopMatrix();
    }

    public static void getFloat(int pname, FloatBuffer params) {
        GL11.glGetFloat((int)pname, (FloatBuffer)params);
    }

    public static void ortho(double left, double right, double bottom, double top, double zNear, double zFar) {
        GL11.glOrtho((double)left, (double)right, (double)bottom, (double)top, (double)zNear, (double)zFar);
    }

    public static void rotate(float angle, float x, float y, float z) {
        GL11.glRotatef((float)angle, (float)x, (float)y, (float)z);
    }

    public static void scale(float x, float y, float z) {
        GL11.glScalef((float)x, (float)y, (float)z);
    }

    public static void scale(double x, double y, double z) {
        GL11.glScaled((double)x, (double)y, (double)z);
    }

    public static void translate(float x, float y, float z) {
        GL11.glTranslatef((float)x, (float)y, (float)z);
    }

    public static void translate(double x, double y, double z) {
        GL11.glTranslated((double)x, (double)y, (double)z);
    }

    public static void multMatrix(FloatBuffer matrix) {
        GL11.glMultMatrix((FloatBuffer)matrix);
    }

    public static void rotate(Quaternion p_187444_0_) {
        GlStateManager.multMatrix(GlStateManager.quatToGlMatrix(BUF_FLOAT_16, p_187444_0_));
    }

    public static FloatBuffer quatToGlMatrix(FloatBuffer p_187418_0_, Quaternion p_187418_1_) {
        p_187418_0_.clear();
        float f = p_187418_1_.x * p_187418_1_.x;
        float f1 = p_187418_1_.x * p_187418_1_.y;
        float f2 = p_187418_1_.x * p_187418_1_.z;
        float f3 = p_187418_1_.x * p_187418_1_.w;
        float f4 = p_187418_1_.y * p_187418_1_.y;
        float f5 = p_187418_1_.y * p_187418_1_.z;
        float f6 = p_187418_1_.y * p_187418_1_.w;
        float f7 = p_187418_1_.z * p_187418_1_.z;
        float f8 = p_187418_1_.z * p_187418_1_.w;
        p_187418_0_.put(1.0f - 2.0f * (f4 + f7));
        p_187418_0_.put(2.0f * (f1 + f8));
        p_187418_0_.put(2.0f * (f2 - f6));
        p_187418_0_.put(0.0f);
        p_187418_0_.put(2.0f * (f1 - f8));
        p_187418_0_.put(1.0f - 2.0f * (f + f7));
        p_187418_0_.put(2.0f * (f5 + f3));
        p_187418_0_.put(0.0f);
        p_187418_0_.put(2.0f * (f2 + f6));
        p_187418_0_.put(2.0f * (f5 - f3));
        p_187418_0_.put(1.0f - 2.0f * (f + f4));
        p_187418_0_.put(0.0f);
        p_187418_0_.put(0.0f);
        p_187418_0_.put(0.0f);
        p_187418_0_.put(0.0f);
        p_187418_0_.put(1.0f);
        p_187418_0_.rewind();
        return p_187418_0_;
    }

    public static void color(float colorRed, float colorGreen, float colorBlue, float colorAlpha) {
        if (colorRed != GlStateManager.colorState.red || colorGreen != GlStateManager.colorState.green || colorBlue != GlStateManager.colorState.blue || colorAlpha != GlStateManager.colorState.alpha) {
            GlStateManager.colorState.red = colorRed;
            GlStateManager.colorState.green = colorGreen;
            GlStateManager.colorState.blue = colorBlue;
            GlStateManager.colorState.alpha = colorAlpha;
            GL11.glColor4f((float)colorRed, (float)colorGreen, (float)colorBlue, (float)colorAlpha);
        }
    }

    public static void color(float colorRed, float colorGreen, float colorBlue) {
        GlStateManager.color(colorRed, colorGreen, colorBlue, 1.0f);
    }

    public static void glTexCoord2f(float p_187426_0_, float p_187426_1_) {
        GL11.glTexCoord2f((float)p_187426_0_, (float)p_187426_1_);
    }

    public static void glVertex3f(float p_187435_0_, float p_187435_1_, float p_187435_2_) {
        GL11.glVertex3f((float)p_187435_0_, (float)p_187435_1_, (float)p_187435_2_);
    }

    public static void resetColor() {
        GlStateManager.colorState.red = -1.0f;
        GlStateManager.colorState.green = -1.0f;
        GlStateManager.colorState.blue = -1.0f;
        GlStateManager.colorState.alpha = -1.0f;
    }

    public static void glNormalPointer(int p_187446_0_, int p_187446_1_, ByteBuffer p_187446_2_) {
        GL11.glNormalPointer((int)p_187446_0_, (int)p_187446_1_, (ByteBuffer)p_187446_2_);
    }

    public static void glTexCoordPointer(int p_187405_0_, int p_187405_1_, int p_187405_2_, int p_187405_3_) {
        GL11.glTexCoordPointer((int)p_187405_0_, (int)p_187405_1_, (int)p_187405_2_, (long)p_187405_3_);
    }

    public static void glTexCoordPointer(int p_187404_0_, int p_187404_1_, int p_187404_2_, ByteBuffer p_187404_3_) {
        GL11.glTexCoordPointer((int)p_187404_0_, (int)p_187404_1_, (int)p_187404_2_, (ByteBuffer)p_187404_3_);
    }

    public static void glVertexPointer(int p_187420_0_, int p_187420_1_, int p_187420_2_, int p_187420_3_) {
        GL11.glVertexPointer((int)p_187420_0_, (int)p_187420_1_, (int)p_187420_2_, (long)p_187420_3_);
    }

    public static void glVertexPointer(int p_187427_0_, int p_187427_1_, int p_187427_2_, ByteBuffer p_187427_3_) {
        GL11.glVertexPointer((int)p_187427_0_, (int)p_187427_1_, (int)p_187427_2_, (ByteBuffer)p_187427_3_);
    }

    public static void glColorPointer(int p_187406_0_, int p_187406_1_, int p_187406_2_, int p_187406_3_) {
        GL11.glColorPointer((int)p_187406_0_, (int)p_187406_1_, (int)p_187406_2_, (long)p_187406_3_);
    }

    public static void glColorPointer(int p_187400_0_, int p_187400_1_, int p_187400_2_, ByteBuffer p_187400_3_) {
        GL11.glColorPointer((int)p_187400_0_, (int)p_187400_1_, (int)p_187400_2_, (ByteBuffer)p_187400_3_);
    }

    public static void glDisableClientState(int p_187429_0_) {
        GL11.glDisableClientState((int)p_187429_0_);
    }

    public static void glEnableClientState(int p_187410_0_) {
        GL11.glEnableClientState((int)p_187410_0_);
    }

    public static void glBegin(int p_187447_0_) {
        GL11.glBegin((int)p_187447_0_);
    }

    public static void glEnd() {
        GL11.glEnd();
    }

    public static void glDrawArrays(int p_187439_0_, int p_187439_1_, int p_187439_2_) {
        GL11.glDrawArrays((int)p_187439_0_, (int)p_187439_1_, (int)p_187439_2_);
    }

    public static void glLineWidth(float p_187441_0_) {
        GL11.glLineWidth((float)p_187441_0_);
    }

    public static void callList(int list) {
        GL11.glCallList((int)list);
    }

    public static void glDeleteLists(int p_187449_0_, int p_187449_1_) {
        GL11.glDeleteLists((int)p_187449_0_, (int)p_187449_1_);
    }

    public static void glNewList(int p_187423_0_, int p_187423_1_) {
        GL11.glNewList((int)p_187423_0_, (int)p_187423_1_);
    }

    public static void glEndList() {
        GL11.glEndList();
    }

    public static int glGenLists(int p_187442_0_) {
        return GL11.glGenLists((int)p_187442_0_);
    }

    public static void glPixelStorei(int p_187425_0_, int p_187425_1_) {
        GL11.glPixelStorei((int)p_187425_0_, (int)p_187425_1_);
    }

    public static void glReadPixels(int p_187413_0_, int p_187413_1_, int p_187413_2_, int p_187413_3_, int p_187413_4_, int p_187413_5_, IntBuffer p_187413_6_) {
        GL11.glReadPixels((int)p_187413_0_, (int)p_187413_1_, (int)p_187413_2_, (int)p_187413_3_, (int)p_187413_4_, (int)p_187413_5_, (IntBuffer)p_187413_6_);
    }

    public static int glGetError() {
        return GL11.glGetError();
    }

    public static String glGetString(int p_187416_0_) {
        return GL11.glGetString((int)p_187416_0_);
    }

    public static void glGetInteger(int p_187445_0_, IntBuffer p_187445_1_) {
        GL11.glGetInteger((int)p_187445_0_, (IntBuffer)p_187445_1_);
    }

    public static int glGetInteger(int p_187397_0_) {
        return GL11.glGetInteger((int)p_187397_0_);
    }

    public static void enableBlendProfile(Profile p_187408_0_) {
        p_187408_0_.apply();
    }

    public static void disableBlendProfile(Profile p_187440_0_) {
        p_187440_0_.clean();
    }

    public static int getActiveTextureUnit() {
        return OpenGlHelper.defaultTexUnit + activeTextureUnit;
    }

    public static void bindCurrentTexture() {
        GL11.glBindTexture((int)3553, (int)GlStateManager.textureState[GlStateManager.activeTextureUnit].textureName);
    }

    public static int getBoundTexture() {
        return GlStateManager.textureState[GlStateManager.activeTextureUnit].textureName;
    }

    public static void checkBoundTexture() {
        if (Config.isMinecraftThread()) {
            int i = GL11.glGetInteger((int)34016);
            int j = GL11.glGetInteger((int)32873);
            int k = GlStateManager.getActiveTextureUnit();
            int l = GlStateManager.getBoundTexture();
            if (l > 0 && (i != k || j != l)) {
                Config.dbg("checkTexture: act: " + k + ", glAct: " + i + ", tex: " + l + ", glTex: " + j);
            }
        }
    }

    public static void deleteTextures(IntBuffer p_deleteTextures_0_) {
        p_deleteTextures_0_.rewind();
        while (p_deleteTextures_0_.position() < p_deleteTextures_0_.limit()) {
            int i = p_deleteTextures_0_.get();
            GlStateManager.deleteTexture(i);
        }
        p_deleteTextures_0_.rewind();
    }

    public static boolean isFogEnabled() {
        return GlStateManager.fogState.fog.currentState;
    }

    public static void setFogEnabled(boolean p_setFogEnabled_0_) {
        GlStateManager.fogState.fog.setState(p_setFogEnabled_0_);
    }

    static class AlphaState {
        public BooleanState alphaTest = new BooleanState(3008);
        public int func = 519;
        public float ref = -1.0f;

        private AlphaState() {
        }
    }

    static class BlendState {
        public BooleanState blend = new BooleanState(3042);
        public int srcFactor = 1;
        public int dstFactor = 0;
        public int srcFactorAlpha = 1;
        public int dstFactorAlpha = 0;

        private BlendState() {
        }
    }

    static class BooleanState {
        private final int capability;
        private boolean currentState;

        public BooleanState(int capabilityIn) {
            this.capability = capabilityIn;
        }

        public void setDisabled() {
            this.setState(false);
        }

        public void setEnabled() {
            this.setState(true);
        }

        public void setState(boolean state) {
            if (state != this.currentState) {
                this.currentState = state;
                if (state) {
                    GL11.glEnable((int)this.capability);
                } else {
                    GL11.glDisable((int)this.capability);
                }
            }
        }
    }

    static class ClearState {
        public double depth = 1.0;
        public Color color = new Color(0.0f, 0.0f, 0.0f, 0.0f);

        private ClearState() {
        }
    }

    static class Color {
        public float red = 1.0f;
        public float green = 1.0f;
        public float blue = 1.0f;
        public float alpha = 1.0f;

        public Color() {
            this(1.0f, 1.0f, 1.0f, 1.0f);
        }

        public Color(float redIn, float greenIn, float blueIn, float alphaIn) {
            this.red = redIn;
            this.green = greenIn;
            this.blue = blueIn;
            this.alpha = alphaIn;
        }
    }

    static class ColorLogicState {
        public BooleanState colorLogicOp = new BooleanState(3058);
        public int opcode = 5379;

        private ColorLogicState() {
        }
    }

    static class ColorMask {
        public boolean red = true;
        public boolean green = true;
        public boolean blue = true;
        public boolean alpha = true;

        private ColorMask() {
        }
    }

    static class ColorMaterialState {
        public BooleanState colorMaterial = new BooleanState(2903);
        public int face = 1032;
        public int mode = 5634;

        private ColorMaterialState() {
        }
    }

    public static enum CullFace {
        FRONT(1028),
        BACK(1029),
        FRONT_AND_BACK(1032);

        public final int mode;

        private CullFace(int modeIn) {
            this.mode = modeIn;
        }
    }

    static class CullState {
        public BooleanState cullFace = new BooleanState(2884);
        public int mode = 1029;

        private CullState() {
        }
    }

    static class DepthState {
        public BooleanState depthTest = new BooleanState(2929);
        public boolean maskEnabled = true;
        public int depthFunc = 513;

        private DepthState() {
        }
    }

    public static enum DestFactor {
        CONSTANT_ALPHA(32771),
        CONSTANT_COLOR(32769),
        DST_ALPHA(772),
        DST_COLOR(774),
        ONE(1),
        ONE_MINUS_CONSTANT_ALPHA(32772),
        ONE_MINUS_CONSTANT_COLOR(32770),
        ONE_MINUS_DST_ALPHA(773),
        ONE_MINUS_DST_COLOR(775),
        ONE_MINUS_SRC_ALPHA(771),
        ONE_MINUS_SRC_COLOR(769),
        SRC_ALPHA(770),
        SRC_COLOR(768),
        ZERO(0);

        public final int factor;

        private DestFactor(int factorIn) {
            this.factor = factorIn;
        }
    }

    public static enum FogMode {
        LINEAR(9729),
        EXP(2048),
        EXP2(2049);

        public final int capabilityId;

        private FogMode(int capabilityIn) {
            this.capabilityId = capabilityIn;
        }
    }

    static class FogState {
        public BooleanState fog = new BooleanState(2912);
        public int mode = 2048;
        public float density = 1.0f;
        public float start;
        public float end = 1.0f;

        private FogState() {
        }
    }

    public static enum LogicOp {
        AND(5377),
        AND_INVERTED(5380),
        AND_REVERSE(5378),
        CLEAR(5376),
        COPY(5379),
        COPY_INVERTED(5388),
        EQUIV(5385),
        INVERT(5386),
        NAND(5390),
        NOOP(5381),
        NOR(5384),
        OR(5383),
        OR_INVERTED(5389),
        OR_REVERSE(5387),
        SET(5391),
        XOR(5382);

        public final int opcode;

        private LogicOp(int opcodeIn) {
            this.opcode = opcodeIn;
        }
    }

    static class PolygonOffsetState {
        public BooleanState polygonOffsetFill = new BooleanState(32823);
        public BooleanState polygonOffsetLine = new BooleanState(10754);
        public float factor;
        public float units;

        private PolygonOffsetState() {
        }
    }

    public static enum Profile {
        DEFAULT{

            @Override
            public void apply() {
                GlStateManager.disableAlpha();
                GlStateManager.alphaFunc(519, 0.0f);
                GlStateManager.disableLighting();
                GL11.glLightModel((int)2899, (FloatBuffer)RenderHelper.setColorBuffer(0.2f, 0.2f, 0.2f, 1.0f));
                int i = 0;
                while (i < 8) {
                    GlStateManager.disableLight(i);
                    GL11.glLight((int)(16384 + i), (int)4608, (FloatBuffer)RenderHelper.setColorBuffer(0.0f, 0.0f, 0.0f, 1.0f));
                    GL11.glLight((int)(16384 + i), (int)4611, (FloatBuffer)RenderHelper.setColorBuffer(0.0f, 0.0f, 1.0f, 0.0f));
                    if (i == 0) {
                        GL11.glLight((int)(16384 + i), (int)4609, (FloatBuffer)RenderHelper.setColorBuffer(1.0f, 1.0f, 1.0f, 1.0f));
                        GL11.glLight((int)(16384 + i), (int)4610, (FloatBuffer)RenderHelper.setColorBuffer(1.0f, 1.0f, 1.0f, 1.0f));
                    } else {
                        GL11.glLight((int)(16384 + i), (int)4609, (FloatBuffer)RenderHelper.setColorBuffer(0.0f, 0.0f, 0.0f, 1.0f));
                        GL11.glLight((int)(16384 + i), (int)4610, (FloatBuffer)RenderHelper.setColorBuffer(0.0f, 0.0f, 0.0f, 1.0f));
                    }
                    ++i;
                }
                GlStateManager.disableColorMaterial();
                GlStateManager.colorMaterial(1032, 5634);
                GlStateManager.disableDepth();
                GlStateManager.depthFunc(513);
                GlStateManager.depthMask(true);
                GlStateManager.disableBlend();
                GlStateManager.blendFunc(SourceFactor.ONE, DestFactor.ZERO);
                GlStateManager.tryBlendFuncSeparate(SourceFactor.ONE, DestFactor.ZERO, SourceFactor.ONE, DestFactor.ZERO);
                GL14.glBlendEquation((int)32774);
                GlStateManager.disableFog();
                GL11.glFogi((int)2917, (int)2048);
                GlStateManager.setFogDensity(1.0f);
                GlStateManager.setFogStart(0.0f);
                GlStateManager.setFogEnd(1.0f);
                GL11.glFog((int)2918, (FloatBuffer)RenderHelper.setColorBuffer(0.0f, 0.0f, 0.0f, 0.0f));
                if (GLContext.getCapabilities().GL_NV_fog_distance) {
                    GL11.glFogi((int)2917, (int)34140);
                }
                GlStateManager.doPolygonOffset(0.0f, 0.0f);
                GlStateManager.disableColorLogic();
                GlStateManager.colorLogicOp(5379);
                GlStateManager.disableTexGenCoord(TexGen.S);
                GlStateManager.texGen(TexGen.S, 9216);
                GlStateManager.texGen(TexGen.S, 9474, RenderHelper.setColorBuffer(1.0f, 0.0f, 0.0f, 0.0f));
                GlStateManager.texGen(TexGen.S, 9217, RenderHelper.setColorBuffer(1.0f, 0.0f, 0.0f, 0.0f));
                GlStateManager.disableTexGenCoord(TexGen.T);
                GlStateManager.texGen(TexGen.T, 9216);
                GlStateManager.texGen(TexGen.T, 9474, RenderHelper.setColorBuffer(0.0f, 1.0f, 0.0f, 0.0f));
                GlStateManager.texGen(TexGen.T, 9217, RenderHelper.setColorBuffer(0.0f, 1.0f, 0.0f, 0.0f));
                GlStateManager.disableTexGenCoord(TexGen.R);
                GlStateManager.texGen(TexGen.R, 9216);
                GlStateManager.texGen(TexGen.R, 9474, RenderHelper.setColorBuffer(0.0f, 0.0f, 0.0f, 0.0f));
                GlStateManager.texGen(TexGen.R, 9217, RenderHelper.setColorBuffer(0.0f, 0.0f, 0.0f, 0.0f));
                GlStateManager.disableTexGenCoord(TexGen.Q);
                GlStateManager.texGen(TexGen.Q, 9216);
                GlStateManager.texGen(TexGen.Q, 9474, RenderHelper.setColorBuffer(0.0f, 0.0f, 0.0f, 0.0f));
                GlStateManager.texGen(TexGen.Q, 9217, RenderHelper.setColorBuffer(0.0f, 0.0f, 0.0f, 0.0f));
                GlStateManager.setActiveTexture(0);
                GL11.glTexParameteri((int)3553, (int)10240, (int)9729);
                GL11.glTexParameteri((int)3553, (int)10241, (int)9986);
                GL11.glTexParameteri((int)3553, (int)10242, (int)10497);
                GL11.glTexParameteri((int)3553, (int)10243, (int)10497);
                GL11.glTexParameteri((int)3553, (int)33085, (int)1000);
                GL11.glTexParameteri((int)3553, (int)33083, (int)1000);
                GL11.glTexParameteri((int)3553, (int)33082, (int)-1000);
                GL11.glTexParameterf((int)3553, (int)34049, (float)0.0f);
                GL11.glTexEnvi((int)8960, (int)8704, (int)8448);
                GL11.glTexEnv((int)8960, (int)8705, (FloatBuffer)RenderHelper.setColorBuffer(0.0f, 0.0f, 0.0f, 0.0f));
                GL11.glTexEnvi((int)8960, (int)34161, (int)8448);
                GL11.glTexEnvi((int)8960, (int)34162, (int)8448);
                GL11.glTexEnvi((int)8960, (int)34176, (int)5890);
                GL11.glTexEnvi((int)8960, (int)34177, (int)34168);
                GL11.glTexEnvi((int)8960, (int)34178, (int)34166);
                GL11.glTexEnvi((int)8960, (int)34184, (int)5890);
                GL11.glTexEnvi((int)8960, (int)34185, (int)34168);
                GL11.glTexEnvi((int)8960, (int)34186, (int)34166);
                GL11.glTexEnvi((int)8960, (int)34192, (int)768);
                GL11.glTexEnvi((int)8960, (int)34193, (int)768);
                GL11.glTexEnvi((int)8960, (int)34194, (int)770);
                GL11.glTexEnvi((int)8960, (int)34200, (int)770);
                GL11.glTexEnvi((int)8960, (int)34201, (int)770);
                GL11.glTexEnvi((int)8960, (int)34202, (int)770);
                GL11.glTexEnvf((int)8960, (int)34163, (float)1.0f);
                GL11.glTexEnvf((int)8960, (int)3356, (float)1.0f);
                GlStateManager.disableNormalize();
                GlStateManager.shadeModel(7425);
                GlStateManager.disableRescaleNormal();
                GlStateManager.colorMask(true, true, true, true);
                GlStateManager.clearDepth(1.0);
                GL11.glLineWidth((float)1.0f);
                GL11.glNormal3f((float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glPolygonMode((int)1028, (int)6914);
                GL11.glPolygonMode((int)1029, (int)6914);
            }

            @Override
            public void clean() {
            }
        }
        ,
        PLAYER_SKIN{

            @Override
            public void apply() {
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            }

            @Override
            public void clean() {
                GlStateManager.disableBlend();
            }
        }
        ,
        TRANSPARENT_MODEL{

            @Override
            public void apply() {
                GlStateManager.color(1.0f, 1.0f, 1.0f, 0.15f);
                GlStateManager.depthMask(false);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
                GlStateManager.alphaFunc(516, 0.003921569f);
            }

            @Override
            public void clean() {
                GlStateManager.disableBlend();
                GlStateManager.alphaFunc(516, 0.1f);
                GlStateManager.depthMask(true);
            }
        };


        public abstract void apply();

        public abstract void clean();
    }

    public static enum SourceFactor {
        CONSTANT_ALPHA(32771),
        CONSTANT_COLOR(32769),
        DST_ALPHA(772),
        DST_COLOR(774),
        ONE(1),
        ONE_MINUS_CONSTANT_ALPHA(32772),
        ONE_MINUS_CONSTANT_COLOR(32770),
        ONE_MINUS_DST_ALPHA(773),
        ONE_MINUS_DST_COLOR(775),
        ONE_MINUS_SRC_ALPHA(771),
        ONE_MINUS_SRC_COLOR(769),
        SRC_ALPHA(770),
        SRC_ALPHA_SATURATE(776),
        SRC_COLOR(768),
        ZERO(0);

        public final int factor;

        private SourceFactor(int factorIn) {
            this.factor = factorIn;
        }
    }

    static class StencilFunc {
        public int func = 519;
        public int mask = -1;

        private StencilFunc() {
        }
    }

    static class StencilState {
        public StencilFunc func = new StencilFunc();
        public int mask = -1;
        public int fail = 7680;
        public int zfail = 7680;
        public int zpass = 7680;

        private StencilState() {
        }
    }

    public static enum TexGen {
        S,
        T,
        R,
        Q;

    }

    static class TexGenCoord {
        public BooleanState textureGen;
        public int coord;
        public int param = -1;

        public TexGenCoord(int coordIn, int capabilityIn) {
            this.coord = coordIn;
            this.textureGen = new BooleanState(capabilityIn);
        }
    }

    static class TexGenState {
        public TexGenCoord s = new TexGenCoord(8192, 3168);
        public TexGenCoord t = new TexGenCoord(8193, 3169);
        public TexGenCoord r = new TexGenCoord(8194, 3170);
        public TexGenCoord q = new TexGenCoord(8195, 3171);

        private TexGenState() {
        }
    }

    static class TextureState {
        public BooleanState texture2DState = new BooleanState(3553);
        public int textureName;

        private TextureState() {
        }
    }
}

