/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.model;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.BufferBuilder;

public class ModelBox {
    private final PositionTextureVertex[] vertexPositions;
    private final TexturedQuad[] quadList;
    public final float posX1;
    public final float posY1;
    public final float posZ1;
    public final float posX2;
    public final float posY2;
    public final float posZ2;
    public String boxName;

    public ModelBox(ModelRenderer renderer, int texU, int texV, float x, float y, float z, int dx, int dy, int dz, float delta) {
        this(renderer, texU, texV, x, y, z, dx, dy, dz, delta, renderer.mirror);
    }

    public ModelBox(ModelRenderer p_i7_1_, int[][] p_i7_2_, float p_i7_3_, float p_i7_4_, float p_i7_5_, float p_i7_6_, float p_i7_7_, float p_i7_8_, float p_i7_9_, boolean p_i7_10_) {
        this.posX1 = p_i7_3_;
        this.posY1 = p_i7_4_;
        this.posZ1 = p_i7_5_;
        this.posX2 = p_i7_3_ + p_i7_6_;
        this.posY2 = p_i7_4_ + p_i7_7_;
        this.posZ2 = p_i7_5_ + p_i7_8_;
        this.vertexPositions = new PositionTextureVertex[8];
        this.quadList = new TexturedQuad[6];
        float f = p_i7_3_ + p_i7_6_;
        float f1 = p_i7_4_ + p_i7_7_;
        float f2 = p_i7_5_ + p_i7_8_;
        p_i7_3_ -= p_i7_9_;
        p_i7_4_ -= p_i7_9_;
        p_i7_5_ -= p_i7_9_;
        f += p_i7_9_;
        f1 += p_i7_9_;
        f2 += p_i7_9_;
        if (p_i7_10_) {
            float f3 = f;
            f = p_i7_3_;
            p_i7_3_ = f3;
        }
        PositionTextureVertex positiontexturevertex7 = new PositionTextureVertex(p_i7_3_, p_i7_4_, p_i7_5_, 0.0f, 0.0f);
        PositionTextureVertex positiontexturevertex = new PositionTextureVertex(f, p_i7_4_, p_i7_5_, 0.0f, 8.0f);
        PositionTextureVertex positiontexturevertex1 = new PositionTextureVertex(f, f1, p_i7_5_, 8.0f, 8.0f);
        PositionTextureVertex positiontexturevertex2 = new PositionTextureVertex(p_i7_3_, f1, p_i7_5_, 8.0f, 0.0f);
        PositionTextureVertex positiontexturevertex3 = new PositionTextureVertex(p_i7_3_, p_i7_4_, f2, 0.0f, 0.0f);
        PositionTextureVertex positiontexturevertex4 = new PositionTextureVertex(f, p_i7_4_, f2, 0.0f, 8.0f);
        PositionTextureVertex positiontexturevertex5 = new PositionTextureVertex(f, f1, f2, 8.0f, 8.0f);
        PositionTextureVertex positiontexturevertex6 = new PositionTextureVertex(p_i7_3_, f1, f2, 8.0f, 0.0f);
        this.vertexPositions[0] = positiontexturevertex7;
        this.vertexPositions[1] = positiontexturevertex;
        this.vertexPositions[2] = positiontexturevertex1;
        this.vertexPositions[3] = positiontexturevertex2;
        this.vertexPositions[4] = positiontexturevertex3;
        this.vertexPositions[5] = positiontexturevertex4;
        this.vertexPositions[6] = positiontexturevertex5;
        this.vertexPositions[7] = positiontexturevertex6;
        this.quadList[0] = this.makeTexturedQuad(new PositionTextureVertex[]{positiontexturevertex4, positiontexturevertex, positiontexturevertex1, positiontexturevertex5}, p_i7_2_[4], false, p_i7_1_.textureWidth, p_i7_1_.textureHeight);
        this.quadList[1] = this.makeTexturedQuad(new PositionTextureVertex[]{positiontexturevertex7, positiontexturevertex3, positiontexturevertex6, positiontexturevertex2}, p_i7_2_[5], false, p_i7_1_.textureWidth, p_i7_1_.textureHeight);
        this.quadList[2] = this.makeTexturedQuad(new PositionTextureVertex[]{positiontexturevertex4, positiontexturevertex3, positiontexturevertex7, positiontexturevertex}, p_i7_2_[1], true, p_i7_1_.textureWidth, p_i7_1_.textureHeight);
        this.quadList[3] = this.makeTexturedQuad(new PositionTextureVertex[]{positiontexturevertex1, positiontexturevertex2, positiontexturevertex6, positiontexturevertex5}, p_i7_2_[0], true, p_i7_1_.textureWidth, p_i7_1_.textureHeight);
        this.quadList[4] = this.makeTexturedQuad(new PositionTextureVertex[]{positiontexturevertex, positiontexturevertex7, positiontexturevertex2, positiontexturevertex1}, p_i7_2_[2], false, p_i7_1_.textureWidth, p_i7_1_.textureHeight);
        this.quadList[5] = this.makeTexturedQuad(new PositionTextureVertex[]{positiontexturevertex3, positiontexturevertex4, positiontexturevertex5, positiontexturevertex6}, p_i7_2_[3], false, p_i7_1_.textureWidth, p_i7_1_.textureHeight);
        if (p_i7_10_) {
            TexturedQuad[] texturedQuadArray = this.quadList;
            int n = this.quadList.length;
            int n2 = 0;
            while (n2 < n) {
                TexturedQuad texturedquad = texturedQuadArray[n2];
                texturedquad.flipFace();
                ++n2;
            }
        }
    }

