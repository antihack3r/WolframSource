/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.wolframclient.Wolfram;
import net.wolframclient.compatibility.WConnection;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.compatibility.WPlayerController;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.PlayerUpdateEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;

public final class FastBow
extends ModuleListener {
    public FastBow() {
        super("FastBow", Module.Category.COMBAT, "Shoot arrows at very high speed");
    }

    @EventTarget
    public void onUpdate(PlayerUpdateEvent event) {
        if (WMinecraft.getPlayer().getHealth() > 0.0f && (WMinecraft.getPlayer().onGround || Wolfram.getWolfram().moduleManager.isEnabled("flight") || Wolfram.getWolfram().moduleManager.isEnabled("creativefly") || WMinecraft.getPlayer().capabilities.isCreativeMode) && WMinecraft.getPlayer().inventory.getCurrentItem() != null && WMinecraft.getPlayer().inventory.getCurrentItem().getItem() instanceof ItemBow && Minecraft.getMinecraft().gameSettings.keyBindUseItem.pressed) {
            WPlayerController.processRightClick();
            int i = 0;
            while (i < 20) {
                WConnection.sendPacket(new CPacketPlayer());
                ++i;
            }
            WConnection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(0, 0, 0), EnumFacing.DOWN));
            WMinecraft.getPlayer().inventory.getCurrentItem().getItem().onPlayerStoppedUsing(WMinecraft.getPlayer().inventory.getCurrentItem(), WMinecraft.getWorld(), WMinecraft.getPlayer(), 10);
        }
    }
}

