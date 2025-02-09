/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package net.wolframclient.module.modules.world;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.wolframclient.compatibility.WConnection;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.Listener;
import net.wolframclient.event.events.RenderEvent;
import net.wolframclient.event.events.WorldRenderEvent;
import net.wolframclient.module.Module;
import net.wolframclient.utils.RenderUtils;
import org.lwjgl.opengl.GL11;

public class Nuker
extends Module
implements Listener {
    public Nuker() {
        super("Nuker", Module.Category.WORLD, "Destroys all blocks around you");
    }

    @Override
    public void onEnable() {
        registry.registerListener(this);
    }

    @Override
    public void onDisable() {
        registry.unregisterListener(this);
    }

    public void renderNukerBox(BlockPos blockPos) {
        GL11.glPushMatrix();
        double x = (double)blockPos.getX() - RenderManager.renderPosX;
        double y = (double)blockPos.getY() - RenderManager.renderPosY;
        double z = (double)blockPos.getZ() - RenderManager.renderPosZ;
        GL11.glTranslated((double)x, (double)y, (double)z);
        RenderUtils.drawOutlinedBox(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0), 0xFF0000);
        RenderUtils.drawBox(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0), 0x20FF0000);
        GL11.glPopMatrix();
    }

    @EventTarget
    public void onRenderWorld(WorldRenderEvent event) {
        if (!WMinecraft.getPlayer().capabilities.isCreativeMode) {
            return;
        }
        int y = (int)this.getSettings().getFloat("nuker_range");
        while (y >= (int)(-this.getSettings().getFloat("nuker_range"))) {
            int z = (int)(-this.getSettings().getFloat("nuker_range"));
            while ((float)z <= this.getSettings().getFloat("nuker_range")) {
                int x = (int)(-this.getSettings().getFloat("nuker_range"));
                while ((float)x <= this.getSettings().getFloat("nuker_range")) {
                    int xPos = (int)Math.round(WMinecraft.getPlayer().posX + (double)x);
                    int yPos = (int)Math.round(WMinecraft.getPlayer().posY + (double)y);
                    int zPos = (int)Math.round(WMinecraft.getPlayer().posZ + (double)z);
                    BlockPos blockPos = new BlockPos(xPos, yPos, zPos);
                    IBlockState state = WMinecraft.getWorld().getBlockState(blockPos);
                    if (state.getMaterial() != Material.AIR && WMinecraft.getPlayer().getDistance(xPos, yPos, zPos) < (double)this.getSettings().getFloat("nuker_range")) {
                        this.renderNukerBox(blockPos);
                    }
                    ++x;
                }
                ++z;
            }
            --y;
        }
    }

    @EventTarget
    public void onUpdate(RenderEvent event) {
        if (!WMinecraft.getPlayer().capabilities.isCreativeMode) {
            return;
        }
        int y = (int)this.getSettings().getFloat("nuker_range");
        while (y >= (int)(-this.getSettings().getFloat("nuker_range"))) {
            int z = (int)(-this.getSettings().getFloat("nuker_range"));
            while ((float)z <= this.getSettings().getFloat("nuker_range")) {
                int x = (int)(-this.getSettings().getFloat("nuker_range"));
                while ((float)x <= this.getSettings().getFloat("nuker_range")) {
                    int xPos = (int)Math.round(WMinecraft.getPlayer().posX + (double)x);
                    int yPos = (int)Math.round(WMinecraft.getPlayer().posY + (double)y);
                    int zPos = (int)Math.round(WMinecraft.getPlayer().posZ + (double)z);
                    BlockPos blockPos = new BlockPos(xPos, yPos, zPos);
                    IBlockState state = WMinecraft.getWorld().getBlockState(blockPos);
                    if (state.getMaterial() != Material.AIR && WMinecraft.getPlayer().getDistance(xPos, yPos, zPos) < (double)this.getSettings().getFloat("nuker_range")) {
                        WConnection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos, EnumFacing.NORTH));
                        WConnection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, EnumFacing.NORTH));
                    }
                    ++x;
                }
                ++z;
            }
            --y;
        }
    }
}

