/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.lwjgl.util.vector.Matrix4f
 *  org.lwjgl.util.vector.ReadableVector3f
 *  org.lwjgl.util.vector.Vector3f
 *  org.lwjgl.util.vector.Vector4f
 */
package net.minecraft.client.renderer.block.model;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.EnumFaceDirection;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.BlockPartRotation;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.src.BlockModelUtils;
import net.minecraft.src.Config;
import net.minecraft.src.Reflector;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.model.ITransformation;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.ReadableVector3f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import shadersmod.client.Shaders;

public class FaceBakery {
    private static final float SCALE_ROTATION_22_5 = 1.0f / (float)Math.cos(0.3926991f) - 1.0f;
    private static final float SCALE_ROTATION_GENERAL = 1.0f / (float)Math.cos(0.7853981633974483) - 1.0f;
    private static final Rotation[] UV_ROTATIONS = new Rotation[ModelRotation.values().length * EnumFacing.values().length];
    private static final Rotation UV_ROTATION_0 = new Rotation(){

        @Override
        BlockFaceUV makeRotatedUV(float p_188007_1_, float p_188007_2_, float p_188007_3_, float p_188007_4_) {
            return new BlockFaceUV(new float[]{p_188007_1_, p_188007_2_, p_188007_3_, p_188007_4_}, 0);
        }
    };
    private static final Rotation UV_ROTATION_270 = new Rotation(){

        @Override
        BlockFaceUV makeRotatedUV(float p_188007_1_, float p_188007_2_, float p_188007_3_, float p_188007_4_) {
            return new BlockFaceUV(new float[]{p_188007_4_, 16.0f - p_188007_1_, p_188007_2_, 16.0f - p_188007_3_}, 270);
        }
    };
    private static final Rotation UV_ROTATION_INVERSE = new Rotation(){

        @Override
        BlockFaceUV makeRotatedUV(float p_188007_1_, float p_188007_2_, float p_188007_3_, float p_188007_4_) {
            return new BlockFaceUV(new float[]{16.0f - p_188007_1_, 16.0f - p_188007_2_, 16.0f - p_188007_3_, 16.0f - p_188007_4_}, 0);
        }
    };
    private static final Rotation UV_ROTATION_90 = new Rotation(){

        @Override
        BlockFaceUV makeRotatedUV(float p_188007_1_, float p_188007_2_, float p_188007_3_, float p_188007_4_) {
            return new BlockFaceUV(new float[]{16.0f - p_188007_2_, p_188007_3_, 16.0f - p_188007_4_, p_188007_1_}, 90);
        }
    };

