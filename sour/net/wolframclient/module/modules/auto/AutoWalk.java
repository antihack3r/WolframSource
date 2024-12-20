/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.auto;

import net.minecraft.client.Minecraft;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.PlayerUpdateEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;

public final class AutoWalk
extends ModuleListener {
    public AutoWalk() {
        super("AutoWalk", Module.Category.AUTO, "Walk automatically");
    }

    @EventTarget
    public void onUpdate(PlayerUpdateEvent event) {
        Minecraft.getMinecraft().gameSettings.keyBindForward.pressed = true;
        if (WMinecraft.getPlayer().isCollidedHorizontally && WMinecraft.getPlayer().onGround) {
            WMinecraft.getPlayer().jump();
        }
    }

    @Override
    public void onDisable2() {
        Minecraft.getMinecraft().gameSettings.keyBindForward.pressed = false;
    }
}

