/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.input.Keyboard
 */
package net.wolframclient.module.modules.movement;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.wolframclient.command.commands.DamageCommand;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.Listener;
import net.wolframclient.event.events.PlayerUpdateEvent;
import net.wolframclient.event.events.WorldRenderEvent;
import net.wolframclient.module.Module;
import net.wolframclient.utils.RenderUtils;
import org.lwjgl.input.Keyboard;

public class FlyNCP
extends Module
implements Listener {
    boolean wasOnGround = true;
    double roofY = -1.0;

    public FlyNCP() {
        super("FlyNCP", Module.Category.MOVEMENT, "NCP bypassed flight");
        registry.registerListener(this);
    }

    @Override
    public void onEnable() {
        if (WMinecraft.getPlayer() != null) {
            DamageCommand.damage(5);
        }
    }

    @EventTarget
    public void onUpdate(PlayerUpdateEvent event) {
        if (!WMinecraft.getPlayer().onGround && this.wasOnGround) {
            this.roofY = WMinecraft.getPlayer().posY + 0.0;
        }
        if (WMinecraft.getPlayer().onGround) {
            this.roofY = -1.0;
        }
        if (WMinecraft.getPlayer().onGround && !this.wasOnGround && this.isEnabled()) {
            this.setEnabled(false, true);
        }
        this.wasOnGround = WMinecraft.getPlayer().onGround;
        if (!this.isEnabled()) {
            return;
        }
        if (!WMinecraft.getPlayer().isOnLadder() && !WMinecraft.getPlayer().onGround) {
            if (Keyboard.isKeyDown((int)57)) {
                if (this.roofY != -1.0) {
                    WMinecraft.getPlayer().motionY = WMinecraft.getPlayer().posY + 0.4 > this.roofY ? 0.0 : 0.4;
                }
            } else {
                WMinecraft.getPlayer().motionY = Keyboard.isKeyDown((int)42) ? -0.4 : 0.0;
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

