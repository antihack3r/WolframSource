/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.movement;

import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.MovementEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;

public final class Dolphin
extends ModuleListener {
    public Dolphin() {
        super("Dolphin", Module.Category.MOVEMENT, "Move faster in water");
    }

    @EventTarget
    public void onMovement(MovementEvent event) {
        if (WMinecraft.getPlayer().isInWater()) {
            event.setMotionX(event.getMotionX() * 2.0);
            event.setMotionY(event.getMotionY() * 2.0);
            event.setMotionZ(event.getMotionZ() * 2.0);
        }
    }
}