    static {
        FaceBakery.addUvRotation(ModelRotation.X0_Y0, EnumFacing.DOWN, UV_ROTATION_0);
        FaceBakery.addUvRotation(ModelRotation.X0_Y0, EnumFacing.EAST, UV_ROTATION_0);
        FaceBakery.addUvRotation(ModelRotation.X0_Y0, EnumFacing.NORTH, UV_ROTATION_0);
        FaceBakery.addUvRotation(ModelRotation.X0_Y0, EnumFacing.SOUTH, UV_ROTATION_0);
        FaceBakery.addUvRotation(ModelRotation.X0_Y0, EnumFacing.UP, UV_ROTATION_0);
        FaceBakery.addUvRotation(ModelRotation.X0_Y0, EnumFacing.WEST, UV_ROTATION_0);
        FaceBakery.addUvRotation(ModelRotation.X0_Y90, EnumFacing.EAST, UV_ROTATION_0);
        FaceBakery.addUvRotation(ModelRotation.X0_Y90, EnumFacing.NORTH, UV_ROTATION_0);
        FaceBakery.addUvRotation(ModelRotation.X0_Y90, EnumFacing.SOUTH, UV_ROTATION_0);
        FaceBakery.addUvRotation(ModelRotation.X0_Y90, EnumFacing.WEST, UV_ROTATION_0);
        FaceBakery.addUvRotation(ModelRotation.X0_Y180, EnumFacing.EAST, UV_ROTATION_0);
        FaceBakery.addUvRotation(ModelRotation.X0_Y180, EnumFacing.NORTH, UV_ROTATION_0);
        FaceBakery.addUvRotation(ModelRotation.X0_Y180, EnumFacing.SOUTH, UV_ROTATION_0);
        FaceBakery.addUvRotation(ModelRotation.X0_Y180, EnumFacing.WEST, UV_ROTATION_0);
        FaceBakery.addUvRotation(ModelRotation.X0_Y270, EnumFacing.EAST, UV_ROTATION_0);
        FaceBakery.addUvRotation(ModelRotation.X0_Y270, EnumFacing.NORTH, UV_ROTATION_0);
        FaceBakery.addUvRotation(ModelRotation.X0_Y270, EnumFacing.SOUTH, UV_ROTATION_0);
        FaceBakery.addUvRotation(ModelRotation.X0_Y270, EnumFacing.WEST, UV_ROTATION_0);
        FaceBakery.addUvRotation(ModelRotation.X90_Y0, EnumFacing.DOWN, UV_ROTATION_0);
        FaceBakery.addUvRotation(ModelRotation.X90_Y0, EnumFacing.SOUTH, UV_ROTATION_0);
        FaceBakery.addUvRotation(ModelRotation.X90_Y90, EnumFacing.DOWN, UV_ROTATION_0);
        FaceBakery.addUvRotation(ModelRotation.X90_Y180, EnumFacing.DOWN, UV_ROTATION_0);
        FaceBakery.addUvRotation(ModelRotation.X90_Y180, EnumFacing.NORTH, UV_ROTATION_0);
        FaceBakery.addUvRotation(ModelRotation.X90_Y270, EnumFacing.DOWN, UV_ROTATION_0);
        FaceBakery.addUvRotation(ModelRotation.X180_Y0, EnumFacing.DOWN, UV_ROTATION_0);
        FaceBakery.addUvRotation(ModelRotation.X180_Y0, EnumFacing.UP, UV_ROTATION_0);
        FaceBakery.addUvRotation(ModelRotation.X270_Y0, EnumFacing.SOUTH, UV_ROTATION_0);
        FaceBakery.addUvRotation(ModelRotation.X270_Y0, EnumFacing.UP, UV_ROTATION_0);
        FaceBakery.addUvRotation(ModelRotation.X270_Y90, EnumFacing.UP, UV_ROTATION_0);
        FaceBakery.addUvRotation(ModelRotation.X270_Y180, EnumFacing.NORTH, UV_ROTATION_0);
        FaceBakery.addUvRotation(ModelRotation.X270_Y180, EnumFacing.UP, UV_ROTATION_0);
        FaceBakery.addUvRotation(ModelRotation.X270_Y270, EnumFacing.UP, UV_ROTATION_0);
        FaceBakery.addUvRotation(ModelRotation.X0_Y270, EnumFacing.UP, UV_ROTATION_270);
        FaceBakery.addUvRotation(ModelRotation.X0_Y90, EnumFacing.DOWN, UV_ROTATION_270);
        FaceBakery.addUvRotation(ModelRotation.X90_Y0, EnumFacing.WEST, UV_ROTATION_270);
        FaceBakery.addUvRotation(ModelRotation.X90_Y90, EnumFacing.WEST, UV_ROTATION_270);
        FaceBakery.addUvRotation(ModelRotation.X90_Y180, EnumFacing.WEST, UV_ROTATION_270);
        FaceBakery.addUvRotation(ModelRotation.X90_Y270, EnumFacing.NORTH, UV_ROTATION_270);
        FaceBakery.addUvRotation(ModelRotation.X90_Y270, EnumFacing.SOUTH, UV_ROTATION_270);
        FaceBakery.addUvRotation(ModelRotation.X90_Y270, EnumFacing.WEST, UV_ROTATION_270);
        FaceBakery.addUvRotation(ModelRotation.X180_Y90, EnumFacing.UP, UV_ROTATION_270);
        FaceBakery.addUvRotation(ModelRotation.X180_Y270, EnumFacing.DOWN, UV_ROTATION_270);
        FaceBakery.addUvRotation(ModelRotation.X270_Y0, EnumFacing.EAST, UV_ROTATION_270);
        FaceBakery.addUvRotation(ModelRotation.X270_Y90, EnumFacing.EAST, UV_ROTATION_270);
        FaceBakery.addUvRotation(ModelRotation.X270_Y90, EnumFacing.NORTH, UV_ROTATION_270);
        FaceBakery.addUvRotation(ModelRotation.X270_Y90, EnumFacing.SOUTH, UV_ROTATION_270);
        FaceBakery.addUvRotation(ModelRotation.X270_Y180, EnumFacing.EAST, UV_ROTATION_270);
        FaceBakery.addUvRotation(ModelRotation.X270_Y270, EnumFacing.EAST, UV_ROTATION_270);
        FaceBakery.addUvRotation(ModelRotation.X0_Y180, EnumFacing.DOWN, UV_ROTATION_INVERSE);
        FaceBakery.addUvRotation(ModelRotation.X0_Y180, EnumFacing.UP, UV_ROTATION_INVERSE);
        FaceBakery.addUvRotation(ModelRotation.X90_Y0, EnumFacing.NORTH, UV_ROTATION_INVERSE);
        FaceBakery.addUvRotation(ModelRotation.X90_Y0, EnumFacing.UP, UV_ROTATION_INVERSE);
        FaceBakery.addUvRotation(ModelRotation.X90_Y90, EnumFacing.UP, UV_ROTATION_INVERSE);
        FaceBakery.addUvRotation(ModelRotation.X90_Y180, EnumFacing.SOUTH, UV_ROTATION_INVERSE);
        FaceBakery.addUvRotation(ModelRotation.X90_Y180, EnumFacing.UP, UV_ROTATION_INVERSE);
        FaceBakery.addUvRotation(ModelRotation.X90_Y270, EnumFacing.UP, UV_ROTATION_INVERSE);
        FaceBakery.addUvRotation(ModelRotation.X180_Y0, EnumFacing.EAST, UV_ROTATION_INVERSE);
        FaceBakery.addUvRotation(ModelRotation.X180_Y0, EnumFacing.NORTH, UV_ROTATION_INVERSE);
        FaceBakery.addUvRotation(ModelRotation.X180_Y0, EnumFacing.SOUTH, UV_ROTATION_INVERSE);
        FaceBakery.addUvRotation(ModelRotation.X180_Y0, EnumFacing.WEST, UV_ROTATION_INVERSE);
        FaceBakery.addUvRotation(ModelRotation.X180_Y90, EnumFacing.EAST, UV_ROTATION_INVERSE);
        FaceBakery.addUvRotation(ModelRotation.X180_Y90, EnumFacing.NORTH, UV_ROTATION_INVERSE);
        FaceBakery.addUvRotation(ModelRotation.X180_Y90, EnumFacing.SOUTH, UV_ROTATION_INVERSE);
        FaceBakery.addUvRotation(ModelRotation.X180_Y90, EnumFacing.WEST, UV_ROTATION_INVERSE);
        FaceBakery.addUvRotation(ModelRotation.X180_Y180, EnumFacing.DOWN, UV_ROTATION_INVERSE);
        FaceBakery.addUvRotation(ModelRotation.X180_Y180, EnumFacing.EAST, UV_ROTATION_INVERSE);
        FaceBakery.addUvRotation(ModelRotation.X180_Y180, EnumFacing.NORTH, UV_ROTATION_INVERSE);
        FaceBakery.addUvRotation(ModelRotation.X180_Y180, EnumFacing.SOUTH, UV_ROTATION_INVERSE);
        FaceBakery.addUvRotation(ModelRotation.X180_Y180, EnumFacing.UP, UV_ROTATION_INVERSE);
        FaceBakery.addUvRotation(ModelRotation.X180_Y180, EnumFacing.WEST, UV_ROTATION_INVERSE);
        FaceBakery.addUvRotation(ModelRotation.X180_Y270, EnumFacing.EAST, UV_ROTATION_INVERSE);
        FaceBakery.addUvRotation(ModelRotation.X180_Y270, EnumFacing.NORTH, UV_ROTATION_INVERSE);
        FaceBakery.addUvRotation(ModelRotation.X180_Y270, EnumFacing.SOUTH, UV_ROTATION_INVERSE);
        FaceBakery.addUvRotation(ModelRotation.X180_Y270, EnumFacing.WEST, UV_ROTATION_INVERSE);
        FaceBakery.addUvRotation(ModelRotation.X270_Y0, EnumFacing.DOWN, UV_ROTATION_INVERSE);
        FaceBakery.addUvRotation(ModelRotation.X270_Y0, EnumFacing.NORTH, UV_ROTATION_INVERSE);
        FaceBakery.addUvRotation(ModelRotation.X270_Y90, EnumFacing.DOWN, UV_ROTATION_INVERSE);
        FaceBakery.addUvRotation(ModelRotation.X270_Y180, EnumFacing.DOWN, UV_ROTATION_INVERSE);
        FaceBakery.addUvRotation(ModelRotation.X270_Y180, EnumFacing.SOUTH, UV_ROTATION_INVERSE);
        FaceBakery.addUvRotation(ModelRotation.X270_Y270, EnumFacing.DOWN, UV_ROTATION_INVERSE);
        FaceBakery.addUvRotation(ModelRotation.X0_Y90, EnumFacing.UP, UV_ROTATION_90);
        FaceBakery.addUvRotation(ModelRotation.X0_Y270, EnumFacing.DOWN, UV_ROTATION_90);
        FaceBakery.addUvRotation(ModelRotation.X90_Y0, EnumFacing.EAST, UV_ROTATION_90);
        FaceBakery.addUvRotation(ModelRotation.X90_Y90, EnumFacing.EAST, UV_ROTATION_90);
        FaceBakery.addUvRotation(ModelRotation.X90_Y90, EnumFacing.NORTH, UV_ROTATION_90);
        FaceBakery.addUvRotation(ModelRotation.X90_Y90, EnumFacing.SOUTH, UV_ROTATION_90);
        FaceBakery.addUvRotation(ModelRotation.X90_Y180, EnumFacing.EAST, UV_ROTATION_90);
        FaceBakery.addUvRotation(ModelRotation.X90_Y270, EnumFacing.EAST, UV_ROTATION_90);
        FaceBakery.addUvRotation(ModelRotation.X270_Y0, EnumFacing.WEST, UV_ROTATION_90);
        FaceBakery.addUvRotation(ModelRotation.X180_Y90, EnumFacing.DOWN, UV_ROTATION_90);
        FaceBakery.addUvRotation(ModelRotation.X180_Y270, EnumFacing.UP, UV_ROTATION_90);
        FaceBakery.addUvRotation(ModelRotation.X270_Y90, EnumFacing.WEST, UV_ROTATION_90);
        FaceBakery.addUvRotation(ModelRotation.X270_Y180, EnumFacing.WEST, UV_ROTATION_90);
        FaceBakery.addUvRotation(ModelRotation.X270_Y270, EnumFacing.NORTH, UV_ROTATION_90);
        FaceBakery.addUvRotation(ModelRotation.X270_Y270, EnumFacing.SOUTH, UV_ROTATION_90);
        FaceBakery.addUvRotation(ModelRotation.X270_Y270, EnumFacing.WEST, UV_ROTATION_90);
    }

