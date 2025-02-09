/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.event.events;

import net.minecraft.network.Packet;
import net.wolframclient.event.EventCancellable;

public final class NetworkManagerPacketSendEvent
extends EventCancellable {
    private Packet packet;

    public NetworkManagerPacketSendEvent(Packet packet) {
        this.packet = packet;
    }

    public Packet getPacket() {
        return this.packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }
}

