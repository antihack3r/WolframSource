/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.compatibility;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class CPacketNewKeepAlive
implements Packet<INetHandlerPlayServer> {
    private long key;

    public CPacketNewKeepAlive() {
    }

    public CPacketNewKeepAlive(long idIn) {
        this.key = idIn;
    }

    @Override
    public void processPacket(INetHandlerPlayServer handler) {
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.key = buf.readLong();
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeLong(this.key);
    }

    public long getKey() {
        return this.key;
    }
}