    public BakedQuad makeBakedQuad(Vector3f posFrom, Vector3f posTo, BlockPartFace face, TextureAtlasSprite sprite, EnumFacing facing, ModelRotation modelRotationIn, @Nullable BlockPartRotation partRotation, boolean uvLocked, boolean shade) {
        return this.makeBakedQuad(posFrom, posTo, face, sprite, facing, (ITransformation)modelRotationIn, partRotation, uvLocked, shade);
    }

    public BakedQuad makeBakedQuad(Vector3f p_makeBakedQuad_1_, Vector3f p_makeBakedQuad_2_, BlockPartFace p_makeBakedQuad_3_, TextureAtlasSprite p_makeBakedQuad_4_, EnumFacing p_makeBakedQuad_5_, ITransformation p_makeBakedQuad_6_, BlockPartRotation p_makeBakedQuad_7_, boolean p_makeBakedQuad_8_, boolean p_makeBakedQuad_9_) {
        BlockFaceUV blockfaceuv = p_makeBakedQuad_3_.blockFaceUV;
        if (p_makeBakedQuad_8_) {
            blockfaceuv = Reflector.ForgeHooksClient_applyUVLock.exists() ? (BlockFaceUV)Reflector.call(Reflector.ForgeHooksClient_applyUVLock, p_makeBakedQuad_3_.blockFaceUV, p_makeBakedQuad_5_, p_makeBakedQuad_6_) : this.applyUVLock(p_makeBakedQuad_3_.blockFaceUV, p_makeBakedQuad_5_, (ModelRotation)p_makeBakedQuad_6_);
        }
        boolean flag = p_makeBakedQuad_9_ && !Reflector.ForgeHooksClient_fillNormal.exists();
        int[] aint = this.makeQuadVertexData(blockfaceuv, p_makeBakedQuad_4_, p_makeBakedQuad_5_, this.getPositionsDiv16(p_makeBakedQuad_1_, p_makeBakedQuad_2_), p_makeBakedQuad_6_, p_makeBakedQuad_7_, flag);
        EnumFacing enumfacing = FaceBakery.getFacingFromVertexData(aint);
        if (p_makeBakedQuad_7_ == null) {
            this.applyFacing(aint, enumfacing);
        }
        if (Reflector.ForgeHooksClient_fillNormal.exists()) {
            Reflector.call(Reflector.ForgeHooksClient_fillNormal, aint, enumfacing);
            return new BakedQuad(aint, p_makeBakedQuad_3_.tintIndex, enumfacing, p_makeBakedQuad_4_, p_makeBakedQuad_9_, DefaultVertexFormats.ITEM);
        }
        return new BakedQuad(aint, p_makeBakedQuad_3_.tintIndex, enumfacing, p_makeBakedQuad_4_);
    }

