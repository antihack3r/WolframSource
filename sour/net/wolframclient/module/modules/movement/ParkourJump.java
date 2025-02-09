/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.movement;

import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.Listener;
import net.wolframclient.event.events.PlayerUpdateEvent;
import net.wolframclient.module.Module;

public class ParkourJump
extends Module
implements Listener {
    public ParkourJump() {
        super("ParkourJump", Module.Category.MOVEMENT, "Jump when reaching a block's edge");
    }

    @Override
    public void onEnable() {
        registry.registerListener(this);
    }

    @Override
    public void onDisable() {
        registry.unregisterListener(this);
    }

    @EventTarget
    public void onUpdate(PlayerUpdateEvent event) {
        if (WMinecraft.getWorld().getCollisionBoxes(WMinecraft.getPlayer(), WMinecraft.getPlayer().getEntityBoundingBox().offset(0.0, -0.5, 0.0).expand(0.001, 0.0, 0.001)).isEmpty() && WMinecraft.getPlayer().onGround && !WMinecraft.getPlayer().isSneaking()) {
            WMinecraft.getPlayer().jump();
        }
    }
}

