/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.world;

import net.minecraft.client.Minecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.PlayerUpdateEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;

public final class Timer
extends ModuleListener {
    public Timer() {
        super("Timer", Module.Category.WORLD, "Speed up the world.");
    }

    @EventTarget
    public void onUpdate(PlayerUpdateEvent event) {
        Minecraft.getMinecraft().timer.timerSpeed = this.getSettings().getFloat("timer_speed");
    }

    @Override
    protected void onDisable2() {
        Minecraft.getMinecraft().timer.timerSpeed = 1.0f;
    }
}

