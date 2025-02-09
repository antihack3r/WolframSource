/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleSimpleAnimated;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemDye;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.wolframclient.Wolfram;

public class ParticleFirework {

    public static class Factory
    implements IParticleFactory {
        @Override
        public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int ... p_178902_15_) {
            Spark particlefirework$spark = new Spark(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, Minecraft.getMinecraft().effectRenderer);
            particlefirework$spark.setAlphaF(0.99f);
            return particlefirework$spark;
        }
    }

    public static class Overlay
    extends Particle {
        protected Overlay(World p_i46466_1_, double p_i46466_2_, double p_i46466_4_, double p_i46466_6_) {
            super(p_i46466_1_, p_i46466_2_, p_i46466_4_, p_i46466_6_);
            this.particleMaxAge = 4;
        }

        @Override
        public void renderParticle(BufferBuilder worldRendererIn, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
            float f = 0.25f;
            float f1 = 0.5f;
            float f2 = 0.125f;
            float f3 = 0.375f;
            float f4 = 7.1f * MathHelper.sin(((float)this.particleAge + partialTicks - 1.0f) * 0.25f * (float)Math.PI);
            this.setAlphaF(0.6f - ((float)this.particleAge + partialTicks - 1.0f) * 0.25f * 0.5f);
            float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
            float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
            float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
            int i = this.getBrightnessForRender(partialTicks);
            int j = i >> 16 & 0xFFFF;
            int k = i & 0xFFFF;
            worldRendererIn.pos(f5 - rotationX * f4 - rotationXY * f4, f6 - rotationZ * f4, f7 - rotationYZ * f4 - rotationXZ * f4).tex(0.5, 0.375).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
            worldRendererIn.pos(f5 - rotationX * f4 + rotationXY * f4, f6 + rotationZ * f4, f7 - rotationYZ * f4 + rotationXZ * f4).tex(0.5, 0.125).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
            worldRendererIn.pos(f5 + rotationX * f4 + rotationXY * f4, f6 + rotationZ * f4, f7 + rotationYZ * f4 + rotationXZ * f4).tex(0.25, 0.125).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
            worldRendererIn.pos(f5 + rotationX * f4 - rotationXY * f4, f6 - rotationZ * f4, f7 + rotationYZ * f4 - rotationXZ * f4).tex(0.25, 0.375).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        }
    }

    public static class Spark
    extends ParticleSimpleAnimated {
        private boolean trail;
        private boolean twinkle;
        private final ParticleManager effectRenderer;
        private float fadeColourRed;
        private float fadeColourGreen;
        private float fadeColourBlue;
        private boolean hasFadeColour;

        public Spark(World p_i46465_1_, double p_i46465_2_, double p_i46465_4_, double p_i46465_6_, double p_i46465_8_, double p_i46465_10_, double p_i46465_12_, ParticleManager p_i46465_14_) {
            super(p_i46465_1_, p_i46465_2_, p_i46465_4_, p_i46465_6_, 160, 8, -0.004f);
            this.motionX = p_i46465_8_;
            this.motionY = p_i46465_10_;
            this.motionZ = p_i46465_12_;
            this.effectRenderer = p_i46465_14_;
            this.particleScale *= 0.75f;
            this.particleMaxAge = 48 + this.rand.nextInt(12);
        }

        public void setTrail(boolean trailIn) {
            this.trail = trailIn;
        }

        public void setTwinkle(boolean twinkleIn) {
            this.twinkle = twinkleIn;
        }

        @Override
        public boolean isTransparent() {
            return true;
        }

        @Override
        public void renderParticle(BufferBuilder worldRendererIn, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
            if (!this.twinkle || this.particleAge < this.particleMaxAge / 3 || (this.particleAge + this.particleMaxAge) / 3 % 2 == 0) {
                super.renderParticle(worldRendererIn, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
            }
        }

        @Override
        public void onUpdate() {
            if (Wolfram.getWolfram().moduleManager.isEnabled("nofireworks")) {
                return;
            }
            super.onUpdate();
            if (this.trail && this.particleAge < this.particleMaxAge / 2 && (this.particleAge + this.particleMaxAge) % 2 == 0) {
                Spark particlefirework$spark = new Spark(this.worldObj, this.posX, this.posY, this.posZ, 0.0, 0.0, 0.0, this.effectRenderer);
                particlefirework$spark.setAlphaF(0.99f);
                particlefirework$spark.setRBGColorF(this.particleRed, this.particleGreen, this.particleBlue);
                particlefirework$spark.particleAge = particlefirework$spark.particleMaxAge / 2;
                if (this.hasFadeColour) {
                    particlefirework$spark.hasFadeColour = true;
                    particlefirework$spark.fadeColourRed = this.fadeColourRed;
                    particlefirework$spark.fadeColourGreen = this.fadeColourGreen;
                    particlefirework$spark.fadeColourBlue = this.fadeColourBlue;
                }
                particlefirework$spark.twinkle = this.twinkle;
                this.effectRenderer.addEffect(particlefirework$spark);
            }
        }
    }

    public static class Starter
    extends Particle {
        private int fireworkAge;
        private final ParticleManager theEffectRenderer;
        private NBTTagList fireworkExplosions;
        boolean twinkle;

        public Starter(World p_i46464_1_, double p_i46464_2_, double p_i46464_4_, double p_i46464_6_, double p_i46464_8_, double p_i46464_10_, double p_i46464_12_, ParticleManager p_i46464_14_, @Nullable NBTTagCompound p_i46464_15_) {
            super(p_i46464_1_, p_i46464_2_, p_i46464_4_, p_i46464_6_, 0.0, 0.0, 0.0);
            this.motionX = p_i46464_8_;
            this.motionY = p_i46464_10_;
            this.motionZ = p_i46464_12_;
            this.theEffectRenderer = p_i46464_14_;
            this.particleMaxAge = 8;
            if (p_i46464_15_ != null) {
                this.fireworkExplosions = p_i46464_15_.getTagList("Explosions", 10);
                if (this.fireworkExplosions.hasNoTags()) {
                    this.fireworkExplosions = null;
                } else {
                    this.particleMaxAge = this.fireworkExplosions.tagCount() * 2 - 1;
                    int i = 0;
                    while (i < this.fireworkExplosions.tagCount()) {
                        NBTTagCompound nbttagcompound = this.fireworkExplosions.getCompoundTagAt(i);
                        if (nbttagcompound.getBoolean("Flicker")) {
                            this.twinkle = true;
                            this.particleMaxAge += 15;
                            break;
                        }
                        ++i;
                    }
                }
            }
        }

        @Override
        public void renderParticle(BufferBuilder worldRendererIn, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        }

        @Override
        public void onUpdate() {
            if (Wolfram.getWolfram().moduleManager.isEnabled("nofireworks")) {
                return;
            }
            if (this.fireworkAge == 0 && this.fireworkExplosions != null) {
                boolean flag = this.isFarFromCamera();
                boolean flag1 = false;
                if (this.fireworkExplosions.tagCount() >= 3) {
                    flag1 = true;
                } else {
                    int i = 0;
                    while (i < this.fireworkExplosions.tagCount()) {
                        NBTTagCompound nbttagcompound = this.fireworkExplosions.getCompoundTagAt(i);
                        if (nbttagcompound.getByte("Type") == 1) {
                            flag1 = true;
                            break;
                        }
                        ++i;
                    }
                }
                SoundEvent soundevent1 = flag1 ? (flag ? SoundEvents.ENTITY_FIREWORK_LARGE_BLAST_FAR : SoundEvents.ENTITY_FIREWORK_LARGE_BLAST) : (flag ? SoundEvents.ENTITY_FIREWORK_BLAST_FAR : SoundEvents.ENTITY_FIREWORK_BLAST);
                this.worldObj.playSound(this.posX, this.posY, this.posZ, soundevent1, SoundCategory.AMBIENT, 20.0f, 0.95f + this.rand.nextFloat() * 0.1f, true);
            }
            if (this.fireworkAge % 2 == 0 && this.fireworkExplosions != null && this.fireworkAge / 2 < this.fireworkExplosions.tagCount()) {
                int k = this.fireworkAge / 2;
                NBTTagCompound nbttagcompound1 = this.fireworkExplosions.getCompoundTagAt(k);
                byte l = nbttagcompound1.getByte("Type");
                boolean flag4 = nbttagcompound1.getBoolean("Trail");
                boolean flag2 = nbttagcompound1.getBoolean("Flicker");
                int[] aint = nbttagcompound1.getIntArray("Colors");
                int[] aint1 = nbttagcompound1.getIntArray("FadeColors");
                if (aint.length == 0) {
                    aint = new int[]{ItemDye.DYE_COLORS[0]};
                }
                if (l == 1) {
                    this.createBall(0.5, 4, aint, aint1, flag4, flag2);
                } else if (l == 2) {
                    this.createShaped(0.5, new double[][]{{0.0, 1.0}, {0.3455, 0.309}, {0.9511, 0.309}, {0.3795918367346939, -0.12653061224489795}, {0.6122448979591837, -0.8040816326530612}, {0.0, -0.35918367346938773}}, aint, aint1, flag4, flag2, false);
                } else if (l == 3) {
                    this.createShaped(0.5, new double[][]{{0.0, 0.2}, {0.2, 0.2}, {0.2, 0.6}, {0.6, 0.6}, {0.6, 0.2}, {0.2, 0.2}, {0.2, 0.0}, {0.4, 0.0}, {0.4, -0.6}, {0.2, -0.6}, {0.2, -0.4}, {0.0, -0.4}}, aint, aint1, flag4, flag2, true);
                } else if (l == 4) {
                    this.createBurst(aint, aint1, flag4, flag2);
                } else {
                    this.createBall(0.25, 2, aint, aint1, flag4, flag2);
                }
                int j = aint[0];
                float f = (float)((j & 0xFF0000) >> 16) / 255.0f;
                float f1 = (float)((j & 0xFF00) >> 8) / 255.0f;
                float f2 = (float)((j & 0xFF) >> 0) / 255.0f;
                Overlay particlefirework$overlay = new Overlay(this.worldObj, this.posX, this.posY, this.posZ);
                particlefirework$overlay.setRBGColorF(f, f1, f2);
                this.theEffectRenderer.addEffect(particlefirework$overlay);
            }
            ++this.fireworkAge;
            if (this.fireworkAge > this.particleMaxAge) {
                if (this.twinkle) {
                    boolean flag3 = this.isFarFromCamera();
                    SoundEvent soundevent = flag3 ? SoundEvents.ENTITY_FIREWORK_TWINKLE_FAR : SoundEvents.ENTITY_FIREWORK_TWINKLE;
                    this.worldObj.playSound(this.posX, this.posY, this.posZ, soundevent, SoundCategory.AMBIENT, 20.0f, 0.9f + this.rand.nextFloat() * 0.15f, true);
                }
                this.setExpired();
            }
        }

        private boolean isFarFromCamera() {
            Minecraft minecraft = Minecraft.getMinecraft();
            return minecraft == null || minecraft.getRenderViewEntity() == null || minecraft.getRenderViewEntity().getDistanceSq(this.posX, this.posY, this.posZ) >= 256.0;
        }

        private void createParticle(double p_92034_1_, double p_92034_3_, double p_92034_5_, double p_92034_7_, double p_92034_9_, double p_92034_11_, int[] p_92034_13_, int[] p_92034_14_, boolean p_92034_15_, boolean p_92034_16_) {
            Spark particlefirework$spark = new Spark(this.worldObj, p_92034_1_, p_92034_3_, p_92034_5_, p_92034_7_, p_92034_9_, p_92034_11_, this.theEffectRenderer);
            particlefirework$spark.setAlphaF(0.99f);
            particlefirework$spark.setTrail(p_92034_15_);
            particlefirework$spark.setTwinkle(p_92034_16_);
            int i = this.rand.nextInt(p_92034_13_.length);
            particlefirework$spark.setColor(p_92034_13_[i]);
            if (p_92034_14_ != null && p_92034_14_.length > 0) {
                particlefirework$spark.setColorFade(p_92034_14_[this.rand.nextInt(p_92034_14_.length)]);
            }
            this.theEffectRenderer.addEffect(particlefirework$spark);
        }

        private void createBall(double speed, int size, int[] colours, int[] fadeColours, boolean trail, boolean twinkleIn) {
            double d0 = this.posX;
            double d1 = this.posY;
            double d2 = this.posZ;
            int i = -size;
            while (i <= size) {
                int j = -size;
                while (j <= size) {
                    int k = -size;
                    while (k <= size) {
                        double d3 = (double)j + (this.rand.nextDouble() - this.rand.nextDouble()) * 0.5;
                        double d4 = (double)i + (this.rand.nextDouble() - this.rand.nextDouble()) * 0.5;
                        double d5 = (double)k + (this.rand.nextDouble() - this.rand.nextDouble()) * 0.5;
                        double d6 = (double)MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5) / speed + this.rand.nextGaussian() * 0.05;
                        this.createParticle(d0, d1, d2, d3 / d6, d4 / d6, d5 / d6, colours, fadeColours, trail, twinkleIn);
                        if (i != -size && i != size && j != -size && j != size) {
                            k += size * 2 - 1;
                        }
                        ++k;
                    }
                    ++j;
                }
                ++i;
            }
        }

        private void createShaped(double speed, double[][] shape, int[] colours, int[] fadeColours, boolean trail, boolean twinkleIn, boolean p_92038_8_) {
            double d0 = shape[0][0];
            double d1 = shape[0][1];
            this.createParticle(this.posX, this.posY, this.posZ, d0 * speed, d1 * speed, 0.0, colours, fadeColours, trail, twinkleIn);
            float f = this.rand.nextFloat() * (float)Math.PI;
            double d2 = p_92038_8_ ? 0.034 : 0.34;
            int i = 0;
            while (i < 3) {
                double d3 = (double)f + (double)((float)i * (float)Math.PI) * d2;
                double d4 = d0;
                double d5 = d1;
                int j = 1;
                while (j < shape.length) {
                    double d6 = shape[j][0];
                    double d7 = shape[j][1];
                    double d8 = 0.25;
                    while (d8 <= 1.0) {
                        double d9 = (d4 + (d6 - d4) * d8) * speed;
                        double d10 = (d5 + (d7 - d5) * d8) * speed;
                        double d11 = d9 * Math.sin(d3);
                        d9 *= Math.cos(d3);
                        double d12 = -1.0;
                        while (d12 <= 1.0) {
                            this.createParticle(this.posX, this.posY, this.posZ, d9 * d12, d10, d11 * d12, colours, fadeColours, trail, twinkleIn);
                            d12 += 2.0;
                        }
                        d8 += 0.25;
                    }
                    d4 = d6;
                    d5 = d7;
                    ++j;
                }
                ++i;
            }
        }

        private void createBurst(int[] colours, int[] fadeColours, boolean trail, boolean twinkleIn) {
            double d0 = this.rand.nextGaussian() * 0.05;
            double d1 = this.rand.nextGaussian() * 0.05;
            int i = 0;
            while (i < 70) {
                double d2 = this.motionX * 0.5 + this.rand.nextGaussian() * 0.15 + d0;
                double d3 = this.motionZ * 0.5 + this.rand.nextGaussian() * 0.15 + d1;
                double d4 = this.motionY * 0.5 + this.rand.nextDouble() * 0.5;
                this.createParticle(this.posX, this.posY, this.posZ, d2, d4, d3, colours, fadeColours, trail, twinkleIn);
                ++i;
            }
        }

        @Override
        public int getFXLayer() {
            return 0;
        }
    }
}

