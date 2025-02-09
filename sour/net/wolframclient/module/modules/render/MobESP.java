/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package net.wolframclient.module.modules.render;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.math.AxisAlignedBB;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.Listener;
import net.wolframclient.event.events.WorldRenderEvent;
import net.wolframclient.module.Module;
import net.wolframclient.utils.EntityHelper;
import net.wolframclient.utils.RenderUtils;
import org.lwjgl.opengl.GL11;

public class MobESP
extends Module
implements Listener {
    public MobESP() {
        super("MobESP", Module.Category.RENDER, "Draws a box around mobs and animals");
    }

    @Override
    public void onEnable() {
        registry.registerListener(this);
    }

    @Override
    public void onDisable() {
        registry.unregisterListener(this);
    }

    @EventTarget(priority=1)
    public void onWorldRender(WorldRenderEvent event) {
        for (Object object : WMinecraft.getWorld().loadedEntityList) {
            int color;
            if (object instanceof EntityMob) {
                color = 0xFF0000;
            } else if (object instanceof EntityAnimal) {
                color = 0x4040FF;
            } else if (object instanceof IMob) {
                color = 0xFF7000;
            } else {
                if (!(object instanceof EntityVillager)) continue;
                color = 0xB000B0;
            }
            EntityLiving mob = (EntityLiving)object;
            double[] pos = EntityHelper.interpolate(mob);
            double x = pos[0] - RenderManager.renderPosX;
            double y = pos[1] - RenderManager.renderPosY;
            double z = pos[2] - RenderManager.renderPosZ;
            GL11.glPushMatrix();
            GL11.glTranslated((double)x, (double)y, (double)z);
            GL11.glRotatef((float)(-mob.rotationYaw), (float)0.0f, (float)1.0f, (float)0.0f);
            RenderUtils.drawOutlinedBox(new AxisAlignedBB((double)mob.width / 2.0, 0.0, -((double)mob.width / 2.0), (double)(-mob.width) / 2.0, (double)mob.height + 0.1, (double)mob.width / 2.0), color);
            GL11.glPopMatrix();
        }
    }
}

