/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.movement;

import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.MovementEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;

public final class CreativeFly
extends ModuleListener {
    public CreativeFly() {
        super("CreativeFly", Module.Category.MOVEMENT, "Fly like in Creative Mode");
    }

    @Override
    protected void onDisable2() {
        WMinecraft.getPlayer().capabilities.isFlying = false;
    }

    @EventTarget
    public void onMovement(MovementEvent event) {
        if (!WMinecraft.getPlayer().capabilities.isFlying) {
            return;
        }
        float speed = this.getSettings().getFloat("flight_speed");
        event.setMotionX(event.getMotionX() * (double)speed);
        event.setMotionY(event.getMotionY() * (double)speed);
        event.setMotionZ(event.getMotionZ() * (double)speed);
    }
}

