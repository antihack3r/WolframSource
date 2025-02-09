/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.movement;

import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.Listener;
import net.wolframclient.event.events.MovementEvent;
import net.wolframclient.event.events.PlayerUpdateEvent;
import net.wolframclient.module.Module;

public class FlyVanilla
extends Module
implements Listener {
    public FlyVanilla() {
        super("FlyVanilla", Module.Category.MOVEMENT, "Fly up to a minute on vanilla servers");
    }

    @Override
    public void onEnable() {
        registry.registerListener(this);
    }

    @Override
    public void onDisable() {
        registry.unregisterListener(this);
    }

    @Override
    public void onToggle() {
        if (WMinecraft.getPlayer() == null) {
            this.setEnabledDirectly(false);
            return;
        }
        WMinecraft.getPlayer().capabilities.isFlying = !this.isEnabled();
    }

    @EventTarget
    public void updatePlayer(PlayerUpdateEvent event) {
        WMinecraft.getPlayer().capabilities.isFlying = true;
        if (WMinecraft.getPlayer().ticksExisted % 10 != 0) {
            event.setCancelled(true);
        }
    }

    @EventTarget
    public void onMove(MovementEvent event) {
        if (!WMinecraft.getPlayer().onGround) {
            float speed = this.getSettings().getFloat("flight_speed");
            event.setMotionX(event.getMotionX() * (double)speed);
            event.setMotionY(event.getMotionY() * (double)speed);
            event.setMotionZ(event.getMotionZ() * (double)speed);
        }
    }
}

