/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.movement;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.wolframclient.Wolfram;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.PlayerUpdateEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;

public final class FastFall
extends ModuleListener {
    public FastFall() {
        super("FastFall", Module.Category.MOVEMENT, "Fall faster");
    }

    @EventTarget
    public void onUpdate(PlayerUpdateEvent event) {
        if (!WMinecraft.getPlayer().isAirBorne || WMinecraft.getPlayer().motionY >= 0.0) {
            return;
        }
        if (WMinecraft.getPlayer().isOnLadder() || Minecraft.getMinecraft().gameSettings.keyBindJump.isPressed()) {
            return;
        }
        WMinecraft.getPlayer().motionY = WMinecraft.getPlayer().isInsideOfMaterial(Material.WEB) && !Wolfram.getWolfram().moduleManager.isEnabled("noslowdown") ? -7.0 : -2.0;
    }
}

