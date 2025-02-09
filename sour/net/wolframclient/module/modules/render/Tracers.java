/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package net.wolframclient.module.modules.render;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.wolframclient.Wolfram;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.Listener;
import net.wolframclient.event.events.WorldRenderEvent;
import net.wolframclient.gui.GuiManager;
import net.wolframclient.module.Module;
import net.wolframclient.utils.EntityFakePlayer;
import net.wolframclient.utils.EntityHelper;
import net.wolframclient.utils.RenderUtils;
import org.lwjgl.opengl.GL11;

public class Tracers
extends Module
implements Listener {
    public Tracers() {
        super("Tracers", Module.Category.RENDER, "Draws a line to players");
    }

    @Override
    public void onEnable() {
        registry.registerListener(this);
    }

    @Override
    public void onDisable() {
        registry.unregisterListener(this);
    }

    @EventTarget(priority=3)
    public void onWorldRender(WorldRenderEvent event) {
        GL11.glPushMatrix();
        for (Object object : WMinecraft.getWorld().loadedEntityList) {
            EntityPlayer player;
            if (!(object instanceof EntityPlayer) || (player = (EntityPlayer)object) == WMinecraft.getPlayer() || player instanceof EntityFakePlayer) continue;
            double[] pos = EntityHelper.interpolate(player);
            double x = pos[0] - RenderManager.renderPosX;
            double y = pos[1] - RenderManager.renderPosY;
            double z = pos[2] - RenderManager.renderPosZ;
            Vec3d eyes = new Vec3d(0.0, 0.0, 1.0).rotatePitch(-((float)Math.toRadians(WMinecraft.getPlayer().rotationPitch))).rotateYaw(-((float)Math.toRadians(WMinecraft.getPlayer().rotationYaw)));
            if (Wolfram.getWolfram().relationManager.isFriend(player)) {
                RenderUtils.drawLine3D(eyes.xCoord, eyes.yCoord + (double)WMinecraft.getPlayer().getEyeHeight(), eyes.zCoord, x, y, z, 65280);
                continue;
            }
            if (Wolfram.getWolfram().relationManager.isEnemy(player)) {
                RenderUtils.drawLine3D(eyes.xCoord, eyes.yCoord + (double)WMinecraft.getPlayer().getEyeHeight(), eyes.zCoord, x, y, z, 0xFF0000);
                continue;
            }
            RenderUtils.drawLine3D(eyes.xCoord, eyes.yCoord + (double)WMinecraft.getPlayer().getEyeHeight(), eyes.zCoord, x, y, z, GuiManager.getHexMainColor());
        }
        GL11.glPopMatrix();
    }
}

