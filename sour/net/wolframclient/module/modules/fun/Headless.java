/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.fun;

import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.PostMotionUpdateEvent;
import net.wolframclient.event.events.PreMotionUpdateEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;

public final class Headless
extends ModuleListener {
    private float pitch;

    public Headless() {
        super("Headles", Module.Category.FUN, "Look like you lost your head");
    }

    @EventTarget
    public void onPreUpdate(PreMotionUpdateEvent event) {
        this.pitch = WMinecraft.getPlayer().rotationPitch;
        WMinecraft.getPlayer().rotationPitch = -180.0f;
    }

    @EventTarget
    public void postUpdate(PostMotionUpdateEvent event) {
        WMinecraft.getPlayer().rotationPitch = this.pitch;
    }
}

