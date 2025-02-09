/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.movement;

import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.PlayerUpdateEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;

public final class AirMove
extends ModuleListener {
    private boolean onGround;

    public AirMove() {
        super("AirMove", Module.Category.MOVEMENT, "Move better in mid-air");
    }

    @EventTarget
    public void onUpdate(PlayerUpdateEvent event) {
        this.onGround = WMinecraft.getPlayer().onGround;
        WMinecraft.getPlayer().onGround = true;
    }

    public boolean isOnGround() {
        return this.onGround;
    }
}

