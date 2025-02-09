/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package net.wolframclient.module.modules.render;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.wolframclient.Wolfram;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.Listener;
import net.wolframclient.event.events.WorldRenderEvent;
import net.wolframclient.module.Module;
import net.wolframclient.utils.EntityHelper;
import net.wolframclient.utils.MillisTimer;
import net.wolframclient.utils.RenderUtils;
import org.lwjgl.opengl.GL11;

public class ProphuntESP
extends Module
implements Listener {
    public List<BlockPos> updatedBlocks = new ArrayList<BlockPos>();
    MillisTimer timer = new MillisTimer();

    public ProphuntESP() {
        super("ProphuntESP", Module.Category.RENDER, "Shows where are other players in the Prophunt minigame");
    }

    @Override
    public void onEnable() {
        registry.registerListener(this);
    }

    @Override
    public void onDisable() {
        registry.unregisterListener(this);
        this.updatedBlocks.clear();
    }

    public void addToList(BlockPos b) {
        if (!this.updatedBlocks.contains(b) && this.updatedBlocks.size() < 1024) {
            this.updatedBlocks.add(b);
        }
    }

    public static ProphuntESP getInstance() {
        return (ProphuntESP)Wolfram.getWolfram().moduleManager.getModule("prophuntesp");
    }

    @EventTarget(priority=1)
    public void onWorldRender(WorldRenderEvent event) {
        if (this.timer.check(2000.0f)) {
            this.timer.reset();
            this.updatedBlocks.clear();
        }
        for (Object object : WMinecraft.getWorld().loadedEntityList) {
            if (!(object instanceof EntityFallingBlock)) continue;
            EntityFallingBlock fallingBlock = (EntityFallingBlock)object;
            double[] pos = EntityHelper.interpolate(fallingBlock);
            double x = pos[0] - RenderManager.renderPosX;
            double y = pos[1] - RenderManager.renderPosY;
            double z = pos[2] - RenderManager.renderPosZ;
            GL11.glPushMatrix();
            GL11.glTranslated((double)x, (double)y, (double)z);
            RenderUtils.drawBox(new AxisAlignedBB(-0.5, 0.0, -0.5, 0.5, 1.0, 0.5), 553603072);
            RenderUtils.drawOutlinedBox(new AxisAlignedBB(-0.5, 0.0, -0.5, 0.5, 1.0, 0.5), 0xFF5000);
            GL11.glPopMatrix();
        }
        for (BlockPos b : this.updatedBlocks) {
            double x = (double)b.getX() - RenderManager.renderPosX;
            double y = (double)b.getY() - RenderManager.renderPosY;
            double z = (double)b.getZ() - RenderManager.renderPosZ;
            GL11.glPushMatrix();
            GL11.glTranslated((double)x, (double)y, (double)z);
            RenderUtils.drawBox(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0), 0x20FF0000);
            RenderUtils.drawOutlinedBox(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0), 0xFF0000);
            GL11.glPopMatrix();
        }
    }
}

