/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package net.wolframclient.module.modules.render;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.WorldRenderEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;
import net.wolframclient.utils.EntityHelper;
import net.wolframclient.utils.RenderUtils;
import org.lwjgl.opengl.GL11;

public class FarmhuntESP
extends ModuleListener {
    public FarmhuntESP() {
        super("FarmhuntESP", Module.Category.RENDER, "Draws a box around players disguised as mobs");
    }

    @EventTarget(priority=1)
    public void onWorldRender(WorldRenderEvent event) {
        for (Object object : WMinecraft.getWorld().loadedEntityList) {
            if (object == WMinecraft.getPlayer() || !(object instanceof EntityLivingBase)) continue;
            EntityLivingBase mob = (EntityLivingBase)object;
            if (mob.rotationPitch == 0.0f) continue;
            double[] pos = EntityHelper.interpolate(mob);
            double x = pos[0] - RenderManager.renderPosX;
            double y = pos[1] - RenderManager.renderPosY;
            double z = pos[2] - RenderManager.renderPosZ;
            GL11.glPushMatrix();
            GL11.glTranslated((double)x, (double)y, (double)z);
            GL11.glRotatef((float)(-mob.rotationYaw), (float)0.0f, (float)1.0f, (float)0.0f);
            RenderUtils.drawOutlinedBox(new AxisAlignedBB((double)mob.width / 2.0, 0.0, -((double)mob.width / 2.0), (double)(-mob.width) / 2.0, (double)mob.height + 0.1, (double)mob.width / 2.0), 0xFF0000);
            GL11.glPopMatrix();
        }
    }
}

