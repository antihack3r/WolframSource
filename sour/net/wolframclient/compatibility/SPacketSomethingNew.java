/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.compatibility;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public final class SPacketSomethingNew
implements Packet<INetHandlerPlayClient> {
    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        buf.readByte();
        buf.readVarIntFromBuffer();
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeByte(0);
        buf.writeVarIntToBuffer(0);
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
    }
}

