/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.movement;

import net.minecraft.network.play.client.CPacketPlayer;
import net.wolframclient.compatibility.WConnection;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.PlayerUpdateEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;

public final class NoFall
extends ModuleListener {
    public NoFall() {
        super("NoFall", Module.Category.MOVEMENT, "No fall damage");
    }

    @EventTarget
    public void onUpdate(PlayerUpdateEvent event) {
        if (WMinecraft.getPlayer().fallDistance > 2.0f) {
            WConnection.sendPacket(new CPacketPlayer(true));
        }
    }
}