    private BlockFaceUV applyUVLock(BlockFaceUV p_188010_1_, EnumFacing p_188010_2_, ModelRotation p_188010_3_) {
        return UV_ROTATIONS[FaceBakery.getIndex(p_188010_3_, p_188010_2_)].rotateUV(p_188010_1_);
    }

    private int[] makeQuadVertexData(BlockFaceUV p_makeQuadVertexData_1_, TextureAtlasSprite p_makeQuadVertexData_2_, EnumFacing p_makeQuadVertexData_3_, float[] p_makeQuadVertexData_4_, ITransformation p_makeQuadVertexData_5_, @Nullable BlockPartRotation p_makeQuadVertexData_6_, boolean p_makeQuadVertexData_7_) {
        int i = 28;
        if (Config.isShaders()) {
            i = 56;
        }
        int[] aint = new int[i];
        int j = 0;
        while (j < 4) {
            this.fillVertexData(aint, j, p_makeQuadVertexData_3_, p_makeQuadVertexData_1_, p_makeQuadVertexData_4_, p_makeQuadVertexData_2_, p_makeQuadVertexData_5_, p_makeQuadVertexData_6_, p_makeQuadVertexData_7_);
            ++j;
        }
        return aint;
    }

    private int getFaceShadeColor(EnumFacing facing) {
        float f = FaceBakery.getFaceBrightness(facing);
        int i = MathHelper.clamp((int)(f * 255.0f), 0, 255);
        return 0xFF000000 | i << 16 | i << 8 | i;
    }

