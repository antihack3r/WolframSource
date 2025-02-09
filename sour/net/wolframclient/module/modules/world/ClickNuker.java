/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.world;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.wolframclient.compatibility.WConnection;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.LeftClickEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;

public class ClickNuker
extends ModuleListener {
    public ClickNuker() {
        super("ClickNuker", Module.Category.WORLD, "Nuke selected blocks by clicking on them");
    }

    @EventTarget
    public void onBreak(LeftClickEvent event) {
        if (Minecraft.getMinecraft().objectMouseOver != null && Minecraft.getMinecraft().objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
            Block block = WMinecraft.getWorld().getBlockState(Minecraft.getMinecraft().objectMouseOver.getBlockPos()).getBlock();
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
                        IBlockState block2 = WMinecraft.getWorld().getBlockState(blockPos);
                        if (block2.getMaterial() != Material.AIR && Block.getIdFromBlock(block) == Block.getIdFromBlock(block2.getBlock()) && WMinecraft.getPlayer().getDistance(xPos, yPos, zPos) < (double)this.getSettings().getFloat("nuker_range")) {
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
}

