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
import net.wolframclient.event.events.MovementEvent;
import net.wolframclient.event.events.PushOutOfBlocksEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;

public class Phase
extends ModuleListener {
    int phase = 0;

    public Phase() {
        super("Phase", Module.Category.MOVEMENT, "Makes you glitch through walls");
    }

    @EventTarget
    public void onMove(MovementEvent event) {
        double x = WMinecraft.getPlayer().posX;
        double y = WMinecraft.getPlayer().posY;
        double z = WMinecraft.getPlayer().posZ;
        float yaw = WMinecraft.getPlayer().rotationYaw;
        if ((WMinecraft.getPlayer().isCollidedHorizontally || this.phase <= 3) && WMinecraft.getPlayer().onGround && !WMinecraft.getPlayer().isInWater() && !WMinecraft.getPlayer().isOnLadder()) {
            if (this.phase > 3) {
                this.phase = 0;
            }
            event.setMotionX(event.getMotionX() * (double)0.1f);
            event.setMotionZ(event.getMotionZ() * (double)0.1f);
            this.sendPacket(new CPacketPlayer.Position(x + Math.sin(Math.toRadians(-yaw)) * 0.4, y, z + Math.cos(Math.toRadians(-yaw)) * 0.4, true));
            this.sendPacket(new CPacketPlayer.Position(x + Math.sin(Math.toRadians(-yaw)) * 0.4, y - 0.1, z + Math.cos(Math.toRadians(-yaw)) * 0.4, true));
            this.sendPacket(new CPacketPlayer.Position(x + Math.sin(Math.toRadians(-yaw)) * 0.4, y - 1.0, z + Math.cos(Math.toRadians(-yaw)) * 0.4, true));
            ++this.phase;
            if (WMinecraft.getPlayer().isCollidedHorizontally) {
                this.phase = 0;
            }
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

