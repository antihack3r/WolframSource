/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.player;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.wolframclient.compatibility.WConnection;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.AttackEntityEvent;
import net.wolframclient.event.events.LeftClickEvent;
import net.wolframclient.event.events.PlayerUpdateEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;
import net.wolframclient.utils.MillisTimer;

public class Enhance
extends ModuleListener {
    int count = 0;
    MillisTimer timer = new MillisTimer();

    public Enhance() {
        super("Enhance", Module.Category.PLAYER, "Help the user by predicting [Work in progress]");
    }

    @EventTarget
    public void onAttack(AttackEntityEvent event) {
        if (event.getTarget() instanceof EntityPigZombie && ((EntityPigZombie)event.getTarget()).angerLevel > 0) {
            event.setCancelled(true);
        }
    }

    @EventTarget
    public void onBreak(LeftClickEvent event) {
        Block block;
        if (Minecraft.getMinecraft().objectMouseOver != null && Minecraft.getMinecraft().objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK && (Block.getIdFromBlock(block = WMinecraft.getWorld().getBlockState(Minecraft.getMinecraft().objectMouseOver.getBlockPos()).getBlock()) == Block.getIdFromBlock(Blocks.TALLGRASS) || Block.getIdFromBlock(block) == Block.getIdFromBlock(Blocks.DOUBLE_PLANT))) {
            ++this.count;
            this.timer.reset();
        }
    }

    @EventTarget
    public void onUpdate(PlayerUpdateEvent event) {
        if (this.count > 0 && this.timer.check(5000.0f)) {
            --this.count;
            this.timer.reset();
        }
        if (this.count >= 5) {
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
                        Block block = WMinecraft.getWorld().getBlockState(blockPos).getBlock();
                        if ((Block.getIdFromBlock(block) == Block.getIdFromBlock(Blocks.TALLGRASS) || Block.getIdFromBlock(block) == Block.getIdFromBlock(Blocks.DOUBLE_PLANT)) && WMinecraft.getPlayer().getDistance(xPos, yPos, zPos) < (double)this.getSettings().getFloat("nuker_range")) {
                            WConnection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos, EnumFacing.NORTH));
                            WConnection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, EnumFacing.NORTH));
                            this.timer.reset();
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

