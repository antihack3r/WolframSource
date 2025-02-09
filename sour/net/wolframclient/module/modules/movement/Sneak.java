/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.movement;

import net.minecraft.client.Minecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.RenderEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;

public final class Sneak
extends ModuleListener {
    public Sneak() {
        super("Sneak", Module.Category.MOVEMENT, "Makes you always sneak");
    }

    @Override
    protected void onDisable2() {
        Minecraft.getMinecraft().gameSettings.keyBindSneak.pressed = false;
    }

    @EventTarget
    public void onUpdate(RenderEvent event) {
        Minecraft.getMinecraft().gameSettings.keyBindSneak.pressed = true;
    }
}

