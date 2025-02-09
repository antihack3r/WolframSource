/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.movement;

import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.PlayerUpdateEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;

public final class AirWalk
extends ModuleListener {
    public AirWalk() {
        super("AirWalk", Module.Category.MOVEMENT, "Jump in mid-air");
    }

    @EventTarget
    public void onUpdate(PlayerUpdateEvent event) {
        WMinecraft.getPlayer().onGround = true;
    }
}

