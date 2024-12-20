/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.movement;

import net.minecraft.client.entity.EntityPlayerSP;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.PlayerUpdateEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;

public final class BunnyHop
extends ModuleListener {
    public BunnyHop() {
        super("BunnyHop", Module.Category.MOVEMENT, "Automatically jump when sprinting");
    }

    @EventTarget
    public void onUpdate(PlayerUpdateEvent event) {
        if (!this.isEnabled()) {
            return;
        }
        EntityPlayerSP player = WMinecraft.getPlayer();
        if (player.onGround && player.isSprinting()) {
            player.jump();
        }
    }
}

