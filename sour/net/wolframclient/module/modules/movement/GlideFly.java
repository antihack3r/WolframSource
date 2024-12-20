/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.input.Keyboard
 */
package net.wolframclient.module.modules.movement;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.Listener;
import net.wolframclient.event.events.PlayerUpdateEvent;
import net.wolframclient.event.events.WorldRenderEvent;
import net.wolframclient.module.Module;
import net.wolframclient.utils.RenderUtils;
import org.lwjgl.input.Keyboard;

public class GlideFly
extends Module
implements Listener {
    boolean wasOnGround = true;
    double roofY = -1.0;

    public GlideFly() {
        super("GlideFly", Module.Category.MOVEMENT, "Glide up and down");
        registry.registerListener(this);
    }

    @EventTarget
    public void onUpdate(PlayerUpdateEvent event) {
        if (!WMinecraft.getPlayer().onGround && this.wasOnGround) {
            this.roofY = WMinecraft.getPlayer().posY + 0.6;
        }
        if (WMinecraft.getPlayer().onGround) {
            this.roofY = -1.0;
        }
        this.wasOnGround = WMinecraft.getPlayer().onGround;
        if (!this.isEnabled()) {
            return;
        }
        if (!WMinecraft.getPlayer().isOnLadder() && !WMinecraft.getPlayer().onGround) {
            if (Keyboard.isKeyDown((int)57)) {
                if (this.roofY != -1.0) {
                    WMinecraft.getPlayer().motionY = WMinecraft.getPlayer().posY + 0.2 > this.roofY ? 0.0 : 0.2;
                }
            } else {
                WMinecraft.getPlayer().motionY = Keyboard.isKeyDown((int)42) ? -0.2 : -0.05;
            }
        }
        if (this.roofY != -1.0 && WMinecraft.getPlayer().posY > this.roofY) {
            WMinecraft.getPlayer().setPosition(WMinecraft.getPlayer().posX, this.roofY, WMinecraft.getPlayer().posZ);
        }
    }

    @EventTarget
    public void onRender(WorldRenderEvent event) {
        if (!this.isEnabled()) {
            return;
        }
        if (this.roofY == -1.0) {
            return;
        }
        double y = this.roofY - RenderManager.renderPosY + (double)WMinecraft.getPlayer().height;
        int color = 0xFF0000;
        RenderUtils.drawBox(new AxisAlignedBB(-2.0, 0.0 + y, -2.0, 2.0, 0.0 + y, 2.0), 0x20FF0000, false);
        RenderUtils.drawOutlinedBox(new AxisAlignedBB(-2.0, 0.0 + y, -2.0, 2.0, 0.0 + y, 2.0), 0xFF0000, false);
    }
}

