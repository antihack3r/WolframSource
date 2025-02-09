/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package net.wolframclient.module.modules.render;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.wolframclient.compatibility.WMath;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.Listener;
import net.wolframclient.event.events.WorldRenderEvent;
import net.wolframclient.gui.GuiManager;
import net.wolframclient.module.Module;
import net.wolframclient.utils.RenderUtils;
import org.lwjgl.opengl.GL11;

public class ArrowTrajectories
extends Module
implements Listener {
    public ArrowTrajectories() {
        super("ArrowTrajectories", Module.Category.RENDER, "Shows the trajectories of fired arrows");
    }

    @Override
    public void onEnable() {
        registry.registerListener(this);
    }

    @Override
    public void onDisable() {
        registry.unregisterListener(this);
    }

    @EventTarget
    public void onUpdate(WorldRenderEvent event) {
        for (Object object : WMinecraft.getWorld().loadedEntityList) {
            EntityPlayer player;
            if (!(object instanceof EntityPlayer) || (player = (EntityPlayer)object) == WMinecraft.getPlayer() || player.getHeldItemMainhand() == null) continue;
            float gravity = 0.05f;
            Item item = player.getHeldItemMainhand().getItem();
            if (!(item instanceof ItemBow)) continue;
            float power = (float)player.getItemInUseMaxCount() / 20.0f;
            if ((double)(power = (power * power + power * 2.0f) / 3.0f) < 0.1) continue;
            if (power > 1.0f) {
                power = 1.0f;
            }
            float motionFactor = power * 3.0f;
            double posX = player.posX - (double)(WMath.cos(player.rotationYaw / 180.0f * (float)Math.PI) * 0.16f);
            double posY = player.posY + (double)player.getEyeHeight() - (double)0.1f;
            double posZ = player.posZ - (double)(WMath.sin(player.rotationYaw / 180.0f * (float)Math.PI) * 0.16f);
            double motionX = -WMath.sin(player.rotationYaw / 180.0f * (float)Math.PI) * WMath.cos(player.rotationPitch / 180.0f * (float)Math.PI);
            double motionY = -WMath.sin(player.rotationPitch / 180.0f * (float)Math.PI);
            double motionZ = WMath.cos(player.rotationYaw / 180.0f * (float)Math.PI) * WMath.cos(player.rotationPitch / 180.0f * (float)Math.PI);
            float distance = (float)Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
            motionX /= (double)distance;
            motionY /= (double)distance;
            motionZ /= (double)distance;
            motionX *= (double)motionFactor;
            motionY *= (double)motionFactor;
            motionZ *= (double)motionFactor;
            RayTraceResult landingPosition = null;
            boolean hasLanded = false;
            RenderUtils.enableRender3D(true);
            RenderUtils.setColor(GuiManager.getHexMainColor());
            GL11.glLineWidth((float)2.0f);
            GL11.glBegin((int)3);
            int limit = 0;
            while (!hasLanded && limit < 300) {
                Vec3d posBefore = new Vec3d(posX, posY, posZ);
                Vec3d posAfter = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
                landingPosition = WMinecraft.getWorld().rayTraceBlocks(posBefore, posAfter, false, true, false);
                if (landingPosition != null) {
                    hasLanded = true;
                }
                BlockPos var18 = new BlockPos(posX += motionX, posY += motionY, posZ += motionZ);
                IBlockState state = WMinecraft.getWorld().getBlockState(var18);
                if (state.getMaterial() == Material.WATER) {
                    motionX *= 0.6;
                    motionY *= 0.6;
                    motionZ *= 0.6;
                } else {
                    motionX *= 0.99;
                    motionY *= 0.99;
                    motionZ *= 0.99;
                }
                motionY -= (double)0.05f;
                GL11.glVertex3d((double)(posX - RenderManager.renderPosX), (double)(posY - RenderManager.renderPosY), (double)(posZ - RenderManager.renderPosZ));
                ++limit;
            }
            GL11.glEnd();
            RenderUtils.disableRender3D(true);
        }
        for (Object object : WMinecraft.getWorld().loadedEntityList) {
            if (!(object instanceof EntityArrow)) continue;
            EntityArrow arrow = (EntityArrow)object;
            if (arrow.inGround) continue;
            double posX = arrow.posX;
            double posY = arrow.posY;
            double posZ = arrow.posZ;
            double motionX = arrow.motionX;
            double motionY = arrow.motionY;
            double motionZ = arrow.motionZ;
            RayTraceResult landingPosition = null;
            boolean hasLanded = false;
            float gravity = 0.05f;
            RenderUtils.enableRender3D(true);
            RenderUtils.setColor(GuiManager.getHexMainColor());
            GL11.glLineWidth((float)2.0f);
            GL11.glBegin((int)3);
            int limit = 0;
            while (!hasLanded && limit < 300) {
                Vec3d posBefore = new Vec3d(posX, posY, posZ);
                Vec3d posAfter = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
                landingPosition = WMinecraft.getWorld().rayTraceBlocks(posBefore, posAfter, false, true, false);
                if (landingPosition != null) {
                    hasLanded = true;
                }
                BlockPos var18 = new BlockPos(posX += motionX, posY += motionY, posZ += motionZ);
                IBlockState state = WMinecraft.getWorld().getBlockState(var18);
                if (state.getMaterial() == Material.WATER) {
                    motionX *= 0.6;
                    motionY *= 0.6;
                    motionZ *= 0.6;
                } else {
                    motionX *= 0.99;
                    motionY *= 0.99;
                    motionZ *= 0.99;
                }
                motionY -= (double)0.05f;
                GL11.glVertex3d((double)(posX - RenderManager.renderPosX), (double)(posY - RenderManager.renderPosY), (double)(posZ - RenderManager.renderPosZ));
                ++limit;
            }
            GL11.glEnd();
            RenderUtils.disableRender3D(true);
        }
    }
}