    public static float getFaceBrightness(EnumFacing p_178412_0_) {
        switch (p_178412_0_) {
            case DOWN: {
                if (Config.isShaders()) {
                    return Shaders.blockLightLevel05;
                }
                return 0.5f;
            }
            case UP: {
                return 1.0f;
            }
            case NORTH: 
            case SOUTH: {
                if (Config.isShaders()) {
                    return Shaders.blockLightLevel08;
                }
                return 0.8f;
            }
            case WEST: 
            case EAST: {
                if (Config.isShaders()) {
                    return Shaders.blockLightLevel06;
                }
                return 0.6f;
            }
        }
        return 1.0f;
    }

    private float[] getPositionsDiv16(Vector3f pos1, Vector3f pos2) {
        float[] afloat = new float[EnumFacing.values().length];
        afloat[EnumFaceDirection.Constants.WEST_INDEX] = pos1.x / 16.0f;
        afloat[EnumFaceDirection.Constants.DOWN_INDEX] = pos1.y / 16.0f;
        afloat[EnumFaceDirection.Constants.NORTH_INDEX] = pos1.z / 16.0f;
        afloat[EnumFaceDirection.Constants.EAST_INDEX] = pos2.x / 16.0f;
        afloat[EnumFaceDirection.Constants.UP_INDEX] = pos2.y / 16.0f;
        afloat[EnumFaceDirection.Constants.SOUTH_INDEX] = pos2.z / 16.0f;
        return afloat;
    }

