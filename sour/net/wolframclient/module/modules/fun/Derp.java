/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.fun;

import java.util.Random;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.PostMotionUpdateEvent;
import net.wolframclient.event.events.PreMotionUpdateEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;

public final class Derp
extends ModuleListener {
    private final Random random = new Random();
    private float yaw;
    private float pitch;

    public Derp() {
        super("Derp", Module.Category.FUN, "Makes you look crazy");
    }

    @EventTarget
    public void onPreUpdate(PreMotionUpdateEvent event) {
        this.yaw = WMinecraft.getPlayer().rotationYaw;
        this.pitch = WMinecraft.getPlayer().rotationPitch;
        WMinecraft.getPlayer().rotationYaw = this.random.nextInt(360) - 180;
        WMinecraft.getPlayer().rotationPitch = this.random.nextInt(360) - 180;
    }

    @EventTarget
    public void onPostUpdate(PostMotionUpdateEvent event) {
        WMinecraft.getPlayer().rotationYaw = this.yaw;
        WMinecraft.getPlayer().rotationPitch = this.pitch;
    }
}

