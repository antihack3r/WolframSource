/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.IOUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.renderer.texture;

import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.src.Config;
import net.minecraft.src.Mipmaps;
import net.minecraft.src.Reflector;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextureUtil {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final IntBuffer DATA_BUFFER = GLAllocation.createDirectIntBuffer(0x400000);
    public static final DynamicTexture MISSING_TEXTURE = new DynamicTexture(16, 16);
    public static final int[] MISSING_TEXTURE_DATA = MISSING_TEXTURE.getTextureData();
    private static final float[] COLOR_GAMMAS;
    private static final int[] MIPMAP_BUFFER;

    static {
        int i = -16777216;
        int j = -524040;
        int[] aint = new int[]{-524040, -524040, -524040, -524040, -524040, -524040, -524040, -524040};
        int[] aint1 = new int[]{-16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216};
        int k = aint.length;
        int l = 0;
        while (l < 16) {
            System.arraycopy(l < k ? aint : aint1, 0, MISSING_TEXTURE_DATA, 16 * l, k);
            System.arraycopy(l < k ? aint1 : aint, 0, MISSING_TEXTURE_DATA, 16 * l + k, k);
            ++l;
        }
        MISSING_TEXTURE.updateDynamicTexture();
        COLOR_GAMMAS = new float[256];
        int i1 = 0;
        while (i1 < COLOR_GAMMAS.length) {
            TextureUtil.COLOR_GAMMAS[i1] = (float)Math.pow((float)i1 / 255.0f, 2.2);
            ++i1;
        }
        MIPMAP_BUFFER = new int[4];
    }

    private static float getColorGamma(int p_188543_0_) {
        return COLOR_GAMMAS[p_188543_0_ & 0xFF];
    }

    public static int glGenTextures() {
        return GlStateManager.generateTexture();
    }

    public static void deleteTexture(int textureId) {
        GlStateManager.deleteTexture(textureId);
    }

    public static int uploadTextureImage(int textureId, BufferedImage texture) {
        return TextureUtil.uploadTextureImageAllocate(textureId, texture, false, false);
    }

    public static void uploadTexture(int textureId, int[] p_110988_1_, int p_110988_2_, int p_110988_3_) {
        TextureUtil.bindTexture(textureId);
        TextureUtil.uploadTextureSub(0, p_110988_1_, p_110988_2_, p_110988_3_, 0, 0, false, false, false);
    }

    public static int[][] generateMipmapData(int p_147949_0_, int p_147949_1_, int[][] p_147949_2_) {
        int[][] aint = new int[p_147949_0_ + 1][];
        aint[0] = p_147949_2_[0];
        if (p_147949_0_ > 0) {
            boolean flag = false;
            int i = 0;
            while (i < p_147949_2_.length) {
                if (p_147949_2_[0][i] >> 24 == 0) {
                    flag = true;
                    break;
                }
                ++i;
            }
            int l1 = 1;
            while (l1 <= p_147949_0_) {
                if (p_147949_2_[l1] != null) {
                    aint[l1] = p_147949_2_[l1];
                } else {
                    int[] aint1 = aint[l1 - 1];
                    int[] aint2 = new int[aint1.length >> 2];
                    int j = p_147949_1_ >> l1;
                    int k = aint2.length / j;
                    int l = j << 1;
                    int i1 = 0;
                    while (i1 < j) {
                        int j1 = 0;
                        while (j1 < k) {
                            int k1 = 2 * (i1 + j1 * l);
                            aint2[i1 + j1 * j] = TextureUtil.blendColors(aint1[k1 + 0], aint1[k1 + 1], aint1[k1 + 0 + l], aint1[k1 + 1 + l], flag);
                            ++j1;
                        }
                        ++i1;
                    }
                    aint[l1] = aint2;
                }
                ++l1;
            }
        }
        return aint;
    }

    private static int blendColors(int p_147943_0_, int p_147943_1_, int p_147943_2_, int p_147943_3_, boolean p_147943_4_) {
        return Mipmaps.alphaBlend(p_147943_0_, p_147943_1_, p_147943_2_, p_147943_3_);
    }

    private static int blendColorComponent(int p_147944_0_, int p_147944_1_, int p_147944_2_, int p_147944_3_, int p_147944_4_) {
        float f = TextureUtil.getColorGamma(p_147944_0_ >> p_147944_4_);
        float f1 = TextureUtil.getColorGamma(p_147944_1_ >> p_147944_4_);
        float f2 = TextureUtil.getColorGamma(p_147944_2_ >> p_147944_4_);
        float f3 = TextureUtil.getColorGamma(p_147944_3_ >> p_147944_4_);
        float f4 = (float)((double)((float)Math.pow((double)(f + f1 + f2 + f3) * 0.25, 0.45454545454545453)));
        return (int)((double)f4 * 255.0);
    }

    public static void uploadTextureMipmap(int[][] p_147955_0_, int p_147955_1_, int p_147955_2_, int p_147955_3_, int p_147955_4_, boolean p_147955_5_, boolean p_147955_6_) {
        int i = 0;
        while (i < p_147955_0_.length) {
            int[] aint = p_147955_0_[i];
            TextureUtil.uploadTextureSub(i, aint, p_147955_1_ >> i, p_147955_2_ >> i, p_147955_3_ >> i, p_147955_4_ >> i, p_147955_5_, p_147955_6_, p_147955_0_.length > 1);
            ++i;
        }
    }

    private static void uploadTextureSub(int p_147947_0_, int[] p_147947_1_, int p_147947_2_, int p_147947_3_, int p_147947_4_, int p_147947_5_, boolean p_147947_6_, boolean p_147947_7_, boolean p_147947_8_) {
        int i = 0x400000 / p_147947_2_;
        TextureUtil.setTextureBlurMipmap(p_147947_6_, p_147947_8_);
        TextureUtil.setTextureClamped(p_147947_7_);
        int k = 0;
        while (k < p_147947_2_ * p_147947_3_) {
            int l = k / p_147947_2_;
            int j = Math.min(i, p_147947_3_ - l);
            int i1 = p_147947_2_ * j;
            TextureUtil.copyToBufferPos(p_147947_1_, k, i1);
            GlStateManager.glTexSubImage2D(3553, p_147947_0_, p_147947_4_, p_147947_5_ + l, p_147947_2_, j, 32993, 33639, DATA_BUFFER);
            k += p_147947_2_ * j;
        }
    }

    public static int uploadTextureImageAllocate(int textureId, BufferedImage texture, boolean blur, boolean clamp) {
        TextureUtil.allocateTexture(textureId, texture.getWidth(), texture.getHeight());
        return TextureUtil.uploadTextureImageSub(textureId, texture, 0, 0, blur, clamp);
    }

    public static void allocateTexture(int textureId, int width, int height) {
        TextureUtil.allocateTextureImpl(textureId, 0, width, height);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void allocateTextureImpl(int glTextureId, int mipmapLevels, int width, int height) {
        Class object = TextureUtil.class;
        if (Reflector.SplashScreen.exists()) {
            object = Reflector.SplashScreen.getTargetClass();
        }
        Class clazz = object;
        synchronized (clazz) {
            TextureUtil.deleteTexture(glTextureId);
            TextureUtil.bindTexture(glTextureId);
        }
        if (mipmapLevels >= 0) {
            GlStateManager.glTexParameteri(3553, 33085, mipmapLevels);
            GlStateManager.glTexParameteri(3553, 33082, 0);
            GlStateManager.glTexParameteri(3553, 33083, mipmapLevels);
            GlStateManager.glTexParameterf(3553, 34049, 0.0f);
        }
        int i = 0;
        while (i <= mipmapLevels) {
            GlStateManager.glTexImage2D(3553, i, 6408, width >> i, height >> i, 0, 32993, 33639, null);
            ++i;
        }
    }

    public static int uploadTextureImageSub(int textureId, BufferedImage p_110995_1_, int p_110995_2_, int p_110995_3_, boolean p_110995_4_, boolean p_110995_5_) {
        TextureUtil.bindTexture(textureId);
        TextureUtil.uploadTextureImageSubImpl(p_110995_1_, p_110995_2_, p_110995_3_, p_110995_4_, p_110995_5_);
        return textureId;
    }

    private static void uploadTextureImageSubImpl(BufferedImage p_110993_0_, int p_110993_1_, int p_110993_2_, boolean p_110993_3_, boolean p_110993_4_) {
        int i = p_110993_0_.getWidth();
        int j = p_110993_0_.getHeight();
        int k = 0x400000 / i;
        int[] aint = new int[k * i];
        TextureUtil.setTextureBlurred(p_110993_3_);
        TextureUtil.setTextureClamped(p_110993_4_);
        int l = 0;
        while (l < i * j) {
            int i1 = l / i;
            int j1 = Math.min(k, j - i1);
            int k1 = i * j1;
            p_110993_0_.getRGB(0, i1, i, j1, aint, 0, i);
            TextureUtil.copyToBuffer(aint, k1);
            GlStateManager.glTexSubImage2D(3553, 0, p_110993_1_, p_110993_2_ + i1, i, j1, 32993, 33639, DATA_BUFFER);
            l += i * k;
        }
    }

    public static void setTextureClamped(boolean p_110997_0_) {
        if (p_110997_0_) {
            GlStateManager.glTexParameteri(3553, 10242, 33071);
            GlStateManager.glTexParameteri(3553, 10243, 33071);
        } else {
            GlStateManager.glTexParameteri(3553, 10242, 10497);
            GlStateManager.glTexParameteri(3553, 10243, 10497);
        }
    }

    private static void setTextureBlurred(boolean p_147951_0_) {
        TextureUtil.setTextureBlurMipmap(p_147951_0_, false);
    }

    public static void setTextureBlurMipmap(boolean p_147954_0_, boolean p_147954_1_) {
        if (p_147954_0_) {
            GlStateManager.glTexParameteri(3553, 10241, p_147954_1_ ? 9987 : 9729);
            GlStateManager.glTexParameteri(3553, 10240, 9729);
        } else {
            int i = Config.getMipmapType();
            GlStateManager.glTexParameteri(3553, 10241, p_147954_1_ ? i : 9728);
            GlStateManager.glTexParameteri(3553, 10240, 9728);
        }
    }

    private static void copyToBuffer(int[] p_110990_0_, int p_110990_1_) {
        TextureUtil.copyToBufferPos(p_110990_0_, 0, p_110990_1_);
    }

    private static void copyToBufferPos(int[] p_110994_0_, int p_110994_1_, int p_110994_2_) {
        int[] aint = p_110994_0_;
        if (Minecraft.getMinecraft().gameSettings.anaglyph) {
            aint = TextureUtil.updateAnaglyph(p_110994_0_);
        }
        DATA_BUFFER.clear();
        DATA_BUFFER.put(aint, p_110994_1_, p_110994_2_);
        DATA_BUFFER.position(0).limit(p_110994_2_);
    }

    public static void bindTexture(int p_94277_0_) {
        GlStateManager.bindTexture(p_94277_0_);
    }

    public static int[] readImageData(IResourceManager resourceManager, ResourceLocation imageLocation) throws IOException {
        Object i;
        IResource iresource = null;
        try {
            iresource = resourceManager.getResource(imageLocation);
            BufferedImage bufferedimage = TextureUtil.readBufferedImage(iresource.getInputStream());
            if (bufferedimage != null) {
                int[] aint;
                int j = bufferedimage.getWidth();
                int i1 = bufferedimage.getHeight();
                int[] aint1 = new int[j * i1];
                bufferedimage.getRGB(0, 0, j, i1, aint1, 0, j);
                int[] nArray = aint = aint1;
                return nArray;
            }
            i = null;
        }
        finally {
            IOUtils.closeQuietly((Closeable)iresource);
        }
        return i;
    }

    public static BufferedImage readBufferedImage(InputStream imageStream) throws IOException {
        BufferedImage bufferedimage;
        if (imageStream == null) {
            return null;
        }
        try {
            bufferedimage = ImageIO.read(imageStream);
        }
        finally {
            IOUtils.closeQuietly((InputStream)imageStream);
        }
        return bufferedimage;
    }

    public static int[] updateAnaglyph(int[] p_110985_0_) {
        int[] aint = new int[p_110985_0_.length];
        int i = 0;
        while (i < p_110985_0_.length) {
            aint[i] = TextureUtil.anaglyphColor(p_110985_0_[i]);
            ++i;
        }
        return aint;
    }

    public static int anaglyphColor(int p_177054_0_) {
        int i = p_177054_0_ >> 24 & 0xFF;
        int j = p_177054_0_ >> 16 & 0xFF;
        int k = p_177054_0_ >> 8 & 0xFF;
        int l = p_177054_0_ & 0xFF;
        int i1 = (j * 30 + k * 59 + l * 11) / 100;
        int j1 = (j * 30 + k * 70) / 100;
        int k1 = (j * 30 + l * 70) / 100;
        return i << 24 | i1 << 16 | j1 << 8 | k1;
    }

    public static void processPixelValues(int[] p_147953_0_, int p_147953_1_, int p_147953_2_) {
        int[] aint = new int[p_147953_1_];
        int i = p_147953_2_ / 2;
        int j = 0;
        while (j < i) {
            System.arraycopy(p_147953_0_, j * p_147953_1_, aint, 0, p_147953_1_);
            System.arraycopy(p_147953_0_, (p_147953_2_ - 1 - j) * p_147953_1_, p_147953_0_, j * p_147953_1_, p_147953_1_);
            System.arraycopy(aint, 0, p_147953_0_, (p_147953_2_ - 1 - j) * p_147953_1_, p_147953_1_);
            ++j;
        }
    }
}

