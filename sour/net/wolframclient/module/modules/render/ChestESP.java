/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package net.wolframclient.module.modules.render;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.math.AxisAlignedBB;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.compatibility.WTileEntity;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.Listener;
import net.wolframclient.event.events.WorldRenderEvent;
import net.wolframclient.module.Module;
import net.wolframclient.utils.RenderUtils;
import org.lwjgl.opengl.GL11;

public class ChestESP
extends Module
implements Listener {
    public ChestESP() {
        super("ChestESP", Module.Category.RENDER, "Draws a box around chests");
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
        for (Object object : WMinecraft.getWorld().loadedTileEntityList) {
            double z;
            double y;
            double x;
            TileEntity chest;
            if (object instanceof TileEntityChest) {
                int color;
                chest = (TileEntityChest)object;
                x = (double)chest.pos.getX() - RenderManager.renderPosX;
                y = (double)chest.pos.getY() - RenderManager.renderPosY;
                z = (double)chest.pos.getZ() - RenderManager.renderPosZ;
                GL11.glPushMatrix();
                GL11.glTranslated((double)x, (double)y, (double)z);
                int n = color = WTileEntity.isTrappedChest(chest) ? 0xFF0000 : 0xFF7000;
                if (chest.adjacentChestXPos == null && chest.adjacentChestZPos == null && chest.adjacentChestXNeg == null && chest.adjacentChestZNeg == null) {
                    RenderUtils.drawBox(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0), color + 0x20000000);
                    RenderUtils.drawOutlinedBox(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0), color);
                } else if (chest.adjacentChestXPos != null) {
                    RenderUtils.drawBox(new AxisAlignedBB(0.0, 0.0, 0.0, 2.0, 1.0, 1.0), color + 0x20000000);
                    RenderUtils.drawOutlinedBox(new AxisAlignedBB(0.0, 0.0, 0.0, 2.0, 1.0, 1.0), color);
                } else if (chest.adjacentChestZPos != null) {
                    RenderUtils.drawBox(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 2.0), color + 0x20000000);
                    RenderUtils.drawOutlinedBox(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 2.0), color);
                }
                GL11.glPopMatrix();
                continue;
            }
            if (!(object instanceof TileEntityEnderChest)) continue;
            chest = (TileEntityEnderChest)object;
            x = (double)((TileEntityEnderChest)chest).pos.getX() - RenderManager.renderPosX;
            y = (double)((TileEntityEnderChest)chest).pos.getY() - RenderManager.renderPosY;
            z = (double)((TileEntityEnderChest)chest).pos.getZ() - RenderManager.renderPosZ;
            GL11.glPushMatrix();
            GL11.glTranslated((double)x, (double)y, (double)z);
            RenderUtils.drawBox(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0), 0x20B000B0);
            RenderUtils.drawOutlinedBox(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0), 0xB000B0);
            GL11.glPopMatrix();
        }
    }
}

