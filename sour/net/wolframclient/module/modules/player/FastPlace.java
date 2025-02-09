/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.player;

import net.minecraft.client.Minecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.RenderEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;

public final class FastPlace
extends ModuleListener {
    public FastPlace() {
        super("FastPlace", Module.Category.PLAYER, "Disables the placing delay");
    }

    @EventTarget
    public void onUpdate(RenderEvent event) {
        Minecraft.getMinecraft().rightClickDelayTimer = 0;
    }
}

