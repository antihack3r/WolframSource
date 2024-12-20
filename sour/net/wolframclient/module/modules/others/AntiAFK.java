/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.others;

import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.RenderEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;
import net.wolframclient.utils.MillisTimer;

public final class AntiAFK
extends ModuleListener {
    private final MillisTimer timer = new MillisTimer();

    public AntiAFK() {
        super("AntiAFK", Module.Category.OTHER, "Prevents from being kicked for AFK");
    }

    @Override
    protected void onEnable2() {
        this.timer.reset();
    }

    @EventTarget
    public void onUpdate(RenderEvent event) {
        if (!this.timer.check(120000.0f)) {
            return;
        }
        WMinecraft.getPlayer().jump();
        this.timer.reset();
    }
}