    private void fillVertexData(int[] p_fillVertexData_1_, int p_fillVertexData_2_, EnumFacing p_fillVertexData_3_, BlockFaceUV p_fillVertexData_4_, float[] p_fillVertexData_5_, TextureAtlasSprite p_fillVertexData_6_, ITransformation p_fillVertexData_7_, @Nullable BlockPartRotation p_fillVertexData_8_, boolean p_fillVertexData_9_) {
        EnumFacing enumfacing = p_fillVertexData_7_.rotate(p_fillVertexData_3_);
        int i = p_fillVertexData_9_ ? this.getFaceShadeColor(enumfacing) : -1;
        EnumFaceDirection.VertexInformation enumfacedirection$vertexinformation = EnumFaceDirection.getFacing(p_fillVertexData_3_).getVertexInformation(p_fillVertexData_2_);
        Vector3f vector3f = new Vector3f(p_fillVertexData_5_[enumfacedirection$vertexinformation.xIndex], p_fillVertexData_5_[enumfacedirection$vertexinformation.yIndex], p_fillVertexData_5_[enumfacedirection$vertexinformation.zIndex]);
        this.rotatePart(vector3f, p_fillVertexData_8_);
        int j = this.rotateVertex(vector3f, p_fillVertexData_3_, p_fillVertexData_2_, p_fillVertexData_7_);
        BlockModelUtils.snapVertexPosition(vector3f);
        this.storeVertexData(p_fillVertexData_1_, j, p_fillVertexData_2_, vector3f, i, p_fillVertexData_6_, p_fillVertexData_4_);
    }

    private void storeVertexData(int[] faceData, int storeIndex, int vertexIndex, Vector3f position, int shadeColor, TextureAtlasSprite sprite, BlockFaceUV faceUV) {
        int i = faceData.length / 4;
        int j = storeIndex * i;
        faceData[j] = Float.floatToRawIntBits(position.x);
        faceData[j + 1] = Float.floatToRawIntBits(position.y);
        faceData[j + 2] = Float.floatToRawIntBits(position.z);
        faceData[j + 3] = shadeColor;
        faceData[j + 4] = Float.floatToRawIntBits(sprite.getInterpolatedU((double)faceUV.getVertexU(vertexIndex) * 0.999 + (double)faceUV.getVertexU((vertexIndex + 2) % 4) * 0.001));
        faceData[j + 4 + 1] = Float.floatToRawIntBits(sprite.getInterpolatedV((double)faceUV.getVertexV(vertexIndex) * 0.999 + (double)faceUV.getVertexV((vertexIndex + 2) % 4) * 0.001));
    }

    private void rotatePart(Vector3f p_178407_1_, @Nullable BlockPartRotation partRotation) {
        if (partRotation != null) {
            Matrix4f matrix4f = this.getMatrixIdentity();
            Vector3f vector3f = new Vector3f(0.0f, 0.0f, 0.0f);
            switch (partRotation.axis) {
                case X: {
                    Matrix4f.rotate((float)(partRotation.angle * ((float)Math.PI / 180)), (Vector3f)new Vector3f(1.0f, 0.0f, 0.0f), (Matrix4f)matrix4f, (Matrix4f)matrix4f);
                    vector3f.set(0.0f, 1.0f, 1.0f);
                    break;
                }
                case Y: {
                    Matrix4f.rotate((float)(partRotation.angle * ((float)Math.PI / 180)), (Vector3f)new Vector3f(0.0f, 1.0f, 0.0f), (Matrix4f)matrix4f, (Matrix4f)matrix4f);
                    vector3f.set(1.0f, 0.0f, 1.0f);
                    break;
                }
                case Z: {
                    Matrix4f.rotate((float)(partRotation.angle * ((float)Math.PI / 180)), (Vector3f)new Vector3f(0.0f, 0.0f, 1.0f), (Matrix4f)matrix4f, (Matrix4f)matrix4f);
                    vector3f.set(1.0f, 1.0f, 0.0f);
                }
            }
            if (partRotation.rescale) {
                if (Math.abs(partRotation.angle) == 22.5f) {
                    vector3f.scale(SCALE_ROTATION_22_5);
                } else {
                    vector3f.scale(SCALE_ROTATION_GENERAL);
                }
                Vector3f.add((Vector3f)vector3f, (Vector3f)new Vector3f(1.0f, 1.0f, 1.0f), (Vector3f)vector3f);
            } else {
                vector3f.set(1.0f, 1.0f, 1.0f);
            }
            this.rotateScale(p_178407_1_, new Vector3f((ReadableVector3f)partRotation.origin), matrix4f, vector3f);
        }
    }

    public int rotateVertex(Vector3f p_188011_1_, EnumFacing p_188011_2_, int p_188011_3_, ModelRotation p_188011_4_) {
        return this.rotateVertex(p_188011_1_, p_188011_2_, p_188011_3_, p_188011_4_);
    }

