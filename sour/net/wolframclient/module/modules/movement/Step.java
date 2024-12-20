/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.movement;

import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.PlayerUpdateEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;

public final class Step
extends ModuleListener {
    public Step() {
        super("Step", Module.Category.MOVEMENT, "Step up full blocks");
    }

    @EventTarget
    public void onUpdate(PlayerUpdateEvent event) {
        WMinecraft.getPlayer().stepHeight = this.getSettings().getFloat("step_height");
    }

    @Override
    public void onDisable2() {
        WMinecraft.getPlayer().stepHeight = 0.5f;
    }
}

