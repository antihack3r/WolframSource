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

public final class Spider
extends ModuleListener {
    public Spider() {
        super("Spider", Module.Category.MOVEMENT, "Climb walls");
    }

    @EventTarget
    public void onUpdate(PlayerUpdateEvent event) {
        EntityPlayerSP player = WMinecraft.getPlayer();
        if (player.isOnLadder() || !player.isCollidedHorizontally) {
            return;
        }
        player.motionY = 0.0;
        player.setPosition(player.posX, player.posY + 0.5, player.posZ);
    }
}

