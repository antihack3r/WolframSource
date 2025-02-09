/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.render;

import net.minecraft.client.Minecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.RenderEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;

public final class Fullbright
extends ModuleListener {
    public Fullbright() {
        super("Fullbright", Module.Category.RENDER, "Lights up the world");
    }

    @EventTarget
    public void onUpdate(RenderEvent event) {
        if (Minecraft.getMinecraft().gameSettings.gammaSetting < 100.0f) {
            Minecraft.getMinecraft().gameSettings.gammaSetting += 1.0f;
        }
    }

    @Override
    protected void onEnable2() {
        Minecraft.getMinecraft().renderGlobal.loadRenderers();
    }

    @Override
    protected void onDisable2() {
        Minecraft.getMinecraft().gameSettings.gammaSetting = 1.0f;
        Minecraft.getMinecraft().renderGlobal.loadRenderers();
    }
}

