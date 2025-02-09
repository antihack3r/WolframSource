/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.movement;

import java.util.Iterator;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.wolframclient.Wolfram;
import net.wolframclient.compatibility.WConnection;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.AddPacketToQueueEvent;
import net.wolframclient.event.events.PlayerUpdateEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;

public class Jesus
extends ModuleListener {
    private int ticksOutOfWater = 10;
    private int time = 0;
    private final int delay = 4;

    public Jesus() {
        super("Jesus", Module.Category.MOVEMENT, "Walk on water");
    }

    @EventTarget
    public void onPacketSend(AddPacketToQueueEvent event) {
        if ((event.getPacket() instanceof CPacketPlayer.Position || event.getPacket() instanceof CPacketPlayer.PositionRotation) && Jesus.isOverWater() && !WMinecraft.getPlayer().isInWater() && !(WMinecraft.getPlayer().fallDistance > 3.0f)) {
            if (WMinecraft.getPlayer().movementInput == null) {
                event.setCancelled(true);
                return;
            }
            ++this.time;
            if (this.time >= 4) {
                event.setCancelled(true);
                double x = WMinecraft.getPlayer().posX;
                double y = WMinecraft.getPlayer().posY;
                double z = WMinecraft.getPlayer().posZ;
                float pitch = WMinecraft.getPlayer().rotationPitch;
                float yaw = WMinecraft.getPlayer().rotationYaw;
                if (WMinecraft.getPlayer().ticksExisted % 2 == 0) {
                    CPacketPlayer.PositionRotation newPacket = new CPacketPlayer.PositionRotation(x, y - 0.05, z, yaw, pitch, true);
                    WConnection.sendPacketBypass(newPacket);
                } else {
                    CPacketPlayer.PositionRotation newPacket = new CPacketPlayer.PositionRotation(x, y + 0.05, z, yaw, pitch, true);
                    WConnection.sendPacketBypass(newPacket);
                }
            }
        }
    }

    @EventTarget
    public void onUpdate(PlayerUpdateEvent event) {
        if (!Minecraft.getMinecraft().gameSettings.keyBindSneak.pressed) {
            if (WMinecraft.getPlayer().isInWater()) {
                WMinecraft.getPlayer().motionY = 0.11;
                this.ticksOutOfWater = 0;
            } else {
                if (this.ticksOutOfWater == 0) {
                    WMinecraft.getPlayer().motionY = 0.3;
                } else if (this.ticksOutOfWater == 1) {
                    WMinecraft.getPlayer().motionY = 0.0;
                }
                ++this.ticksOutOfWater;
            }
        }
    }

    public static boolean isOverWater() {
        EntityPlayerSP thePlayer = WMinecraft.getPlayer();
        boolean isOnWater = false;
        boolean isOnSolid = false;
        Iterator<AxisAlignedBB> iterator = WMinecraft.getWorld().getCollisionBoxes(thePlayer, thePlayer.getEntityBoundingBox().offset(0.0, -1.0, 0.0).expand(-0.001, 0.0, -0.001)).iterator();
        while (iterator.hasNext()) {
            AxisAlignedBB o;
            AxisAlignedBB bbox = o = iterator.next();
            BlockPos blockPos = new BlockPos(bbox.maxX - (bbox.maxX - bbox.minX) / 2.0, bbox.maxY - (bbox.maxY - bbox.minY) / 2.0, bbox.maxZ - (bbox.maxZ - bbox.minZ) / 2.0);
            Material material = WMinecraft.getWorld().getBlockState(blockPos).getMaterial();
            if (material == Material.WATER || material == Material.LAVA) {
                isOnWater = true;
                continue;
            }
            if (material == Material.AIR) continue;
            isOnSolid = true;
        }
        return isOnWater && !isOnSolid;
    }

    public static boolean shouldBeSolid() {
        return Wolfram.getWolfram().moduleManager.isEnabled("jesus") && WMinecraft.getPlayer() != null && !(WMinecraft.getPlayer().fallDistance > 3.0f) && !Minecraft.getMinecraft().gameSettings.keyBindSneak.pressed && !WMinecraft.getPlayer().isInWater();
    }
}