    public int rotateVertex(Vector3f p_rotateVertex_1_, EnumFacing p_rotateVertex_2_, int p_rotateVertex_3_, ITransformation p_rotateVertex_4_) {
        if (p_rotateVertex_4_ == ModelRotation.X0_Y0) {
            return p_rotateVertex_3_;
        }
        if (Reflector.ForgeHooksClient_transform.exists()) {
            Reflector.call(Reflector.ForgeHooksClient_transform, p_rotateVertex_1_, p_rotateVertex_4_.getMatrix());
        } else {
            this.rotateScale(p_rotateVertex_1_, new Vector3f(0.5f, 0.5f, 0.5f), ((ModelRotation)p_rotateVertex_4_).getMatrix4d(), new Vector3f(1.0f, 1.0f, 1.0f));
        }
        return p_rotateVertex_4_.rotate(p_rotateVertex_2_, p_rotateVertex_3_);
    }

    private void rotateScale(Vector3f position, Vector3f rotationOrigin, Matrix4f rotationMatrix, Vector3f scale) {
        Vector4f vector4f = new Vector4f(position.x - rotationOrigin.x, position.y - rotationOrigin.y, position.z - rotationOrigin.z, 1.0f);
        Matrix4f.transform((Matrix4f)rotationMatrix, (Vector4f)vector4f, (Vector4f)vector4f);
        vector4f.x *= scale.x;
        vector4f.y *= scale.y;
        vector4f.z *= scale.z;
        position.set(vector4f.x + rotationOrigin.x, vector4f.y + rotationOrigin.y, vector4f.z + rotationOrigin.z);
    }

    private Matrix4f getMatrixIdentity() {
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.setIdentity();
        return matrix4f;
    }

