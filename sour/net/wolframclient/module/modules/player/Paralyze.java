/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.player;

import net.minecraft.network.play.client.CPacketPlayer;
import net.wolframclient.compatibility.WConnection;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.PlayerUpdateEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;

public class Paralyze
extends ModuleListener {
    public Paralyze() {
        super("Paralyze", Module.Category.PLAYER, "Make other players unable to do anything when colliding them");
    }

    @EventTarget
    public void onUpdate(PlayerUpdateEvent event) {
        int i = 0;
        while (i < 20000) {
            WConnection.sendPacketBypass(new CPacketPlayer(false));
            ++i;
        }
    }
}

