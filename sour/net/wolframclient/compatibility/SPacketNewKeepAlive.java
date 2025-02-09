/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.compatibility;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketNewKeepAlive
implements Packet<INetHandlerPlayClient> {
    private long id;

    public SPacketNewKeepAlive() {
    }

    public SPacketNewKeepAlive(long idIn) {
        this.id = idIn;
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleNewKeepAlive(this);
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.id = buf.readLong();
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeLong(this.id);
    }

    public long getId() {
        return this.id;
    }
}

