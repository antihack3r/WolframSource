/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.movement;

import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.NetworkManagerPacketSendEvent;
import net.wolframclient.event.events.PreMotionUpdateEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;

public class Speed
extends ModuleListener {
    private int speedTicks;
    private float prevYaw;
    private int newGroundTicks;
    private final Minecraft mc = Minecraft.getMinecraft();

    public Speed() {
        super("Speed", Module.Category.MOVEMENT, "Makes you go really fast!");
    }

    @Override
    public void onDisable2() {
        this.mc.timer.timerSpeed = 1.0f;
    }

    @EventTarget
    public void onEvent(PreMotionUpdateEvent event) {
        this.newGroundTicks = WMinecraft.getPlayer().onGround ? ++this.newGroundTicks : 0;
        if (!this.onGround()) {
            this.speedTicks = -4;
        }
        this.mc.timer.timerSpeed = 1.0f;
        if (this.onGround()) {
            double speed = 2.525;
            if (this.speedTicks == 1) {
                speed = 1.72;
                if (WMinecraft.getPlayer().moveStrafing != 0.0f || WMinecraft.getPlayer().moveForward < 0.0f) {
                    speed = 0.0;
                }
                if (this.isTurning()) {
                    speed = 1.0;
                }
                if (!this.isColliding(WMinecraft.getPlayer().getEntityBoundingBox().offset(WMinecraft.getPlayer().motionX * speed, 0.0, WMinecraft.getPlayer().motionZ * speed).expand(0.2, 0.0, 0.2))) {
                    WMinecraft.getPlayer().setEntityBoundingBox(WMinecraft.getPlayer().getEntityBoundingBox().offset(WMinecraft.getPlayer().motionX * speed, 0.0, WMinecraft.getPlayer().motionZ * speed));
                    this.mc.timer.timerSpeed = 1.6f;
                }
            } else if (this.speedTicks == 2) {
                if (WMinecraft.getPlayer().moveStrafing != 0.0f || WMinecraft.getPlayer().moveForward < 0.0f) {
                    speed = 1.4;
                    if (this.isTurning()) {
                        speed = 0.0;
                    }
                }
                if (!this.isColliding(WMinecraft.getPlayer().getEntityBoundingBox().offset(WMinecraft.getPlayer().motionX * speed, 0.0, WMinecraft.getPlayer().motionZ * speed).expand(0.2, 0.0, 0.2))) {
                    WMinecraft.getPlayer().setEntityBoundingBox(WMinecraft.getPlayer().getEntityBoundingBox().offset(WMinecraft.getPlayer().motionX * speed, 0.0, WMinecraft.getPlayer().motionZ * speed));
                }
            } else if (this.speedTicks == 3) {
                speed = 0.62;
                if (WMinecraft.getPlayer().moveStrafing == 0.0f && WMinecraft.getPlayer().moveForward >= 0.0f && !this.isTurning() && !this.isColliding(WMinecraft.getPlayer().getEntityBoundingBox().offset(WMinecraft.getPlayer().motionX * speed, 0.0, WMinecraft.getPlayer().motionZ * speed).expand(0.2, 0.0, 0.2))) {
                    WMinecraft.getPlayer().setEntityBoundingBox(WMinecraft.getPlayer().getEntityBoundingBox().offset(WMinecraft.getPlayer().motionX * speed, 0.0, WMinecraft.getPlayer().motionZ * speed));
                }
            } else if (this.speedTicks == 4) {
                speed = 0.52;
                if (WMinecraft.getPlayer().moveStrafing == 0.0f && WMinecraft.getPlayer().moveForward >= 0.0f && !this.isTurning() && !this.isColliding(WMinecraft.getPlayer().getEntityBoundingBox().offset(WMinecraft.getPlayer().motionX * speed, 0.0, WMinecraft.getPlayer().motionZ * speed).expand(0.2, 0.0, 0.2))) {
                    WMinecraft.getPlayer().setEntityBoundingBox(WMinecraft.getPlayer().getEntityBoundingBox().offset(WMinecraft.getPlayer().motionX * speed, 0.0, WMinecraft.getPlayer().motionZ * speed));
                }
            } else if (this.speedTicks >= 5) {
                speed = 0.42;
                if (WMinecraft.getPlayer().moveStrafing == 0.0f && WMinecraft.getPlayer().moveForward >= 0.0f && !this.isTurning() && this.speedTicks == 5 && !this.isColliding(WMinecraft.getPlayer().getEntityBoundingBox().offset(WMinecraft.getPlayer().motionX * speed, 0.0, WMinecraft.getPlayer().motionZ * speed).expand(0.2, 0.0, 0.2))) {
                    WMinecraft.getPlayer().setEntityBoundingBox(WMinecraft.getPlayer().getEntityBoundingBox().offset(WMinecraft.getPlayer().motionX * speed, 0.0, WMinecraft.getPlayer().motionZ * speed));
                }
                this.speedTicks = 0;
            }
        }
        this.isTurning();
        this.prevYaw = WMinecraft.getPlayer().rotationYaw;
        ++this.speedTicks;
    }

    @EventTarget
    public void onPacketSend(NetworkManagerPacketSendEvent event) {
        if (event.getPacket() instanceof CPacketPlayer.Position || event.getPacket() instanceof CPacketPlayer.PositionRotation) {
            double speed = 2.525;
            if (!(this.isTurning() || WMinecraft.getPlayer().moveStrafing != 0.0f || WMinecraft.getPlayer().moveForward < 0.0f || this.speedTicks != 4 || !this.movementChecks() || this.isColliding(WMinecraft.getPlayer().getEntityBoundingBox().offset(WMinecraft.getPlayer().motionX * 2.525, 0.0, WMinecraft.getPlayer().motionZ * 2.525).expand(0.2, 0.0, 0.2)) || this.isColliding(WMinecraft.getPlayer().getEntityBoundingBox().offset(0.0, 0.5, 0.0)))) {
                ((CPacketPlayer)event.getPacket()).y += 0.4;
            }
        }
    }

    private boolean onGround() {
        return this.newGroundTicks > 3;
    }

    private boolean isColliding(AxisAlignedBB bb) {
        boolean colliding = false;
        Iterator<AxisAlignedBB> iterator = WMinecraft.getWorld().getCollisionBoxes(WMinecraft.getPlayer(), bb).iterator();
        while (iterator.hasNext()) {
            iterator.next();
            colliding = true;
            this.newGroundTicks = 0;
        }
        return colliding;
    }

    private boolean isTurning() {
        return Speed.getDistanceBetweenAngles(WMinecraft.getPlayer().rotationYaw, this.prevYaw) > 5.0f;
    }

    public static float getDistanceBetweenAngles(float angle1, float angle2) {
        float angle = Math.abs(angle1 - angle2) % 360.0f;
        if (angle > 180.0f) {
            angle = 360.0f - angle;
        }
        return angle;
    }

    private boolean movementChecks() {
        return !WMinecraft.getPlayer().isSneaking() && !WMinecraft.getPlayer().isCollidedHorizontally && (WMinecraft.getPlayer().isCollidedHorizontally || WMinecraft.getPlayer().moveForward != 0.0f || WMinecraft.getPlayer().moveStrafing != 0.0f) && !this.mc.gameSettings.keyBindJump.isPressed() && WMinecraft.getPlayer().onGround;
    }
}

