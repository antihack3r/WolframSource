/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package net.wolframclient.module.modules.render;

import java.util.ArrayList;
import net.minecraft.client.renderer.entity.RenderManager;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.Listener;
import net.wolframclient.event.events.RenderEvent;
import net.wolframclient.event.events.WorldRenderEvent;
import net.wolframclient.gui.GuiManager;
import net.wolframclient.module.Module;
import net.wolframclient.utils.RenderUtils;
import org.lwjgl.opengl.GL11;

public class Breadcrumbs
extends Module
implements Listener {
    double lastX = 0.0;
    double lastY = 0.0;
    double lastZ = 0.0;
    ArrayList<double[]> crumbs = new ArrayList();
    public boolean customColor = false;
    public int color = 0;

    public Breadcrumbs() {
        super("Breadcrumbs", Module.Category.RENDER, "Leaves a trace behind you");
    }

    @Override
    public void onEnable() {
        registry.registerListener(this);
    }

    @Override
    public void onDisable() {
        this.crumbs.clear();
        registry.unregisterListener(this);
        this.lastX = 0.0;
        this.lastY = 0.0;
        this.lastZ = 0.0;
    }

    @EventTarget(priority=0)
    public void onWorldRender(WorldRenderEvent event) {
        GL11.glPushMatrix();
        for (double[] crumb : this.crumbs) {
            double renderX1 = crumb[0] - RenderManager.renderPosX;
            double renderY1 = crumb[1] - RenderManager.renderPosY + 0.05;
            double renderZ1 = crumb[2] - RenderManager.renderPosZ;
            double renderX2 = crumb[3] - RenderManager.renderPosX;
            double renderY2 = crumb[4] - RenderManager.renderPosY + 0.05;
            double renderZ2 = crumb[5] - RenderManager.renderPosZ;
            RenderUtils.drawLine3D(renderX1, renderY1, renderZ1, renderX2, renderY2, renderZ2, this.customColor ? this.color : GuiManager.getHexMainColor(), !this.getSettings().getBoolean("breadcrumbs_depth"));
        }
        GL11.glPopMatrix();
    }

    @EventTarget
    public void onUpdate(RenderEvent event) {
        if (this.lastX == 0.0 && this.lastY == 0.0 && this.lastZ == 0.0) {
            this.lastX = WMinecraft.getPlayer().posX;
            this.lastY = WMinecraft.getPlayer().posY;
            this.lastZ = WMinecraft.getPlayer().posZ;
            return;
        }
        double[] crumb = new double[]{this.lastX, this.lastY, this.lastZ, WMinecraft.getPlayer().posX, WMinecraft.getPlayer().posY, WMinecraft.getPlayer().posZ};
        this.crumbs.add(crumb);
        this.lastX = WMinecraft.getPlayer().posX;
        this.lastY = WMinecraft.getPlayer().posY;
        this.lastZ = WMinecraft.getPlayer().posZ;
    }
}

