/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class CPacketKeepAlive
implements Packet<INetHandlerPlayServer> {
    private int key;

    public CPacketKeepAlive() {
    }

    public CPacketKeepAlive(int idIn) {
        this.key = idIn;
    }

    @Override
    public void processPacket(INetHandlerPlayServer handler) {
        handler.processKeepAlive(this);
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.key = buf.readVarIntFromBuffer();
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeVarIntToBuffer(this.key);
    }

    public int getKey() {
        return this.key;
    }
}

