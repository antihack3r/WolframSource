/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package net.wolframclient.module.modules.render;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.AxisAlignedBB;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.Listener;
import net.wolframclient.event.events.WorldRenderEvent;
import net.wolframclient.gui.GuiManager;
import net.wolframclient.module.Module;
import net.wolframclient.utils.EntityHelper;
import net.wolframclient.utils.RenderUtils;
import org.lwjgl.opengl.GL11;

public class ItemESP
extends Module
implements Listener {
    public ItemESP() {
        super("ItemESP", Module.Category.RENDER, "Draws a box around dropped items");
    }

    @Override
    public void onEnable() {
        registry.registerListener(this);
    }

    @Override
    public void onDisable() {
        registry.unregisterListener(this);
    }

    @EventTarget(priority=0)
    public void onRenderWorld(WorldRenderEvent event) {
        for (Object object : WMinecraft.getWorld().loadedEntityList) {
            if (!(object instanceof EntityItem)) continue;
            EntityItem item = (EntityItem)object;
            double[] pos = EntityHelper.interpolate(item);
            double x = pos[0] - RenderManager.renderPosX;
            double y = pos[1] - RenderManager.renderPosY + 0.1;
            double z = pos[2] - RenderManager.renderPosZ;
            GL11.glPushMatrix();
            GL11.glTranslated((double)x, (double)y, (double)z);
            RenderUtils.drawOutlinedBox(new AxisAlignedBB((double)item.width / 2.0, 0.0, -((double)item.width / 2.0), (double)(-item.width) / 2.0, item.height, (double)item.width / 2.0), GuiManager.getHexMainColor());
            GL11.glPopMatrix();
        }
    }
}

