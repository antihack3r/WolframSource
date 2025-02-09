/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package net.wolframclient.module.modules.render;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
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

public class PlayerESP
extends Module
implements Listener {
    public PlayerESP() {
        super("PlayerESP", Module.Category.RENDER, "Draws a box around players");
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
        for (Object object : WMinecraft.getWorld().loadedEntityList) {
            EntityPlayer player;
            if (!(object instanceof EntityPlayer) || (player = (EntityPlayer)object) == WMinecraft.getPlayer() || player instanceof EntityFakePlayer || Wolfram.getWolfram().moduleManager.isEnabled("farmhuntesp") && player.rotationPitch != 0.0f) continue;
            double[] pos = EntityHelper.interpolate(player);
            double x = pos[0] - RenderManager.renderPosX;
            double y = pos[1] - RenderManager.renderPosY;
            double z = pos[2] - RenderManager.renderPosZ;
            GL11.glPushMatrix();
            GL11.glTranslated((double)x, (double)y, (double)z);
            GL11.glRotatef((float)(-player.rotationYaw), (float)0.0f, (float)1.0f, (float)0.0f);
            int color = Wolfram.getWolfram().relationManager.isFriend(player) ? 65280 : (Wolfram.getWolfram().relationManager.isEnemy(player) ? 0xFF0000 : GuiManager.getHexMainColor());
            RenderUtils.drawOutlinedBox(new AxisAlignedBB((double)player.width / 2.0, 0.0, -((double)player.width / 2.0), (double)(-player.width) / 2.0, (double)player.height + 0.1, (double)player.width / 2.0), color);
            GL11.glPopMatrix();
        }
    }
}

