/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.event.events;

import net.minecraft.network.Packet;
import net.wolframclient.event.EventCancellable;

public final class NetworkManagerPacketReceiveEvent
extends EventCancellable {
    private final Packet packet;

    public NetworkManagerPacketReceiveEvent(Packet packet) {
        this.packet = packet;
    }

    public Packet getPacket() {
        return this.packet;
    }
}

