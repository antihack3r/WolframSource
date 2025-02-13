/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.src;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.src.Blender;
import net.minecraft.src.Config;
import net.minecraft.src.CustomSkyLayer;
import net.minecraft.src.TextureUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class CustomSky {
    private static CustomSkyLayer[][] worldSkyLayers = null;

    public static void reset() {
        worldSkyLayers = null;
    }

    public static void update() {
        CustomSky.reset();
        if (Config.isCustomSky()) {
            worldSkyLayers = CustomSky.readCustomSkies();
        }
    }

    private static CustomSkyLayer[][] readCustomSkies() {
        CustomSkyLayer[][] acustomskylayer = new CustomSkyLayer[10][0];
        String s = "mcpatcher/sky/world";
        int i = -1;
        int j = 0;
        while (j < acustomskylayer.length) {
            String s1 = String.valueOf(s) + j + "/sky";
            ArrayList<CustomSkyLayer> list = new ArrayList<CustomSkyLayer>();
            int k = 1;
            while (k < 1000) {
                String s2 = String.valueOf(s1) + k + ".properties";
                try {
                    ResourceLocation resourcelocation = new ResourceLocation(s2);
                    InputStream inputstream = Config.getResourceStream(resourcelocation);
                    if (inputstream == null) break;
                    Properties properties = new Properties();
                    properties.load(inputstream);
                    inputstream.close();
                    Config.dbg("CustomSky properties: " + s2);
                    String s3 = String.valueOf(s1) + k + ".png";
                    CustomSkyLayer customskylayer = new CustomSkyLayer(properties, s3);
                    if (customskylayer.isValid(s2)) {
                        ResourceLocation resourcelocation1 = new ResourceLocation(customskylayer.source);
                        ITextureObject itextureobject = TextureUtils.getTexture(resourcelocation1);
                        if (itextureobject == null) {
                            Config.log("CustomSky: Texture not found: " + resourcelocation1);
                        } else {
                            customskylayer.textureId = itextureobject.getGlTextureId();
                            list.add(customskylayer);
                            inputstream.close();
                        }
                    }
                }
                catch (FileNotFoundException var15) {
                    break;
                }
                catch (IOException ioexception) {
                    ioexception.printStackTrace();
                }
                ++k;
            }
            if (list.size() > 0) {
                CustomSkyLayer[] acustomskylayer2 = list.toArray(new CustomSkyLayer[list.size()]);
                acustomskylayer[j] = acustomskylayer2;
                i = j;
            }
            ++j;
        }
        if (i < 0) {
            return null;
        }
        int l = i + 1;
        CustomSkyLayer[][] acustomskylayer1 = new CustomSkyLayer[l][0];
        int i1 = 0;
        while (i1 < acustomskylayer1.length) {
            acustomskylayer1[i1] = acustomskylayer[i1];
            ++i1;
        }
        return acustomskylayer1;
    }

    public static void renderSky(World p_renderSky_0_, TextureManager p_renderSky_1_, float p_renderSky_2_) {
        CustomSkyLayer[] acustomskylayer;
        int i;
        if (worldSkyLayers != null && (i = p_renderSky_0_.provider.getDimensionType().getId()) >= 0 && i < worldSkyLayers.length && (acustomskylayer = worldSkyLayers[i]) != null) {
            long j = p_renderSky_0_.getWorldTime();
            int k = (int)(j % 24000L);
            float f = p_renderSky_0_.getCelestialAngle(p_renderSky_2_);
            float f1 = p_renderSky_0_.getRainStrength(p_renderSky_2_);
            float f2 = p_renderSky_0_.getThunderStrength(p_renderSky_2_);
            if (f1 > 0.0f) {
                f2 /= f1;
            }
            int l = 0;
            while (l < acustomskylayer.length) {
                CustomSkyLayer customskylayer = acustomskylayer[l];
                if (customskylayer.isActive(p_renderSky_0_, k)) {
                    customskylayer.render(k, f, f1, f2);
                }
                ++l;
            }
            float f3 = 1.0f - f1;
            Blender.clearBlend(f3);
        }
    }

    public static boolean hasSkyLayers(World p_hasSkyLayers_0_) {
        if (worldSkyLayers == null) {
            return false;
        }
        if (Config.getGameSettings().renderDistanceChunks < 8) {
            return false;
        }
        int i = p_hasSkyLayers_0_.provider.getDimensionType().getId();
        if (i >= 0 && i < worldSkyLayers.length) {
            CustomSkyLayer[] acustomskylayer = worldSkyLayers[i];
            if (acustomskylayer == null) {
                return false;
            }
            return acustomskylayer.length > 0;
        }
        return false;
    }
}

