/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.PlayerUpdateEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;

public final class SprintLegit
extends ModuleListener {
    public SprintLegit() {
        super("SprintLegit", Module.Category.MOVEMENT, "Always sprint");
    }

    @EventTarget
    public void onUpdate(PlayerUpdateEvent event) {
        EntityPlayerSP player = WMinecraft.getPlayer();
        if (!player.isCollidedHorizontally && !player.isSneaking() && Minecraft.getMinecraft().gameSettings.keyBindForward.pressed) {
            player.setSprinting(true);
        } else {
            player.setSprinting(false);
        }
    }
}