    public static EnumFacing getFacingFromVertexData(int[] faceData) {
        int i = faceData.length / 4;
        int j = i * 2;
        Vector3f vector3f = new Vector3f(Float.intBitsToFloat(faceData[0]), Float.intBitsToFloat(faceData[1]), Float.intBitsToFloat(faceData[2]));
        Vector3f vector3f1 = new Vector3f(Float.intBitsToFloat(faceData[i]), Float.intBitsToFloat(faceData[i + 1]), Float.intBitsToFloat(faceData[i + 2]));
        Vector3f vector3f2 = new Vector3f(Float.intBitsToFloat(faceData[j]), Float.intBitsToFloat(faceData[j + 1]), Float.intBitsToFloat(faceData[j + 2]));
        Vector3f vector3f3 = new Vector3f();
        Vector3f vector3f4 = new Vector3f();
        Vector3f vector3f5 = new Vector3f();
        Vector3f.sub((Vector3f)vector3f, (Vector3f)vector3f1, (Vector3f)vector3f3);
        Vector3f.sub((Vector3f)vector3f2, (Vector3f)vector3f1, (Vector3f)vector3f4);
        Vector3f.cross((Vector3f)vector3f4, (Vector3f)vector3f3, (Vector3f)vector3f5);
        float f = (float)Math.sqrt(vector3f5.x * vector3f5.x + vector3f5.y * vector3f5.y + vector3f5.z * vector3f5.z);
        vector3f5.x /= f;
        vector3f5.y /= f;
        vector3f5.z /= f;
        EnumFacing enumfacing = null;
        float f1 = 0.0f;
        EnumFacing[] enumFacingArray = EnumFacing.values();
        int n = enumFacingArray.length;
        int n2 = 0;
        while (n2 < n) {
            EnumFacing enumfacing1 = enumFacingArray[n2];
            Vec3i vec3i = enumfacing1.getDirectionVec();
            Vector3f vector3f6 = new Vector3f((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
            float f2 = Vector3f.dot((Vector3f)vector3f5, (Vector3f)vector3f6);
            if (f2 >= 0.0f && f2 > f1) {
                f1 = f2;
                enumfacing = enumfacing1;
            }
            ++n2;
        }
        if (enumfacing == null) {
            return EnumFacing.UP;
        }
        return enumfacing;
    }

    private void applyFacing(int[] p_178408_1_, EnumFacing p_178408_2_) {
        int[] aint = new int[p_178408_1_.length];
        System.arraycopy(p_178408_1_, 0, aint, 0, p_178408_1_.length);
        float[] afloat = new float[EnumFacing.values().length];
        afloat[EnumFaceDirection.Constants.WEST_INDEX] = 999.0f;
        afloat[EnumFaceDirection.Constants.DOWN_INDEX] = 999.0f;
        afloat[EnumFaceDirection.Constants.NORTH_INDEX] = 999.0f;
        afloat[EnumFaceDirection.Constants.EAST_INDEX] = -999.0f;
        afloat[EnumFaceDirection.Constants.UP_INDEX] = -999.0f;
        afloat[EnumFaceDirection.Constants.SOUTH_INDEX] = -999.0f;
        int i = p_178408_1_.length / 4;
        int j = 0;
        while (j < 4) {
            int k = i * j;
            float f = Float.intBitsToFloat(aint[k]);
            float f1 = Float.intBitsToFloat(aint[k + 1]);
            float f2 = Float.intBitsToFloat(aint[k + 2]);
            if (f < afloat[EnumFaceDirection.Constants.WEST_INDEX]) {
                afloat[EnumFaceDirection.Constants.WEST_INDEX] = f;
            }
            if (f1 < afloat[EnumFaceDirection.Constants.DOWN_INDEX]) {
                afloat[EnumFaceDirection.Constants.DOWN_INDEX] = f1;
            }
            if (f2 < afloat[EnumFaceDirection.Constants.NORTH_INDEX]) {
                afloat[EnumFaceDirection.Constants.NORTH_INDEX] = f2;
            }
            if (f > afloat[EnumFaceDirection.Constants.EAST_INDEX]) {
                afloat[EnumFaceDirection.Constants.EAST_INDEX] = f;
            }
            if (f1 > afloat[EnumFaceDirection.Constants.UP_INDEX]) {
                afloat[EnumFaceDirection.Constants.UP_INDEX] = f1;
            }
            if (f2 > afloat[EnumFaceDirection.Constants.SOUTH_INDEX]) {
                afloat[EnumFaceDirection.Constants.SOUTH_INDEX] = f2;
            }
            ++j;
        }
        EnumFaceDirection enumfacedirection = EnumFaceDirection.getFacing(p_178408_2_);
        int j1 = 0;
        while (j1 < 4) {
            int k1 = i * j1;
            EnumFaceDirection.VertexInformation enumfacedirection$vertexinformation = enumfacedirection.getVertexInformation(j1);
            float f8 = afloat[enumfacedirection$vertexinformation.xIndex];
            float f3 = afloat[enumfacedirection$vertexinformation.yIndex];
            float f4 = afloat[enumfacedirection$vertexinformation.zIndex];
            p_178408_1_[k1] = Float.floatToRawIntBits(f8);
            p_178408_1_[k1 + 1] = Float.floatToRawIntBits(f3);
            p_178408_1_[k1 + 2] = Float.floatToRawIntBits(f4);
            int l = 0;
            while (l < 4) {
                int i1 = i * l;
                float f5 = Float.intBitsToFloat(aint[i1]);
                float f6 = Float.intBitsToFloat(aint[i1 + 1]);
                float f7 = Float.intBitsToFloat(aint[i1 + 2]);
                if (MathHelper.epsilonEquals(f8, f5) && MathHelper.epsilonEquals(f3, f6) && MathHelper.epsilonEquals(f4, f7)) {
                    p_178408_1_[k1 + 4] = aint[i1 + 4];
                    p_178408_1_[k1 + 4 + 1] = aint[i1 + 4 + 1];
                }
                ++l;
            }
            ++j1;
        }
    }

    private static void addUvRotation(ModelRotation p_188013_0_, EnumFacing p_188013_1_, Rotation p_188013_2_) {
        FaceBakery.UV_ROTATIONS[FaceBakery.getIndex((ModelRotation)p_188013_0_, (EnumFacing)p_188013_1_)] = p_188013_2_;
    }

    private static int getIndex(ModelRotation p_188014_0_, EnumFacing p_188014_1_) {
        return ModelRotation.values().length * p_188014_1_.ordinal() + p_188014_0_.ordinal();
    }

    static abstract class Rotation {
        private Rotation() {
        }

        public BlockFaceUV rotateUV(BlockFaceUV p_188006_1_) {
            float f = p_188006_1_.getVertexU(p_188006_1_.getVertexRotatedRev(0));
            float f1 = p_188006_1_.getVertexV(p_188006_1_.getVertexRotatedRev(0));
            float f2 = p_188006_1_.getVertexU(p_188006_1_.getVertexRotatedRev(2));
            float f3 = p_188006_1_.getVertexV(p_188006_1_.getVertexRotatedRev(2));
            return this.makeRotatedUV(f, f1, f2, f3);
        }

        abstract BlockFaceUV makeRotatedUV(float var1, float var2, float var3, float var4);
    }
}

