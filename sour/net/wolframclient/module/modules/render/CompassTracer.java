/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.render;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.WorldRenderEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;
import net.wolframclient.utils.RenderUtils;

public class CompassTracer
extends ModuleListener {
    public CompassTracer() {
        super("CompassTracer", Module.Category.RENDER, "Shows where your compass points to");
    }

    @EventTarget
    public void onRender(WorldRenderEvent event) {
        BlockPos compassPos = WMinecraft.getWorld().getSpawnPoint();
        double x = (double)compassPos.getX() - RenderManager.renderPosX;
        double y = (double)compassPos.getY() - RenderManager.renderPosY;
        double z = (double)compassPos.getZ() - RenderManager.renderPosZ;
        RenderUtils.drawLine3D(0.0, 0.0f + WMinecraft.getPlayer().getEyeHeight(), 0.0, x + 0.5, y + 0.5, z + 0.5, 0xFFFFFF);
        RenderUtils.drawBox(new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0), 0x20FFFFFF);
        RenderUtils.drawOutlinedBox(new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0), 0xFFFFFF);
    }
}

