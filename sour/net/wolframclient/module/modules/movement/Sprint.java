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

public final class Sprint
extends ModuleListener {
    public Sprint() {
        super("Sprint", Module.Category.MOVEMENT, "Always sprint and sprint backwards");
    }

    @EventTarget
    public void onUpdate(PlayerUpdateEvent event) {
        EntityPlayerSP player = WMinecraft.getPlayer();
        if (!(player.isCollidedHorizontally || player.isSneaking() || player.motionX == 0.0 && player.motionZ == 0.0)) {
            player.setSprinting(true);
        } else {
            player.setSprinting(false);
        }
    }
}

