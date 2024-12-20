/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.player;

import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.RenderEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;

public final class NoBreakDelay
extends ModuleListener {
    public NoBreakDelay() {
        super("NoBreakDelay", Module.Category.PLAYER, "Disables the delay between block breaking");
    }

    @EventTarget
    public void onUpdate(RenderEvent event) {
        PlayerControllerMP.blockHitDelay = 0;
    }
}

