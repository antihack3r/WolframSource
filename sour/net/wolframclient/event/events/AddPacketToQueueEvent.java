/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.event.events;

import net.minecraft.network.Packet;
import net.wolframclient.event.EventCancellable;

public final class AddPacketToQueueEvent
extends EventCancellable {
    private final Packet packet;

    public AddPacketToQueueEvent(Packet packet) {
        this.packet = packet;
    }

    public Packet getPacket() {
        return this.packet;
    }
}