    private TexturedQuad makeTexturedQuad(PositionTextureVertex[] p_makeTexturedQuad_1_, int[] p_makeTexturedQuad_2_, boolean p_makeTexturedQuad_3_, float p_makeTexturedQuad_4_, float p_makeTexturedQuad_5_) {
        if (p_makeTexturedQuad_2_ == null) {
            return null;
        }
        return p_makeTexturedQuad_3_ ? new TexturedQuad(p_makeTexturedQuad_1_, p_makeTexturedQuad_2_[2], p_makeTexturedQuad_2_[3], p_makeTexturedQuad_2_[0], p_makeTexturedQuad_2_[1], p_makeTexturedQuad_4_, p_makeTexturedQuad_5_) : new TexturedQuad(p_makeTexturedQuad_1_, p_makeTexturedQuad_2_[0], p_makeTexturedQuad_2_[1], p_makeTexturedQuad_2_[2], p_makeTexturedQuad_2_[3], p_makeTexturedQuad_4_, p_makeTexturedQuad_5_);
    }

    public ModelBox(ModelRenderer renderer, int texU, int texV, float x, float y, float z, int dx, int dy, int dz, float delta, boolean mirror) {
        this.posX1 = x;
        this.posY1 = y;
        this.posZ1 = z;
        this.posX2 = x + (float)dx;
        this.posY2 = y + (float)dy;
        this.posZ2 = z + (float)dz;
        this.vertexPositions = new PositionTextureVertex[8];
        this.quadList = new TexturedQuad[6];
        float f = x + (float)dx;
        float f1 = y + (float)dy;
        float f2 = z + (float)dz;
        x -= delta;
        y -= delta;
        z -= delta;
        f += delta;
        f1 += delta;
        f2 += delta;
        if (mirror) {
            float f3 = f;
            f = x;
            x = f3;
        }
        PositionTextureVertex positiontexturevertex7 = new PositionTextureVertex(x, y, z, 0.0f, 0.0f);
        PositionTextureVertex positiontexturevertex = new PositionTextureVertex(f, y, z, 0.0f, 8.0f);
        PositionTextureVertex positiontexturevertex1 = new PositionTextureVertex(f, f1, z, 8.0f, 8.0f);
        PositionTextureVertex positiontexturevertex2 = new PositionTextureVertex(x, f1, z, 8.0f, 0.0f);
        PositionTextureVertex positiontexturevertex3 = new PositionTextureVertex(x, y, f2, 0.0f, 0.0f);
        PositionTextureVertex positiontexturevertex4 = new PositionTextureVertex(f, y, f2, 0.0f, 8.0f);
        PositionTextureVertex positiontexturevertex5 = new PositionTextureVertex(f, f1, f2, 8.0f, 8.0f);
        PositionTextureVertex positiontexturevertex6 = new PositionTextureVertex(x, f1, f2, 8.0f, 0.0f);
        this.vertexPositions[0] = positiontexturevertex7;
        this.vertexPositions[1] = positiontexturevertex;
        this.vertexPositions[2] = positiontexturevertex1;
        this.vertexPositions[3] = positiontexturevertex2;
        this.vertexPositions[4] = positiontexturevertex3;
        this.vertexPositions[5] = positiontexturevertex4;
        this.vertexPositions[6] = positiontexturevertex5;
        this.vertexPositions[7] = positiontexturevertex6;
        this.quadList[0] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex4, positiontexturevertex, positiontexturevertex1, positiontexturevertex5}, texU + dz + dx, texV + dz, texU + dz + dx + dz, texV + dz + dy, renderer.textureWidth, renderer.textureHeight);
        this.quadList[1] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex7, positiontexturevertex3, positiontexturevertex6, positiontexturevertex2}, texU, texV + dz, texU + dz, texV + dz + dy, renderer.textureWidth, renderer.textureHeight);
        this.quadList[2] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex4, positiontexturevertex3, positiontexturevertex7, positiontexturevertex}, texU + dz, texV, texU + dz + dx, texV + dz, renderer.textureWidth, renderer.textureHeight);
        this.quadList[3] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex1, positiontexturevertex2, positiontexturevertex6, positiontexturevertex5}, texU + dz + dx, texV + dz, texU + dz + dx + dx, texV, renderer.textureWidth, renderer.textureHeight);
        this.quadList[4] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex, positiontexturevertex7, positiontexturevertex2, positiontexturevertex1}, texU + dz, texV + dz, texU + dz + dx, texV + dz + dy, renderer.textureWidth, renderer.textureHeight);
        this.quadList[5] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex3, positiontexturevertex4, positiontexturevertex5, positiontexturevertex6}, texU + dz + dx + dz, texV + dz, texU + dz + dx + dz + dx, texV + dz + dy, renderer.textureWidth, renderer.textureHeight);
        if (mirror) {
            TexturedQuad[] texturedQuadArray = this.quadList;
            int n = this.quadList.length;
            int n2 = 0;
            while (n2 < n) {
                TexturedQuad texturedquad = texturedQuadArray[n2];
                texturedquad.flipFace();
                ++n2;
            }
        }
    }

    public void render(BufferBuilder renderer, float scale) {
        TexturedQuad[] texturedQuadArray = this.quadList;
        int n = this.quadList.length;
        int n2 = 0;
        while (n2 < n) {
            TexturedQuad texturedquad = texturedQuadArray[n2];
            if (texturedquad != null) {
                texturedquad.draw(renderer, scale);
            }
            ++n2;
        }
    }

    public ModelBox setBoxName(String name) {
        this.boxName = name;
        return this;
    }
}

