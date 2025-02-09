/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.util.glu.Cylinder
 */
package net.wolframclient.module.modules.render;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemSplashPotion;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.wolframclient.Wolfram;
import net.wolframclient.compatibility.WMath;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.Listener;
import net.wolframclient.event.events.WorldRenderEvent;
import net.wolframclient.gui.GuiManager;
import net.wolframclient.module.Module;
import net.wolframclient.utils.RenderUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;

public class Projectiles
extends Module
implements Listener {
    public Projectiles() {
        super("Projectiles", Module.Category.RENDER, "See where your arrows are going to land");
    }

    @Override
    public void onEnable() {
        registry.registerListener(this);
    }

    @Override
    public void onDisable() {
        registry.unregisterListener(this);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @EventTarget
    public void onUpdate(WorldRenderEvent event) {
        float size;
        float gravity;
        boolean isBow = false;
        float pitchDifference = 0.0f;
        float motionFactor = 1.5f;
        float motionSlowdown = 0.99f;
        EntityPlayerSP thePlayer = WMinecraft.getPlayer();
        if (thePlayer.getHeldItemMainhand() == null) return;
        Item item = thePlayer.getHeldItemMainhand().getItem();
        if (item instanceof ItemBow) {
            isBow = true;
            gravity = 0.05f;
            size = 0.3f;
            float power = (float)thePlayer.getItemInUseMaxCount() / 20.0f;
            power = (power * power + power * 2.0f) / 3.0f;
            if (Wolfram.getWolfram().moduleManager.isEnabled("fastbow")) {
                power = 1.0f;
            }
            if ((double)power < 0.1) {
                return;
            }
            if (power > 1.0f) {
                power = 1.0f;
            }
            motionFactor = power * 3.0f;
        } else if (item instanceof ItemFishingRod) {
            gravity = 0.04f;
            size = 0.25f;
            motionSlowdown = 0.92f;
        } else if (thePlayer.getHeldItemMainhand().getItem() instanceof ItemSplashPotion) {
            gravity = 0.05f;
            size = 0.25f;
            pitchDifference = -20.0f;
            motionFactor = 0.5f;
        } else {
            if (!(item instanceof ItemSnowball) && !(item instanceof ItemEnderPearl) && !(item instanceof ItemEgg)) return;
            gravity = 0.03f;
            size = 0.25f;
        }
        double posX = RenderManager.renderPosX - (double)(WMath.cos(thePlayer.rotationYaw / 180.0f * (float)Math.PI) * 0.16f);
        double posY = RenderManager.renderPosY + (double)thePlayer.getEyeHeight() - (double)0.1f;
        double posZ = RenderManager.renderPosZ - (double)(WMath.sin(thePlayer.rotationYaw / 180.0f * (float)Math.PI) * 0.16f);
        double motionX = (double)(-WMath.sin(thePlayer.rotationYaw / 180.0f * (float)Math.PI) * WMath.cos(thePlayer.rotationPitch / 180.0f * (float)Math.PI)) * (isBow ? 1.0 : 0.4);
        double motionY = (double)(-WMath.sin((thePlayer.rotationPitch + pitchDifference) / 180.0f * (float)Math.PI)) * (isBow ? 1.0 : 0.4);
        double motionZ = (double)(WMath.cos(thePlayer.rotationYaw / 180.0f * (float)Math.PI) * WMath.cos(thePlayer.rotationPitch / 180.0f * (float)Math.PI)) * (isBow ? 1.0 : 0.4);
        float distance = (float)Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
        motionX /= (double)distance;
        motionY /= (double)distance;
        motionZ /= (double)distance;
        motionX *= (double)motionFactor;
        motionY *= (double)motionFactor;
        motionZ *= (double)motionFactor;
        RayTraceResult landingPosition = null;
        boolean hasLanded = false;
        boolean hitEntity = false;
        RenderUtils.enableRender3D(true);
        RenderUtils.setColor(GuiManager.getHexMainColor());
        GL11.glLineWidth((float)2.0f);
        GL11.glBegin((int)3);
        boolean limit = false;
        while (!hasLanded && posY > 0.0) {
            Vec3d posBefore = new Vec3d(posX, posY, posZ);
            Vec3d posAfter = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
            landingPosition = WMinecraft.getWorld().rayTraceBlocks(posBefore, posAfter, false, true, false);
            posBefore = new Vec3d(posX, posY, posZ);
            posAfter = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
            if (landingPosition != null) {
                hasLanded = true;
                posAfter = new Vec3d(landingPosition.hitVec.xCoord, landingPosition.hitVec.yCoord, landingPosition.hitVec.zCoord);
            }
            AxisAlignedBB arrowBox = new AxisAlignedBB(posX - (double)size, posY - (double)size, posZ - (double)size, posX + (double)size, posY + (double)size, posZ + (double)size);
            List<Entity> entityList = this.getEntitiesWithinAABB(arrowBox.addCoord(motionX, motionY, motionZ).expand(1.0, 1.0, 1.0));
            int i = 0;
            while (i < entityList.size()) {
                AxisAlignedBB possibleEntityBoundingBox;
                RayTraceResult possibleEntityLanding;
                Entity possibleEntity = entityList.get(i);
                if (possibleEntity.canBeCollidedWith() && possibleEntity != thePlayer && (possibleEntityLanding = (possibleEntityBoundingBox = possibleEntity.getEntityBoundingBox().expand(size, size, size)).calculateIntercept(posBefore, posAfter)) != null) {
                    hitEntity = true;
                    hasLanded = true;
                    landingPosition = possibleEntityLanding;
                }
                ++i;
            }
            BlockPos var18 = new BlockPos(posX += motionX, posY += motionY, posZ += motionZ);
            IBlockState var2 = WMinecraft.getWorld().getBlockState(var18);
            if (var2.getMaterial() == Material.WATER) {
                motionX *= 0.6;
                motionY *= 0.6;
                motionZ *= 0.6;
            } else {
                motionX *= (double)motionSlowdown;
                motionY *= (double)motionSlowdown;
                motionZ *= (double)motionSlowdown;
            }
            motionY -= (double)gravity;
            GL11.glVertex3d((double)(posX - RenderManager.renderPosX), (double)(posY - RenderManager.renderPosY), (double)(posZ - RenderManager.renderPosZ));
        }
        GL11.glEnd();
        GL11.glPushMatrix();
        GL11.glTranslated((double)(posX - RenderManager.renderPosX), (double)(posY - RenderManager.renderPosY), (double)(posZ - RenderManager.renderPosZ));
        if (landingPosition != null) {
            switch (landingPosition.sideHit.getAxis().ordinal()) {
                case 0: {
                    GL11.glRotatef((float)90.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                    break;
                }
                case 2: {
                    GL11.glRotatef((float)90.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                    break;
                }
            }
            if (hitEntity) {
                RenderUtils.setColor(GuiManager.getNegativeHexColor());
            }
        }
        this.renderPoint();
        GL11.glPopMatrix();
        RenderUtils.disableRender3D(true);
    }

    private void renderPoint() {
        GL11.glBegin((int)1);
        GL11.glVertex3d((double)-0.5, (double)0.0, (double)0.0);
        GL11.glVertex3d((double)0.0, (double)0.0, (double)0.0);
        GL11.glVertex3d((double)0.0, (double)0.0, (double)-0.5);
        GL11.glVertex3d((double)0.0, (double)0.0, (double)0.0);
        GL11.glVertex3d((double)0.5, (double)0.0, (double)0.0);
        GL11.glVertex3d((double)0.0, (double)0.0, (double)0.0);
        GL11.glVertex3d((double)0.0, (double)0.0, (double)0.5);
        GL11.glVertex3d((double)0.0, (double)0.0, (double)0.0);
        GL11.glEnd();
        Cylinder c = new Cylinder();
        GL11.glRotatef((float)-90.0f, (float)1.0f, (float)0.0f, (float)0.0f);
        c.setDrawStyle(100011);
        c.draw(0.5f, 0.5f, 0.1f, 24, 1);
    }

    private List<Entity> getEntitiesWithinAABB(AxisAlignedBB axisalignedBB) {
        ArrayList<Entity> list = new ArrayList<Entity>();
        int chunkMinX = WMath.floor((axisalignedBB.minX - 2.0) / 16.0);
        int chunkMaxX = WMath.floor((axisalignedBB.maxX + 2.0) / 16.0);
        int chunkMinZ = WMath.floor((axisalignedBB.minZ - 2.0) / 16.0);
        int chunkMaxZ = WMath.floor((axisalignedBB.maxZ + 2.0) / 16.0);
        int x = chunkMinX;
        while (x <= chunkMaxX) {
            int z = chunkMinZ;
            while (z <= chunkMaxZ) {
                WMinecraft.getWorld().getChunkFromChunkCoords(x, z).getEntitiesWithinAABBForEntity(WMinecraft.getPlayer(), axisalignedBB, list, null);
                ++z;
            }
            ++x;
        }
        return list;
    }
}

