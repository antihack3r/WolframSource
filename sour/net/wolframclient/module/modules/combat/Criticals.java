/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.combat;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.JumpEvent;
import net.wolframclient.event.events.PlayerUpdateEvent;
import net.wolframclient.event.events.PostMotionUpdateEvent;
import net.wolframclient.event.events.PreMotionUpdateEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;

public final class Criticals
extends ModuleListener {
    private boolean wasOnGround = true;
    private double fallDistance = 0.0;
    private double newFallDistance = 0.0;
    private boolean shouldJump = false;

    public Criticals() {
        super("Criticals", Module.Category.COMBAT, "Always make critical hits");
    }

    @Override
    public void onEnable2() {
        if (WMinecraft.getPlayer() != null) {
            this.newFallDistance = WMinecraft.getPlayer().fallDistance;
        } else {
            this.fallDistance = 0.0;
            this.newFallDistance = 0.0;
        }
    }

    @Override
    public void onDisable2() {
        this.fallDistance = 0.0;
        this.newFallDistance = 0.0;
    }

    private boolean isSafe(EntityPlayerSP entity) {
        return entity.inWater || entity.isOnLadder() || entity.isInLava();
    }

    @EventTarget
    public void onUpdate(PlayerUpdateEvent event) {
        if (WMinecraft.getPlayer().onGround && !PlayerControllerMP.isHittingBlock) {
            if (this.fallDistance == 0.0) {
                this.shouldJump = true;
            }
            if (this.shouldJump) {
                WMinecraft.getPlayer().jumpBypass();
                this.shouldJump = false;
            }
        } else {
            this.shouldJump = false;
        }
    }

    @EventTarget
    public void onPreMotionUpdate(PreMotionUpdateEvent event) {
        this.wasOnGround = WMinecraft.getPlayer().onGround;
        if (!this.isSafe(WMinecraft.getPlayer())) {
            if ((double)WMinecraft.getPlayer().fallDistance > this.newFallDistance) {
                this.newFallDistance = WMinecraft.getPlayer().fallDistance;
            }
            if (WMinecraft.getPlayer().fallDistance == 0.0f) {
                this.fallDistance += this.newFallDistance;
                this.newFallDistance = 0.0;
            }
        } else {
            this.fallDistance = 0.0;
            this.newFallDistance = 0.0;
        }
        if (this.wasOnGround && (this.fallDistance > 2.0 || this.shouldJump || PlayerControllerMP.isHittingBlock)) {
            this.fallDistance = 0.0;
            this.newFallDistance = 0.0;
        } else if (this.fallDistance > 0.0) {
            WMinecraft.getPlayer().onGround = false;
        }
    }

    @EventTarget
    public void onJump(JumpEvent event) {
        event.setCancelled(true);
        this.shouldJump = true;
    }

    @EventTarget
    public void onPostMotionUpdate(PostMotionUpdateEvent event) {
        WMinecraft.getPlayer().onGround = this.wasOnGround;
    }
}

