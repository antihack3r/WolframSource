/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.movement;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockVine;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.PreMotionUpdateEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;

public final class FastLadder
extends ModuleListener {
    public FastLadder() {
        super("FastLadder", Module.Category.MOVEMENT, "Climb up ladders instantly");
    }

    @EventTarget
    public void onPreMotionUpdate(PreMotionUpdateEvent event) {
        if (!(Minecraft.getMinecraft().gameSettings.keyBindForward.pressed || Minecraft.getMinecraft().gameSettings.keyBindBack.pressed || Minecraft.getMinecraft().gameSettings.keyBindLeft.pressed || Minecraft.getMinecraft().gameSettings.keyBindRight.pressed || Minecraft.getMinecraft().gameSettings.keyBindSneak.pressed)) {
            return;
        }
        if (!(WMinecraft.getPlayer().isOnLadder() && WMinecraft.getPlayer().isCollidedHorizontally && WMinecraft.getPlayer().onGround)) {
            return;
        }
        int ladders = this.getAboveLadders();
        if (ladders == 0) {
            return;
        }
        WMinecraft.getPlayer().setPosition(WMinecraft.getPlayer().posX, (ladders < 10 ? (double)ladders + 0.99 : 9.99) + WMinecraft.getPlayer().posY, WMinecraft.getPlayer().posZ);
        WMinecraft.getPlayer().motionY = -0.1;
        WMinecraft.getPlayer().motionX = 0.0;
        WMinecraft.getPlayer().motionZ = 0.0;
    }

    public int getAboveLadders() {
        int ladders = 0;
        int dist = 1;
        while (dist < 256) {
            BlockPos pos = new BlockPos(WMinecraft.getPlayer().posX, WMinecraft.getPlayer().posY + (double)dist, WMinecraft.getPlayer().posZ);
            Block block = WMinecraft.getWorld().getBlockState(pos).getBlock();
            if (!(block instanceof BlockLadder) && !(block instanceof BlockVine)) break;
            ++ladders;
            ++dist;
        }
        return ladders;
    }
}

