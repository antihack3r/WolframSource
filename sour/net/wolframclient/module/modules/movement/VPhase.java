/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.movement;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.wolframclient.compatibility.WConnection;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.InsideOpaqueBlockEvent;
import net.wolframclient.event.events.PlayerUpdateEvent;
import net.wolframclient.event.events.PushOutOfBlocksEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;

public class VPhase
extends ModuleListener {
    public VPhase() {
        super("VPhase", Module.Category.MOVEMENT, "Phase through blocks under you");
    }

    @EventTarget
    public void onUpdate(PlayerUpdateEvent event) {
        double x = WMinecraft.getPlayer().posX;
        double y = WMinecraft.getPlayer().posY;
        double z = WMinecraft.getPlayer().posZ;
        if (WMinecraft.getPlayer().onGround && !WMinecraft.getPlayer().isInWater() && !WMinecraft.getPlayer().isOnLadder()) {
            this.sendPacket(new CPacketPlayer.Position(x, y - 3.0, z, true));
            this.sendPacket(new CPacketPlayer.Position(x, y - 3.0, z, true));
            this.sendPacket(new CPacketPlayer.Position(x, y - 3.0, z, true));
            WMinecraft.getPlayer().setPosition(x, y - 1.0, z);
        }
    }

    private void sendPacket(Packet p) {
        WConnection.sendPacket(p);
    }

    @EventTarget
    public void eventPushOut(PushOutOfBlocksEvent event) {
        event.setCancelled(true);
    }

    @EventTarget
    public void eventInsideBlock(InsideOpaqueBlockEvent event) {
        event.setCancelled(true);
    }
}

