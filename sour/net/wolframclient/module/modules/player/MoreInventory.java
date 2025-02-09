/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.player;

import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.server.SPacketCloseWindow;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.NetworkManagerPacketSendEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;

public class MoreInventory
extends ModuleListener {
    public MoreInventory() {
        super("MoreInventory", Module.Category.PLAYER, "Don't drop items from crafting slots");
    }

    @EventTarget
    public void onPacketSend(NetworkManagerPacketSendEvent event) {
        if (event.getPacket() instanceof SPacketCloseWindow || event.getPacket() instanceof CPacketCloseWindow) {
            event.setCancelled(true);
        }
    }
}

