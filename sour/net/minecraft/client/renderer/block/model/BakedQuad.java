/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.block.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.src.Config;
import net.minecraft.src.QuadBounds;
import net.minecraft.src.Reflector;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.client.model.pipeline.IVertexProducer;

public class BakedQuad
implements IVertexProducer {
    protected int[] vertexData;
    protected final int tintIndex;
    protected EnumFacing face;
    protected TextureAtlasSprite sprite;
    private int[] vertexDataSingle = null;
    protected boolean applyDiffuseLighting = Reflector.ForgeHooksClient_fillNormal.exists();
    protected VertexFormat format = DefaultVertexFormats.ITEM;
    private QuadBounds quadBounds;

    public BakedQuad(int[] p_i0_1_, int p_i0_2_, EnumFacing p_i0_3_, TextureAtlasSprite p_i0_4_, boolean p_i0_5_, VertexFormat p_i0_6_) {
        this.vertexData = p_i0_1_;
        this.tintIndex = p_i0_2_;
        this.face = p_i0_3_;
        this.sprite = p_i0_4_;
        this.applyDiffuseLighting = p_i0_5_;
        this.format = p_i0_6_;
        this.fixVertexData();
    }

    public BakedQuad(int[] vertexDataIn, int tintIndexIn, EnumFacing faceIn, TextureAtlasSprite spriteIn) {
        this.vertexData = vertexDataIn;
        this.tintIndex = tintIndexIn;
        this.face = faceIn;
        this.sprite = spriteIn;
        this.fixVertexData();
    }

    public TextureAtlasSprite getSprite() {
        if (this.sprite == null) {
            this.sprite = BakedQuad.getSpriteByUv(this.getVertexData());
        }
        return this.sprite;
    }

    public int[] getVertexData() {
        this.fixVertexData();
        return this.vertexData;
    }

    public boolean hasTintIndex() {
        return this.tintIndex != -1;
    }

    public int getTintIndex() {
        return this.tintIndex;
    }

    public EnumFacing getFace() {
        if (this.face == null) {
            this.face = FaceBakery.getFacingFromVertexData(this.getVertexData());
        }
        return this.face;
    }

    public int[] getVertexDataSingle() {
        if (this.vertexDataSingle == null) {
            this.vertexDataSingle = BakedQuad.makeVertexDataSingle(this.getVertexData(), this.getSprite());
        }
        return this.vertexDataSingle;
    }

    private static int[] makeVertexDataSingle(int[] p_makeVertexDataSingle_0_, TextureAtlasSprite p_makeVertexDataSingle_1_) {
        int[] aint = (int[])p_makeVertexDataSingle_0_.clone();
        int i = p_makeVertexDataSingle_1_.sheetWidth / p_makeVertexDataSingle_1_.getIconWidth();
        int j = p_makeVertexDataSingle_1_.sheetHeight / p_makeVertexDataSingle_1_.getIconHeight();
        int k = aint.length / 4;
        int l = 0;
        while (l < 4) {
            int i1 = l * k;
            float f = Float.intBitsToFloat(aint[i1 + 4]);
            float f1 = Float.intBitsToFloat(aint[i1 + 4 + 1]);
            float f2 = p_makeVertexDataSingle_1_.toSingleU(f);
            float f3 = p_makeVertexDataSingle_1_.toSingleV(f1);
            aint[i1 + 4] = Float.floatToRawIntBits(f2);
            aint[i1 + 4 + 1] = Float.floatToRawIntBits(f3);
            ++l;
        }
        return aint;
    }

    @Override
    public void pipe(IVertexConsumer p_pipe_1_) {
        Reflector.callVoid(Reflector.LightUtil_putBakedQuad, p_pipe_1_, this);
    }

    public VertexFormat getFormat() {
        return this.format;
    }

    public boolean shouldApplyDiffuseLighting() {
        return this.applyDiffuseLighting;
    }

    private static TextureAtlasSprite getSpriteByUv(int[] p_getSpriteByUv_0_) {
        float f = 1.0f;
        float f1 = 1.0f;
        float f2 = 0.0f;
        float f3 = 0.0f;
        int i = p_getSpriteByUv_0_.length / 4;
        int j = 0;
        while (j < 4) {
            int k = j * i;
            float f4 = Float.intBitsToFloat(p_getSpriteByUv_0_[k + 4]);
            float f5 = Float.intBitsToFloat(p_getSpriteByUv_0_[k + 4 + 1]);
            f = Math.min(f, f4);
            f1 = Math.min(f1, f5);
            f2 = Math.max(f2, f4);
            f3 = Math.max(f3, f5);
            ++j;
        }
        float f6 = (f + f2) / 2.0f;
        float f7 = (f1 + f3) / 2.0f;
        TextureAtlasSprite textureatlassprite = Minecraft.getMinecraft().getTextureMapBlocks().getIconByUV(f6, f7);
        return textureatlassprite;
    }

    protected void fixVertexData() {
        if (Config.isShaders()) {
            if (this.vertexData.length == 28) {
                this.vertexData = BakedQuad.expandVertexData(this.vertexData);
            }
        } else if (this.vertexData.length == 56) {
            this.vertexData = BakedQuad.compactVertexData(this.vertexData);
        }
    }

    private static int[] expandVertexData(int[] p_expandVertexData_0_) {
        int i = p_expandVertexData_0_.length / 4;
        int j = i * 2;
        int[] aint = new int[j * 4];
        int k = 0;
        while (k < 4) {
            System.arraycopy(p_expandVertexData_0_, k * i, aint, k * j, i);
            ++k;
        }
        return aint;
    }

    private static int[] compactVertexData(int[] p_compactVertexData_0_) {
        int i = p_compactVertexData_0_.length / 4;
        int j = i / 2;
        int[] aint = new int[j * 4];
        int k = 0;
        while (k < 4) {
            System.arraycopy(p_compactVertexData_0_, k * i, aint, k * j, j);
            ++k;
        }
        return aint;
    }

    public QuadBounds getQuadBounds() {
        if (this.quadBounds == null) {
            this.quadBounds = new QuadBounds(this.getVertexData());
        }
        return this.quadBounds;
    }

    public float getMidX() {
        QuadBounds quadbounds = this.getQuadBounds();
        return (quadbounds.getMaxX() + quadbounds.getMinX()) / 2.0f;
    }

    public double getMidY() {
        QuadBounds quadbounds = this.getQuadBounds();
        return (quadbounds.getMaxY() + quadbounds.getMinY()) / 2.0f;
    }

    public double getMidZ() {
        QuadBounds quadbounds = this.getQuadBounds();
        return (quadbounds.getMaxZ() + quadbounds.getMinZ()) / 2.0f;
    }

    public boolean isFaceQuad() {
        QuadBounds quadbounds = this.getQuadBounds();
        return quadbounds.isFaceQuad(this.face);
    }

    public boolean isFullQuad() {
        QuadBounds quadbounds = this.getQuadBounds();
        return quadbounds.isFullQuad(this.face);
    }

    public boolean isFullFaceQuad() {
        return this.isFullQuad() && this.isFaceQuad();
    }

    public String toString() {
        return "vertex: " + this.vertexData.length / 7 + ", tint: " + this.tintIndex + ", facing: " + this.face + ", sprite: " + this.sprite;
    }
}

