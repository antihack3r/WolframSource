/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.movement;

import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.PlayerUpdateEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;

public class Glide
extends ModuleListener {
    public Glide() {
        super("Glide", Module.Category.MOVEMENT, "Fall slower");
    }

    @EventTarget
    public void onUpdate(PlayerUpdateEvent event) {
        if (WMinecraft.getPlayer().motionY < 0.0 && WMinecraft.getPlayer().isAirBorne && !WMinecraft.getPlayer().isOnLadder() && WMinecraft.getWorld().getCollisionBoxes(WMinecraft.getPlayer(), WMinecraft.getPlayer().getEntityBoundingBox().offset(0.0, -0.5, 0.0).expand(-0.001, 0.0, -0.001)).isEmpty()) {
            WMinecraft.getPlayer().motionY = -this.getSettings().getFloat("glide_speed");
        }
    }
}

