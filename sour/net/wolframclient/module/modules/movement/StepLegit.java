/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.movement;

import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.PlayerUpdateEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;

public class StepLegit
extends ModuleListener {
    boolean wasOnGround;

    public StepLegit() {
        super("StepLegit", Module.Category.MOVEMENT, "Makes you step 1 blocks high on NCP servers");
    }

    @EventTarget
    public void onUpdate(PlayerUpdateEvent event) {
        if (WMinecraft.getPlayer() == null) {
            return;
        }
        boolean onGround = WMinecraft.getPlayer().onGround;
        if (WMinecraft.getPlayer().isCollidedHorizontally) {
            WMinecraft.getPlayer().onGround = true;
            if (this.wasOnGround) {
                WMinecraft.getPlayer().jump();
                onGround = false;
            }
        }
        this.wasOnGround = onGround;
    }
}

